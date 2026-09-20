/*
 * =====================================================================
 *  Snowflake ID Generator                        HLD building block  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Hand every node in a cluster a way to mint unique 64-bit IDs with no coordination:
 *   no database sequence, no lock, no network round trip. IDs must be unique across the
 *   whole fleet and roughly sorted by creation time so they work as a primary/sort key.
 *
 * LAYOUT (Twitter Snowflake, 64 bits)
 *   1 bit   sign, always 0        -> the id stays a positive Java long
 *   41 bits millis since epoch    -> 2^41 ms = ~69 years from the custom epoch
 *   5 bits  datacenter id         -> 32 datacenters
 *   5 bits  worker id             -> 32 machines per datacenter
 *   12 bits sequence              -> 4096 ids per machine per millisecond
 *   Ceiling: 1024 machines * 4096 ids/ms = ~4.1 million ids per millisecond fleet-wide.
 *
 * DESIGN (classes and why)
 *   SnowflakeIdGenerator - one instance per process, holding its own (datacenterId,
 *     workerId). Those two fields are the whole reason no coordination is needed at
 *     mint time: uniqueness is guaranteed by construction, not by consensus.
 *   nextId()   - synchronized; same millisecond -> bump sequence, new millisecond ->
 *     reset sequence to 0. If 4096 ids are burned inside one millisecond it spins
 *     until the clock ticks rather than handing out a duplicate.
 *   decode()   - shifts the id back apart. Being able to read a Snowflake by eye
 *     ("which box minted this, at what time") is half of why the format is used.
 *
 * KEY DECISIONS
 *   Custom epoch (2021-01-01), not the Unix epoch: 41 bits starting at 1970 would have
 *     expired in 2039; starting the clock recently buys the full 69 years.
 *   Timestamp occupies the HIGH bits so numeric order == time order: B-tree inserts stay
 *     at the right edge of the index and keyset pagination ("WHERE id > ?") just works.
 *   Clock moving backwards throws instead of minting: an NTP step back would otherwise
 *     replay a (timestamp, sequence) pair that was already handed out.
 *
 * COMPLEXITY
 *   Time  O(1) per id, a handful of shifts and ORs; only a full 4096-id millisecond spins.
 *   Space O(1) - two longs of mutable state (lastTimestamp, sequence) per generator.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Snowflake vs UUIDv4: v4 is random, so it scatters B-tree inserts and kills locality.
 *   - Snowflake vs UUIDv7: v7 is also time-ordered but 128 bits and carries no worker id.
 *   - How is workerId assigned? ZooKeeper/etcd ephemeral node, or pod ordinal in a
 *     StatefulSet. Two live processes sharing a worker id is the one fatal failure.
 *   - Clock skew: wait it out, or steal sequence bits to ride out a small backwards step.
 *   - IDs leak volume and timing to clients; if that matters, encrypt or hash the id.
 *
 * RUN
 *   main() runs 4 cases (typical, decode round-trip, 5000-id burst, bad worker id)
 *   and prints actual vs expected.
 */

import java.util.HashSet;
import java.util.Set;

class SnowflakeIdGenerator {

    /** Custom epoch: 2021-01-01T00:00:00Z. The 41-bit clock is counted from here. */
    private static final long EPOCH = 1609459200000L;

    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    // ~(-1L << n) is n ones: the largest value that fits in n bits.
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);          // 31
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);  // 31
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);           // 4095

    // Where each field starts, counting from the low bit.
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;                             // 12
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;        // 17
    private static final long TIMESTAMP_SHIFT =
            SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;                           // 22

    private final long workerId;
    private final long datacenterId;

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0)
            throw new IllegalArgumentException("Worker ID out of range 0.." + MAX_WORKER_ID);
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0)
            throw new IllegalArgumentException(
                    "Datacenter ID out of range 0.." + MAX_DATACENTER_ID);

        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /** Mints the next id. Synchronized because sequence/lastTimestamp are shared state. */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            // NTP stepped the clock back. Minting now would replay ids already issued.
            throw new IllegalStateException("Clock moved backwards by "
                    + (lastTimestamp - timestamp) + " ms; refusing to mint");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                // All 4096 slots for this millisecond are gone: wait for the clock to tick.
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    /** The inverse of nextId(): pulls the four fields back out of a minted id. */
    public static Parts decode(long id) {
        return new Parts(
                (id >>> TIMESTAMP_SHIFT) + EPOCH,                        // wall-clock millis
                (id >>> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID,
                (id >>> WORKER_ID_SHIFT) & MAX_WORKER_ID,
                id & SEQUENCE_MASK);
    }

    /** Decoded view of one id. */
    static class Parts {
        final long timestampMillis;
        final long datacenterId;
        final long workerId;
        final long sequence;

        Parts(long timestampMillis, long datacenterId, long workerId, long sequence) {
            this.timestampMillis = timestampMillis;
            this.datacenterId = datacenterId;
            this.workerId = workerId;
            this.sequence = sequence;
        }

        @Override
        public String toString() {
            return "dc=" + datacenterId + ", worker=" + workerId + ", seq=" + sequence;
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1 (typical): five ids from one generator are unique and strictly increasing.
        SnowflakeIdGenerator gen = new SnowflakeIdGenerator(7, 3);
        long[] ids = new long[5];
        for (int i = 0; i < ids.length; i++) ids[i] = gen.nextId();

        boolean increasing = true;
        for (int i = 1; i < ids.length; i++) increasing &= ids[i] > ids[i - 1];
        System.out.println("sample ids  : " + ids[0] + " .. " + ids[ids.length - 1]);
        print("case 1 strictly increasing", increasing, true);

        // Case 2 (round-trip): the id carries its own metadata; decode must return it.
        long before = System.currentTimeMillis();
        long id = gen.nextId();
        long after = System.currentTimeMillis();
        Parts p = decode(id);
        print("case 2 decoded fields", p, "dc=3, worker=7, seq=" + (id & SEQUENCE_MASK));
        print("case 2 timestamp in range",
                p.timestampMillis >= before && p.timestampMillis <= after, true);

        // Case 3 (edge / tricky): 5000 ids > the 4096-per-ms ceiling, so the sequence
        // rolls over and nextId() must spin to the next millisecond instead of repeating.
        SnowflakeIdGenerator burst = new SnowflakeIdGenerator(0, 0);
        Set<Long> seen = new HashSet<>();
        long previous = Long.MIN_VALUE;
        boolean stillIncreasing = true;
        for (int i = 0; i < 5000; i++) {
            long next = burst.nextId();
            seen.add(next);
            stillIncreasing &= next > previous;
            previous = next;
        }
        print("case 3 unique ids in burst", seen.size(), 5000);
        print("case 3 still increasing", stillIncreasing, true);

        // Case 4 (edge): worker id 32 does not fit in 5 bits and must be rejected at
        // construction, not silently masked into someone else's id space.
        String rejected;
        try {
            new SnowflakeIdGenerator(32, 0);
            rejected = "no exception";
        } catch (IllegalArgumentException e) {
            rejected = "IllegalArgumentException";
        }
        print("case 4 workerId=32", rejected, "IllegalArgumentException");
    }
}

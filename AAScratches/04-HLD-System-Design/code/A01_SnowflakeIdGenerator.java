import java.util.UUID;

// 128 Bits or (16 bytes)
// Indroducing UUID version 7 (time-ordered) in 26

// 64 bits (8 bytes)
// 1 bit unused (sign bit)
// 41 bits for time in milliseconds - gives us 69 years after new epoch used
// 5 bits for datacenter id - gives us 32 datacenters
// 5 bits for machine id - gives us 32 machines
// 12 bits for sequence - gives us 4096 ids per machine per millisecond

class SnowflakeIdGenerator {
    private final long workerId;
    private final long datacenterId;
    private final long epoch = 1609459200000L; // Custom epoch: 2021-01-01

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private static final long workerIdBits = 5L;
    private static final long datacenterIdBits = 5L;
    private static final long sequenceBits = 12L;

    private static final long maxWorkerId = ~(-1L << workerIdBits);
    private static final long maxDatacenterId = ~(-1L << datacenterIdBits);

    private static final long workerIdShift = sequenceBits;
    private static final long datacenterIdShift = sequenceBits + workerIdBits;
    private static final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private static final long sequenceMask = ~(-1L << sequenceBits);

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0)
            throw new IllegalArgumentException("Worker ID out of range");
        if (datacenterId > maxDatacenterId || datacenterId < 0)
            throw new IllegalArgumentException("Datacenter ID out of range");

        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards!");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // Sequence exhausted in this millisecond, wait for next
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public static void main(String[] args) {
        SnowflakeIdGenerator gen = new SnowflakeIdGenerator(1, 1);
        for (int i = 0; i < 5; i++) {
            System.out.println("Snowflake ID: " + gen.nextId());
        }
    }
}

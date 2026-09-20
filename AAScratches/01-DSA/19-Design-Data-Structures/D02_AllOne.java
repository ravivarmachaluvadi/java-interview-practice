/*
 * =====================================================================
 *  All O`one Data Structure                           LeetCode 432 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Track a multiset of string keys with four operations, every one in O(1):
 *   inc(key) adds one to the key's count (creating it at 1), dec(key) subtracts one and
 *   removes the key when its count hits 0, getMaxKey() and getMinKey() return ANY key at
 *   the highest / lowest count, or "" when the structure is empty.
 *   Counts are always positive, so "empty" and "count 0" are the same thing.
 *
 * EXAMPLE
 *   inc(hello) inc(hello)         -> max=hello  min=hello        (hello=2)
 *   inc(leet)                     -> max=hello  min=leet         (hello=2, leet=1)
 *   inc(leet) inc(leet)           -> max=leet   min=hello        (hello=2, leet=3)
 *   dec every key down to 0       -> max=""     min=""           (empty again)
 *
 * DESIGN  (two approaches, same interface, both driven from main)
 *   AllOneApi   the shared contract, so one test script runs against both.
 *
 *   1. TwoMaps    keyFreqMap : key  -> count
 *                 freqKeysMap: count -> set of keys at that count (bucket)
 *                 plus tracked minFreq / maxFreq ints.
 *                 Easy to write under time pressure and correct, but NOT strictly O(1):
 *                 when the last key at minFreq is decremented away, the next non-empty
 *                 bucket can be anywhere above, so minFreq has to scan upward.
 *
 *   2. BucketList a doubly linked list of buckets kept sorted by count, with sentinel
 *                 head and tail, plus keyBucketMap : key -> its bucket node.
 *                 min = head.next, max = tail.prev, always, with no scan. True O(1).
 *
 * KEY DECISIONS
 *   1. Store keys grouped BY COUNT, not sorted by count. A hash set per count is enough
 *      because getMaxKey/getMinKey may return any key in the bucket.
 *   2. inc moves a key exactly one bucket up and dec exactly one bucket down. Neighbouring
 *      buckets differ by exactly 1, so in approach 2 the target bucket is either the
 *      adjacent node (reuse it) or a brand-new node spliced in next to the old one.
 *   3. A bucket that loses its last key is unlinked immediately. That is what keeps
 *      head.next and tail.prev meaningful without any cleanup pass.
 *   4. Approach 1's minFreq scan is the whole reason approach 2 exists -- state approach 1
 *      in an interview, name that weakness yourself, then offer the linked list.
 *
 * COMPLEXITY
 *   TwoMaps     Time O(1) for inc / getMax / getMin; dec is O(1) amortised but O(maxCount)
 *               in the worst case when the minimum bucket empties.  Space O(number of keys).
 *   BucketList  Time O(1) worst case for all four operations.
 *               Space O(number of keys + number of distinct counts).
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not a TreeMap of counts? It gives O(log n), and the question demands O(1).
 *   - Why not a heap? Updating one key's count is O(n) to find it, and both min and max
 *     are needed, which would mean two heaps plus lazy deletion.
 *   - Return the FULL set of keys at the max count instead of any one: same buckets, but
 *     you now hand back the set, so it must not be mutated by the caller.
 *   - Make it thread-safe: the bucket splice is the critical section, not the maps.
 *
 * RUN
 *   main() runs one identical script (build up, tie, sparse counts, drain to empty)
 *   against both implementations and prints actual vs expected for every step.
 *   Ties are legitimately ambiguous, so those lines accept either valid key.
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class AllOne {

    /** Common contract so one main can drive both approaches. */
    interface AllOneApi {
        void inc(String key);

        void dec(String key);

        String getMaxKey();

        String getMinKey();
    }

    // =====================================================================
    // Approach 1: two HashMaps + tracked minFreq / maxFreq
    // =====================================================================
    static class TwoMaps implements AllOneApi {
        private final Map<String, Integer> keyFreqMap = new HashMap<>();       // key  -> frequency
        private final Map<Integer, Set<String>> freqKeysMap = new HashMap<>(); // freq -> keys at it
        private int minFreq = 0; // 0 means "empty"
        private int maxFreq = 0;

        /*
         * Fixed: three bugs that the naive version of this approach always has.
         *  - inserting a new key reset BOTH minFreq and maxFreq to 1, so maxFreq was lost.
         *  - inc() did minFreq++ when the min bucket emptied, but buckets are sparse
         *    (e.g. 1 -> 3), so minFreq could point at a bucket that does not exist -> NPE.
         *  - dec() also did minFreq++ when the min bucket emptied, but a decremented key
         *    moves DOWN, so minFreq must become newFreq, not minFreq + 1.
         */

        @Override
        public void inc(String key) {
            int oldFreq = keyFreqMap.getOrDefault(key, 0);
            int newFreq = oldFreq + 1;
            keyFreqMap.put(key, newFreq);
            freqKeysMap.computeIfAbsent(newFreq, k -> new HashSet<>()).add(key);

            if (oldFreq > 0) {
                removeFromBucket(key, oldFreq);
                // Every other key had freq >= oldFreq. If oldFreq was the min and its bucket
                // is now empty, the new min is exactly newFreq (nothing can sit in between).
                if (oldFreq == minFreq && !freqKeysMap.containsKey(oldFreq)) {
                    minFreq = newFreq;
                }
            } else {
                minFreq = 1; // a brand-new key is always the minimum
            }
            maxFreq = Math.max(maxFreq, newFreq);
        }

        @Override
        public void dec(String key) {
            Integer oldFreq = keyFreqMap.get(key);
            if (oldFreq == null) {
                return; // key does not exist; LeetCode guarantees it does, this is a safety net
            }
            int newFreq = oldFreq - 1;
            removeFromBucket(key, oldFreq);

            if (newFreq == 0) {
                keyFreqMap.remove(key);
            } else {
                keyFreqMap.put(key, newFreq);
                freqKeysMap.computeIfAbsent(newFreq, k -> new HashSet<>()).add(key);
            }

            if (keyFreqMap.isEmpty()) {
                minFreq = 0;
                maxFreq = 0;
                return;
            }
            // Every other key had freq <= oldFreq. If oldFreq was the max and its bucket
            // is now empty, the new max is exactly newFreq (newFreq > 0 here, else map is empty).
            if (oldFreq == maxFreq && !freqKeysMap.containsKey(oldFreq)) {
                maxFreq = newFreq;
            }
            if (newFreq > 0) {
                minFreq = Math.min(minFreq, newFreq);
            } else if (oldFreq == minFreq && !freqKeysMap.containsKey(oldFreq)) {
                // The only min key vanished. The next min is somewhere above - this scan is
                // O(maxFreq), the one place this design is not O(1). Approach 2 fixes it.
                while (!freqKeysMap.containsKey(minFreq)) {
                    minFreq++;
                }
            }
        }

        @Override
        public String getMaxKey() {
            return maxFreq == 0 ? "" : freqKeysMap.get(maxFreq).iterator().next();
        }

        @Override
        public String getMinKey() {
            return minFreq == 0 ? "" : freqKeysMap.get(minFreq).iterator().next();
        }

        private void removeFromBucket(String key, int freq) {
            Set<String> bucket = freqKeysMap.get(freq);
            bucket.remove(key);
            if (bucket.isEmpty()) {
                freqKeysMap.remove(freq); // an empty bucket must not be visible to min/max
            }
        }
    }

    // =====================================================================
    // Approach 2: doubly linked list of count buckets + key -> bucket map
    // =====================================================================
    static class BucketList implements AllOneApi {

        /** One bucket per distinct count; holds every key currently at that count. */
        private static class Bucket {
            final int count;
            final Set<String> keys = new HashSet<>();
            Bucket prev;
            Bucket next;

            Bucket(int count) {
                this.count = count;
            }
        }

        // Sentinel (dummy) head and tail: the list is always sorted by count, ascending.
        private final Bucket head = new Bucket(Integer.MIN_VALUE);
        private final Bucket tail = new Bucket(Integer.MAX_VALUE);
        private final Map<String, Bucket> keyBucketMap = new HashMap<>(); // key -> its bucket

        BucketList() {
            head.next = tail;
            tail.prev = head;
        }

        @Override
        public void inc(String key) {
            Bucket curr = keyBucketMap.get(key);
            // A new key starts at count 1, which belongs in the bucket right after head.
            Bucket anchor = (curr == null) ? head : curr;
            int newCount = (curr == null) ? 1 : curr.count + 1;
            moveKey(key, curr, anchor, anchor.next, newCount);
        }

        @Override
        public void dec(String key) {
            Bucket curr = keyBucketMap.get(key);
            if (curr == null) {
                return; // key does not exist; safety net, same as above
            }
            if (curr.count == 1) {
                keyBucketMap.remove(key); // count would hit 0, so the key disappears entirely
                detachKey(key, curr);
            } else {
                moveKey(key, curr, curr.prev, curr, curr.count - 1);
            }
        }

        @Override
        public String getMaxKey() {
            return tail.prev == head ? "" : tail.prev.keys.iterator().next();
        }

        @Override
        public String getMinKey() {
            return head.next == tail ? "" : head.next.keys.iterator().next();
        }

        /**
         * Put {@code key} into the bucket with {@code newCount}, which must sit between
         * {@code left} and {@code right}. Reuse that bucket if it already exists there,
         * otherwise splice in a new one. Then drop the key from its old bucket (if any).
         */
        private void moveKey(String key, Bucket old, Bucket left, Bucket right, int newCount) {
            Bucket target;
            if (right != tail && right.count == newCount) {
                target = right;                 // bucket already exists (inc case)
            } else if (left != head && left.count == newCount) {
                target = left;                  // bucket already exists (dec case)
            } else {
                target = new Bucket(newCount);  // create it between left and right
                insertAfter(left, target);
            }
            target.keys.add(key);
            keyBucketMap.put(key, target);
            if (old != null) {
                detachKey(key, old);
            }
        }

        private void detachKey(String key, Bucket bucket) {
            bucket.keys.remove(key);
            if (bucket.keys.isEmpty()) {
                removeBucket(bucket); // keeps head.next / tail.prev meaningful with no cleanup pass
            }
        }

        private void insertAfter(Bucket node, Bucket toInsert) {
            toInsert.prev = node;
            toInsert.next = node.next;
            node.next.prev = toInsert;
            node.next = toInsert;
        }

        private void removeBucket(Bucket node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.prev = null; // help GC and make any stale use fail loudly
            node.next = null;
        }
    }

    // =====================================================================
    // Driver: the same script against both approaches
    // =====================================================================

    /** Ties are legitimately ambiguous, so a step may list several acceptable answers. */
    private static void check(String label, String actual, String... allowed) {
        boolean ok = false;
        StringBuilder expected = new StringBuilder();
        for (int i = 0; i < allowed.length; i++) {
            if (i > 0) {
                expected.append(" or ");
            }
            expected.append("'").append(allowed[i]).append("'");
            if (allowed[i].equals(actual)) {
                ok = true;
            }
        }
        System.out.println("  " + label + " -> actual '" + actual + "'   expected "
                + expected + (ok ? "   OK" : "   FAIL"));
    }

    private static void run(String label, AllOneApi ds) {
        System.out.println("== " + label + " ==");

        check("empty            max", ds.getMaxKey(), "");
        check("empty            min", ds.getMinKey(), "");

        ds.inc("hello");
        ds.inc("hello");
        check("hello=2          max", ds.getMaxKey(), "hello");
        check("hello=2          min", ds.getMinKey(), "hello");

        ds.inc("leet");
        check("hello=2 leet=1   max", ds.getMaxKey(), "hello");
        check("hello=2 leet=1   min", ds.getMinKey(), "leet");

        ds.inc("leet");
        ds.inc("leet");
        check("hello=2 leet=3   max", ds.getMaxKey(), "leet");
        check("hello=2 leet=3   min", ds.getMinKey(), "hello");

        ds.dec("leet");
        check("both=2 tie       max", ds.getMaxKey(), "hello", "leet");
        check("both=2 tie       min", ds.getMinKey(), "hello", "leet");

        // Sparse counts: hello=2, leet=2, world goes 1 -> 2 -> 3, so bucket 1 empties.
        ds.inc("world");
        check("world=1          max", ds.getMaxKey(), "hello", "leet");
        check("world=1          min", ds.getMinKey(), "world");

        ds.inc("world");
        ds.inc("world");
        check("world=3          max", ds.getMaxKey(), "world");
        check("world=3          min", ds.getMinKey(), "hello", "leet");

        ds.dec("nosuchkey"); // decrementing an absent key must be a harmless no-op
        check("dec missing key  max", ds.getMaxKey(), "world");

        // Drain both count-2 keys to 0 so the minimum has to jump from 1 up to 3.
        ds.dec("hello");
        ds.dec("hello");
        ds.dec("leet");
        ds.dec("leet");
        check("only world=3     max", ds.getMaxKey(), "world");
        check("only world=3     min", ds.getMinKey(), "world");

        ds.dec("world");
        ds.dec("world");
        ds.dec("world");
        check("drained          max", ds.getMaxKey(), "");
        check("drained          min", ds.getMinKey(), "");
        System.out.println();
    }

    public static void main(String[] args) {
        run("Approach 1: two maps + tracked min/max", new TwoMaps());
        run("Approach 2: doubly linked bucket list (true O(1))", new BucketList());
    }
}

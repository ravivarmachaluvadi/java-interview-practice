import java.util.*;

/**
 * Problem: LeetCode 432. All O`one Data Structure
 * https://leetcode.com/problems/all-oone-data-structure/description/
 * Support inc(key), dec(key), getMaxKey(), getMinKey() - all in O(1).
 *
 * Approaches:
 *  1. TwoMaps    - key->freq map + freq->Set<key> map + tracked minFreq/maxFreq.
 *                  Simple, but NOT strictly O(1): when the only min-frequency key
 *                  drops to 0 we must scan upward to find the next non-empty bucket.
 *  2. BucketList - doubly-linked list of count buckets (sorted by count) + key->bucket map.
 *                  True O(1): min bucket = head.next, max bucket = tail.prev, always.
 */
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
        private final Map<Integer, Set<String>> freqKeysMap = new HashMap<>(); // freq -> keys with that freq
        private int minFreq = 0; // 0 means "empty"
        private int maxFreq = 0;

        /*
         * Pitfalls from the first attempt (now fixed below):
         *  - inserting a new key reset BOTH minFreq and maxFreq to 1 -> maxFreq lost.
         *  - inc() did minFreq++ when the min bucket emptied, but buckets are sparse
         *    (e.g. 1 -> 3), so minFreq could point at a bucket that does not exist -> NPE.
         *  - dec() also did minFreq++ when the min bucket emptied, but a decremented key
         *    goes DOWN, so minFreq must become newFreq, not minFreq + 1.
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
            if (oldFreq == null) return; // key does not exist
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
                // The only min key vanished. Next min is somewhere above - this scan is
                // O(maxFreq), the one place this design is not O(1). Approach 2 fixes it.
                while (!freqKeysMap.containsKey(minFreq)) minFreq++;
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
            if (bucket.isEmpty()) freqKeysMap.remove(freq);
        }
    }

    // =====================================================================
    // Approach 2: doubly-linked list of count buckets + key -> bucket map
    // =====================================================================
    static class BucketList implements AllOneApi {

        /** One bucket per distinct count; holds every key currently at that count. */
        private static class Bucket {
            final int count;
            final Set<String> keys = new HashSet<>();
            Bucket prev, next;

            Bucket(int count) { this.count = count; }
        }

        // Sentinel (dummy) head & tail: list is always sorted by count, ascending.
        private final Bucket head = new Bucket(Integer.MIN_VALUE);
        private final Bucket tail = new Bucket(Integer.MAX_VALUE);
        private final Map<String, Bucket> keyBucketMap = new HashMap<>(); // key -> its current bucket

        BucketList() {
            head.next = tail;
            tail.prev = head;
        }

        @Override
        public void inc(String key) {
            Bucket curr = keyBucketMap.get(key);
            // New key starts at count 1, which is the bucket right after head.
            Bucket anchor = (curr == null) ? head : curr;
            int newCount = (curr == null) ? 1 : curr.count + 1;
            moveKey(key, curr, anchor, anchor.next, newCount);
        }

        @Override
        public void dec(String key) {
            Bucket curr = keyBucketMap.get(key);
            if (curr == null) return; // LeetCode guarantees the key exists; safety check
            if (curr.count == 1) {
                keyBucketMap.remove(key);
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
         * {@code left} and {@code right}. Reuse the bucket if it already exists there,
         * otherwise create it. Then drop the key from its old bucket (if any).
         */
        private void moveKey(String key, Bucket old, Bucket left, Bucket right, int newCount) {
            Bucket target;
            if (right != tail && right.count == newCount) {
                target = right;                 // bucket already exists (inc case)
            } else if (left != head && left.count == newCount) {
                target = left;                  // bucket already exists (dec case)
            } else {
                target = new Bucket(newCount);  // create between left and right
                insertAfter(left, target);
            }
            target.keys.add(key);
            keyBucketMap.put(key, target);
            if (old != null) detachKey(key, old);
        }

        private void detachKey(String key, Bucket bucket) {
            bucket.keys.remove(key);
            if (bucket.keys.isEmpty()) removeBucket(bucket);
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
            node.prev = node.next = null; // optional: help GC / catch stale use
        }
    }

    // =====================================================================
    // Driver: same script against both approaches
    // =====================================================================
    private static void run(String label, AllOneApi ds) {
        System.out.println("== " + label + " ==");
        System.out.println("empty            -> max='" + ds.getMaxKey() + "' min='" + ds.getMinKey() + "'");

        ds.inc("hello"); ds.inc("hello");
        System.out.println("hello x2         -> max=" + ds.getMaxKey() + " min=" + ds.getMinKey()); // hello / hello

        ds.inc("leet");
        System.out.println("+leet            -> max=" + ds.getMaxKey() + " min=" + ds.getMinKey()); // hello / leet

        ds.inc("leet"); ds.inc("leet");
        System.out.println("leet x3          -> max=" + ds.getMaxKey() + " min=" + ds.getMinKey()); // leet / hello

        ds.dec("leet");
        System.out.println("leet -> 2        -> max=" + ds.getMaxKey() + " min=" + ds.getMinKey()); // both count 2: either

        // Sparse buckets: hello=2, leet=2, then world=1. Bumping world twice goes 1 -> 2 -> 3.
        ds.inc("world");
        System.out.println("+world (1)       -> max=" + ds.getMaxKey() + " min=" + ds.getMinKey()); // hello|leet / world
        ds.inc("world"); ds.inc("world");
        System.out.println("world x3         -> max=" + ds.getMaxKey() + " min=" + ds.getMinKey()); // world / hello|leet

        // Drain the min keys to 0 so the min bucket must jump upward (the tricky case).
        ds.dec("hello"); ds.dec("hello");
        ds.dec("leet");  ds.dec("leet");
        System.out.println("drop hello,leet  -> max=" + ds.getMaxKey() + " min=" + ds.getMinKey()); // world / world

        ds.dec("world"); ds.dec("world"); ds.dec("world");
        System.out.println("drop world       -> max='" + ds.getMaxKey() + "' min='" + ds.getMinKey() + "'"); // '' / ''
        System.out.println();
    }

    public static void main(String[] args) {
        run("Approach 1: two maps + tracked min/max", new TwoMaps());
        run("Approach 2: doubly-linked bucket list (true O(1))", new BucketList());
    }
}

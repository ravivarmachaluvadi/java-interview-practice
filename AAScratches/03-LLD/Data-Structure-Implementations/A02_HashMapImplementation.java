/*
 * =====================================================================
 *  HashMap from scratch (separate chaining)   LLD build | Medium    MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Build a generic key-value store with average O(1) put / get / remove, using
 *   only an array. Two different keys may hash to the same slot, so the design
 *   must answer three questions: how a key becomes an index, what happens when
 *   two keys land on the same index, and what happens when the table fills up.
 *
 * DESIGN  (classes and why)
 *   Entry<K, V>    one key-value pair. key is final (changing it would strand
 *                  the entry in the wrong bucket); value is mutable so an
 *                  overwrite is an assignment, not a remove-then-insert.
 *   MyHashMap<K,V> owns buckets: a list of B chains. Each chain is a List of
 *                  entries whose keys all hashed to that index.
 *                  Layout: buckets[ h(key) ] -> [Entry, Entry, ...]
 *   The array gives O(1) addressing; the chain absorbs collisions.
 *
 * KEY DECISIONS
 *   1. Separate chaining, not open addressing. Chains are far easier to delete
 *      from - open addressing needs tombstones or a back-shift on every remove.
 *   2. Index = Math.abs(key.hashCode() % B). hashCode() % B is already inside
 *      (-B, B), so abs() is safe here. Note that the tempting
 *      Math.abs(key.hashCode()) % B is NOT safe: abs(Integer.MIN_VALUE) is
 *      still negative, and that one line has crashed real production maps.
 *   3. Resize when size / B exceeds 0.75. Chains grow as the load factor grows,
 *      so O(1) lookup is only true if the load factor is bounded - the resize
 *      is what makes the "average O(1)" claim honest, not an optional extra.
 *   4. Rehash on resize, never copy. h(key) depends on B, so every entry has to
 *      be re-indexed against the new bucket count.
 *   5. equals() finds the key, hashCode() only finds the bucket. A key whose
 *      hashCode changes after insertion is lost forever: use immutable keys.
 *   6. Simplification kept from the original: null keys and null values are
 *      rejected (put is a no-op). Real HashMap allows one null key and any
 *      number of null values, which forces containsKey to exist separately.
 *
 * COMPLEXITY
 *   Time  put / get / remove: O(1 + n/B) average, which is O(1) while the load
 *         factor is capped at 0.75; O(n) worst case if every key collides.
 *         resize is O(n) but is paid once per doubling, so O(1) amortized.
 *   Space O(n + B): one Entry per pair plus the bucket array itself.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why does real java.util.HashMap turn a long chain into a red-black tree
 *     at 8 nodes? (worst case drops from O(n) to O(log n) under hash attacks)
 *   - Why is the real bucket count a power of two? (index = hash & (B-1), and a
 *     spread step XORs the high bits down so they are not thrown away)
 *   - Open addressing with linear probing: how do you delete safely?
 *   - Make it concurrent: one lock, striped locks, or ConcurrentHashMap's CAS
 *     on the head of each bin?
 *
 * RUN
 *   main() runs 3 cases (typical puts with an overwrite, empty/null edges, and
 *   a forced-collision case that triggers a resize) printing actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

class MyHashMap<K, V> {

    private static final int DEFAULT_BUCKETS = 10;
    private static final double MAX_LOAD_FACTOR = 0.75;

    private static class Entry<K, V> {
        private final K key;   // final: a mutated key would sit in the wrong bucket
        private V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private List<List<Entry<K, V>>> buckets;
    private int size;

    MyHashMap() {
        this(DEFAULT_BUCKETS);
    }

    /** A small bucket count is useful in tests: collisions and resizes happen early. */
    MyHashMap(int initialBuckets) {
        if (initialBuckets < 1) {
            throw new IllegalArgumentException("bucket count must be >= 1");
        }
        this.buckets = newBuckets(initialBuckets);
        this.size = 0;
    }

    private static <K, V> List<List<Entry<K, V>>> newBuckets(int count) {
        List<List<Entry<K, V>>> fresh = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            fresh.add(new ArrayList<>());
        }
        return fresh;
    }

    private int bucketIndexOf(K key) {
        // hashCode() % B is in (-B, B), so abs() cannot overflow here.
        return Math.abs(key.hashCode() % buckets.size());
    }

    public int size() {
        return size;
    }

    /** Exposed so the demo can show the table doubling. */
    public int bucketCount() {
        return buckets.size();
    }

    public void put(K key, V value) {
        if (key == null || value == null) {
            return;   // documented simplification: this map stores neither
        }
        List<Entry<K, V>> chain = buckets.get(bucketIndexOf(key));
        for (Entry<K, V> entry : chain) {
            if (key.equals(entry.key)) {
                entry.value = value;   // same key -> overwrite, size is unchanged
                return;
            }
        }
        chain.add(new Entry<>(key, value));
        size++;
        if ((double) size / buckets.size() > MAX_LOAD_FACTOR) {
            resize();
        }
    }

    public V get(K key) {
        if (key == null) {
            return null;
        }
        for (Entry<K, V> entry : buckets.get(bucketIndexOf(key))) {
            if (key.equals(entry.key)) {
                return entry.value;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /** Removes the key and returns its value, or null if the key was absent. */
    public V remove(K key) {
        if (key == null) {
            return null;
        }
        List<Entry<K, V>> chain = buckets.get(bucketIndexOf(key));
        for (int i = 0; i < chain.size(); i++) {
            if (key.equals(chain.get(i).key)) {
                size--;
                return chain.remove(i).value;
            }
        }
        return null;
    }

    /**
     * Doubles the bucket count and re-indexes every entry, because the index
     * depends on the bucket count. Entries cannot simply be copied across.
     */
    private void resize() {
        List<List<Entry<K, V>>> oldBuckets = buckets;
        buckets = newBuckets(oldBuckets.size() * 2);
        for (List<Entry<K, V>> chain : oldBuckets) {
            for (Entry<K, V> entry : chain) {
                buckets.get(bucketIndexOf(entry.key)).add(entry);
            }
        }
    }

    /**
     * Bucket order, not insertion order - which is exactly the lesson: a hash
     * map has no meaningful iteration order, and it changes after a resize.
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "{", "}");
        for (List<Entry<K, V>> chain : buckets) {
            for (Entry<K, V> entry : chain) {
                joiner.add(entry.key + "=" + entry.value);
            }
        }
        return joiner.toString();
    }
}

class HashMapImplementation {

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1 - typical: three distinct keys, then an overwrite of key 1.
        MyHashMap<Integer, Integer> map = new MyHashMap<>();
        map.put(1, 2);
        map.put(3, 4);
        map.put(5, 6);
        print("case 1 get(3)", map.get(3), 4);
        print("case 1 size", map.size(), 3);
        map.put(1, 7);
        print("case 1 overwrite get(1)", map.get(1), 7);
        print("case 1 size after overwrite", map.size(), 3);
        print("case 1 contents", map, "{1=7, 3=4, 5=6}");

        // Case 2 - edge: empty map, absent key, null key, null value.
        MyHashMap<String, String> words = new MyHashMap<>();
        print("case 2 empty size", words.size(), 0);
        print("case 2 empty contents", words, "{}");
        print("case 2 get on empty", words.get("one"), null);
        print("case 2 remove on empty", words.remove("one"), null);
        words.put("one", "uno");
        words.put("one", "eins");                // overwrite, not a second entry
        print("case 2 overwrite", words.get("one"), "eins");
        words.put(null, "ignored");
        words.put("two", null);
        print("case 2 nulls rejected", words.size(), 1);
        print("case 2 containsKey(two)", words.containsKey("two"), false);
        print("case 2 remove(one)", words.remove("one"), "eins");
        print("case 2 size after remove", words.size(), 0);

        // Case 3 - tricky: 4 buckets, keys chosen so 1, 5, 9, 13 all hash to the
        // same bucket. The 4th put pushes the load factor past 0.75 and resizes.
        MyHashMap<Integer, Integer> collide = new MyHashMap<>(4);
        collide.put(1, 10);
        collide.put(5, 50);
        collide.put(9, 90);    // all three are now one chain in bucket 1
        print("case 3 buckets before", collide.bucketCount(), 4);
        print("case 3 chained gets", collide.get(1) + "/" + collide.get(5) + "/" + collide.get(9),
                "10/50/90");
        collide.put(13, 130);  // size 4 / 4 buckets = 1.0 > 0.75 -> resize
        print("case 3 buckets after", collide.bucketCount(), 8);
        print("case 3 gets survive rehash",
                collide.get(1) + "/" + collide.get(5) + "/"
                        + collide.get(9) + "/" + collide.get(13),
                "10/50/90/130");
        collide.put(-7, -70);  // negative hashCode must still land on a valid index
        print("case 3 negative key", collide.get(-7), -70);
        print("case 3 bucket order", collide, "{1=10, 9=90, 5=50, 13=130, -7=-70}");
        print("case 3 remove(5)", collide.remove(5), 50);
        print("case 3 size after remove", collide.size(), 4);
        print("case 3 containsKey(5)", collide.containsKey(5), false);
    }
}

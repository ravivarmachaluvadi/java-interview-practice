/**
 * Implements a simple generic hash map using separate chaining.
 *
 * Problem: Store key-value pairs with efficient lookup, insertion and update
 * operations while handling collisions via linked lists (buckets).
 *
 * Approach:
 * - Use an ArrayList of buckets; each bucket is a List of Entry objects.
 * - Compute bucket index by hashing the key and taking modulus of bucket count.
 * - On put: if key exists, overwrite value; otherwise add new entry to bucket.
 * - On get: search bucket for matching key and return its value or null.
 *
 * Time Complexity:
 *   Average O(1) for put/get when load factor is low.
 *   Worst-case O(n) if all keys collide into one bucket.
 *
 * Space Complexity:
 *   O(n + b) where n = number of entries, b = number of buckets (fixed at 10).
 */
import java.util.*;

class MyHashMap<K, V> {

    private final ArrayList<List<Entry<K, V>>> listOfBucket;

    private static class Entry<K, V> {
        private final K key;
        private V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    MyHashMap() {
        this.listOfBucket = new ArrayList<>();
        // Start with 10 buckets
        for (int i = 0; i < 10; ++i) {
            this.listOfBucket.add(new ArrayList<>());
        }
    }

    private int getBucketIndex(K key) {
        return Math.abs(key.hashCode() % listOfBucket.size());
    }

    void put(K key, V value) {
        if (key == null || value == null) {
            return;
        }
        int idx = getBucketIndex(key);
        List<Entry<K, V>> bucketList = this.listOfBucket.get(idx);

        for (Entry<K, V> i : bucketList) {
            if (key.equals(i.key)) {
                i.value = value;
                return;
            }
        }
        bucketList.add(new Entry<>(key, value));
    }

    V get(K key) {
        if (key == null) return null;

        int idx = getBucketIndex(key);
        List<Entry<K, V>> bucketList = this.listOfBucket.get(idx);
        for (Entry<K, V> i : bucketList) {
            if (key.equals(i.key)) {
                return i.value;
            }
        }
        return null;
    }
}


public class HashMapImplementation {

    public void doTestsPass() {
        // Integer Test Cases
        int[][] testCases = {{1, 2}, {3, 4}, {5, 6}, {1, 7}, {1, 8}};
        boolean passed = true;

        MyHashMap<Integer, Integer> map = new MyHashMap<>();

        for (int[] test : testCases) {
            Integer key = test[0];
            Integer value = test[1];
            map.put(key, value);

            if (!value.equals(map.get(key))) {
                System.out.println("Test failed [" + key + "," + value + "]");
                passed = false;
            }
        }

        // String Test Cases
        MyHashMap<String, String> strMap = new MyHashMap<>();
        String[][] strTestCases = {{"one", "two"}, {"three", "four"}, {"one", "five"}};

        for (String[] test : strTestCases) {
            String key = test[0];
            String value = test[1];
            strMap.put(key, value);
            if (!value.equals(strMap.get(key))) {
                System.out.println("Test failed [" + key + "," + value + "]");
                passed = false;
            }
        }

        if (passed) {
            System.out.println("All tests passed");
        }
    }

    /**
     * Execution entry point.
     */
    public static void main(String[] args) {
        new HashMapImplementation().doTestsPass();
    }
}

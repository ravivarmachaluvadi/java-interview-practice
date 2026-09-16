import java.util.*;

/**
 * Design a time-based key-value data structure that can store
 * <p>
 * multiple values for the same key at different time stamps
 * <p>
 * and retrieve the key's value at a certain timestamp.
 */
class TimeBasedKeyValueStore {
    // Map from key → (map from timestamp → value)
    private Map<String, TreeMap<Integer, String>> map;

    /**
     * Initialize your data structure here.
     */
    public TimeBasedKeyValueStore() {
        map = new HashMap<>();
    }

    /**
     * Stores the value for key at timestamp.
     */
    public void set(String key, String value, int timestamp) {
        map.computeIfAbsent(key, k -> new TreeMap<>())
                .put(timestamp, value);
    }

    /**
     * Returns a value such that it was set at the largest timestamp ≤ given timestamp.
     */
    public String get(String key, int timestamp) {
        if (!map.containsKey(key)) {
            return "";
        }
        TreeMap<Integer, String> tree = map.get(key);
        // floorKey gives the greatest key ≤ timestamp, or null if none.
        // lower bound
        Integer t = tree.floorKey(timestamp);
        if (t == null) {
            return "";
        }
        return tree.get(t);
    }

    // Main method with example usage
    public static void main(String[] args) {
        TimeBasedKeyValueStore timeMap = new TimeBasedKeyValueStore();
        timeMap.set("foo", "bar", 1);
        System.out.println(timeMap.get("foo", 1));   // prints "bar"
        System.out.println(timeMap.get("foo", 3));   // prints "bar", since only one entry at timestamp 1
        timeMap.set("foo", "bar2", 4);
        System.out.println(timeMap.get("foo", 4));   // prints "bar2"
        System.out.println(timeMap.get("foo", 5));   // prints "bar2"
        System.out.println(timeMap.get("foo", 0));   // prints "", no timestamp ≤ 0
    }
}

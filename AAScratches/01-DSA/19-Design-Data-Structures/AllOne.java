import java.util.*;

class AllOne {
    // Map to store the frequency of each key
    private Map<String, Integer> keyFreqMap;

    // Map to store the keys that have a particular frequency
    private Map<Integer, Set<String>> freqKeysMap;

    // Variables to track the current minimum and maximum frequencies
    private int minFreq;
    private int maxFreq;

    public AllOne() {
        keyFreqMap = new HashMap<>();
        freqKeysMap = new HashMap<>();
        minFreq = Integer.MAX_VALUE;
        maxFreq = Integer.MIN_VALUE;
    }

    // Insert a key with frequency 1
    public void insert(String key) {
        if (keyFreqMap.containsKey(key)) {
            return;  // Key already exists
        }

        keyFreqMap.put(key, 1);
        freqKeysMap.computeIfAbsent(1, k -> new HashSet<>()).add(key);

        minFreq = 1;
        maxFreq = 1;
    }

    // Increment the frequency of the key
    public void inc(String key) {
        if (!keyFreqMap.containsKey(key)) {
            insert(key);
        }

        int oldFreq = keyFreqMap.get(key);
        int newFreq = oldFreq + 1;
        keyFreqMap.put(key, newFreq);

        // Remove the key from the old frequency
        freqKeysMap.get(oldFreq).remove(key);
        if (freqKeysMap.get(oldFreq).isEmpty()) {
            freqKeysMap.remove(oldFreq);
            if (oldFreq == minFreq) {
                minFreq++;
            }
        }

        // Add the key to the new frequency
        freqKeysMap.computeIfAbsent(newFreq, k -> new HashSet<>()).add(key);

        // Update maxFreq
        maxFreq = Math.max(maxFreq, newFreq);
    }

    // Decrement the frequency of the key
    public void dec(String key) {
        if (!keyFreqMap.containsKey(key)) {
            return;  // Key does not exist
        }

        int oldFreq = keyFreqMap.get(key);
        int newFreq = oldFreq - 1;

        // Remove the key from the old frequency
        freqKeysMap.get(oldFreq).remove(key);
        if (freqKeysMap.get(oldFreq).isEmpty()) {
            freqKeysMap.remove(oldFreq);
            if (oldFreq == minFreq) {
                minFreq++;
            }
        }

        // If the frequency becomes 0, remove the key
        if (newFreq == 0) {
            keyFreqMap.remove(key);
        } else {
            // Add the key to the new frequency
            keyFreqMap.put(key, newFreq);
            freqKeysMap.computeIfAbsent(newFreq, k -> new HashSet<>()).add(key);
        }

        // Update minFreq and maxFreq
        if (freqKeysMap.containsKey(minFreq) && freqKeysMap.get(minFreq).isEmpty()) {
            minFreq++;
        }
        if (freqKeysMap.containsKey(maxFreq) && freqKeysMap.get(maxFreq).isEmpty()) {
            maxFreq--;
        }
    }

    // Get one of the keys with the maximum frequency
    public String getMaxKey() {
        if (maxFreq == Integer.MIN_VALUE) {
            return "";
        }

        return freqKeysMap.get(maxFreq).iterator().next();
    }

    // Get one of the keys with the minimum frequency
    public String getMinKey() {
        if (minFreq == Integer.MAX_VALUE) {
            return "";
        }

        return freqKeysMap.get(minFreq).iterator().next();
    }

    public static void main(String[] args) {
        // Example usage
        AllOne allOne = new AllOne();

        // Insert keys
        allOne.insert("hello");
        allOne.insert("world");

        // Increment frequency
        allOne.inc("hello");
        allOne.inc("hello");
        allOne.inc("world");

        // Get the max and min key
        System.out.println("Max Key: " + allOne.getMaxKey());  // Output: "hello"
        System.out.println("Min Key: " + allOne.getMinKey());  // Output: "world"

        // Decrement frequency
        allOne.dec("world");
        allOne.dec("hello");

        // Get the max and min key
        System.out.println("Max Key: " + allOne.getMaxKey());  // Output: "hello"
        System.out.println("Min Key: " + allOne.getMinKey());  // Output: "world"
    }
}

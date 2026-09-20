/*
 * =====================================================================
 *  TreeMap floorKey and friends                       Java Core | Easy
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   TreeMap is a sorted map (red-black tree), so on top of get/put it can answer
 *   "which key is nearest to this probe?" in O(log n). floorKey(k) returns the
 *   greatest key that is <= k, or null when every key in the map is bigger.
 *
 * WHAT YOU WILL SEE
 *   map = {5=A, 10=B, 15=C} floorKey(3)  -> null   no key is <= 3
 *   floorKey(12) -> 10     10 is the greatest key <= 12
 *   floorKey(10) -> 10     floor INCLUDES an exact match
 *   floorEntry(12) -> 10=B same lookup, but you also get the value
 *
 * HOW IT WORKS
 *   1. Walk down from the root comparing the probe with the current key.
 *   2. Going right (probe is bigger) records the current key as the best
 *      candidate so far; going left discards it.
 *   3. The last recorded candidate when the walk falls off the tree is the floor.
 *   4. Same walk, four answers: lower/higher EXCLUDE the probe,
 *      floor/ceiling INCLUDE it; floor/lower look down, ceiling/higher look up.
 *
 * KEY INSIGHT
 *   Reach for TreeMap the moment a problem says "nearest", "previous", "next",
 *   "last value before time T" or "which bucket does this fall into". A HashMap
 *   cannot answer those at all; scanning a sorted array is O(n) per query.
 *
 * GOTCHAS
 *   - The return type is Integer, not int: a miss returns null, so unboxing a
 *     floorKey result straight into an int can throw NullPointerException.
 *   - floorKey needs only a key to compare, so it works even when the key is absent.
 *   - Ordering comes from compareTo (or the supplied Comparator), never equals.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Difference between floorKey and lowerKey? (<= versus strictly <)
 *   - How would you find the value in force at a timestamp? (floorEntry on a
 *     TreeMap<timestamp, value> - the standard "time-based key-value store")
 *   - When is a sorted array plus binary search better? (static data, no inserts)
 *   - What does subMap/headMap give you that floorKey does not? (a live range view)
 *
 * RUN
 *   main() runs 3 groups of cases (typical, miss, exact match) and prints
 *   actual vs expected.
 */
import java.util.Map;
import java.util.TreeMap;

class FloorKeyExample {

    private static TreeMap<Integer, String> sampleMap() {
        TreeMap<Integer, String> map = new TreeMap<>();
        map.put(5, "A");
        map.put(10, "B");
        map.put(15, "C");
        return map;
    }

    /** Greatest key <= probe, or null when every key in the map is bigger. */
    static Integer floorKey(TreeMap<Integer, String> map, int probe) {
        return map.floorKey(probe);
    }

    /** Same walk as floorKey, but hands back the value too - the useful form in real code. */
    static Map.Entry<Integer, String> floorEntry(TreeMap<Integer, String> map, int probe) {
        return map.floorEntry(probe);
    }

    /** All four navigation keys side by side; this is the table people mix up. */
    static String neighbours(TreeMap<Integer, String> map, int probe) {
        return "lower=" + map.lowerKey(probe)      // strictly <  probe
                + " floor=" + map.floorKey(probe)     // <=            probe
                + " ceiling=" + map.ceilingKey(probe) // >=            probe
                + " higher=" + map.higherKey(probe);  // strictly >  probe
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        TreeMap<Integer, String> map = sampleMap();
        System.out.println("map                     : " + map);

        // Typical: probe sits between two keys.
        print("floorKey(12)            : ", floorKey(map, 12), 10);
        print("floorEntry(12)          : ", floorEntry(map, 12), "10=B");

        // Edge: probe below every key -> null, not an exception and not 0.
        print("floorKey(3)             : ", floorKey(map, 3), "null");
        print("floorEntry(3)           : ", floorEntry(map, 3), "null");

        // Tricky: exact match. floor includes it, lower does not.
        print("floorKey(10)            : ", floorKey(map, 10), 10);
        print("lowerKey(10)            : ", map.lowerKey(10), 5);
        print("neighbours(10)          : ", neighbours(map, 10),
                "lower=5 floor=10 ceiling=10 higher=15");

        // Probe above every key: floor is the last key, ceiling/higher are null.
        print("neighbours(99)          : ", neighbours(map, 99),
                "lower=15 floor=15 ceiling=null higher=null");
    }
}

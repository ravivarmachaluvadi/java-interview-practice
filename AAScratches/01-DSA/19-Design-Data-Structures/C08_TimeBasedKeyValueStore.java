/*
 * =====================================================================
 *  Time Based Key-Value Store             LeetCode 981 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Design a store where set(key, value, timestamp) records a value for a key at a
 *   point in time, and get(key, timestamp) returns the value written at the LARGEST
 *   stored timestamp that is <= the queried one, or "" if there is none.
 *   Timestamps passed to set are strictly increasing per key.
 *
 * EXAMPLE
 *   set("foo", "bar", 1)
 *   get("foo", 1) -> "bar"    exact hit
 *   get("foo", 3) -> "bar"    nothing newer than 1 yet, so the value at 1 still holds
 *   set("foo", "bar2", 4)
 *   get("foo", 5) -> "bar2"   largest stamp <= 5 is 4
 *   get("foo", 0) -> ""       every stored stamp is newer than the query
 *
 * DESIGN  (TreeMap floorKey per key)
 *   Map<String, TreeMap<Integer,String>> store
 *     outer HashMap   key -> that key's own timeline, so keys never interfere.
 *     inner TreeMap   timestamp -> value, kept sorted by timestamp.
 *   set  store.computeIfAbsent(key, ...).put(timestamp, value)  - one sorted insert.
 *   get  floorKey(timestamp) is exactly "greatest key <= timestamp", returning null
 *        when the timeline starts after the query; then read that entry's value.
 *
 * KEY DECISIONS
 *   - Why a TreeMap and not a HashMap? A point-in-time read is a RANGE question, not an
 *     equality one. A HashMap can only answer "is this exact stamp present".
 *   - floorKey vs the neighbours: floor is <= (what we want), lower is strictly <,
 *     ceiling is >=, higher is strictly >. Picking the wrong one is the classic slip.
 *   - A timeline per key, not one global sorted map: a single map would force get to
 *     skip past other keys' entries, turning an O(log n) read into a scan.
 *   - Because set's timestamps increase per key, the timeline is append-only. That is
 *     what makes the array-plus-binary-search variant below equivalent and O(1) to add.
 *
 * COMPLEXITY
 *   Time  set O(log m), get O(log m), where m is the number of writes for that key
 *   Space O(total writes) - every (timestamp, value) pair is kept, nothing is merged
 *
 * INTERVIEW FOLLOW-UPS
 *   - No TreeMap allowed: keep an ArrayList per key and hand-roll the floor with binary
 *     search - the appends stay sorted for free. (SnapshotArray, LC 1146, is that form.)
 *   - Unsorted or out-of-order timestamps: TreeMap still works, the array version breaks.
 *   - Bound the memory: evict stamps older than a retention window, or snapshot-compact.
 *   - Concurrent readers and writers: ConcurrentSkipListMap gives the same floorKey.
 *
 * RUN
 *   main() runs 3 cases: the LeetCode walkthrough, edge cases (unknown key, and a query
 *   before the first write), and a tricky case with two independent keys plus an
 *   overwrite at an already-used timestamp. Each line prints actual vs expected.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class TimeBasedKeyValueStore {

    /** key -> (timestamp -> value), sorted by timestamp so a floor query is O(log m). */
    private final Map<String, TreeMap<Integer, String>> store;

    public TimeBasedKeyValueStore() {
        store = new HashMap<>();
    }

    /** Stores value for key at the given timestamp. */
    public void set(String key, String value, int timestamp) {
        store.computeIfAbsent(key, k -> new TreeMap<>()).put(timestamp, value);
    }

    /**
     * Returns the value written at the largest timestamp <= the given one,
     * or "" when the key is unknown or its timeline starts later.
     */
    public String get(String key, int timestamp) {
        TreeMap<Integer, String> timeline = store.get(key);
        if (timeline == null) {
            return "";
        }
        // floorKey = greatest stored stamp <= timestamp, or null if there is none.
        Integer at = timeline.floorKey(timestamp);
        return at == null ? "" : timeline.get(at);
    }

    public static void main(String[] args) {
        // ---- case 1: the LeetCode walkthrough -----------------------------------
        TimeBasedKeyValueStore timeMap = new TimeBasedKeyValueStore();
        timeMap.set("foo", "bar", 1);
        print("case 1 get(foo, 1)", timeMap.get("foo", 1), "bar");
        // Nothing newer exists yet, so the value written at stamp 1 is still current.
        print("case 1 get(foo, 3)", timeMap.get("foo", 3), "bar");
        timeMap.set("foo", "bar2", 4);
        print("case 1 get(foo, 4)", timeMap.get("foo", 4), "bar2");
        print("case 1 get(foo, 5)", timeMap.get("foo", 5), "bar2");
        // A read between the two writes must still see the older value.
        print("case 1 get(foo, 3) again", timeMap.get("foo", 3), "bar");

        // ---- case 2: edge - nothing to return -----------------------------------
        print("case 2 get(foo, 0) before first write", timeMap.get("foo", 0), "");
        print("case 2 get(missing, 10) unknown key", timeMap.get("missing", 10), "");

        // ---- case 3: tricky - two keys, and an overwrite at the same stamp -------
        TimeBasedKeyValueStore multi = new TimeBasedKeyValueStore();
        multi.set("a", "a1", 1);
        multi.set("b", "b1", 2);
        multi.set("a", "a2", 5);
        // Keys have independent timelines: b's write at 2 never shadows a's write at 1.
        print("case 3 get(a, 4)", multi.get("a", 4), "a1");
        print("case 3 get(b, 4)", multi.get("b", 4), "b1");
        print("case 3 get(b, 1) before b's first write", multi.get("b", 1), "");
        multi.set("a", "a1-fixed", 1);   // same stamp: the TreeMap entry is replaced
        print("case 3 get(a, 1) after overwrite", multi.get("a", 1), "a1-fixed");
        print("case 3 get(a, 9) still newest", multi.get("a", 9), "a2");
    }

    /** Quotes the strings so an empty result is visible rather than blank. */
    private static void print(String label, String actual, String expected) {
        System.out.println(label + ": \"" + actual + "\"   expected \"" + expected + "\"");
    }
}

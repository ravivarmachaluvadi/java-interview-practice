/*
 * =====================================================================
 *  Snapshot Array                                 LeetCode 1146 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Implement an array of the given length, all zeros to start, supporting
 *   set(index, val), snap() which freezes the current contents and returns an
 *   increasing snapshot id, and get(index, snapId) which reads the value that
 *   index held at the time of that snapshot. Up to ~50k of each call.
 *
 * EXAMPLE
 *   new SnapshotArray(3); set(0, 5); snap() -> 0; set(0, 6)
 *   get(0, 0) -> 5    the frozen value, not the current 6
 *   index 1 was never written, so get(1, 0) -> 0
 *
 * DESIGN  (per-index version list plus binary search)
 *   List<Version>[] history   one append-only timeline per index, each Version being
 *                             (snapId, val) meaning "from this snapshot on, the value
 *                             was val". Each list is seeded with (0, 0) for the zeros.
 *   int currentSnapId         the snapshot being written into right now.
 *   set   if the newest version already belongs to the current snapshot, overwrite it
 *         in place; otherwise append a new version. So one snapshot leaves at most one
 *         version per index, no matter how many times it is written.
 *   snap  return currentSnapId, then increment. O(1) - nothing is copied.
 *   get   binary search that index's timeline for the largest snapId <= the query,
 *         which is the version in force at that snapshot.
 *
 * KEY DECISIONS
 *   - Why not copy the whole array on snap()? That is O(length) per snapshot and blows
 *     up to 50k * 50k cells. Recording only what CHANGED makes the cost proportional to
 *     the number of writes, not to snapshots times length.
 *   - Why per index rather than one global change log? A read then has to hunt for the
 *     right index; a timeline per index makes the binary search land directly.
 *   - The seed version (0, 0) is what lets get() skip a null check: the search can
 *     never fall off the left end, because snapshot 0 always has an entry.
 *   - Same "largest stamp <= query" question as TimeBasedKeyValueStore (LC 981). There
 *     it is TreeMap.floorKey; here the lists are append-only, so a plain ArrayList plus
 *     binary search is cheaper and this is the hand-rolled form of floorKey.
 *
 * COMPLEXITY
 *   Time  set O(1) amortised, snap O(1), get O(log v) with v = versions at that index
 *   Space O(length + number of set calls) - one seed version each, plus one per change
 *
 * INTERVIEW FOLLOW-UPS
 *   - Delete a snapshot / cap retention: needs a refcount per snapshot before pruning.
 *   - Range queries inside a snapshot: layer a persistent segment tree instead.
 *   - Writes far outnumber reads - would you still version? Copy-on-write per block is
 *     the usual answer, trading read cost for fewer objects.
 *   - What if snapshot ids could be requested out of order, or reused? The append-only
 *     assumption breaks and each timeline has to become a TreeMap.
 *
 * RUN
 *   main() runs 3 cases: the LeetCode example, edge cases (a never-written index and a
 *   snapshot taken with no writes at all), and a tricky case showing that repeated sets
 *   inside one snapshot collapse to a single stored version. Each line prints actual
 *   vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class SnapshotArray {

    /** One entry in an index's timeline: the value in force from snapId onwards. */
    private static class Version {
        final int snapId;
        int val;

        Version(int snapId, int val) {
            this.snapId = snapId;
            this.val = val;
        }
    }

    private final List<Version>[] history;
    private int currentSnapId;

    @SuppressWarnings("unchecked") // no generic array literals in Java; the casts are safe
    public SnapshotArray(int length) {
        history = (List<Version>[]) new List[length];
        for (int i = 0; i < length; i++) {
            history[i] = new ArrayList<>();
            // Seed every index with 0 at snapshot 0 so get() always finds something.
            history[i].add(new Version(0, 0));
        }
        currentSnapId = 0;
    }

    public void set(int index, int val) {
        List<Version> timeline = history[index];
        Version newest = timeline.get(timeline.size() - 1);
        if (newest.snapId == currentSnapId) {
            // Still inside the same snapshot - no one can observe the old value.
            newest.val = val;
        } else {
            timeline.add(new Version(currentSnapId, val));
        }
    }

    /** Freezes the current state and returns its id. O(1): nothing is copied. */
    public int snap() {
        return currentSnapId++;
    }

    /** Value at index as of snapId: the newest version with version.snapId <= snapId. */
    public int get(int index, int snapId) {
        List<Version> timeline = history[index];
        int lo = 0;
        int hi = timeline.size() - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (timeline.get(mid).snapId <= snapId) {
                lo = mid + 1;  // this one qualifies; look for a later one
            } else {
                hi = mid - 1;
            }
        }
        // hi settles on the last qualifying version, and is never -1 thanks to the
        // seed version at snapshot 0.
        return timeline.get(hi).val;
    }

    /** How many versions this index has actually stored. Demo only. */
    int versionCount(int index) {
        return history[index].size();
    }

    public static void main(String[] args) {
        // ---- case 1: the LeetCode example ---------------------------------------
        SnapshotArray arr = new SnapshotArray(3);
        arr.set(0, 5);
        int snap0 = arr.snap();
        print("case 1 snap()", snap0, 0);
        arr.set(0, 6);                       // lands in snapshot 1, not 0
        print("case 1 get(0, snap0)", arr.get(0, snap0), 5);
        print("case 1 get(1, snap0) never written", arr.get(1, snap0), 0);
        int snap1 = arr.snap();
        print("case 1 snap()", snap1, 1);
        print("case 1 get(0, snap1)", arr.get(0, snap1), 6);
        print("case 1 get(0, snap0) unchanged", arr.get(0, snap0), 5);

        // ---- case 2: edge - empty snapshot and an untouched index ---------------
        SnapshotArray edge = new SnapshotArray(3);
        int e0 = edge.snap();                // snapshot of an all-zero array
        print("case 2 get(2, e0)", edge.get(2, e0), 0);
        edge.set(2, 42);
        int e1 = edge.snap();
        print("case 2 get(2, e0) still historical", edge.get(2, e0), 0);
        print("case 2 get(2, e1)", edge.get(2, e1), 42);
        int e2 = edge.snap();                // no writes between e1 and e2
        print("case 2 get(2, e2) carries forward", edge.get(2, e2), 42);
        print("case 2 versions at index 2", edge.versionCount(2), 2);

        // ---- case 3: tricky - repeated writes inside one snapshot collapse -------
        SnapshotArray collapse = new SnapshotArray(2);
        collapse.set(0, 5);
        collapse.set(0, 6);
        collapse.set(0, 7);                  // three writes, still snapshot 0
        print("case 3 versions after 3 writes", collapse.versionCount(0), 1);
        int c0 = collapse.snap();
        collapse.set(0, 8);
        collapse.set(0, 9);                  // append once, then overwrite in place
        print("case 3 versions after 2 more writes", collapse.versionCount(0), 2);
        print("case 3 get(0, c0)", collapse.get(0, c0), 7);
        int c1 = collapse.snap();
        print("case 3 get(0, c1)", collapse.get(0, c1), 9);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

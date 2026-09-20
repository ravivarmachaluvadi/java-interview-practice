/*
 * =====================================================================
 *  Non-overlapping Intervals                         LeetCode 435 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a list of intervals, return the minimum number you must REMOVE so
 *   that the survivors never overlap. Intervals that merely touch, such as
 *   [1,2] and [2,3], do not count as overlapping. Removing the fewest is the
 *   same as keeping the most, so this is activity selection in disguise.
 *
 * EXAMPLE
 *   [[1,2],[2,3],[3,4],[1,3]]  ->  1   (drop [1,3]; the other three chain cleanly)
 *   [[1,2],[1,2],[1,2]]        ->  2   (keep one copy, remove the other two)
 *   [[1,2]]                    ->  0   (nothing to remove)
 *   [[1,100],[11,22],[1,11],[2,12]] -> 2  (the greedy keeps [1,11] and [11,22])
 *
 * APPROACH  (greedy: sort by END time, keep the earliest finisher)
 *   1. Sort the intervals by their end value, ascending.
 *   2. Track lastEnd, the end of the most recent interval we decided to KEEP.
 *      Start it at Integer.MIN_VALUE so the first interval is always kept.
 *   3. Scan in that order. If interval.start >= lastEnd it does not clash with
 *      anything kept so far: keep it and set lastEnd = interval.end.
 *   4. Otherwise it overlaps the kept set, so count it as a removal and move on -
 *      lastEnd is untouched, because the interval we kept ends no later.
 *   5. The removal count is the answer.
 *
 * KEY INSIGHT
 *   Sort by END, not by start. This is the folder's second axis and the classic
 *   exchange argument: among intervals that clash, the one finishing earliest
 *   leaves the most room for everything after it, so keeping it is never worse
 *   than keeping any rival. Sorting by start is the trap - with [[1,100],[2,3],
 *   [3,4]] it anchors on the 99-wide interval and gets the answer wrong.
 *   Recognise it by the shape "keep the largest non-conflicting subset".
 *
 * COMPLEXITY
 *   Time  O(n log n)  the sort dominates; the single scan is O(n).
 *   Space O(1)        in place, ignoring the sort's own overhead.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Minimum Arrows to Burst Balloons (LC 452) is the same greedy with the
 *     answer reframed as "groups kept" instead of "intervals removed".
 *   - If touching intervals DID overlap, change the test to interval[0] > lastEnd.
 *   - Weighted version (each interval has a value, maximise the kept value):
 *     greedy breaks, it becomes DP with binary search - O(n log n).
 *   - Return the intervals you kept, not just how many you dropped.
 *
 * RUN
 *   main() runs 4 cases (typical, all identical, single interval, nested) and
 *   prints actual vs expected.
 */

import java.util.Arrays;
import java.util.Comparator;

class NonOverlappingIntervals {

    public int eraseOverlapIntervals(int[][] intervals) {
        // Earliest finisher first: that is what makes the greedy optimal.
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[1]));

        int count = 0;                    // intervals removed so far
        int lastEnd = Integer.MIN_VALUE;  // end of the last interval we kept

        for (int[] interval : intervals) {
            if (interval[0] >= lastEnd) {
                // Starts at or after the kept one ends, so no clash: keep it.
                lastEnd = interval[1];
            } else {
                // Clashes with what we already kept, and that one ends earlier,
                // so dropping this interval is never the worse choice.
                count++;
            }
        }
        return count;
    }

    private static void print(String label, int actual, int expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        NonOverlappingIntervals solution = new NonOverlappingIntervals();

        // Case 1: typical - one wide interval must go.
        print("case 1 (typical)",
                solution.eraseOverlapIntervals(new int[][]{{1, 2}, {2, 3}, {3, 4}, {1, 3}}), 1);

        // Case 2: edge - every interval identical, so all but one are removed.
        print("case 2 (all equal)",
                solution.eraseOverlapIntervals(new int[][]{{1, 2}, {1, 2}, {1, 2}}), 2);

        // Case 3: edge - a single interval never overlaps anything.
        print("case 3 (single)",
                solution.eraseOverlapIntervals(new int[][]{{1, 2}}), 0);

        // Case 4: tricky - a nested giant. Sorting by START would keep [1,100]
        // first and report 3; sorting by END keeps [1,11] and [11,22] and reports 2.
        print("case 4 (nested giant)",
                solution.eraseOverlapIntervals(
                        new int[][]{{1, 100}, {11, 22}, {1, 11}, {2, 12}}), 2);
    }
}

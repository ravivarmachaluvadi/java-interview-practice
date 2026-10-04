/*
 * =====================================================================
 *  P049 Intervals: Sort by End, Keep the Most   Canonical LC 435 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 435, Non-overlapping Intervals)
 *   Return the minimum number of intervals to remove so the rest do not overlap.
 *   Intervals that only touch ([1,2] and [2,3]) do not overlap.
 *
 * EXAMPLE
 *   [[1,2],[2,3],[3,4],[1,3]]  ->  1     remove [1,3]
 *   [[1,2],[1,2],[1,2]]        ->  2
 *   [[1,2],[2,3]]              ->  0
 *
 * RECOGNIZE WHEN
 *   - "maximum number of non-overlapping intervals", "minimum to remove", "minimum points /
 *     arrows that hit every interval", "longest chain of pairs" -- classic activity
 *     selection.
 *   Not this if: you want the union of intervals -> P048_MergeIntervals (sort by START); you
 *   need how many overlap at the same time -> P050_SweepLineMinRooms; intervals carry
 *   weights / profits -> P093_WeightedJobScheduling (DP, not greedy).
 *
 * TEMPLATE
 *   sort by END
 *   lastEnd = -infinity, kept = 0
 *   for [s, e] in intervals:
 *       if s >= lastEnd:            // compatible with everything kept (use > for "touching
 *           kept++; lastEnd = e     //  overlaps", e.g. arrows)
 *   removed = n - kept
 *
 * APPROACH
 *   1. Sort by end time.
 *   2. Keep an interval whenever it starts at or after the end of the last kept one.
 *   3. Everything not kept is removed.
 *
 * KEY INSIGHT
 *   Among all intervals you could keep next, the one that ENDS first leaves the most room
 *   for the rest, so choosing it can never be worse (exchange argument). Sorting by start
 *   fails: one long early interval would block many short ones.
 *
 * COMPLEXITY
 *   Time O(n log n), space O(1) besides the sort.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 452  Min Arrows for Balloons  an arrow at the kept interval's END bursts all
 *                                            that start <= it; touching counts as hit
 *   [coded] LC 646  Max Length Pair Chain    count kept pairs; chain needs strict b < c
 *           LC 1288 Remove Covered Intervals sort start asc, end desc (a different greedy)
 *           LC 757  Set Intersection Size 2  sort by end, keep the two largest picked
 *                                            points per interval
 *           Activity selection               identical to counting kept intervals
 *
 * PITFALLS
 *   - Sorting with (a, b) -> a[1] - b[1] overflows on extreme values; use Integer.compare.
 *   - Strict vs non-strict: balloons that touch share an arrow (start <= end); LC 435
 *     intervals that touch are compatible (start >= lastEnd).
 *
 * DEEP DIVE
 *   C06_NonOverlappingIntervals, C07_MinimumArrowsToBurstBalloons (15-Intervals)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;

class SortByEndGreedy {

    // Canonical LC 435.
    static int eraseOverlapIntervals(int[][] intervals) {
        int[][] a = intervals.clone();
        Arrays.sort(a, (x, y) -> Integer.compare(x[1], y[1]));
        long lastEnd = Long.MIN_VALUE;
        int kept = 0;
        for (int[] cur : a) {
            if (cur[0] >= lastEnd) {
                kept++;
                lastEnd = cur[1];
            }
        }
        return a.length - kept;
    }

    // LC 452.
    static int findMinArrowShots(int[][] points) {
        int[][] a = points.clone();
        Arrays.sort(a, (x, y) -> Integer.compare(x[1], y[1]));
        int arrows = 0;
        long arrowAt = Long.MIN_VALUE;
        for (int[] p : a) {
            if (p[0] > arrowAt) {                 // not hit by the last arrow
                arrows++;
                arrowAt = p[1];
            }
        }
        return arrows;
    }

    // LC 646: pair [a, b] can follow [c, d] only if d < a.
    static int findLongestChain(int[][] pairs) {
        int[][] a = pairs.clone();
        Arrays.sort(a, (x, y) -> Integer.compare(x[1], y[1]));
        int chain = 0;
        long lastEnd = Long.MIN_VALUE;
        for (int[] p : a) {
            if (p[0] > lastEnd) {
                chain++;
                lastEnd = p[1];
            }
        }
        return chain;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 435 remove one",
                eraseOverlapIntervals(new int[][]{{1, 2}, {2, 3}, {3, 4}, {1, 3}}), 1);
        check("LC 435 three copies", eraseOverlapIntervals(new int[][]{{1, 2}, {1, 2}, {1, 2}}), 2);
        check("LC 435 touching is fine", eraseOverlapIntervals(new int[][]{{1, 2}, {2, 3}}), 0);
        check("LC 435 one long blocks many",
                eraseOverlapIntervals(new int[][]{{1, 100}, {2, 3}, {4, 5}, {6, 7}}), 1);

        check("LC 452 four balloons",
                findMinArrowShots(new int[][]{{10, 16}, {2, 8}, {1, 6}, {7, 12}}), 2);
        check("LC 452 disjoint", findMinArrowShots(new int[][]{{1, 2}, {3, 4}, {5, 6}, {7, 8}}), 4);
        check("LC 452 touching shares",
                findMinArrowShots(new int[][]{{1, 2}, {2, 3}, {3, 4}, {4, 5}}), 2);
        int[][] extremes = {{-2147483646, -2147483645}, {2147483646, 2147483647}};
        check("LC 452 overflow trap", findMinArrowShots(extremes), 2);

        check("LC 646 [[1,2],[2,3],[3,4]]",
                findLongestChain(new int[][]{{1, 2}, {2, 3}, {3, 4}}), 2);
        check("LC 646 [[1,2],[7,8],[4,5]]",
                findLongestChain(new int[][]{{1, 2}, {7, 8}, {4, 5}}), 3);
    }
}

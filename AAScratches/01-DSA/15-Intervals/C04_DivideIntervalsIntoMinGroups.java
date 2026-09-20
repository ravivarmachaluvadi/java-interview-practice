/*
 * =====================================================================
 *  Divide Intervals Into Minimum Number of Groups   LeetCode 2406 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given intervals [left, right] that are INCLUSIVE on both ends, split them
 *   into the fewest groups such that no two intervals inside one group intersect.
 *   Return that group count. Inclusive matters: [1,2] and [2,3] share the point
 *   2, so they intersect and cannot live in the same group.
 *
 * EXAMPLE
 *   [[5,10],[6,8],[1,5],[2,3],[1,10]]  ->  3   (at time 5: [5,10],[1,5],[1,10])
 *   [[1,2],[2,3],[3,4],[4,5]]          ->  2   (chain touching at endpoints)
 *   [[1,2]]                            ->  1   (single interval)
 *
 * APPROACH  (min groups = peak overlap depth, via a chronological sweep)
 *   1. Copy all left endpoints into start[] and all right endpoints into end[].
 *   2. Sort each array independently - pairing is irrelevant, only the timeline is.
 *   3. Sweep with j over start[] and k over end[], keeping a running count grp.
 *   4. If start[j] <= end[k] an interval opens before the earliest one closes
 *      (<= not <, because the endpoints are inclusive): grp++, update maxGrp, j++.
 *   5. Otherwise the earliest interval has closed: grp--, k++.
 *   6. maxGrp, the deepest the counter ever reached, is the answer.
 *
 * KEY INSIGHT
 *   The whole problem is a renaming. "Fewest groups with no internal overlap"
 *   equals "maximum number of intervals alive at any single instant": you need at
 *   least that many groups because those intervals pairwise intersect, and that
 *   many always suffice. Same count as Meeting Rooms II and Minimum Platforms -
 *   recognise the restatement and the algorithm is already written.
 *
 * COMPLEXITY
 *   Time  O(n log n)  two sorts; the sweep is O(n).
 *   Space O(n)        the two endpoint arrays.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Half-open intervals [left, right) instead? Flip the test to start[j] < end[k].
 *   - Print the actual grouping, not just the count: sort by start and keep a
 *     min-heap of each group's current right endpoint, reusing the smallest.
 *   - Coordinate range is small (<= 1e6)? A +1/-1 difference array over the axis
 *     gives O(n + range) with no sorting.
 *   - Prove the lower bound: why can peak depth never be beaten?
 *
 * RUN
 *   main() runs 3 cases (typical, touching endpoints, single interval) and prints
 *   actual vs expected.
 */

import java.util.Arrays;

class DivideIntervalsIntoMinGroups {

    // Same machinery as Minimum Platforms / Meeting Rooms II.
    public static int minGroups(int[][] intervals) {
        int n = intervals.length;
        int[] start = new int[n];
        int[] end = new int[n];
        for (int i = 0; i < n; i++) {
            start[i] = intervals[i][0];
            end[i] = intervals[i][1];
        }
        Arrays.sort(start);
        Arrays.sort(end);

        int maxGrp = 0, grp = 0;
        int j = 0, k = 0;

        // j runs out first (every start is consumed), so one bound would do.
        while (j < n && k < n) {
            if (start[j] <= end[k]) {
                // Inclusive ends: start == end still counts as an overlap.
                grp++;
                maxGrp = Math.max(grp, maxGrp);
                j++;
            } else {
                // The earliest interval has closed; its group is free again.
                grp--;
                k++;
            }
        }
        return maxGrp;
    }

    private static void print(String label, int actual, int expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1: typical - three intervals are alive at time 5.
        int[][] intervals1 = {{5, 10}, {6, 8}, {1, 5}, {2, 3}, {1, 10}};
        print("case 1 (typical)", minGroups(intervals1), 3);

        // Case 2: tricky - a chain that only touches at endpoints. Because the
        // intervals are INCLUSIVE, [1,2] and [2,3] do intersect, so the answer is
        // 2 (odd ones in one group, even ones in the other), not 1.
        int[][] intervals2 = {{1, 2}, {2, 3}, {3, 4}, {4, 5}};
        print("case 2 (touching endpoints)", minGroups(intervals2), 2);

        // Case 3: edge - a single interval needs exactly one group.
        int[][] intervals3 = {{1, 2}};
        print("case 3 (single interval)", minGroups(intervals3), 1);
    }
}

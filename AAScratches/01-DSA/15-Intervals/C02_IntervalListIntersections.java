/*
 * =====================================================================
 *  Interval List Intersections                        LeetCode 986 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Two lists of closed intervals, each already sorted by start time and each
 *   internally disjoint (no interval in a list overlaps another in the SAME list).
 *   Return every interval that appears in both lists, i.e. their intersection,
 *   also sorted. Closed means [1,3] and [3,5] do intersect, at the single point 3.
 *
 * EXAMPLE
 *   first  = [[0,2],[5,10],[13,23],[24,25]]
 *   second = [[1,5],[8,12],[15,24],[25,26]]
 *      ->   [[1,2],[5,5],[8,10],[15,23],[24,24],[25,25]]
 *   first = [[1,7]], second = []   ->  []           (one list empty)
 *   first = [[1,3]], second = [[4,6]]  ->  []       (no overlap at all)
 *
 * APPROACH  (two pointers over the two sorted lists)
 *   1. i walks firstList, j walks secondList; both start at 0.
 *   2. The candidate overlap of firstList[i] and secondList[j] is
 *        start = max(of the two starts), end = min(of the two ends).
 *   3. If start <= end the candidate is a real interval, so record it.
 *      If start > end the two intervals miss each other, record nothing.
 *   4. Advance the pointer whose interval ENDS first: that interval can never
 *      intersect anything further along in the other list, so it is finished.
 *   5. Stop when either list runs out.
 *
 * KEY INSIGHT
 *   Intersection is max-of-starts, min-of-ends, valid only while start <= end.
 *   That is the mirror image of merging, which is min-of-starts, max-of-ends.
 *   The pointer move is the part worth reciting: discard the smaller END, never
 *   the smaller start, because the interval ending first is the one that cannot
 *   reach any later interval. Each step retires one interval, so it is linear.
 *
 * COMPLEXITY
 *   Time  O(m + n)  every loop turn advances exactly one of the two pointers.
 *   Space O(1)      beyond the output list itself.
 *
 * INTERVIEW FOLLOW-UPS
 *   - What if a list may contain overlapping intervals? Merge it first (LC 56).
 *   - What if the inputs are not sorted? Sort by start: O(m log m + n log n).
 *   - Intersect K lists instead of 2? Fold pairwise, or sweep all starts/ends.
 *   - Return the UNION of the two lists instead - how does the rule change?
 *
 * RUN
 *   main() runs 3 cases (typical, empty list, disjoint lists) and prints
 *   actual vs expected.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class IntervalListIntersections {

    public int[][] intervalIntersection(int[][] firstList, int[][] secondList) {
        List<int[]> ans = new ArrayList<>();
        int i = 0, j = 0;

        while (i < firstList.length && j < secondList.length) {
            // The overlap of two intervals starts at the later start ...
            int start = Math.max(firstList[i][0], secondList[j][0]);
            // ... and finishes at the earlier end.
            int end = Math.min(firstList[i][1], secondList[j][1]);

            // start > end means the two intervals do not actually touch.
            if (start <= end) {
                ans.add(new int[]{start, end});
            }

            // Retire the interval that ends first: nothing later can reach it.
            if (firstList[i][1] < secondList[j][1]) {
                i++;
            } else {
                j++;
            }
        }
        return ans.toArray(new int[ans.size()][]);
    }

    private static void print(String label, int[][] actual, String expected) {
        System.out.println(label + ": " + Arrays.deepToString(actual) + "   expected " + expected);
    }

    public static void main(String[] args) {
        IntervalListIntersections solution = new IntervalListIntersections();

        // Case 1: typical - several overlaps, including single-point ones.
        int[][] first1 = {{0, 2}, {5, 10}, {13, 23}, {24, 25}};
        int[][] second1 = {{1, 5}, {8, 12}, {15, 24}, {25, 26}};
        print("case 1 (typical)", solution.intervalIntersection(first1, second1),
                "[[1, 2], [5, 5], [8, 10], [15, 23], [24, 24], [25, 25]]");

        // Case 2: edge - one list is empty, so the loop never runs.
        int[][] first2 = {{1, 7}};
        int[][] second2 = {};
        print("case 2 (empty list)", solution.intervalIntersection(first2, second2), "[]");

        // Case 3: tricky - lists never overlap, and [3,4] touching [4,6]
        //         would be an intersection, so keep them clearly apart.
        int[][] first3 = {{1, 3}, {7, 9}};
        int[][] second3 = {{4, 6}, {10, 12}};
        print("case 3 (disjoint)", solution.intervalIntersection(first3, second3), "[]");
    }
}

/*
 * =====================================================================
 *  Insert Interval                              LeetCode 57 | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   intervals is already sorted by start and contains no overlaps. Insert
 *   newInterval into it, merging anything it touches, and return the result still
 *   sorted and still overlap-free. Because the input is pre-sorted, this must be
 *   done in O(n) - sorting again would be the wrong answer to the question.
 *
 * EXAMPLE
 *   [[1,3],[6,9]], new [2,5]                  ->  [[1,5],[6,9]]
 *   [[1,2],[3,5],[6,7],[8,10],[12,16]], [4,8] ->  [[1,2],[3,10],[12,16]]
 *   [], new [5,7]                             ->  [[5,7]]        empty list
 *   [[3,5]], new [1,2]                        ->  [[1,2],[3,5]]  lands before all
 *
 * APPROACH  (three-phase scan: before, overlapping, after)
 *   1. BEFORE: copy every interval that ends strictly before newInterval starts.
 *      Those can never be affected.
 *   2. OVERLAP: while the next interval starts at or before the new interval's
 *      end, absorb it - stretch start to the min and end to the max. Then append
 *      the single stretched interval once.
 *   3. AFTER: copy everything that is left; each of those starts after the merged
 *      block ends.
 *
 * KEY INSIGHT
 *   Sortedness means the intervals touching the new one form ONE contiguous run,
 *   so the list splits cleanly into three phases and each element is visited once.
 *   The merge itself is the same fold as Merge Intervals (A02_MergeIntervals); the only new idea
 *   is that the accumulator is the new interval rather than the last output. The
 *   overlap test is intervals[i].start <= newEnd and the "strictly before" test is
 *   intervals[i].end < newStart, so touching endpoints merge, matching LeetCode.
 *
 * COMPLEXITY
 *   Time  O(n)  each interval is examined by exactly one of the three loops
 *   Space O(n)  the output list; O(1) beyond what the answer itself needs
 *
 * INTERVIEW FOLLOW-UPS
 *   - The input is NOT sorted: that is Merge Intervals (A02_MergeIntervals), O(n log n).
 *   - Find the insertion point with binary search: still O(n) because the merged
 *     run can be long, but it helps when few intervals overlap.
 *   - Remove an interval (LeetCode 1272) instead of inserting one.
 *   - Insert many intervals one after another: sort them and merge in one pass.
 *
 * FIXES APPLIED
 *   Fixed: insert() wrote the merged bounds back into the caller's newInterval
 *          array. It now merges into local variables and leaves the input alone.
 *
 * RUN
 *   main() runs 5 cases (typical, long merged run, empty list, insert before all,
 *   insert after all) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class InsertInterval {

    public static int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int n = intervals.length;
        int i = 0;

        // Local copies so the caller's newInterval array is never modified.
        int newStart = newInterval[0];
        int newEnd = newInterval[1];

        // Phase 1 - everything that ends before the new interval begins.
        while (i < n && intervals[i][1] < newStart) {
            result.add(intervals[i]);
            i++;
        }

        // Phase 2 - the contiguous run that touches the new interval. Sortedness
        // guarantees this run is unbroken, so one while loop absorbs all of it.
        // [[1,2],[3,5],[6,7],[8,10],[12,16]] with [4,8] absorbs [3,5],[6,7],[8,10].
        while (i < n && intervals[i][0] <= newEnd) {
            newStart = Math.min(newStart, intervals[i][0]);
            newEnd = Math.max(newEnd, intervals[i][1]);
            i++;
        }
        result.add(new int[]{newStart, newEnd}); // the single merged interval

        // Phase 3 - everything that starts after the merged block ends.
        while (i < n) {
            result.add(intervals[i]);
            i++;
        }

        return result.toArray(new int[result.size()][]);
    }

    private static void print(String label, int[][] actual, String expected) {
        System.out.println(label + ": " + Arrays.deepToString(actual) + "   expected " + expected);
    }

    public static void main(String[] args) {
        // typical: the new interval swallows part of the first interval
        print("case 1", insert(new int[][]{{1, 3}, {6, 9}}, new int[]{2, 5}), "[[1, 5], [6, 9]]");

        // tricky: a long run of three intervals collapses into one
        int[][] intervals2 = {{1, 2}, {3, 5}, {6, 7}, {8, 10}, {12, 16}};
        int[] new2 = {4, 8};
        print("case 2", insert(intervals2, new2), "[[1, 2], [3, 10], [12, 16]]");
        print("case 2 input unchanged", new int[][]{new2}, "[[4, 8]]");

        // edge: empty interval list
        print("case 3", insert(new int[][]{}, new int[]{5, 7}), "[[5, 7]]");

        // edge: the new interval sits entirely before everything
        print("case 4", insert(new int[][]{{3, 5}}, new int[]{1, 2}), "[[1, 2], [3, 5]]");

        // edge: the new interval sits entirely after everything, touching the end
        print("case 5", insert(new int[][]{{1, 2}, {3, 5}}, new int[]{5, 8}), "[[1, 2], [3, 8]]");
    }
}

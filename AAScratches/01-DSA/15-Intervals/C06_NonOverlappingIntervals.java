import java.util.*;

// https://leetcode.com/problems/non-overlapping-intervals/description/
// 435. Non-overlapping Intervals

/**
 * Input: intervals = [[1,2],[2,3],[3,4],[1,3]]
 * Output: 1
 * Explanation: [1,3] can be removed and the rest of the intervals are non-overlapping.
 */
class NonOverlappingIntervals {
    public int eraseOverlapIntervals(int[][] intervals) {
        // Sort intervals by their end times
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[1]));

        // Counter for overlapping intervals to remove
        int count = 0;
        // Tracks the end of the last non-overlapping interval
        int lastEnd = Integer.MIN_VALUE;

        for (int[] interval : intervals) {
            if (interval[0] >= lastEnd) {
                // No overlap, update the end
                lastEnd = interval[1];
            } else {
                // Overlap found, increment the removal count
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        NonOverlappingIntervals solution = new NonOverlappingIntervals();

        int[][] intervals = {
                {1, 2},
                {2, 3},
                {3, 4},
                {1, 3}
        };

        int result = solution.eraseOverlapIntervals(intervals);

        System.out.println("Number of intervals to remove: " + result);
    }
}

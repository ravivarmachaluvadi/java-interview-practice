import java.util.Arrays;
import java.util.PriorityQueue;

// https://leetcode.com/problems/divide-intervals-into-minimum-number-of-groups/description/
// 2406. Divide Intervals Into Minimum Number of Groups
public class DivideIntervalsIntoMinGroups {
    // variation minimum platforms required
    public static int minGroups(int[][] intervals) {
        int start[] = new int[intervals.length];
        int end[] = new int[intervals.length];
        for (int i = 0; i < intervals.length; i++) {
            start[i] = intervals[i][0];
            end[i] = intervals[i][1];
        }
        Arrays.sort(start);
        Arrays.sort(end);
        int maxGrp = 0, grp = 0;
        int j = 0, k = 0;

        while (j < intervals.length && k < intervals.length) {
            if (start[j] <= end[k]) {
                grp++;
                maxGrp = Math.max(grp, maxGrp);
                j++;
            } else {
                grp--;
                k++;
            }
        }
        return maxGrp;

    }

    public static void main(String[] args) {
        int[][] intervals1 = {
                {5, 10},
                {6, 8},
                {1, 5},
                {2, 3},
                {1, 10}
        };
        System.out.println("Example 1 → expected: 3, actual: " + minGroups(intervals1));

        int[][] intervals2 = {
                {1, 2},
                {2, 3},
                {3, 4},
                {4, 5}
        };
        System.out.println("Example 2 → expected: 1, actual: " + minGroups(intervals2));
    }
}

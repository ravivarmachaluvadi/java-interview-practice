import java.util.ArrayList;
import java.util.List;

// 986. Interval List Intersections
// https://leetcode.com/problems/interval-list-intersections/description/
class IntervalListIntersections {
    public int[][] intervalIntersection(int[][] firstList, int[][] secondList) {
        List<int[]> ans = new ArrayList<>();
        int i = 0, j = 0;

        while (i < firstList.length && j < secondList.length) {
            // max of starts of two intervals
            int start = Math.max(firstList[i][0], secondList[j][0]);
            // min of ends of two intervals
            int end = Math.min(firstList[i][1], secondList[j][1]);

            if (start <= end) ans.add(new int[]{start, end});
            // comparing ends and leaving min end move further
            if (firstList[i][1] < secondList[j][1]) i++;
            else j++;
        }
        return ans.toArray(new int[ans.size()][]);
    }

    public static void main(String[] args) {
        IntervalListIntersections solution = new IntervalListIntersections();
        int[][] firstList = {{0, 2}, {5, 10}, {13, 23}, {24, 25}};
        int[][] secondList = {{1, 5}, {8, 12}, {15, 24}, {25, 26}};
        int[][] result = solution.intervalIntersection(firstList, secondList);

        System.out.println("Interval Intersections:");
        for (int[] interval : result) {
            System.out.println("[" + interval[0] + ", " + interval[1] + "]");
        }
        //[1, 2]
        //[5, 5]
        //[8, 10]
        //[15, 23]
        //[24, 24]
        //[25, 25]
    }
}

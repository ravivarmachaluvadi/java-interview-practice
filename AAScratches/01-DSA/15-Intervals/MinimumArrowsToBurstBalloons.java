import java.util.*;

/**
 * Input: points = [[1,2],[3,4],[5,6],[7,8]]
 * <p>
 * Output: 4
 */
// https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/description/
// 452. Minimum Number of Arrows to Burst Balloons
class MinimumArrowsToBurstBalloons {

    public static int findMinArrowShots(int[][] points) {
        if (points == null || points.length == 0) return 0;
        // Sort intervals by their end point
        Arrays.sort(points, Comparator.comparingInt(a -> a[1]));
        int arrows = 1; // At least one arrow is needed
        int end = points[0][1];

        for (int i = 1; i < points.length; i++) {
// If the current balloon starts after the last arrow's end point, shoot another arrow
            if (points[i][0] > end) {
                arrows++;
                end = points[i][1];
            }
        }
        return arrows;
    }

    public static void main(String[] args) {
        int[][] points = {{10, 16}, {2, 8}, {1, 6}, {7, 12}};
        System.out.println("Input balloons: " + Arrays.deepToString(points));
        int result = findMinArrowShots(points);
        System.out.println("Minimum arrows needed: " + result);
    }
}

import java.util.*;

/**
 * Given a triangle array, return the minimum path sum from top to bottom.
 * <p>
 * For each step, you may move to an adjacent number of the row below. More formally,
 * <p>
 * if you are on index i on the current row, you may move to either index i or index i + 1 on the next row.
 * <p>
 * <p>
 * <p>
 * Example 1:
 * <p>
 * Input: triangle = [[2],[3,4],[6,5,7],[4,1,8,3]]
 * <p>
 * Output: 11
 * <p>
 * Explanation: The triangle looks like:
 * <p>
 * 2
 * <p>
 * 3 4
 * <p>
 * 6 5 7
 * <p>
 * 4 1 8 3
 * <p>
 * The minimum path sum from top to bottom is 2 + 3 + 5 + 1 = 11 (underlined above).
 */
class Triangle {

    public static int minimumTotal(List<List<Integer>> triangle) {
        int n = triangle.size();
        // Create a DP array initialized with the last row of the triangle
        int[] dp = new int[n];
        for (int i = 0; i < n; i++) {
            // single row solution , minimum of dp array is answer if single row
            dp[i] = triangle.get(n - 1).get(i);
        }

        // Start from the second last row and work upwards
        for (int row = n - 2; row >= 0; row--) {
            for (int col = 0; col <= row; col++) {
                // updating dp row using triangle and dp (which contains prev row answer)
                // here dp[col+1] like prev row and next column value
                dp[col] = triangle.get(row).get(col) + Math.min(dp[col], dp[col + 1]);
            }
        }

        // The minimum path sum is stor ed at dp[0]
        return dp[0];
    }

    public static void main(String[] args) {
        List<List<Integer>> triangle = new ArrayList<>();
        triangle.add(List.of(2));
        triangle.add(Arrays.asList(3, 4));
        triangle.add(Arrays.asList(6, 5, 7));
        triangle.add(Arrays.asList(4, 1, 8, 3));

        System.out.println("Minimum total path sum: " + minimumTotal(triangle));
    }
}

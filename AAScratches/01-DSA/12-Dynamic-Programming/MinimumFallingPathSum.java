/**
 * Given a 2d array called matrix consisting of integer values.
 * <p>Return the minimum path sum that can be obtained by starting
 * <p>at any cell in the first row and ending at any cell in the last row.
 * <p>Movement is allowed only to the
 * <p>
 * bottom,
 * <p>
 * bottom-right or
 * <p>
 * bottom-left
 * <p>
 * cell of the current cell.
 */
class MinimumFallingPathSum {

    public int minFallingPathSum(int[][] matrix) {
        int n = matrix.length, m = matrix[0].length;

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < m; j++) {
                // we have 3 opportunities with edge cases extracting these 3 values
                // and pick minVal possible and sum up with mat[i][j] update same at i,j
                int up = matrix[i - 1][j];
                int left = j > 0 ? matrix[i - 1][j - 1] : Integer.MAX_VALUE;
                int right = j < m - 1 ? matrix[i - 1][j + 1] : Integer.MAX_VALUE;
                // for current row update sum for all
                // possible columns with minimum sum
                matrix[i][j] += Math.min(up, Math.min(left, right));
            }
        }

        // Find min value in the last row
        int ans = Integer.MAX_VALUE;
        for (int val : matrix[n - 1]) {
            ans = Math.min(ans, val);
        }
        return ans;
    }

    public static void main(String[] args) {
        int[][] matrix = {
                {1, 2, 10, 4},
                {100, 3, 2, 1},
                {1, 1, 20, 2},
                {1, 2, 2, 1}
        };

        MinimumFallingPathSum sol = new MinimumFallingPathSum();
        System.out.println(sol.minFallingPathSum(matrix)); // Output: 6
    }
}

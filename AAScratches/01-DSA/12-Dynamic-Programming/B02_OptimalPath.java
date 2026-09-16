/**
 * This solution implements a dynamic programming approach to solve the "optimal path" problem,
 * which aims to find the maximum number of rocks collected by following a path starting
 * from the bottom-left corner and moving either right or up to reach the top-right corner.
 */
class OptimalPath {

    public static int optimalPath(int[][] grid) {
        int rowCount = grid.length;
        int colCount = rowCount > 0 ? grid[0].length : 0;

        if (rowCount == 0 || colCount == 0) {
            return 0; // Handle empty grid case
        }

        // Traverse the grid from bottom-left to top-right
        for (int row = rowCount - 1; row >= 0; row--) {
            for (int col = 0; col < colCount; col++) {
                if (row < rowCount - 1 && col > 0) {
                    grid[row][col] += Math.max(grid[row + 1][col], grid[row][col - 1]);
                }
                // If we can only move up
                else if (row < rowCount - 1) {
                    grid[row][col] += grid[row + 1][col];
                }
                // If we can only move right
                else if (col > 0) {
                    grid[row][col] += grid[row][col - 1];
                }
            }
        }

        return grid[0][colCount - 1]; // The top-right corner has the result
    }

    public static boolean doTestsPass() {
        boolean result = true;

        // Base test case
        result &= optimalPath(new int[][]{
                {0, 0, 0, 0, 5},
                {0, 1, 1, 1, 0},
                {2, 0, 0, 0, 0}
        }) == 10;

        // Random numbers
        result &= optimalPath(new int[][]{
                {1, 3, 2, 0, 2, 1, 8},
                {3, 4, 1, 2, 0, 1, 1},
                {1, 1, 1, 2, 3, 2, 1},
                {1, 0, 1, 1, 4, 2, 1}
        }) == 25;

        // All 0's
        result &= optimalPath(new int[][]{
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        }) == 0;

        // Many optimal paths
        result &= optimalPath(new int[][]{
                {1, 1, 1, 1, 1},
                {1, 0, 1, 0, 1},
                {1, 0, 1, 0, 1},
                {1, 1, 1, 1, 1}
        }) == 8;

        // Empty grid
        result &= optimalPath(new int[][]{{}}) == 0;

        return result;
    }

    public int findMaxRocks(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;

        // Create a DP table with the same dimensions as the grid
        int[][] dp = new int[m][n];

        // Initialize the starting point
        dp[m - 1][0] = grid[m - 1][0];

        // Fill the last row (can only move right)
        for (int j = 1; j < n; j++) {
            dp[m - 1][j] = grid[m - 1][j] + dp[m - 1][j - 1];
        }
        // Fill the last column (can only move up)
        for (int i = m - 2; i >= 0; i--) {
            dp[i][n - 1] = grid[i][n - 1] + dp[i + 1][n - 1];
        }

        // Fill the rest of the DP table
        for (int i = m - 2; i >= 0; i--) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = grid[i][j] + Math.max(dp[i + 1][j], dp[i][j - 1]);
            }
        }

        // Return the value at the top-right corner
        return dp[0][n - 1];
    }

    public static void main(String[] args) {
        if (doTestsPass()) {
            System.out.println("All tests pass");
        } else {
            System.out.println("Tests fail.");
        }
    }
}

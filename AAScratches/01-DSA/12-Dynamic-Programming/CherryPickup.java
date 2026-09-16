import java.util.Arrays;

//https://leetcode.com/problems/cherry-pickup/description/
// 741. Cherry Pickup
class CherryPickup {
    public static int cherryPickup(int[][] grid) {
        int n = grid.length;
        // dp[k][i1][i2] = maximum cherries collected when both persons have taken k steps,
        // person1 is at (i1, j1=k-i1), person2 at (i2, j2=k-i2)
        // k ranges from 0 to 2*(n-1)
        int maxK = 2 * (n - 1);
        // Initialize dp to very negative values for invalid/uncomputed states
        int[][][] dp = new int[maxK + 1][n][n];
        for (int k = 0; k <= maxK; k++) {
            for (int i1 = 0; i1 < n; i1++) {
                Arrays.fill(dp[k][i1], Integer.MIN_VALUE);
            }
        }
        // Starting state: k=0, both at (0,0)
        if (grid[0][0] == -1) return 0;
        dp[0][0][0] = grid[0][0];

        for (int k = 1; k <= maxK; k++) {
            for (int i1 = 0; i1 < n; i1++) {
                int j1 = k - i1;
                if (j1 < 0 || j1 >= n) continue;
                if (grid[i1][j1] == -1) continue;
                for (int i2 = 0; i2 < n; i2++) {
                    int j2 = k - i2;
                    if (j2 < 0 || j2 >= n) continue;
                    if (grid[i2][j2] == -1) continue;
                    // we are at valid positions (i1,j1) and (i2,j2)
                    int cherries = grid[i1][j1];
                    if (i1 != i2 || j1 != j2) {
                        cherries += grid[i2][j2];
                    }
                    // transitions: both persons could have come from left or up
                    int bestPrev = Integer.MIN_VALUE;
                    // four possible previous combinations:
                    // (i1-1, i2-1), (i1-1, i2), (i1, i2-1), (i1, i2)
                    if (i1 > 0 && i2 > 0) bestPrev = Math.max(bestPrev, dp[k - 1][i1 - 1][i2 - 1]);
                    if (i1 > 0 && j2 > 0) bestPrev = Math.max(bestPrev, dp[k - 1][i1 - 1][i2]);
                    if (j1 > 0 && i2 > 0) bestPrev = Math.max(bestPrev, dp[k - 1][i1][i2 - 1]);
                    if (j1 > 0 && j2 > 0) bestPrev = Math.max(bestPrev, dp[k - 1][i1][i2]);

                    if (bestPrev != Integer.MIN_VALUE) {
                        dp[k][i1][i2] = bestPrev + cherries;
                    }
                }
            }
        }

        int result = dp[maxK][n - 1][n - 1];
        return Math.max(0, result);
    }

    public static void main(String[] args) {
        // Example 1:
        int[][] grid1 = {
                {0, 1, -1},
                {1, 0, -1},
                {1, 1, 1}
        };
        System.out.println("Output (Example1) = " + cherryPickup(grid1));
        // Expected output: 5

        // Example 2:
        int[][] grid2 = {
                {1, 1, -1},
                {1, -1, 1},
                {-1, 1, 1}
        };
        System.out.println("Output (Example2) = " + cherryPickup(grid2));
        // Expected output: 0
    }
}

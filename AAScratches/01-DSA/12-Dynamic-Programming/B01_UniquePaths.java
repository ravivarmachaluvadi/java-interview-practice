/*
 * =====================================================================
 *  Unique Paths                             LeetCode 62 | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   A robot starts at the top-left cell of an m x n grid and must reach the bottom-right
 *   cell. It may only move one step right or one step down. Return how many distinct paths
 *   exist. m and n are at least 1, and the answer always fits in a 32-bit int for the
 *   LeetCode constraints (m, n <= 100).
 *
 * EXAMPLE
 *   m = 3, n = 2  ->  3   RDD, DDR, DRD
 *   m = 3, n = 7  ->  28
 *   m = 1, n = 1  ->  1   edge case: already at the destination, the empty path counts
 *   m = 1, n = 10 ->  1   edge case: a single row leaves no choice at all
 *
 * APPROACH  (grid counting DP)
 *   1. dp[i][j] = number of distinct paths from the start to cell (i, j).
 *   2. Row 0 and column 0 are all 1: with only right moves, or only down moves, there is
 *      exactly one way to reach any cell on the edge.
 *   3. Every other cell is entered either from directly above or from directly left, and
 *      those two path sets are disjoint, so dp[i][j] = dp[i-1][j] + dp[i][j-1].
 *   4. Fill row by row; the answer is dp[m-1][n-1].
 *   uniquePathsOneRow() is the same recurrence with a single array: while scanning left to
 *   right, dp[j] still holds the value from the row above and dp[j-1] already holds this
 *   row's left neighbour, so "dp[j] += dp[j-1]" is exactly the 2-D transition.
 *
 *   3 x 3 table:
 *   |   | 0 | 1 | 2 |
 *   | 0 | 1 | 1 | 1 |
 *   | 1 | 1 | 2 | 3 |
 *   | 2 | 1 | 3 | 6 |
 *
 * KEY INSIGHT
 *   Count paths by their LAST move. Every path into a cell ends with either a down step or a
 *   right step, and no path can end with both, so the counts simply add. Recognise this
 *   "value of a cell = sum (or min/max) of the cells it can be entered from" shape - it is
 *   the base that Minimum Path Sum, Falling Path Sum and Unique Paths II all reuse.
 *
 * COMPLEXITY
 *   Time  O(m * n)   every cell is computed once, in O(1)
 *   Space O(m * n)   the table; the one-row version drops it to O(n)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Unique Paths II (LeetCode 63): obstacles set dp[i][j] = 0 instead of the sum.
 *   - Minimum Path Sum (LeetCode 64): same grid, replace the sum with cost + min of the two.
 *   - Closed form: the path is a fixed sequence of (m-1) downs and (n-1) rights, so the
 *     answer is C(m+n-2, m-1) - O(m+n) time, O(1) space, but watch for overflow.
 *   - Allow diagonal moves, or count paths in a 3-D grid.
 *
 * RUN
 *   main() runs 4 cases (typical, larger typical, 1 x 1, single row) and prints both the
 *   2-D table and the one-row version against the expected count.
 */

import java.util.Arrays;

class UniquePaths {

    /** Classic 2-D table: dp[i][j] = number of paths from (0, 0) to (i, j). */
    public static int uniquePaths(int m, int n) {
        if (m <= 0 || n <= 0) return 0;

        int[][] dp = new int[m][n];

        // First column: only down moves get you there, so exactly one path each.
        for (int i = 0; i < m; i++) {
            dp[i][0] = 1;
        }
        // First row: only right moves get you there, so exactly one path each.
        for (int j = 0; j < n; j++) {
            dp[0][j] = 1;
        }

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                // a path arrives from above or from the left, and never from both
                dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
            }
        }

        return dp[m - 1][n - 1];
    }

    /** Same recurrence held in one row: dp[j] is the row above until it is overwritten. */
    public static int uniquePathsOneRow(int m, int n) {
        if (m <= 0 || n <= 0) return 0;

        int[] dp = new int[n];
        Arrays.fill(dp, 1);                 // row 0 of the table

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[j] += dp[j - 1];         // (old dp[j] = above) + (new dp[j-1] = left)
            }
        }
        return dp[n - 1];
    }

    // -------------------------------------------------------------------------------------
    private static void print(String label, int actual, int expected) {
        System.out.println(label + " -> " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] cases = {{3, 2}, {3, 7}, {1, 1}, {1, 10}};
        int[] expected = {3, 28, 1, 1};

        for (int c = 0; c < cases.length; c++) {
            int m = cases[c][0], n = cases[c][1];
            System.out.println("case " + (c + 1) + ": m = " + m + ", n = " + n);
            print("  uniquePaths (2-D table)", uniquePaths(m, n), expected[c]);
            print("  uniquePaths (one row)  ", uniquePathsOneRow(m, n), expected[c]);
        }
    }
}

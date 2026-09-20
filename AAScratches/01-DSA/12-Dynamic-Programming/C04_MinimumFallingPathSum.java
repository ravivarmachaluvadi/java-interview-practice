/*
 * =====================================================================
 *  Minimum Falling Path Sum                        LeetCode 931 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a matrix of integers, start at any cell of the first row and fall to the
 *   last row. From cell (i, j) the next step must land on (i+1, j-1), (i+1, j) or
 *   (i+1, j+1). Return the smallest total of the visited cells. Values may be
 *   negative, so a "just take the small cell" greedy walk is wrong.
 *
 * EXAMPLE
 *   [[2, 1, 3],
 *    [6, 5, 4],       -> 13   path 1 -> 4 -> 8
 *    [7, 8, 9]]
 *
 *   [[-19, 57],
 *    [-40, -5]]       -> -59  path -19 -> -40
 *
 *   [[5]]             ->   5  single cell, first row is also the last row
 *
 * APPROACH  (grid DP, three predecessors per cell)
 *   1. dp[i][j] = cheapest cost of any path that starts in row 0 and ends at (i, j).
 *   2. Row 0 is already the answer for itself, so start from row 1.
 *   3. A cell (i, j) can only be reached from three cells in the row above:
 *        up    = dp[i-1][j] left  = dp[i-1][j-1]   (does not exist when j == 0)
 *        right = dp[i-1][j+1]   (does not exist when j == m-1)
 *      Missing neighbours are treated as Integer.MAX_VALUE so min() ignores them;
 *      "up" always exists, so a sentinel can never be the winner and never gets added.
 *   4. dp[i][j] = matrix[i][j] + min(up, left, right), written into the table row by row.
 *   5. The answer is the minimum over the whole last row, not a fixed corner - the
 *      path may end anywhere.
 *
 * KEY INSIGHT
 *   This is the first grid problem where a cell has three predecessors instead of
 *   two and the answer is a min over an entire edge of the grid. Once you see a
 *   move set, the recurrence writes itself: cost(cell) + best over the cells that
 *   can reach it. Only the "which neighbours exist" bookkeeping changes.
 *
 * COMPLEXITY
 *   Time  O(n * m)   each cell is filled once from three neighbours
 *   Space O(n * m)   for the working copy; O(m) if you keep only the previous row
 *
 * INTERVIEW FOLLOW-UPS
 *   - Falling Path Sum II (LeetCode 1289): any column except the same one may be
 *     chosen, so the naive O(n*m*m) needs the two smallest values of each row.
 *   - Keep only one row of the table for O(m) space.
 *   - Reconstruct the actual path, not just its cost.
 *   - Maximum falling path: flip min to max; nothing else changes.
 *
 * RUN
 *   main() runs 4 cases (4x4 typical, 3x3 typical, all-negative, single cell) and
 *   prints actual vs expected.
 *
 * Fixed: the original accumulated into the caller's matrix, so a second call on
 *        the same array returned garbage. It now fills a copy.
 */

class MinimumFallingPathSum {

    public static int minFallingPathSum(int[][] matrix) {
        int n = matrix.length, m = matrix[0].length;

        // work on a copy so the caller's matrix is not overwritten
        int[][] dp = new int[n][];
        for (int i = 0; i < n; i++) {
            dp[i] = matrix[i].clone();
        }

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < m; j++) {
                // the three cells of the previous row that can fall into (i, j);
                // MAX_VALUE marks a neighbour that is off the grid
                int up = dp[i - 1][j];
                int left = j > 0 ? dp[i - 1][j - 1] : Integer.MAX_VALUE;
                int right = j < m - 1 ? dp[i - 1][j + 1] : Integer.MAX_VALUE;

                dp[i][j] += Math.min(up, Math.min(left, right));
            }
        }

        // the path may finish in any column of the last row
        int ans = Integer.MAX_VALUE;
        for (int val : dp[n - 1]) {
            ans = Math.min(ans, val);
        }
        return ans;
    }

    private static void print(String label, int[][] matrix, int expected) {
        System.out.println(label + " -> " + minFallingPathSum(matrix)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (4x4):", new int[][]{
                {1, 2, 10, 4},
                {100, 3, 2, 1},
                {1, 1, 20, 2},
                {1, 2, 2, 1}}, 6);

        print("case 2 (3x3):", new int[][]{
                {2, 1, 3},
                {6, 5, 4},
                {7, 8, 9}}, 13);

        print("case 3 (negatives):", new int[][]{
                {-19, 57},
                {-40, -5}}, -59);

        print("case 4 (single cell):", new int[][]{{5}}, 5);
    }
}

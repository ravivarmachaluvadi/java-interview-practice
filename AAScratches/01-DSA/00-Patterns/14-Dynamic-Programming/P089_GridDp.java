/*
 * =====================================================================
 *  P089 Grid DP (Paths Moving Right / Down)   Canonical LC 62 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 62, Unique Paths)
 *   A robot at the top-left of an m x n grid moves only right or down. How many paths
 *   reach the bottom-right corner?
 *
 * EXAMPLE
 *   m = 3, n = 7  ->  28
 *   m = 3, n = 2  ->  3
 *
 * RECOGNIZE WHEN
 *   - A grid (or triangle) where moves only go one way (right / down / to the next row), and
 *     you count paths or optimise a path sum.
 *   - Squares / rectangles of 1s built from neighbouring cells.
 *   Not this if: moves can go in all 4 directions -> BFS / Dijkstra (P067_BfsShortestPath,
 *   P072_Dijkstra); you must list the paths -> P080_GridPathBacktracking.
 *
 * TEMPLATE
 *   dp[r][c] = answer for paths ending at (r, c)
 *   first row / column: only one way to arrive
 *   dp[r][c] = combine(dp[r-1][c], dp[r][c-1]) (+ cell cost)     // count: +, cost: min
 *   one row is enough: dp[c] = combine(dp[c] (from above), dp[c-1] (from the left))
 *
 * APPROACH
 *   1. The number of ways to reach a cell is the ways to reach the cell above plus the
 *      cell to the left.
 *   2. Roll a single row: dp[c] += dp[c - 1].
 *
 * KEY INSIGHT
 *   Because moves only go right or down, a cell's answer depends only on cells already
 *   computed in row-major order, so the grid fills in one pass. A single row suffices:
 *   before the update dp[c] still holds "from above". (LC 62 also equals C(m+n-2, m-1).)
 *
 * COMPLEXITY
 *   Time O(m * n), space O(n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 63   Unique Paths II          obstacles set dp to 0
 *   [coded] LC 64   Minimum Path Sum         min of the two neighbours + the cell
 *   [coded] LC 120  Triangle                 bottom-up: row[c] = val + min(below, below-right)
 *   [coded] LC 221  Maximal Square           side = 1 + min(up, left, up-left) on '1' cells
 *           LC 931  Min Falling Path Sum     three cells from the row above
 *           LC 174  Dungeon Game             fill from the BOTTOM-RIGHT: health needed
 *           LC 1277 Count Square Submatrices sum of LC 221's side lengths
 *           LC 1463 Cherry Pickup II         two robots at once: dp over (row, c1, c2)
 *
 * PITFALLS
 *   - LC 63: an obstacle in the first row blocks everything to its right.
 *   - LC 120 top-down needs edge handling; bottom-up avoids it.
 *   - LC 221 returns the AREA (side * side), not the side.
 *
 * DEEP DIVE
 *   B01_UniquePaths, B02_OptimalPath, C05_Triangle, C04_MinimumFallingPathSum,
 *   C06_CountSquareSubmatricesWithAllOnes (12-Dynamic-Programming)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.List;

class GridDp {

    // Canonical LC 62.
    static int uniquePaths(int m, int n) {
        int[] dp = new int[n];
        java.util.Arrays.fill(dp, 1);              // first row: one way to each cell
        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) {
                dp[c] += dp[c - 1];                // from above (old dp[c]) + from the left
            }
        }
        return dp[n - 1];
    }

    // LC 63.
    static int uniquePathsWithObstacles(int[][] grid) {
        int n = grid[0].length;
        int[] dp = new int[n];
        dp[0] = grid[0][0] == 1 ? 0 : 1;
        for (int[] row : grid) {
            for (int c = 0; c < n; c++) {
                if (row[c] == 1) {
                    dp[c] = 0;
                } else if (c > 0) {
                    dp[c] += dp[c - 1];
                }
            }
        }
        return dp[n - 1];
    }

    // LC 64.
    static int minPathSum(int[][] g) {
        int rows = g.length;
        int cols = g[0].length;
        int[] dp = new int[cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == 0 && c == 0) {
                    dp[c] = g[0][0];
                } else if (r == 0) {
                    dp[c] = dp[c - 1] + g[r][c];
                } else if (c == 0) {
                    dp[c] = dp[c] + g[r][c];
                } else {
                    dp[c] = Math.min(dp[c], dp[c - 1]) + g[r][c];
                }
            }
        }
        return dp[cols - 1];
    }

    // LC 120: from the bottom row up, each cell keeps the best path below it.
    static int minimumTotal(List<List<Integer>> triangle) {
        int n = triangle.size();
        int[] best = new int[n + 1];
        for (int r = n - 1; r >= 0; r--) {
            for (int c = 0; c <= r; c++) {
                best[c] = triangle.get(r).get(c) + Math.min(best[c], best[c + 1]);
            }
        }
        return best[0];
    }

    // LC 221: largest square of '1's; returns its area.
    static int maximalSquare(char[][] m) {
        int cols = m[0].length;
        int[] side = new int[cols + 1];
        int best = 0;
        for (char[] row : m) {
            int diag = 0;                          // side[c] from the previous row, previous col
            for (int c = 1; c <= cols; c++) {
                int up = side[c];
                side[c] = row[c - 1] == '1' ? 1 + Math.min(up, Math.min(side[c - 1], diag)) : 0;
                diag = up;
                best = Math.max(best, side[c]);
            }
        }
        return best * best;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 62 3x7", uniquePaths(3, 7), 28);
        check("LC 62 3x2", uniquePaths(3, 2), 3);
        check("LC 62 1x1", uniquePaths(1, 1), 1);

        check("LC 63 centre obstacle",
                uniquePathsWithObstacles(new int[][]{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}), 2);
        check("LC 63 2x2", uniquePathsWithObstacles(new int[][]{{0, 1}, {0, 0}}), 1);
        check("LC 63 blocked start", uniquePathsWithObstacles(new int[][]{{1}}), 0);

        check("LC 64 3x3", minPathSum(new int[][]{{1, 3, 1}, {1, 5, 1}, {4, 2, 1}}), 7);
        check("LC 64 2x3", minPathSum(new int[][]{{1, 2, 3}, {4, 5, 6}}), 12);

        check("LC 120 four rows", minimumTotal(List.of(List.of(2), List.of(3, 4), List.of(6, 5, 7),
                List.of(4, 1, 8, 3))), 11);
        check("LC 120 single", minimumTotal(List.of(List.of(-10))), -10);

        char[][] sq = {"10100".toCharArray(), "10111".toCharArray(), "11111".toCharArray(),
                "10010".toCharArray()};
        check("LC 221 4x5", maximalSquare(sq), 4);
        check("LC 221 diagonal ones", maximalSquare(new char[][]{{'0', '1'}, {'1', '0'}}), 1);
        check("LC 221 all zero", maximalSquare(new char[][]{{'0'}}), 0);
    }
}

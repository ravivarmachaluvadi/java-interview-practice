/*
 * =====================================================================
 *  Optimal Path - collect the most rocks                  Grid DP | Medium
 * =====================================================================
 *
 * PROBLEM
 *   grid[r][c] holds the number of rocks in a cell. Start at the BOTTOM-LEFT cell and finish
 *   at the TOP-RIGHT cell, moving only up or right, collecting the rocks of every cell you
 *   stand on (including the start and the end). Return the largest total collectable.
 *   An empty grid collects 0.
 *
 * EXAMPLE
 *   {{0,0,0,0,5},                   ->  10   up the left edge is worthless here, so walk
 *    {0,1,1,1,0},                          right along the 1s then up to the 5:
 *    {2,0,0,0,0}}                          2 + 1 + 1 + 1 + 5 = 10
 *
 *   {{ 0,10},                       ->  18   the best route climbs column 0 first:
 *    { 7, 0},                             1 + 7 + 0 + 10. This is the case that exposed
 *    { 1, 0}}                             the column-0 bug in findMaxRocks (see Fixed).
 *
 *   {{}}                            ->   0   edge case: empty grid
 *
 * APPROACH  (grid max-path accumulation)
 *   1. best(r, c) = most rocks collectable on any legal walk from the start to (r, c).
 *   2. A cell is entered from BELOW (r + 1, c) or from the LEFT (r, c - 1), so
 *      best(r, c) = grid[r][c] + max(best(r + 1, c), best(r, c - 1)).
 *   3. Boundaries: the bottom-left start is just its own value; the rest of the bottom row
 *      can only be reached from the left; the rest of column 0 only from below.
 *   4. Sweep rows bottom to top and columns left to right, so both predecessors are ready.
 *   5. The answer sits in the top-right cell.
 *   optimalPath() does this IN PLACE, overwriting grid with running totals - O(1) extra
 *   space, but it destroys the caller's array. findMaxRocks() keeps a separate dp table.
 *
 * KEY INSIGHT
 *   Fix the direction of travel, then sweep in the order that guarantees every predecessor
 *   is already final. Here "up or right" means you must sweep rows upward and columns
 *   rightward - get that order wrong and you read cells that are still raw input. The same
 *   "cell = own value + max/min of the cells it can be entered from" shape drives Minimum
 *   Path Sum, Triangle and Minimum Falling Path Sum.
 *
 * Fixed: findMaxRocks never initialised column 0 above the bottom row, so those cells stayed
 *   0 and any path that climbs the left edge was undercounted. On {{0,10},{7,0},{1,0}} it
 *   returned 11 instead of 18. Column 0 is now seeded from below before the main sweep, and
 *   the redundant last-column pre-fill (immediately overwritten by the sweep) was dropped.
 *
 * COMPLEXITY
 *   Time  O(rows * cols)   every cell is finalised once, in O(1)
 *   Space O(rows * cols)   for findMaxRocks's dp table; optimalPath is O(1) but mutates input
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the path itself, not just the total (store which predecessor won per cell).
 *   - Minimise instead of maximise, or forbid some cells (obstacles) with a -infinity guard.
 *   - Reduce the table to a single column/row of O(cols) space.
 *   - Allow a third move (diagonal), or let the walk start anywhere on the bottom row.
 *
 * RUN
 *   main() runs 6 cases (typical, random, all zeros, many tied paths, empty, the column-0
 *   regression) and prints both implementations against the expected total.
 */

import java.util.Arrays;

class OptimalPath {

    /**
     * In-place version: each cell is replaced by the best total for reaching it.
     * Mutates the grid it is given, so callers must pass a copy if they need the input again.
     */
    public static int optimalPath(int[][] grid) {
        int rowCount = grid.length;
        int colCount = rowCount > 0 ? grid[0].length : 0;

        if (rowCount == 0 || colCount == 0) {
            return 0; // empty grid: nothing to collect
        }

        // Sweep rows bottom-to-top and columns left-to-right so both predecessors are final.
        for (int row = rowCount - 1; row >= 0; row--) {
            for (int col = 0; col < colCount; col++) {
                if (row < rowCount - 1 && col > 0) {
                    // reachable from below and from the left: take the better one
                    grid[row][col] += Math.max(grid[row + 1][col], grid[row][col - 1]);
                } else if (row < rowCount - 1) {
                    // column 0: the only way in is from below
                    grid[row][col] += grid[row + 1][col];
                } else if (col > 0) {
                    // bottom row: the only way in is from the left
                    grid[row][col] += grid[row][col - 1];
                }
                // else: the bottom-left start cell keeps its own value
            }
        }

        return grid[0][colCount - 1]; // the top-right corner holds the answer
    }

    /**
     * Same recurrence with a separate dp table, so the caller's grid is left untouched.
     * dp[i][j] = most rocks collectable on a walk from the bottom-left cell to (i, j).
     */
    public static int findMaxRocks(int[][] grid) {
        int m = grid.length;
        int n = m > 0 ? grid[0].length : 0;
        if (m == 0 || n == 0) return 0;

        int[][] dp = new int[m][n];

        // Start cell: bottom-left.
        dp[m - 1][0] = grid[m - 1][0];

        // Bottom row: can only be entered from the left.
        for (int j = 1; j < n; j++) {
            dp[m - 1][j] = grid[m - 1][j] + dp[m - 1][j - 1];
        }
        // Column 0: can only be entered from below. Without this the main sweep below reads
        // dp[i][0] == 0 as if the left edge were worthless. (This was the bug.)
        for (int i = m - 2; i >= 0; i--) {
            dp[i][0] = grid[i][0] + dp[i + 1][0];
        }

        // Every remaining cell: better of "came from below" and "came from the left".
        for (int i = m - 2; i >= 0; i--) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = grid[i][j] + Math.max(dp[i + 1][j], dp[i][j - 1]);
            }
        }

        return dp[0][n - 1]; // top-right corner
    }

    // -------------------------------------------------------------------------------------

    /** Deep copy, because optimalPath consumes the grid it is handed. */
    private static int[][] copy(int[][] grid) {
        int[][] out = new int[grid.length][];
        for (int i = 0; i < grid.length; i++) out[i] = Arrays.copyOf(grid[i], grid[i].length);
        return out;
    }

    private static void print(String label, int actual, int expected) {
        System.out.println(label + " -> " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][][] grids = {
                // typical: walk right along the 1s, then up to the 5
                {{0, 0, 0, 0, 5},
                 {0, 1, 1, 1, 0},
                 {2, 0, 0, 0, 0}},

                // random numbers
                {{1, 3, 2, 0, 2, 1, 8},
                 {3, 4, 1, 2, 0, 1, 1},
                 {1, 1, 1, 2, 3, 2, 1},
                 {1, 0, 1, 1, 4, 2, 1}},

                // all zeros: nothing to collect anywhere
                {{0, 0, 0, 0, 0},
                 {0, 0, 0, 0, 0},
                 {0, 0, 0, 0, 0},
                 {0, 0, 0, 0, 0}},

                // many equally optimal paths
                {{1, 1, 1, 1, 1},
                 {1, 0, 1, 0, 1},
                 {1, 0, 1, 0, 1},
                 {1, 1, 1, 1, 1}},

                // edge case: empty grid
                {{}},

                // regression: the best path climbs column 0 first
                {{0, 10},
                 {7, 0},
                 {1, 0}}
        };
        int[] expected = {10, 25, 0, 8, 0, 18};

        for (int c = 0; c < grids.length; c++) {
            System.out.println("case " + (c + 1) + ": grid = " + Arrays.deepToString(grids[c]));
            print("  findMaxRocks (dp table)", findMaxRocks(copy(grids[c])), expected[c]);
            print("  optimalPath  (in place)", optimalPath(copy(grids[c])), expected[c]);
        }
    }
}

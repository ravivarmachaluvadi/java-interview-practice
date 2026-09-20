/*
 * =====================================================================
 *  Number of Enclaves                               LeetCode 1020 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a binary grid where 1 is land and 0 is sea, you may walk between
 *   4-directionally adjacent land cells and may walk off the edge of the grid.
 *   Return how many land cells you can NOT walk off the grid from.
 *
 * EXAMPLE
 *   [[0,0,0,0],                    ->  3   the three 1s in the middle block are
 *    [1,0,1,0],                         enclosed; the 1 at (1,0) touches the
 *    [0,1,1,0],                         left border so its whole island escapes
 *    [0,0,0,0]]
 *   [[0,1,1,0],[0,0,1,0],[0,0,1,0],[0,0,0,0]]  ->  0   every 1 reaches the top
 *   [[1]]                          ->  0   a single cell IS the border
 *
 * APPROACH  (boundary DFS, then count the complement)
 *   1. Instead of asking "can this cell escape", flip it: anything connected to
 *      the border escapes by definition.
 *   2. Walk the four borders. From every 1 you find there, DFS and sink the
 *      whole island by writing 0 over it.
 *   3. Whatever 1s survive that sweep cannot touch the border, so they are the
 *      enclaves. Sum the grid - every remaining cell is a 1, so the sum IS the
 *      count.
 *
 * KEY INSIGHT
 *   Do not search from each land cell to see if it escapes; that repeats work
 *   and needs per-island bookkeeping. Search from the ESCAPE ROUTE inward once,
 *   erase everything it reaches, and the answer is what is left. This
 *   "eliminate from the border, count the remainder" move is the same one used
 *   by Surrounded Regions (130) and Pacific Atlantic Water Flow (417).
 *
 * COMPLEXITY
 *   Time  O(n * m)  each cell is sunk at most once, plus the final sum
 *   Space O(n * m)  recursion depth in the worst case (a grid that is all land)
 *
 * GOTCHA
 *   numEnclaves mutates the caller's grid - the sinking is done in place. main()
 *   therefore hands each case its own copy. Say this out loud in an interview;
 *   the alternative is a separate visited[][] at the same space cost.
 *
 * INTERVIEW FOLLOW-UPS
 *   - LeetCode 130 Surrounded Regions: same sweep, but flip the survivors
 *     instead of counting them.
 *   - LeetCode 1254 Number of Closed Islands: count enclosed ISLANDS, not cells.
 *   - Replace the recursion with an explicit stack or a BFS queue when the grid
 *     is large enough to overflow the call stack.
 *   - 8-directional movement: only the delta list changes.
 *
 * RUN
 *   main() runs 4 cases (typical, nothing enclosed, all sea, single cell) and
 *   prints actual vs expected.
 */

class NumberOfEnclaves {

    /** Sink the island containing (i, j) by overwriting its land with sea. */
    public void dfs(int[][] grid, int i, int j) {
        // one guard for both "fell off the grid" and "not land / already sunk"
        if (i < 0 || j < 0 || i >= grid.length || j >= grid[0].length || grid[i][j] == 0) {
            return;
        }
        grid[i][j] = 0;
        dfs(grid, i + 1, j);
        dfs(grid, i - 1, j);
        dfs(grid, i, j - 1);
        dfs(grid, i, j + 1);
    }

    public int numEnclaves(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) return 0;
        int n = grid.length;
        int m = grid[0].length;

        // sink every island touching the first or last column
        for (int row = 0; row < n; row++) {
            dfs(grid, row, 0);
            dfs(grid, row, m - 1);
        }
        // sink every island touching the first or last row
        for (int col = 0; col < m; col++) {
            dfs(grid, 0, col);
            dfs(grid, n - 1, col);
        }

        // only enclosed land is still 1, so summing the grid counts it
        int enclosed = 0;
        for (int[] row : grid) {
            for (int cell : row) {
                enclosed += cell;
            }
        }
        return enclosed;
    }

    private static int[][] copyOf(int[][] grid) {
        int[][] copy = new int[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            copy[i] = grid[i].clone();
        }
        return copy;
    }

    private static void print(String label, int[][] grid, int expected) {
        int actual = new NumberOfEnclaves().numEnclaves(copyOf(grid)); // copy: the solver mutates
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1: one escaping island at (1,0), one enclosed block of 3
        print("case 1 typical",
                new int[][]{{0, 0, 0, 0}, {1, 0, 1, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}}, 3);

        // case 2: every land cell is connected to the top row, so nothing is trapped
        print("case 2 all escape",
                new int[][]{{0, 1, 1, 0}, {0, 0, 1, 0}, {0, 0, 1, 0}, {0, 0, 0, 0}}, 0);

        // case 3 (edge): no land at all
        print("case 3 all sea", new int[][]{{0, 0}, {0, 0}}, 0);

        // case 4 (edge): a 1x1 grid - the only cell is on the border
        print("case 4 single cell", new int[][]{{1}}, 0);
    }
}

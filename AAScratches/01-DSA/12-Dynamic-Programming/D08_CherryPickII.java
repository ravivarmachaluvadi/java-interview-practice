/*
 * =====================================================================
 *  Cherry Pickup II                              LeetCode 1463 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   An n x m grid holds a cherry count in every cell. Robot 1 starts at (0, 0), robot 2 at
 *   (0, m-1). Both move down one row per step, each shifting at most one column left or right,
 *   until both reach the last row. A cell's cherries are collected once even if both robots
 *   stand on it. Return the maximum cherries the pair can collect together.
 *
 * EXAMPLE
 *   [[2,3,1,2],                robot 1: 2 -> 4 -> 6   robot 2: 2 -> 2 -> 5
 *    [3,4,2,2],        ->  21 [5,6,3,5]]
 *   [[3,1,1],[2,5,1],[1,5,5],[2,1,1]]  ->  24
 *   [[5],[3]]  ->  8   (one column: both robots are forced onto the same cells, no double count)
 *
 * APPROACH  (move the two robots in lockstep; state = (row, col1, col2))
 *   1. Because both robots advance exactly one row per step, they always share a row. So the
 *      whole configuration is (i, j1, j2) - one row index, not two.
 *   2. From (i, j1, j2) each robot has 3 column moves, giving 9 joint transitions. Recurse on
 *      all 9 and keep the best.
 *   3. Collect matrix[i][j1] + matrix[i][j2], but only matrix[i][j1] when j1 == j2, which is
 *      the single rule that stops double counting.
 *   4. Base case: on the last row return what the two robots stand on. A column outside the
 *      grid returns a large negative value so that branch can never be the maximum.
 *
 *   Two methods are provided and main() runs both:
 *     cherryPickup     - the plain recursion, exponential, easiest to read
 *     cherryPickupMemo - the same recursion with a 3D memo, which is the interview answer
 *
 * KEY INSIGHT
 *   Two agents do NOT need two independent DPs. Synchronise them on a shared clock (here, the
 *   row) and the state collapses from (i1, j1, i2, j2) to (i, j1, j2). Every multi-agent grid
 *   problem starts with finding that shared clock.
 *
 * COMPLEXITY
 *   Plain recursion  Time O(9^n)          every row branches nine ways
 *   Memoised         Time O(n * m * m * 9), Space O(n * m * m) table + O(n) stack
 *
 * INTERVIEW FOLLOW-UPS
 *   - Convert to bottom-up tabulation over rows and drop to O(m * m) space with two layers.
 *   - Three robots: the state becomes (i, j1, j2, j3) with 27 transitions - still polynomial.
 *   - Cherry Pickup I (LC 741) is the harder sibling: one robot goes there and back, so the
 *     shared clock is the step count i + j rather than the row.
 *   - Reconstruct the two paths: store the winning (dj1, dj2) per state and replay it.
 *
 * RUN
 *   main() runs 4 cases (typical, LeetCode sample, single column, single row) and prints the
 *   plain and memoised answers against the expected value.
 */
import java.util.Arrays;

class CherryPickII {

    /** Any move off the grid scores this, so it can never win a Math.max. */
    private static final int OUT_OF_GRID = (int) -1e9;

    /** Marker for "this state has not been computed yet". */
    private static final int UNSET = Integer.MIN_VALUE;

    // ---------- 1. plain recursion: clear, but exponential ----------

    private int func(int i, int j1, int j2, int n, int m, int[][] matrix) {
        if (j1 < 0 || j1 >= m || j2 < 0 || j2 >= m) {
            return OUT_OF_GRID;
        }

        // Last row: nothing more to choose, just bank the cherries under both robots.
        if (i == n - 1) {
            return (j1 == j2) ? matrix[i][j1] : matrix[i][j1] + matrix[i][j2];
        }

        int cherriesHere = (j1 == j2) ? matrix[i][j1] : matrix[i][j1] + matrix[i][j2];

        // 3 moves for robot 1 x 3 moves for robot 2 = 9 joint transitions.
        int best = Integer.MIN_VALUE;
        for (int dj1 = -1; dj1 <= 1; dj1++) {
            for (int dj2 = -1; dj2 <= 1; dj2++) {
                best = Math.max(best, func(i + 1, j1 + dj1, j2 + dj2, n, m, matrix));
            }
        }
        return cherriesHere + best;
    }

    public int cherryPickup(int[][] matrix) {
        int n = matrix.length;
        int m = matrix[0].length;
        return func(0, 0, m - 1, n, m, matrix);
    }

    // ---------- 2. same recursion, memoised on (row, col1, col2) ----------

    private int funcMemo(int i, int j1, int j2, int n, int m, int[][] matrix, int[][][] memo) {
        if (j1 < 0 || j1 >= m || j2 < 0 || j2 >= m) {
            return OUT_OF_GRID;
        }
        if (i == n - 1) {
            return (j1 == j2) ? matrix[i][j1] : matrix[i][j1] + matrix[i][j2];
        }
        if (memo[i][j1][j2] != UNSET) {
            return memo[i][j1][j2];
        }

        int cherriesHere = (j1 == j2) ? matrix[i][j1] : matrix[i][j1] + matrix[i][j2];

        int best = Integer.MIN_VALUE;
        for (int dj1 = -1; dj1 <= 1; dj1++) {
            for (int dj2 = -1; dj2 <= 1; dj2++) {
                best = Math.max(best, funcMemo(i + 1, j1 + dj1, j2 + dj2, n, m, matrix, memo));
            }
        }
        return memo[i][j1][j2] = cherriesHere + best;
    }

    public int cherryPickupMemo(int[][] matrix) {
        int n = matrix.length;
        int m = matrix[0].length;
        int[][][] memo = new int[n][m][m];
        for (int[][] layer : memo) {
            for (int[] row : layer) {
                Arrays.fill(row, UNSET);
            }
        }
        return funcMemo(0, 0, m - 1, n, m, matrix, memo);
    }

    // ---------- demo ----------

    private static void print(String label, int plain, int memo, int expected) {
        System.out.println(label + ": recursion=" + plain + " memo=" + memo
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        CherryPickII sol = new CherryPickII();

        // Case 1 (typical): 3 x 4 grid.
        int[][] grid1 = {
                {2, 3, 1, 2},
                {3, 4, 2, 2},
                {5, 6, 3, 5}
        };
        print("case 1 3x4 grid     ", sol.cherryPickup(grid1), sol.cherryPickupMemo(grid1), 21);

        // Case 2 (LeetCode sample 1).
        int[][] grid2 = {
                {3, 1, 1},
                {2, 5, 1},
                {1, 5, 5},
                {2, 1, 1}
        };
        print("case 2 leetcode #1  ", sol.cherryPickup(grid2), sol.cherryPickupMemo(grid2), 24);

        // Case 3 (edge): one column forces both robots onto the same cell every row,
        // so each cell counts once: 5 + 3 = 8.
        int[][] grid3 = {
                {5},
                {3}
        };
        print("case 3 single column", sol.cherryPickup(grid3), sol.cherryPickupMemo(grid3), 8);

        // Case 4 (edge): one row, so the robots never move: 1 + 3 = 4.
        int[][] grid4 = {
                {1, 2, 3}
        };
        print("case 4 single row   ", sol.cherryPickup(grid4), sol.cherryPickupMemo(grid4), 4);
    }
}

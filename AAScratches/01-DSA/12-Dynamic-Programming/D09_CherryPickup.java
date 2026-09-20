/*
 * =====================================================================
 *  Cherry Pickup                                  LeetCode 741 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   An n x n grid holds 0 (empty), 1 (a cherry) or -1 (a thorn you cannot enter). Walk from
 *   (0, 0) to (n-1, n-1) moving only right or down, then walk back to (0, 0) moving only left
 *   or up, picking up every cherry you step on (the cell becomes 0 once picked).
 *   Return the most cherries you can collect. If no round trip exists, return 0.
 *
 * EXAMPLE
 *   [[0, 1,-1],          down/right there and back collects 1+1+1+1+1
 *    [1, 0,-1],   ->  5
 *    [1, 1, 1]]
 *   [[1, 1,-1],[1,-1, 1],[-1, 1, 1]]  ->  0   (thorns block every path to the corner)
 *   [[1]]      ->  1   (single cell, start == end)
 *   [[1,1],[1,1]] -> 4 (both trips together cover all four cells)
 *
 * APPROACH  (turn the return trip into a second forward walk, then DP on step count)
 *   1. Walking back from (n-1, n-1) to (0, 0) with left/up moves is the same set of paths as
 *      walking forward from (0, 0) with right/down moves. So model TWO people walking forward
 *      simultaneously instead of one person going there and back.
 *   2. Both take exactly one step per tick, so after k steps a person at row i must be at
 *      column j = k - i. One number k plus two row indices fixes both positions.
 *      State: dp[k][i1][i2] = best total after k steps, person 1 at (i1, k-i1), person 2 at
 *      (i2, k-i2). k runs 0 .. 2*(n-1).
 *   3. Transition: each person arrived from above or from the left, so four combinations feed
 *      dp[k][i1][i2]. Add the cherries at both cells, counting the cell ONCE when the two
 *      people are standing on the same square - that is exactly the "already picked" rule.
 *   4. Unreachable states stay at Integer.MIN_VALUE and can never win a max. Thorny cells are
 *      skipped outright. The answer is dp[2(n-1)][n-1][n-1], floored at 0 when no path exists.
 *
 * KEY INSIGHT
 *   A there-and-back trip is two forward trips. Once both walkers move on the same clock
 *   (k = i + j), the column is implied by the row and the state drops from four indices to
 *   three. Greedily taking the best single path first does NOT work: the best round trip is
 *   often two mediocre paths that overlap as little as possible.
 *
 * COMPLEXITY
 *   Time  O(n^3)  2n step values times n x n row pairs, with O(1) work each
 *   Space O(n^3)  the full dp cube; keeping only layer k-1 reduces it to O(n^2)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Drop to O(n^2) space by keeping only the previous k layer.
 *   - Write it as memoised recursion on (k, i1, i2) - often easier to derive under pressure.
 *   - Cherry Pickup II (LC 1463): two robots that start on the same row, so the shared clock
 *     is the row itself and the state is (row, col1, col2).
 *   - Why is "best path, zero it out, best path again" wrong? Construct a counterexample.
 *   - Reconstruct both routes: store the chosen predecessor per state and replay it.
 *
 * RUN
 *   main() runs 4 cases (typical, fully blocked, single cell, all cherries) printing actual
 *   vs expected.
 */
import java.util.Arrays;

// https://leetcode.com/problems/cherry-pickup/description/
// 741. Cherry Pickup
class CherryPickup {

    public static int cherryPickup(int[][] grid) {
        int n = grid.length;
        if (grid[0][0] == -1) return 0;     // cannot even start

        // dp[k][i1][i2]: both walkers have taken k steps; walker 1 sits at (i1, k-i1),
        // walker 2 at (i2, k-i2). Columns are implied, which is what keeps this 3D.
        int maxK = 2 * (n - 1);
        int[][][] dp = new int[maxK + 1][n][n];
        for (int k = 0; k <= maxK; k++) {
            for (int i1 = 0; i1 < n; i1++) {
                Arrays.fill(dp[k][i1], Integer.MIN_VALUE);   // MIN_VALUE == unreachable
            }
        }
        dp[0][0][0] = grid[0][0];           // both start on the top-left cell

        for (int k = 1; k <= maxK; k++) {
            for (int i1 = 0; i1 < n; i1++) {
                int j1 = k - i1;                            // column is forced by the clock
                if (j1 < 0 || j1 >= n) continue;
                if (grid[i1][j1] == -1) continue;           // thorn: walker 1 cannot stand here

                for (int i2 = 0; i2 < n; i2++) {
                    int j2 = k - i2;
                    if (j2 < 0 || j2 >= n) continue;
                    if (grid[i2][j2] == -1) continue;

                    // Same cell counts once: the second walker finds it already picked.
                    int cherries = grid[i1][j1];
                    if (i1 != i2) {
                        cherries += grid[i2][j2];
                    }

                    // Each walker came from above (row-1, same column) or from the left
                    // (same row, column-1). Four joint predecessors.
                    int bestPrev = Integer.MIN_VALUE;
                    if (i1 > 0 && i2 > 0) bestPrev = Math.max(bestPrev, dp[k - 1][i1 - 1][i2 - 1]);
                    if (i1 > 0 && j2 > 0) bestPrev = Math.max(bestPrev, dp[k - 1][i1 - 1][i2]);
                    if (j1 > 0 && i2 > 0) bestPrev = Math.max(bestPrev, dp[k - 1][i1][i2 - 1]);
                    if (j1 > 0 && j2 > 0) bestPrev = Math.max(bestPrev, dp[k - 1][i1][i2]);

                    // Leave the state unreachable if no valid predecessor fed it.
                    if (bestPrev != Integer.MIN_VALUE) {
                        dp[k][i1][i2] = bestPrev + cherries;
                    }
                }
            }
        }

        // Both walkers must finish in the bottom-right corner; no path at all scores 0.
        return Math.max(0, dp[maxK][n - 1][n - 1]);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1 (typical): two overlapping routes around the thorns.
        int[][] grid1 = {
                {0, 1, -1},
                {1, 0, -1},
                {1, 1, 1}
        };
        print("case 1 leetcode #1  ", cherryPickup(grid1), 5);

        // Case 2 (tricky): thorns cut every route, so even the cherries near the start count 0.
        int[][] grid2 = {
                {1, 1, -1},
                {1, -1, 1},
                {-1, 1, 1}
        };
        print("case 2 blocked      ", cherryPickup(grid2), 0);

        // Case 3 (edge): 1 x 1 grid - start and end are the same cell, picked once.
        int[][] grid3 = {
                {1}
        };
        print("case 3 single cell  ", cherryPickup(grid3), 1);

        // Case 4 (edge): the two trips together sweep all four cells.
        int[][] grid4 = {
                {1, 1},
                {1, 1}
        };
        print("case 4 all cherries ", cherryPickup(grid4), 4);
    }
}

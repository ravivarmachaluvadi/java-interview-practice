/*
 * =====================================================================
 *  Number of Islands                  LeetCode 200 | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an m x n grid of '1' (land) and '0' (water), count the islands.
 *   An island is a maximal group of '1' cells joined 4-directionally (up, down,
 *   left, right). Diagonal touching does not join two islands, and the grid is
 *   surrounded by water on all sides.
 *
 * EXAMPLE
 *   [['1','1','1','1','0'],
 *    ['1','1','0','1','0'],
 *    ['1','1','0','0','0'],
 *    ['0','0','0','0','0']]   ->  1   all the land is one connected blob
 *   [['0','0'],['0','0']]     ->  0   edge case: no land at all
 *
 * APPROACH  (count connected components in a grid)
 *   1. Scan every cell in row-major order.
 *   2. The first time you meet an unvisited '1', you have found a new island:
 *      increment the counter.
 *   3. Immediately flood the whole island with DFS, flipping each '1' to '0'.
 *      That sinking is what marks cells visited, so the outer scan never counts
 *      the same island twice.
 *   4. The DFS returns early on any out-of-bounds cell or on water, which keeps
 *      the bounds check in exactly one place.
 *
 * KEY INSIGHT
 *   "Count the components" = outer scan that starts one traversal per unvisited
 *   source, plus an inner traversal that consumes the whole component. The outer
 *   loop counts, the inner loop marks. Every grid counting question (provinces,
 *   enclaves, largest island) is this same two-loop skeleton with a different
 *   inner body.
 *
 * COMPLEXITY
 *   Time  O(m * n)  each cell is scanned once and sunk at most once
 *   Space O(m * n)  worst-case recursion depth when the whole grid is land
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it without mutating the input: carry a boolean[][] visited instead.
 *   - Swap DFS for a BFS queue to avoid stack overflow on a 1000 x 1000 grid.
 *   - Union-Find version, which is the one that extends to streaming updates
 *     (Number of Islands II, where land is added one cell at a time).
 *   - Count the largest island or the island perimeter with the same traversal.
 *
 * FIXES APPLIED
 *   Fixed: deepCopy() called original.clone(), which copies only the array of row
 *   references - the rows themselves stayed shared, so the "copy" was sunk along
 *   with the original and a second call on the same grid returned 0. It now copies
 *   each row.
 *
 * RUN
 *   main() runs 3 cases (one big island, all water, and the same grid counted
 *   twice to prove the copy is real) and prints actual vs expected.
 */

import java.util.*;

class NumberOfIslands {

    public static int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }

        int nr = grid.length;      // number of rows
        int nc = grid[0].length;   // number of columns
        int count = 0;

        for (int r = 0; r < nr; r++) {
            for (int c = 0; c < nc; c++) {
                if (grid[r][c] == '1') {
                    count++;                 // a land cell nobody has reached yet
                    dfs(grid, r, c, nr, nc); // sink the rest of this island
                }
            }
        }
        return count;
    }

    private static void dfs(char[][] grid, int r, int c, int nr, int nc) {
        // off the grid, or water (which also means "already sunk / already visited")
        if (r < 0 || c < 0 || r >= nr || c >= nc || grid[r][c] == '0') {
            return;
        }

        grid[r][c] = '0'; // sinking the cell is how we mark it visited

        dfs(grid, r - 1, c, nr, nc);
        dfs(grid, r + 1, c, nr, nc);
        dfs(grid, r, c - 1, nr, nc);
        dfs(grid, r, c + 1, nr, nc);
    }

    /** Real deep copy: numIslands destroys the grid it is given. */
    private static char[][] deepCopy(char[][] original) {
        char[][] copy = new char[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone(); // clone each ROW, not just the row array
        }
        return copy;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        char[][] oneIsland = {
                {'1', '1', '1', '1', '0'},
                {'1', '1', '0', '1', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '0', '0', '0'}
        };

        char[][] threeIslands = {
                {'1', '1', '0', '0', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '1', '0', '0'},
                {'0', '0', '0', '1', '1'}
        };

        // typical: one L-shaped blob plus a column that joins it on the top row
        print("case 1 (one island) ", numIslands(deepCopy(oneIsland)), 1);

        // edge case: no land at all
        print("case 2 (all water)  ", numIslands(new char[][]{{'0', '0'}, {'0', '0'}}), 0);

        // tricky: same grid counted twice - only a true deep copy gives 3 both times
        int first = numIslands(deepCopy(threeIslands));
        int second = numIslands(deepCopy(threeIslands));
        print("case 3 (grid twice) ", first + " then " + second, "3 then 3");
    }
}

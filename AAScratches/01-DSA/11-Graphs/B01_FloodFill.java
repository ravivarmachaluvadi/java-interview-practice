/*
 * =====================================================================
 *  Flood Fill                                      LeetCode 733 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   An image is an m x n grid of integer colours. Starting at pixel (sr, sc),
 *   repaint that pixel and every pixel reachable from it through 4-directional
 *   moves (up, down, left, right) that has the same colour as the start pixel.
 *   Diagonal neighbours do not count. Return the modified image.
 *
 * EXAMPLE
 *   image = [[1,1,1],[1,1,0],[1,0,1]], sr=1, sc=1, colour=2
 *     -> [[2,2,2],[2,2,0],[2,0,1]]   the bottom-right 1 is only diagonally attached
 *   image = [[0,0],[0,0]], sr=0, sc=0, colour=0  ->  unchanged (new colour == old)
 *
 * APPROACH  (grid DFS with delta arrays)
 *   1. Remember iniColor = image[sr][sc], the colour we are allowed to overwrite.
 *   2. If iniColor == newColor, return immediately. Without this guard the repaint
 *      never changes anything, so the "same colour" test stays true forever and the
 *      recursion loops between two neighbours until the stack blows.
 *   3. Paint the current cell, then for each of the 4 directions compute
 *      nrow = row + delRow[i], ncol = col + delCol[i].
 *   4. Recurse into a neighbour only if it is inside the grid AND still iniColor.
 *      Repainting is what marks a cell visited, so no separate visited[][] is needed.
 *
 * KEY INSIGHT
 *   A grid is a graph in disguise: each cell is a node and the 4 deltas are its
 *   edges. The delRow/delCol pair plus one bounds check is the idiom that every
 *   later grid problem (islands, rotten oranges, shortest path in a matrix) reuses -
 *   only the "may I step here" condition changes.
 *
 * COMPLEXITY
 *   Time  O(m * n)  each cell is painted at most once, then never matches again
 *   Space O(m * n)  recursion depth in the worst case (one snake-shaped region)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Rewrite with an explicit stack or a BFS queue to avoid stack overflow.
 *   - 8-directional fill: extend the delta arrays to 8 entries.
 *   - What if you may not mutate the input? Carry a separate visited[][].
 *   - Count the filled region size, which is exactly Number of Islands' helper.
 *
 * RUN
 *   main() runs 3 cases (typical, same-colour no-op, diagonal-only neighbour) and
 *   prints actual vs expected.
 */

import java.util.*;

class FloodFill {

    // delRow/delCol together give up, right, down, left ("del" = delta)
    private static final int[] DEL_ROW = {-1, 0, 1, 0};
    private static final int[] DEL_COL = {0, 1, 0, -1};

    private void dfs(int row, int col, int[][] image, int newColor, int iniColor) {
        image[row][col] = newColor; // painting also serves as "visited"
        int n = image.length, m = image[0].length;

        for (int i = 0; i < 4; i++) {
            int nrow = row + DEL_ROW[i];
            int ncol = col + DEL_COL[i];
            // inside the grid and still part of the original blob
            if (nrow >= 0 && nrow < n && ncol >= 0 && ncol < m
                    && image[nrow][ncol] == iniColor) {
                dfs(nrow, ncol, image, newColor, iniColor);
            }
        }
    }

    public int[][] floodFill(int[][] image, int sr, int sc, int newColor) {
        int iniColor = image[sr][sc];
        if (iniColor == newColor) return image; // guard against infinite recursion
        dfs(sr, sc, image, newColor, iniColor);
        return image;
    }

    private static void print(String label, int[][] actual, String expected) {
        System.out.println(label + ": " + Arrays.deepToString(actual)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        FloodFill obj = new FloodFill();

        // typical: the plus-shaped blob of 1s around (1,1) turns into 2s
        int[][] image1 = {{1, 1, 1}, {1, 1, 0}, {1, 0, 1}};
        print("case 1 (typical)   ", obj.floodFill(image1, 1, 1, 2),
                "[[2, 2, 2], [2, 2, 0], [2, 0, 1]]");

        // edge case: new colour equals the start colour, so nothing may change
        int[][] image2 = {{0, 0}, {0, 0}};
        print("case 2 (same colour)", obj.floodFill(image2, 0, 0, 0), "[[0, 0], [0, 0]]");

        // tricky: the other 1 touches only diagonally, so it must stay a 1
        int[][] image3 = {{1, 0}, {0, 1}};
        print("case 3 (diagonal)  ", obj.floodFill(image3, 0, 0, 3), "[[3, 0], [0, 1]]");
    }
}

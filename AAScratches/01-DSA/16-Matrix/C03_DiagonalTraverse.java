/*
 * =====================================================================
 *  Diagonal Traverse                            LeetCode 498 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an m x n matrix, return all its elements in diagonal order. The
 *   first diagonal is walked up-and-right, the next down-and-left, and the
 *   direction alternates for every diagonal. Output has exactly m * n items.
 *
 * EXAMPLE
 *   [[1,2,3],[4,5,6],[7,8,9]] -> [1,2,4,7,5,3,6,8,9]
 *   [[1,2,3],[4,5,6]]         -> [1,2,4,5,3,6]
 *   [[1],[2],[3]]             -> [1,2,3]   single column, one cell per diagonal
 *   [[7]]                     -> [7]       single cell
 *
 * APPROACH  (direction toggle with boundary cases)
 *   1. Cells on one diagonal all share the same value of row + col, and the
 *      walk keeps that sum constant: up-right is (row-1, col+1).
 *   2. Emit mat[row][col], then decide the next cell from the direction.
 *   3. Going up-right: if col is on the right edge, drop one row (row++);
 *      else if row is on the top edge, step one column right (col++);
 *      otherwise move up-right. Then flip direction in the first two cases.
 *   4. Going down-left is the mirror image: bottom edge first (col++), then
 *      left edge (row++), otherwise move down-left.
 *   5. Loop exactly m * n times; no visited array and no sorting are needed.
 *
 * KEY INSIGHT
 *   The whole problem is the ORDER of the two boundary checks at a corner.
 *   Going up-right at the top-right cell (0, n-1) both "top row" and "right
 *   column" are true; the right-edge check must win, because the next diagonal
 *   starts one row down, not one column right (which is off the matrix).
 *   Same at the bottom-left corner going down-left: the bottom-edge check
 *   must be tested before the left-edge check. Recognise the pattern: when a
 *   walk can hit two boundaries at once, decide the tie deliberately.
 *
 * COMPLEXITY
 *   Time  O(m * n)  one iteration per cell, O(1) work each
 *   Space O(1)      excluding the required m * n output array
 *
 * INTERVIEW FOLLOW-UPS
 *   - Diagonal Traverse II (LC 1424): a jagged list of lists, not a matrix.
 *     The index trick breaks; bucket by row + col instead.
 *   - Group all elements by diagonal without alternating direction.
 *   - Sort each diagonal (LC 1329) using the same row - col grouping.
 *   - Walk anti-diagonals (row - col constant) instead of diagonals.
 *
 * RUN
 *   main() runs 4 cases (square, wide, single column, single cell) and
 *   prints actual vs expected.
 */

import java.util.Arrays;

class DiagonalTraverse {

    public static int[] findDiagonalOrder(int[][] mat) {
        if (mat == null || mat.length == 0 || mat[0].length == 0) {
            return new int[0];
        }
        int m = mat.length, n = mat[0].length;
        int[] result = new int[m * n];

        int row = 0, col = 0;
        int direction = 1; // 1 = walking up-right, -1 = walking down-left

        for (int i = 0; i < m * n; i++) {
            result[i] = mat[row][col];

            if (direction == 1) {          // up-right
                if (col == n - 1) {        // right edge wins the corner tie
                    row++;
                    direction = -1;
                } else if (row == 0) {     // top edge: start the next diagonal
                    col++;
                    direction = -1;
                } else {
                    row--;
                    col++;                 // keep row + col constant
                }
            } else {                       // down-left
                if (row == m - 1) {        // bottom edge wins the corner tie
                    col++;
                    direction = 1;
                } else if (col == 0) {     // left edge: start the next diagonal
                    row++;
                    direction = 1;
                } else {
                    row++;
                    col--;                 // keep row + col constant
                }
            }
        }
        return result;
    }

    private static void print(String label, int[] actual, String expected) {
        System.out.println(label + ": " + Arrays.toString(actual) + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] square = {{1, 2, 3},
                          {4, 5, 6},
                          {7, 8, 9}};
        print("case 1 (3x3)", findDiagonalOrder(square), "[1, 2, 4, 7, 5, 3, 6, 8, 9]");

        int[][] wide = {{1, 2, 3},
                        {4, 5, 6}};
        print("case 2 (2x3)", findDiagonalOrder(wide), "[1, 2, 4, 5, 3, 6]");

        int[][] singleCol = {{1}, {2}, {3}};
        print("case 3 (3x1)", findDiagonalOrder(singleCol), "[1, 2, 3]");

        int[][] singleCell = {{7}};
        print("case 4 (1x1)", findDiagonalOrder(singleCell), "[7]");
    }
}

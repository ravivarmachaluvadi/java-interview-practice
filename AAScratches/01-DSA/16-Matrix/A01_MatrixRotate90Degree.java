/*
 * =====================================================================
 *  Rotate Image (rotate an n x n matrix in place)   LeetCode 48 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an n x n matrix, rotate it 90 degrees clockwise IN PLACE - you may not
 *   allocate a second matrix. The matrix is always square, so every row index is
 *   also a valid column index; that is what makes the in-place trick possible.
 *
 * EXAMPLE
 *   [[1,2],[3,4]]                    ->  [[3,1],[4,2]]
 *   [[1,2,3],[4,5,6],[7,8,9]]        ->  [[7,4,1],[8,5,2],[9,6,3]]
 *   [[5]]                            ->  [[5]]            (single cell, no work)
 *
 * APPROACH  (transpose, then reverse each row)
 *   1. Transpose: swap mat[i][j] with mat[j][i] for every j > i. Starting the inner
 *      loop at j = i + 1 visits each unordered pair exactly once; starting it at 0
 *      would swap every pair twice and leave the matrix unchanged.
 *   2. Reverse each row with two pointers walking in from both ends.
 *   3. Transpose maps (r,c) -> (c,r); reversing the row then maps (c,r) -> (c,n-1-r),
 *      which is exactly the clockwise rotation.
 *   4. rotate270() reuses the same transpose but reverses each COLUMN instead, so
 *      (r,c) -> (n-1-c, r): a 90 degree anticlockwise turn.
 *
 * KEY INSIGHT
 *   Every square-matrix rotation is built from two cheap primitives, transpose and
 *   reverse. Memorise the three combinations:
 *     90 clockwise        = transpose, then reverse each row
 *     180                 = reverse each row, then reverse each column
 *     270 clockwise (=90 anticlockwise) = transpose, then reverse each column
 *   If you can recite those, you never have to re-derive index arithmetic at a whiteboard.
 *
 * COMPLEXITY
 *   Time  O(n^2)  every cell is touched a constant number of times in each pass
 *   Space O(1)    only swap temporaries; the rotation happens in the input array
 *
 * INTERVIEW FOLLOW-UPS
 *   - Rotate anticlockwise instead: transpose, then reverse each column.
 *   - Rotate a non-square m x n matrix: O(1) space is impossible, allocate n x m.
 *   - Rotate by layers (four-way cyclic swap) - same O(1) space, one pass, trickier indices.
 *   - Spiral traversal reuses the same boundary-shrinking mindset
 *     (C02_SpiralTraversalOfMatrix).
 *
 * RUN
 *   main() runs 4 cases (2x2 90 cw, 3x3 90 cw, 1x1 edge, 3x3 90 anticlockwise via
 *   rotate270) and prints actual vs expected.
 */

import java.util.Arrays;

class MatrixRotate90Degree {

    /** Rotates a square matrix 90 degrees clockwise, in place. */
    static void rotate90(int[][] mat) {
        transpose(mat);
        reverseEachRow(mat);
    }

    /** Rotates a square matrix 90 degrees anticlockwise: transpose, then reverse columns. */
    static void rotate270(int[][] mat) {
        transpose(mat);
        reverseEachColumn(mat);
    }

    /** Mirrors the matrix across its main diagonal: mat[i][j] <-> mat[j][i]. */
    private static void transpose(int[][] mat) {
        int n = mat.length;
        for (int i = 0; i < n; i++) {
            // j starts at i + 1 so each pair is swapped exactly once (j = 0 would undo the work)
            for (int j = i + 1; j < n; j++) {
                int temp = mat[i][j];
                mat[i][j] = mat[j][i];
                mat[j][i] = temp;
            }
        }
    }

    private static void reverseEachRow(int[][] mat) {
        for (int[] row : mat) {
            int start = 0, end = row.length - 1;
            while (start < end) {
                int temp = row[start];
                row[start] = row[end];
                row[end] = temp;
                start++;
                end--;
            }
        }
    }

    private static void reverseEachColumn(int[][] mat) {
        int n = mat.length;
        for (int col = 0; col < n; col++) {
            int top = 0, bottom = n - 1;
            while (top < bottom) {
                int temp = mat[top][col];
                mat[top][col] = mat[bottom][col];
                mat[bottom][col] = temp;
                top++;
                bottom--;
            }
        }
    }

    private static void print(String label, int[][] actual, String expected) {
        System.out.println(label + ": " + Arrays.deepToString(actual) + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] two = {{1, 2}, {3, 4}};
        rotate90(two);
        print("case 1 (2x2, 90 cw)", two, "[[3, 1], [4, 2]]");

        int[][] three = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        rotate90(three);
        print("case 2 (3x3, 90 cw)", three, "[[7, 4, 1], [8, 5, 2], [9, 6, 3]]");

        int[][] one = {{5}};
        rotate90(one);
        print("case 3 (1x1 edge)", one, "[[5]]");

        int[][] anti = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        rotate270(anti);
        print("case 4 (3x3, 90 anticlockwise)", anti, "[[3, 6, 9], [2, 5, 8], [1, 4, 7]]");
    }
}

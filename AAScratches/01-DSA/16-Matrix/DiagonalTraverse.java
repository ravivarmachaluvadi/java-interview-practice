// LeetCode Problem: https://leetcode.com/problems/diagonal-traverse/

import java.util.*;

// https://leetcode.com/problems/diagonal-traverse/description/
class DiagonalTraverse {
    public static int[] findDiagonalOrder(int[][] mat) {
        if (mat == null || mat.length == 0) {
            return new int[0];
        }
        int m = mat.length, n = mat[0].length;
        int[] result = new int[m * n];
        int row = 0, col = 0, direction = 1;

        for (int i = 0; i < m * n; i++) {
            result[i] = mat[row][col];

            // Move up
            if (direction == 1) {
                if (col == n - 1) { // If we hit the right boundary, go down
                    row++;
                    direction = -1;
                } else if (row == 0) { // If we hit the top boundary, go right
                    col++;
                    direction = -1;
                } else { // Move diagonally up
                    row--;
                    col++;
                }
            }
            // Move down
            else {
                if (row == m - 1) { // If we hit the bottom boundary, go right
                    col++;
                    direction = 1;
                } else if (col == 0) { // If we hit the left boundary, go down
                    row++;
                    direction = 1;
                } else { // Move diagonally down
                    row++;
                    col--;
                }
            }
        }

        return result;
        // [1, 2, 4, 7, 5, 3, 6, 8, 9]
    }

    public static void main(String[] args) {
        int[][] mat = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };

        int[] result = findDiagonalOrder(mat);

        System.out.println("Diagonal Traverse:");
        System.out.println(Arrays.toString(result));
        // Expected output: [1, 2, 4, 7, 5, 3, 6, 8, 9]
    }
}

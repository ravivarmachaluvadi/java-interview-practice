/**
 * Problem: Given an m x n integer matrix, set entire row and column to zero
 * if an element is zero.
 *
 * Approach: First pass records rows and columns that contain a zero using two
 * hash sets. Second pass iterates again, setting any cell whose row or column
 * is marked to zero.
 *
 * Time Complexity: O(m*n) – two full traversals of the matrix.
 * Space Complexity: O(m + n) – storage for at most all rows and columns in the sets.
 */
import java.util.HashSet;
import java.util.Set;

class SetMatrixZeroes {

    public void setZeroes(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        Set<Integer> rowzero = new HashSet<>();
        Set<Integer> colzero = new HashSet<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == 0) {
                    rowzero.add(i);
                    colzero.add(j);
                }
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (rowzero.contains(i) || colzero.contains(j)) {
                    matrix[i][j] = 0;
                }
            }
        }
    }

    public static void main(String[] args) {
        int[][] matrix = {
            {1, 2, 3},
            {4, 0, 6},
            {7, 8, 9}
        };
        System.out.println("Input matrix:");
        for (int[] row : matrix) {
            System.out.println(java.util.Arrays.toString(row));
        }
        SetMatrixZeroes solver = new SetMatrixZeroes();
        solver.setZeroes(matrix);
        System.out.println("\nOutput matrix after setZeroes:");
        for (int[] row : matrix) {
            System.out.println(java.util.Arrays.toString(row));
        }
    }
}

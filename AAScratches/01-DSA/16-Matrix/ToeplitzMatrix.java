// https://leetcode.com/problems/toeplitz-matrix/
/**
 * Checks if the given matrix is a Toeplitz matrix.
 * <p>
 * A matrix is Toeplitz if every diagonal from top-left to bottom-right has the same element.
 */
// Method to check if the given matrix is Toeplitz
class ToeplitzMatrix {

    // Method to check if the given matrix is Toeplitz
    public static boolean isToeplitzMatrix(int[][] matrix) {
        // Traverse through the matrix except the last row and column
        // matrix.length - 1  ,  matrix[0].length - 1
        for (int i = 0; i < matrix.length - 1; i++) {
            for (int j = 0; j < matrix[0].length - 1; j++) {
                // Check if the current element is equal to the next diagonal element
                if (matrix[i][j] != matrix[i + 1][j + 1]) {
                    return false;
                }
            }
        }
        // returning true
        return true;
    }

    // Main method to demonstrate the functionality
    public static void main(String[] args) {
        int[][] matrix1 = {
                {1, 2, 3, 4},
                {5, 1, 2, 3},
                {9, 5, 1, 2}
        };

        int[][] matrix2 = {
                {1, 2},
                {2, 2}
        };

        System.out.println("Matrix 1 is Toeplitz: " + isToeplitzMatrix(matrix1)); // Should return true
        System.out.println("Matrix 2 is Toeplitz: " + isToeplitzMatrix(matrix2)); // Should return false
    }
}

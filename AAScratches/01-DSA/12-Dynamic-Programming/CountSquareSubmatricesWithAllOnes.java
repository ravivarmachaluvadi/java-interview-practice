/**
 * Problem: Count the total number of square submatrices that contain only 1s in a binary matrix.
 *
 * Approach: Dynamic programming – for each cell (i,j) with value 1, compute the size of the largest
 * square ending at that cell as one plus the minimum of its top, left, and top-left neighbors.
 * Sum all computed sizes to get the total count. The input matrix is updated in place to store these sizes.
 *
 * Time Complexity: O(rows × cols) – each cell processed a constant number of times.
 * Space Complexity: O(1) additional space (in‑place DP), aside from the output integer.
 */
class CountSquareSubmatricesWithAllOnes {
    public int countSquares(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int count = 0;

        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                if (matrix[i][j] == 1) {
                    // i, j as i,j not as i,i or j,j or j,i
                    matrix[i][j] = Math.min(matrix[i - 1][j], Math.min(matrix[i][j - 1], matrix[i - 1][j - 1])) + 1;
                }
            }
        }
        for (int i = 0; i < rows; i++) for (int j = 0; j < cols; j++) count += matrix[i][j];

        return count;
    }

    public static void main(String[] args) {
        CountSquareSubmatricesWithAllOnes solution = new CountSquareSubmatricesWithAllOnes();
        int[][] matrix1 = {
                {0, 1, 1, 1},
                {1, 1, 1, 1},
                {0, 1, 1, 1}
        };
        System.out.println(solution.countSquares(matrix1)); // Output: 15

        int[][] matrix2 = {
                {1, 0, 1},
                {1, 1, 0},
                {1, 1, 0}
        };
        System.out.println(solution.countSquares(matrix2)); // Output: 7
    }
}
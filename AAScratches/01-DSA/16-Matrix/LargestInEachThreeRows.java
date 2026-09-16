// LeetCode Link: https://leetcode.com/problems/find-largest-value-in-each-row-of-binary-tree/
class LargestInEachThreeRows {

    public int[] findLargestInThreeRows(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[] result = new int[cols];  // Array to store largest values for each column

        // Iterate through the matrix in sets of 3 rows
        for (int i = 0; i < rows - 2; i += 3) {
            for (int j = 0; j < cols; j++) {
                // Find the maximum value in the jth column across the 3 rows
                int maxInCurrentRowPlusInNextTwoRowsOfSameColumn = Math.max(matrix[i][j], Math.max(matrix[i + 1][j], matrix[i + 2][j]));
                result[j] = Math.max(result[j], maxInCurrentRowPlusInNextTwoRowsOfSameColumn);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        LargestInEachThreeRows solution = new LargestInEachThreeRows();
        int[][] matrix = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9},
                {10, 11, 12},
                {13, 14, 15},
                {16, 17, 18}
        };

        int[] largestValues = solution.findLargestInThreeRows(matrix);

        System.out.println("Largest values in each set of three rows for each column:");
        for (int value : largestValues) {
            System.out.print(value + " ");
        }
        // Output should be the largest values in each column
    }
}

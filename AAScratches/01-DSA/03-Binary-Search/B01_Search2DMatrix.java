/**
 * Problem: Search for a target integer in a 2D matrix where each row is sorted
 * ascending left-to-right and the first integer of each row is greater than the last
 * integer of the previous row.
 *
 * Approach:
 * 1) Treat the matrix as a flattened sorted array and perform binary search on indices.
 * 2) Convert a linear index to (row, col) via division and modulo.
 * 3) Alternatively, start from top-right corner and move left or down based on comparison.
 *
 * Time Complexity: O(log(m*n)) for the binary search version; O(m+n) for the
 * top-right traversal version.
 * Space Complexity: O(1) – only a few integer variables are used.
 */
class Search2DMatrix {

    public static boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }
        int rows = matrix.length;
        int cols = matrix[0].length;

        int left = 0;
        int right = rows * cols - 1;
        // no of cols = record or row size
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midValue = matrix[mid / cols][mid % cols];

            if (midValue == target) {
                return true;
            } else if (midValue < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return false;
    }

    public static boolean searchMatrixV2(int[][] matrix, int target) {
        int i = 0;
        int j = matrix[0].length - 1;
        while (i < matrix.length && j > -1) {
            int temp = matrix[i][j];
            if (target == temp) {
                return true;
            } else if (temp > target) {
                j--;
            } else {
                i++;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[][] matrix = {
                {1, 3, 5, 7},
                {10, 11, 16, 20},
                {23, 30, 34, 60}
        };
        int target = 3;

        boolean result = searchMatrix(matrix, target);
        System.out.println("Is target " + target + " found in the matrix? " + result);
    }
}

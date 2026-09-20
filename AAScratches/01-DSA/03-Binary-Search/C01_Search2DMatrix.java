/*
 * =====================================================================
 *  Search a 2D Matrix                         LeetCode 74 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an m x n int matrix where each row is sorted ascending and the first element of
 *   every row is greater than the last element of the previous row, return whether target
 *   is present. Expected time is O(log(m * n)).
 *
 * EXAMPLE
 *   matrix = [[1, 3, 5, 7], [10, 11, 16, 20], [23, 30, 34, 60]], target = 3   ->  true
 *   same matrix, target = 13  ->  false   (falls between 11 and 16)
 *   same matrix, target = 60  ->  true    (last element: index rows*cols-1)
 *   matrix = [[5]], target = 5  ->  true  (single cell)
 *   matrix = [], target = 1     ->  false (empty input)
 *
 * APPROACH  (flatten the 2D index to 1D, then plain binary search)
 *   1. Because rows are sorted and row boundaries are ordered, reading the matrix row by row
 *      gives one sorted list of length rows * cols. Do not build it; only pretend it exists.
 *   2. Binary search over linear index k in [0, rows*cols-1].
 *   3. Map k back to a cell: row = k / cols, col = k % cols.
 *   4. Compare matrix[row][col] with target and move left/right exactly as in a 1D search.
 *
 *   Second method (staircase, from the top-right corner):
 *   1. Start at (0, cols-1). Everything left in the row is smaller; everything below is larger.
 *   2. If the cell > target, move left (col--). If cell < target, move down (row++).
 *   3. Stops after at most rows + cols steps. Works even when only rows and columns are
 *      sorted (LC 240), where the flattening trick does NOT apply.
 *
 * KEY INSIGHT
 *   A row-major matrix with ordered row boundaries IS a sorted array with a different
 *   addressing scheme. The only new idea over A01_LowerAndUpperBounds is the div/mod mapping k ->
 *   (k / cols, k % cols). Whenever a structure is "sorted if you read it in some fixed order", you
 *   can binary search the reading order and translate indices on the fly.
 *
 * COMPLEXITY
 *   Time  O(log(m * n))  binary search  |  O(m + n) staircase
 *   Space O(1)           both methods use a few ints
 *
 * INTERVIEW FOLLOW-UPS
 *   - LC 240 (rows and columns sorted, but row boundaries NOT ordered): flattening breaks,
 *     use the staircase method.
 *   - Two-step alternative: binary search rows by first element, then binary search the row.
 *     Same O(log m + log n) bound, more code.
 *   - Return the (row, col) position instead of a boolean.
 *
 * RUN
 *   main() runs 5 cases (present, absent between rows, last cell, single cell, empty) through
 *   BOTH methods and prints actual vs expected.
 */
class Search2DMatrix {

    /** O(log(m*n)): treat the matrix as one sorted array of length rows * cols. */
    public static boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }
        int rows = matrix.length;
        int cols = matrix[0].length;

        int left = 0;
        int right = rows * cols - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            // Linear index -> cell: each row holds `cols` items, so row = mid / cols.
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

    /** O(m+n): staircase walk from the top-right corner (also valid for LC 240). */
    public static boolean searchMatrixStaircase(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }
        int row = 0;
        int col = matrix[0].length - 1;
        while (row < matrix.length && col >= 0) {
            int current = matrix[row][col];
            if (current == target) {
                return true;
            } else if (current > target) {
                col--;   // everything below in this column is even larger
            } else {
                row++;   // everything to the left in this row is even smaller
            }
        }
        return false;
    }

    private static void print(String label, int[][] matrix, int target, boolean expected) {
        System.out.println(label + ": binary " + searchMatrix(matrix, target)
                + ", staircase " + searchMatrixStaircase(matrix, target)
                + "   expected " + expected + " (both)");
    }

    public static void main(String[] args) {
        int[][] matrix = {
                {1, 3, 5, 7},
                {10, 11, 16, 20},
                {23, 30, 34, 60}
        };
        print("case 1 present          ", matrix, 3, true);
        print("case 2 between rows     ", matrix, 13, false);
        print("case 3 last cell        ", matrix, 60, true);
        print("case 4 single cell      ", new int[][]{{5}}, 5, true);
        print("case 5 empty matrix     ", new int[][]{}, 1, false);
    }
}

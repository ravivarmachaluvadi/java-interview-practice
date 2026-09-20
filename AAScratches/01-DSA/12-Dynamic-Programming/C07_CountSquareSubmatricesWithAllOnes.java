/*
 * =====================================================================
 *  Count Square Submatrices With All Ones      LeetCode 1277 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a matrix of 0s and 1s, count every square submatrix made entirely of 1s.
 *   Squares of all sizes count, and squares that overlap are counted separately,
 *   so a 2x2 block of 1s contributes four 1x1 squares plus one 2x2 square = 5.
 *
 * EXAMPLE
 *   [[0,1,1,1],                 -> 15   (10 squares of size 1, 4 of size 2, 1 of size 3)
 *    [1,1,1,1],
 *    [0,1,1,1]]
 *   [[1,0,1],[1,1,0],[1,1,0]]   -> 7    (6 of size 1, 1 of size 2)
 *   [[0,0,0]]                   -> 0    no 1s at all
 *
 * APPROACH  (grid DP where the state is a SIZE, not a path cost)
 *   1. Let dp[i][j] = the side length of the largest all-ones square whose BOTTOM-RIGHT
 *      corner is cell (i, j). If the cell is 0 then dp[i][j] = 0.
 *   2. Row 0 and column 0 have no room to grow, so dp there is just the cell value.
 *   3. For any other cell holding a 1:
 *        dp[i][j] = 1 + min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])
 *      A square of side k ending here needs squares of side k-1 ending above, to the
 *      left, and diagonally up-left; the weakest of those three caps the growth.
 *   4. The answer is the sum of all dp values, because a cell with dp = k is the
 *      bottom-right corner of exactly k squares (sides 1, 2, ... k).
 *
 * KEY INSIGHT
 *   Summing dp is the whole trick: you never enumerate squares, you count corners.
 *   Each cell reports how many squares end at it, and every square has exactly one
 *   bottom-right corner, so no square is counted twice and none is missed. The same
 *   min-of-three-neighbours recurrence solves Maximal Square (LeetCode 221) - there you
 *   take max(dp)^2 instead of sum(dp). Recognise it whenever a question asks about
 *   solid squares in a binary grid.
 *
 * COMPLEXITY
 *   Time  O(rows * cols)  each cell is relaxed once from three already-final neighbours
 *   Space countSquares          O(1) extra, but it OVERWRITES the caller's matrix
 *         countSquaresRowRolling O(cols), keeps only two rows and leaves the input intact
 *
 * INTERVIEW FOLLOW-UPS
 *   - Maximal Square (LeetCode 221): same dp, answer is max(dp) squared.
 *   - Count rectangles instead of squares - why does this recurrence stop working?
 *   - Largest all-ones square under a query "which cells may be flipped once"?
 *   - Is mutating the input acceptable? Show the O(cols) rolling version if not.
 *
 * RUN
 *   main() runs 3 cases (typical, zeros-blocking, all-zeros edge) through BOTH methods
 *   and prints actual vs expected. Each method gets its own copy of the matrix because
 *   countSquares is destructive.
 */

import java.util.Arrays;

class CountSquareSubmatricesWithAllOnes {

    /**
     * In-place DP: the matrix itself becomes the dp table.
     * Destructive - the caller's matrix holds square sizes afterwards, not the original 0/1s.
     */
    public int countSquares(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        // Row 0 and column 0 already hold the right answer (0 or 1), so start at (1, 1).
        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                if (matrix[i][j] == 1) {
                    // Mind the indices: up is (i-1, j), left is (i, j-1), diagonal is (i-1, j-1).
                    int up = matrix[i - 1][j];
                    int left = matrix[i][j - 1];
                    int diagonal = matrix[i - 1][j - 1];
                    matrix[i][j] = 1 + Math.min(up, Math.min(left, diagonal));
                }
                // A 0 cell stays 0: no square can end on an empty cell.
            }
        }

        // A cell with value k is the bottom-right corner of exactly k squares.
        int count = 0;
        for (int[] row : matrix) {
            for (int size : row) {
                count += size;
            }
        }
        return count;
    }

    /**
     * Same recurrence, but keeps only the previous row, so the input matrix is untouched.
     * This is the version to offer when the interviewer says "don't modify the input".
     */
    public int countSquaresRowRolling(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[] previous = new int[cols];   // dp values of row i - 1
        int[] current = new int[cols];    // dp values of row i, being filled left to right
        int count = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == 0) {
                    current[j] = 0;
                } else if (i == 0 || j == 0) {
                    current[j] = 1;       // edge cell: no room for a bigger square
                } else {
                    int smallestNeighbour =
                            Math.min(previous[j], Math.min(current[j - 1], previous[j - 1]));
                    current[j] = 1 + smallestNeighbour;
                }
                count += current[j];
            }
            // Swap the buffers: this row becomes the previous row for the next pass.
            int[] swap = previous;
            previous = current;
            current = swap;
        }
        return count;
    }

    private static int[][] copyOf(int[][] matrix) {
        int[][] copy = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = matrix[i].clone();
        }
        return copy;
    }

    private static void runCase(CountSquareSubmatricesWithAllOnes solution,
                                String label, int[][] matrix, int expected) {
        int inPlace = solution.countSquares(copyOf(matrix));
        int rolling = solution.countSquaresRowRolling(copyOf(matrix));
        System.out.println(label + " " + Arrays.deepToString(matrix));
        System.out.println("  in-place   : " + inPlace + "   expected " + expected);
        System.out.println("  row-rolling: " + rolling + "   expected " + expected);
    }

    public static void main(String[] args) {
        CountSquareSubmatricesWithAllOnes solution = new CountSquareSubmatricesWithAllOnes();

        // Case 1 - typical: a wide block of 1s with one 3x3 square inside it
        runCase(solution, "case 1", new int[][]{
                {0, 1, 1, 1},
                {1, 1, 1, 1},
                {0, 1, 1, 1}}, 15);

        // Case 2 - tricky: zeros cut the grid so only one 2x2 square survives
        runCase(solution, "case 2", new int[][]{
                {1, 0, 1},
                {1, 1, 0},
                {1, 1, 0}}, 7);

        // Case 3 - edge: no 1s at all, and a single row so the DP never leaves row 0
        runCase(solution, "case 3", new int[][]{{0, 0, 0}}, 0);
    }
}

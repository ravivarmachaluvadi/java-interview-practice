/*
 * =====================================================================
 *  Toeplitz Matrix                                   LeetCode 766 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an m x n matrix, return true if it is Toeplitz: every diagonal running
 *   from top-left to bottom-right holds a single repeated value. The matrix does
 *   not have to be square, and a matrix with one row or one column is trivially
 *   Toeplitz because no cell has a down-right neighbour.
 *
 * EXAMPLE
 *   [[1,2,3,4],[5,1,2,3],[9,5,1,2]]  ->  true    each diagonal is all-equal
 *   [[1,2],[2,2]]                    ->  false   (0,0)=1 but its diagonal neighbour (1,1)=2
 *   [[7,8,9]]                        ->  true    single row, nothing to compare
 *
 * APPROACH  (compare every cell with its down-right neighbour)
 *   1. Walk every cell (i, j) that has a neighbour below-right, i.e. i < m-1 and j < n-1.
 *   2. If matrix[i][j] != matrix[i+1][j+1], two cells on the same diagonal differ, so
 *      return false immediately.
 *   3. If the whole scan passes, every diagonal is internally consistent: equality is
 *      transitive, so checking consecutive pairs is enough to prove the whole diagonal.
 *
 * KEY INSIGHT
 *   Cells sit on the same top-left-to-bottom-right diagonal exactly when i - j is the
 *   same for both. Moving down-right adds 1 to both i and j, so i - j does not change.
 *   That single fact - "diagonal identity = i - j" - is the indexing reflex behind
 *   diagonal traversal, anti-diagonals (i + j), and N-Queens diagonal conflict checks.
 *
 * COMPLEXITY
 *   Time  O(m * n)  each cell is compared against one neighbour at most once
 *   Space O(1)      only loop counters; nothing is buffered
 *
 * INTERVIEW FOLLOW-UPS
 *   - Follow-up in the original problem: if you can only load one row into memory at a
 *     time, keep the previous row and check prev[j] == curr[j+1] as you stream.
 *   - Group cells by diagonal using a HashMap keyed on i - j (needed when you must
 *     report WHICH diagonal broke, not just a boolean).
 *   - Anti-diagonals use i + j instead - the same idea with the other sign.
 *   - Rotate/transpose questions (see A01) lean on the same index arithmetic.
 *
 * RUN
 *   main() runs 4 cases (true, false, single row edge, single column edge) and prints
 *   actual vs expected.
 */

class ToeplitzMatrix {

    /** True when every top-left to bottom-right diagonal contains a single repeated value. */
    public static boolean isToeplitzMatrix(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        // stop one short both ways: the last row and last column have no down-right neighbour
        for (int i = 0; i < rows - 1; i++) {
            for (int j = 0; j < cols - 1; j++) {
                if (matrix[i][j] != matrix[i + 1][j + 1]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] toeplitz = {
                {1, 2, 3, 4},
                {5, 1, 2, 3},
                {9, 5, 1, 2}
        };
        int[][] notToeplitz = {
                {1, 2},
                {2, 2}
        };
        int[][] singleRow = {{7, 8, 9}};
        int[][] singleColumn = {{7}, {8}, {9}};

        print("case 1 (3x4 Toeplitz)", isToeplitzMatrix(toeplitz), true);
        print("case 2 (2x2 broken diagonal)", isToeplitzMatrix(notToeplitz), false);
        print("case 3 (single row edge)", isToeplitzMatrix(singleRow), true);
        print("case 4 (single column edge)", isToeplitzMatrix(singleColumn), true);
    }
}

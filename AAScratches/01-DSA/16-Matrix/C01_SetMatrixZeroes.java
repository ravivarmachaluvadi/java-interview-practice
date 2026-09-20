/*
 * =====================================================================
 *  Set Matrix Zeroes                                LeetCode 73 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an m x n integer matrix, if any cell is 0 then set that cell's ENTIRE row
 *   and ENTIRE column to 0. The change must happen in place. The trap is that zeroing
 *   as you scan creates new zeros, which would then zero further rows and columns and
 *   eventually wipe the whole matrix.
 *
 * EXAMPLE
 *   [[1,1,1],[1,0,1],[1,1,1]]        ->  [[1,0,1],[0,0,0],[1,0,1]]
 *   [[0,1,2,0],[3,4,5,2],[1,3,1,5]]  ->  [[0,0,0,0],[0,4,5,0],[0,3,1,0]]
 *   [[1,2],[3,4]]                    ->  unchanged (no zero anywhere)
 *   [[0]]                            ->  [[0]]   (single cell edge)
 *
 * APPROACH  (record first, apply second - two passes)
 *   1. Pass 1 (record): scan every cell. On a 0 at (i, j), remember row i in a set and
 *      column j in a set. Change nothing yet.
 *   2. Pass 2 (apply): scan every cell again and write 0 wherever its row OR its column
 *      was recorded.
 *   3. Separating the two passes is what stops a zero written in pass 2 from being read
 *      as an original input zero.
 *
 *   setZeroesConstantSpace() is the O(1)-space follow-up interviewers push for:
 *   the same record-then-apply idea, but row 0 and column 0 serve as the two marker
 *   arrays instead of hash sets. Cell (0,0) is shared by both markers, so one extra
 *   boolean tracks whether column 0 itself originally contained a zero, and the first
 *   row and first column are rewritten LAST, after every inner cell has read them.
 *
 * KEY INSIGHT
 *   Record then apply. Whenever an in-place update changes the very data the scan is
 *   reading, split it into a read-only pass that collects marks and a write pass that
 *   applies them. The O(1)-space version then makes the classic space trade: store the
 *   marks inside the input itself, and pay for it with one flag for the overlapping cell
 *   and a strict ordering rule (inner cells first, border last).
 *
 * COMPLEXITY
 *   setZeroes             Time O(m*n)  two full passes     Space O(m+n)  two hash sets
 *   setZeroesConstantSpace Time O(m*n)  two full passes     Space O(1)    one boolean flag
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it with O(1) extra space - the expected follow-up, answered below.
 *   - Why zero the border last? Inner cells still need the markers while they are read.
 *   - What if the matrix is huge / distributed? Emit the zero row and column ids, then
 *     broadcast the marks; the marks are far smaller than the matrix.
 *   - Game of Life (LeetCode 289) is the same trap: encode the new state in spare bits
 *     so a cell's old value survives until every neighbour has read it.
 *
 * RUN
 *   main() runs 4 cases (LeetCode sample, first-row/first-column zeros, no zeros, 1x1
 *   zero) through BOTH methods on independent copies and prints actual vs expected.
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class SetMatrixZeroes {

    /** O(m + n) space: remember the zero rows and columns in two sets, then apply them. */
    public static void setZeroes(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        Set<Integer> zeroRows = new HashSet<>();
        Set<Integer> zeroCols = new HashSet<>();

        // pass 1: record only - writing here would create zeros the same scan reads back
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == 0) {
                    zeroRows.add(i);
                    zeroCols.add(j);
                }
            }
        }

        // pass 2: apply
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (zeroRows.contains(i) || zeroCols.contains(j)) {
                    matrix[i][j] = 0;
                }
            }
        }
    }

    /** O(1) space: row 0 and column 0 hold the marks; one boolean covers their overlap. */
    public static void setZeroesConstantSpace(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean firstColumnHasZero = false;

        // pass 1: mark. matrix[i][0] marks row i, matrix[0][j] marks column j.
        for (int i = 0; i < rows; i++) {
            if (matrix[i][0] == 0) {
                // column 0's own marker would be overwritten by row markers, so keep it apart
                firstColumnHasZero = true;
            }
            for (int j = 1; j < cols; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0;
                    matrix[0][j] = 0;
                }
            }
        }

        // pass 2: apply to the inner cells only, while the markers are still readable
        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) {
                    matrix[i][j] = 0;
                }
            }
        }

        // pass 3: now it is safe to overwrite the marker row and marker column themselves
        if (matrix[0][0] == 0) {
            Arrays.fill(matrix[0], 0);
        }
        if (firstColumnHasZero) {
            for (int i = 0; i < rows; i++) {
                matrix[i][0] = 0;
            }
        }
    }

    private static int[][] copyOf(int[][] matrix) {
        int[][] copy = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = matrix[i].clone();
        }
        return copy;
    }

    private static void print(String label, int[][] input, String expected) {
        int[][] withSets = copyOf(input);
        int[][] inPlace = copyOf(input);
        setZeroes(withSets);
        setZeroesConstantSpace(inPlace);
        System.out.println(label + ":");
        System.out.println("    sets : " + Arrays.deepToString(withSets)
                + "   expected " + expected);
        System.out.println("    O(1) : " + Arrays.deepToString(inPlace)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (single interior zero)",
                new int[][]{{1, 1, 1}, {1, 0, 1}, {1, 1, 1}},
                "[[1, 0, 1], [0, 0, 0], [1, 0, 1]]");

        print("case 2 (tricky: zeros in row 0 and column 0)",
                new int[][]{{0, 1, 2, 0}, {3, 4, 5, 2}, {1, 3, 1, 5}},
                "[[0, 0, 0, 0], [0, 4, 5, 0], [0, 3, 1, 0]]");

        print("case 3 (edge: no zeros, matrix unchanged)",
                new int[][]{{1, 2}, {3, 4}},
                "[[1, 2], [3, 4]]");

        print("case 4 (edge: 1x1 zero)",
                new int[][]{{0}},
                "[[0]]");
    }
}

/*
 * =====================================================================
 *  P010 Matrix Traversal and In-Place Tricks   Canonical LC 54 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 54, Spiral Matrix)
 *   Given an m x n matrix, return all of its elements in clockwise spiral order, starting
 *   at the top-left corner.
 *
 * EXAMPLE
 *   [[1,2,3],[4,5,6],[7,8,9]]            ->  [1,2,3,6,9,8,7,4,5]
 *   [[1,2,3,4],[5,6,7,8],[9,10,11,12]]   ->  [1,2,3,4,8,12,11,10,9,5,6,7]
 *   [[1],[2],[3]]                        ->  [1,2,3]     the trap: a single column
 *
 * RECOGNIZE WHEN
 *   - Walk a grid in a non-row-major order: spiral, diagonals, layers.
 *   - Rotate / transpose / mirror a square matrix in place.
 *   - Update every cell from its neighbours "simultaneously" with O(1) extra space.
 *   Not this if: you search connected regions of cells -> P065_GridFloodFill; you need
 *   path counts or costs over the grid -> P089_GridDp.
 *
 * TEMPLATE
 *   spiral:   top, bottom, left, right bounds; walk top row, right column, bottom row,
 *             left column; shrink the bound you just walked; re-check top <= bottom and
 *             left <= right before the bottom row and the left column
 *   rotate:   transpose (swap a[i][j], a[j][i] for j > i), then reverse every row
 *   in-place simultaneous update: encode old and new state in one cell (bits or markers)
 *
 * APPROACH
 *   1. Keep four bounds. Each lap prints one ring of the matrix.
 *   2. After printing a side, move that bound inward.
 *   3. The bottom row and left column only exist if the ring is still two-dimensional.
 *
 * KEY INSIGHT
 *   Grid problems become simple once you name the invariant: for spiral it is the four
 *   shrinking bounds; for rotation it is "rotate = transpose + mirror"; for in-place
 *   updates it is "store the new value somewhere the old value is still readable".
 *
 * COMPLEXITY
 *   Time O(m * n) for all of them; space O(1) besides the output.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 48   Rotate Image              transpose, then reverse each row
 *   [coded] LC 73   Set Matrix Zeroes         first row and column hold the markers; a
 *                                             separate flag remembers the first column
 *   [coded] LC 289  Game of Life              bit 1 = next state, bit 0 = current state
 *   [coded] LC 498  Diagonal Traverse         cells with equal r + c share a diagonal;
 *                                             alternate the direction per diagonal
 *           LC 59   Spiral Matrix II          the same four bounds, writing 1..n^2
 *           LC 867  Transpose Matrix          new n x m array (not square, so not in place)
 *           LC 1914 Cyclically Rotate a Grid  peel each layer into a list, rotate, write back
 *
 * PITFALLS
 *   - Spiral: without the re-check, a single row or column is printed twice.
 *   - Rotate counter-clockwise = transpose + reverse each COLUMN (or reverse rows first).
 *   - Set zeroes: clear cells from the bottom-right, or the markers get wiped too early.
 *
 * DEEP DIVE
 *   C02_SpiralTraversalOfMatrix, A01_MatrixRotate90Degree, C01_SetMatrixZeroes,
 *   C03_DiagonalTraverse (all in 16-Matrix)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MatrixInPlace {

    // Canonical LC 54.
    static List<Integer> spiralOrder(int[][] m) {
        List<Integer> out = new ArrayList<>();
        int top = 0;
        int bottom = m.length - 1;
        int left = 0;
        int right = m[0].length - 1;
        while (top <= bottom && left <= right) {
            for (int c = left; c <= right; c++) {
                out.add(m[top][c]);
            }
            top++;
            for (int r = top; r <= bottom; r++) {
                out.add(m[r][right]);
            }
            right--;
            if (top <= bottom) {
                for (int c = right; c >= left; c--) {
                    out.add(m[bottom][c]);
                }
                bottom--;
            }
            if (left <= right) {
                for (int r = bottom; r >= top; r--) {
                    out.add(m[r][left]);
                }
                left++;
            }
        }
        return out;
    }

    // LC 48: clockwise rotation = transpose + reverse each row.
    static int[][] rotate(int[][] m) {
        int n = m.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int t = m[i][j];
                m[i][j] = m[j][i];
                m[j][i] = t;
            }
        }
        for (int[] row : m) {
            for (int l = 0, r = n - 1; l < r; l++, r--) {
                int t = row[l];
                row[l] = row[r];
                row[r] = t;
            }
        }
        return m;
    }

    // LC 73: row 0 and column 0 become the "this line must be zeroed" markers.
    static int[][] setZeroes(int[][] m) {
        int rows = m.length;
        int cols = m[0].length;
        boolean firstColZero = false;
        for (int r = 0; r < rows; r++) {
            if (m[r][0] == 0) {
                firstColZero = true;
            }
            for (int c = 1; c < cols; c++) {
                if (m[r][c] == 0) {
                    m[r][0] = 0;
                    m[0][c] = 0;
                }
            }
        }
        for (int r = rows - 1; r >= 0; r--) {          // bottom-up keeps row 0 markers intact
            for (int c = cols - 1; c >= 1; c--) {
                if (m[r][0] == 0 || m[0][c] == 0) {
                    m[r][c] = 0;
                }
            }
            if (firstColZero) {
                m[r][0] = 0;
            }
        }
        return m;
    }

    // LC 289: bit 0 holds the current state, bit 1 the next one; shift at the end.
    static int[][] gameOfLife(int[][] b) {
        int rows = b.length;
        int cols = b[0].length;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int live = 0;
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int nr = r + dr;
                        int nc = c + dc;
                        if ((dr != 0 || dc != 0) && nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                            live += b[nr][nc] & 1;
                        }
                    }
                }
                boolean alive = (b[r][c] & 1) == 1;
                if ((alive && (live == 2 || live == 3)) || (!alive && live == 3)) {
                    b[r][c] |= 2;
                }
            }
        }
        for (int[] row : b) {
            for (int c = 0; c < cols; c++) {
                row[c] >>= 1;
            }
        }
        return b;
    }

    // LC 498: diagonal d holds cells with r + c == d; even d goes up-right, odd goes down-left.
    static int[] findDiagonalOrder(int[][] m) {
        int rows = m.length;
        int cols = m[0].length;
        int[] out = new int[rows * cols];
        int k = 0;
        for (int d = 0; d < rows + cols - 1; d++) {
            if (d % 2 == 0) {
                int r = Math.min(d, rows - 1);
                for (; r >= 0 && d - r < cols; r--) {
                    out[k++] = m[r][d - r];
                }
            } else {
                int c = Math.min(d, cols - 1);
                for (; c >= 0 && d - c < rows; c--) {
                    out[k++] = m[d - c][c];
                }
            }
        }
        return out;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 54 3x3", spiralOrder(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}),
                "[1, 2, 3, 6, 9, 8, 7, 4, 5]");
        check("LC 54 3x4", spiralOrder(new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}}),
                "[1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7]");
        check("LC 54 single column", spiralOrder(new int[][]{{1}, {2}, {3}}), "[1, 2, 3]");
        check("LC 54 single row", spiralOrder(new int[][]{{1, 2, 3}}), "[1, 2, 3]");

        check("LC 48 3x3",
                Arrays.deepToString(rotate(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})),
                "[[7, 4, 1], [8, 5, 2], [9, 6, 3]]");
        check("LC 48 1x1", Arrays.deepToString(rotate(new int[][]{{5}})), "[[5]]");

        check("LC 73 centre zero",
                Arrays.deepToString(setZeroes(new int[][]{{1, 1, 1}, {1, 0, 1}, {1, 1, 1}})),
                "[[1, 0, 1], [0, 0, 0], [1, 0, 1]]");
        int[][] rowZero = {{0, 1, 2, 0}, {3, 4, 5, 2}, {1, 3, 1, 5}};
        check("LC 73 zeros in row 0", Arrays.deepToString(setZeroes(rowZero)),
                "[[0, 0, 0, 0], [0, 4, 5, 0], [0, 3, 1, 0]]");

        int[][] glider = {{0, 1, 0}, {0, 0, 1}, {1, 1, 1}, {0, 0, 0}};
        check("LC 289 glider", Arrays.deepToString(gameOfLife(glider)),
                "[[0, 0, 0], [1, 0, 1], [0, 1, 1], [0, 1, 0]]");
        check("LC 289 2x2 block stays",
                Arrays.deepToString(gameOfLife(new int[][]{{1, 1}, {1, 0}})),
                "[[1, 1], [1, 1]]");

        check("LC 498 3x3",
                Arrays.toString(findDiagonalOrder(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})),
                "[1, 2, 4, 7, 5, 3, 6, 8, 9]");
        check("LC 498 2x2",
                Arrays.toString(findDiagonalOrder(new int[][]{{1, 2}, {3, 4}})), "[1, 2, 3, 4]");
    }
}

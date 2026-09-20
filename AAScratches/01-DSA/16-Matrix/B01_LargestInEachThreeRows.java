/*
 * =====================================================================
 *  Largest Value Per Column, In Each Block Of Three Rows      Easy (not a LeetCode problem)
 * =====================================================================
 *
 * PROBLEM
 *   Given an m x n matrix, split the rows into consecutive blocks of three
 *   (rows 0-2, rows 3-5, ...) and, for each block, report the largest value in
 *   every column. Values may be negative, and the row count need not be a
 *   multiple of three - the final block may hold only one or two rows.
 *   Result: one int[] of length n per block.
 *
 * EXAMPLE
 *   [[1,2,3],[4,5,6],[7,8,9],[10,11,12],[13,14,15],[16,17,18]]
 *       ->  [[7, 8, 9], [16, 17, 18]]        two full blocks
 *   [[-5,-9],[-7,-2],[-3,-8],[-1,-4]]
 *       ->  [[-3, -2], [-1, -4]]             second block holds a single row
 *
 * APPROACH  (column-wise scan over row blocks)
 *   1. Walk the start row of each block: start = 0, 3, 6, ... while start < rows.
 *   2. The block ends at min(start + 3, rows) - the min is what keeps a partial
 *      final block of one or two rows from being dropped or from reading past the end.
 *   3. For each column j, seed the running maximum with the block's FIRST row value,
 *      matrix[start][j], then fold in the remaining rows of the block.
 *   4. Collect one result array per block.
 *
 * KEY INSIGHT
 *   Seed a running maximum from real data (the first element you will scan), never
 *   from a convenient literal like 0. Seeding with 0 silently returns 0 for any column
 *   that is entirely negative - a bug that passes every all-positive test you write.
 *   Integer.MIN_VALUE is the other safe seed when there is no natural first element.
 *   The same "block start, clamped block end" loop shape covers chunking, batching and
 *   fixed-window sliding problems.
 *
 * COMPLEXITY
 *   Time  O(m * n)  each cell is read exactly once
 *   Space O(m/3 * n) for the output; O(1) extra beyond what is returned
 *
 * INTERVIEW FOLLOW-UPS
 *   - Make the block size k a parameter instead of hard-coding 3.
 *   - Report the minimum per block too, or the row index that held the maximum.
 *   - Stream it: with rows arriving one at a time, hold only the current block's maxima.
 *   - Sliding window of three rows (overlapping blocks) instead of disjoint blocks.
 *
 * Fixed: the original seeded result[j] with 0, so an all-negative column reported 0;
 *        it looped while i < rows - 2, silently dropping a trailing partial block; and
 *        it folded every block into ONE array, so the per-three-rows answer was lost.
 *        largestPerColumn() below keeps that whole-matrix collapse as its own method.
 *        Also removed a copy-pasted LeetCode link that pointed at an unrelated tree problem.
 *
 * RUN
 *   main() runs 4 cases (two full blocks, negatives with a partial block, single row edge,
 *   whole-matrix column maxima) and prints actual vs expected.
 */

import java.util.Arrays;

class LargestInEachThreeRows {

    private static final int BLOCK_ROWS = 3;

    /** For each block of three consecutive rows, the largest value in every column. */
    public static int[][] findLargestInThreeRows(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        // ceiling division so a trailing partial block still gets a result row
        int blocks = (rows + BLOCK_ROWS - 1) / BLOCK_ROWS;
        int[][] result = new int[blocks][cols];

        int block = 0;
        for (int start = 0; start < rows; start += BLOCK_ROWS) {
            int end = Math.min(start + BLOCK_ROWS, rows);    // clamp so the last block may be short
            for (int j = 0; j < cols; j++) {
                int max = matrix[start][j];                  // seed from real data, not from 0
                for (int i = start + 1; i < end; i++) {
                    max = Math.max(max, matrix[i][j]);
                }
                result[block][j] = max;
            }
            block++;
        }
        return result;
    }

    /** The author's original shape: the largest value in each column across the WHOLE matrix. */
    public static int[] largestPerColumn(int[][] matrix) {
        int cols = matrix[0].length;
        int[] result = new int[cols];
        for (int j = 0; j < cols; j++) {
            int max = matrix[0][j];
            for (int[] row : matrix) {
                max = Math.max(max, row[j]);
            }
            result[j] = max;
        }
        return result;
    }

    private static void print(String label, Object actual, String expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] sixRows = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9},
                {10, 11, 12},
                {13, 14, 15},
                {16, 17, 18}
        };
        int[][] negativesWithPartialBlock = {
                {-5, -9},
                {-7, -2},
                {-3, -8},
                {-1, -4}
        };
        int[][] singleRow = {{-2, -4}};

        print("case 1 (6 rows, two full blocks)",
                Arrays.deepToString(findLargestInThreeRows(sixRows)),
                "[[7, 8, 9], [16, 17, 18]]");

        print("case 2 (negatives, 4th row is a partial block)",
                Arrays.deepToString(findLargestInThreeRows(negativesWithPartialBlock)),
                "[[-3, -2], [-1, -4]]");

        print("case 3 (single row edge)",
                Arrays.deepToString(findLargestInThreeRows(singleRow)),
                "[[-2, -4]]");

        print("case 4 (whole-matrix column maxima, negatives)",
                Arrays.toString(largestPerColumn(negativesWithPartialBlock)),
                "[-1, -2]");
    }
}

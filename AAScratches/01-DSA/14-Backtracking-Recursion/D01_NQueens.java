/*
 * =====================================================================
 *  N-Queens                                   LeetCode 51 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Place n queens on an n x n board so that no two attack each other: no shared row,
 *   no shared column, no shared diagonal. Return every distinct board, each drawn as n
 *   strings of 'Q' and '.'. Constraint 1 <= n <= 9, so exhaustive search is affordable.
 *
 * EXAMPLE
 *   n = 4 -> 2 boards, the first being   ..Q.
 *                                        Q...
 *                                        ...Q
 *                                        .Q..
 *   n = 1 -> 1 board ["Q"]      edge case
 *   n = 2 and n = 3 -> 0 boards, no placement exists
 *   n = 8 -> 92 boards, the classic answer
 *
 * APPROACH  (one queen per column, O(1) safety via three marker arrays)
 *   1. Recurse on the column. Level c places exactly one queen in column c, so "no two
 *      in a column" is true by construction and only rows and diagonals need checking.
 *   2. Base case: col == n means all n queens are placed - copy the board into the result.
 *   3. For each row in this column, the square is free when all three markers are 0:
 *        leftRow[row]                    - no queen already in that row
 *        lowerDiagonal[row + col]        - the "/" family: row + col is constant along it
 *        upperDiagonal[n - 1 + col - row]- the "\" family: col - row is constant, shifted
 *                                          by n - 1 so the index is never negative
 *   4. Place 'Q', set the three markers, recurse into column col + 1.
 *   5. On the way back clear the square and all three markers - the undo that frees the
 *      row and both diagonals for the next candidate row.
 *
 * KEY INSIGHT
 *   Every diagonal has a constant, so membership is an array lookup instead of a scan.
 *   row + col identifies one diagonal family, col - row the other, and adding n - 1
 *   shifts the range -(n-1)..(n-1) into 0..2n-2 so it can index a plain int array.
 *   That turns the O(n) "is this square attacked?" test into O(1), which is the whole
 *   difference between N-Queens and the naive M-Coloring style check. The same trick
 *   indexes Sudoku boxes and any "cells sharing an invariant" constraint.
 *
 * COMPLEXITY
 *   Time  O(n!)   n choices in column 0, at most n-1 survive into column 1, and so on;
 *                 constructing each finished board costs another O(n^2).
 *   Space O(n^2)  the board, plus O(n) for the three marker arrays and the recursion.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Only the COUNT is wanted -> N-Queens II: drop the board, return an int.
 *   - Only the FIRST solution -> return as soon as one is found, no full enumeration.
 *   - Replace the three int arrays with three bitmasks and iterate free squares with
 *     lowbit tricks: same algorithm, far faster constants.
 *   - Symmetry pruning: fix column 0 to the top half and mirror the results.
 *
 * RUN
 *   main() runs 4 cases (n=4 typical with the board printed, n=1 edge, n=3 impossible,
 *   n=8 count) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class NQueens {

    public static List<List<String>> solveNQueens(int n) {
        char[][] board = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = '.';
            }
        }
        List<List<String>> res = new ArrayList<>();
        int[] leftRow = new int[n];                 // rows already used
        int[] upperDiagonal = new int[2 * n - 1];   // "\" diagonals, keyed by n-1+col-row
        int[] lowerDiagonal = new int[2 * n - 1];   // "/" diagonals, keyed by row+col
        solve(0, board, res, leftRow, lowerDiagonal, upperDiagonal);
        return res;
    }

    /** Place one queen in column col, then recurse into the next column. */
    static void solve(int col,
                      char[][] board,
                      List<List<String>> res,
                      int[] leftRow,
                      int[] lowerDiagonal,
                      int[] upperDiagonal) {
        int length = board.length;
        if (col == length) {          // every column has a queen - a complete board
            res.add(construct(board));
            return;
        }

        for (int row = 0; row < length; row++) {
            // three O(1) lookups replace scanning the row and both diagonals
            if (leftRow[row] == 0
                    && lowerDiagonal[row + col] == 0
                    && upperDiagonal[length - 1 + col - row] == 0) {

                board[row][col] = 'Q';
                leftRow[row] = 1;
                lowerDiagonal[row + col] = 1;
                upperDiagonal[length - 1 + col - row] = 1;

                solve(col + 1, board, res, leftRow, lowerDiagonal, upperDiagonal);

                // undo all four marks so the next row starts from a clean state
                board[row][col] = '.';
                leftRow[row] = 0;
                lowerDiagonal[row + col] = 0;
                upperDiagonal[length - 1 + col - row] = 0;
            }
        }
    }

    /** Snapshot the mutable board as immutable strings, one per row. */
    static List<String> construct(char[][] board) {
        List<String> res = new LinkedList<>();
        for (char[] row : board) {
            res.add(new String(row));
        }
        return res;
    }

    private static void print(String label, Object actual, String expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        List<List<String>> four = solveNQueens(4);
        print("case 1 n=4 solution count", four.size(), "2");
        print("case 1 n=4 first board", four.get(0), "[..Q., Q..., ...Q, .Q..]");

        System.out.println("case 1 n=4 boards drawn out:");
        int index = 1;
        for (List<String> arrangement : four) {
            System.out.println("  arrangement " + index++);
            for (String row : arrangement) {
                System.out.println("  " + row);
            }
        }

        print("case 2 n=1 (edge)", solveNQueens(1), "[[Q]]");
        print("case 3 n=3 (edge: impossible)", solveNQueens(3), "[]");
        print("case 4 n=8 solution count (classic)", solveNQueens(8).size(), "92");
    }
}

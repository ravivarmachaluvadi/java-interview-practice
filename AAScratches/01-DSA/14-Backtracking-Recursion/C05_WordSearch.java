/*
 * =====================================================================
 *  Word Search                              LeetCode 79 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an m x n grid of characters and a word, return true if the word can be
 *   spelled by walking the grid one step at a time, moving only up / down / left / right.
 *   The same CELL may not be used twice inside one path, though the same letter may
 *   appear elsewhere in the grid and be reused by a different path.
 *
 * EXAMPLE
 *   board = A B C E        word = "ABCCED" -> true   (A,B,C then down, down-left, left)
 *           S F C S        word = "SEE"    -> true
 *           A D E E        word = "ABCB"   -> false  (the only B would have to be reused)
 *   board = [['A']],       word = "A"      -> true   (edge: 1x1 grid)
 *
 * APPROACH  (4-directional DFS with an in-place visited marker)
 *   1. Every cell is a possible starting point, so scan the whole grid and launch a DFS
 *      from each one with index 0.
 *   2. searchNext(row, col, index) answers "can the rest of the word, starting at
 *      word[index], be spelled starting from this cell?"
 *   3. Success base case FIRST: index == word.length() means every letter is placed.
 *   4. Failure bases: off the grid, wrong letter here, or this cell is already on the
 *      current path.
 *   5. Mark the cell with a sentinel '!' so the rest of this path cannot step back onto
 *      it, recurse into the four neighbours with index + 1, then RESTORE the letter.
 *   6. Return true if any of the four directions worked.
 *
 * KEY INSIGHT
 *   The visited set is the board itself. Overwriting the cell with a character that can
 *   never match the word is a free O(1) "on the current path" flag with no extra array,
 *   and restoring it on the way out is exactly what makes this backtracking rather than
 *   a one-shot flood fill - a cell blocked on this path must be free again for the next.
 *   Recognise the shape: mark, explore, restore. Sudoku, N-Queens and Rat-in-a-Maze
 *   are the same three lines around a different feasibility test.
 *
 * COMPLEXITY
 *   Time  O(m * n * 4^L)  L = word length; each of m*n starts explores a tree that
 *                         branches 4 ways (really 3, you never go straight back) to depth L.
 *   Space O(L)            recursion depth only - the visited marks live in the board.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Search many words at once -> Word Search II: put the words in a Trie and DFS once.
 *   - Allow the 8 diagonal moves too -> only the direction list changes.
 *   - Why not a boolean[][] visited? Works and is cleaner if the board must stay
 *     read-only or be shared across threads; the sentinel just saves the allocation.
 *   - Cheap early exit: if some letter's count in the word exceeds its count in the
 *     grid, answer false without any DFS.
 *
 * RUN
 *   main() runs 4 cases (typical, second word on the same board, reuse-is-illegal
 *   trick, 1x1 edge) and prints actual vs expected.
 */

class WordSearch {

    public boolean exist(char[][] board, String word) {
        if (board == null || board.length == 0 || board[0].length == 0 || word.isEmpty()) {
            return false;
        }
        int rows = board.length;
        int columns = board[0].length;

        // any cell could be the first letter, so try them all
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // searchNext re-checks the letter itself, so just launch from index 0
                if (searchNext(board, word, i, j, 0, rows, columns)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Can word[index..] be spelled starting at (row, col)? */
    private boolean searchNext(char[][] board, String word, int row, int col,
                               int index, int rows, int columns) {

        // every letter placed - success, and this must be tested before the bounds check
        if (index == word.length()) return true;

        // off the grid, wrong letter, or '!' meaning the cell is already on this path
        if (row < 0 || col < 0 || row == rows || col == columns
                || board[row][col] != word.charAt(index) || board[row][col] == '!') {
            return false;
        }

        // mark: '!' can never equal a letter of the word, so it blocks revisiting
        char letter = board[row][col];
        board[row][col] = '!';

        boolean found = searchNext(board, word, row - 1, col, index + 1, rows, columns)   // up
                || searchNext(board, word, row, col + 1, index + 1, rows, columns)        // right
                || searchNext(board, word, row + 1, col, index + 1, rows, columns)        // down
                || searchNext(board, word, row, col - 1, index + 1, rows, columns);       // left

        board[row][col] = letter;   // restore: the cell is free again for other paths

        return found;
    }

    private static char[][] classicBoard() {
        return new char[][]{
                {'A', 'B', 'C', 'E'},
                {'S', 'F', 'C', 'S'},
                {'A', 'D', 'E', 'E'}};
    }

    private static void print(String label, Object actual, String expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        WordSearch solver = new WordSearch();

        print("case 1 \"ABCCED\"",
                solver.exist(classicBoard(), "ABCCED"), "true");

        print("case 2 \"SEE\"",
                solver.exist(classicBoard(), "SEE"), "true");

        print("case 3 \"ABCB\" (tricky: would reuse the same B)",
                solver.exist(classicBoard(), "ABCB"), "false");

        print("case 4 1x1 grid, word \"A\" (edge)",
                solver.exist(new char[][]{{'A'}}, "A"), "true");
    }
}

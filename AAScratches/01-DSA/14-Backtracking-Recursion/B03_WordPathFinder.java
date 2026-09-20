/*
 * =====================================================================
 *  Word Path Finder (right / down only)                          Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a grid of characters and a target word, decide whether the word can
 *   be spelled by starting at any cell and then moving only RIGHT or DOWN, one
 *   cell per letter. Return the list of coordinates that spell it, or null if
 *   no such path exists. This is the warm-up version of Word Search (LC 79).
 *
 * EXAMPLE
 *   grid = A B C E     word = "ABCCE"  ->  [0,0] [0,1] [0,2] [1,2] [2,2]
 *          S F C S     word = "SEE"    ->  null   (would need to move up/left)
 *          A D E E     1x1 grid "A"    ->  [0,0]  (single-cell edge case)
 *
 * APPROACH  (grid DFS with only two moves)
 *   1. Try every cell as a starting point; the first successful path wins.
 *   2. dfs(idx, x, y) asks: can word[idx..] be spelled starting at cell (x,y)?
 *   3. If idx has passed the last letter the word is already complete -- return
 *      true BEFORE the bounds check, so running off the grid on the final step
 *      is harmless.
 *   4. Otherwise reject out-of-bounds cells and character mismatches.
 *   5. Record (x,y) on the path, then try right and then down for idx + 1.
 *   6. If both fail, remove (x,y) from the path (the undo) and return false.
 *
 * KEY INSIGHT
 *   Because the moves only ever increase x or y, the search can never revisit a
 *   cell -- so this version needs no visited set at all. That is the one thing
 *   that makes it easier than Word Search. The path list is shared mutable
 *   state, so every add on the way down needs a matching remove on the way out;
 *   the only exception is the winning path, which we return before unwinding.
 *
 * COMPLEXITY
 *   Time  O(n * m * 2^L)  each of the n*m starts branches 2 ways per letter,
 *                         L = word length (in practice far less, it stops early)
 *   Space O(L)            recursion stack plus the path list
 *
 * INTERVIEW FOLLOW-UPS
 *   - Allow all 4 directions: now you need a visited marker (see C05 WordSearch).
 *   - Return every path, not just the first one.
 *   - Count paths instead of listing them -- with right/down only this becomes
 *     a clean DP over the grid, no backtracking needed.
 *   - Search many words at once: build a Trie and prune dead prefixes (LC 212).
 *
 * RUN
 *   main() runs 3 cases (found, not found because of the move restriction,
 *   single-cell grid) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class WordPathFinder {

    // The only two legal moves: right (row+0, col+1) and down (row+1, col+0).
    private static final int[] DX = {0, 1};
    private static final int[] DY = {1, 0};

    public static List<int[]> findWord(char[][] grid, String word) {
        if (grid == null || grid.length == 0 || grid[0].length == 0 || word == null || word.isEmpty()) {
            return null;
        }

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                List<int[]> path = new ArrayList<>();
                if (dfs(grid, word, 0, row, col, path)) {
                    return path;
                }
            }
        }
        return null;
    }

    private static boolean dfs(char[][] grid, String word, int idx, int x, int y, List<int[]> path) {
        // Checked first on purpose: the last letter may sit on the grid edge, and
        // its "next" cell is off the grid. Success must win over the bounds test.
        if (idx == word.length()) {
            return true;
        }

        if (x < 0 || y < 0 || x >= grid.length || y >= grid[0].length) {
            return false;
        }

        if (grid[x][y] != word.charAt(idx)) {
            return false;
        }

        path.add(new int[]{x, y}); // choose this cell for letter idx

        for (int d = 0; d < DX.length; d++) {
            if (dfs(grid, word, idx + 1, x + DX[d], y + DY[d], path)) {
                return true; // keep the path intact all the way back to the caller
            }
        }

        path.remove(path.size() - 1); // undo: this cell leads nowhere
        return false;
    }

    private static String pathToString(List<int[]> path) {
        if (path == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (int[] cell : path) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append('[').append(cell[0]).append(", ").append(cell[1]).append(']');
        }
        return sb.toString();
    }

    private static void print(String label, List<int[]> actual, String expected) {
        System.out.println(label + " " + pathToString(actual) + "   expected " + expected);
    }

    public static void main(String[] args) {
        char[][] grid = {
                {'A', 'B', 'C', 'E'},
                {'S', 'F', 'C', 'S'},
                {'A', 'D', 'E', 'E'}
        };

        // case 1: right, right, down, down spells ABCCE
        print("case 1 \"ABCCE\":", findWord(grid, "ABCCE"),
                "[0, 0] [0, 1] [0, 2] [1, 2] [2, 2]");

        // case 2: SEE exists in the grid but only via moves we do not allow
        print("case 2 \"SEE\":  ", findWord(grid, "SEE"), "null");

        // case 3: edge -- one cell, one letter, no move ever taken
        print("case 3 1x1 \"A\":", findWord(new char[][]{{'A'}}, "A"), "[0, 0]");
    }
}

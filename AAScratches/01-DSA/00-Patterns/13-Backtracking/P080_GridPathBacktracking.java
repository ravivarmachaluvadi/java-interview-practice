/*
 * =====================================================================
 *  P080 Backtracking on a Grid Path   Canonical LC 79 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 79, Word Search)
 *   Return whether `word` can be traced in the letter grid through horizontally or
 *   vertically adjacent cells, using each cell at most once.
 *
 * EXAMPLE
 *   ABCE / SFCS / ADEE, "ABCCED"  ->  true
 *   ABCE / SFCS / ADEE, "SEE"     ->  true
 *   ABCE / SFCS / ADEE, "ABCB"    ->  false    B would be reused
 *
 * RECOGNIZE WHEN
 *   - Find or count SIMPLE paths in a grid (no cell twice) that satisfy something: spell a
 *     word, visit every empty cell, collect gold, escape a maze listing every route.
 *   - Shortest is NOT asked (that is BFS); "all paths" or "does a path exist with a
 *     property that depends on the whole path" is.
 *   Not this if: shortest path -> P067_BfsShortestPath; regions -> P065_GridFloodFill; many
 *   words at once -> P064_TrieDfsBitTrie.
 *
 * TEMPLATE
 *   dfs(r, c, state):
 *       if out of bounds or blocked or visited or cell does not fit: return false / 0
 *       if goal reached: return true / 1
 *       mark (r, c) visited
 *       result = combine dfs over the 4 neighbours
 *       unmark (r, c)                          // the cell is free again for other paths
 *       return result
 *
 * APPROACH
 *   1. Try every cell as the start of the word.
 *   2. Each step must match the next letter; mark the cell (overwrite with '#') while it is
 *      on the current path.
 *   3. Restore it on the way back so other paths can use it.
 *
 * KEY INSIGHT
 *   Unlike flood fill, "visited" here belongs to the CURRENT PATH, not to the whole search:
 *   a cell used by one failed attempt must be usable by the next. That is why the mark is
 *   undone after the recursive calls, the defining step of backtracking.
 *
 * COMPLEXITY
 *   LC 79: O(R * C * 3^L) (3 directions after the first step). Space O(L).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 980  Unique Paths III         count paths that visit EVERY empty cell:
 *                                            carry "cells left" and require 0 at the end
 *   [coded] Rat in a Maze (GfG)              collect the move strings D, L, R, U in order
 *           LC 1219 Path with Maximum Gold   try every start; best sum over simple paths
 *           LC 212  Word Search II           many words -> P064_TrieDfsBitTrie
 *           LC 489  Robot Room Cleaner       backtrack with relative directions
 *
 * PITFALLS
 *   - Forgetting to restore the cell makes later paths fail mysteriously.
 *   - Check the letter BEFORE recursing, or the search explores hopeless branches.
 *   - Pruning: if the board lacks enough of some letter, return false immediately.
 *
 * DEEP DIVE
 *   C05_WordSearch, B03_WordPathFinder (14-Backtracking-Recursion)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.List;

class GridPathBacktracking {

    // Canonical LC 79.
    static boolean exist(char[][] board, String word) {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (trace(board, word, 0, r, c)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean trace(char[][] b, String w, int i, int r, int c) {
        if (i == w.length()) {
            return true;
        }
        if (r < 0 || c < 0 || r >= b.length || c >= b[0].length || b[r][c] != w.charAt(i)) {
            return false;
        }
        char saved = b[r][c];
        b[r][c] = '#';                             // on the current path
        boolean found = trace(b, w, i + 1, r + 1, c) || trace(b, w, i + 1, r - 1, c)
                || trace(b, w, i + 1, r, c + 1) || trace(b, w, i + 1, r, c - 1);
        b[r][c] = saved;                           // free it for other paths
        return found;
    }

    // LC 980: 1 start, 2 end, 0 empty, -1 obstacle; walk over every non-obstacle cell once.
    static int uniquePathsIII(int[][] grid) {
        int empty = 1;                             // the start cell counts as one to cover
        int sr = 0;
        int sc = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == 0) {
                    empty++;
                } else if (grid[r][c] == 1) {
                    sr = r;
                    sc = c;
                }
            }
        }
        return walk(grid, sr, sc, empty);
    }

    private static int walk(int[][] g, int r, int c, int left) {
        if (r < 0 || c < 0 || r >= g.length || c >= g[0].length || g[r][c] < 0) {
            return 0;
        }
        if (g[r][c] == 2) {
            return left == 0 ? 1 : 0;
        }
        int saved = g[r][c];
        g[r][c] = -2;                              // visited on this path
        int paths = walk(g, r + 1, c, left - 1) + walk(g, r - 1, c, left - 1)
                + walk(g, r, c + 1, left - 1) + walk(g, r, c - 1, left - 1);
        g[r][c] = saved;
        return paths;
    }

    // Rat in a maze: 1 = open. All routes from (0,0) to (n-1,n-1), moves in D, L, R, U order.
    static List<String> findPaths(int[][] maze) {
        List<String> out = new ArrayList<>();
        int n = maze.length;
        if (maze[0][0] == 1 && maze[n - 1][n - 1] == 1) {
            route(maze, 0, 0, new StringBuilder(), out);
        }
        return out;
    }

    private static final int[][] MOVES = {{1, 0}, {0, -1}, {0, 1}, {-1, 0}};
    private static final String NAMES = "DLRU";

    private static void route(int[][] m, int r, int c, StringBuilder path, List<String> out) {
        int n = m.length;
        if (r == n - 1 && c == n - 1) {
            out.add(path.toString());
            return;
        }
        m[r][c] = 0;                               // block while on the path
        for (int k = 0; k < 4; k++) {
            int nr = r + MOVES[k][0];
            int nc = c + MOVES[k][1];
            if (nr >= 0 && nc >= 0 && nr < n && nc < n && m[nr][nc] == 1) {
                path.append(NAMES.charAt(k));
                route(m, nr, nc, path, out);
                path.deleteCharAt(path.length() - 1);
            }
        }
        m[r][c] = 1;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        char[][] board = {"ABCE".toCharArray(), "SFCS".toCharArray(), "ADEE".toCharArray()};
        check("LC 79 ABCCED", exist(board, "ABCCED"), true);
        check("LC 79 SEE", exist(board, "SEE"), true);
        check("LC 79 ABCB reuses a cell", exist(board, "ABCB"), false);
        check("LC 79 board restored",
                new String(board[0]) + new String(board[1]) + new String(board[2]),
                "ABCESFCSADEE");

        check("LC 980 two paths",
                uniquePathsIII(new int[][]{{1, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 2, -1}}), 2);
        check("LC 980 four paths",
                uniquePathsIII(new int[][]{{1, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 2}}), 4);
        check("LC 980 impossible", uniquePathsIII(new int[][]{{0, 1}, {2, 0}}), 0);

        check("rat in a maze two routes",
                findPaths(new int[][]{{1, 0, 0, 0}, {1, 1, 0, 1}, {1, 1, 0, 0}, {0, 1, 1, 1}}),
                "[DDRDRR, DRDDRR]");
        check("rat in a maze blocked start", findPaths(new int[][]{{0, 1}, {1, 1}}), "[]");
    }
}

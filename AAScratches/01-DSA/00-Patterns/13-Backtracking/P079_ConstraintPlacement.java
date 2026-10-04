/*
 * =====================================================================
 *  P079 Backtracking: Place Under Constraints   Canonical LC 51 | Hard
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 51, N-Queens)
 *   Place n queens on an n x n board so no two attack each other (same row, column or
 *   diagonal). Return every distinct board.
 *
 * EXAMPLE
 *   n = 4  ->  [[".Q..","...Q","Q...","..Q."], ["..Q.","Q...","...Q",".Q.."]]
 *   n = 1  ->  [["Q"]]
 *
 * RECOGNIZE WHEN
 *   - Fill slots (rows, cells, nodes) one at a time, where each choice must respect rules
 *     involving earlier choices: queens, sudoku, graph colouring, crosswords.
 *   - You need ALL solutions, or ANY one, and n is small.
 *   Not this if: there is a polynomial greedy / DP structure (e.g. interval scheduling) ->
 *   that is faster; the search space is a grid path -> P080_GridPathBacktracking.
 *
 * TEMPLATE
 *   solve(slot):
 *       if every slot is filled: record / return true
 *       for choice in options(slot):
 *           if allowed(slot, choice):              // O(1) checks with bookkeeping sets
 *               apply(choice); if solve(next slot) and want just one: return true
 *               undo(choice)
 *       return false
 *   bookkeeping for queens: usedCol[c], usedDiag[r - c + n], usedAnti[r + c]
 *
 * APPROACH
 *   1. Place exactly one queen per row, row by row.
 *   2. A column / diagonal / anti-diagonal is "taken" once a queen sits on it; three
 *      boolean arrays make the check O(1).
 *   3. Undo the placement after exploring it.
 *
 * KEY INSIGHT
 *   Choosing ROWS as the slots removes the row constraint entirely, and indexing diagonals
 *   by r - c and anti-diagonals by r + c turns "is this square attacked?" into three array
 *   lookups. Constraint bookkeeping, not cleverness, is what makes backtracking fast.
 *
 * COMPLEXITY
 *   N-Queens about O(n!); sudoku bounded by 9^(empty cells) but heavily pruned.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 52   N-Queens II              count solutions instead of building boards
 *   [coded] LC 37   Sudoku Solver            slots = empty cells; rows / cols / boxes as
 *                                            9 x 9 boolean tables; stop at the first solution
 *   [coded] M-colouring (GfG)                slots = nodes; a colour is allowed if no
 *                                            neighbour already has it
 *           LC 36   Valid Sudoku             only the bookkeeping, no search
 *           LC 1240 Tiling a Rectangle       place the largest square at the first empty cell
 *
 * PITFALLS
 *   - Diagonal index r - c can be negative: shift by n - 1 (or n).
 *   - Undo EVERY piece of bookkeeping you set, in reverse order.
 *   - Sudoku: return true up the stack as soon as one solution is found, or you wipe it.
 *
 * DEEP DIVE
 *   D01_NQueens, C06_MColoringProblem (14-Backtracking-Recursion)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ConstraintPlacement {

    // Canonical LC 51.
    static List<List<String>> solveNQueens(int n) {
        List<List<String>> out = new ArrayList<>();
        char[][] board = new char[n][n];
        for (char[] row : board) {
            Arrays.fill(row, '.');
        }
        placeRow(0, board, new boolean[n], new boolean[2 * n], new boolean[2 * n], out);
        return out;
    }

    private static void placeRow(int r, char[][] b, boolean[] col, boolean[] diag, boolean[] anti,
                                 List<List<String>> out) {
        int n = b.length;
        if (r == n) {
            List<String> rows = new ArrayList<>();
            for (char[] row : b) {
                rows.add(new String(row));
            }
            out.add(rows);
            return;
        }
        for (int c = 0; c < n; c++) {
            if (col[c] || diag[r - c + n] || anti[r + c]) {
                continue;
            }
            col[c] = diag[r - c + n] = anti[r + c] = true;
            b[r][c] = 'Q';
            placeRow(r + 1, b, col, diag, anti, out);
            b[r][c] = '.';
            col[c] = diag[r - c + n] = anti[r + c] = false;
        }
    }

    // LC 52.
    static int totalNQueens(int n) {
        return solveNQueens(n).size();
    }

    // LC 37: fills the board in place; '.' marks an empty cell.
    static boolean solveSudoku(char[][] b) {
        boolean[][] row = new boolean[9][10];
        boolean[][] col = new boolean[9][10];
        boolean[][] box = new boolean[9][10];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (b[r][c] != '.') {
                    int d = b[r][c] - '0';
                    row[r][d] = col[c][d] = box[r / 3 * 3 + c / 3][d] = true;
                }
            }
        }
        return fill(b, 0, row, col, box);
    }

    private static boolean fill(char[][] b, int cell, boolean[][] row, boolean[][] col,
                                boolean[][] box) {
        if (cell == 81) {
            return true;
        }
        int r = cell / 9;
        int c = cell % 9;
        if (b[r][c] != '.') {
            return fill(b, cell + 1, row, col, box);
        }
        int k = r / 3 * 3 + c / 3;
        for (int d = 1; d <= 9; d++) {
            if (row[r][d] || col[c][d] || box[k][d]) {
                continue;
            }
            row[r][d] = col[c][d] = box[k][d] = true;
            b[r][c] = (char) ('0' + d);
            if (fill(b, cell + 1, row, col, box)) {
                return true;                       // keep the first solution
            }
            b[r][c] = '.';
            row[r][d] = col[c][d] = box[k][d] = false;
        }
        return false;
    }

    // M-colouring: can the graph be coloured with m colours, no edge joining equal colours?
    static boolean graphColoring(int n, int[][] edges, int m) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        return colour(0, adj, new int[n], m);
    }

    private static boolean colour(int u, List<List<Integer>> adj, int[] colours, int m) {
        if (u == colours.length) {
            return true;
        }
        for (int c = 1; c <= m; c++) {
            boolean clash = false;
            for (int v : adj.get(u)) {
                if (colours[v] == c) {
                    clash = true;
                    break;
                }
            }
            if (!clash) {
                colours[u] = c;
                if (colour(u + 1, adj, colours, m)) {
                    return true;
                }
                colours[u] = 0;
            }
        }
        return false;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 51 n=4", solveNQueens(4), "[[.Q.., ...Q, Q..., ..Q.], [..Q., Q..., ...Q, .Q..]]");
        check("LC 51 n=1", solveNQueens(1), "[[Q]]");
        check("LC 51 n=3 none", solveNQueens(3), "[]");

        check("LC 52 n=8", totalNQueens(8), 92);
        check("LC 52 n=6", totalNQueens(6), 4);

        String[] puzzle = {"53..7....", "6..195...", ".98....6.", "8...6...3", "4..8.3..1",
                "7...2...6", ".6....28.", "...419..5", "....8..79"};
        char[][] board = new char[9][];
        for (int i = 0; i < 9; i++) {
            board[i] = puzzle[i].toCharArray();
        }
        check("LC 37 solved", solveSudoku(board), true);
        check("LC 37 first row", new String(board[0]), "534678912");
        check("LC 37 last row", new String(board[8]), "345286179");

        int[][] edges = {{0, 1}, {1, 2}, {2, 3}, {3, 0}, {0, 2}};
        check("m-colouring m=3", graphColoring(4, edges, 3), true);
        check("m-colouring m=2", graphColoring(4, edges, 2), false);
    }
}

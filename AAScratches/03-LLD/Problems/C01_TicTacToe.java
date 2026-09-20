/*
 * =====================================================================
 *  Design Tic-Tac-Toe (n x n board)                 LeetCode 348 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Design a Tic-Tac-Toe game on an n x n board for two players. Player 1 and
 *   player 2 alternately call move(row, col, player) to claim an empty cell.
 *   move() returns the winning player id (1 or 2) if that move completed a full
 *   row, column, main diagonal or anti-diagonal, and 0 otherwise.
 *
 * EXAMPLE
 *   n = 3 move(0,0,1) -> 0   move(0,2,2) -> 0   move(2,2,1) -> 0   move(1,1,2) -> 0
 *   move(2,0,1) -> 0   move(1,0,2) -> 0   move(2,1,1) -> 1   (row 2 is all 1s)
 *   move(2,1,2) on the taken cell -> IllegalArgumentException (cell already taken)
 *
 * DESIGN  (grid state behind one validated move() API)
 *   TicTacToe   owns the only mutable state: an int[n][n] where 0 = empty.
 *               Nothing outside the class can touch the grid.
 *   move()      the single entry point: validate, mutate, then check for a win.
 *   checkRow / checkColumn / checkDiagonal / checkAntiDiagonal
 *               four tiny private predicates, each scanning exactly n cells.
 *
 * KEY DECISIONS
 *   1. Validate before mutating. Out-of-range coordinates or an already-claimed
 *      cell throw IllegalArgumentException, so an illegal move can never leave
 *      the board in a half-updated state.
 *   2. Only check the lines the move can possibly have completed: its own row,
 *      its own column, the main diagonal only when row == col, and the
 *      anti-diagonal only when col == n - 1 - row. A full board rescan is O(n^2)
 *      and buys nothing.
 *   3. Store player ids (1 / 2) in the grid rather than booleans, so 0 doubles
 *      as "empty" and one array serves both players.
 *   4. The O(n) scan is deliberate and readable. The O(1) counter trick (keep
 *      per-row, per-column and per-diagonal running sums) is listed as a
 *      follow-up; it is the same idea with the scan pre-aggregated.
 *
 * COMPLEXITY
 *   Time  O(n) per move   at most a row, a column and two diagonals of n cells.
 *   Space O(n^2)          the board itself; no other state is kept.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Make move() O(1): keep rowCount[], colCount[], diag, antiDiag; add +1 for
 *     player 1 and -1 for player 2, and a magnitude of n means that line is won.
 *   - How do you detect a draw? Track the number of filled cells; n*n with no
 *     winner is a draw.
 *   - Generalise to "k in a row" on a large board (Gomoku): scan outward from
 *     the placed cell in 4 directions instead of whole lines.
 *   - Make it thread-safe / multiplayer: whose turn is it, and where does that
 *     rule live - in TicTacToe, or in a Game class above it?
 *
 * RUN
 *   main() runs 3 cases: a full game won on a row, a 1x1 board (instant win),
 *   and a rejected move on an occupied cell.
 */
class TicTacToe {

    /** 0 = empty, otherwise the id of the player who claimed the cell. */
    private final int[][] board;
    private final int n;

    public TicTacToe(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("board size must be positive");
        }
        this.n = n;
        this.board = new int[n][n];
    }

    /**
     * Claims (row, col) for the given player.
     *
     * @return the player id if this move won the game, 0 otherwise.
     */
    public int move(int row, int col, int player) {
        validate(row, col, player);
        board[row][col] = player;

        // Only the lines passing through (row, col) can have just been completed.
        boolean onMainDiagonal = (row == col);
        boolean onAntiDiagonal = (col == n - 1 - row);
        if (checkRow(row, player)
                || checkColumn(col, player)
                || (onMainDiagonal && checkDiagonal(player))
                || (onAntiDiagonal && checkAntiDiagonal(player))) {
            return player;
        }
        return 0; // no winner yet
    }

    private void validate(int row, int col, int player) {
        if (row < 0 || row >= n || col < 0 || col >= n) {
            throw new IllegalArgumentException("cell (" + row + ", " + col + ") is off the board");
        }
        if (player != 1 && player != 2) {
            throw new IllegalArgumentException("player must be 1 or 2, got " + player);
        }
        if (board[row][col] != 0) {
            throw new IllegalArgumentException("cell (" + row + ", " + col + ") is already taken");
        }
    }

    private boolean checkRow(int row, int player) {
        for (int col = 0; col < n; col++) {
            if (board[row][col] != player) {
                return false;
            }
        }
        return true;
    }

    private boolean checkColumn(int col, int player) {
        for (int row = 0; row < n; row++) {
            if (board[row][col] != player) {
                return false;
            }
        }
        return true;
    }

    private boolean checkDiagonal(int player) {
        for (int row = 0; row < n; row++) {
            if (board[row][row] != player) {
                return false;
            }
        }
        return true;
    }

    private boolean checkAntiDiagonal(int player) {
        for (int row = 0; row < n; row++) {
            if (board[row][n - 1 - row] != player) {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------
    //  Demo
    // ------------------------------------------------------------------

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1: typical 3x3 game, player 1 wins by filling the bottom row.
        TicTacToe game = new TicTacToe(3);
        print("case 1a move(0,0,P1)", game.move(0, 0, 1), 0);
        print("case 1b move(0,2,P2)", game.move(0, 2, 2), 0);
        print("case 1c move(2,2,P1)", game.move(2, 2, 1), 0);
        print("case 1d move(1,1,P2)", game.move(1, 1, 2), 0);
        print("case 1e move(2,0,P1)", game.move(2, 0, 1), 0);
        print("case 1f move(1,0,P2)", game.move(1, 0, 2), 0);
        print("case 1g move(2,1,P1)", game.move(2, 1, 1), 1); // bottom row complete

        // Case 2: edge case - a 1x1 board, where the first move wins on all four lines.
        TicTacToe tiny = new TicTacToe(1);
        print("case 2  1x1 first move", tiny.move(0, 0, 2), 2);

        // Case 3: tricky - replaying an occupied cell must be rejected, not silently
        // overwritten (the old code overwrote it and could hand out a bogus win).
        String outcome;
        try {
            game.move(2, 1, 2);
            outcome = "no exception";
        } catch (IllegalArgumentException e) {
            outcome = "rejected: " + e.getMessage();
        }
        print("case 3  replay taken cell", outcome, "rejected: cell (2, 1) is already taken");
    }
}

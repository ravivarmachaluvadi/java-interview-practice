/**
 * Implements a Tic‑Tac‑Toe game on an n×n board.
 *
 * Problem: After each move, determine if the player has won by completing
 * a full row, column, main diagonal, or anti‑diagonal.
 *
 * Approach: Store moves in a 2D array. On every move, update the cell and
 * check only the affected row, column, and possibly diagonals for a win.
 *
 * Time Complexity: O(n) per move (checking up to n cells).
 * Space Complexity: O(n²) for the board storage.
 */
class TicTacToe {

    private int[][] board;
    private int n;

    public TicTacToe(int n) {
        board = new int[n][n];
        this.n = n;
    }

    public int move(int row, int col, int player) {
        board[row][col] = player;

        // Check if player wins
        if ((checkRow(row, player)) ||
                (checkColumn(col, player)) ||
                (row == col && checkDiagonal(player)) ||
                (col == n - row - 1 && checkAntiDiagonal(player))) {
            return player;
        }

        // No one wins
        return 0;
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
            if (board[row][n - row - 1] != player) {
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

    private boolean checkRow(int row, int player) {
        for (int col = 0; col < n; col++) {
            if (board[row][col] != player) {
                return false;
            }
        }
        return true;
    }

    // --- Example usage ---
    public static void main(String[] args) {
        int n = 3;
        TicTacToe game = new TicTacToe(n);

        System.out.println(game.move(0, 0, 1)); // Player 1 moves -> 0
        System.out.println(game.move(0, 2, 2)); // Player 2 moves -> 0
        System.out.println(game.move(2, 2, 1)); // Player 1 moves -> 0
        System.out.println(game.move(1, 1, 2)); // Player 2 moves -> 0
        System.out.println(game.move(2, 0, 1)); // Player 1 moves -> 0
        System.out.println(game.move(1, 0, 2)); // Player 2 moves -> 0
        System.out.println(game.move(2, 1, 1)); // Player 1 moves -> 1 (Player 1 wins)
    }
}

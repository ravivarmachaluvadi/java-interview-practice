// Array, Backtracking, Matrix
// https://leetcode.com/problems/word-search
class WordSearch {

    public boolean exist(char[][] board, String word) {
        int rows = board.length;
        int columns = board[0].length;
        int index = 0;
// any character in the grid could be starting character of the word
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (board[i][j] == word.charAt(index)) {
                    if (searchNext(board, word, i, j, index, rows, columns))
                        return true;
                }
            }
        }

        return false;
    }

    private boolean searchNext(char[][] board, String word, int row, int col,
                               int index, int rows, int columns) {

        if (index == word.length()) return true;

        if (row < 0 || col < 0 || row == rows || col == columns
                || board[row][col] != word.charAt(index) || board[row][col] == '!')
            return false;

        // this is to prevent reusing of the same character
        char c = board[row][col];
        board[row][col] = '!';

        // top direction
        boolean top = searchNext(board, word, row - 1, col, index + 1, rows, columns);
        // right direction
        boolean right = searchNext(board, word, row, col + 1, index + 1, rows, columns);
        // bottom direction
        boolean bottom = searchNext(board, word, row + 1, col, index + 1, rows, columns);
        // left direction
        boolean left = searchNext(board, word, row, col - 1, index + 1, rows, columns);

        board[row][col] = c; // undo change

        return top || right || bottom || left;
    }

    public static void main(String[] args) {
        char[][] board = {{'A', 'B', 'C', 'E'},
                {'S', 'F', 'C', 'S'},
                {'A', 'D', 'E', 'E'}};

        String word = "ABCCED";
        WordSearch sol = new WordSearch();
        boolean res = sol.exist(board, word);
        System.out.println(res);
    }

}

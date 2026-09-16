// https://leetcode.com/problems/number-of-islands/description/
// 200. Number of Islands
class NumberOfIslands {

    public static int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        // no.of rows
        int nr = grid.length;
        // no.of columns
        int nc = grid[0].length;
        int count = 0;

        for (int r = 0; r < nr; r++) {
            for (int c = 0; c < nc; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    dfs(grid, r, c, nr, nc);
                }
            }
        }
        return count;
    }

    private static void dfs(char[][] grid, int r, int c, int nr, int nc) {
        // Boundary check
        if (r < 0 || c < 0 || r >= nr || c >= nc || grid[r][c] == '0') {
            return;
        }

        // Mark current as visited by turning '1' to '0'
        grid[r][c] = '0';

        // Visit all 4 directions
        dfs(grid, r - 1, c, nr, nc);
        dfs(grid, r + 1, c, nr, nc);
        dfs(grid, r, c - 1, nr, nc);
        dfs(grid, r, c + 1, nr, nc);
    }

    public static void main(String[] args) {
        char[][] grid1 = {
                {'1', '1', '1', '1', '0'},
                {'1', '1', '0', '1', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '0', '0', '0'}
        };

        char[][] grid2 = {
                {'1', '1', '0', '0', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '1', '0', '0'},
                {'0', '0', '0', '1', '1'}
        };

        System.out.println("Example 1: " + numIslands(deepCopy(grid1)));  // expected output: 1
        System.out.println("Example 2: " + numIslands(deepCopy(grid2)));  // expected output: 3
    }

    // Helper to deep-copy a 2D char array, since numIslands modifies the grid
    private static char[][] deepCopy(char[][] original) {
        return original.clone();
    }
}

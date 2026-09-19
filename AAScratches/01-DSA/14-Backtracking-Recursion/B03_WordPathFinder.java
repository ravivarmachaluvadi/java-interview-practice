/**
 * Problem: Given a 2D grid of characters and a target word, determine whether the word can be formed by
 * moving only right or down from any starting cell, and if so, return the sequence of coordinates that
 * spell the word.
 *
 * Approach: Perform a depth‑first search (DFS) from every cell. At each step match the current grid
 * character with the corresponding letter in the word; then recursively explore the two allowed moves
 * (right and down). Backtrack when a path fails, storing successful coordinates along the way.
 *
 * Time Complexity: O(N*M*2^L) in the worst case, where N×M is the grid size and L is the length of the word,
 * because each cell can spawn up to two recursive calls per letter. In practice the search stops early
 * when a match is found or boundaries are hit.
 *
 * Space Complexity: O(L) for the recursion stack and path list, plus O(1) auxiliary space aside from the input grid.
 */
import java.util.*;
import java.util.*;

class WordPathFinder {

    // Directions: right (0,1), down (1,0)
    private static final int[] DX = {0, 1};
    private static final int[] DY = {1, 0};

    public static void main(String[] args) {
        char[][] grid = {
                {'A', 'B', 'C', 'E'},
                {'S', 'F', 'C', 'S'},
                {'A', 'D', 'E', 'E'}
        };
        String word = "ABCCE";

        List<int[]> path = findWord(grid, word);

        if (path != null) {
            System.out.print("Word found at path: ");
            for (int[] p : path) {
                System.out.print(Arrays.toString(p) + " ");
            }
            System.out.println();
        } else {
            System.out.println("Word not found.");
        }
    }

    public static List<int[]> findWord(char[][] grid, String word) {
        if (grid == null || grid.length == 0 || grid[0].length == 0 || word == null || word.isEmpty()) {
            return null;
        }

        int n = grid.length;
        int m = grid[0].length;

        // Try starting from every cell
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                List<int[]> path = new ArrayList<>();
                if (dfs(grid, word, 0, i, j, path)) {
                    return path;
                }
            }
        }
        return null;
    }

    private static boolean dfs(char[][] grid, String word, int idx, int x, int y, List<int[]> path) {
        // If we've matched all characters, we're done
        if (idx == word.length()) {
            return true;
        }

        // Bounds check
        if (x < 0 || y < 0 || x >= grid.length || y >= grid[0].length) {
            return false;
        }

        // Character mismatch
        if (grid[x][y] != word.charAt(idx)) {
            return false;
        }

        // Add current cell to path
        path.add(new int[]{x, y});

        // Try moving right
        if (dfs(grid, word, idx + 1, x, y + 1, path)) {
            return true;
        }

        // Try moving down
        if (dfs(grid, word, idx + 1, x + 1, y, path)) {
            return true;
        }

        // Backtrack
        path.remove(path.size() - 1);
        return false;
    }
}
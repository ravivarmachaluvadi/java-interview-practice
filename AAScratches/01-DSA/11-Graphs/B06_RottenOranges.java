import java.util.*;

/**
 * You are given an m x n grid where each cell can have one of three values:
 * <p>
 * 0 representing an empty cell,
 * <p>
 * 1 representing a fresh orange, or
 * <p>
 * 2 representing a rotten orange.
 * <p>
 * Every minute, any fresh orange that is 4-directionally
 * <p>
 * adjacent to a rotten orange becomes rotten.
 * <p>
 * Return the minimum number of minutes that must elapse until no
 * <p>
 * cell has a fresh orange. If this is impossible, return -1.
 */
class RottenOranges {

    public static int orangesRotting(int[][] grid) {
        if (grid == null || grid.length == 0) return 0;

        int rows = grid.length, cols = grid[0].length;
        Queue<int[]> queue = new LinkedList<>();
        int fresh = 0;

        // Count fresh oranges & enqueue rotten ones
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 2) {
                    queue.offer(new int[]{i, j});
                } else if (grid[i][j] == 1) {
                    fresh++;
                }
            }
        }

        if (fresh == 0) return 0; // no fresh oranges

        int minutes = 0;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        // remember fresh>0 also
        while (!queue.isEmpty() && fresh > 0) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] point = queue.poll();
                for (int[] d : dirs) {
                    int x = point[0] + d[0];
                    int y = point[1] + d[1];
                    // out of bounds with or condition is more performant
                    // than all are in bounds with and condition
                    if (x < 0 || y < 0 || x >= rows || y >= cols || grid[x][y] != 1)
                        continue;
                    grid[x][y] = 2;  // rot it
                    queue.offer(new int[]{x, y});
                    fresh--;  // one less fresh
                }
            }
            minutes++;
        }

        return fresh == 0 ? minutes : -1;
    }

    public static void main(String[] args) {
        int[][] arr = {{2, 1, 1}, {1, 1, 0}, {0, 1, 1}};
        int rotting = orangesRotting(arr);
        System.out.println("Minimum Number of Minutes Required " + rotting);
    }
}

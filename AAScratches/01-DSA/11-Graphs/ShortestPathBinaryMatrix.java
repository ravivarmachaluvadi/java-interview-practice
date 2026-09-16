
import java.util.*;

// https://leetcode.com/problems/shortest-path-in-a-binary-matrix/
class ShortestPathBinaryMatrix {

    // Directions for moving up, down, left, right
    private static final int[] DIRECTIONS = {-1, 0, 1, 0, -1, 0, 0, -1, 0, 1};

    public static int shortestPathBinaryMatrix(int[][] grid) {
        int n = grid.length;

        // If the start or end cell is blocked, return -1
        if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1)
            return -1;

        // BFS setup
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{0, 0, 1}); // {row, col, distance}
        boolean[][] visited = new boolean[n][n];
        visited[0][0] = true;

        // Perform BFS
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1], dist = current[2];

            // If we reach the bottom-right corner, return the distance
            if (x == n - 1 && y == n - 1) {
                return dist;
            }

            // Explore the 4 possible directions
            for (int i = 0; i < 4; i++) {
                int newX = x + DIRECTIONS[i * 2];
                int newY = y + DIRECTIONS[i * 2 + 1];

                // Check if the new cell is within bounds and not visited or blocked
                if (newX >= 0 && newX < n && newY >= 0 && newY < n && grid[newX][newY] == 0 && !visited[newX][newY]) {
                    queue.offer(new int[]{newX, newY, dist + 1});
                    visited[newX][newY] = true;
                }
            }
        }

        // If no path exists, return -1
        return -1;
    }

    public static void main(String[] args) {
        int[][] grid1 = {
                {0, 1},
                {1, 0}
        };

        int[][] grid2 = {
                {0, 0, 0},
                {1, 1, 0},
                {1, 1, 0}
        };

        System.out.println("Shortest path in grid1: " + shortestPathBinaryMatrix(grid1)); // Should return 2
        System.out.println("Shortest path in grid2: " + shortestPathBinaryMatrix(grid2)); // Should return 4
    }
}

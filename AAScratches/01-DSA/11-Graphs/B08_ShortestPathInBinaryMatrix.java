import java.util.LinkedList;
import java.util.Queue;

// https://leetcode.com/problems/shortest-path-in-binary-matrix/description/
// 1091. Shortest Path in Binary Matrix
class ShortestPathInBinaryMatrix {
    // Directions (8 neighbours)
    private static final int[][] DIRS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    public int shortestPathBinaryMatrix(int[][] grid) {
        int n = grid.length;
        if (n == 0) return -1;
        if (grid[0][0] != 0 || grid[n-1][n-1] != 0) return -1;

        boolean[][] visited = new boolean[n][n];
        Queue<int[]> queue = new LinkedList<>();
        // store: (row, col, distanceSoFar)
        queue.offer(new int[]{0, 0, 1});
        visited[0][0] = true;

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1], dist = cur[2];
            if (r == n-1 && c == n-1) {
                return dist;
            }
            for (int[] d : DIRS) {
                int nr = r + d[0], nc = c + d[1];
                if (nr >= 0 && nr < n && nc >= 0 && nc < n
                        && !visited[nr][nc]
                        && grid[nr][nc] == 0) {
                    visited[nr][nc] = true;
                    queue.offer(new int[]{nr, nc, dist + 1});
                }
            }
        }
        return -1;
    }

    // Example / main method
    public static void main(String[] args) {
        ShortestPathInBinaryMatrix solver = new ShortestPathInBinaryMatrix();

        int[][] grid1 = {
                {0, 1},
                {1, 0}
        };
        System.out.println("Example 1: " + solver.shortestPathBinaryMatrix(grid1));
        // Expected output: 2 (path: (0,0) → (1,1))

        int[][] grid2 = {
                {0, 0, 0},
                {1, 1, 0},
                {1, 1, 0}
        };
        System.out.println("Example 2: " + solver.shortestPathBinaryMatrix(grid2));
        // Expected output: 4 (path: (0,0) → (0,1) → (0,2) → (1,2) → (2,2))

        int[][] grid3 = {
                {1, 0, 0},
                {1, 1, 0},
                {1, 1, 0}
        };
        System.out.println("Example 3: " + solver.shortestPathBinaryMatrix(grid3));
        // Expected output: -1 (start is blocked)
    }
}

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Constraints:
 * <p>
 * n == grid.length == grid[i].length
 * <p>
 * 2 <= n <= 100
 * <p>
 * grid[i][j] is either 0 or 1.
 * <p>
 * There are exactly two islands in grid.
 */
class ShortestBridgeSolution {

    public int shortestBridge(int[][] grid) {
        int n = grid.length;
        Deque<int[]> queue = new ArrayDeque<>();
        boolean found = false;
        // direction vectors: up, right, down, left
        int[] dirs = new int[]{-1, 0, 1, 0, -1};

        // Step 1: find first island, mark it and add its cells to queue
        for (int i = 0; i < n && !found; i++) {
            for (int j = 0; j < n && !found; j++) {
                if (grid[i][j] == 1) {
                    dfsMark(grid, i, j, n, queue, dirs);
                    found = true;
                }
            }
        }

        // Step 2: BFS from the first island's cells to reach second island
        int flips = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int k = 0; k < size; k++) {
                int[] cell = queue.pollFirst();
                int x = cell[0], y = cell[1];
                for (int d = 0; d < 4; d++) {
                    int nx = x + dirs[d];
                    int ny = y + dirs[d + 1];
                    if (nx >= 0 && nx < n && ny >= 0 && ny < n) {
                        if (grid[nx][ny] == 1) {
                            // reached the second island
                            return flips;
                        }
                        if (grid[nx][ny] == 0) {
                            // flip this water to visited land marker (2)
                            grid[nx][ny] = 2;
                            queue.offerLast(new int[]{nx, ny});
                        }
                    }
                }
            }
            flips++;
        }

        return -1; // should never happen under valid input (two islands exist)
    }

    private void dfsMark(int[][] grid, int i, int j, int n, Deque<int[]> queue, int[] dirs) {
        if (i < 0 || i >= n || j < 0 || j >= n) return;
        if (grid[i][j] != 1) return;
        // mark and add to queue
        grid[i][j] = 2;
        queue.offerLast(new int[]{i, j});
        for (int d = 0; d < 4; d++) {
            int nx = i + dirs[d];
            int ny = j + dirs[d + 1];
            dfsMark(grid, nx, ny, n, queue, dirs);
        }
    }

    // Example main
    public static void main(String[] args) {
        ShortestBridgeSolution sol = new ShortestBridgeSolution();

        int[][] grid1 = {
                {0, 1},
                {1, 0}
        };
        System.out.println("Example1 -> " + sol.shortestBridge(grid1)); // expected 1

        int[][] grid2 = {
                {0, 1, 0},
                {0, 0, 0},
                {0, 0, 1}
        };
        System.out.println("Example2 -> " + sol.shortestBridge(grid2)); // expected 2

        int[][] grid3 = {
                {1, 1, 1, 1, 1},
                {1, 0, 0, 0, 1},
                {1, 0, 1, 0, 1},
                {1, 0, 0, 0, 1},
                {1, 1, 1, 1, 1}
        };
        System.out.println("Example3 -> " + sol.shortestBridge(grid3)); // expected 1
    }
}

import java.util.*;

// https://leetcode.com/problems/last-day-where-you-can-still-cross/description/
// 1970. Last Day when You Can Still Cross

/**
 * There is a 1-based binary matrix where 0
 * <p>
 * represents land and 1 represents water.
 * <p>
 * Constraints:
 * <p>
 * cells.length == row * col
 */
class LastDayToCrossPQ {

    private static final int[][] dirs = new int[][]{
            {-1, 0}, {0, 1}, {1, 0}, {0, -1}
    };

    public int latestDayToCross(int row, int col, int[][] cells) {
        // map[r][c] = the day on which this cell becomes water
        int[][] map = new int[row + 1][col + 1];
        for (int[] r : map) {
            Arrays.fill(r, Integer.MAX_VALUE);
        }
        for (int i = 0; i < cells.length; i++) {
            map[cells[i][0]][cells[i][1]] = i; // 0-indexed day
        }

        boolean[][] visited = new boolean[row + 1][col + 1];

        // Max-heap by "latest possible day"
        PriorityQueue<int[]> queue = new PriorityQueue<>(
                (a, b) -> Integer.compare(b[2], a[2])
        );

        // Start from top row cells
        for (int j = 1; j <= col; j++) {
            queue.add(new int[]{1, j, map[1][j]});
            visited[1][j] = true;
        }

        // BFS-like traversal
        while (!queue.isEmpty()) {
            int[] top = queue.poll();
            int r = top[0], c = top[1], day = top[2];

            // If we reached bottom row, return this day
            if (r == row) {
                return day;
            }

            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];
                if (nr <= 0 || nr > row || nc <= 0 || nc > col || visited[nr][nc]) {
                    continue;
                }
                visited[nr][nc] = true;
                queue.add(new int[]{nr, nc, Math.min(day, map[nr][nc])});
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        LastDayToCrossPQ solver = new LastDayToCrossPQ();

        int row = 3, col = 3;
        int[][] cells = {
                {1, 2}, {2, 1}, {3, 3},
                {2, 2}, {1, 1}, {1, 3},
                {2, 3}, {3, 2}, {3, 1}
        };

        int result = solver.latestDayToCross(row, col, cells);
        System.out.println("Latest day you can still cross: " + result);
        // Expected output: 3
    }
}

import java.util.*;

// https://leetcode.com/problems/minimum-time-to-visit-a-cell-in-a-grid/description/
// 2577. Minimum Time to Visit a Cell In a Grid
class MinimumTimeToVisitCell {
    public static int minimumTime(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        // If you cannot move from the start at all
        if (m > 1 && n > 1 && grid[0][1] > 1 && grid[1][0] > 1) {
            return -1;
        }

        int[][] dist = new int[m][n];
        for (int[] row : dist) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        dist[0][0] = 0;

        // priority queue holds [time, i, j]
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, 0, 0});

        int[] dirs = {-1, 0, 1, 0, -1};

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int t = cur[0], i = cur[1], j = cur[2];
            if (i == m - 1 && j == n - 1) {
                return t;
            }
            if (t > dist[i][j]) {
                continue;  // outdated entry
            }
            for (int k = 0; k < 4; ++k) {
                int ni = i + dirs[k];
                int nj = j + dirs[k + 1];
                if (ni >= 0 && ni < m && nj >= 0 && nj < n) {
                    int nt = t + 1;
                    if (nt < grid[ni][nj]) {
                        // wait until we can enter; parity adjustment
                        int wait = grid[ni][nj] - nt;
                        nt = grid[ni][nj] + (wait % 2);
                    }
                    if (nt < dist[ni][nj]) {
                        dist[ni][nj] = nt;
                        pq.offer(new int[]{nt, ni, nj});
                    }
                }
            }
        }

        return -1;
    }

    // example driver
    public static void main(String[] args) {
        int[][] grid1 = {
                {0, 1, 3, 2},
                {5, 1, 2, 5},
                {4, 3, 8, 6}
        };
        System.out.println("Example1 result: " + minimumTime(grid1));  // expected 7

        int[][] grid2 = {
                {0, 2, 4},
                {3, 2, 1},
                {1, 0, 4}
        };
        System.out.println("Example2 result: " + minimumTime(grid2));  // expected -1
    }
}

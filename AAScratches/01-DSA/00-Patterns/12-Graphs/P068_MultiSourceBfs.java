/*
 * =====================================================================
 *  P068 Multi-Source BFS   Canonical LC 994 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 994, Rotting Oranges)
 *   0 = empty, 1 = fresh orange, 2 = rotten. Every minute, fresh oranges next to a rotten
 *   one (4 directions) rot. Return the minutes until none are fresh, or -1 if some never rot.
 *
 * EXAMPLE
 *   [[2,1,1],[1,1,0],[0,1,1]]  ->  4
 *   [[2,1,1],[0,1,1],[1,0,1]]  ->  -1     the bottom-left orange is cut off
 *   [[0,2]]                    ->  0      nothing fresh at the start
 *
 * RECOGNIZE WHEN
 *   - Something spreads from SEVERAL starting points at the same speed: rot, fire, water,
 *     gates, "distance to the nearest 0 / land / exit" for every cell.
 *   - You need, for every cell, the distance to the NEAREST of many sources.
 *   Not this if: there is one source -> P067_BfsShortestPath; costs differ -> P072_Dijkstra.
 *
 * TEMPLATE
 *   queue = ALL sources at once (distance 0); mark them seen
 *   minutes = 0
 *   while queue:
 *       for size of the level: expand each cell to unseen neighbours, set their distance
 *       minutes++ (only if something new was added)
 *   distance of each cell = the level at which it was first reached
 *
 * APPROACH
 *   1. Put every rotten orange in the queue and count the fresh ones.
 *   2. Each BFS level is one minute; rot the fresh neighbours and decrement the count.
 *   3. If fresh oranges remain at the end, return -1.
 *
 * KEY INSIGHT
 *   Starting BFS from all sources at once is the same as adding one super-source joined to
 *   every source. Each cell is reached first by its nearest source, so one O(cells) pass
 *   replaces a BFS per source.
 *
 * COMPLEXITY
 *   Time O(R * C), space O(R * C).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 542  01 Matrix                sources = every 0; dist of each 1 to the nearest 0
 *   [coded] LC 286  Walls and Gates          sources = gates; fill rooms with the distance
 *   [coded] LC 934  Shortest Bridge          DFS one island into the queue, BFS over water
 *                                            until the other island is touched
 *           LC 1162 As Far from Land         sources = all land; the last level reached
 *           LC 1765 Map of Highest Peak      sources = water cells; height = BFS distance
 *           LC 2812 Safest Path in a Grid    multi-source BFS from thieves, then a max-min path
 *
 * PITFALLS
 *   - Count minutes only for levels that actually rot something (or subtract one at the end).
 *   - No fresh oranges at the start: the answer is 0, not -1.
 *   - Mark cells when they are enqueued; a cell next to two sources must be added once.
 *
 * DEEP DIVE
 *   B06_RottenOranges, B07_WallsAndGates, C02_ShortestBridge (11-Graphs)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

class MultiSourceBfs {

    static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    // Canonical LC 994.
    static int orangesRotting(int[][] grid) {
        Deque<int[]> queue = new ArrayDeque<>();
        int fresh = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == 2) {
                    queue.add(new int[]{r, c});
                } else if (grid[r][c] == 1) {
                    fresh++;
                }
            }
        }
        int minutes = 0;
        while (!queue.isEmpty() && fresh > 0) {
            for (int size = queue.size(); size > 0; size--) {
                int[] cur = queue.poll();
                for (int[] d : DIRS) {
                    int r = cur[0] + d[0];
                    int c = cur[1] + d[1];
                    boolean inside = r >= 0 && c >= 0 && r < grid.length && c < grid[0].length;
                    if (inside && grid[r][c] == 1) {
                        grid[r][c] = 2;
                        fresh--;
                        queue.add(new int[]{r, c});
                    }
                }
            }
            minutes++;
        }
        return fresh == 0 ? minutes : -1;
    }

    // LC 542: distance from each cell to the nearest 0.
    static int[][] updateMatrix(int[][] mat) {
        int rows = mat.length;
        int cols = mat[0].length;
        int[][] dist = new int[rows][cols];
        Deque<int[]> queue = new ArrayDeque<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (mat[r][c] == 0) {
                    queue.add(new int[]{r, c});
                } else {
                    dist[r][c] = -1;               // not reached yet
                }
            }
        }
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            for (int[] d : DIRS) {
                int r = cur[0] + d[0];
                int c = cur[1] + d[1];
                if (r >= 0 && c >= 0 && r < rows && c < cols && dist[r][c] == -1) {
                    dist[r][c] = dist[cur[0]][cur[1]] + 1;
                    queue.add(new int[]{r, c});
                }
            }
        }
        return dist;
    }

    // LC 286: -1 wall, 0 gate, INF empty room.
    static int[][] wallsAndGates(int[][] rooms) {
        final int inf = Integer.MAX_VALUE;
        Deque<int[]> queue = new ArrayDeque<>();
        for (int r = 0; r < rooms.length; r++) {
            for (int c = 0; c < rooms[0].length; c++) {
                if (rooms[r][c] == 0) {
                    queue.add(new int[]{r, c});
                }
            }
        }
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            for (int[] d : DIRS) {
                int r = cur[0] + d[0];
                int c = cur[1] + d[1];
                boolean inside = r >= 0 && c >= 0 && r < rooms.length && c < rooms[0].length;
                if (inside && rooms[r][c] == inf) {
                    rooms[r][c] = rooms[cur[0]][cur[1]] + 1;
                    queue.add(new int[]{r, c});
                }
            }
        }
        return rooms;
    }

    // LC 934: flip the fewest 0s to connect the two islands.
    static int shortestBridge(int[][] grid) {
        int n = grid.length;
        Deque<int[]> queue = new ArrayDeque<>();
        outer:
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 1) {
                    markIsland(grid, r, c, queue);        // first island becomes 2s
                    break outer;
                }
            }
        }
        int flips = 0;
        while (!queue.isEmpty()) {
            for (int size = queue.size(); size > 0; size--) {
                int[] cur = queue.poll();
                for (int[] d : DIRS) {
                    int r = cur[0] + d[0];
                    int c = cur[1] + d[1];
                    if (r < 0 || c < 0 || r >= n || c >= n || grid[r][c] == 2) {
                        continue;
                    }
                    if (grid[r][c] == 1) {
                        return flips;                     // reached the other island
                    }
                    grid[r][c] = 2;
                    queue.add(new int[]{r, c});
                }
            }
            flips++;
        }
        return -1;
    }

    private static void markIsland(int[][] g, int r, int c, Deque<int[]> queue) {
        if (r < 0 || c < 0 || r >= g.length || c >= g.length || g[r][c] != 1) {
            return;
        }
        g[r][c] = 2;
        queue.add(new int[]{r, c});
        for (int[] d : DIRS) {
            markIsland(g, r + d[0], c + d[1], queue);
        }
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 994 four minutes",
                orangesRotting(new int[][]{{2, 1, 1}, {1, 1, 0}, {0, 1, 1}}), 4);
        check("LC 994 one is cut off",
                orangesRotting(new int[][]{{2, 1, 1}, {0, 1, 1}, {1, 0, 1}}), -1);
        check("LC 994 nothing fresh", orangesRotting(new int[][]{{0, 2}}), 0);
        check("LC 994 two sources meet", orangesRotting(new int[][]{{2, 1, 1, 1, 2}}), 2);

        check("LC 542 centre one",
                Arrays.deepToString(updateMatrix(new int[][]{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}})),
                "[[0, 0, 0], [0, 1, 0], [0, 0, 0]]");
        check("LC 542 bottom row",
                Arrays.deepToString(updateMatrix(new int[][]{{0, 0, 0}, {0, 1, 0}, {1, 1, 1}})),
                "[[0, 0, 0], [0, 1, 0], [1, 2, 1]]");

        final int inf = Integer.MAX_VALUE;
        int[][] rooms = {
                {inf, -1, 0, inf}, {inf, inf, inf, -1}, {inf, -1, inf, -1}, {0, -1, inf, inf}};
        check("LC 286 two gates", Arrays.deepToString(wallsAndGates(rooms)),
                "[[3, -1, 0, 1], [2, 2, 1, -1], [1, -1, 2, -1], [0, -1, 3, 4]]");

        check("LC 934 touching diagonally", shortestBridge(new int[][]{{0, 1}, {1, 0}}), 1);
        check("LC 934 corners", shortestBridge(new int[][]{{0, 1, 0}, {0, 0, 0}, {0, 0, 1}}), 2);
        check("LC 934 ring around a dot",
                shortestBridge(new int[][]{{1, 1, 1, 1, 1}, {1, 0, 0, 0, 1},
                {1, 0, 1, 0, 1}, {1, 0, 0, 0, 1}, {1, 1, 1, 1, 1}}), 1);
    }
}

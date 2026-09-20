/*
 * =====================================================================
 *  Walls and Gates                                  LeetCode 286 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   An m x n grid holds -1 (wall), 0 (gate) or INF = Integer.MAX_VALUE (empty
 *   room). Fill each empty room in place with its distance to the NEAREST gate,
 *   moving 4-directionally. A room no gate can reach keeps INF.
 *
 * EXAMPLE
 *   [[INF, -1,  0, INF],            [[  3, -1,  0,  1],
 *    [INF, INF, INF, -1],    ->      [  2,  2,  1, -1],
 *    [INF, -1, INF, -1],             [  1, -1,  2, -1],
 *    [  0, -1, INF, INF]]            [  0, -1,  3,  4]]
 *   [[INF, -1], [INF, INF]]  ->  unchanged: there is no gate to measure from
 *
 * APPROACH  (multi-source BFS writing distances into the grid)
 *   1. Scan the grid and enqueue EVERY gate (value 0). All gates start at
 *      distance 0 together.
 *   2. Pop a cell, look at its four neighbours.
 *   3. Only step into a neighbour still holding INF. That single test does
 *      three jobs: it skips walls (-1), skips gates (0), and skips rooms
 *      already assigned - so it is also the visited check.
 *   4. Write rooms[next] = rooms[current] + 1 and enqueue the neighbour.
 *   5. When the queue drains, every reachable room holds its true minimum, and
 *      unreachable rooms are untouched at INF.
 *
 * KEY INSIGHT
 *   The first time BFS reaches a room, it arrives along a shortest path from
 *   the closest gate - so the first value written is already the minimum and
 *   never needs updating. That is why one pass replaces "BFS once per gate",
 *   turning O(gates * m * n) into O(m * n). Writing the answer into the grid
 *   instead of a separate dist[][] makes the cell itself the visited marker.
 *
 * COMPLEXITY
 *   Time  O(m * n)  each room is written once and enqueued once
 *   Space O(m * n)  the queue when the grid is mostly gates
 *
 * INTERVIEW FOLLOW-UPS
 *   - Rotting Oranges (994) is the same machinery counting levels as minutes.
 *   - 01 Matrix (542): distance to the nearest 0, identical multi-source BFS.
 *   - Why not DFS from each gate? It revisits rooms with worse distances and
 *     degrades to O(gates * m * n) with repeated overwrites.
 *   - Weighted terrain (crossing a room costs k): the queue becomes a heap and
 *     this turns into Dijkstra.
 *
 * RUN
 *   main() runs 3 cases (typical, no gate at all, a single gate) and prints the
 *   resulting grid as a string against the expected one.
 */

import java.util.LinkedList;
import java.util.Queue;

class WallsAndGates {

    private static final int INF = Integer.MAX_VALUE;

    public void wallsAndGates(int[][] rooms) {
        if (rooms == null || rooms.length == 0 || rooms[0].length == 0) return;

        int rows = rooms.length;
        int cols = rooms[0].length;
        Queue<int[]> queue = new LinkedList<>();

        // Step 1: every gate is a BFS source, all at distance 0
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (rooms[i][j] == 0) {
                    queue.offer(new int[]{i, j});
                }
            }
        }

        // rolling pairs: (0,1) right, (1,0) down, (0,-1) left, (-1,0) up
        int[] directions = {0, 1, 0, -1, 0};

        // Step 2: expand outward; first arrival is the shortest distance
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            for (int i = 0; i < 4; i++) {
                int nx = x + directions[i];
                int ny = y + directions[i + 1];

                // still INF means: in bounds, not a wall, not a gate, not yet assigned
                if (nx >= 0 && nx < rows && ny >= 0 && ny < cols && rooms[nx][ny] == INF) {
                    rooms[nx][ny] = rooms[x][y] + 1; // one step further than where we came from
                    queue.offer(new int[]{nx, ny});
                }
            }
        }
    }

    /** Render the grid with INF spelled out, so cases can be compared as strings. */
    private static String format(int[][] grid) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < grid.length; i++) {
            sb.append(i == 0 ? "[" : ", [");
            for (int j = 0; j < grid[i].length; j++) {
                if (j > 0) sb.append(", ");
                sb.append(grid[i][j] == INF ? "INF" : String.valueOf(grid[i][j]));
            }
            sb.append(']');
        }
        return sb.append(']').toString();
    }

    private static void print(String label, int[][] rooms, String expected) {
        new WallsAndGates().wallsAndGates(rooms);
        System.out.println(label + ": " + format(rooms) + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1: two gates; every reachable room takes the nearer one
        print("case 1 typical", new int[][]{
                {INF, -1, 0, INF},
                {INF, INF, INF, -1},
                {INF, -1, INF, -1},
                {0, -1, INF, INF}
        }, "[[3, -1, 0, 1], [2, 2, 1, -1], [1, -1, 2, -1], [0, -1, 3, 4]]");

        // case 2 (edge): no gate anywhere, so nothing is reachable and INF stays
        print("case 2 no gate", new int[][]{
                {INF, -1},
                {INF, INF}
        }, "[[INF, -1], [INF, INF]]");

        // case 3 (edge): a lone gate - it is already 0 and must not be overwritten
        print("case 3 single gate", new int[][]{{0}}, "[[0]]");
    }
}

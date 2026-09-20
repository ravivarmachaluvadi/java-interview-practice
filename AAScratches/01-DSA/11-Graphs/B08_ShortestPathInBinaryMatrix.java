/*
 * =====================================================================
 *  Shortest Path in Binary Matrix                  LeetCode 1091 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an n x n binary matrix, walk from (0,0) to (n-1,n-1) stepping only on
 *   cells equal to 0 and moving in any of the 8 directions. Return the number of
 *   CELLS on the shortest such path (so the start itself counts as 1), or -1 if
 *   no clear path exists.
 *
 * EXAMPLE
 *   [[0,1],[1,0]]                  ->  2   (0,0) -> (1,1) diagonally
 *   [[0,0,0],[1,1,0],[1,1,0]]      ->  4   across the top row, then down the right
 *   [[1,0,0],[1,1,0],[1,1,0]]      -> -1   the start cell is blocked
 *   [[0]]                          ->  1   start and end are the same cell
 *
 * APPROACH  (8-direction BFS, distance carried in the queue)
 *   1. Reject immediately if either corner is a 1 - there is nothing to search.
 *   2. Push {0, 0, 1}: row, column, and cells-used-so-far starting at 1.
 *   3. Pop a cell. If it is the bottom-right corner, its stored distance is the
 *      answer - BFS guarantees no shorter route can arrive later.
 *   4. Otherwise push every in-bounds, unvisited, zero-valued neighbour among
 *      the 8 with distance + 1, marking it visited AT PUSH TIME.
 *   5. An empty queue means the corner was never reached: return -1.
 *
 * KEY INSIGHT
 *   Mark visited when you ENQUEUE, not when you dequeue. With 8 neighbours the
 *   same cell can be offered by several cells in the same level; marking at
 *   dequeue time lets all those copies into the queue and blows it up
 *   exponentially in dense grids. The answer itself is free: because every edge
 *   costs 1, the first time BFS touches the target it has already used the
 *   fewest cells, so no relaxation or priority queue is needed.
 *
 * COMPLEXITY
 *   Time  O(n^2)   each cell is enqueued once and scans 8 fixed neighbours
 *   Space O(n^2)   visited matrix plus the queue frontier
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the path itself: store a parent[][] and walk back from the corner.
 *   - Cells cost different amounts to enter: that is Dijkstra, not BFS.
 *   - Huge grid: bidirectional BFS from both corners roughly halves the
 *     explored area; A* with a Chebyshev-distance heuristic does better still.
 *   - Why store the distance in the queue instead of a dist[][]? Either works;
 *     level-size BFS with a counter is a third equivalent formulation.
 *
 * RUN
 *   main() runs 4 cases (diagonal hop, longer detour, blocked start, 1x1 grid)
 *   and prints actual vs expected.
 */

import java.util.LinkedList;
import java.util.Queue;

class ShortestPathInBinaryMatrix {

    // all 8 neighbours: orthogonal first, then the diagonals
    private static final int[][] DIRS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    public int shortestPathBinaryMatrix(int[][] grid) {
        if (grid == null || grid.length == 0) return -1;
        int n = grid.length;
        // no path can start or finish on a blocked corner
        if (grid[0][0] != 0 || grid[n - 1][n - 1] != 0) return -1;

        boolean[][] visited = new boolean[n][n];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{0, 0, 1}); // {row, col, cells used including this one}
        visited[0][0] = true;

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1], dist = cur[2];

            // first arrival at the corner is the shortest one - BFS guarantees it
            if (r == n - 1 && c == n - 1) return dist;

            for (int[] d : DIRS) {
                int nr = r + d[0], nc = c + d[1];
                if (nr >= 0 && nr < n && nc >= 0 && nc < n
                        && !visited[nr][nc]
                        && grid[nr][nc] == 0) {
                    visited[nr][nc] = true; // mark on enqueue, or duplicates flood the queue
                    queue.offer(new int[]{nr, nc, dist + 1});
                }
            }
        }
        return -1; // frontier died out without reaching the corner
    }

    private static void print(String label, int[][] grid, int expected) {
        int actual = new ShortestPathInBinaryMatrix().shortestPathBinaryMatrix(grid);
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1: the diagonal step is what makes this 2 and not -1
        print("case 1 diagonal hop", new int[][]{
                {0, 1},
                {1, 0}
        }, 2);

        // case 2: walls force a detour along the top row and down the right edge
        print("case 2 detour", new int[][]{
                {0, 0, 0},
                {1, 1, 0},
                {1, 1, 0}
        }, 4);

        // case 3 (edge): start cell is blocked, so the search never begins
        print("case 3 blocked start", new int[][]{
                {1, 0, 0},
                {1, 1, 0},
                {1, 1, 0}
        }, -1);

        // case 4 (edge): 1x1 open grid - start IS the target, and counts as 1 cell
        print("case 4 single cell", new int[][]{{0}}, 1);
    }
}

/*
 * =====================================================================
 *  Rotting Oranges                        LeetCode 994 | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   An m x n grid holds 0 (empty), 1 (fresh orange) or 2 (rotten orange).
 *   Every minute, each fresh orange 4-directionally adjacent to a rotten one
 *   also rots. Return the minutes until no fresh orange is left, or -1 if some
 *   fresh orange can never be reached.
 *
 * EXAMPLE
 *   [[2,1,1],[1,1,0],[0,1,1]]  ->  4   rot spreads outward from (0,0)
 *   [[2,1,1],[0,1,1],[1,0,1]]  -> -1   the 1 at (2,0) is walled off by zeros
 *   [[0,2]]                    ->  0   nothing fresh, so zero minutes elapse
 *
 * APPROACH  (multi-source BFS, one level per minute)
 *   1. Scan the grid once: push EVERY rotten cell into the queue and count the
 *      fresh ones.
 *   2. If fresh == 0, no time passes - return 0 immediately.
 *   3. Loop while the queue is non-empty AND fresh > 0. Snapshot size =
 *      queue.size() first: those cells are exactly the current minute's front.
 *   4. Drain that many cells, rot each in-bounds fresh neighbour (write 2, push
 *      it, decrement fresh), then minutes++.
 *   5. Afterwards fresh == 0 means everything rotted in `minutes`; anything
 *      left is unreachable, so return -1.
 *
 * KEY INSIGHT
 *   Seeding the queue with every source at once makes ordinary BFS compute all
 *   sources' shortest distances simultaneously - it behaves as if a virtual
 *   super-source sat one step behind all of them. Because BFS expands strictly
 *   level by level, the level counter IS elapsed time. The `fresh > 0` guard on
 *   the while loop is what stops the classic off-by-one: without it the last
 *   level still increments minutes after nothing new rots.
 *
 * COMPLEXITY
 *   Time  O(m * n)  each cell is enqueued at most once and has 4 neighbours
 *   Space O(m * n)  the queue in the worst case (every orange rotten at t = 0)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Walls and Gates (286): same multi-source BFS, writing distances instead
 *     of counting minutes.
 *   - Why not DFS? DFS can reach a cell by a longer path first and record the
 *     wrong time; you would have to allow revisits with a smaller distance.
 *   - Which orange rotted last, or which fresh ones are unreachable?
 *   - Diagonal spread: extend the direction list to 8.
 *
 * RUN
 *   main() runs 4 cases (typical, unreachable orange, no fresh oranges, a lone
 *   fresh orange) and prints actual vs expected.
 */

import java.util.LinkedList;
import java.util.Queue;

class RottenOranges {

    private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public static int orangesRotting(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) return 0;

        int rows = grid.length;
        int cols = grid[0].length;
        Queue<int[]> queue = new LinkedList<>();
        int fresh = 0;

        // seed the queue with EVERY rotten orange and count what must still rot
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 2) {
                    queue.offer(new int[]{i, j});
                } else if (grid[i][j] == 1) {
                    fresh++;
                }
            }
        }

        if (fresh == 0) return 0; // nothing to wait for, even if rotten ones exist

        int minutes = 0;
        // fresh > 0 in the condition is what stops minutes from over-counting by one
        while (!queue.isEmpty() && fresh > 0) {
            int size = queue.size(); // exactly the cells that rotted at this minute
            for (int i = 0; i < size; i++) {
                int[] cell = queue.poll();
                for (int[] d : DIRS) {
                    int x = cell[0] + d[0];
                    int y = cell[1] + d[1];
                    // one out-of-bounds OR chain is cheaper to read than four in-bounds ANDs
                    if (x < 0 || y < 0 || x >= rows || y >= cols || grid[x][y] != 1) continue;
                    grid[x][y] = 2;          // rot it - this doubles as the visited mark
                    queue.offer(new int[]{x, y});
                    fresh--;
                }
            }
            minutes++;
        }

        return fresh == 0 ? minutes : -1; // leftovers mean some orange was walled off
    }

    private static int[][] copyOf(int[][] grid) {
        int[][] copy = new int[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            copy[i] = grid[i].clone();
        }
        return copy;
    }

    private static void print(String label, int[][] grid, int expected) {
        int actual = orangesRotting(copyOf(grid)); // copy: the solver rots the grid in place
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1: rot spreads from the single corner source across the whole grid
        print("case 1 typical", new int[][]{{2, 1, 1}, {1, 1, 0}, {0, 1, 1}}, 4);

        // case 2: (2,0) is isolated behind empty cells, so it never rots
        print("case 2 unreachable", new int[][]{{2, 1, 1}, {0, 1, 1}, {1, 0, 1}}, -1);

        // case 3 (edge): nothing fresh at all - answer is 0, not 1
        print("case 3 no fresh", new int[][]{{0, 2}}, 0);

        // case 4 (edge): a fresh orange with no rotten neighbour anywhere
        print("case 4 lone fresh", new int[][]{{1}}, -1);
    }
}

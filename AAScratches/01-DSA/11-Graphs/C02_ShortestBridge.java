/*
 * =====================================================================
 *  Shortest Bridge                                  LeetCode 934 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   An n x n grid holds exactly two islands, where an island is a group of 1s
 *   connected up/down/left/right. Flip some 0s to 1s so the two islands become
 *   one, and return the smallest number of flips needed. 2 <= n <= 100, so the
 *   grid is small enough to modify in place.
 *
 * EXAMPLE
 *   [[0,1],                     ->  1   flip (0,0) or (1,1)
 *    [1,0]]
 *   [[0,1,0],                   ->  2   the two 1s are three steps apart,
 *    [0,0,0],                        so two water cells sit between them
 *    [0,0,1]]
 *   ring of 1s with a single 1 in the middle  ->  1   (tricky case)
 *
 * APPROACH  (DFS to mark one island, then multi-source BFS to expand it)
 *   1. Scan row by row for the first 1. That cell belongs to island A.
 *   2. DFS from it, repainting every cell of island A to 2 and pushing each one
 *      into the queue. Repainting doubles as the visited marker.
 *   3. BFS outward from ALL of island A at once (multi-source, which is why the
 *      whole island goes into the queue, not just one cell). flips counts how
 *      many rings of water have been crossed so far.
 *   4. While expanding a ring: a neighbouring 0 becomes 2 and joins the queue; a
 *      neighbouring 1 can only be island B, so the current flips value is the
 *      answer and we return immediately.
 *
 * KEY INSIGHT
 *   Two traversals with two different jobs: DFS answers "which cells are one
 *   island" (connectivity), BFS answers "how far away is the other one"
 *   (shortest distance). Seeding the BFS with every cell of island A is what
 *   makes the first contact the globally shortest bridge - a single-source BFS
 *   from one arbitrary cell would measure from the wrong place. Recognise the
 *   chain whenever a problem says "distance between two groups", not "between
 *   two points".
 *
 * COMPLEXITY
 *   Time  O(n^2)  the scan, the DFS and the BFS each touch every cell at most
 *                 a constant number of times
 *   Space O(n^2)  the queue can hold a whole ring, and the DFS recursion can go
 *                 as deep as the island is large
 *
 * INTERVIEW FOLLOW-UPS
 *   - More than two islands, connect the two closest: BFS from each island and
 *     take the minimum, or run the same expansion from all islands at once.
 *   - Return the actual cells flipped, not just the count: store a parent per
 *     cell during the BFS and walk it back.
 *   - Diagonal moves allowed: use 8 directions in both traversals.
 *   - Why can the DFS blow the stack, and how do you avoid it? Convert it to an
 *     explicit stack, or use BFS to mark the island too.
 *
 * RUN
 *   main() runs 4 cases (minimum 2x2 grid, a diagonal gap, a ring around a
 *   single cell, two blocks one row apart) and prints actual vs expected.
 *   Note each case gets a fresh grid because the algorithm writes into it.
 */
import java.util.ArrayDeque;
import java.util.Deque;

class ShortestBridgeSolution {

    public int shortestBridge(int[][] grid) {
        int n = grid.length;
        Deque<int[]> queue = new ArrayDeque<>();
        boolean found = false;
        // Direction pairs read as (dirs[d], dirs[d + 1]): up, right, down, left.
        int[] dirs = new int[]{-1, 0, 1, 0, -1};

        // Step 1: find the first island, repaint it to 2 and seed the queue with it
        for (int i = 0; i < n && !found; i++) {
            for (int j = 0; j < n && !found; j++) {
                if (grid[i][j] == 1) {
                    dfsMark(grid, i, j, n, queue, dirs);
                    found = true;
                }
            }
        }

        // Step 2: expand outward one ring of water at a time until island B is hit
        int flips = 0;
        while (!queue.isEmpty()) {
            int size = queue.size(); // freeze the ring before adding the next one
            for (int k = 0; k < size; k++) {
                int[] cell = queue.pollFirst();
                int x = cell[0], y = cell[1];
                for (int d = 0; d < 4; d++) {
                    int nx = x + dirs[d];
                    int ny = y + dirs[d + 1];
                    if (nx < 0 || nx >= n || ny < 0 || ny >= n) continue;
                    if (grid[nx][ny] == 1) {
                        // a 1 that is still a 1 can only belong to the second island
                        return flips;
                    }
                    if (grid[nx][ny] == 0) {
                        grid[nx][ny] = 2; // claim this water so no ring revisits it
                        queue.offerLast(new int[]{nx, ny});
                    }
                }
            }
            flips++; // one more ring of water crossed
        }

        return -1; // unreachable for valid input, which always has two islands
    }

    /** Repaints the island containing (i, j) from 1 to 2 and queues every cell. */
    private void dfsMark(int[][] grid, int i, int j, int n, Deque<int[]> queue, int[] dirs) {
        if (i < 0 || i >= n || j < 0 || j >= n) return;
        if (grid[i][j] != 1) return; // water, or already repainted
        grid[i][j] = 2;
        queue.offerLast(new int[]{i, j});
        for (int d = 0; d < 4; d++) {
            dfsMark(grid, i + dirs[d], j + dirs[d + 1], n, queue, dirs);
        }
    }

    private static void check(String label, int[][] grid, int expected) {
        int actual = new ShortestBridgeSolution().shortestBridge(grid);
        System.out.println(label + " -> " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // edge case: the smallest legal grid, the islands touch diagonally
        check("case 1 tiny 2x2  ", new int[][]{
                {0, 1},
                {1, 0}
        }, 1);

        // typical: two single cells on opposite corners, two waters in between
        check("case 2 diagonal  ", new int[][]{
                {0, 1, 0},
                {0, 0, 0},
                {0, 0, 1}
        }, 2);

        // tricky: a ring island surrounding a one-cell island
        check("case 3 ring      ", new int[][]{
                {1, 1, 1, 1, 1},
                {1, 0, 0, 0, 1},
                {1, 0, 1, 0, 1},
                {1, 0, 0, 0, 1},
                {1, 1, 1, 1, 1}
        }, 1);

        // typical: two multi-cell islands separated by one empty row
        check("case 4 two blocks", new int[][]{
                {1, 1, 0},
                {0, 0, 0},
                {0, 1, 1}
        }, 1);
    }
}

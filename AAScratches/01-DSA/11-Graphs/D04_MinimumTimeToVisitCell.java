/*
 * =====================================================================
 *  Minimum Time to Visit a Cell in a Grid            LeetCode 2577 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   You start at cell (0, 0) of an m x n grid at time 0 and want to reach
 *   (m-1, n-1). Each step to an adjacent cell takes exactly 1 second, and you
 *   may only enter cell (i, j) at a time >= grid[i][j]. grid[0][0] is 0.
 *   Return the earliest arrival time at the bottom-right cell, or -1.
 *
 * EXAMPLE
 *   [[0,1,3,2],[5,1,2,5],[4,3,8,6]]  ->  7    walk down then right, waiting
 *   [[0,2,4],[3,2,1],[1,0,4]]        -> -1    both neighbours of start need t>1
 *   [[0,1],[1,3]]                    ->  4    step out at t=1, bounce, enter at 4
 *
 * APPROACH  (Dijkstra on time, with a parity trick for waiting)
 *   1. dist[i][j] = earliest time we can stand on (i, j). Min-heap of
 *      {time, row, col}, seeded with {0, 0, 0}; pop the smallest time first.
 *   2. Early exit: if m > 1 and n > 1 and BOTH (0,1) and (1,0) need time > 1,
 *      the very first move is impossible, so return -1.
 *   3. For a popped cell at time t and a neighbour v with g = grid[v]:
 *        - if t + 1 >= g we simply walk in and arrive at t + 1;
 *        - otherwise we must burn time first. Stepping back and forth between
 *          two cells costs 2 seconds, so we can only reach v at times with the
 *          same parity as t + 1. Arrival = g if g has that parity, else g + 1.
 *   4. Relax v with that arrival time; the first pop of the target is the answer.
 *
 * KEY INSIGHT
 *   Waiting is free but only in steps of 2, because the only way to idle is to
 *   walk to a neighbour and back. So the reachable arrival times at a cell form
 *   one parity class: earliest = max(g, t+1) rounded UP to the parity of t+1.
 *   Pattern: Dijkstra still works when an edge weight depends on arrival time,
 *   as long as that weight is non-negative and monotone in the arrival time.
 *
 * COMPLEXITY
 *   Time  O(m*n*log(m*n))  every cell enters the heap O(1) times, 4 edges each
 *   Space O(m*n)           dist matrix plus the heap
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why does plain BFS fail here? (edge cost is not always 1 once you wait)
 *   - What if waiting in place were allowed? (drop the parity fix: use max(t+1, g))
 *   - What if each cell also had an entry cost? (add it to the tentative time)
 *   - Reconstruct the actual path, not just the time. (store a parent per cell)
 *
 * RUN
 *   main() runs 4 cases (typical, unreachable, parity wait, smallest grid)
 *   and prints actual vs expected.
 */

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

class MinimumTimeToVisitCell {

    /** Up, right, down, left as (row, col) offset pairs read through a sliding window. */
    private static final int[] DIRS = {-1, 0, 1, 0, -1};

    public static int minimumTime(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;

        // If neither neighbour of the start can be entered at t = 1 we can never
        // take a first step, and with no first step there is nothing to bounce off.
        if (m > 1 && n > 1 && grid[0][1] > 1 && grid[1][0] > 1) {
            return -1;
        }

        int[][] dist = new int[m][n];
        for (int[] row : dist) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        dist[0][0] = 0;

        // Entries are {time, row, col}, ordered by time.
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, 0, 0});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int t = cur[0], i = cur[1], j = cur[2];

            if (i == m - 1 && j == n - 1) {
                return t; // first pop of the target is its final distance
            }
            if (t > dist[i][j]) {
                continue; // stale heap entry, a better time was already settled
            }

            for (int k = 0; k < 4; k++) {
                int ni = i + DIRS[k];
                int nj = j + DIRS[k + 1];
                if (ni < 0 || ni >= m || nj < 0 || nj >= n) {
                    continue;
                }
                int arrival = earliestArrival(t, grid[ni][nj]);
                if (arrival < dist[ni][nj]) {
                    dist[ni][nj] = arrival;
                    pq.offer(new int[]{arrival, ni, nj});
                }
            }
        }

        return -1;
    }

    /**
     * Earliest time we can stand on a cell that opens at {@code openAt}, having
     * left the current cell at time {@code t}. Idling costs 2 seconds a time
     * (step away and back), so only times with the parity of t + 1 are reachable.
     */
    private static int earliestArrival(int t, int openAt) {
        int arrival = t + 1;
        if (arrival < openAt) {
            int wait = openAt - arrival;
            arrival = openAt + (wait % 2); // bump by 1 only when the parity is wrong
        }
        return arrival;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] typical = {
                {0, 1, 3, 2},
                {5, 1, 2, 5},
                {4, 3, 8, 6}
        };
        print("case 1 (typical)   ", minimumTime(typical), 7);

        int[][] blocked = {
                {0, 2, 4},
                {3, 2, 1},
                {1, 0, 4}
        };
        print("case 2 (stuck)     ", minimumTime(blocked), -1);

        // Target opens at 3 but we can only arrive at even times, so we wait to 4.
        int[][] parity = {
                {0, 1},
                {1, 3}
        };
        print("case 3 (parity)    ", minimumTime(parity), 4);

        // Smallest legal grid, no waiting needed anywhere.
        int[][] smallest = {
                {0, 1},
                {1, 2}
        };
        print("case 4 (no waiting)", minimumTime(smallest), 2);
    }
}

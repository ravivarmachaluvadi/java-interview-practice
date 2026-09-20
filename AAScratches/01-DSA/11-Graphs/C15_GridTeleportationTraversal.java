/*
 * =====================================================================
 *  Grid Teleportation Traversal                      LeetCode 3552 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   A grid of characters: '.' is free, '#' is a wall, and any uppercase letter
 *   is a portal. From (0, 0) you may step to an adjacent free cell for 1 move.
 *   The first time you stand on a portal letter you may teleport to any other
 *   cell holding the same letter for 0 moves; each letter is usable once.
 *   Return the fewest moves to reach (m-1, n-1), or -1 if it is unreachable.
 *
 * EXAMPLE
 *   ["A.#.", ".#B#", "....", "B#A."]  ->  1   teleport A(0,0)->A(3,2), step right
 *   ["A.#", "#B#", ".B."]             ->  3   the lone A is useless, B saves a walk
 *   ["A.", ".A"]                      ->  0   the portal lands on the destination
 *
 * APPROACH  (0-1 BFS with an ArrayDeque)
 *   1. Pre-scan the grid into portalMap: letter -> list of its cell positions.
 *   2. dist[][] starts at INF except dist[0][0] = 0. Push (0,0) on a deque.
 *   3. Pop from the FRONT. Two kinds of outgoing edge from cell (i, j):
 *        - teleport, weight 0: if (i,j) holds an unused letter, relax every cell
 *          of that letter to the same distance and push them on the FRONT;
 *        - walk, weight 1: relax the four free neighbours to d + 1 and push
 *          them on the BACK.
 *   4. The first time the destination is popped, its distance is final.
 *
 * KEY INSIGHT
 *   A deque is a two-bucket priority queue. With only weights 0 and 1, "push
 *   front for 0, push back for 1" keeps the deque sorted by distance, so you get
 *   Dijkstra's ordering at BFS cost - no heap, no log factor. Second insight:
 *   a letter only ever needs to fire once, because that first firing already
 *   gives every cell of the letter the minimum distance the group can have.
 *
 * COMPLEXITY
 *   Time  O(m*n + P)   each cell is settled once; P = total portal cells, and
 *                      each letter group is expanded a single time
 *   Space O(m*n)       dist matrix, deque, and the portal map
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is a deque enough here, and when must you fall back to a real heap?
 *   - What changes if teleporting costs 1 instead of 0? (plain BFS, or Dijkstra)
 *   - What if each letter could be used twice? (state becomes cell + uses left)
 *   - Print the route taken, distinguishing walked steps from teleports.
 *
 * RUN
 *   main() runs 5 cases (portal shortcut, portal mid-path, portal onto the goal,
 *   walled-off grid, single cell) and prints actual vs expected.
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class GridTeleportationTraversal {

    private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public static int minMoves(String[] matrix) {
        int m = matrix.length;
        if (m == 0) {
            return -1;
        }
        int n = matrix[0].length();

        // A walled destination can never be entered, whatever the portals do.
        if (matrix[m - 1].charAt(n - 1) == '#') {
            return -1;
        }

        Map<Character, List<int[]>> portalMap = buildPortalMap(matrix, m, n);

        final int INF = Integer.MAX_VALUE / 2;
        int[][] dist = new int[m][n];
        for (int[] row : dist) {
            Arrays.fill(row, INF);
        }
        dist[0][0] = 0;

        Deque<int[]> deque = new ArrayDeque<>();
        deque.offerFirst(new int[]{0, 0});

        // Each letter fires at most once: the first firing already hands every
        // cell of that letter the smallest distance the group can ever have.
        Set<Character> usedPortal = new HashSet<>();

        while (!deque.isEmpty()) {
            int[] pos = deque.pollFirst();
            int i = pos[0], j = pos[1];
            int d = dist[i][j]; // read the current best, the entry itself may be stale

            if (i == m - 1 && j == n - 1) {
                return d; // popped in non-decreasing distance order, so this is final
            }

            char c = matrix[i].charAt(j);
            if (isPortal(c) && usedPortal.add(c)) {
                // Zero-cost edges: same distance, so they go on the FRONT.
                for (int[] p : portalMap.get(c)) {
                    int x = p[0], y = p[1];
                    if (d < dist[x][y]) {
                        dist[x][y] = d;
                        deque.offerFirst(new int[]{x, y});
                    }
                }
            }

            // Cost-one edges: distance d + 1, so they go on the BACK.
            for (int[] dir : DIRS) {
                int ni = i + dir[0], nj = j + dir[1];
                if (ni >= 0 && ni < m && nj >= 0 && nj < n
                        && matrix[ni].charAt(nj) != '#'
                        && d + 1 < dist[ni][nj]) {
                    dist[ni][nj] = d + 1;
                    deque.offerLast(new int[]{ni, nj});
                }
            }
        }

        return -1;
    }

    private static Map<Character, List<int[]>> buildPortalMap(String[] matrix, int m, int n) {
        Map<Character, List<int[]>> portalMap = new HashMap<>();
        for (int i = 0; i < m; i++) {
            String row = matrix[i];
            for (int j = 0; j < n; j++) {
                char c = row.charAt(j);
                if (isPortal(c)) {
                    portalMap.computeIfAbsent(c, k -> new ArrayList<>()).add(new int[]{i, j});
                }
            }
        }
        return portalMap;
    }

    private static boolean isPortal(char c) {
        return c >= 'A' && c <= 'Z';
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // A(0,0) teleports to A(3,2) for free, then one step right to the goal.
        String[] portalShortcut = {
                "A.#.",
                ".#B#",
                "....",
                "B#A."
        };
        print("case 1 (portal shortcut)", minMoves(portalShortcut), 1);

        // The single A is a dead end; the two Bs save the walk across row 1.
        String[] portalMidPath = {
                "A.#",
                "#B#",
                ".B."
        };
        print("case 2 (portal mid path)", minMoves(portalMidPath), 3);

        // Teleporting lands directly on the destination, so zero moves.
        String[] portalOntoGoal = {
                "A.",
                ".A"
        };
        print("case 3 (portal on goal) ", minMoves(portalOntoGoal), 0);

        // Both routes out of the start are walls and there is no portal to use.
        String[] walledOff = {
                ".#",
                "#."
        };
        print("case 4 (unreachable)    ", minMoves(walledOff), -1);

        // Edge case: start cell is already the destination.
        String[] singleCell = {"."};
        print("case 5 (single cell)    ", minMoves(singleCell), 0);
    }
}

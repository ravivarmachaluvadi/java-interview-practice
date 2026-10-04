/*
 * =====================================================================
 *  P067 BFS Shortest Path (Unweighted, incl. State Space)   Canonical LC 1091 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 1091, Shortest Path in Binary Matrix)
 *   In an n x n grid of 0 (open) and 1 (blocked), return the number of cells on the
 *   shortest 8-directional path from the top-left to the bottom-right, or -1.
 *
 * EXAMPLE
 *   [[0,1],[1,0]]                  ->  2
 *   [[0,0,0],[1,1,0],[1,1,0]]      ->  4
 *   [[1,0,0],[1,1,0],[1,1,0]]      ->  -1     the start itself is blocked
 *
 * RECOGNIZE WHEN
 *   - "minimum number of steps / moves / transformations" where every step costs the same.
 *   - The "graph" may be implicit: lock combinations, words one letter apart, a position
 *     PLUS extra state (keys held, obstacles removed so far).
 *   Not this if: steps have different costs -> P072_Dijkstra (0/1 costs: 0-1 BFS); you need
 *   the distance from MANY sources at once -> P068_MultiSourceBfs.
 *
 * TEMPLATE
 *   queue = [start]; seen = {start}; steps = 0
 *   while queue:
 *       for size of the current level:
 *           state = queue.poll()
 *           if state is the goal: return steps
 *           for next in neighbours(state):
 *               if next is valid and next not in seen: seen.add(next); queue.add(next)
 *       steps++
 *   return -1
 *   state space: state = (position, extra); seen is over the WHOLE state
 *
 * APPROACH
 *   1. BFS from (0, 0) over the 8 neighbours of each open cell.
 *   2. The level at which (n-1, n-1) is first popped is the shortest path (in cells).
 *
 * KEY INSIGHT
 *   BFS explores in rings of equal distance, so the first time it reaches the goal is via a
 *   shortest path, as long as every edge has the same cost. The trick in harder problems is
 *   choosing the STATE: if "how you got here" matters (k eliminations left), it is part of
 *   the node.
 *
 * COMPLEXITY
 *   Time O(states * branching), space O(states). LC 1091: O(n^2).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 752  Open the Lock            state = 4-digit string; 8 neighbours (+-1 per
 *                                            wheel); deadends start as seen
 *   [coded] LC 127  Word Ladder              neighbours = change one letter to a..z and
 *                                            keep it if it is in the word set
 *   [coded] LC 1293 Path With Obstacle Elim. state = (r, c, eliminations left)
 *           LC 433  Minimum Genetic Mutation LC 127 with the alphabet ACGT
 *           LC 909  Snakes and Ladders       board squares as nodes; dice roll = 6 edges
 *           LC 864  Shortest Path to Get Keys state = (r, c, bitmask of keys held)
 *           LC 1730 Shortest Path to Food    plain grid BFS
 *
 * PITFALLS
 *   - Mark seen when you ENQUEUE, not when you dequeue, or states are queued many times.
 *   - Check the start (blocked? already the goal?) before the loop.
 *   - State-space BFS: seen must include the extra state, or valid paths get pruned.
 *
 * DEEP DIVE
 *   B08_ShortestPathInBinaryMatrix, C03_OpenTheLock, D01_WordLadder (11-Graphs)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class BfsShortestPath {

    // Canonical LC 1091.
    static int shortestPathBinaryMatrix(int[][] grid) {
        int n = grid.length;
        if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1) {
            return -1;
        }
        boolean[][] seen = new boolean[n][n];
        Deque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{0, 0});
        seen[0][0] = true;
        int cells = 1;
        while (!queue.isEmpty()) {
            for (int size = queue.size(); size > 0; size--) {
                int[] cur = queue.poll();
                if (cur[0] == n - 1 && cur[1] == n - 1) {
                    return cells;
                }
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int r = cur[0] + dr;
                        int c = cur[1] + dc;
                        if (r >= 0 && c >= 0 && r < n && c < n && grid[r][c] == 0 && !seen[r][c]) {
                            seen[r][c] = true;
                            queue.add(new int[]{r, c});
                        }
                    }
                }
            }
            cells++;
        }
        return -1;
    }

    // LC 752.
    static int openLock(String[] deadends, String target) {
        Set<String> seen = new HashSet<>(Arrays.asList(deadends));
        if (seen.contains("0000")) {
            return -1;
        }
        Deque<String> queue = new ArrayDeque<>();
        queue.add("0000");
        seen.add("0000");
        int turns = 0;
        while (!queue.isEmpty()) {
            for (int size = queue.size(); size > 0; size--) {
                String cur = queue.poll();
                if (cur.equals(target)) {
                    return turns;
                }
                for (int i = 0; i < 4; i++) {
                    for (int delta : new int[]{1, 9}) {    // +1 or -1 mod 10
                        char[] c = cur.toCharArray();
                        c[i] = (char) ('0' + (c[i] - '0' + delta) % 10);
                        String next = new String(c);
                        if (seen.add(next)) {
                            queue.add(next);
                        }
                    }
                }
            }
            turns++;
        }
        return -1;
    }

    // LC 127: number of WORDS in the shortest sequence (0 if impossible).
    static int ladderLength(String begin, String end, List<String> wordList) {
        Set<String> words = new HashSet<>(wordList);
        if (!words.contains(end)) {
            return 0;
        }
        Deque<String> queue = new ArrayDeque<>();
        queue.add(begin);
        words.remove(begin);
        int length = 1;
        while (!queue.isEmpty()) {
            for (int size = queue.size(); size > 0; size--) {
                String cur = queue.poll();
                if (cur.equals(end)) {
                    return length;
                }
                char[] c = cur.toCharArray();
                for (int i = 0; i < c.length; i++) {
                    char original = c[i];
                    for (char ch = 'a'; ch <= 'z'; ch++) {
                        c[i] = ch;
                        String next = new String(c);
                        if (words.remove(next)) {          // removal doubles as "seen"
                            queue.add(next);
                        }
                    }
                    c[i] = original;
                }
            }
            length++;
        }
        return 0;
    }

    // LC 1293: you may remove up to k obstacles; 4-directional moves.
    static int shortestPath(int[][] grid, int k) {
        int rows = grid.length;
        int cols = grid[0].length;
        boolean[][][] seen = new boolean[rows][cols][k + 1];
        Deque<int[]> queue = new ArrayDeque<>();              // {r, c, eliminations left}
        queue.add(new int[]{0, 0, k});
        seen[0][0][k] = true;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int steps = 0;
        while (!queue.isEmpty()) {
            for (int size = queue.size(); size > 0; size--) {
                int[] cur = queue.poll();
                if (cur[0] == rows - 1 && cur[1] == cols - 1) {
                    return steps;
                }
                for (int[] d : dirs) {
                    int r = cur[0] + d[0];
                    int c = cur[1] + d[1];
                    if (r < 0 || c < 0 || r >= rows || c >= cols) {
                        continue;
                    }
                    int left = cur[2] - grid[r][c];
                    if (left >= 0 && !seen[r][c][left]) {
                        seen[r][c][left] = true;
                        queue.add(new int[]{r, c, left});
                    }
                }
            }
            steps++;
        }
        return -1;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 1091 2x2 diagonal", shortestPathBinaryMatrix(new int[][]{{0, 1}, {1, 0}}), 2);
        check("LC 1091 3x3",
                shortestPathBinaryMatrix(new int[][]{{0, 0, 0}, {1, 1, 0}, {1, 1, 0}}), 4);
        check("LC 1091 blocked start",
                shortestPathBinaryMatrix(new int[][]{{1, 0, 0}, {1, 1, 0}, {1, 1, 0}}), -1);
        check("LC 1091 single open cell", shortestPathBinaryMatrix(new int[][]{{0}}), 1);

        check("LC 752 target 0202",
                openLock(new String[]{"0201", "0101", "0102", "1212", "2002"}, "0202"), 6);
        check("LC 752 wrap around 9", openLock(new String[]{"8888"}, "0009"), 1);
        check("LC 752 target walled in",
                openLock(new String[]{"8887", "8889", "8878", "8898", "8788", "8988",
                "7888", "9888"}, "8888"), -1);
        check("LC 752 0000 is a deadend", openLock(new String[]{"0000"}, "8888"), -1);

        List<String> words = List.of("hot", "dot", "dog", "lot", "log", "cog");
        check("LC 127 hit to cog", ladderLength("hit", "cog", words), 5);
        check("LC 127 end missing",
                ladderLength("hit", "cog", List.of("hot", "dot", "dog", "lot", "log")), 0);

        check("LC 1293 k=1",
                shortestPath(new int[][]{{0, 0, 0}, {1, 1, 0}, {0, 0, 0}, {0, 1, 1}, {0, 0, 0}}, 1),
                6);
        check("LC 1293 k=1 not enough",
                shortestPath(new int[][]{{0, 1, 1}, {1, 1, 1}, {1, 0, 0}}, 1), -1);
    }
}

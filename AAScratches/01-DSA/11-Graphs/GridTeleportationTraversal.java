import java.util.*;

// 3552. Grid Teleportation Traversal
// https://leetcode.com/problems/grid-teleportation-traversal/description/
class GridTeleportationTraversal {
    public static int minMoves(String[] matrix) {
        int m = matrix.length;
        if (m == 0) return -1;
        int n = matrix[0].length();

        // If destination is obstacle, cannot reach
        if (matrix[m - 1].charAt(n - 1) == '#') return -1;

        // Map portals: letter -> list of positions
        Map<Character, List<int[]>> portalMap = new HashMap<>();
        for (int i = 0; i < m; i++) {
            String row = matrix[i];
            for (int j = 0; j < n; j++) {
                char c = row.charAt(j);
                if (c >= 'A' && c <= 'Z') {
                    portalMap.computeIfAbsent(c, k -> new ArrayList<>()).add(new int[]{i, j});
                }
            }
        }

        // Directions for moving up/down/left/right
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        // Distance array
        final int INF = Integer.MAX_VALUE / 2;
        int[][] dist = new int[m][n];
        for (int i = 0; i < m; i++) {
            Arrays.fill(dist[i], INF);
        }
        dist[0][0] = 0;

        // Deque for 0-1 BFS
        Deque<int[]> deque = new ArrayDeque<>();
        deque.offerFirst(new int[]{0, 0});

        // Track which portal letters have been used
        Set<Character> usedPortal = new HashSet<>();

        while (!deque.isEmpty()) {
            int[] pos = deque.pollFirst();
            int i = pos[0], j = pos[1];
            int d = dist[i][j];

            // If reached destination
            if (i == m - 1 && j == n - 1) {
                return d;
            }

            char c = matrix[i].charAt(j);
            // Teleportation via portals
            if (c >= 'A' && c <= 'Z' && !usedPortal.contains(c)) {
                usedPortal.add(c);
                List<int[]> list = portalMap.get(c);
                if (list != null) {
                    for (int[] p : list) {
                        int x = p[0], y = p[1];
                        if (d < dist[x][y]) {
                            dist[x][y] = d;
                            deque.offerFirst(new int[]{x, y});
                        }
                    }
                }
            }

            // Adjacent moves cost 1
            for (int[] dir : dirs) {
                int ni = i + dir[0], nj = j + dir[1];
                if (ni >= 0 && ni < m && nj >= 0 && nj < n
                        && matrix[ni].charAt(nj) != '#'
                        && d + 1 < dist[ni][nj]) {
                    dist[ni][nj] = d + 1;
                    deque.offerLast(new int[]{ni, nj});
                }
            }
        }

        // Destination unreachable
        return -1;
    }

    public static void main(String[] args) {
        String[] matrix1 = {
                "A.#.",
                ".#B#",
                "....",
                "B#A."
        };

        int result1 = minMoves(matrix1);
        System.out.println("Minimum moves: " + result1);
        // Explanation:
        // Start at (0,0) 'A' -> teleport to (3,2) 'A' (0 cost)
        // (3,2) -> (3,3) -> destination, total cost = 1
        // Output: 1

        String[] matrix2 = {
                "A.#",
                "#B#",
                ".B."
        };
        int result2 = minMoves(matrix2);
        System.out.println("Minimum moves: " + result2);
        // Output: 3
    }
}

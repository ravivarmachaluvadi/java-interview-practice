/*
 * =====================================================================
 *  P072 Dijkstra (and 0-1 BFS)   Canonical LC 743 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 743, Network Delay Time)
 *   times[i] = (u, v, w): a signal takes w time from u to v. A signal starts at node k.
 *   Return the time until all n nodes have it, or -1 if some never do.
 *
 * EXAMPLE
 *   [[2,1,1],[2,3,1],[3,4,1]], n = 4, k = 2  ->  2
 *   [[1,2,1]], n = 2, k = 1                  ->  1
 *   [[1,2,1]], n = 2, k = 2                  ->  -1     node 1 is unreachable
 *
 * RECOGNIZE WHEN
 *   - Shortest / cheapest path with NON-NEGATIVE weights that differ per edge.
 *   - A path's cost is not a sum but still "monotonic": max edge (minimax effort),
 *     product of probabilities (max-heap), number of shortest paths.
 *   - Edge weights are only 0 or 1: a deque replaces the heap (0-1 BFS).
 *   Not this if: all weights equal -> P067_BfsShortestPath; negative weights or "at most k
 *   edges" -> P073_BellmanFordFloyd.
 *
 * TEMPLATE
 *   dist[*] = infinity; dist[src] = 0; heap = [(0, src)]
 *   while heap:
 *       (d, u) = heap.poll()
 *       if d > dist[u]: continue                       // stale entry: skip
 *       for (v, w) in adj[u]:
 *           if d + w < dist[v]: dist[v] = d + w; heap.add((dist[v], v))
 *   0-1 BFS: deque; weight 0 -> addFirst, weight 1 -> addLast; same relax rule
 *
 * APPROACH
 *   1. Pop the closest unsettled node; its distance is final.
 *   2. Relax its outgoing edges; push improved neighbours.
 *   3. The answer is the largest final distance (or -1 if any stays infinite).
 *
 * KEY INSIGHT
 *   With non-negative weights, the closest node in the heap can never be improved later,
 *   because any other route goes through nodes that are at least as far. "Skip stale
 *   entries" replaces a decrease-key operation and keeps the code short.
 *
 * COMPLEXITY
 *   O((V + E) log V) with a binary heap; 0-1 BFS O(V + E). Space O(V + E).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 1631 Path With Min Effort     cost = max |height difference| along the path:
 *                                            relax with max(d, edge) instead of d + edge
 *   [coded] LC 1514 Max Probability Path     MAX-heap; relax with d * p (probabilities <= 1)
 *   [coded] LC 1976 Ways to Arrive           count paths: equal distance adds the ways,
 *                                            a shorter one resets them (mod 1e9 + 7)
 *   [coded] LC 1368 Min Cost Valid Path      grid; following the arrow costs 0, changing it
 *                                            costs 1 -> 0-1 BFS with a deque
 *           LC 778  Swim in Rising Water     minimax like LC 1631 (or binary search + BFS)
 *           LC 2290 Min Obstacle Removal     0-1 BFS: empty 0, obstacle 1
 *           LC 787  Cheapest Flights, k stops  Dijkstra on (node, stops) or
 *                                            -> P073_BellmanFordFloyd
 *
 * PITFALLS
 *   - Negative edges break Dijkstra silently.
 *   - Without the stale check the algorithm is still correct but much slower.
 *   - Use long distances when weights times path length can exceed int.
 *
 * DEEP DIVE
 *   A06_DijkstrasAlgoPQ, C14_NumberOfWaysToArriveAtDestination, C11_ShortestPath,
 *   D05_MinimumTimeToVisitCell (11-Graphs)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;

class Dijkstra {

    // Canonical LC 743. Nodes are 1..n.
    static int networkDelayTime(int[][] times, int n, int k) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] t : times) {
            adj.get(t[0]).add(new int[]{t[1], t[2]});
        }
        long[] dist = new long[n + 1];
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[k] = 0;
        PriorityQueue<long[]> heap = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        heap.add(new long[]{0, k});
        while (!heap.isEmpty()) {
            long[] top = heap.poll();
            int u = (int) top[1];
            if (top[0] > dist[u]) {
                continue;                          // stale
            }
            for (int[] e : adj.get(u)) {
                if (top[0] + e[1] < dist[e[0]]) {
                    dist[e[0]] = top[0] + e[1];
                    heap.add(new long[]{dist[e[0]], e[0]});
                }
            }
        }
        long worst = 0;
        for (int v = 1; v <= n; v++) {
            if (dist[v] == Long.MAX_VALUE) {
                return -1;
            }
            worst = Math.max(worst, dist[v]);
        }
        return (int) worst;
    }

    static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    // LC 1631: minimise the largest step along the path.
    static int minimumEffortPath(int[][] h) {
        int rows = h.length;
        int cols = h[0].length;
        int[][] effort = new int[rows][cols];
        for (int[] row : effort) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        effort[0][0] = 0;
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        heap.add(new int[]{0, 0, 0});
        while (!heap.isEmpty()) {
            int[] top = heap.poll();
            int r = top[1];
            int c = top[2];
            if (top[0] > effort[r][c]) {
                continue;
            }
            if (r == rows - 1 && c == cols - 1) {
                return top[0];
            }
            for (int[] d : DIRS) {
                int nr = r + d[0];
                int nc = c + d[1];
                if (nr < 0 || nc < 0 || nr >= rows || nc >= cols) {
                    continue;
                }
                int e = Math.max(top[0], Math.abs(h[nr][nc] - h[r][c]));
                if (e < effort[nr][nc]) {
                    effort[nr][nc] = e;
                    heap.add(new int[]{e, nr, nc});
                }
            }
        }
        return 0;
    }

    // LC 1514: undirected; maximise the product of success probabilities.
    static double maxProbability(int n, int[][] edges, double[] succProb, int start, int end) {
        List<List<double[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int i = 0; i < edges.length; i++) {
            adj.get(edges[i][0]).add(new double[]{edges[i][1], succProb[i]});
            adj.get(edges[i][1]).add(new double[]{edges[i][0], succProb[i]});
        }
        double[] best = new double[n];
        best[start] = 1.0;
        PriorityQueue<double[]> heap = new PriorityQueue<>((a, b) -> Double.compare(b[0], a[0]));
        heap.add(new double[]{1.0, start});
        while (!heap.isEmpty()) {
            double[] top = heap.poll();
            int u = (int) top[1];
            if (top[0] < best[u]) {
                continue;
            }
            if (u == end) {
                return top[0];
            }
            for (double[] e : adj.get(u)) {
                int v = (int) e[0];
                if (top[0] * e[1] > best[v]) {
                    best[v] = top[0] * e[1];
                    heap.add(new double[]{best[v], v});
                }
            }
        }
        return 0.0;
    }

    // LC 1976: number of shortest paths from 0 to n - 1, undirected roads.
    static int countPaths(int n, int[][] roads) {
        final long mod = 1_000_000_007L;
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] r : roads) {
            adj.get(r[0]).add(new int[]{r[1], r[2]});
            adj.get(r[1]).add(new int[]{r[0], r[2]});
        }
        long[] dist = new long[n];
        long[] ways = new long[n];
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[0] = 0;
        ways[0] = 1;
        PriorityQueue<long[]> heap = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        heap.add(new long[]{0, 0});
        while (!heap.isEmpty()) {
            long[] top = heap.poll();
            int u = (int) top[1];
            if (top[0] > dist[u]) {
                continue;
            }
            for (int[] e : adj.get(u)) {
                long nd = top[0] + e[1];
                if (nd < dist[e[0]]) {
                    dist[e[0]] = nd;
                    ways[e[0]] = ways[u];          // a strictly shorter route resets the count
                    heap.add(new long[]{nd, e[0]});
                } else if (nd == dist[e[0]]) {
                    ways[e[0]] = (ways[e[0]] + ways[u]) % mod;
                }
            }
        }
        return (int) ways[n - 1];
    }

    // LC 1368: 1 right, 2 left, 3 down, 4 up; changing a cell's arrow costs 1.
    static int minCost(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        int[][] move = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        int[][] dist = new int[rows][cols];
        for (int[] row : dist) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        dist[0][0] = 0;
        Deque<int[]> deque = new ArrayDeque<>();
        deque.add(new int[]{0, 0});
        while (!deque.isEmpty()) {
            int[] cur = deque.pollFirst();
            int r = cur[0];
            int c = cur[1];
            for (int k = 0; k < 4; k++) {
                int nr = r + move[k][0];
                int nc = c + move[k][1];
                if (nr < 0 || nc < 0 || nr >= rows || nc >= cols) {
                    continue;
                }
                int cost = grid[r][c] == k + 1 ? 0 : 1;
                if (dist[r][c] + cost < dist[nr][nc]) {
                    dist[nr][nc] = dist[r][c] + cost;
                    if (cost == 0) {
                        deque.addFirst(new int[]{nr, nc});
                    } else {
                        deque.addLast(new int[]{nr, nc});
                    }
                }
            }
        }
        return dist[rows - 1][cols - 1];
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 743 from 2",
                networkDelayTime(new int[][]{{2, 1, 1}, {2, 3, 1}, {3, 4, 1}}, 4, 2), 2);
        check("LC 743 one edge", networkDelayTime(new int[][]{{1, 2, 1}}, 2, 1), 1);
        check("LC 743 unreachable", networkDelayTime(new int[][]{{1, 2, 1}}, 2, 2), -1);
        check("LC 743 longer path is cheaper",
                networkDelayTime(new int[][]{{1, 2, 10}, {1, 3, 1}, {3, 2, 1}}, 3, 1), 2);

        check("LC 1631 3x3 effort 2",
                minimumEffortPath(new int[][]{{1, 2, 2}, {3, 8, 2}, {5, 3, 5}}), 2);
        check("LC 1631 3x3 effort 1",
                minimumEffortPath(new int[][]{{1, 2, 3}, {3, 8, 4}, {5, 3, 5}}), 1);
        check("LC 1631 flat route exists",
                minimumEffortPath(new int[][]{{1, 2, 1, 1, 1}, {1, 2, 1, 2, 1},
                {1, 2, 1, 2, 1}, {1, 2, 1, 2, 1}, {1, 1, 1, 2, 1}}), 0);

        int[][] tri = {{0, 1}, {1, 2}, {0, 2}};
        check("LC 1514 via node 1",
                maxProbability(3, tri, new double[]{0.5, 0.5, 0.2}, 0, 2), 0.25);
        check("LC 1514 direct edge",
                maxProbability(3, tri, new double[]{0.5, 0.5, 0.3}, 0, 2), 0.3);
        check("LC 1514 unreachable",
                maxProbability(3, new int[][]{{0, 1}}, new double[]{0.5}, 0, 2), 0.0);

        int[][] roads = {{0, 6, 7}, {0, 1, 2}, {1, 2, 3}, {1, 3, 3}, {6, 3, 3},
                {3, 5, 1}, {6, 5, 1}, {2, 5, 1}, {0, 4, 5}, {4, 6, 2}};
        check("LC 1976 four shortest routes", countPaths(7, roads), 4);
        check("LC 1976 single road", countPaths(2, new int[][]{{1, 0, 10}}), 1);

        check("LC 1368 3 changes",
                minCost(new int[][]{{1, 1, 1, 1}, {2, 2, 2, 2}, {1, 1, 1, 1}, {2, 2, 2, 2}}), 3);
        check("LC 1368 already valid", minCost(new int[][]{{1, 1, 3}, {3, 2, 2}, {1, 1, 4}}), 0);
        check("LC 1368 2x2", minCost(new int[][]{{1, 2}, {4, 3}}), 1);
    }
}

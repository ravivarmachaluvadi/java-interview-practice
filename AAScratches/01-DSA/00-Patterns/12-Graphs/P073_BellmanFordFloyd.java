/*
 * =====================================================================
 *  P073 Bellman-Ford and Floyd-Warshall   Canonical LC 787 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 787, Cheapest Flights Within K Stops)
 *   flights[i] = (from, to, price). Return the cheapest price from src to dst using at
 *   most k stops (at most k + 1 flights), or -1.
 *
 * EXAMPLE
 *   4 cities, [[0,1,100],[1,2,100],[2,0,100],[1,3,600],[2,3,200]], 0 -> 3, k = 1  ->  700
 *   3 cities, [[0,1,100],[1,2,100],[0,2,500]], 0 -> 2, k = 1                       ->  200
 *   same, k = 0                                                                   ->  500
 *
 * RECOGNIZE WHEN
 *   - Shortest path with a LIMIT on the number of edges ("at most k stops").
 *   - NEGATIVE edge weights, or "detect a negative cycle" (arbitrage).
 *   - ALL-PAIRS distances on a small graph (n <= ~400): "the city with the fewest
 *     reachable cities", "is u a prerequisite of v" for many queries.
 *   Not this if: weights are non-negative and there is no edge limit -> P072_Dijkstra.
 *
 * TEMPLATE
 *   Bellman-Ford: dist[src] = 0; repeat (n - 1) times (or k + 1 for "k stops"):
 *                     copy = dist                       // copy = use only last round's values
 *                     for (u, v, w): if dist[u] + w < copy[v]: copy[v] = dist[u] + w
 *                     dist = copy
 *                 one more round still improves -> negative cycle
 *   Floyd:        for k: for i: for j: d[i][j] = min(d[i][j], d[i][k] + d[k][j])   // k outermost
 *
 * APPROACH
 *   1. After round r, dist[v] is the cheapest price using at most r flights.
 *   2. Relax every flight from a COPY of the previous round, so one round adds one flight.
 *   3. After k + 1 rounds, dist[dst] is the answer.
 *
 * KEY INSIGHT
 *   Bellman-Ford's round number IS the edge count, which is exactly the "at most k stops"
 *   constraint Dijkstra cannot express. Floyd-Warshall's outer loop says "paths may now pass
 *   through nodes 0..k", a DP over allowed intermediate nodes.
 *
 * COMPLEXITY
 *   Bellman-Ford O(rounds * E); Floyd O(n^3) time, O(n^2) space.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 1334 City With Fewest Neighbors  Floyd, then count dist <= threshold per city;
 *                                            ties go to the larger index
 *   [coded] Negative cycle detection         an n-th round that still relaxes an edge
 *   [coded] Shortest paths w/ negative edges plain Bellman-Ford, n - 1 rounds
 *           LC 1462 Course Schedule IV       Floyd on booleans (transitive closure)
 *           LC 2976 Min Cost to Convert String  Floyd over 26 letters, then sum per position
 *           LC 787 with Dijkstra             state (city, flights used)
 *
 * PITFALLS
 *   - "k stops" means k + 1 edges, so k + 1 rounds.
 *   - Relaxing in place (without the copy) lets one round chain several edges and breaks
 *     the stop limit.
 *   - Floyd: k must be the OUTER loop; and guard INF + INF overflow.
 *
 * DEEP DIVE
 *   A07_BellmanFord, A08_FloydWarshallAlgorithm, C12_CheapestFlight, C13_FindTheCity
 *   (11-Graphs)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;

class BellmanFordFloyd {

    static final int INF = Integer.MAX_VALUE / 2;          // INF + INF still fits in int

    // Canonical LC 787.
    static int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        int[] dist = new int[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        for (int round = 0; round <= k; round++) {
            int[] next = dist.clone();
            for (int[] f : flights) {
                if (dist[f[0]] + f[2] < next[f[1]]) {
                    next[f[1]] = dist[f[0]] + f[2];
                }
            }
            dist = next;
        }
        return dist[dst] >= INF ? -1 : dist[dst];
    }

    // LC 1334: the city that reaches the fewest others within the threshold (ties: largest).
    static int findTheCity(int n, int[][] edges, int threshold) {
        int[][] d = new int[n][n];
        for (int[] row : d) {
            Arrays.fill(row, INF);
        }
        for (int i = 0; i < n; i++) {
            d[i][i] = 0;
        }
        for (int[] e : edges) {
            d[e[0]][e[1]] = e[2];
            d[e[1]][e[0]] = e[2];
        }
        for (int via = 0; via < n; via++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    d[i][j] = Math.min(d[i][j], d[i][via] + d[via][j]);
                }
            }
        }
        int best = -1;
        int fewest = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            int reach = 0;
            for (int j = 0; j < n; j++) {
                if (i != j && d[i][j] <= threshold) {
                    reach++;
                }
            }
            if (reach <= fewest) {
                fewest = reach;
                best = i;
            }
        }
        return best;
    }

    // Bellman-Ford distances from src; null if a negative cycle is reachable.
    static int[] bellmanFord(int n, int[][] edges, int src) {
        int[] dist = new int[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        for (int round = 0; round < n - 1; round++) {
            for (int[] e : edges) {
                if (dist[e[0]] < INF && dist[e[0]] + e[2] < dist[e[1]]) {
                    dist[e[1]] = dist[e[0]] + e[2];
                }
            }
        }
        for (int[] e : edges) {
            if (dist[e[0]] < INF && dist[e[0]] + e[2] < dist[e[1]]) {
                return null;                       // still improving: negative cycle
            }
        }
        return dist;
    }

    static boolean hasNegativeCycle(int n, int[][] edges, int src) {
        return bellmanFord(n, edges, src) == null;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] four = {{0, 1, 100}, {1, 2, 100}, {2, 0, 100}, {1, 3, 600}, {2, 3, 200}};
        check("LC 787 k=1 four cities", findCheapestPrice(4, four, 0, 3, 1), 700);
        int[][] three = {{0, 1, 100}, {1, 2, 100}, {0, 2, 500}};
        check("LC 787 k=1 one stop", findCheapestPrice(3, three, 0, 2, 1), 200);
        check("LC 787 k=0 direct only", findCheapestPrice(3, three, 0, 2, 0), 500);
        check("LC 787 unreachable", findCheapestPrice(3, new int[][]{{0, 1, 100}}, 0, 2, 1), -1);

        check("LC 1334 threshold 4",
                findTheCity(4, new int[][]{{0, 1, 3}, {1, 2, 1}, {1, 3, 4}, {2, 3, 1}}, 4), 3);
        int[][] sixEdges = {{0, 1, 2}, {0, 4, 8}, {1, 2, 3}, {1, 4, 2}, {2, 3, 1}, {3, 4, 1}};
        check("LC 1334 threshold 2", findTheCity(5, sixEdges, 2), 0);

        check("Bellman-Ford with a negative edge",
                Arrays.toString(bellmanFord(3, new int[][]{{0, 1, 4}, {0, 2, 5}, {2, 1, -3}}, 0)),
                "[0, 2, 5]");
        check("negative cycle present",
                hasNegativeCycle(3, new int[][]{{0, 1, 1}, {1, 2, -1}, {2, 0, -1}}, 0), true);
        check("cycle but not negative",
                hasNegativeCycle(3, new int[][]{{0, 1, 1}, {1, 2, -1}, {2, 0, 1}}, 0), false);
    }
}

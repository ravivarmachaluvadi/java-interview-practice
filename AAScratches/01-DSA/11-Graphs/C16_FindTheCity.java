/*
 * =====================================================================
 *  Find the City With the Smallest Number of Neighbors     LeetCode 1334 | Medium
 *  at a Threshold Distance
 * =====================================================================
 *
 * PROBLEM
 *   n cities numbered 0..n-1 are joined by weighted bidirectional edges
 *   edges[i] = {u, v, weight}. For each city count how many other cities are
 *   reachable with a total path weight of at most distanceThreshold. Return the
 *   city with the smallest such count; if several tie, return the largest
 *   numbered one. n is small (up to 100), so an all-pairs table is affordable.
 *
 * EXAMPLE
 *   n=4, edges={{0,1,3},{1,2,1},{1,3,4},{2,3,1}}, threshold=4  ->  3
 *     city 0 reaches 1 (3) and 2 (4)              -> 2
 *     city 1 reaches 0 (3), 2 (1), 3 (2)          -> 3
 *     city 2 reaches 0 (4), 1 (1), 3 (1)          -> 3
 *     city 3 reaches 1 (2), 2 (1); 0 is 5, too far -> 2
 *     0 and 3 tie at 2, so the larger index 3 wins
 *   n=3, edges={} , threshold=5  ->  2   nobody reaches anybody, tie broken high
 *
 * APPROACH  (Floyd-Warshall, then a threshold count)
 *   1. Build an n x n matrix: 0 on the diagonal, the edge weight for a direct
 *      road, INF otherwise. Keep the smaller weight if the input repeats a pair.
 *   2. Floyd-Warshall: for every intermediate k, then every i, then every j,
 *      shorten dist[i][j] to dist[i][k] + dist[k][j] when that is better.
 *   3. For each city count the entries in its row that are within the
 *      threshold, skipping itself. Track the minimum count, and because the
 *      scan runs low to high, overwrite on a tie so the largest index survives.
 *
 * KEY INSIGHT
 *   Floyd-Warshall's loop order is the whole algorithm: after the k-th outer
 *   pass, dist[i][j] is the best route allowed to pass only through cities
 *   0..k. The intermediate node must be the OUTERMOST loop - swap it inside
 *   and the table is simply wrong. Reach for this whenever you need every pair
 *   and n is around a hundred; n separate Dijkstra runs are the answer once
 *   the graph is large and sparse.
 *
 * COMPLEXITY
 *   Time  O(n^3)   three nested loops; the final count is only O(n^2)
 *   Space O(n^2)   the distance matrix
 *
 * INTERVIEW FOLLOW-UPS
 *   - When is n Dijkstras (O(n * E log n)) faster than one Floyd-Warshall?
 *   - Reconstruct the actual path: keep a next[][] or parent[][] table.
 *   - How does Floyd-Warshall detect a negative cycle? (dist[i][i] < 0)
 *   - Reverse the tie-break to the smallest index - what changes?
 *
 * RUN
 *   main() runs 4 cases (the two LeetCode examples, an empty-graph edge case,
 *   and a threshold that admits nothing) and prints actual vs expected.
 */

import java.util.*;

class FindTheCity {

    public static int findTheCity(int n, int[][] edges, int distanceThreshold) {
        // halved so that INF + INF still fits in an int and never looks "shorter"
        final int INF = Integer.MAX_VALUE / 2;

        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
        }

        // seed the direct roads; min() guards against a duplicated pair
        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            dist[u][v] = Math.min(dist[u][v], w);
            dist[v][u] = Math.min(dist[v][u], w);
        }

        // Floyd-Warshall. k is outermost: after pass k every entry is the best
        // route that is allowed to hop through cities 0..k only.
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        int resultCity = -1;
        int minReachable = Integer.MAX_VALUE;

        for (int i = 0; i < n; i++) {
            int count = 0;
            for (int j = 0; j < n; j++) {
                if (i != j && dist[i][j] <= distanceThreshold) count++;
            }
            // i climbs, so "<=" on a tie keeps overwriting and the largest
            // qualifying index is the one left standing
            if (count <= minReachable) {
                minReachable = count;
                resultCity = i;
            }
        }
        return resultCity;
    }

    /* ---------- demo ---------- */
    public static void main(String[] args) {
        int[][] four = {{0, 1, 3}, {1, 2, 1}, {1, 3, 4}, {2, 3, 1}};
        int[][] five = {{0, 1, 2}, {0, 4, 8}, {1, 2, 3}, {1, 4, 2}, {2, 3, 1}, {3, 4, 1}};

        // typical: 0 and 3 both reach two cities, the tie-break picks 3
        print("typical n=4, threshold 4", findTheCity(4, four, 4), 3);

        // typical: city 0 only reaches city 1 within distance 2
        print("typical n=5, threshold 2", findTheCity(5, five, 2), 0);

        // edge: no roads at all, every count is 0, largest index wins
        print("edge: no edges", findTheCity(3, new int[0][0], 5), 2);

        // tricky: threshold 0 rules out every road, so the tie-break decides
        print("tricky: threshold 0", findTheCity(2, new int[][]{{0, 1, 1}}, 0), 1);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

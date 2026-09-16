import java.util.*;

// 1334. Find the City With the Smallest Number of Neighbors at a Threshold Distance
// https://leetcode.com/problems/find-the-city-with-the-smallest-number-of-neighbors-at-a-threshold-distance/description/
class FindTheCity {
    public static int findTheCity(int n, int[][] edges, int distanceThreshold) {
        // initialize distance matrix with large values
        int INF = Integer.MAX_VALUE / 2;
        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
        }

        // set direct edge weights (bidirectional)
        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            dist[u][v] = w;
            dist[v][u] = w;
        }

        // Floyd-Warshall: compute the shortest path between all pairs
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        // for each city, count how many other cities are reachable within threshold
        int resultCity = -1;
        int minReachable = Integer.MAX_VALUE;

        for (int i = 0; i < n; i++) {
            int count = 0;
            for (int j = 0; j < n; j++) {
                if (i != j && dist[i][j] <= distanceThreshold) {
                    count++;
                }
            }
            // we want the smallest count, and in case of tie, the largest city index
            if (count < minReachable || (count == minReachable && i > resultCity)) {
                resultCity = i;
                minReachable = count;
            }
        }
        return resultCity;
    }

    public static void main(String[] args) {
        // Example from problem description:
        int n = 4;
        int[][] edges = {
                {0, 1, 3},
                {1, 2, 1},
                {1, 3, 4},
                {2, 3, 1}
        };
        int distanceThreshold = 4;
        int ans = findTheCity(n, edges, distanceThreshold);
        System.out.println("The city with the smallest number of reachable cities: " + ans);
        // Expected output: 3
    }
}

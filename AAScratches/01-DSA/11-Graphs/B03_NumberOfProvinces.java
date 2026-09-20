/*
 * =====================================================================
 *  Number of Provinces                            LeetCode 547 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   You are given an n x n adjacency MATRIX where isConnected[i][j] == 1 means
 *   city i and city j are directly connected. Connection is symmetric and
 *   transitive: a province is a maximal group of cities connected directly or
 *   indirectly. Return how many provinces there are.
 *
 * EXAMPLE
 *   [[1,0,1],
 *    [0,1,0],    ->  2   cities 0 and 2 form one province, city 1 is alone
 *    [1,0,1]]
 *   [[1,0,0],[0,1,0],[0,0,1]]  ->  3   edge case: nobody is connected
 *   [[1,1,1],[1,1,1],[1,1,1]]  ->  1   everybody is in one province
 *
 * APPROACH  (connected components, matrix converted to adjacency list)
 *   1. Convert the matrix to an adjacency list: for every pair i != j with
 *      matrix[i][j] == 1, add j to adj[i] and i to adj[j]. Skip i == j, because
 *      the diagonal is always 1 and a self-loop tells us nothing.
 *   2. Keep a vis[] array over the n cities.
 *   3. Loop i from 0 to n-1. Whenever vis[i] is still 0, that city belongs to a
 *      province nobody has counted yet: increment the counter and DFS from it.
 *   4. The DFS marks every city reachable from i, so the outer loop skips the
 *      rest of that province.
 *
 * KEY INSIGHT
 *   This is Number of Islands with the grid replaced by an explicit graph - the
 *   same "outer loop counts, inner traversal marks" skeleton. The only new skill
 *   is the matrix-to-list conversion, which is worth doing on sight: an n x n
 *   matrix scan is O(n^2) no matter what, but the list keeps the traversal itself
 *   proportional to the real number of edges.
 *
 * COMPLEXITY
 *   Time  O(n^2)  dominated by reading every cell of the matrix once
 *   Space O(n^2)  adjacency list in the dense case, plus O(n) vis and recursion
 *
 * INTERVIEW FOLLOW-UPS
 *   - Union-Find version: union(i, j) for every 1, then count distinct roots.
 *   - Skip the conversion and DFS straight over the matrix row - same answer,
 *     still O(n^2), but the list version is what later problems reuse.
 *   - Cities joining over time (dynamic connectivity) forces Union-Find.
 *   - Return the size of the largest province instead of the count.
 *
 * RUN
 *   main() runs 3 cases (two provinces, all isolated, all connected) and prints
 *   actual vs expected.
 */

import java.util.*;

class NumberOfProvines {

    private static void dfs(int node, ArrayList<ArrayList<Integer>> adjLs, int[] vis) {
        vis[node] = 1;
        for (int next : adjLs.get(node)) {
            if (vis[next] == 0) {
                dfs(next, adjLs, vis);
            }
        }
    }

    static int numProvinces(ArrayList<ArrayList<Integer>> adj, int V) {

        // adjacency MATRIX -> adjacency LIST
        ArrayList<ArrayList<Integer>> adjLs = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adjLs.add(new ArrayList<>());
        }
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                // i != j skips the always-1 diagonal (a city connected to itself)
                if (i != j && adj.get(i).get(j) == 1) {
                    adjLs.get(i).add(j);
                    adjLs.get(j).add(i);
                }
            }
        }

        int[] vis = new int[V];
        int count = 0;
        for (int i = 0; i < V; i++) {
            if (vis[i] == 0) {
                count++;            // city i starts a province nobody has seen
                dfs(i, adjLs, vis); // and this consumes the whole province
            }
        }
        return count;
    }

    /** Wraps a plain int[][] matrix into the ArrayList form the method expects. */
    private static ArrayList<ArrayList<Integer>> toMatrix(int[][] rows) {
        ArrayList<ArrayList<Integer>> matrix = new ArrayList<>();
        for (int[] row : rows) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int value : row) {
                list.add(value);
            }
            matrix.add(list);
        }
        return matrix;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {

        // typical: {0, 2} are one province, {1} is another
        int[][] two = {{1, 0, 1}, {0, 1, 0}, {1, 0, 1}};
        print("case 1 (two provinces)", numProvinces(toMatrix(two), 3), 2);

        // edge case: only the diagonal is set, so every city is its own province
        int[][] isolated = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
        print("case 2 (all isolated) ", numProvinces(toMatrix(isolated), 3), 3);

        // tricky: fully connected matrix collapses to a single province
        int[][] complete = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};
        print("case 3 (all connected)", numProvinces(toMatrix(complete), 3), 1);
    }
}

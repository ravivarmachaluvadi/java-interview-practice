/*
 * =====================================================================
 *  Floyd-Warshall: All-Pairs Shortest Path               GfG | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a directed weighted graph as an n x n matrix where matrix[i][j] is
 *   the weight of edge i -> j and -1 means "no edge", rewrite every cell in
 *   place with the shortest distance from i to j (-1 if unreachable). Weights
 *   may be negative; a negative cycle shows as a negative diagonal entry.
 *
 * EXAMPLE
 *   { {0, 2,-1,-1},          { {0, 2, 5,-1},
 *     {1, 0, 3,-1},    ->      {1, 0, 3,-1},     0->2 becomes 5 via 0->1->2
 *     {-1,-1,0,-1},            {-1,-1,0,-1},     row 2 reaches nobody
 *     {3, 5, 4, 0} }           {3, 5, 4, 0} }
 *   0->1 = 4, 1->2 = -2, 0->2 = 5   ->  0->2 = 2   a negative edge is fine
 *   0->1 = 1, 1->2 = -2, 2->1 = -3  ->  diagonal turns negative: cycle
 *
 * APPROACH  (dynamic programming over "allowed intermediate nodes")
 *   1. Turn the -1 markers into INF and force the diagonal to 0.
 *   2. For k = 0..n-1, for every pair (i, j):
 *        dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])
 *      Read it as: "after round k, dist[i][j] is the best path that may only
 *      pass through nodes 0..k in the middle." k must be the OUTER loop.
 *   3. Skip the relaxation when either half is INF, otherwise INF + (-2) looks
 *      cheaper than INF and invents a path out of an unreachable node.
 *      Fixed: the original relaxed unconditionally, which corrupted
 *      unreachable cells the moment a negative weight existed.
 *   4. Turn the remaining INF cells back into -1.
 *
 * KEY INSIGHT
 *   The whole algorithm is one DP question asked n times: "does routing
 *   through node k beat what I already have?" Because k is the outer loop,
 *   every pair is re-checked once per waypoint, so nothing is missed even
 *   with negative edges. dist[i][i] < 0 afterwards is the negative-cycle
 *   test: a node reached itself for less than nothing.
 *
 * COMPLEXITY
 *   Time  O(n^3)  three nested loops, no early exit
 *   Space O(1)    extra - the input matrix is rewritten in place
 *
 * INTERVIEW FOLLOW-UPS
 *   - When over Dijkstra-from-every-node? Dense graphs, small n (n <= ~400),
 *     or negative edges. n * Dijkstra is O(n * E log V) and needs no negatives.
 *   - Reconstruct the path: keep next[i][j] and update it inside the min.
 *   - Transitive closure: same loops with OR/AND instead of min/plus (Warshall).
 *   - Applied straight to a problem: LeetCode 1334, Find the City (see C16).
 *
 * RUN
 *   main() runs 4 cases (the classic 4x4, single node, a negative edge,
 *   a negative cycle) and prints actual vs expected.
 */
import java.util.Arrays;

class Solution {

    /** "Unreachable" during the sweep; 1e9 + 1e9 still fits in an int. */
    static final int INF = (int) 1e9;

    /** Rewrites matrix in place with all-pairs shortest distances (-1 = no path). */
    public void shortestDistance(int[][] matrix) {
        int n = matrix.length;

        // Step 1: -1 means no edge -> INF. Distance to yourself is 0.
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == -1) {
                    matrix[i][j] = INF;
                }
                if (i == j) {
                    matrix[i][j] = 0;
                }
            }
        }

        // Step 2: k is the waypoint being allowed, so it must be the outer loop.
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    // Only route through k if both halves actually exist.
                    if (matrix[i][k] != INF && matrix[k][j] != INF) {
                        matrix[i][j] = Math.min(matrix[i][j],
                                matrix[i][k] + matrix[k][j]);
                    }
                }
            }
        }

        // Step 3: whatever is still INF was never reachable.
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == INF) {
                    matrix[i][j] = -1;
                }
            }
        }
    }

    /** Call after shortestDistance: a node that reached itself for < 0 is on a cycle. */
    public boolean hasNegativeCycle(int[][] solved) {
        for (int i = 0; i < solved.length; i++) {
            if (solved[i][i] < 0) {
                return true;
            }
        }
        return false;
    }
}

class FloydWarshallAlgorithm {

    /** Builds an n x n matrix in the "-1 means no edge" encoding. */
    private static int[][] buildMatrix(int n, int[][] edges) {
        int[][] matrix = new int[n][n];
        for (int[] row : matrix) {
            Arrays.fill(row, -1);
        }
        for (int[] e : edges) {
            matrix[e[0]][e[1]] = e[2];
        }
        return matrix;
    }

    private static void check(String label, int[][] actual, String expected) {
        System.out.println(label + ": " + Arrays.deepToString(actual)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        Solution obj = new Solution();

        // Case 1: the classic sample. 0 -> 2 only exists via node 1.
        int[][] classic = buildMatrix(4, new int[][]{
                {0, 1, 2}, {1, 0, 1}, {1, 2, 3}, {3, 0, 3}, {3, 1, 5}, {3, 2, 4}
        });
        obj.shortestDistance(classic);
        check("case 1 classic 4x4", classic,
                "[[0, 2, 5, -1], [1, 0, 3, -1], [-1, -1, 0, -1], [3, 5, 4, 0]]");

        // Case 2 (edge): one node, no edges - only the forced diagonal zero.
        int[][] single = buildMatrix(1, new int[][]{});
        obj.shortestDistance(single);
        check("case 2 single node", single, "[[0]]");

        // Case 3: a negative edge with no cycle - 0->1->2 (2) beats the direct 5.
        int[][] negEdge = buildMatrix(3, new int[][]{{0, 1, 4}, {1, 2, -2}, {0, 2, 5}});
        obj.shortestDistance(negEdge);
        check("case 3 negative edge", negEdge,
                "[[0, 4, 2], [-1, 0, -2], [-1, -1, 0]]");

        // Case 4 (tricky): 1 -> 2 -> 1 costs -5, so the diagonal drops below zero.
        int[][] negCycle = buildMatrix(3, new int[][]{{0, 1, 1}, {1, 2, -2}, {2, 1, -3}});
        obj.shortestDistance(negCycle);
        System.out.println("case 4 negative cycle detected: "
                + obj.hasNegativeCycle(negCycle) + "   expected true");
    }
}

/*
 * =====================================================================
 *  P074 Minimum Spanning Tree (Kruskal and Prim)   Canonical LC 1584 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 1584, Min Cost to Connect All Points)
 *   Connect all points so every pair is linked by some path. The cost of an edge is the
 *   Manhattan distance |x1 - x2| + |y1 - y2|. Return the minimum total cost.
 *
 * EXAMPLE
 *   [[0,0],[2,2],[3,10],[5,2],[7,0]]  ->  20
 *   [[3,12],[-2,5],[-4,1]]            ->  18
 *
 * RECOGNIZE WHEN
 *   - "connect everything as cheaply as possible": cables, roads, pipes, clustering.
 *   - Undirected, weighted, and the result is a tree (n - 1 edges), not a path.
 *   Not this if: you need the cheapest route between two nodes -> P072_Dijkstra (a shortest
 *   path tree is NOT a minimum spanning tree).
 *
 * TEMPLATE
 *   Kruskal (sparse graphs, edge list): sort edges by weight; for each edge, if union(u, v)
 *            succeeds, take it; stop at n - 1 edges                 (P071_UnionFind)
 *   Prim (dense graphs): inTree = {start}; repeatedly add the cheapest edge leaving the tree
 *            O(n^2) array version for complete graphs, heap version for sparse ones
 *
 * APPROACH
 *   1. Prim with an array: best[v] = cheapest edge from the tree to v.
 *   2. Each round, add the outside point with the smallest best[v], then update the others.
 *   3. n rounds, O(n^2) total: right for a complete graph of n points.
 *
 * KEY INSIGHT
 *   Cut property: for any split of the nodes into two groups, the cheapest edge crossing the
 *   split belongs to some MST. Kruskal applies it globally (cheapest edge that joins two
 *   components); Prim applies it locally (cheapest edge leaving the growing tree).
 *
 * COMPLEXITY
 *   Kruskal O(E log E). Prim O(n^2) with arrays (best for complete graphs), O(E log V) with
 *   a heap.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 1584 Kruskal                  build all n(n-1)/2 edges, sort, union-find
 *   [coded] LC 1135 Connecting Cities        Kruskal; -1 if fewer than n - 1 edges taken
 *           LC 1168 Water Distribution       virtual node 0 with an edge of cost well[i] to
 *                                            each house, then MST
 *           LC 1489 Critical Edges           rebuild the MST without / forcing each edge
 *           Maximum spanning tree            sort descending
 *
 * PITFALLS
 *   - Kruskal on a complete graph builds O(n^2) edges; Prim's array version avoids that.
 *   - Check the edge count at the end: fewer than n - 1 means the graph is disconnected.
 *   - Prim: mark a node "in the tree" when it is chosen, not when it is first seen.
 *
 * DEEP DIVE
 *   A09_PrimsAlgo, A10_KruskalAlgorithm (11-Graphs)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MinimumSpanningTree {

    static class DSU {
        private final int[] parent;

        DSU(int n) {
            parent = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        int find(int x) {
            while (parent[x] != x) {
                parent[x] = parent[parent[x]];
                x = parent[x];
            }
            return x;
        }

        boolean union(int a, int b) {
            int ra = find(a);
            int rb = find(b);
            if (ra == rb) {
                return false;
            }
            parent[ra] = rb;
            return true;
        }
    }

    // Canonical LC 1584 with Prim's O(n^2) array version.
    static int minCostConnectPoints(int[][] p) {
        int n = p.length;
        int[] best = new int[n];
        Arrays.fill(best, Integer.MAX_VALUE);
        boolean[] inTree = new boolean[n];
        best[0] = 0;
        int total = 0;
        for (int round = 0; round < n; round++) {
            int u = -1;
            for (int v = 0; v < n; v++) {
                if (!inTree[v] && (u == -1 || best[v] < best[u])) {
                    u = v;
                }
            }
            inTree[u] = true;
            total += best[u];
            for (int v = 0; v < n; v++) {
                if (!inTree[v]) {
                    int cost = Math.abs(p[u][0] - p[v][0]) + Math.abs(p[u][1] - p[v][1]);
                    best[v] = Math.min(best[v], cost);
                }
            }
        }
        return total;
    }

    // LC 1584 with Kruskal: every pair is an edge.
    static int minCostConnectPointsKruskal(int[][] p) {
        int n = p.length;
        List<int[]> edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int cost = Math.abs(p[i][0] - p[j][0]) + Math.abs(p[i][1] - p[j][1]);
                edges.add(new int[]{cost, i, j});
            }
        }
        edges.sort((a, b) -> Integer.compare(a[0], b[0]));
        DSU dsu = new DSU(n);
        int total = 0;
        int taken = 0;
        for (int[] e : edges) {
            if (taken == n - 1) {
                break;
            }
            if (dsu.union(e[1], e[2])) {
                total += e[0];
                taken++;
            }
        }
        return total;
    }

    // LC 1135: cities 1..n; -1 if they cannot all be connected.
    static int minimumCost(int n, int[][] connections) {
        int[][] edges = connections.clone();
        Arrays.sort(edges, (a, b) -> Integer.compare(a[2], b[2]));
        DSU dsu = new DSU(n + 1);
        int total = 0;
        int taken = 0;
        for (int[] e : edges) {
            if (dsu.union(e[0], e[1])) {
                total += e[2];
                taken++;
            }
        }
        return taken == n - 1 ? total : -1;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] five = {{0, 0}, {2, 2}, {3, 10}, {5, 2}, {7, 0}};
        int[][] three = {{3, 12}, {-2, 5}, {-4, 1}};
        check("LC 1584 Prim five points", minCostConnectPoints(five), 20);
        check("LC 1584 Prim three points", minCostConnectPoints(three), 18);
        check("LC 1584 Kruskal five points", minCostConnectPointsKruskal(five), 20);
        check("LC 1584 Kruskal three points", minCostConnectPointsKruskal(three), 18);
        check("LC 1584 single point", minCostConnectPoints(new int[][]{{0, 0}}), 0);

        check("LC 1135 three cities",
                minimumCost(3, new int[][]{{1, 2, 5}, {1, 3, 6}, {2, 3, 1}}), 6);
        check("LC 1135 disconnected", minimumCost(4, new int[][]{{1, 2, 3}, {3, 4, 4}}), -1);
    }
}

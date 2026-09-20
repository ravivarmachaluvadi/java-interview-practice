/*
 * =====================================================================
 *  Articulation Points (Cut Vertices)                     GfG | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given an undirected graph with n nodes (0..n-1) as an adjacency list, return
 *   every articulation point: a node whose removal (with its edges) increases the
 *   number of connected components. The graph may be disconnected.
 *   Convention used here: return [-1] when there is no articulation point.
 *
 * EXAMPLE
 *   edges = 0-1, 0-2, 0-3, 2-3, 2-4, 2-5, 4-6, 5-6   ->  [0, 2]
 *     removing 0 strands node 1; removing 2 strands {4, 5, 6}
 *   edges = 0-1, 1-2, 2-0 (triangle)                 ->  [-1]  nothing is critical
 *
 * APPROACH  (Tarjan: one DFS carrying tin[] and low[])
 *   1. tin[u] = the time DFS first discovered u. low[u] = the smallest tin that
 *      u's subtree can reach using tree edges plus at most one back edge.
 *   2. DFS from every unvisited node so disconnected components are covered.
 *      Skip the edge straight back to the parent.
 *   3. For a tree child v: after recursing, low[u] = min(low[u], low[v]).
 *      If low[v] >= tin[u] and u is NOT the DFS root, then v's subtree cannot
 *      reach above u without passing through u, so u is a cut vertex.
 *   4. For an already-visited neighbour v (a back edge) use tin[v], not low[v]:
 *      the edge only lets you jump to v itself.
 *   5. The DFS root is special: it is a cut vertex only if it has two or more
 *      tree children, because then those subtrees are joined only through it.
 *
 * KEY INSIGHT
 *   Same tin/low machinery as bridges, with two differences worth reciting:
 *   the comparison is >= instead of > (the child may reach u itself and u is still
 *   the only way past), and the DFS root needs the two-children rule because it has
 *   no parent edge to be cut.
 *
 * COMPLEXITY
 *   Time  O(V + E)  one DFS, every edge looked at twice
 *   Space O(V + E)  adjacency list, plus O(V) for tin/low/visited/mark and recursion
 *
 * INTERVIEW FOLLOW-UPS
 *   - Bridges (critical edges): same pass but low[v] > tin[u] and no root case.
 *   - Why >= here and > for bridges? A node can be critical even when the child
 *     reaches back to it; an edge cannot.
 *   - Biconnected components: push edges on a stack and pop at each cut vertex.
 *   - Iterative DFS when V is large enough for the recursion stack to overflow.
 *
 * RUN
 *   main() runs 3 cases (the classic 7-node graph, a triangle with no cut vertex,
 *   a disconnected graph) and prints actual vs expected.
 */

import java.util.*;

class ArticulationPointInGraph {

    private int timer = 1;

    private void dfs(int node, int parent, boolean[] vis,
                     int[] tin, int[] low, boolean[] mark,
                     ArrayList<ArrayList<Integer>> adj) {

        vis[node] = true;
        tin[node] = low[node] = timer;
        timer++;             // every node must consume one timestamp

        int children = 0;    // tree children only, used for the root rule

        for (int next : adj.get(node)) {
            if (next == parent) continue; // do not re-use the edge we arrived on

            if (!vis[next]) {
                dfs(next, node, vis, tin, low, mark, adj);
                low[node] = Math.min(low[node], low[next]);

                // next's subtree cannot climb above `node`, so `node` holds it on
                // (the root has no parent edge, so it is judged by child count instead)
                if (low[next] >= tin[node] && parent != -1) {
                    mark[node] = true;
                }
                children++;
            } else {
                // back edge: reaches `next` itself, hence tin and not low
                low[node] = Math.min(low[node], tin[next]);
            }
        }

        // the DFS root splits the graph only if it joins two or more subtrees
        if (parent == -1 && children > 1) {
            mark[node] = true;
        }
    }

    public ArrayList<Integer> articulationPoints(int n, ArrayList<ArrayList<Integer>> adj) {

        boolean[] vis = new boolean[n];
        int[] tin = new int[n];
        int[] low = new int[n];
        boolean[] mark = new boolean[n];  // mark[i] = i is an articulation point

        timer = 1;
        for (int i = 0; i < n; i++) {
            if (!vis[i]) {
                dfs(i, -1, vis, tin, low, mark, adj);
            }
        }

        ArrayList<Integer> ans = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (mark[i]) ans.add(i);
        }
        // problem convention: report -1 when the graph has no cut vertex
        if (ans.isEmpty()) return new ArrayList<>(Arrays.asList(-1));
        return ans;
    }

    /** Builds an undirected adjacency list from [u, v] pairs. */
    private static ArrayList<ArrayList<Integer>> build(int n, int[][] edges) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        return adj;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        ArticulationPointInGraph obj = new ArticulationPointInGraph();

        // typical: 0 holds node 1 on, 2 holds the {4,5,6} ring on
        int[][] classic = {{0, 1}, {0, 2}, {0, 3}, {2, 3}, {2, 4}, {2, 5}, {4, 6}, {5, 6}};
        print("case 1 (classic 7-node)", obj.articulationPoints(7, build(7, classic)), "[0, 2]");

        // edge case: a cycle has no cut vertex, so the -1 convention kicks in
        print("case 2 (triangle)      ",
                obj.articulationPoints(3, build(3, new int[][]{{0, 1}, {1, 2}, {2, 0}})), "[-1]");

        // tricky: two components - chain 0-1-2 plus a separate edge 3-4
        print("case 3 (disconnected)  ",
                obj.articulationPoints(5, build(5, new int[][]{{0, 1}, {1, 2}, {3, 4}})), "[1]");
    }
}

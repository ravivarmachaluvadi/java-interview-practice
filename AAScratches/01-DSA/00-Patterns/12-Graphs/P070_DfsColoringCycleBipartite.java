/*
 * =====================================================================
 *  P070 DFS Colouring: Bipartite and Cycle Detection   Canonical LC 785 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 785, Is Graph Bipartite?)
 *   graph[u] lists u's neighbours (undirected, maybe disconnected). Return whether the
 *   nodes can be split into two sets so every edge joins the two sets.
 *
 * EXAMPLE
 *   [[1,2,3],[0,2],[0,1,3],[0,2]]  ->  false     triangle 0-1-2
 *   [[1,3],[0,2],[1,3],[0,2]]      ->  true      a square
 *
 * RECOGNIZE WHEN
 *   - "split into two groups / teams / colours so that no edge stays inside a group".
 *   - "does this graph have a cycle?" (directed: dependency loops; undirected: extra edge).
 *   - "is this graph a tree?" (connected and no cycle).
 *   Not this if: you need a cycle-free ORDER -> P069_TopologicalSort; edges arrive online ->
 *   P071_UnionFind.
 *
 * TEMPLATE
 *   bipartite:  colour[u] in {unset, 0, 1}; dfs gives each neighbour the opposite colour;
 *               a neighbour already holding MY colour -> not bipartite
 *   directed cycle: state[u] in {white, grey (on the current path), black (done)};
 *               reaching a GREY node again -> cycle
 *   undirected cycle: dfs(u, parent); reaching a visited node that is not the parent -> cycle
 *   run from every unvisited node (the graph may be disconnected)
 *
 * APPROACH
 *   1. Colour an uncoloured node 0 and DFS.
 *   2. Every neighbour must get the other colour; a clash means an odd cycle.
 *   3. Repeat from every node still uncoloured.
 *
 * KEY INSIGHT
 *   A graph is bipartite exactly when it has no odd-length cycle, and 2-colouring by DFS
 *   finds such a cycle as a colour clash. The same "colour while you walk" idea detects
 *   directed cycles with three states: grey marks the nodes on the current recursion path.
 *
 * COMPLEXITY
 *   Time O(V + E), space O(V).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 886  Possible Bipartition     build the dislike graph, then LC 785
 *   [coded] Directed cycle (3 colours)       grey -> grey edge is a back edge
 *   [coded] Undirected cycle (parent)        visited neighbour other than the parent
 *   [coded] LC 261  Graph Valid Tree         n - 1 edges AND connected (or: no cycle and
 *                                            one component)
 *           LC 207  Course Schedule          directed cycle check -> P069_TopologicalSort
 *           LC 1042 Flower Planting          greedy colouring with 4 colours
 *           M-colouring (GfG)                backtracking -> P079_ConstraintPlacement
 *
 * PITFALLS
 *   - Disconnected graphs: loop over every node, not just node 0.
 *   - Undirected cycle: skipping the parent by NODE assumes no parallel edges (true for
 *     LC 261); with multi-edges, skip the parent EDGE by its index instead.
 *   - Directed graphs: a visited BLACK node is fine; only GREY means a cycle.
 *
 * DEEP DIVE
 *   B09_IsBipartite, A02_CheckForCycleInUnDirected, A03_CycleCheckInDirectedGraph
 *   (11-Graphs)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class DfsColoringCycleBipartite {

    // Canonical LC 785.
    static boolean isBipartite(int[][] graph) {
        int[] colour = new int[graph.length];
        Arrays.fill(colour, -1);
        for (int u = 0; u < graph.length; u++) {
            if (colour[u] == -1 && !paint(graph, u, 0, colour)) {
                return false;
            }
        }
        return true;
    }

    private static boolean paint(int[][] g, int u, int c, int[] colour) {
        colour[u] = c;
        for (int v : g[u]) {
            if (colour[v] == c) {
                return false;                      // same colour on both ends: odd cycle
            }
            if (colour[v] == -1 && !paint(g, v, 1 - c, colour)) {
                return false;
            }
        }
        return true;
    }

    // LC 886: people 1..n; dislikes must end up in different groups.
    static boolean possibleBipartition(int n, int[][] dislikes) {
        List<List<Integer>> adj = adjacency(n + 1, dislikes, true);
        int[][] graph = new int[n + 1][];
        for (int i = 0; i <= n; i++) {
            graph[i] = adj.get(i).stream().mapToInt(Integer::intValue).toArray();
        }
        return isBipartite(graph);
    }

    // Directed cycle with three states: 0 white, 1 grey (on the path), 2 black (finished).
    static boolean hasDirectedCycle(int n, int[][] edges) {
        List<List<Integer>> adj = adjacency(n, edges, false);
        int[] state = new int[n];
        for (int u = 0; u < n; u++) {
            if (state[u] == 0 && greyHit(adj, u, state)) {
                return true;
            }
        }
        return false;
    }

    private static boolean greyHit(List<List<Integer>> adj, int u, int[] state) {
        state[u] = 1;
        for (int v : adj.get(u)) {
            if (state[v] == 1 || (state[v] == 0 && greyHit(adj, v, state))) {
                return true;
            }
        }
        state[u] = 2;
        return false;
    }

    // Undirected cycle: a visited neighbour that is not the node we came from.
    static boolean hasUndirectedCycle(int n, int[][] edges) {
        List<List<Integer>> adj = adjacency(n, edges, true);
        boolean[] seen = new boolean[n];
        for (int u = 0; u < n; u++) {
            if (!seen[u] && loopFrom(adj, u, -1, seen)) {
                return true;
            }
        }
        return false;
    }

    private static boolean loopFrom(List<List<Integer>> adj, int u, int parent, boolean[] seen) {
        seen[u] = true;
        for (int v : adj.get(u)) {
            if (v == parent) {
                continue;
            }
            if (seen[v] || loopFrom(adj, v, u, seen)) {
                return true;
            }
        }
        return false;
    }

    // LC 261: a tree has exactly n - 1 edges and no cycle (hence connected).
    static boolean validTree(int n, int[][] edges) {
        return edges.length == n - 1 && !hasUndirectedCycle(n, edges);
    }

    static List<List<Integer>> adjacency(int n, int[][] edges, boolean undirected) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            if (undirected) {
                adj.get(e[1]).add(e[0]);
            }
        }
        return adj;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 785 triangle inside",
                isBipartite(new int[][]{{1, 2, 3}, {0, 2}, {0, 1, 3}, {0, 2}}), false);
        check("LC 785 square", isBipartite(new int[][]{{1, 3}, {0, 2}, {1, 3}, {0, 2}}), true);
        check("LC 785 disconnected", isBipartite(new int[][]{{}, {2}, {1}}), true);

        check("LC 886 n=4", possibleBipartition(4, new int[][]{{1, 2}, {1, 3}, {2, 4}}), true);
        check("LC 886 triangle",
                possibleBipartition(3, new int[][]{{1, 2}, {1, 3}, {2, 3}}), false);

        check("directed loop 0-1-2-0",
                hasDirectedCycle(3, new int[][]{{0, 1}, {1, 2}, {2, 0}}), true);
        check("directed diamond, no loop",
                hasDirectedCycle(3, new int[][]{{0, 1}, {1, 2}, {0, 2}}), false);

        check("undirected path", hasUndirectedCycle(4, new int[][]{{0, 1}, {1, 2}, {2, 3}}), false);
        check("undirected with 3-1",
                hasUndirectedCycle(4, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 1}}), true);

        check("LC 261 star-ish tree",
                validTree(5, new int[][]{{0, 1}, {0, 2}, {0, 3}, {1, 4}}), true);
        check("LC 261 has a cycle",
                validTree(5, new int[][]{{0, 1}, {1, 2}, {2, 3}, {1, 3}, {1, 4}}), false);
        check("LC 261 n-1 edges, cycle plus a loner",
                validTree(4, new int[][]{{0, 1}, {1, 2}, {2, 0}}), false);
    }
}

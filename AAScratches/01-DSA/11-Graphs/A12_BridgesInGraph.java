/*
 * =====================================================================
 *  Bridges in a Graph (Critical Connections)        LeetCode 1192 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given an undirected graph with n nodes (0..n-1) and a list of edges, find every
 *   bridge: an edge whose removal increases the number of connected components.
 *   The graph may be disconnected and may contain isolated nodes.
 *
 * EXAMPLE
 *   n = 4, edges = [[0,1],[1,2],[2,0],[1,3]]  ->  [[1, 3]]     0-1-2 is a cycle
 *   n = 3, edges = [[0,1],[1,2],[2,0]]        ->  []           every edge is on a cycle
 *   n = 6, edges = [[0,1],[2,3],[3,4]]        ->  [[0, 1], [2, 3], [3, 4]]  3 components
 *
 * APPROACH  (Tarjan's bridge algorithm: DFS with tin[] and low[])
 *   1. tin[u] = the timestamp at which DFS first discovered u (discovery time).
 *   2. low[u] = the smallest tin reachable from u's DFS subtree using tree edges
 *      plus at most one back edge.
 *   3. DFS from every unvisited node, skipping the edge back to the parent.
 *   4. After returning from a tree child v, pull low[v] into low[u].
 *      If low[v] > tin[u] then nothing in v's subtree can reach u or above
 *      except through the edge u-v, so u-v is a bridge.
 *   5. For an already-visited neighbour (a back edge) relax with tin[v], the
 *      standard form: a back edge only tells you that you can jump to v itself.
 *
 * KEY INSIGHT
 *   A tree edge u-v is a bridge exactly when v's subtree has no back edge climbing
 *   to u or higher. low[] is just "highest node my subtree can reach"; comparing it
 *   to tin[u] is the whole algorithm. Recognise this whenever a question asks which
 *   single edge/server/cable failure would split the network.
 *
 * COMPLEXITY
 *   Time  O(V + E)  one DFS, each edge examined twice (once from each endpoint)
 *   Space O(V + E)  adjacency list, plus O(V) for tin/low/visited and recursion
 *
 * INTERVIEW FOLLOW-UPS
 *   - Articulation points (cut vertices): same pass, use low[v] >= tin[u] plus a
 *     root special case - see A15_ArticulationPointInGraph.
 *   - Parallel edges break the "skip the parent" rule; track the edge id instead.
 *   - Recursion depth is O(V); rewrite iteratively for very deep graphs.
 *   - Bridges partition the graph into 2-edge-connected components.
 *
 * FIXES APPLIED
 *   Fixed: the search started at node 0 only, so bridges in any other connected
 *   component were silently missed. It now loops over every unvisited node.
 *
 * RUN
 *   main() runs 3 cases (cycle plus tail, pure cycle, disconnected) and prints
 *   actual vs expected.
 */

import java.util.*;

class BridgesInGraph {

    private int timer = 1;

    private void dfs(int node, int parent, boolean[] visited, List<Integer>[] adj,
                     int[] tin, int[] low, List<List<Integer>> bridges) {

        visited[node] = true;
        // discovery time; low starts as "I can at least reach myself"
        tin[node] = low[node] = timer;
        timer++;

        for (int next : adj[node]) {
            if (next == parent) continue; // never walk straight back up the tree edge

            if (!visited[next]) {
                dfs(next, node, visited, adj, tin, low, bridges);

                // a child can only help me climb as high as it can climb
                low[node] = Math.min(low[node], low[next]);

                // nothing under `next` reaches `node` or above, so this edge is the
                // only link between the two sides
                if (low[next] > tin[node]) {
                    bridges.add(Arrays.asList(Math.min(node, next), Math.max(node, next)));
                }
            } else {
                // back edge: it lets me jump to `next` itself, not below it
                low[node] = Math.min(low[node], tin[next]);
            }
        }
    }

    /** Returns every bridge as a sorted [smaller, larger] pair, in sorted order. */
    public List<List<Integer>> criticalBridges(int n, List<List<Integer>> connections) {

        List<Integer>[] adj = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
        }
        for (List<Integer> edge : connections) {
            int u = edge.get(0), v = edge.get(1);
            adj[u].add(v);
            adj[v].add(u);
        }

        boolean[] visited = new boolean[n];
        int[] tin = new int[n];   // discovery time of each node
        int[] low = new int[n];   // highest (smallest tin) node the subtree can reach
        List<List<Integer>> bridges = new ArrayList<>();

        timer = 1;
        // one DFS per connected component, otherwise other components are skipped
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(i, -1, visited, adj, tin, low, bridges);
            }
        }

        // sort only so the printed answer is stable and easy to compare
        bridges.sort(Comparator.<List<Integer>>comparingInt(e -> e.get(0))
                .thenComparingInt(e -> e.get(1)));
        return bridges;
    }

    private static List<List<Integer>> edges(int[][] pairs) {
        List<List<Integer>> list = new ArrayList<>();
        for (int[] p : pairs) {
            list.add(Arrays.asList(p[0], p[1]));
        }
        return list;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        BridgesInGraph obj = new BridgesInGraph();

        // typical: triangle 0-1-2 with a tail 1-3; only the tail is a bridge
        print("case 1 (cycle + tail)",
                obj.criticalBridges(4, edges(new int[][]{{0, 1}, {1, 2}, {2, 0}, {1, 3}})),
                "[[1, 3]]");

        // edge case: every edge sits on a cycle, so there is no bridge at all
        print("case 2 (pure cycle)   ",
                obj.criticalBridges(3, edges(new int[][]{{0, 1}, {1, 2}, {2, 0}})),
                "[]");

        // tricky: three components (0-1, 2-3-4, isolated 5) - needs the outer loop
        print("case 3 (disconnected) ",
                obj.criticalBridges(6, edges(new int[][]{{0, 1}, {2, 3}, {3, 4}})),
                "[[0, 1], [2, 3], [3, 4]]");
    }
}

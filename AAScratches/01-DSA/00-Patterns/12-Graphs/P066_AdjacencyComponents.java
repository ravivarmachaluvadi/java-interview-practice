/*
 * =====================================================================
 *  P066 Graph DFS / BFS on an Adjacency List   Canonical LC 547 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 547, Number of Provinces)
 *   isConnected[i][j] == 1 means cities i and j are directly connected. A province is a
 *   group of cities connected directly or indirectly. Return the number of provinces.
 *
 * EXAMPLE
 *   [[1,1,0],[1,1,0],[0,0,1]]  ->  2
 *   [[1,0,0],[0,1,0],[0,0,1]]  ->  3
 *
 * RECOGNIZE WHEN
 *   - Explicit nodes and edges (edge list, adjacency matrix, "rooms with keys", "equations")
 *     and the question is reachability or connected components.
 *   - Weighted edges whose weights MULTIPLY along a path (ratios, currency conversion).
 *   Not this if: the graph is a grid -> P065_GridFloodFill; you need a shortest path ->
 *   P067_BfsShortestPath or P072_Dijkstra; edges arrive over time and you only ask
 *   "connected?" -> P071_UnionFind.
 *
 * TEMPLATE
 *   adj = list of neighbours per node (build it from the input first)
 *   visited = set()
 *   for each node u: if u not visited: components++; dfs(u)
 *   dfs(u): visited.add(u); for v in adj[u]: if v not visited: dfs(v)
 *   (BFS with a queue works the same; pick it when the depth could be large)
 *
 * APPROACH
 *   1. For each city not yet visited, start a DFS that marks its whole province.
 *   2. The number of DFS starts is the number of provinces.
 *
 * KEY INSIGHT
 *   Every connectivity question reduces to "start a traversal from an unvisited node and
 *   mark everything reachable". Building the adjacency list first turns any input format
 *   (matrix, pairs, strings) into the same traversal.
 *
 * COMPLEXITY
 *   Time O(V + E) with an adjacency list (O(V^2) for a matrix), space O(V).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 841  Keys and Rooms           one DFS from room 0; can every room be visited?
 *   [coded] LC 1971 Find if Path Exists      edge list -> adjacency list; DFS from source
 *   [coded] LC 399  Evaluate Division        edge a -> b with weight a / b and b -> a with
 *                                            b / a; DFS multiplies weights along the path
 *           LC 323  Connected Components     LC 547 from an edge list
 *           LC 1557 Min Vertices to Reach All  nodes with in-degree 0
 *           LC 2492 Min Score of a Path      DFS the component of node 1, min edge inside
 *
 * PITFALLS
 *   - Undirected input: add BOTH directions to the adjacency list.
 *   - Mark visited when you push / enter, not when you pop, or nodes are queued twice.
 *   - LC 399: a variable that never appeared has no answer (-1.0), even x / x.
 *
 * DEEP DIVE
 *   B03_NumberOfProvinces, B04_KeysAndRooms, A01_BFSandDFS (11-Graphs)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class AdjacencyComponents {

    // Canonical LC 547.
    static int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        boolean[] seen = new boolean[n];
        int provinces = 0;
        for (int i = 0; i < n; i++) {
            if (!seen[i]) {
                provinces++;
                visit(isConnected, i, seen);
            }
        }
        return provinces;
    }

    private static void visit(int[][] m, int u, boolean[] seen) {
        seen[u] = true;
        for (int v = 0; v < m.length; v++) {
            if (m[u][v] == 1 && !seen[v]) {
                visit(m, v, seen);
            }
        }
    }

    // LC 841.
    static boolean canVisitAllRooms(List<List<Integer>> rooms) {
        boolean[] seen = new boolean[rooms.size()];
        int[] count = {0};
        enter(rooms, 0, seen, count);
        return count[0] == rooms.size();
    }

    private static void enter(List<List<Integer>> rooms, int u, boolean[] seen, int[] count) {
        seen[u] = true;
        count[0]++;
        for (int key : rooms.get(u)) {
            if (!seen[key]) {
                enter(rooms, key, seen, count);
            }
        }
    }

    // LC 1971: undirected edge list.
    static boolean validPath(int n, int[][] edges, int source, int destination) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        boolean[] seen = new boolean[n];
        List<Integer> stack = new ArrayList<>(List.of(source));
        seen[source] = true;
        while (!stack.isEmpty()) {
            int u = stack.remove(stack.size() - 1);
            if (u == destination) {
                return true;
            }
            for (int v : adj.get(u)) {
                if (!seen[v]) {
                    seen[v] = true;
                    stack.add(v);
                }
            }
        }
        return false;
    }

    // LC 399: a / b = value means an edge a -> b of weight value and b -> a of 1 / value.
    static double[] calcEquation(List<List<String>> equations, double[] values,
                                 List<List<String>> queries) {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            String a = equations.get(i).get(0);
            String b = equations.get(i).get(1);
            graph.computeIfAbsent(a, k -> new HashMap<>()).put(b, values[i]);
            graph.computeIfAbsent(b, k -> new HashMap<>()).put(a, 1 / values[i]);
        }
        double[] out = new double[queries.size()];
        for (int i = 0; i < out.length; i++) {
            String from = queries.get(i).get(0);
            String to = queries.get(i).get(1);
            out[i] = graph.containsKey(from) && graph.containsKey(to)
                    ? product(graph, from, to, new HashSet<>()) : -1.0;
        }
        return out;
    }

    private static double product(Map<String, Map<String, Double>> g, String u, String target,
                                  Set<String> seen) {
        if (u.equals(target)) {
            return 1.0;
        }
        seen.add(u);
        for (Map.Entry<String, Double> e : g.get(u).entrySet()) {
            if (!seen.contains(e.getKey())) {
                double rest = product(g, e.getKey(), target, seen);
                if (rest != -1.0) {
                    return e.getValue() * rest;
                }
            }
        }
        return -1.0;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 547 two provinces",
                findCircleNum(new int[][]{{1, 1, 0}, {1, 1, 0}, {0, 0, 1}}), 2);
        check("LC 547 three provinces",
                findCircleNum(new int[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}), 3);

        check("LC 841 chain of keys",
                canVisitAllRooms(List.of(List.of(1), List.of(2), List.of(3), List.of())), true);
        check("LC 841 room 2 locked",
                canVisitAllRooms(List.of(List.of(1, 3), List.of(3, 0, 1), List.of(2), List.of(0))),
                false);

        check("LC 1971 triangle", validPath(3, new int[][]{{0, 1}, {1, 2}, {2, 0}}, 0, 2), true);
        check("LC 1971 two components",
                validPath(6, new int[][]{{0, 1}, {0, 2}, {3, 5}, {5, 4}, {4, 3}}, 0, 5), false);

        List<List<String>> eq = List.of(List.of("a", "b"), List.of("b", "c"));
        List<List<String>> q = List.of(List.of("a", "c"), List.of("b", "a"), List.of("a", "e"),
                List.of("a", "a"), List.of("x", "x"));
        check("LC 399 five queries", Arrays.toString(calcEquation(eq, new double[]{2.0, 3.0}, q)),
                "[6.0, 0.5, -1.0, 1.0, -1.0]");
    }
}

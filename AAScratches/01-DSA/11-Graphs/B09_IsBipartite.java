/*
 * =====================================================================
 *  Is Graph Bipartite?                              LeetCode 785 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an undirected graph as an adjacency list over vertices 0..V-1, decide
 *   whether the vertices can be split into two groups so that every edge joins a
 *   vertex of one group to a vertex of the other. The graph may be disconnected,
 *   so every component has to be checked.
 *
 * EXAMPLE
 *   V = 4, edges = 0-2, 0-3, 1-3, 2-3   ->  false  (0-2-3-0 is an odd cycle)
 *   V = 4, edges = 0-1, 1-2, 2-3        ->  true   (a path is always bipartite)
 *   V = 3, edges = none                 ->  true   (isolated vertices, edge case)
 *
 * APPROACH  (two-colouring during traversal)
 *   1. color[] holds -1 for "not yet coloured", 0 and 1 for the two groups.
 *   2. For each uncoloured vertex, start a traversal and paint it colour 0.
 *      A fresh start per component is what handles a disconnected graph.
 *   3. When moving from a vertex of colour c to a neighbour:
 *        - neighbour uncoloured  -> paint it 1 - c and keep going;
 *        - neighbour already c   -> two adjacent vertices share a group, so the
 *                                   graph is not bipartite; report false at once.
 *        - neighbour already 1-c -> nothing to do, the edge is already fine.
 *   4. Surviving the whole traversal means a valid 2-colouring exists.
 *
 *   Two versions are shown: dfsColouring (recursive) and bfsColouring (queue,
 *   no recursion depth risk). main() runs both on every case and they must agree.
 *
 * KEY INSIGHT
 *   Bipartite is exactly "no odd-length cycle". A traversal fixes one colour per
 *   vertex, so a conflict can only appear when a cycle closes back on a vertex of
 *   the same colour - which requires an odd number of edges. The pattern to
 *   recognise: whenever a problem wants a 2-way split with a "must differ"
 *   constraint on each edge, it is a 2-colouring, not a search over subsets.
 *
 * COMPLEXITY
 *   Time  O(V + E)  every vertex is coloured once and every edge inspected twice
 *   Space O(V)      colour array plus recursion stack / BFS queue (adjacency
 *                   list itself is the O(V + E) input)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the two groups, not just true/false: collect vertices by colour.
 *   - Same question on a directed graph: ignore direction, the answer is unchanged.
 *   - Possible Bipartition (LeetCode 886): dislikes list, identical algorithm.
 *   - Can 3-colouring be done the same way? No - that is NP-complete; only k = 2
 *     forces the neighbour's colour, which is why the greedy pass works here.
 *
 * RUN
 *   main() runs 4 cases (odd cycle, even cycle, isolated vertices, two
 *   components) through both versions and prints actual vs expected.
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

class IsBipartite {

    /** Paints node with col, then recurses. Returns false as soon as a clash is seen. */
    private static boolean dfs(int node, int col, int[] color, ArrayList<ArrayList<Integer>> adj) {
        color[node] = col;
        for (int neighbour : adj.get(node)) {
            if (color[neighbour] == -1) {
                // uncoloured: the opposite colour is forced on it
                if (!dfs(neighbour, 1 - col, color, adj)) return false;
            } else if (color[neighbour] == col) {
                // same colour on both ends of an edge -> odd cycle
                return false;
            }
            // else it already holds 1 - col, which is exactly what we want
        }
        return true;
    }

    public static boolean dfsColouring(int V, ArrayList<ArrayList<Integer>> adj) {
        int[] color = new int[V];
        Arrays.fill(color, -1);
        for (int start = 0; start < V; start++) {
            // one start per component; note it is start, not some fixed vertex
            if (color[start] == -1 && !dfs(start, 0, color, adj)) return false;
        }
        return true;
    }

    /** Same rule, queue instead of recursion - safe for very deep graphs. */
    public static boolean bfsColouring(int V, ArrayList<ArrayList<Integer>> adj) {
        int[] color = new int[V];
        Arrays.fill(color, -1);
        for (int start = 0; start < V; start++) {
            if (color[start] != -1) continue;
            color[start] = 0;
            Queue<Integer> queue = new ArrayDeque<>();
            queue.add(start);
            while (!queue.isEmpty()) {
                int node = queue.poll();
                for (int neighbour : adj.get(node)) {
                    if (color[neighbour] == -1) {
                        color[neighbour] = 1 - color[node];
                        queue.add(neighbour);
                    } else if (color[neighbour] == color[node]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /** Builds an undirected adjacency list; edges are given as flat pairs. */
    private static ArrayList<ArrayList<Integer>> buildGraph(int V, int[][] edges) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        return adj;
    }

    private static void check(String label, int V, int[][] edges, boolean expected) {
        ArrayList<ArrayList<Integer>> adj = buildGraph(V, edges);
        System.out.println(label + ": dfs=" + dfsColouring(V, adj)
                + " bfs=" + bfsColouring(V, adj) + "   expected " + expected);
    }

    public static void main(String[] args) {
        // typical: triangle 0-2-3 plus a pendant edge -> odd cycle
        check("case 1 odd cycle    ", 4, new int[][]{{0, 2}, {0, 3}, {1, 3}, {2, 3}}, false);

        // even cycle 0-1-2-3-0 -> colours alternate cleanly
        check("case 2 even cycle   ", 4, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}}, true);

        // edge case: no edges at all, every vertex is its own component
        check("case 3 no edges     ", 3, new int[][]{}, true);

        // tricky: component 0-1 is fine, component 2-3-4 is a triangle -> false.
        // This is the case a single-start solution gets wrong.
        check("case 4 two parts    ", 5, new int[][]{{0, 1}, {2, 3}, {3, 4}, {4, 2}}, false);
    }
}

/*
 * =====================================================================
 *  Detect Cycle in an Undirected Graph            GFG classic | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an undirected graph with V vertices (0..V-1) as an adjacency list,
 *   report whether it contains at least one cycle. The graph may be
 *   disconnected, so every component has to be checked, not just one.
 *
 * EXAMPLE
 *   V=4, edges 1-2, 2-3                 ->  false  (a plain path, no cycle)
 *   V=4, edges 1-2, 2-3, 3-1            ->  true   (triangle 1-2-3-1)
 *   V=6, edges 0-1 | 2-3, 3-4, 4-2      ->  true   (cycle is in component 2)
 *   V=1, no edges                       ->  false  (edge case: single vertex)
 *
 * APPROACH  (traversal + "where did I come from")
 *   1. In an undirected graph every edge u-v is stored twice, so from v you can
 *      always walk straight back to u. That step is NOT a cycle.
 *   2. So carry the parent along with the node. When you meet an already
 *      visited neighbour that is not the parent, you have found a second way
 *      into that vertex -> a cycle.
 *   3. DFS form: recurse with (node, parent).
 *      BFS form: the queue holds Node(vertex, parent) pairs instead of ints.
 *   4. Wrap either traversal in a loop over all vertices so disconnected
 *      components are covered. Once visited, a vertex is never re-explored,
 *      so the whole sweep is still linear.
 *
 * KEY INSIGHT
 *   Undirected cycle detection = "visited neighbour that is not my parent".
 *   Compare with a DIRECTED graph, where visited is not enough and you need a
 *   recursion-stack array (pathVis) instead - see A03_CycleCheckInDirectedGraph. Recognising which
 *   of the two rules applies is the whole question.
 *
 * COMPLEXITY
 *   Time  O(V + E)  every vertex is pushed once, every edge is scanned twice.
 *   Space O(V)      visited array plus the queue (BFS) or call stack (DFS).
 *
 * INTERVIEW FOLLOW-UPS
 *   - Print the cycle itself, not just true/false (keep a parent array and walk back).
 *   - Same question on a DIRECTED graph - why does the parent trick break?
 *   - Solve it with Union-Find: an edge whose endpoints already share a root closes a cycle.
 *   - What about self-loops (u-u) and parallel edges (u-v twice)? Both are cycles,
 *     and the plain parent check misses a duplicate u-v pair.
 *
 * RUN
 *   main() runs 4 cases (path, triangle, disconnected-with-cycle, single vertex)
 *   through BOTH the DFS and the BFS version and prints actual vs expected.
 */
import java.util.*;

class CheckForCycleInUnDirected {

    /** DFS form: recurse carrying the vertex we arrived from. */
    private boolean dfs(int node,
                        int parent,
                        boolean[] vis,
                        ArrayList<ArrayList<Integer>> adj) {
        vis[node] = true;
        for (int adjacentNode : adj.get(node)) {
            if (!vis[adjacentNode]) {
                if (dfs(adjacentNode, node, vis, adj)) return true;
            }
            // Visited AND not the vertex we came from -> a second route into it -> cycle.
            // e.g. 1-2-3-1 : from 3 we see 1, already visited, parent is 2, so cycle.
            else if (adjacentNode != parent) {
                return true;
            }
        }
        return false;
    }

    /** BFS form: the queue stores (vertex, parent) so the same rule applies. */
    private boolean bfs(ArrayList<ArrayList<Integer>> adj,
                        int src,
                        boolean[] vis) {
        Queue<Node> q = new LinkedList<>();
        q.add(new Node(src, -1));
        vis[src] = true;

        while (!q.isEmpty()) {
            Node current = q.poll();
            int u = current.u;
            int parent = current.parent;

            for (int v : adj.get(u)) {
                if (!vis[v]) {
                    vis[v] = true;          // mark at enqueue time, or a vertex can be queued twice
                    q.add(new Node(v, u));
                } else if (v != parent) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCycleDFS(int V, ArrayList<ArrayList<Integer>> adj) {
        boolean[] vis = new boolean[V];
        for (int node = 0; node < V; node++) {
            if (!vis[node] && dfs(node, -1, vis, adj)) return true;   // -1 = "no parent yet"
        }
        return false;
    }

    public boolean isCycleBFS(int V, ArrayList<ArrayList<Integer>> adj) {
        boolean[] vis = new boolean[V];
        for (int node = 0; node < V; node++) {
            if (!vis[node] && bfs(adj, node, vis)) return true;
        }
        return false;
    }

    // ------------------------------------------------------------------ demo

    private static ArrayList<ArrayList<Integer>> emptyGraph(int V) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        return adj;
    }

    /** Undirected edge: store it in both directions. */
    private static void addEdge(ArrayList<ArrayList<Integer>> adj, int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    public static void main(String[] args) {
        CheckForCycleInUnDirected obj = new CheckForCycleInUnDirected();

        // case 1: typical - a path 1-2-3, no cycle
        ArrayList<ArrayList<Integer>> g1 = emptyGraph(4);
        addEdge(g1, 1, 2);
        addEdge(g1, 2, 3);
        check(obj, "case 1 path 1-2-3        ", 4, g1, false);

        // case 2: typical - a triangle
        ArrayList<ArrayList<Integer>> g2 = emptyGraph(4);
        addEdge(g2, 1, 2);
        addEdge(g2, 2, 3);
        addEdge(g2, 3, 1);
        check(obj, "case 2 triangle 1-2-3-1  ", 4, g2, true);

        // case 3: tricky - cycle hides in the SECOND component, so the outer loop matters
        ArrayList<ArrayList<Integer>> g3 = emptyGraph(6);
        addEdge(g3, 0, 1);
        addEdge(g3, 2, 3);
        addEdge(g3, 3, 4);
        addEdge(g3, 4, 2);
        check(obj, "case 3 disconnected+cycle", 6, g3, true);

        // case 4: edge case - one vertex, no edges
        check(obj, "case 4 single vertex     ", 1, emptyGraph(1), false);
    }

    private static void check(CheckForCycleInUnDirected obj, String label,
                              int V, ArrayList<ArrayList<Integer>> adj, boolean expected) {
        boolean byDfs = obj.isCycleDFS(V, adj);
        boolean byBfs = obj.isCycleBFS(V, adj);
        System.out.println(label + " -> DFS " + byDfs + ", BFS " + byBfs
                + "   expected " + expected + ", " + expected);
    }
}

/** (vertex, parent) pair carried through the BFS queue. */
class Node {
    int u;
    int parent;

    public Node(int u, int parent) {
        this.u = u;
        this.parent = parent;
    }
}

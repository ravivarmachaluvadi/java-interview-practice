/*
 * =====================================================================
 *  Kosaraju: Strongly Connected Components                 GfG | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given a DIRECTED graph of V nodes as an adjacency list, count its
 *   strongly connected components. An SCC is a maximal group of nodes where
 *   every node can reach every other one. A node with no cycle around it is
 *   an SCC of size one, so every node belongs to exactly one SCC.
 *
 * EXAMPLE
 *   V = 5, edges 0->2, 0->3, 1->0, 2->1, 3->4
 *     ->  3 components: [0, 1, 2] (the cycle 0->2->1->0), [3], [4]
 *   V = 1, no edges                ->  1 component:  [0]
 *   V = 4, edges 0->1, 1->2, 2->3  ->  4 components: a DAG has no cycles at all
 *
 * APPROACH  (two DFS passes with the graph reversed in between)
 *   1. Pass one: plain DFS over the original graph, pushing each node onto a
 *      stack when its recursion FINISHES. The stack now holds nodes in
 *      decreasing finish time, so a node sits above everything it can reach.
 *   2. Reverse every edge into a transpose graph.
 *   3. Pass two: pop nodes off the stack; each unvisited pop starts a DFS on
 *      the transpose. Everything that DFS reaches is exactly one SCC.
 *
 * KEY INSIGHT
 *   Reversing the edges keeps every SCC intact (a reversed cycle is the same
 *   cycle) but destroys every one-way bridge BETWEEN components. So on the
 *   transpose a DFS cannot leak out of its own SCC - and the finish-time
 *   stack guarantees you start in the right place, the "source" SCC of the
 *   condensed DAG. Same stack idea as topological sort (A04), new purpose.
 *
 * COMPLEXITY
 *   Time  O(V + E)  two full DFS traversals plus one pass to build the transpose
 *   Space O(V + E)  transpose adjacency list, stack, visited array, recursion
 *
 * INTERVIEW FOLLOW-UPS
 *   - Tarjan's algorithm does the same job in ONE pass with tin/low links
 *     (the same machinery as bridges, A14) - know that it exists and why.
 *   - Condensation graph: collapse each SCC to a node and you get a DAG, which
 *     you can then topologically sort - the usual second half of the question.
 *   - 2-SAT is solved by exactly this: x and NOT x in one SCC means unsatisfiable.
 *   - Recursion depth on 10^5 nodes will overflow the stack - rewrite iteratively.
 *
 * RUN
 *   main() runs 3 cases (a graph with a cycle, a single node, a pure DAG)
 *   and prints the actual count and grouping against the expected ones.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class KosarajusAlgorithm {

    /** Pass one: DFS that pushes a node only after all its descendants are done. */
    private void dfsStack(int node, boolean[] vis,
                          List<List<Integer>> adj, Stack<Integer> st) {
        vis[node] = true;
        for (int next : adj.get(node)) {
            if (!vis[next]) {
                dfsStack(next, vis, adj, st);
            }
        }
        // Pushed on the way OUT, so finish order is what the stack records.
        st.push(node);
    }

    /** Pass two: DFS on the transpose, collecting one whole component. */
    private void collectComponent(int node, boolean[] vis,
                                  List<List<Integer>> adjT, List<Integer> component) {
        vis[node] = true;
        component.add(node);
        for (int next : adjT.get(node)) {
            if (!vis[next]) {
                collectComponent(next, vis, adjT, component);
            }
        }
    }

    /** Every edge u -> v becomes v -> u. */
    private List<List<Integer>> transpose(int V, List<List<Integer>> adj) {
        List<List<Integer>> reverseGraph = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            reverseGraph.add(new ArrayList<>());
        }
        for (int u = 0; u < V; u++) {
            for (int v : adj.get(u)) {
                reverseGraph.get(v).add(u);
            }
        }
        return reverseGraph;
    }

    /** @return each SCC as a list of nodes, in the order the second pass finds them. */
    public List<List<Integer>> kosarajuComponents(int V, List<List<Integer>> adj) {
        boolean[] vis = new boolean[V];
        Stack<Integer> finishOrder = new Stack<>();
        for (int i = 0; i < V; i++) {
            if (!vis[i]) {
                dfsStack(i, vis, adj, finishOrder);
            }
        }

        List<List<Integer>> adjT = transpose(V, adj);

        List<List<Integer>> components = new ArrayList<>();
        boolean[] visT = new boolean[V]; // fresh visited array for the second pass
        while (!finishOrder.isEmpty()) {
            int node = finishOrder.pop();
            if (!visT[node]) {
                List<Integer> component = new ArrayList<>();
                collectComponent(node, visT, adjT, component);
                components.add(component);
            }
        }
        return components;
    }

    /** The usual interview answer: just how many SCCs are there. */
    public int kosaraju(int V, List<List<Integer>> adj) {
        return kosarajuComponents(V, adj).size();
    }

    /** Builds a directed adjacency list from {from, to} pairs. */
    private static List<List<Integer>> buildAdj(int V, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
        }
        return adj;
    }

    private static void check(String label, int V, int[][] edges,
                              int expectedCount, String expectedGroups) {
        KosarajusAlgorithm sol = new KosarajusAlgorithm();
        List<List<Integer>> adj = buildAdj(V, edges);
        List<List<Integer>> groups = sol.kosarajuComponents(V, adj);
        System.out.println(label + ": count " + sol.kosaraju(V, adj) + " groups " + groups
                + "   expected count " + expectedCount + " groups " + expectedGroups);
    }

    public static void main(String[] args) {
        // Case 1: 0 -> 2 -> 1 -> 0 is a cycle, 3 and 4 hang off it one-way.
        check("case 1 one cycle plus two tails", 5,
                new int[][]{{0, 2}, {0, 3}, {1, 0}, {2, 1}, {3, 4}},
                3, "[[0, 1, 2], [3], [4]]");

        // Edge case: a lone node is its own component.
        check("case 2 single node", 1, new int[][]{}, 1, "[[0]]");

        // Tricky: a DAG has no cycles, so every node is an SCC by itself.
        check("case 3 pure DAG", 4,
                new int[][]{{0, 1}, {1, 2}, {2, 3}},
                4, "[[0], [1], [2], [3]]");
    }
}

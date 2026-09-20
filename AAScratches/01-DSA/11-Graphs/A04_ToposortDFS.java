/*
 * =====================================================================
 *  Topological Sort of a DAG                    GFG classic | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a directed acyclic graph with V vertices (0..V-1), return any linear
 *   ordering of the vertices such that for every edge u -> v, u appears before
 *   v. A topological order exists only for a DAG; a cycle makes it impossible.
 *   Many valid orders usually exist, so an answer is judged by the ordering
 *   property, not by matching one specific array.
 *
 * EXAMPLE
 *   V=6, edges 2->3, 3->1, 4->0, 4->1, 5->0, 5->2
 *     DFS  -> [5, 4, 2, 3, 1, 0]      Kahn -> [4, 5, 0, 2, 3, 1]     both valid
 *   V=3, no edges                      -> every permutation is valid
 *   Add 1->2 to the first graph (cycle 2->3->1->2)
 *     Kahn -> [4, 5, 0] only, length 3 < 6, which is how you detect the cycle
 *
 * APPROACH 1  (DFS + stack, post-order)
 *   1. DFS from every unvisited vertex.
 *   2. Push a vertex onto a stack only AFTER all of its descendants are pushed.
 *   3. A vertex therefore ends up above everything it points to, so popping the
 *      stack emits it first. Emptying the stack gives the order.
 *
 * APPROACH 2  (Kahn's algorithm, BFS on in-degree)
 *   1. Count in-degree of every vertex; queue all vertices with in-degree 0.
 *   2. Poll a vertex, append it to the order, then "delete" it from the graph by
 *      decrementing the in-degree of each neighbour.
 *   3. Any neighbour that drops to 0 is now a source: enqueue it.
 *   4. If the finished order holds fewer than V vertices, the leftovers are
 *      trapped in a cycle and no topological order exists.
 *
 * KEY INSIGHT
 *   Two ways to say "emit a vertex only once nothing forces it later".
 *   DFS says it structurally: finish-time order reversed is a topological order.
 *   Kahn says it by counting: in-degree 0 means no unmet prerequisite left.
 *   Prefer Kahn in interviews - it is iterative (no stack-overflow risk) and it
 *   detects a cycle for free, which DFS needs an extra pathVis array to do.
 *
 * COMPLEXITY
 *   Time  O(V + E)  both: each vertex handled once, each edge relaxed once.
 *   Space O(V)      stack/queue + visited or in-degree array (+ O(V) recursion in DFS).
 *
 * INTERVIEW FOLLOW-UPS
 *   - Course Schedule I / II (LC 207 / 210): the same sort with a cycle check (B11).
 *   - Return the lexicographically smallest valid order: use a PriorityQueue in Kahn's.
 *   - Minimum semesters / Parallel Courses: process Kahn's level by level (C09).
 *   - Alien Dictionary (LC 269): derive the edges from word pairs, then run this (C10).
 *
 * RUN
 *   main() runs 3 cases (a 6-vertex DAG, an edge-free graph, the same DAG made
 *   cyclic). Each prints the order produced, whether it is a valid topological
 *   order, and the expected value.
 */
import java.util.*;

class Solution {

    // ---------- Approach 1: DFS + stack ----------

    //    1->2->3->4  pushes 4 first, then 3, 2, 1
    //    Stack (top last) -> [4, 3, 2, 1];  popping gives 1 2 3 4
    private static void dfs(int node, boolean[] vis, Stack<Integer> st,
                            ArrayList<ArrayList<Integer>> adj) {
        vis[node] = true;
        for (int neighbour : adj.get(node)) {
            if (!vis[neighbour]) dfs(neighbour, vis, st, adj);
        }
        // pushed only after every descendant is pushed, so this node sits ABOVE
        // everything it points to -> it pops out before them
        st.push(node);
    }

    static int[] topoSortDFS(int V, ArrayList<ArrayList<Integer>> adj) {
        boolean[] vis = new boolean[V];
        Stack<Integer> st = new Stack<>();
        // loop over all vertices: the graph may have several disconnected components
        for (int i = 0; i < V; i++) {
            if (!vis[i]) dfs(i, vis, st, adj);
        }

        int[] ans = new int[V];
        int i = 0;
        while (!st.isEmpty()) ans[i++] = st.pop();
        return ans;
    }

    // ---------- Approach 2: Kahn's algorithm (BFS on in-degree) ----------

    // Source vertices have in-degree 0. If the returned order is shorter than V,
    // the graph has a cycle (see hasCycleKahn).
    static int[] topoSortKahn(int V, ArrayList<ArrayList<Integer>> adj) {
        int[] indegree = new int[V];
        for (int node = 0; node < V; node++) {
            for (int neighbour : adj.get(node)) indegree[neighbour]++;
        }

        Queue<Integer> q = new LinkedList<>();
        for (int node = 0; node < V; node++) {
            if (indegree[node] == 0) q.add(node);
        }

        int[] topo = new int[V];
        int i = 0;
        while (!q.isEmpty()) {
            int node = q.poll();
            topo[i++] = node;
            // node is placed, so "remove" it from the graph: every neighbour loses
            // one unmet prerequisite; any that reaches 0 becomes a new source
            for (int neighbour : adj.get(node)) {
                if (--indegree[neighbour] == 0) q.add(neighbour);
            }
        }
        // i < V here means some vertices never reached in-degree 0 -> cycle
        return Arrays.copyOf(topo, i);
    }

    // Kahn's gives cycle detection for free: a cycle means fewer than V vertices get placed.
    static boolean hasCycleKahn(int V, ArrayList<ArrayList<Integer>> adj) {
        return topoSortKahn(V, adj).length != V;
    }
}

class ToposortDFS {

    private static ArrayList<ArrayList<Integer>> emptyGraph(int V) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        return adj;
    }

    /**
     * A DAG can have many valid orders, so we verify the property instead of a
     * fixed array: every vertex present exactly once, and for each edge u -> v
     * the position of u is smaller than the position of v.
     */
    private static boolean isValidTopoOrder(int V, ArrayList<ArrayList<Integer>> adj, int[] order) {
        if (order.length != V) return false;
        int[] position = new int[V];
        Arrays.fill(position, -1);
        for (int i = 0; i < order.length; i++) {
            if (position[order[i]] != -1) return false;   // duplicate vertex
            position[order[i]] = i;
        }
        for (int u = 0; u < V; u++) {
            for (int v : adj.get(u)) {
                if (position[u] > position[v]) return false;
            }
        }
        return true;
    }

    private static void report(String label, int V, ArrayList<ArrayList<Integer>> adj) {
        int[] byDfs = Solution.topoSortDFS(V, adj);
        int[] byKahn = Solution.topoSortKahn(V, adj);
        System.out.println(label + " DFS  order " + Arrays.toString(byDfs)
                + "  valid " + isValidTopoOrder(V, adj, byDfs) + "   expected valid true");
        System.out.println(label + " Kahn order " + Arrays.toString(byKahn)
                + "  valid " + isValidTopoOrder(V, adj, byKahn) + "   expected valid true");
    }

    public static void main(String[] args) {
        // case 1: typical DAG
        int V = 6;
        ArrayList<ArrayList<Integer>> adj = emptyGraph(V);
        adj.get(2).add(3);
        adj.get(3).add(1);
        adj.get(4).add(0);
        adj.get(4).add(1);
        adj.get(5).add(0);
        adj.get(5).add(2);
        report("case 1 DAG  :", V, adj);
        System.out.println("case 1 hasCycle " + Solution.hasCycleKahn(V, adj)
                + "   expected false");

        // case 2: edge case - no edges at all, so any permutation is a valid order
        ArrayList<ArrayList<Integer>> noEdges = emptyGraph(3);
        report("case 2 edgeless:", 3, noEdges);

        // case 3: tricky - add 1->2 and the graph gains the cycle 2 -> 3 -> 1 -> 2.
        // Kahn stops early (its length is the tell); DFS still returns V vertices,
        // but that array is NOT a topological order - which is why DFS alone
        // cannot be trusted to detect a cycle.
        adj.get(1).add(2);
        int[] kahnCyclic = Solution.topoSortKahn(V, adj);
        System.out.println("case 3 cyclic Kahn order " + Arrays.toString(kahnCyclic)
                + "   expected [4, 5, 0]");
        System.out.println("case 3 cyclic Kahn length " + kahnCyclic.length
                + " of " + V + "   expected 3 of 6");
        System.out.println("case 3 hasCycle " + Solution.hasCycleKahn(V, adj)
                + "   expected true");
        System.out.println("case 3 DFS output still valid? "
                + isValidTopoOrder(V, adj, Solution.topoSortDFS(V, adj))
                + "   expected false");
    }
}

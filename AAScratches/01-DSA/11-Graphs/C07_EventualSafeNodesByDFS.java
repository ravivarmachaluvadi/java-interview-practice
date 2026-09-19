/**
 * Problem: LeetCode 802 Eventual Safe Nodes. Given a directed graph as an adjacency list (int[][]),
 * return every node from which EVERY path ends at a terminal node (no outgoing edges) and never enters a cycle.
 *
 * Approaches (both DFS cycle detection, both O(V + E) time, O(V) space):
 *  - threeArrays : vis / pathVis / check booleans (Striver style). Relies on a subtle invariant, see below.
 *  - stateMemo   : one int[] state (0 unvisited, 1 visiting, 2 safe) with memoised return. Cleaner, preferred in interviews.
 *
 * Key idea shared by both: a node is unsafe iff it is on a cycle OR it can reach a cycle.
 * DFS from every node; a back edge to a node that is still on the recursion stack means "cycle".
 */
import java.util.*;

class EventualSafeNodesByDFS {

    // ------------------------------------------------------------------
    // Approach 1: three boolean arrays
    // ------------------------------------------------------------------

    /**
     * Returns true if a cycle is reachable from node (node is unsafe).
     *
     * Invariant that makes this correct: when a cycle is found we return true
     * immediately WITHOUT resetting pathVis[node]. So every unsafe node stays
     * pathVis == true forever. A later DFS that reaches an unsafe node sees
     * vis && pathVis and correctly reports "cycle" without re-exploring it.
     * Only safe nodes get pathVis cleared and check set to true.
     */
    private boolean dfsCheck(int node, int[][] adj, boolean[] vis, boolean[] pathVis, boolean[] check) {
        vis[node] = true;
        pathVis[node] = true;
        check[node] = false;

        for (int it : adj[node]) {
            if (!vis[it]) {
                if (dfsCheck(it, adj, vis, pathVis, check)) return true;
            } else if (pathVis[it]) {
                return true; // back edge to a node on the stack (or to an already-known unsafe node)
            }
        }
        // Neither on a cycle nor pointing to one: safe
        check[node] = true;
        pathVis[node] = false;
        return false;
    }

    public int[] eventualSafeNodesThreeArrays(int V, int[][] adj) {
        boolean[] vis = new boolean[V];
        boolean[] pathVis = new boolean[V];
        boolean[] check = new boolean[V];
        for (int i = 0; i < V; i++) {
            if (!vis[i]) dfsCheck(i, adj, vis, pathVis, check);
        }
        List<Integer> temp = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            if (check[i]) temp.add(i);
        }
        int[] ans = new int[temp.size()];
        for (int i = 0; i < temp.size(); i++) ans[i] = temp.get(i);
        return ans;
    }

    // ------------------------------------------------------------------
    // Approach 2: single state array with memoised return
    // ------------------------------------------------------------------

    /** Returns true if node is safe. state: 0 = unvisited, 1 = visiting (on stack), 2 = safe. */
    private boolean dfs(int node, int[][] graph, int[] state) {
        if (state[node] != 0) {
            // state 2 -> already proven safe.
            // state 1 -> either on the current stack (back edge = cycle) or an earlier DFS
            //            aborted through it (unsafe). Either way: not safe.
            return state[node] == 2;
        }
        state[node] = 1; // visiting
        for (int nei : graph[node]) {
            if (!dfs(nei, graph, state)) return false; // leads to a cycle; leave state 1 as "unsafe" marker
        }
        state[node] = 2; // safe
        return true;
    }

    public List<Integer> eventualSafeNodesStateMemo(int[][] graph) {
        int n = graph.length;
        int[] state = new int[n];
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (dfs(i, graph, state)) result.add(i);
        }
        return result; // ascending order because we scan i = 0..n-1
    }

    // ------------------------------------------------------------------

    public static void main(String[] args) {
        // 0->1,2  1->2,3  2->5  3->0  4->5  5,6 terminal.  Cycle 0->1->3->0, so 0,1,3 unsafe.
        int[][] graph = {{1, 2}, {2, 3}, {5}, {0}, {5}, {}, {}};
        EventualSafeNodesByDFS sol = new EventualSafeNodesByDFS();

        System.out.println("threeArrays : " + Arrays.toString(sol.eventualSafeNodesThreeArrays(graph.length, graph))); // [2, 4, 5, 6]
        System.out.println("stateMemo   : " + sol.eventualSafeNodesStateMemo(graph));                                     // [2, 4, 5, 6]

        // Second case: cycle 1->2->3->1, node 0 points into it, node 4 is terminal. Only 4 is safe.
        int[][] graph2 = {{1}, {2}, {3}, {1}, {}};
        System.out.println("threeArrays : " + Arrays.toString(sol.eventualSafeNodesThreeArrays(graph2.length, graph2))); // [4]
        System.out.println("stateMemo   : " + sol.eventualSafeNodesStateMemo(graph2));                                     // [4]
    }
}

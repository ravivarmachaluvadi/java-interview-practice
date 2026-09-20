/*
 * =====================================================================
 *  Find Eventual Safe States                       LeetCode 802 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   You are given a directed graph as an adjacency list graph[i]. A node is
 *   "terminal" if it has no outgoing edges. A node is "safe" if EVERY path
 *   leaving it ends at a terminal node - that is, no path from it can wander
 *   into a cycle. Return all safe nodes in ascending order.
 *
 * EXAMPLE
 *   graph = [[1,2],[2,3],[5],[0],[5],[],[]]  ->  [2, 4, 5, 6]
 *     cycle 0 -> 1 -> 3 -> 0 makes 0, 1, 3 unsafe; 2 and 4 only reach 5.
 *   graph = [[1],[2],[3],[1],[]]             ->  [4]
 *     cycle 1 -> 2 -> 3 -> 1; node 0 feeds into it, so only terminal 4 survives.
 *   graph = [[0],[2],[]]                     ->  [1, 2]   (0 is a self-loop)
 *   graph = [[],[],[]]                       ->  [0, 1, 2]  (all terminal)
 *
 * APPROACH  (directed cycle detection, reused as a safety test)
 *   This is A03_CycleCheckInDirectedGraph's vis + pathVis cycle detector with one extra output
 *   array. Two equivalent implementations are shown and both are run from main().
 *
 *   threeArrays (Striver style)
 *     1. DFS every unvisited node, setting vis[node] and pathVis[node] on entry.
 *     2. If a neighbour is unvisited, recurse; if it returns true, a cycle is
 *        reachable, so return true immediately WITHOUT clearing pathVis.
 *     3. If a neighbour is already visited AND still pathVis, that is a back
 *        edge -> cycle -> return true.
 *     4. Survive the loop: mark check[node] = true (safe), clear pathVis, return false.
 *
 *   stateMemo (one int[] per node, usually cleaner to write live)
 *     0 = unvisited, 1 = visiting/unsafe, 2 = proven safe. dfs returns "is safe",
 *     memoising the answer, so each node is explored once.
 *
 * KEY INSIGHT
 *   Safe is the exact complement of "on a cycle or able to reach one", so the
 *   cycle detector you already own answers this with one extra array.
 *   The subtle part in threeArrays: when a cycle is found the recursion bails
 *   out WITHOUT resetting pathVis. So an unsafe node keeps pathVis == true
 *   forever, and a later DFS that walks into it sees vis && pathVis and
 *   correctly reports "cycle" without re-exploring. Only safe nodes ever get
 *   pathVis cleared - that leftover flag is the memo, and it is why the whole
 *   thing stays O(V + E) instead of degrading to exponential re-exploration.
 *
 * COMPLEXITY
 *   Time  O(V + E)  every node entered once, every edge examined once
 *   Space O(V)      the marker arrays plus recursion depth (O(V) worst case)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Solve it with Kahn's on the REVERSED graph: repeatedly peel nodes whose
 *     out-degree has dropped to zero; whatever gets peeled is safe.
 *   - Why can a node be safe even though it sits downstream of a cycle?
 *     Safety is about where you can GO, not where you can be reached FROM.
 *   - Deep graphs blow the recursion stack: rewrite the DFS iteratively.
 *   - Related: Course Schedule (does any cycle exist at all?).
 *
 * RUN
 *   main() runs 4 cases (cycle plus safe branch, everything feeding a cycle,
 *   self-loop, all-terminal) through BOTH implementations, printing actual vs
 *   expected on each line.
 */

import java.util.*;

class EventualSafeNodesByDFS {

    // ------------------------------------------------------------------
    // Approach 1: three boolean arrays (vis / pathVis / check)
    // ------------------------------------------------------------------

    /** Returns true if a cycle is reachable from node, i.e. node is UNSAFE. */
    private boolean dfsCheck(int node, int[][] adj,
                             boolean[] vis, boolean[] pathVis, boolean[] check) {
        vis[node] = true;
        pathVis[node] = true;
        check[node] = false;

        for (int next : adj[node]) {
            if (!vis[next]) {
                if (dfsCheck(next, adj, vis, pathVis, check)) return true;
            } else if (pathVis[next]) {
                // back edge to a node still on the stack, or to an already-known unsafe node
                return true;
            }
        }
        // Neither on a cycle nor pointing at one: safe. Clearing pathVis here is
        // what distinguishes a safe node from an abandoned (unsafe) one.
        check[node] = true;
        pathVis[node] = false;
        return false;
    }

    public int[] eventualSafeNodesThreeArrays(int v, int[][] adj) {
        boolean[] vis = new boolean[v];
        boolean[] pathVis = new boolean[v];
        boolean[] check = new boolean[v];

        for (int i = 0; i < v; i++) {
            if (!vis[i]) dfsCheck(i, adj, vis, pathVis, check);
        }

        List<Integer> safe = new ArrayList<>();
        for (int i = 0; i < v; i++) {
            if (check[i]) safe.add(i);
        }

        int[] ans = new int[safe.size()];
        for (int i = 0; i < ans.length; i++) ans[i] = safe.get(i);
        return ans;   // ascending, because the scan above runs 0..v-1
    }

    // ------------------------------------------------------------------
    // Approach 2: one state array with a memoised return value
    // ------------------------------------------------------------------

    /** Returns true if node is SAFE. state: 0 = unvisited, 1 = visiting, 2 = safe. */
    private boolean dfs(int node, int[][] graph, int[] state) {
        if (state[node] != 0) {
            // 2 -> already proven safe.
            // 1 -> either on the current stack (back edge = cycle) or an earlier
            //      DFS bailed out through it. Either way: not safe.
            return state[node] == 2;
        }
        state[node] = 1;
        for (int next : graph[node]) {
            if (!dfs(next, graph, state)) return false;  // leave state 1 as the "unsafe" marker
        }
        state[node] = 2;
        return true;
    }

    public List<Integer> eventualSafeNodesStateMemo(int[][] graph) {
        int[] state = new int[graph.length];
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < graph.length; i++) {
            if (dfs(i, graph, state)) result.add(i);
        }
        return result;   // ascending order because we scan i = 0..n-1
    }

    // ------------------------------------------------------------------

    private static void runCase(String label, int[][] graph, String expected) {
        // A fresh solver per call: both methods allocate their own state arrays,
        // but a new instance makes the independence obvious.
        EventualSafeNodesByDFS sol = new EventualSafeNodesByDFS();
        System.out.println(label
                + ": threeArrays="
                + Arrays.toString(sol.eventualSafeNodesThreeArrays(graph.length, graph))
                + ", stateMemo=" + sol.eventualSafeNodesStateMemo(graph)
                + "   expected " + expected + " for both");
    }

    public static void main(String[] args) {
        // 0->1,2  1->2,3  2->5  3->0  4->5  5,6 terminal. Cycle 0->1->3->0.
        runCase("cycle + branch ",
                new int[][]{{1, 2}, {2, 3}, {5}, {0}, {5}, {}, {}}, "[2, 4, 5, 6]");

        // Cycle 1->2->3->1; node 0 feeds into it; node 4 is terminal.
        runCase("feeds one cycle", new int[][]{{1}, {2}, {3}, {1}, {}}, "[4]");

        // Node 0 is a self-loop, which is a cycle of length 1.
        runCase("self-loop      ", new int[][]{{0}, {2}, {}}, "[1, 2]");

        // No edges at all: every node is terminal, therefore safe.
        runCase("all terminal   ", new int[][]{{}, {}, {}}, "[0, 1, 2]");
    }
}

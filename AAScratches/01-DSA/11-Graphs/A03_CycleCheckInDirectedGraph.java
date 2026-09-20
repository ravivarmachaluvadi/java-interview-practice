/*
 * =====================================================================
 *  Detect Cycle in a Directed Graph       GFG classic | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a directed graph with V vertices (0..V-1) as an adjacency list,
 *   report whether it contains a directed cycle. The graph may be
 *   disconnected, so the search must start from every unvisited vertex.
 *
 * EXAMPLE
 *   V=11, edges 1->2 2->3 3->4 3->7 4->5 5->6 7->5 8->9 9->10 10->8  ->  true
 *         (the cycle is 8 -> 9 -> 10 -> 8, in a separate component)
 *   Same graph minus 10->8                                           ->  false (a DAG)
 *   V=3, edge 0->0                                                   ->  true  (self loop)
 *   V=4, edges 0->1 0->2 1->3 2->3 (diamond)                         ->  false
 *
 * APPROACH  (DFS with a recursion-stack array: vis + pathVis)
 *   1. vis[node]     = 1 means "this node was explored at some point, ever".
 *   2. pathVis[node] = 1 means "this node is on the path I am standing on RIGHT NOW".
 *   3. On entering a node set both to 1. Recurse into unvisited neighbours.
 *   4. If a neighbour is already visited AND pathVis[neighbour] == 1, that edge
 *      points back into the current path -> a back edge -> a cycle.
 *   5. On the way out of a node set pathVis[node] = 0 (we left that path) but
 *      leave vis[node] = 1 (we never need to explore it again).
 *   6. Repeat from every vertex whose vis is still 0, to cover all components.
 *
 * KEY INSIGHT
 *   "Already visited" is NOT a cycle in a directed graph - a diamond 0->1->3,
 *   0->2->3 revisits 3 with no cycle anywhere. Only a node still ON the current
 *   recursion stack proves a cycle. That is the whole reason for the second
 *   array, and unsetting pathVis on the way back up is what makes it correct.
 *   Contrast with UNDIRECTED graphs (A02_CheckForCycleInUnDirected), where the rule
 *   is "visited and not my parent". Same shape of question, different test.
 *
 * COMPLEXITY
 *   Time  O(V + E)  each vertex entered once, each edge examined once.
 *   Space O(V)      vis + pathVis + recursion stack (O(V) deep in a long chain).
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the cycle itself, not just true/false (record the parent of each node).
 *   - Do it iteratively with Kahn's algorithm: if the topological order is shorter
 *     than V, a cycle exists (see A04_ToposortDFS).
 *   - Course Schedule (LC 207) is literally this question in disguise
 *     (see B10_CourseSchedule).
 *   - Eventual Safe Nodes (LC 802) reuses vis + pathVis with a third result array
 *     (see C06_EventualSafeNodesByDFS).
 *
 * RUN
 *   main() runs 4 cases: the cycle-in-a-far-component graph, the same graph as a
 *   DAG, a self loop, and a diamond. Each prints actual vs expected.
 */
import java.util.*;

class CycleCheckInDirectedGraph {

    private boolean dfsCheck(int node,
                             ArrayList<ArrayList<Integer>> adj,
                             int[] vis,
                             int[] pathVis) {
        vis[node] = 1;
        pathVis[node] = 1;   // node is now on the current path

        for (int neighbour : adj.get(node)) {
            if (vis[neighbour] == 0) {
                if (dfsCheck(neighbour, adj, vis, pathVis)) return true;
            }
            // Seen before - but only a back edge into the CURRENT path is a cycle.
            else if (pathVis[neighbour] == 1) {
                return true;
            }
        }

        // Every descendant explored: we are leaving this path, so clear pathVis only.
        // vis stays 1 so the node is never re-explored.
        pathVis[node] = 0;
        return false;
    }

    public boolean isCyclic(int V, ArrayList<ArrayList<Integer>> adj) {
        int[] vis = new int[V];
        int[] pathVis = new int[V];

        for (int i = 0; i < V; i++) {
            if (vis[i] == 0 && dfsCheck(i, adj, vis, pathVis)) return true;
        }
        return false;
    }
}

class CycleCheckInDirectedGraphMain {

    private static ArrayList<ArrayList<Integer>> emptyGraph(int V) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        return adj;
    }

    /** Builds the 11-vertex sample; withBackEdge adds 10->8 to close the cycle. */
    private static ArrayList<ArrayList<Integer>> sampleGraph(boolean withBackEdge) {
        ArrayList<ArrayList<Integer>> adj = emptyGraph(11);
        adj.get(1).add(2);
        adj.get(2).add(3);
        adj.get(3).add(4);
        adj.get(3).add(7);
        adj.get(4).add(5);
        adj.get(5).add(6);
        adj.get(7).add(5);
        adj.get(8).add(9);
        adj.get(9).add(10);
        if (withBackEdge) adj.get(10).add(8);
        return adj;
    }

    private static void check(String label, int V,
                              ArrayList<ArrayList<Integer>> adj, boolean expected) {
        boolean actual = new CycleCheckInDirectedGraph().isCyclic(V, adj);
        System.out.println(label + " -> " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1: typical - cycle 8->9->10->8 sits in a component the first DFS never reaches
        check("case 1 cycle 8->9->10->8 ", 11, sampleGraph(true), true);

        // case 2: same graph without the back edge - a plain DAG
        check("case 2 same graph, no 10->8", 11, sampleGraph(false), false);

        // case 3: edge case - a self loop is a cycle of length 1
        ArrayList<ArrayList<Integer>> selfLoop = emptyGraph(3);
        selfLoop.get(0).add(0);
        check("case 3 self loop 0->0     ", 3, selfLoop, true);

        // case 4: tricky - a diamond revisits node 3, but that is NOT a cycle
        ArrayList<ArrayList<Integer>> diamond = emptyGraph(4);
        diamond.get(0).add(1);
        diamond.get(0).add(2);
        diamond.get(1).add(3);
        diamond.get(2).add(3);
        check("case 4 diamond (revisit)  ", 4, diamond, false);
    }
}

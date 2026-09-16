/**
 * Problem: Given a directed graph represented by an adjacency list, find all
 * vertices that are eventually safe. A vertex is safe if every possible path
 * starting from it leads to a terminal node (no outgoing edges) and never enters
 * a cycle.
 *
 * Approach: Perform DFS with three boolean arrays:
 * - vis: marks nodes visited in any DFS traversal,
 * - pathVis: marks nodes on the current recursion stack,
 * - check: records whether a node is safe.
 * During DFS, if we encounter a back edge (pathVis[neighbor] == true) or
 * revisit an already visited node that is not yet determined safe, we detect
 * a cycle and return true. If no cycles are reachable from the current node,
 * mark it as safe.
 *
 * Complexity:
 * Time:  O(V + E) – each edge and vertex is processed once during DFS.
 * Space: O(V) – recursion stack plus three boolean arrays of size V.
 */
import java.util.*;

class EventualSafeNodesByDFS {

    private boolean dfsCheck(int node, int[][] adj,
                             boolean[] vis,
                             boolean[] pathVis,
                             boolean[] check) {

        vis[node] = true;
        pathVis[node] = true;
        check[node] = false;

        for (int it : adj[node]) {

            if (!vis[it]) {
                if (dfsCheck(it, adj, vis, pathVis, check))
                    return true;
            } else if (pathVis[it]) {
                return true;
            }
        }
        /* If the current node neither exist 
        in a cycle nor points to a cycle, 
        it can be marked as a safe node */
        check[node] = true;
        pathVis[node] = false;
        return false;
    }

    // Function to get the eventually safe nodes
    public int[] eventualSafeNodes(int V, int[][] adj) {

        boolean[] vis = new boolean[V];
        boolean[] pathVis = new boolean[V];
        boolean[] check = new boolean[V];
        for (int i = 0; i < V; i++) {
            if (!vis[i]) {
                dfsCheck(i, adj, vis, pathVis, check);
            }
        }
        List<Integer> temp = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            if (check[i])
                temp.add(i);
        }
        int[] ans = new int[temp.size()];
        for (int i = 0; i < temp.size(); i++)
            ans[i] = temp.get(i);
        return ans;
    }

    public static void main(String[] args) {
        int V = 7;
        int[][] adj = {
                {1, 2},
                {2, 3},
                {5},
                {0},
                {5},
                {},
                {}
        };
        EventualSafeNodesByDFS sol = new EventualSafeNodesByDFS();
        int[] ans = sol.eventualSafeNodes(V, adj);
        System.out.println("The eventually safe nodes in the graph are:");
        for (int node : ans) {
            System.out.print(node + " ");
        }
    }
}

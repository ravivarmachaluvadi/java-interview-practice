/**
 * Detects whether a directed graph contains a cycle.
 *
 * The algorithm performs a depth‑first search from each unvisited vertex,
 * maintaining two visitation arrays: one for all visited nodes and another
 * for the current recursion stack (path). If during DFS we encounter a node
 * that is already on the current path, a back edge exists and the graph has
 * a cycle.
 *
 * Time Complexity: O(V + E) – each vertex and edge is processed once.
 * Space Complexity: O(V) – recursion stack plus visitation arrays.
 */
import java.util.*;

class CycleCheckInDirectedGraph {
    private boolean dfsCheck(int node,
                             ArrayList<ArrayList<Integer>> adj,
                             int[] vis,
                             int[] pathVis) {
        vis[node] = 1;
        pathVis[node] = 1;

        for (int it : adj.get(node)) {
            if (vis[it] == 0) {
                if (dfsCheck(it, adj, vis, pathVis))
                    return true;
            }
            // if the node has been previously visited
            // but it has to be visited on the same path
            else if (pathVis[it] == 1) {
                return true;
            }
        }
        // all adjacent nodes of node are visited means
        // currentNode explored so set path un-visited
        pathVis[node] = 0;
        return false;
    }

    public boolean isCyclic(int V, ArrayList<ArrayList<Integer>> adj) {
        int[] vis = new int[V];
        int[] pathVis = new int[V];

        for (int i = 0; i < V; i++) {
            if (vis[i] == 0) {
                if (dfsCheck(i, adj, vis, pathVis)) return true;
            }
        }
        return false;
    }
}

class CycleCheckInDirectedGraphMain {
    public static void main(String[] args) {
        int V = 11;
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
        adj.get(1).add(2);
        adj.get(2).add(3);
        adj.get(3).add(4);
        adj.get(3).add(7);
        adj.get(4).add(5);
        adj.get(5).add(6);
        adj.get(7).add(5);
        adj.get(8).add(9);
        adj.get(9).add(10);
        adj.get(10).add(8);

        CycleCheckInDirectedGraph obj = new CycleCheckInDirectedGraph();
        boolean ans = obj.isCyclic(V, adj);
        if (ans)
            System.out.println("True");
        else
            System.out.println("False");
    }
}

/**
 * Problem:
 *   Given a directed graph with V vertices and adjacency list representation,
 *   compute the number of strongly connected components (SCCs) using Kosaraju's algorithm.
 *
 * Approach:
 *   1. Perform DFS on the original graph to fill a stack with vertices in order of finishing times.
 *   2. Build the transpose (reversed) graph.
 *   3. Pop vertices from the stack and run DFS on the transposed graph; each DFS visit
 *      identifies one SCC, incrementing the count.
 *
 * Complexity:
 *   Time:  O(V + E) – two passes of DFS plus graph reversal.
 *   Space: O(V + E) – adjacency lists, reverse graph, stack, and visited arrays.
 */
import java.util.*;

class KosarajusAlgorithm {

    private void dfsStack(int node,
                          int[] vis,
                          ArrayList<ArrayList<Integer>> adj,
                          Stack<Integer> st) {

        vis[node] = 1;
        // Traverse all its neighbors
        for (int it : adj.get(node)) {
            if (vis[it] == 0) {
                // Recursively perform DFS if not visited already
                dfsStack(it, vis, adj, st);
            }
        }
        // Push the node in stack
        st.push(node);
    }

    private void visitingDFS(int node,
                             int[] vis,
                             ArrayList<ArrayList<Integer>> adjT) {
        vis[node] = 1;
        // Traverse all its neighbors
        for (int it : adjT.get(node)) {
            if (vis[it] == 0)
                visitingDFS(it, vis, adjT);
        }
    }

    public int kosaraju(int V, ArrayList<ArrayList<Integer>> adj) {
        int[] vis = new int[V];
        Stack<Integer> st = new Stack<>();
        for (int i = 0; i < V; i++) {
            if (vis[i] == 0) {
                dfsStack(i, vis, adj, st);
            }
        }

        // To store the reversed graph
        ArrayList<ArrayList<Integer>> reverseGraph = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            reverseGraph.add(new ArrayList<>());
        }

        for (int i = 0; i < V; i++) {
            vis[i] = 0;
            for (int it : adj.get(i)) {
                reverseGraph.get(it).add(i);
            }
        }

        /* To store the count of strongly
        connected components */
        int count = 0;

        /* Start DFS call from every unvisited
        node based on their finishing time */
        while (!st.isEmpty()) {
            // Get the node
            int node = st.pop();

            // If not visited already
            if (vis[node] == 0) {
                count += 1;
                visitingDFS(node, vis, reverseGraph);
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int V = 5;
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
        adj.get(0).add(2);
        adj.get(0).add(3);
        adj.get(1).add(0);
        adj.get(2).add(1);
        adj.get(3).add(4);

        KosarajusAlgorithm sol = new KosarajusAlgorithm();
        int count = sol.kosaraju(V, adj);
        System.out.println("Number of strongly connected components: " + count);// 3
    }
}

import java.util.*;
// it also known as tarzans algorithm
class BridgesInGraph {
    private int timer = 1;

    private void dfs(int node, int parent, int[] vis, List<Integer>[] adj,
                     int[] tin, int[] low, List<List<Integer>> bridges) {
        vis[node] = 1;
        // tin(time of insertion) is also known as discovery time
        tin[node] = low[node] = timer;
        timer++;

        for (int it : adj[node]) {
            if (it == parent) continue;
            if (vis[it] == 0) {
                dfs(it, node, vis, adj, tin, low, bridges);
                low[node] = Math.min(low[it], low[node]);
                /* If the lowest time of insertion of the 
                node is found to be greater than the 
                time of insertion of the neighbor */
                if (low[it] > tin[node]) {
                    bridges.add(Arrays.asList(it, node));
                }
            } else {
                // Update the lowest time of insertion of the node
                low[node] = Math.min(low[node], low[it]);
            }
        }
    }

    // Function to identify the bridges in a graph
    public List<List<Integer>> criticalBridges(int n,
                                               List<List<Integer>> connections) {

        // Adjacency list
        List<Integer>[] adj = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
        }

        // Add all the edges to the adjacency list
        for (List<Integer> it : connections) {
            int u = it.get(0), v = it.get(1);
            adj[u].add(v);
            adj[v].add(u);
        }

        // Visited array
        int[] vis = new int[n];

        // To store the time of insertion (discovery time) of nodes
        int[] tin = new int[n];

        // To store the lowest time of insert of the nodes
        int[] low = new int[n];

        // To store the bridges of the graph
        List<List<Integer>> bridges = new ArrayList<>();

        // Start a DFS traversal from node 0 with its parent as -1
        dfs(0, -1, vis, adj, tin, low, bridges);

        // Return the computed result
        return bridges;
    }

    public static void main(String[] args) {
        int v = 4;
        List<List<Integer>> edges = Arrays.asList(
                Arrays.asList(0, 1),
                Arrays.asList(1, 2),
                Arrays.asList(2, 0),
                Arrays.asList(1, 3)
        );

        // Creating an instance of Solution class
        BridgesInGraph obj = new BridgesInGraph();

        // Function call to identify the bridges in a graph
        List<List<Integer>> ans = obj.criticalBridges(v, edges);

        System.out.println("The critical connections in the given graph are:");
        for (List<Integer> bridge : ans) {
            System.out.println(bridge.get(0) + " " + bridge.get(1));
        }
    }
}

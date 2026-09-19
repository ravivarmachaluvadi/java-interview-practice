import java.util.*;

class BFSandDFS {

    // zero (0) vertix and their adjacent nodes and
    // adjacent node's adjacent nodes and so on
    public ArrayList<Integer> bfsOfGraph(int V, ArrayList<ArrayList<Integer>> adj) {

        ArrayList<Integer> bfs = new ArrayList<>();
        boolean[] vis = new boolean[V];
        Queue<Integer> q = new LinkedList<>();

        q.add(0);
        vis[0] = true;

        while (!q.isEmpty()) {
            Integer u = q.poll();
            bfs.add(u);
            // Get all adjacent vertices of the dequeued vertex s
            // If a adjacent has not been visited, then mark it
            // visited and enqueue it to explore it's adjacent vertices
            for (Integer vNode : adj.get(u)) {
                // if not visited
                if (!vis[vNode]) {
                    vis[vNode] = true;
                    q.add(vNode);
                }
            }
        }
        return bfs;
    }

    public static void dfs(int node, boolean[] vis, ArrayList<ArrayList<Integer>> adj, ArrayList<Integer> dfs) {

        //marking current node as visited
        //node == u
        vis[node] = true;
        dfs.add(node);

        //getting neighbour nodes , it == v
        for (Integer it : adj.get(node)) {
            // if not visited then explore
            if (!vis[it]) {
                dfs(it, vis, adj, dfs);
            }
        }
    }

    // Function to return a list containing the DFS traversal of the graph.
    public ArrayList<Integer> dfsOfGraph(int V, ArrayList<ArrayList<Integer>> adj) {
        //boolean array to keep track of visited vertices
        boolean[] vis = new boolean[V];
        vis[0] = true;
        ArrayList<Integer> dfs = new ArrayList<>();
        dfs(0, vis, adj, dfs);
        return dfs;
    }

    public static void main(String[] args) {

        ArrayList<ArrayList<Integer>> adjList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            adjList.add(new ArrayList<>());
        }
        // bi-directional graph
        // edges
        adjList.get(0).add(1);
        adjList.get(1).add(0);
        adjList.get(0).add(4);
        adjList.get(4).add(0);
        adjList.get(1).add(2);
        adjList.get(2).add(1);
        adjList.get(1).add(3);
        adjList.get(3).add(1);

        BFSandDFS sl = new BFSandDFS();
        // 0,1,2,3,4 -> 5 total number of vertices
        ArrayList<Integer> bfs = sl.bfsOfGraph(5, adjList);
        ArrayList<Integer> dfs = sl.dfsOfGraph(5, adjList);
        System.out.println("bfs " + bfs); // [0, 1, 4, 2, 3]
        System.out.println("dfs " + dfs); // [0, 1, 2, 3, 4]

    }
}

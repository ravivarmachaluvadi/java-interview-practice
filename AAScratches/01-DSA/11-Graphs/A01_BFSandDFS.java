/*
 * =====================================================================
 *  BFS and DFS traversal of a graph          GFG | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given V vertices (0..V-1) and an adjacency list, return the order in which
 *   BFS and DFS visit the vertices starting from vertex 0. Neighbours are visited
 *   in the order they appear in the adjacency list. The graph may be disconnected;
 *   the classic GFG version only traverses the component containing 0.
 *
 * EXAMPLE
 *   edges 0-1, 0-4, 1-2, 1-3 (undirected)
 *     BFS from 0 -> [0, 1, 4, 2, 3]   level by level: 0 | 1 4 | 2 3
 *     DFS from 0 -> [0, 1, 2, 3, 4]   go deep along 0->1->2, back up, 3, back up, 4
 *   single vertex, no edges -> BFS [0], DFS [0]
 *   0-1, 2-3 (two components) -> BFS from 0 = [0, 1]; vertices 2, 3 unreachable
 *
 * APPROACH  (BFS queue and DFS recursion)
 *   BFS
 *   1. Mark the start visited and push it on a queue.
 *   2. Pop a vertex, append it to the answer, then for every unvisited neighbour
 *      mark it visited and push it. Marking at push time stops duplicates.
 *   DFS
 *   1. Mark the current vertex visited and append it to the answer.
 *   2. For every neighbour not yet visited, recurse into it.
 *
 * KEY INSIGHT
 *   The only difference between the two is the container: a FIFO queue gives
 *   level order (BFS), the call stack gives depth first (DFS). Every other file
 *   in this folder is one of these two loops with extra state bolted on
 *   (parent, colour, distance, recursion-stack flag).
 *
 * COMPLEXITY
 *   Time  O(V + E)  every vertex is enqueued/visited once, every edge scanned once
 *   Space O(V)      visited array plus queue / recursion depth
 *
 * INTERVIEW FOLLOW-UPS
 *   - How do you cover a disconnected graph? Loop over all vertices, start a
 *     traversal from each one that is still unvisited.
 *   - Iterative DFS: replace recursion with an explicit Stack (avoids stack
 *     overflow on deep graphs).
 *   - Why mark visited when enqueuing, not when dequeuing? Otherwise the same
 *     vertex can sit in the queue several times.
 *
 * RUN
 *   main() runs 3 cases (typical, single vertex, disconnected) and prints
 *   actual vs expected for both traversals.
 */
import java.util.*;

class BFSandDFS {

    /** Breadth-first: vertex 0, then its neighbours, then their neighbours, and so on. */
    public ArrayList<Integer> bfsOfGraph(int V, ArrayList<ArrayList<Integer>> adj) {
        ArrayList<Integer> order = new ArrayList<>();
        boolean[] visited = new boolean[V];
        Queue<Integer> queue = new LinkedList<>();

        queue.add(0);
        visited[0] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);
            for (int v : adj.get(u)) {
                // mark at enqueue time so a vertex never enters the queue twice
                if (!visited[v]) {
                    visited[v] = true;
                    queue.add(v);
                }
            }
        }
        return order;
    }

    /** Depth-first: dive into the first unvisited neighbour before touching the rest. */
    public ArrayList<Integer> dfsOfGraph(int V, ArrayList<ArrayList<Integer>> adj) {
        boolean[] visited = new boolean[V];
        ArrayList<Integer> order = new ArrayList<>();
        dfs(0, visited, adj, order);
        return order;
    }

    private static void dfs(int node, boolean[] visited,
                            ArrayList<ArrayList<Integer>> adj, ArrayList<Integer> order) {
        visited[node] = true;
        order.add(node);
        for (int next : adj.get(node)) {
            if (!visited[next]) {
                dfs(next, visited, adj, order);
            }
        }
    }

    // ---------- test scaffolding ----------

    private static ArrayList<ArrayList<Integer>> undirectedGraph(int V, int[][] edges) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        return adj;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        BFSandDFS g = new BFSandDFS();

        // case 1: typical - 5 vertices, edges 0-1, 0-4, 1-2, 1-3
        ArrayList<ArrayList<Integer>> adj1 = undirectedGraph(5,
                new int[][]{{0, 1}, {0, 4}, {1, 2}, {1, 3}});
        print("case 1 bfs", g.bfsOfGraph(5, adj1), "[0, 1, 4, 2, 3]");
        print("case 1 dfs", g.dfsOfGraph(5, adj1), "[0, 1, 2, 3, 4]");

        // case 2: edge - single vertex, no edges
        ArrayList<ArrayList<Integer>> adj2 = undirectedGraph(1, new int[][]{});
        print("case 2 bfs", g.bfsOfGraph(1, adj2), "[0]");
        print("case 2 dfs", g.dfsOfGraph(1, adj2), "[0]");

        // case 3: tricky - disconnected graph; 2 and 3 are never reached from 0
        ArrayList<ArrayList<Integer>> adj3 = undirectedGraph(4,
                new int[][]{{0, 1}, {2, 3}});
        print("case 3 bfs", g.bfsOfGraph(4, adj3), "[0, 1]");
        print("case 3 dfs", g.dfsOfGraph(4, adj3), "[0, 1]");
    }
}

/**
 * Problem: Detect whether an undirected graph contains a cycle.
 *
 * Approach:
 * 1. Perform BFS from each unvisited vertex, keeping track of the parent node for
 *    every visited vertex.
 * 2. If during traversal we encounter an already visited vertex that is not
 *    the parent of the current vertex, a back‑edge indicates a cycle.
 * 3. The DFS version is provided but commented out; BFS suffices.
 *
 * Time Complexity: O(V + E) – each vertex and edge is processed once.
 * Space Complexity: O(V) for the visited array and queue (or recursion stack in DFS).
 */
import java.util.*;

class CheckForCycleInUnDirected {

    private boolean dfs(int node,
                        int parent,
                        boolean[] vis,
                        ArrayList<ArrayList<Integer>> adj) {
        vis[node] = true;
        for (int adjacentNode : adj.get(node)) {
            // if adjacent node not visited then visit it
            if (!vis[adjacentNode]) {
                if (dfs(adjacentNode, node, vis, adj)) return true;
            }
            /**
             1->2
             2->1
             cycle in undirected Graph
             1->2->1 (parent-> node->adjNode) (parent!=adjNode) return true
             */
            // adjacentNode(vNode) node visited but not parent of current node
            // then means it's forming cycle
            else if (adjacentNode != parent) return true;
        }
        return false;
    }

    boolean checkForCycleBFS(ArrayList<ArrayList<Integer>> adj,
                             int s,
                             boolean[] vis) {
        Queue<Node> q = new LinkedList<>(); //BFS
        q.add(new Node(s, -1));
        vis[s] = true;
        while (!q.isEmpty()) {
            int u = q.peek().u;
            int par = q.peek().parent;
            q.poll();

            for (Integer v : adj.get(u)) {
                if (!vis[v]) {
                    q.add(new Node(v, u));
                    vis[v] = true;
                } else if (par != v) return true;
            }
        }
        return false;
    }

    public boolean isCycle(int V, ArrayList<ArrayList<Integer>> adj) {
        boolean[] vis = new boolean[V];

        for (int node = 0; node < V; node++)
            if (!vis[node])
//                if (checkForCycleBFS(adj, node, vis))
                if (dfs(node, -1, vis, adj))
                    return true;

        return false;
    }

    public static void main(String[] args) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            adj.add(new ArrayList<>());
        }
        adj.get(1).add(2);
        adj.get(2).add(1);
        adj.get(2).add(3);
        adj.get(3).add(2);

        CheckForCycleInUnDirected obj = new CheckForCycleInUnDirected();
        boolean ans = obj.isCycle(4, adj);
        if (ans)
            System.out.println("1");
        else
            System.out.println("0");
    }
}

class Node {
    int u;
    int parent;

    public Node(int u, int parent) {
        this.u = u;
        this.parent = parent;
    }
}

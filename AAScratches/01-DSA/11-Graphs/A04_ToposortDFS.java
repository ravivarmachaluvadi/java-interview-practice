import java.util.*;

/**
 * Problem: Topological sort of a Directed Acyclic Graph (DAG) — a linear ordering of
 *          vertices such that for every edge u -> v, u appears before v.
 *          Topological sorting only exists for a DAG (directed edges, no cycle).
 * <p>
 * Approaches:
 *   1. topoSortDFS   — DFS, push each node to a stack AFTER all its descendants (post-order);
 *                      popping the stack gives the order. O(V + E).
 *   2. topoSortKahn  — Kahn's BFS: repeatedly take nodes whose in-degree is 0 (sources),
 *                      and decrement in-degree of their neighbours. O(V + E).
 * <p>
 * Both are valid topological orders; they differ because a DAG can have many valid orders.
 * Kahn's has a bonus: if the result has fewer than V nodes, the graph contains a cycle
 * (those nodes never reach in-degree 0). DFS needs a separate path-visited array to detect cycles.
 */
class Solution {

    // ---------- Approach 1: DFS + stack ----------

    //    1->2->3->4
    //    Stack -> [4, 3, 2, 1];  popping gives 1 2 3 4
    private static void dfs(int node, boolean[] vis, Stack<Integer> st,
                            ArrayList<ArrayList<Integer>> adj) {
        vis[node] = true;
        for (int it : adj.get(node)) {
            if (!vis[it])
                dfs(it, vis, st, adj);
        }
        // pushed only after every descendant is pushed, so the node ends up ABOVE
        // all nodes it points to -> pops out before them
        st.push(node);
    }

    static int[] topoSortDFS(int V, ArrayList<ArrayList<Integer>> adj) {
        boolean[] vis = new boolean[V];
        Stack<Integer> st = new Stack<>();
        // loop over all nodes: graph may have disconnected components
        for (int i = 0; i < V; i++) {
            if (!vis[i])
                dfs(i, vis, st, adj);
        }

        int[] ans = new int[V];
        int i = 0;
        while (!st.isEmpty())
            ans[i++] = st.pop();

        return ans;
    }

    // ---------- Approach 2: Kahn's algorithm (BFS on in-degree) ----------

    // Source nodes have in-degree 0.
    // If the returned order has size != V, the graph has a cycle (see hasCycleKahn).
    static int[] topoSortKahn(int V, ArrayList<ArrayList<Integer>> adj) {
        int[] indegree = new int[V];
        for (int node = 0; node < V; node++) {
            for (int it : adj.get(node))
                indegree[it]++;
        }
        Queue<Integer> q = new LinkedList<>();
        for (int node = 0; node < V; node++) {
            if (indegree[node] == 0)
                q.add(node);
        }
        int[] topo = new int[V];
        int i = 0;
        while (!q.isEmpty()) {
            int node = q.poll();
            topo[i++] = node;
            // node is now placed in the order, so "remove" it from the graph:
            // reduce in-degree of every neighbour by 1; any that hit 0 become sources
            for (int it : adj.get(node)) {
                indegree[it]--;
                if (indegree[it] == 0)
                    q.add(it);
            }
        }
        // i < V here means some nodes were never freed -> cycle
        return Arrays.copyOf(topo, i);
    }

    // Kahn's gives cycle detection for free: a cycle means fewer than V nodes get placed.
    static boolean hasCycleKahn(int V, ArrayList<ArrayList<Integer>> adj) {
        return topoSortKahn(V, adj).length != V;
    }
}

class ToposortDFS {

    private static ArrayList<ArrayList<Integer>> emptyGraph(int V) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        return adj;
    }

    public static void main(String[] args) {
        int V = 6;
        ArrayList<ArrayList<Integer>> adj = emptyGraph(V);
        adj.get(2).add(3);
        adj.get(3).add(1);
        adj.get(4).add(0);
        adj.get(4).add(1);
        adj.get(5).add(0);
        adj.get(5).add(2);

        System.out.println("DFS  topo sort : " + Arrays.toString(Solution.topoSortDFS(V, adj)));  // [5, 4, 2, 3, 1, 0]
        System.out.println("Kahn topo sort : " + Arrays.toString(Solution.topoSortKahn(V, adj))); // [4, 5, 0, 2, 3, 1]
        System.out.println("Has cycle (Kahn): " + Solution.hasCycleKahn(V, adj));                // false

        // Add edge 1 -> 2 : now 2 -> 3 -> 1 -> 2 is a cycle, so no topological order exists
        adj.get(1).add(2);
        System.out.println("After adding 1->2, Kahn result : " + Arrays.toString(Solution.topoSortKahn(V, adj))); // [4, 5, 0] only
        System.out.println("Has cycle (Kahn): " + Solution.hasCycleKahn(V, adj));                                // true
    }
}

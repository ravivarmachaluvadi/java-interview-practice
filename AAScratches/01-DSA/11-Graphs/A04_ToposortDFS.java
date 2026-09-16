import java.util.*;

/**
 * Topological sorting only exists in Directed Acyclic Graph (DAG).
 * <p>
 * If the nodes of a graph are connected through
 * <p>
 * directed edges and the graph does not contain a cycle,
 * <p>
 * it is called a directed acyclic graph(DAG).
 * <p>
 * The topological sorting of a directed acyclic graph is nothing
 * <p>
 * but the linear ordering of vertices such that
 * <p>
 * if there is an edge between node u and v(u -> v),
 * <p>
 * node u appears before v in that ordering.
 */
class Solution {

    //    1->2->3->4
    //    Stack -> [4, 3, 2, 1];
    private static void dfs(int node, boolean[] vis, Stack<Integer> st,
                            ArrayList<ArrayList<Integer>> adj) {
        vis[node] = true;
        for (int it : adj.get(node)) {
            if (!vis[it])
                dfs(it, vis, st, adj);
        }
        // last node added as first node to stack , first
        // node will be added to as first node to stack
        st.push(node);
    }

    static int[] topoSort(int V, ArrayList<ArrayList<Integer>> adj) {
        boolean[] vis = new boolean[V];
        Stack<Integer> st = new Stack<>();
        // exploring all nodes graph may have disconnected components
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
}

class ToposortDFS {
    public static void main(String[] args) {
        int V = 6;
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
        adj.get(2).add(3);
        adj.get(3).add(1);
        adj.get(4).add(0);
        adj.get(4).add(1);
        adj.get(5).add(0);
        adj.get(5).add(2);

        int[] ans = Solution.topoSort(V, adj);
        for (int node : ans) {
            System.out.print(node + " "); // 5 4 2 3 1 0
        }
    }
}

import java.util.*;

class Solution {

    // toposort size != noOfVertices if Graph has
    // cycle using BFS Kahn's Algorithm
    // Source nodes in-degree is zero 0
    static int[] topoSort(int V, ArrayList<ArrayList<Integer>> adj) {
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
            // node is in your topo sort
            // so please remove it from the indegree means
            // reduce indegree for all adjacent nodes by 1
            for (int it : adj.get(node)) {
                indegree[it]--;
                if (indegree[it] == 0)
                    q.add(it);
            }
        }
        return topo;
    }
}

class KahnsTopoBFS {
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
            System.out.print(node + " "); // 4 5 0 2 3 1
        }
    }
}

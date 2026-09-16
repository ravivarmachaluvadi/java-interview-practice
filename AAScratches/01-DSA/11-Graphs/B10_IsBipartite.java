/**
 * Determines whether an undirected graph is bipartite.
 *
 * The algorithm colors each connected component using two colors via DFS.
 * If a neighbor has the same color as the current node, the graph is not
 * bipartite and false is returned immediately; otherwise all nodes are
 * colored successfully.
 *
 * Time Complexity: O(V + E) – each vertex and edge is visited once during DFS.
 * Space Complexity: O(V + E) – adjacency list storage plus recursion stack up to V.
 */
import java.util.*;

class IsBipartite {

    private static boolean dfs(int node,
                               int col,
                               int[] color,
                               ArrayList<ArrayList<Integer>> adj) {
        color[node] = col;
        for (int it : adj.get(node)) {
            if (color[it] == -1) {
                // remember nested if
                if (!dfs(it, 1 - col, color, adj)) return false;
            }
            // remember if else if
            else if (color[it] == col) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBipartite(int V, ArrayList<ArrayList<Integer>> adj) {
        int[] color = new int[V];
        Arrays.fill(color, -1);
        for (int i = 0; i < V; i++) {
            if (color[i] == -1) {
                // remember passing i not v
                if (!dfs(i, 0, color, adj)) return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        // V = 4, E = 4
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            adj.add(new ArrayList<>());
        }
        adj.get(0).add(2);
        adj.get(2).add(0);
        adj.get(0).add(3);
        adj.get(3).add(0);
        adj.get(1).add(3);
        adj.get(3).add(1);
        adj.get(2).add(3);
        adj.get(3).add(2);

        boolean ans = isBipartite(4, adj);
        if (ans) System.out.println("1");
        else System.out.println("0");
    }

}

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The bellman-Ford algorithm helps to find the shortest distance from the source
 * <p>
 * node to all other nodes. But, we have already learned Dijkstra's algorithm
 * <p>
 * Bellman-Ford's algorithm successfully solves these problems.
 * <p>
 * It works fine with negative edges as well as it is able to detect
 * <p>
 * if the graph contains a negative cycle. But this algorithm is only
 * <p>
 * applicable for directed graphs. In order to apply this algorithm to an
 * <p>
 * undirected graph, we just need to convert the undirected edges into
 * <p>
 * directed edges like the following:
 */

class Solution {
    static int[] bellman_ford(int V,
                              ArrayList<ArrayList<Integer>> edges, int S) {
        int[] dist = new int[V];
        Arrays.fill(dist, (int) (1e8));
        dist[S] = 0;
        // V= N -> N is number of nodes
        // V x E
        // N-1 relaxations for all edges
        for (int i = 0; i < V - 1; i++) {
            for (ArrayList<Integer> edge : edges) {
                int u = edge.get(0);
                int v = edge.get(1);
                int wt = edge.get(2);
                if (dist[u] != 1e8 && dist[u] + wt < dist[v]) {
                    dist[v] = dist[u] + wt;
                }
            }
        }
        // Nth relaxation to check negative cycle
        for (ArrayList<Integer> it : edges) {
            int u = it.get(0);
            int v = it.get(1);
            int wt = it.get(2);
            if (dist[u] != 1e8 && dist[u] + wt < dist[v]) {
                int[] temp = new int[1];
                temp[0] = -1;
                return temp;
            }
        }
        return dist;
    }
}

// 0 5 3 3 1 2
class BellmanFord {
    public static void main(String[] args) {
        int V = 6;
        int src = 0;
        ArrayList<ArrayList<Integer>> edges = new ArrayList<>() {
            {
                add(new ArrayList<>(Arrays.asList(3, 2, 6)));
                add(new ArrayList<>(Arrays.asList(5, 3, 1)));
                add(new ArrayList<>(Arrays.asList(0, 1, 5)));
                add(new ArrayList<>(Arrays.asList(1, 5, -3)));
                add(new ArrayList<>(Arrays.asList(1, 2, -2)));
                add(new ArrayList<>(Arrays.asList(3, 4, -2)));
                add(new ArrayList<>(Arrays.asList(2, 4, 3)));
            }
        };


        int[] dist = Solution.bellman_ford(V, edges, src);
        for (int i = 0; i < V; i++) {
            System.out.print(dist[i] + " ");
        }
        System.out.println("");
    }
}

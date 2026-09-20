/*
 * =====================================================================
 *  Bellman-Ford: Shortest Path With Negative Edges        GfG | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a directed graph of V nodes as an edge list {u, v, weight} and a
 *   source S, return the shortest distance from S to every node. Weights may
 *   be negative; if a negative-weight cycle exists, return {-1} because no
 *   shortest path does. Unreachable nodes keep the sentinel INF (100000000).
 *
 * EXAMPLE
 *   V = 6, S = 0, edges = (3,2,6) (5,3,1) (0,1,5) (1,5,-3)
 *                         (1,2,-2) (3,4,-2) (2,4,3)
 *     ->  [0, 5, 3, 3, 1, 2]    e.g. 0->1->5->3->4 costs 5-3+1-2 = 1
 *   V = 3, S = 0, edges = (0,1,4)            ->  [0, 4, 100000000]  node 2 unreachable
 *   V = 3, S = 0, edges = (0,1,1) (1,2,-1) (2,1,-1)  ->  [-1]  cycle 1->2->1 costs -2
 *
 * APPROACH  (relax every edge V-1 times, then one extra pass)
 *   1. dist[] = INF everywhere, dist[S] = 0.
 *   2. Repeat V-1 times: for every edge (u, v, w), if u is reachable and
 *      dist[u] + w < dist[v], lower dist[v]. One pass locks in at least one
 *      more hop of the true shortest path, and a simple path has <= V-1 hops.
 *   3. Run the same sweep one more time. If anything still improves, some
 *      path can be made cheaper forever: report the negative cycle.
 *   4. The "dist[u] != INF" guard stops INF + (-5) from looking like a real
 *      distance and leaking a fake path out of an unreachable node.
 *
 * KEY INSIGHT
 *   Dijkstra freezes a node the moment it leaves the heap, which a later
 *   negative edge could invalidate - so it is simply wrong on negative
 *   weights. Bellman-Ford never freezes anything, it just re-relaxes all E
 *   edges V-1 times: slower, but immune. The Vth pass is the detection trick -
 *   a correct answer is already stable, so any further gain proves a cycle.
 *
 * COMPLEXITY
 *   Time  O(V * E)  V-1 sweeps (plus one) over the whole edge list
 *   Space O(V)      the dist array only; edges are read in place
 *
 * INTERVIEW FOLLOW-UPS
 *   - Print the cycle itself? Keep a parent[] and walk back V steps from the
 *     node that improved on the Vth pass, then follow parents until repeat.
 *   - Early exit: stop when a full sweep changes nothing (often way under V-1).
 *   - Undirected graph with a negative edge is always a negative cycle (u->v->u).
 *   - All pairs with negative edges instead -> Floyd-Warshall (see A10).
 *
 * RUN
 *   main() runs 3 cases (typical negative-edge graph, unreachable node,
 *   negative cycle) and prints actual vs expected.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution {

    /** Sentinel for "not reached yet"; small enough that INF + w never overflows. */
    static final int INF = 100000000;

    /**
     * @return dist[] from S, or a one-element {-1} when a negative cycle exists.
     */
    static int[] bellmanFord(int V, List<List<Integer>> edges, int S) {
        int[] dist = new int[V];
        Arrays.fill(dist, INF);
        dist[S] = 0;

        // V-1 sweeps: after sweep k, every shortest path using <= k edges is final.
        for (int pass = 0; pass < V - 1; pass++) {
            boolean changed = relaxAllEdges(edges, dist);
            if (!changed) {
                break; // nothing left to improve, the answer is already stable
            }
        }

        // One extra sweep. A stable answer cannot improve; an improvement means
        // some reachable cycle has negative total weight.
        if (relaxAllEdges(edges, dist)) {
            return new int[]{-1};
        }
        return dist;
    }

    /** One full pass over the edge list. Returns true if any distance dropped. */
    private static boolean relaxAllEdges(List<List<Integer>> edges, int[] dist) {
        boolean changed = false;
        for (List<Integer> edge : edges) {
            int u = edge.get(0);
            int v = edge.get(1);
            int wt = edge.get(2);
            // Guard: an unreachable u must never hand out a distance.
            if (dist[u] != INF && dist[u] + wt < dist[v]) {
                dist[v] = dist[u] + wt;
                changed = true;
            }
        }
        return changed;
    }
}

class BellmanFord {

    /** Builds the edge list the solver expects from plain {u, v, w} triples. */
    private static List<List<Integer>> toEdgeList(int[][] triples) {
        List<List<Integer>> edges = new ArrayList<>();
        for (int[] t : triples) {
            edges.add(new ArrayList<>(Arrays.asList(t[0], t[1], t[2])));
        }
        return edges;
    }

    private static void check(String label, int[] actual, int[] expected) {
        System.out.println(label + ": " + Arrays.toString(actual)
                + "   expected " + Arrays.toString(expected));
    }

    public static void main(String[] args) {
        int[][] graph = {
                {3, 2, 6}, {5, 3, 1}, {0, 1, 5},
                {1, 5, -3}, {1, 2, -2}, {3, 4, -2}, {2, 4, 3}
        };
        check("case 1 negative edges, no cycle",
                Solution.bellmanFord(6, toEdgeList(graph), 0),
                new int[]{0, 5, 3, 3, 1, 2});

        // Edge case: node 2 has no incoming edge, so it stays at INF.
        check("case 2 unreachable node",
                Solution.bellmanFord(3, toEdgeList(new int[][]{{0, 1, 4}}), 0),
                new int[]{0, 4, Solution.INF});

        // Tricky: 1 -> 2 -> 1 costs -2, so distances can fall forever.
        int[][] cycle = {{0, 1, 1}, {1, 2, -1}, {2, 1, -1}};
        check("case 3 negative cycle",
                Solution.bellmanFord(3, toEdgeList(cycle), 0),
                new int[]{-1});
    }
}

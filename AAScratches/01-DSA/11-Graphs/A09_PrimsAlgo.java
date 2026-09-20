/*
 * =====================================================================
 *  Prim's Algorithm: Minimum Spanning Tree               GfG | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a connected, undirected, weighted graph of V nodes as an adjacency
 *   list, return the total weight of a Minimum Spanning Tree: the cheapest
 *   set of V-1 edges that keeps every node connected with no cycle.
 *   On a disconnected graph this returns only the tree of node 0's component.
 *
 * EXAMPLE
 *   V = 5, edges (0,1,2) (0,2,1) (1,2,1) (2,3,2) (3,4,1) (4,2,2)
 *     ->  5    picks 0-2(1), 2-1(1), 2-3(2) [or 2-4], 3-4(1)
 *   V = 1, no edges                    ->  0   nothing to connect
 *   V = 4, edges (0,1,3) (2,3,4)       ->  3   only node 0's component is grown
 *
 * APPROACH  (grow one tree, always take the cheapest edge leaving it)
 *   1. Min-heap of (weight, node), seeded with (0, 0): the start costs nothing.
 *   2. Pop the cheapest entry. If that node is already in the tree it is a
 *      stale copy from an earlier push - skip it.
 *   3. Otherwise mark it visited and add the popped weight: that weight is
 *      the edge that pulled it in.
 *   4. Push every edge to a not-yet-visited neighbour, keyed by edge weight,
 *      and repeat until the heap drains.
 *
 * KEY INSIGHT
 *   It is the Dijkstra loop with one word changed: Dijkstra keys the heap by
 *   dist[u] + w (cost of the whole path from the source), Prim keys it by w
 *   alone (cost of this one edge). Path cost versus edge cost is the entire
 *   difference, and it answers "how is MST related to shortest path?". The
 *   lazy-deletion trick - push duplicates, skip visited ones on pop - is what
 *   avoids needing a decrease-key operation.
 *
 * COMPLEXITY
 *   Time  O(E log E)  every edge can be pushed once and popped once
 *   Space O(E)        the heap can hold one entry per edge, plus vis[]
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the edges, not just the sum? Push the parent alongside and record
 *     (parent, node) whenever a node is first marked visited.
 *   - Prim vs Kruskal (A12): Prim is better on dense graphs, Kruskal on sparse
 *     ones and when the edges arrive already sorted.
 *   - Disconnected input: restart from every unvisited node for a spanning FOREST.
 *   - Is the MST unique? Yes if and only if all edge weights are distinct.
 *   - Maximum spanning tree: flip the comparator (or negate the weights).
 *
 * RUN
 *   main() runs 3 cases (typical graph, single node, disconnected graph)
 *   and prints actual vs expected.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/** One heap entry: "this node can be joined to the tree for this weight". */
class Pair {
    int node;
    int distance;

    // Note the argument order: weight first, then node. Easy to flip by accident.
    public Pair(int distance, int node) {
        this.node = node;
        this.distance = distance;
    }
}

class Solution {

    /** adj.get(u) holds {neighbour, weight} pairs. Returns the MST weight. */
    static int spanningTree(int V, List<List<int[]>> adj) {
        PriorityQueue<Pair> pq = new PriorityQueue<>((x, y) -> x.distance - y.distance);
        boolean[] inTree = new boolean[V];

        pq.add(new Pair(0, 0)); // start anywhere; reaching the start is free
        int sum = 0;

        while (!pq.isEmpty()) {
            Pair cheapest = pq.remove();
            int node = cheapest.node;
            int wt = cheapest.distance;

            // Stale entry: this node was already pulled in by a cheaper edge.
            if (inTree[node]) {
                continue;
            }
            inTree[node] = true;
            sum += wt; // the edge that actually pulled this node in

            for (int[] edge : adj.get(node)) {
                int adjNode = edge[0];
                int edgeWeight = edge[1];
                if (!inTree[adjNode]) {
                    // Key by the EDGE weight, not by a running total - this is
                    // the one line that separates Prim from Dijkstra.
                    pq.add(new Pair(edgeWeight, adjNode));
                }
            }
        }
        return sum;
    }
}

class PrimsAlgo {

    /** Builds an undirected adjacency list from {u, v, weight} triples. */
    private static List<List<int[]>> buildAdj(int V, int[][] edges) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] e : edges) {
            adj.get(e[0]).add(new int[]{e[1], e[2]});
            adj.get(e[1]).add(new int[]{e[0], e[2]}); // undirected: store both ways
        }
        return adj;
    }

    private static void check(String label, int actual, int expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] edges = {{0, 1, 2}, {0, 2, 1}, {1, 2, 1}, {2, 3, 2}, {3, 4, 1}, {4, 2, 2}};
        check("case 1 connected 5-node graph",
                Solution.spanningTree(5, buildAdj(5, edges)), 5);

        // Edge case: a single node is already a spanning tree of weight 0.
        check("case 2 single node",
                Solution.spanningTree(1, buildAdj(1, new int[][]{})), 0);

        // Tricky: Prim only grows the component it starts in, so edge (2,3,4)
        // is never seen. A spanning FOREST would need a restart per component.
        check("case 3 disconnected, component of node 0 only",
                Solution.spanningTree(4, buildAdj(4, new int[][]{{0, 1, 3}, {2, 3, 4}})), 3);
    }
}

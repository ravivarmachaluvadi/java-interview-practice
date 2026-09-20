/*
 * =====================================================================
 *  Kruskal's Algorithm: MST With Union-Find              GfG | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an undirected weighted graph of n nodes as an edge list, return the
 *   total weight of a Minimum Spanning Tree and the edges it uses. A
 *   disconnected graph yields a minimum spanning FOREST instead - the
 *   cheapest tree per component, so fewer than n-1 edges come back.
 *
 * EXAMPLE
 *   n = 6, edges (0,1,4) (0,2,4) (1,2,2) (1,0,4) (2,3,3)
 *                (2,5,2) (2,4,4) (3,4,3) (5,4,3)
 *     ->  14   using 1--2(2), 2--5(2), 2--3(3), 3--4(3), 0--1(4)
 *   n = 4, edges (0,1,1) (2,3,2)  ->  3   a forest: only 2 edges, not n-1 = 3
 *   n = 1, no edges               ->  0   one node is already a tree
 *
 * APPROACH  (sort the edges, keep the ones that merge two components)
 *   1. Sort all edges by weight, cheapest first.
 *   2. Start a Disjoint Set Union with every node in its own set.
 *   3. Walk the sorted edges. If u and v are already in the same set, this
 *      edge would close a cycle - skip it. Otherwise union them, add the
 *      weight, and record the edge.
 *   4. Stop when the edges run out. A connected graph yields exactly n-1 edges.
 *
 * KEY INSIGHT
 *   "Would this edge create a cycle?" is the only question Kruskal asks, and
 *   DSU answers it in near-constant time - that is why the two are always
 *   taught together. The greedy choice is safe because of the cut property:
 *   the cheapest edge crossing any split of the nodes is always in some MST,
 *   so taking the globally cheapest non-cycling edge can never be a mistake.
 *
 * COMPLEXITY
 *   Time  O(E log E)  dominated by the sort; the DSU scan is near O(E * alpha)
 *   Space O(n + E)    parent/rank arrays plus the sorted copy of the edges
 *
 * INTERVIEW FOLLOW-UPS
 *   - Kruskal vs Prim (A09_PrimsAlgo): Kruskal wins on sparse or pre-sorted
 *     edges and handles disconnected input for free; Prim wins on dense graphs.
 *   - Detect disconnection: the MST has n-1 edges if and only if it is connected.
 *   - Maximum spanning tree: sort descending, everything else is identical.
 *   - Why union by rank AND path compression? Either alone is O(log n);
 *     together they are effectively O(1) amortised.
 *
 * RUN
 *   main() runs 3 cases (connected graph, disconnected forest, single node)
 *   and prints actual vs expected.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** One undirected edge; compareTo puts the cheapest edge first. */
class Edge implements Comparable<Edge> {
    int u, v, weight;

    Edge(int u, int v, int weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }

    public int compareTo(Edge b) {
        return Integer.compare(this.weight, b.weight);
    }

    @Override
    public String toString() {
        return u + "--" + v + "(" + weight + ")";
    }
}

/** Disjoint Set Union with path compression and union by rank. */
class DSU {
    int[] parent, rank;

    DSU(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i; // every node starts alone
        }
    }

    /** Root of x's set, flattening the chain on the way back up. */
    int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // path compression
        }
        return parent[x];
    }

    /** Merges the two sets. Returns false when they were already the same set. */
    boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX == rootY) {
            return false; // already connected - joining them would make a cycle
        }
        // Hang the shallower tree under the deeper one so depth grows slowly.
        if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++; // equal depth: the winner gets one level taller
        }
        return true;
    }
}

class KruskalAlgorithm {

    /**
     * @param mstOut if not null, receives the chosen edges in the order taken.
     * @return total weight of the MST (or of the spanning forest).
     */
    static int kruskalMST(int n, List<Edge> edges, List<Edge> mstOut) {
        List<Edge> sorted = new ArrayList<>(edges); // do not disturb the caller's list
        Collections.sort(sorted);

        DSU dsu = new DSU(n);
        int mstWeight = 0;

        for (Edge edge : sorted) {
            // union() returns false when u and v already share a root, which is
            // exactly the "this edge closes a cycle" case.
            if (dsu.union(edge.u, edge.v)) {
                mstWeight += edge.weight;
                if (mstOut != null) {
                    mstOut.add(edge);
                }
            }
        }
        return mstWeight;
    }

    private static void check(String label, int n, List<Edge> edges,
                              int expectedWeight, String expectedEdges) {
        List<Edge> mst = new ArrayList<>();
        int weight = kruskalMST(n, edges, mst);
        System.out.println(label + ": weight " + weight + " edges " + mst
                + "   expected weight " + expectedWeight + " edges " + expectedEdges);
    }

    private static List<Edge> edgeList(int[][] triples) {
        List<Edge> edges = new ArrayList<>();
        for (int[] t : triples) {
            edges.add(new Edge(t[0], t[1], t[2]));
        }
        return edges;
    }

    public static void main(String[] args) {
        int[][] connected = {
                {0, 1, 4}, {0, 2, 4}, {1, 2, 2}, {1, 0, 4}, {2, 3, 3},
                {2, 5, 2}, {2, 4, 4}, {3, 4, 3}, {5, 4, 3}
        };
        check("case 1 connected", 6, edgeList(connected), 14,
                "[1--2(2), 2--5(2), 2--3(3), 3--4(3), 0--1(4)]");

        // Tricky: two components, so only 2 edges come back instead of n-1 = 3.
        check("case 2 disconnected forest", 4,
                edgeList(new int[][]{{0, 1, 1}, {2, 3, 2}}), 3,
                "[0--1(1), 2--3(2)]");

        // Edge case: nothing to connect.
        check("case 3 single node", 1, edgeList(new int[][]{}), 0, "[]");
    }
}

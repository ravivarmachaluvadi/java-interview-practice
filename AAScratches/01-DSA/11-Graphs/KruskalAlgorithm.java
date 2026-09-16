import java.util.*;

// Class to represent an edge with its weight
class Edge implements Comparable<Edge> {
    int u, v, weight;

    Edge(int u, int v, int weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }

    // Comparator function to sort edges by weight
    public int compareTo(Edge b) {
        return this.weight - b.weight;
    }
}

// Disjoint Set Union (DSU) or Union-Find structure
class DSU {
    int[] parent, rank;

    DSU(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    // Find the root of a set with path compression
    int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    // Union two sets by rank
    void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX != rootY) {
            if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
        }
    }
}

/**
 * Edges in the Minimum Spanning Tree:
 * 1 -- 2 == 2
 * 2 -- 5 == 2
 * 2 -- 3 == 3
 * 3 -- 4 == 3
 * 0 -- 1 == 4
 * Total weight of MST: 14
 */
class KruskalAlgorithm {

    // Kruskal's Algorithm
    static int kruskalMST(int n, ArrayList<Edge> edges) {
        Collections.sort(edges); // Sort edges by weight
        DSU dsu = new DSU(n);
        int mstWeight = 0;
        ArrayList<Edge> mstEdges = new ArrayList<>();

        for (Edge edge : edges) {
            // Check if adding this edge creates a cycle
            if (dsu.find(edge.u) != dsu.find(edge.v)) {
                dsu.union(edge.u, edge.v); // <-- Missing step
                mstWeight += edge.weight;
                mstEdges.add(edge);
            }
        }

        // Printing the MST
        System.out.println("Edges in the Minimum Spanning Tree:");
        for (Edge e : mstEdges)
            System.out.println(e.u + " -- " + e.v + " == " + e.weight);

        return mstWeight;
    }

    public static void main(String[] args) {
        int n = 6; // Number of vertices
        ArrayList<Edge> edges = new ArrayList<>();

        // Add edges: src, dest, weight
        edges.add(new Edge(0, 1, 4));
        edges.add(new Edge(0, 2, 4));
        edges.add(new Edge(1, 2, 2));
        edges.add(new Edge(1, 0, 4));
        edges.add(new Edge(2, 3, 3));
        edges.add(new Edge(2, 5, 2));
        edges.add(new Edge(2, 4, 4));
        edges.add(new Edge(3, 4, 3));
        edges.add(new Edge(5, 4, 3));

        // Run Kruskal's Algorithm
        int mstWeight = kruskalMST(n, edges);
        System.out.println("Total weight of MST: " + mstWeight);
    }
}

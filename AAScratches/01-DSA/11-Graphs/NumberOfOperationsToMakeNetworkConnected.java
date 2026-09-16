import java.util.*;

class DisjointSet {
    int[] rank, parent, size;

    DisjointSet(int n) {
        rank = new int[n + 1];
        parent = new int[n + 1];
        size = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    int findUPar(int node) {
        if (node == parent[node])
            return node;
        return parent[node] = findUPar(parent[node]);
    }

    void unionByRank(int u, int v) {
        int ulp_u = findUPar(u);
        int ulp_v = findUPar(v);
        if (ulp_u == ulp_v) return;
        if (rank[ulp_u] < rank[ulp_v]) {
            parent[ulp_u] = ulp_v;
        } else if (rank[ulp_v] < rank[ulp_u]) {
            parent[ulp_v] = ulp_u;
        } else {
            parent[ulp_v] = ulp_u;
            rank[ulp_u]++;
        }
    }

    // Function to implement union by size
    void unionBySize(int u, int v) {
        int ulp_u = findUPar(u);
        int ulp_v = findUPar(v);
        if (ulp_u == ulp_v) return;
        if (size[ulp_u] < size[ulp_v]) {
            parent[ulp_u] = ulp_v;
            size[ulp_v] += size[ulp_u];
        } else {
            parent[ulp_v] = ulp_u;
            size[ulp_u] += size[ulp_v];
        }
    }
}

// Solution class
public class NumberOfOperationsToMakeNetworkConnected {

    public int solve(int n, int[][] Edge) {
        int size = Edge.length;
        /* Return -1 if connecting all 
        vertices is not possible */
        if (size < n - 1) return -1;
        DisjointSet ds = new DisjointSet(n);

        // Add all the edges in the set
        for (int i = 0; i < size; i++) {
            ds.unionByRank(Edge[i][0], Edge[i][1]);
        }
        // number of components
        int count = 0;
        for (int i = 0; i < n; i++)
            if (ds.parent[i] == i) count++;

        // Return the result
        return count - 1;
    }

    public static void main(String[] args) {
        int n = 4;
        int[][] Edge = {
                {0, 1},
                {0, 2},
                {1, 2}
        };

        NumberOfOperationsToMakeNetworkConnected sol =
                new NumberOfOperationsToMakeNetworkConnected();
        int ans = sol.solve(n, Edge);

        System.out.println("The number of operations to make network connected is: " + ans);
    }
}

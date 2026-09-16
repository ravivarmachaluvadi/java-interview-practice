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

// https://leetcode.com/problems/most-stones-removed-with-same-row-or-column/description/

class MostStonesRemovedWithSameRowOrColumn {
    // Function to remove maximum stones
    public int maxRemove(int[][] stones, int n) {
        int maxRow = 0;
        int maxCol = 0;
        for (int[] stone : stones) {
            maxRow = Math.max(maxRow, stone[0]);
            maxCol = Math.max(maxCol, stone[1]);
        }

        DisjointSet ds = new DisjointSet(maxRow + maxCol + 2);
        // To store the nodes having a stone in Disjoint Set
        Set<Integer> stoneNodes = new HashSet<>();

        // Iterate on all stones
        for (int[] stone : stones) {
            // Row number
            int nodeRow = stone[0];
            // Converted column number
            // colNumber+rowSize+1
            int nodeCol = stone[1] + maxRow + 1;
            ds.unionBySize(nodeRow, nodeCol);
            // Add the nodes to the map
            stoneNodes.add(nodeRow);
            stoneNodes.add(nodeCol);
        }

        // To store the number of connected components
        int k = 0;
        // Iterate on the set
        for (int node : stoneNodes) {
            /* Increment the count if
            a new component is found */
            if (ds.findUPar(node) == node)
                k++;
        }
        return n - k;
    }

    /*
    Row\Col  0   1   2
        0    1   1   0
        1    1   0   1
        2    0   1   1
     */
    public static void main(String[] args) {
        int n = 6;
        int[][] stones = {
                {0, 0}, {0, 1}, {1, 0},
                {1, 2}, {2, 1}, {2, 2}
        };
        MostStonesRemovedWithSameRowOrColumn sol = new MostStonesRemovedWithSameRowOrColumn();
        int ans = sol.maxRemove(stones, n);
        // Output
        System.out.println("The size of the largest island is: " + ans);
        //ans :  5
    }
}

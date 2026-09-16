/**
 * Problem: Given an n×n binary matrix, change at most one 0 to 1 and return the
 * maximum possible size of a connected component (island) of 1s.
 *
 * Approach: Use a Disjoint Set Union to merge all existing islands. For each
 * zero cell, collect unique neighboring island representatives, sum their sizes,
 * add one for the flipped cell, and track the maximum. Finally compare with
 * any pre‑existing island size in case no flip improves it.
 *
 * Time Complexity: O(n² α(n)) – linear scans plus DSU operations per edge.
 * Space Complexity: O(n²) for parent, rank, size arrays and auxiliary sets.
 */
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

// https://takeuforward.org/plus/dsa/problems/making-a-large-island?tab=editorial
// 827. Making A Large Island

/**
 * You are given an n x n binary matrix grid.
 * <p>
 * You are allowed to change at most one 0 to be 1.
 * <p>
 * Return the size of the largest island in grid after applying this operation.
 * <p>
 * An island is a 4-directionally connected group of 1s.
 */
class MakingALargeIsland {
    private int[] delRow = {-1, 0, 1, 0};
    private int[] delCol = {0, 1, 0, -1};

    /* Helper function to check 
    if a pixel is within boundaries */
    private boolean isValid(int i, int j, int n) {
        // Return false if pixel is invalid
        if (i < 0 || i >= n) return false;
        if (j < 0 || j >= n) return false;
        return true;
    }

    private void addInitialIslands(int[][] grid,
                                   DisjointSet ds,
                                   int n) {
        // Traverse all the cells in the grid
        // doing union all of land node with {x,y}=1
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (grid[row][col] == 0) continue;

                for (int ind = 0; ind < 4; ind++) {
                    int newRow = row + delRow[ind];
                    int newCol = col + delCol[ind];
                    if (isValid(newRow, newCol, n) &&
                            grid[newRow][newCol] == 1) {
                        int nodeNo = row * n + col;
                        int adjNodeNo = newRow * n + newCol;
                        ds.unionBySize(nodeNo, adjNodeNo);
                    }
                }
            }
        }
    }

    // Function to get the size of the largest island
    public int largestIsland(int[][] grid) {
        // Dimensions of grid
        int n = grid.length;
        DisjointSet ds = new DisjointSet(n * n);
        //doing union by of dsu
        addInitialIslands(grid, ds, n);
        int ans = 0;

        // Traverse on the grid
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {

                // If the cell is a land cell, skip
                if (grid[row][col] == 1) continue;
                
                /* Set to store the ultimate 
                parents of neighboring islands */
                Set<Integer> components = new HashSet<>();

                // Traverse on all its neighbors
                for (int ind = 0; ind < 4; ind++) {
                    // Coordinates of neighboring cell
                    int newRow = row + delRow[ind];
                    int newCol = col + delCol[ind];

                    if (isValid(newRow, newCol, n) &&
                            grid[newRow][newCol] == 1) {
                            
                        /* Perform union and store 
                        ultimate parent in the set */
                        int nodeNumber = newRow * n + newCol;
                        components.add(ds.findUPar(nodeNumber));
                    }
                }

                int sizeTotal = 0;

                for (int parent : components) {
                    // Update the size
                    sizeTotal += ds.size[ds.findUPar(parent)];
                }

                // Store the maximum size of island
                ans = Math.max(ans, sizeTotal + 1);
            }
        }

        // Edge case
        for (int cellNo = 0; cellNo < n * n; cellNo++) {
            // Keep the answer updated
            ans = Math.max(ans, ds.size[ds.findUPar(cellNo)]);
        }

        // Return the answer
        return ans;
    }

    public static void main(String[] args) {
        int[][] grid = {
                {1, 0},
                {0, 1}
        };

        // Creating instance of Solution class
        MakingALargeIsland sol = new MakingALargeIsland();
        
        /* Function call to get the 
        size of the largest island */
        int ans = sol.largestIsland(grid);

        // Output
        System.out.println("The size of the largest island is: " + ans);
    }
}

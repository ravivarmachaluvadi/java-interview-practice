/*
 * =====================================================================
 *  Making A Large Island                            LeetCode 827 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given an n x n binary grid, you may flip at most one 0 into a 1. Return the
 *   size of the largest 4-directionally connected group of 1s afterwards. "At
 *   most one" matters: if the grid is already all 1s there is nothing to flip
 *   and the answer is the whole grid.
 *
 * EXAMPLE
 *   {{1,0},{0,1}}            ->  3   flip either 0 and two diagonals join
 *   {{1,1},{1,0}}            ->  4   flip the single 0 and the grid is one island
 *   {{1,1},{1,1}}            ->  4   nothing to flip, the island is already whole
 *   {{0,0},{0,0}}            ->  1   the flipped cell is an island of one
 *   {{1,0,1},{1,0,1},{1,1,1}} -> 8   one U-shaped island of 7 touches the same
 *                                    0 twice, so it must be counted once
 *
 * DESIGN (classes and why)
 *   DisjointSet - union-find over n*n cells, keyed by cell number row*n + col.
 *     It unions BY SIZE, not by rank, because the algorithm has to read the
 *     component size later; rank is an internal balancing number with no
 *     meaning to the caller.
 *   MakingALargeIsland - two passes over the grid:
 *     pass 1 (addInitialIslands) unions every pair of neighbouring 1s, which
 *            leaves each island as one DSU component whose root carries its size;
 *     pass 2 (largestIsland) visits each 0, collects the ROOTS of its up-to-four
 *            land neighbours in a Set, sums those component sizes and adds 1 for
 *            the flipped cell.
 *     A final sweep over the land cells covers the "no zero anywhere" case.
 *
 * KEY DECISIONS
 *   - Roots go into a HashSet. Two different neighbours of the same 0 can belong
 *     to the same island, and adding its size twice is the bug this problem is
 *     built to catch.
 *   - Nothing is actually unioned in pass 2. The flip is hypothetical and must
 *     not leak into the next candidate cell, so we only read sizes.
 *   - A 2D cell is flattened to the single integer row*n + col so a plain array
 *     DSU works with no nested structures.
 *   - findUPar does path compression on the way back up, so repeated lookups in
 *     pass 2 stay effectively constant time.
 *
 * COMPLEXITY
 *   Time  O(n^2 * alpha(n^2))  each cell is touched a constant number of times,
 *                              each touch costing a near-constant DSU operation
 *   Space O(n^2)               parent and size arrays, plus a set of at most 4
 *
 * INTERVIEW FOLLOW-UPS
 *   - Solve it without DSU: flood fill each island with an id, store id -> size
 *     in a map, then do the same neighbour dedupe. Same complexity.
 *   - What if you may flip up to two zeros? (The clean DSU trick stops working.)
 *   - Allow 8-directional connectivity - what in the code changes?
 *   - Why union by size here rather than union by rank?
 *
 * RUN
 *   main() runs 5 cases (typical, the all-land and all-water edge cases, and
 *   the duplicate-neighbour trap) and prints actual vs expected.
 */

import java.util.*;

class DisjointSet {
    int[] parent, size;

    DisjointSet(int n) {
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    /** Root of node's component, compressing the path on the way back. */
    int findUPar(int node) {
        if (node == parent[node]) return node;
        return parent[node] = findUPar(parent[node]);
    }

    /** Attach the smaller component under the larger one and merge the sizes. */
    void unionBySize(int u, int v) {
        int rootU = findUPar(u);
        int rootV = findUPar(v);
        if (rootU == rootV) return;
        if (size[rootU] < size[rootV]) {
            parent[rootU] = rootV;
            size[rootV] += size[rootU];
        } else {
            parent[rootV] = rootU;
            size[rootU] += size[rootV];
        }
    }
}

class MakingALargeIsland {
    private final int[] delRow = {-1, 0, 1, 0};
    private final int[] delCol = {0, 1, 0, -1};

    private boolean isValid(int row, int col, int n) {
        return row >= 0 && row < n && col >= 0 && col < n;
    }

    /** Pass 1: union every 1 with its neighbouring 1s, so one island = one root. */
    private void addInitialIslands(int[][] grid, DisjointSet ds, int n) {
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (grid[row][col] == 0) continue;
                for (int dir = 0; dir < 4; dir++) {
                    int newRow = row + delRow[dir];
                    int newCol = col + delCol[dir];
                    if (isValid(newRow, newCol, n) && grid[newRow][newCol] == 1) {
                        ds.unionBySize(row * n + col, newRow * n + newCol);
                    }
                }
            }
        }
    }

    public int largestIsland(int[][] grid) {
        int n = grid.length;
        DisjointSet ds = new DisjointSet(n * n);
        addInitialIslands(grid, ds, n);

        int ans = 0;

        // Pass 2: try flipping each 0 and measure what the merge would produce.
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (grid[row][col] == 1) continue;

                // roots of the distinct islands touching this cell; the Set is
                // what stops one island being counted twice
                Set<Integer> neighbourRoots = new HashSet<>();
                for (int dir = 0; dir < 4; dir++) {
                    int newRow = row + delRow[dir];
                    int newCol = col + delCol[dir];
                    if (isValid(newRow, newCol, n) && grid[newRow][newCol] == 1) {
                        neighbourRoots.add(ds.findUPar(newRow * n + newCol));
                    }
                }

                int merged = 1;   // the flipped cell itself
                for (int root : neighbourRoots) merged += ds.size[root];

                ans = Math.max(ans, merged);
            }
        }

        // If the grid has no 0 at all, pass 2 never ran; the answer is then the
        // biggest island as it already stands.
        for (int cell = 0; cell < n * n; cell++) {
            if (grid[cell / n][cell % n] == 1) {
                ans = Math.max(ans, ds.size[ds.findUPar(cell)]);
            }
        }
        return ans;
    }

    /* ---------- demo ---------- */
    public static void main(String[] args) {
        MakingALargeIsland sol = new MakingALargeIsland();

        // typical: two single cells on a diagonal, one flip joins them
        print("typical 2x2 diagonal", sol.largestIsland(new int[][]{{1, 0}, {0, 1}}), 3);

        // typical: one flip completes the square
        print("one zero left", sol.largestIsland(new int[][]{{1, 1}, {1, 0}}), 4);

        // edge: no zero to flip, answer is the existing island
        print("edge: all land", sol.largestIsland(new int[][]{{1, 1}, {1, 1}}), 4);

        // edge: no land at all, the flipped cell stands alone
        print("edge: all water", sol.largestIsland(new int[][]{{0, 0}, {0, 0}}), 1);

        // tricky: one U-shaped island of 7 borders the middle 0 on both sides,
        // so without the root Set it would be counted twice and report 15
        print("tricky: same island twice",
                sol.largestIsland(new int[][]{{1, 0, 1}, {1, 0, 1}, {1, 1, 1}}), 8);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

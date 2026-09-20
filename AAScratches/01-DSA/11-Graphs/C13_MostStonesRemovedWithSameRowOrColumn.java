/*
 * =====================================================================
 *  Most Stones Removed with Same Row or Column   LeetCode 947 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Stones sit on integer coordinates of a 2D plane, at most one per cell. You
 *   may remove a stone if some OTHER stone still on the plane shares its row or
 *   its column. Removals continue as long as such a stone exists. Return the
 *   largest number of stones that can be removed.
 *
 * EXAMPLE
 *   [[0,0],[0,1],[1,0],[1,2],[2,1],[2,2]]  ->  5   one group, 1 stone survives
 *   [[0,0],[0,2],[1,1],[2,0],[2,2]]        ->  3   two groups, 2 stones survive
 *   [[0,0]]                                ->  0   nothing shares its row/column
 *
 * APPROACH  (rows and columns as DSU nodes)
 *   1. Think of each row index and each column index as a NODE, and each stone
 *      as an edge joining its row node to its column node.
 *   2. Row and column indices overlap (row 2 and column 2 are different nodes),
 *      so shift every column: colNode = col + maxRow + 1. Now every id is unique.
 *   3. Union(rowNode, colNode) for each stone. Stones that share a row or column
 *      land in the same DSU component, and so do stones linked only indirectly.
 *   4. Count the components, but only over nodes that actually carry a stone -
 *      an empty row would otherwise count as a component of its own.
 *   5. Inside a component of any size you can peel stones off one at a time
 *      until exactly one remains, so the answer is totalStones - components.
 *
 * KEY INSIGHT
 *   The removal order never matters: a connected group always collapses to
 *   exactly one survivor, so "maximise removals" is just "count the groups".
 *   The encoding trick is the other half - by making rows and columns the nodes
 *   and stones the edges, transitive links (A shares a row with B, B shares a
 *   column with C) fall out for free instead of needing an n^2 pairwise scan.
 *   Offsetting one id space to avoid collision is the reusable move here.
 *
 * COMPLEXITY
 *   Time  O(n * alpha(n))     one union per stone, near-constant each.
 *   Space O(maxRow + maxCol)  DSU arrays plus the set of occupied nodes.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Prove that a connected component always reduces to exactly one stone.
 *   - Coordinates up to 1e9 but only 1000 stones: compress ids with a HashMap.
 *   - Solve it with DFS over a stone-to-stone graph and compare the cost.
 *   - Which stone survives in each group - can you name one valid final layout?
 *
 * FIXED
 *   main() printed the answer under the label "size of the largest island",
 *   copied from a different problem.
 *
 * RUN
 *   main() runs 3 cases: one full group, two separate groups, a lone stone.
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

    /** Ultimate parent, flattening the chain on the way back up. */
    int findUPar(int node) {
        if (node == parent[node])
            return node;
        return parent[node] = findUPar(parent[node]);
    }

    // Union by rank: hang the shallower tree under the deeper one
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

    // Union by size: hang the smaller set under the larger one
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

    /**
     * @param stones list of {row, col} positions
     * @param n      number of stones (stones.length)
     * @return how many stones can be removed
     */
    public int maxRemove(int[][] stones, int n) {
        int maxRow = 0;
        int maxCol = 0;
        for (int[] stone : stones) {
            maxRow = Math.max(maxRow, stone[0]);
            maxCol = Math.max(maxCol, stone[1]);
        }

        // node ids: rows occupy 0..maxRow, columns occupy maxRow+1..maxRow+maxCol+1
        DisjointSet ds = new DisjointSet(maxRow + maxCol + 2);

        // only nodes that carry a stone may be counted as components later
        Set<Integer> stoneNodes = new HashSet<>();

        for (int[] stone : stones) {
            int nodeRow = stone[0];
            int nodeCol = stone[1] + maxRow + 1;   // shifted so it cannot clash with a row id
            ds.unionBySize(nodeRow, nodeCol);      // the stone IS the edge
            stoneNodes.add(nodeRow);
            stoneNodes.add(nodeCol);
        }

        int components = 0;
        for (int node : stoneNodes) {
            if (ds.findUPar(node) == node) components++;
        }

        // every component keeps exactly one stone behind
        return n - components;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        MostStonesRemovedWithSameRowOrColumn sol = new MostStonesRemovedWithSameRowOrColumn();

        /* Row\Col  0   1   2
               0    1   1   0
               1    1   0   1
               2    0   1   1      -> all six stones link into one component */
        int[][] oneGroup = {
                {0, 0}, {0, 1}, {1, 0},
                {1, 2}, {2, 1}, {2, 2}
        };
        print("case 1 one group  ", sol.maxRemove(oneGroup, oneGroup.length), 5);

        /* {1,1} shares no row or column with any other stone, so it is a
           component of its own and must stay put. */
        int[][] twoGroups = {
                {0, 0}, {0, 2}, {1, 1},
                {2, 0}, {2, 2}
        };
        print("case 2 two groups ", sol.maxRemove(twoGroups, twoGroups.length), 3);

        // edge: a single stone has nothing to pair with
        int[][] lone = {{0, 0}};
        print("case 3 lone stone ", sol.maxRemove(lone, lone.length), 0);
    }
}

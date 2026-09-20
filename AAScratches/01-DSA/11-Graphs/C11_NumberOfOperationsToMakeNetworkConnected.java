/*
 * =====================================================================
 *  Number of Operations to Make Network Connected   LeetCode 1319 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   n computers are numbered 0..n-1 and joined by a list of undirected cables.
 *   In one operation you may unplug any cable and re-plug it between any two
 *   computers. Return the minimum number of operations that leaves every
 *   computer in one single network, or -1 if that is impossible.
 *
 * EXAMPLE
 *   n = 4, edges = [[0,1],[0,2],[1,2]]                 ->  1   move the spare 1-2
 *   n = 6, edges = [[0,1],[0,2],[0,3],[1,2],[1,3]]     ->  2   3 groups, 2 spares
 *   n = 4, edges = [[0,1],[1,2]]                       -> -1   only 2 cables
 *   n = 1, edges = []                                  ->  0   already connected
 *
 * APPROACH  (Disjoint Set Union, then count components)
 *   1. Connecting n computers needs at least n-1 cables. If edges.length < n-1
 *      no amount of rewiring can help, so return -1 immediately.
 *   2. Otherwise you have enough cable. Union the two endpoints of every edge
 *      with a DSU that uses path compression and union by rank.
 *   3. Count the components: a node is the representative of its own component
 *      exactly when parent[i] == i.
 *   4. Joining c components into one takes c-1 cables, and step 1 already proved
 *      that many spare cables exist. Return c - 1.
 *
 * KEY INSIGHT
 *   You never have to work out WHICH cable to move. Once the total cable count
 *   clears the n-1 bar, every redundant edge inside a component is free to
 *   reuse, so the answer collapses to (components - 1). Counting components is
 *   the whole problem, and DSU counts them in one pass without any traversal.
 *   Remember the pairing: "is it connectable" is a cable-count check, "how many
 *   moves" is a component count.
 *
 * COMPLEXITY
 *   Time  O(n + m * alpha(n))  m unions, each near-constant after compression.
 *   Space O(n)                 parent, rank and size arrays.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the actual cables to move, not just how many.
 *   - Cables arrive one at a time: answer after each arrival (DSU streams well).
 *   - Why DSU over BFS/DFS here, and when would BFS be the better answer?
 *   - What changes if cables are directed (strong connectivity, not DSU)?
 *
 * RUN
 *   main() runs 4 cases: typical, multi-component, impossible, single node.
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

    /** Ultimate parent, flattening the chain on the way back up (path compression). */
    int findUPar(int node) {
        if (node == parent[node])
            return node;
        return parent[node] = findUPar(parent[node]);
    }

    // Attach the shallower tree under the deeper one, so depth grows slowly
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
            rank[ulp_u]++;   // only a tie can increase the rank
        }
    }

    // Sibling strategy: attach the smaller set under the larger one
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

class NumberOfOperationsToMakeNetworkConnected {

    public int solve(int n, int[][] edges) {
        int cableCount = edges.length;

        // n nodes need n-1 cables at minimum; fewer than that is hopeless
        if (cableCount < n - 1) return -1;

        DisjointSet ds = new DisjointSet(n);
        for (int[] edge : edges) {
            ds.unionByRank(edge[0], edge[1]);
        }

        // a node that is still its own parent represents one component
        int components = 0;
        for (int i = 0; i < n; i++) {
            if (ds.parent[i] == i) components++;
        }

        // c components are stitched together by c-1 rewirings
        return components - 1;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        NumberOfOperationsToMakeNetworkConnected sol =
                new NumberOfOperationsToMakeNetworkConnected();

        int[][] e1 = {{0, 1}, {0, 2}, {1, 2}};
        print("case 1 one spare cable ", sol.solve(4, e1), 1);

        int[][] e2 = {{0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 3}};
        print("case 2 three groups    ", sol.solve(6, e2), 2);

        // edge: 2 cables cannot ever span 4 computers
        int[][] e3 = {{0, 1}, {1, 2}};
        print("case 3 not enough cable", sol.solve(4, e3), -1);

        // edge: a lone computer is already a connected network
        print("case 4 single computer ", sol.solve(1, new int[0][0]), 0);
    }
}

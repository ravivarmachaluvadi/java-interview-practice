/*
 * =====================================================================
 *  P071 Union-Find (Disjoint Set Union)   Canonical LC 684 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 684, Redundant Connection)
 *   A tree of n nodes (1..n) got one extra edge. Return the edge that can be removed to
 *   leave a tree; if several qualify, the one that appears LAST in the input.
 *
 * EXAMPLE
 *   [[1,2],[1,3],[2,3]]              ->  [2,3]
 *   [[1,2],[2,3],[3,4],[1,4],[1,5]]  ->  [1,4]
 *
 * RECOGNIZE WHEN
 *   - Edges / equalities / "same group" facts arrive one by one and you ask "are x and y
 *     connected?" or "how many groups?" along the way.
 *   - The first edge that closes a cycle; Kruskal's MST; merging accounts by shared emails.
 *   - Equations "a == b" first, then check every "a != b".
 *   Not this if: you need paths or distances -> BFS / DFS (P066_AdjacencyComponents,
 *   P067_BfsShortestPath); edges get DELETED -> DSU cannot split groups.
 *
 * TEMPLATE
 *   parent[i] = i, size[i] = 1
 *   find(x): while parent[x] != x: parent[x] = parent[parent[x]]; x = parent[x]   // compress
 *   union(a, b): ra = find(a), rb = find(b); if ra == rb: return false (already joined)
 *                attach the smaller tree under the larger; return true
 *   components = n - number of successful unions
 *
 * APPROACH
 *   1. Process edges in order, uniting their endpoints.
 *   2. The first edge whose endpoints are ALREADY in the same set closes the cycle: that is
 *      the redundant edge (and it is the last such edge, since only one extra exists).
 *
 * KEY INSIGHT
 *   DSU answers "same group?" in nearly O(1) (inverse Ackermann) while groups only ever
 *   merge. union() returning false is the cycle detector; counting successful unions gives
 *   the number of components for free.
 *
 * COMPLEXITY
 *   O(alpha(n)) per operation with path compression + union by size; space O(n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 1319 Make Network Connected   need n - 1 cables; answer = components - 1
 *   [coded] LC 721  Accounts Merge           union all emails of one account; group by root
 *   [coded] LC 990  Equality Equations       union all "==" first, then every "!=" must
 *                                            have different roots
 *           LC 547  Number of Provinces      components after all unions
 *           LC 947  Most Stones Removed      union stones sharing a row or column;
 *                                            answer = stones - components
 *           LC 1202 Smallest String w/ Swaps union swappable indices, sort each group
 *           LC 2092 Find All People w/ Secret process meetings by time; reset non-secret
 *                                            unions after each time step
 *           LC 1584 / Kruskal MST            sort edges, union if not connected
 *                                            -> P074_MinimumSpanningTree
 *
 * PITFALLS
 *   - Without path compression and union by size, find can degrade to O(n).
 *   - 1-indexed nodes: size the arrays n + 1.
 *   - LC 990: do ALL unions before checking any "!=", or order changes the answer.
 *
 * DEEP DIVE
 *   A05_DisjointSets, C08_NumberOfOperationsToMakeNetworkConnected, C09_AccountsMerge,
 *   C10_MostStonesRemovedWithSameRowOrColumn (11-Graphs)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

class UnionFind {

    static class DSU {
        private final int[] parent;
        private final int[] size;

        DSU(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            while (parent[x] != x) {
                parent[x] = parent[parent[x]];     // path halving
                x = parent[x];
            }
            return x;
        }

        boolean union(int a, int b) {
            int ra = find(a);
            int rb = find(b);
            if (ra == rb) {
                return false;
            }
            if (size[ra] < size[rb]) {
                int t = ra;
                ra = rb;
                rb = t;
            }
            parent[rb] = ra;
            size[ra] += size[rb];
            return true;
        }
    }

    // Canonical LC 684.
    static int[] findRedundantConnection(int[][] edges) {
        DSU dsu = new DSU(edges.length + 1);
        for (int[] e : edges) {
            if (!dsu.union(e[0], e[1])) {
                return e;
            }
        }
        return new int[0];
    }

    // LC 1319.
    static int makeConnected(int n, int[][] connections) {
        if (connections.length < n - 1) {
            return -1;                             // not enough cables in total
        }
        DSU dsu = new DSU(n);
        int components = n;
        for (int[] c : connections) {
            if (dsu.union(c[0], c[1])) {
                components--;
            }
        }
        return components - 1;
    }

    // LC 721: accounts sharing any email belong to one person.
    static List<List<String>> accountsMerge(List<List<String>> accounts) {
        DSU dsu = new DSU(accounts.size());
        Map<String, Integer> owner = new HashMap<>();
        for (int i = 0; i < accounts.size(); i++) {
            for (String email : accounts.get(i).subList(1, accounts.get(i).size())) {
                Integer j = owner.putIfAbsent(email, i);
                if (j != null) {
                    dsu.union(i, j);
                }
            }
        }
        Map<Integer, TreeSet<String>> byRoot = new HashMap<>();
        owner.forEach((email, i) ->
                byRoot.computeIfAbsent(dsu.find(i), k -> new TreeSet<>()).add(email));
        List<List<String>> out = new ArrayList<>();
        byRoot.forEach((root, emails) -> {
            List<String> merged = new ArrayList<>();
            merged.add(accounts.get(root).get(0));
            merged.addAll(emails);
            out.add(merged);
        });
        out.sort((a, b) -> a.toString().compareTo(b.toString()));   // any order is accepted
        return out;
    }

    // LC 990: equations like "a==b" or "a!=b" over lowercase letters.
    static boolean equationsPossible(String[] equations) {
        DSU dsu = new DSU(26);
        for (String e : equations) {
            if (e.charAt(1) == '=') {
                dsu.union(e.charAt(0) - 'a', e.charAt(3) - 'a');
            }
        }
        for (String e : equations) {
            if (e.charAt(1) == '!' && dsu.find(e.charAt(0) - 'a') == dsu.find(e.charAt(3) - 'a')) {
                return false;
            }
        }
        return true;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 684 triangle",
                Arrays.toString(findRedundantConnection(new int[][]{{1, 2}, {1, 3}, {2, 3}})),
                "[2, 3]");
        int[][] fiveEdges = {{1, 2}, {2, 3}, {3, 4}, {1, 4}, {1, 5}};
        check("LC 684 five edges", Arrays.toString(findRedundantConnection(fiveEdges)), "[1, 4]");

        check("LC 1319 one move", makeConnected(4, new int[][]{{0, 1}, {0, 2}, {1, 2}}), 1);
        check("LC 1319 two moves",
                makeConnected(6, new int[][]{{0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 3}}), 2);
        check("LC 1319 too few cables",
                makeConnected(6, new int[][]{{0, 1}, {0, 2}, {0, 3}, {1, 2}}), -1);

        List<List<String>> accounts = List.of(
                List.of("John", "johnsmith@mail.com", "john_newyork@mail.com"),
                List.of("John", "johnsmith@mail.com", "john00@mail.com"),
                List.of("Mary", "mary@mail.com"),
                List.of("John", "johnnybravo@mail.com"));
        check("LC 721 four accounts", accountsMerge(accounts),
                "[[John, john00@mail.com, john_newyork@mail.com, johnsmith@mail.com], "
                        + "[John, johnnybravo@mail.com], [Mary, mary@mail.com]]");

        check("LC 990 a==b b!=a", equationsPossible(new String[]{"a==b", "b!=a"}), false);
        check("LC 990 b==a a==b", equationsPossible(new String[]{"b==a", "a==b"}), true);
        check("LC 990 order trap", equationsPossible(new String[]{"a==b", "b!=c", "c==a"}), false);
        check("LC 990 self and others",
                equationsPossible(new String[]{"c==c", "b==d", "x!=z"}), true);
    }
}

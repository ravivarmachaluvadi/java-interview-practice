/*
 * =====================================================================
 *  P075 Advanced DFS: Bridges, SCC, Euler Path   Canonical LC 1192 | Hard
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 1192, Critical Connections in a Network)
 *   n servers, undirected connections. A connection is critical if removing it disconnects
 *   some servers. Return all critical connections (bridges).
 *
 * EXAMPLE
 *   n = 4, [[0,1],[1,2],[2,0],[1,3]]  ->  [[1,3]]
 *   n = 2, [[0,1]]                    ->  [[0,1]]
 *
 * RECOGNIZE WHEN
 *   - "critical edge / single point of failure / articulation point" -> Tarjan low-link.
 *   - "strongly connected", "mutually reachable groups" in a DIRECTED graph -> Kosaraju or
 *     Tarjan SCC.
 *   - "use every edge exactly once", "itinerary using all tickets", "de Bruijn sequence"
 *     -> Euler path with Hierholzer.
 *   Not this if: plain connectivity -> P066_AdjacencyComponents; an order of dependencies
 *   -> P069_TopologicalSort.
 *
 * TEMPLATE
 *   bridges (Tarjan): disc[u] = low[u] = timer++
 *       for v in adj[u] (skip the parent edge):
 *           if v unvisited: dfs(v); low[u] = min(low[u], low[v])
 *                           if low[v] > disc[u]: (u, v) is a bridge
 *           else: low[u] = min(low[u], disc[v])
 *   Kosaraju: DFS order by finish time; reverse the edges; DFS in reverse finish order;
 *             each DFS tree is one SCC
 *   Hierholzer: dfs(u): while u has unused edges: take one (u -> v), dfs(v); then append u;
 *               reverse the appended list
 *
 * APPROACH
 *   1. DFS assigns discovery times. low[u] = earliest discovery time reachable from u's
 *      subtree using at most one back edge.
 *   2. If a child's low is still later than u's discovery, nothing in the child's subtree
 *      reaches back above it: the edge u-v is the only link, a bridge.
 *
 * KEY INSIGHT
 *   One DFS with two timestamps per node exposes every "only path" edge: a back edge shows
 *   an alternative route, so an edge is a bridge exactly when no back edge from below
 *   jumps over it. Kosaraju and Hierholzer are likewise one or two DFS passes with a
 *   clever ORDER of processing.
 *
 * COMPLEXITY
 *   All three are O(V + E) (Hierholzer O(E log E) when neighbours must be sorted).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] Kosaraju SCC count               two passes; second on the reversed graph
 *   [coded] LC 332  Reconstruct Itinerary    Hierholzer from "JFK", neighbours in lexical
 *                                            order (a min-heap per airport)
 *           Articulation points              low[v] >= disc[u] (root: 2+ DFS children)
 *           LC 753  Cracking the Safe        Euler circuit on (n-1)-digit nodes
 *           LC 2097 Valid Arrangement Pairs  Euler path; start at out - in == 1
 *           Tarjan SCC                       one pass with a stack and low-link
 *
 * PITFALLS
 *   - Bridges: skip the edge to the PARENT, not every visit to the parent node, when
 *     parallel edges exist.
 *   - Hierholzer: append the node AFTER its edges are exhausted, then reverse; greedy
 *     "always take the smallest" without backtracking gets stuck.
 *   - Deep recursion on big graphs: mention an iterative version.
 *
 * DEEP DIVE
 *   A12_BridgesInGraph, A13_ArticulationPointInGraph, A11_KosarajusAlgorithm,
 *   D07_ReconstructItinerary (11-Graphs)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

class BridgesSccEuler {

    private static int timer;

    // Canonical LC 1192.
    static List<List<Integer>> criticalConnections(int n, List<List<Integer>> connections) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (List<Integer> c : connections) {
            adj.get(c.get(0)).add(c.get(1));
            adj.get(c.get(1)).add(c.get(0));
        }
        int[] disc = new int[n];
        int[] low = new int[n];
        java.util.Arrays.fill(disc, -1);
        timer = 0;
        List<List<Integer>> bridges = new ArrayList<>();
        for (int u = 0; u < n; u++) {
            if (disc[u] == -1) {
                lowLink(adj, u, -1, disc, low, bridges);
            }
        }
        for (List<Integer> b : bridges) {
            Collections.sort(b);
        }
        bridges.sort((a, b) -> a.toString().compareTo(b.toString()));
        return bridges;
    }

    private static void lowLink(List<List<Integer>> adj, int u, int parent, int[] disc, int[] low,
                                List<List<Integer>> bridges) {
        disc[u] = low[u] = timer++;
        for (int v : adj.get(u)) {
            if (v == parent) {
                continue;
            }
            if (disc[v] == -1) {
                lowLink(adj, v, u, disc, low, bridges);
                low[u] = Math.min(low[u], low[v]);
                if (low[v] > disc[u]) {
                    bridges.add(new ArrayList<>(List.of(u, v)));
                }
            } else {
                low[u] = Math.min(low[u], disc[v]);   // back edge
            }
        }
    }

    // Kosaraju: number of strongly connected components of a directed graph.
    static int countScc(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        List<List<Integer>> rev = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
            rev.add(new ArrayList<>());
        }
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            rev.get(e[1]).add(e[0]);
        }
        boolean[] seen = new boolean[n];
        List<Integer> finished = new ArrayList<>();
        for (int u = 0; u < n; u++) {
            if (!seen[u]) {
                finishOrder(adj, u, seen, finished);
            }
        }
        seen = new boolean[n];
        int components = 0;
        for (int i = n - 1; i >= 0; i--) {
            int u = finished.get(i);
            if (!seen[u]) {
                components++;
                finishOrder(rev, u, seen, new ArrayList<>());
            }
        }
        return components;
    }

    private static void finishOrder(List<List<Integer>> g, int u, boolean[] seen,
                                    List<Integer> out) {
        seen[u] = true;
        for (int v : g.get(u)) {
            if (!seen[v]) {
                finishOrder(g, v, seen, out);
            }
        }
        out.add(u);
    }

    // LC 332: use every ticket once, starting at JFK, lexically smallest itinerary.
    static List<String> findItinerary(List<List<String>> tickets) {
        Map<String, PriorityQueue<String>> next = new HashMap<>();
        for (List<String> t : tickets) {
            next.computeIfAbsent(t.get(0), k -> new PriorityQueue<>()).add(t.get(1));
        }
        LinkedList<String> route = new LinkedList<>();
        visit("JFK", next, route);
        return route;
    }

    private static void visit(String airport, Map<String, PriorityQueue<String>> next,
                              LinkedList<String> route) {
        PriorityQueue<String> out = next.get(airport);
        while (out != null && !out.isEmpty()) {
            visit(out.poll(), next, route);
        }
        route.addFirst(airport);                   // added after its tickets are used up
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        List<List<Integer>> tailed =
                List.of(List.of(0, 1), List.of(1, 2), List.of(2, 0), List.of(1, 3));
        check("LC 1192 triangle plus a tail", criticalConnections(4, tailed), "[[1, 3]]");
        check("LC 1192 single edge", criticalConnections(2, List.of(List.of(0, 1))), "[[0, 1]]");
        check("LC 1192 chain, every edge critical",
                criticalConnections(3, List.of(List.of(0, 1), List.of(1, 2))), "[[0, 1], [1, 2]]");

        check("Kosaraju three SCCs",
                countScc(5, new int[][]{{1, 0}, {0, 2}, {2, 1}, {0, 3}, {3, 4}}), 3);
        check("Kosaraju one big cycle", countScc(3, new int[][]{{0, 1}, {1, 2}, {2, 0}}), 1);

        check("LC 332 four tickets",
                findItinerary(List.of(List.of("MUC", "LHR"), List.of("JFK", "MUC"),
                List.of("SFO", "SJC"), List.of("LHR", "SFO"))), "[JFK, MUC, LHR, SFO, SJC]");
        check("LC 332 greedy dead end avoided", findItinerary(List.of(List.of("JFK", "KUL"),
                List.of("JFK", "NRT"), List.of("NRT", "JFK"))), "[JFK, NRT, JFK, KUL]");
        check("LC 332 five tickets",
                findItinerary(List.of(List.of("JFK", "SFO"), List.of("JFK", "ATL"),
                List.of("SFO", "ATL"), List.of("ATL", "JFK"), List.of("ATL", "SFO"))),
                "[JFK, ATL, JFK, SFO, ATL, SFO]");
    }
}

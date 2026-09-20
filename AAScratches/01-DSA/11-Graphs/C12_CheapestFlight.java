/*
 * =====================================================================
 *  Cheapest Flights Within K Stops          LeetCode 787 | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   n cities are joined by directed flights[i] = {from, to, price}. Travel from
 *   src to dst using at most k intermediate stops and return the cheapest total
 *   price, or -1 if no such route exists. src and dst themselves are not stops,
 *   so an allowed route uses at most k + 1 flights.
 *
 * EXAMPLE
 *   n=4, flights={{0,1,100},{1,2,100},{2,0,100},{1,3,600},{2,3,200}}
 *   src=0, dst=3, k=1  ->  700   0->1->3 costs 700; 0->1->2->3 costs 400 but
 *                                uses 2 stops, which is over the limit
 *   n=3, flights={{0,1,100},{1,2,100},{0,2,500}}, src=0, dst=2, k=0  ->  500
 *
 * APPROACH  (shortest path with a second constraint dimension)
 *   Two methods are shown; main() runs both on every case.
 *
 *   A. byCostWithStopGuard - a price-ordered priority queue.
 *      1. Push {src, cost 0, flightsTaken 0} and pop the cheapest entry.
 *      2. On popping dst, that price is the answer: everything still queued
 *         costs at least as much and every queued entry already obeys k.
 *      3. Otherwise drop the entry if it has already used more than k stops,
 *         or if this node was reached earlier with fewer flights; bestStops[]
 *         is the pruning table instead of the usual dist[].
 *
 *   B. byStopRounds - relax every edge k + 1 times (Bellman-Ford shaped).
 *      1. cost[] holds the cheapest price using at most r flights.
 *      2. Each round reads a frozen copy of cost[] so one round cannot chain
 *         two flights together; after round r the values are exact for r hops.
 *
 * KEY INSIGHT
 *   Plain Dijkstra is unsafe here: it finalises a city the first time it is
 *   popped, but the cheapest way into a city may use too many flights while a
 *   pricier way stays inside the budget. The fix is to make stops part of the
 *   state. Order by stops (B) and cost never has to be revisited; order by
 *   cost (A) and you must keep the best stop count seen per node. Recognise
 *   this whenever a shortest-path problem adds a budget, a fuel cap or a
 *   hop limit.
 *
 * COMPLEXITY
 *   A: Time  O(E * k * log(E * k))  each node can be queued once per stop count
 *      Space O(N + E * k)           queue entries plus the adjacency list
 *   B: Time  O(k * E)               k + 1 rounds over every edge
 *      Space O(N)                   two cost arrays
 *
 * INTERVIEW FOLLOW-UPS
 *   - Rebuild the actual itinerary, not just its price.
 *   - What if edges could be negative? (B still works, A does not.)
 *   - Cap total travel time as well as stops - how does the state grow?
 *   - Many queries on one graph: what can be precomputed?
 *
 * RUN
 *   main() runs 5 cases (typical, k=0 boundary, src==dst, unreachable,
 *   cheap-but-too-many-hops trap) and prints actual vs expected for both
 *   methods.
 */

import java.util.*;

class CheapestFlight {

    /* ---------- A. price-ordered search, stop count used for pruning ---------- */
    public int byCostWithStopGuard(int n, int[][] flights, int src, int dst, int k) {
        if (src == dst) return 0;

        List<List<int[]>> adjList = buildAdjacency(n, flights);

        // entry = {node, costTaken, flightsTaken}; cheapest price pops first
        PriorityQueue<int[]> queue = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        queue.add(new int[]{src, 0, 0});

        // fewest flights we have ever used to stand on this node
        int[] bestStops = new int[n];
        Arrays.fill(bestStops, Integer.MAX_VALUE);

        while (!queue.isEmpty()) {
            int[] polled = queue.poll();
            int node = polled[0];
            int costTaken = polled[1];
            int flightsTaken = polled[2];

            // first pop of dst is the cheapest legal route: the queue is price
            // ordered and nothing was ever enqueued past the stop budget
            if (node == dst) return costTaken;

            // budget spent: standing on an intermediate node after f flights
            // means f stops have been used, so f must not exceed k
            if (flightsTaken > k) continue;

            // an earlier pop was both cheaper (price order) and no longer in
            // hops, so it dominates this entry
            if (flightsTaken > bestStops[node]) continue;
            bestStops[node] = flightsTaken;

            for (int[] next : adjList.get(node)) {
                queue.add(new int[]{next[0], costTaken + next[1], flightsTaken + 1});
            }
        }
        return -1;
    }

    /* ---------- B. round by round on the number of flights allowed ---------- */
    public int byStopRounds(int n, int[][] flights, int src, int dst, int k) {
        final int INF = Integer.MAX_VALUE / 2;   // halved so INF + price cannot overflow

        int[] cost = new int[n];
        Arrays.fill(cost, INF);
        cost[src] = 0;

        // round r makes routes of at most r flights exact; k stops = k + 1 flights
        for (int round = 0; round <= k; round++) {
            // snapshot: relaxing against the live array would let one round
            // chain several flights and silently exceed the stop budget
            int[] previous = cost.clone();
            for (int[] flight : flights) {
                int from = flight[0], to = flight[1], price = flight[2];
                if (previous[from] + price < cost[to]) {
                    cost[to] = previous[from] + price;
                }
            }
        }
        return cost[dst] >= INF ? -1 : cost[dst];
    }

    private List<List<int[]>> buildAdjacency(int n, int[][] flights) {
        List<List<int[]>> adjList = new ArrayList<>();
        for (int i = 0; i < n; i++) adjList.add(new ArrayList<>());
        for (int[] flight : flights) {
            adjList.get(flight[0]).add(new int[]{flight[1], flight[2]});
        }
        return adjList;
    }

    /* ---------- demo ---------- */
    public static void main(String[] args) {
        CheapestFlight sol = new CheapestFlight();

        int[][] four = {{0, 1, 100}, {1, 2, 100}, {2, 0, 100}, {1, 3, 600}, {2, 3, 200}};
        int[][] three = {{0, 1, 100}, {1, 2, 100}, {0, 2, 500}};

        // typical: the 400 route exists but needs 2 stops, so 700 wins
        check(sol, "typical, k=1", 4, four, 0, 3, 1, 700);

        // same graph, one more stop allowed: the cheap chain becomes legal
        check(sol, "same graph, k=2", 4, four, 0, 3, 2, 400);

        // boundary: k=0 forbids every intermediate city, only the direct hop counts
        check(sol, "k=0 direct only", 3, three, 0, 2, 0, 500);

        // edge: already at the destination
        check(sol, "src == dst", 3, three, 1, 1, 5, 0);

        // edge: no route at all
        check(sol, "unreachable", 2, new int[][]{{1, 0, 50}}, 0, 1, 3, -1);
    }

    private static void check(CheapestFlight sol, String label, int n, int[][] flights,
                              int src, int dst, int k, int expected) {
        int a = sol.byCostWithStopGuard(n, flights, src, dst, k);
        int b = sol.byStopRounds(n, flights, src, dst, k);
        System.out.println(label + ": byCost = " + a + ", byStops = " + b
                + "   expected " + expected + " for both");
    }
}

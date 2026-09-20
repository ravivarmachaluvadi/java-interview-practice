/*
 * =====================================================================
 *  Number of Ways to Arrive at Destination      LeetCode 1976 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   n intersections 0..n-1 are joined by bidirectional roads, each given as
 *   {u, v, time} with a strictly positive travel time. Count how many distinct
 *   routes get you from intersection 0 to intersection n-1 in the shortest
 *   possible total time. The count can be huge, so return it modulo 1e9 + 7.
 *
 * EXAMPLE
 *   n=7, roads = {0-6:7, 0-1:2, 1-2:3, 1-3:3, 6-3:3, 3-5:1, 6-5:1,
 *                 2-5:1, 0-4:5, 4-6:2}                          ->  4
 *     the shortest time to 6 is 7, reached by the direct road and by three
 *     routes through 1 and 5
 *   n=4, roads = {0-1:1, 0-2:1, 1-3:1, 2-3:1}                    ->  2
 *     the two symmetric routes both take time 2
 *
 * APPROACH  (Dijkstra carrying a path count alongside the distance)
 *   1. Keep two arrays: minTime[v], the best time to reach v, and ways[v], how
 *      many shortest routes achieve that time. Start minTime[0] = 0, ways[0] = 1.
 *   2. Run Dijkstra with a min-heap keyed on time. Skip any popped entry whose
 *      time is worse than minTime for that node - it is a stale duplicate.
 *   3. Relaxing edge node -> adjNode with weight w, there are exactly two
 *      interesting outcomes:
 *        strictly better (time + w < minTime[adjNode]) - the old routes are
 *          obsolete, so OVERWRITE: ways[adjNode] = ways[node], and push.
 *        exactly equal (time + w == minTime[adjNode]) - a second family of
 *          shortest routes, so ADD: ways[adjNode] += ways[node]. Do NOT push;
 *          the distance did not change and re-queueing would double count.
 *   4. Answer is ways[n-1].
 *
 * KEY INSIGHT
 *   The counts are correct only because weights are strictly positive. If
 *   minTime[v] = minTime[u] + w with w > 0 then minTime[u] < minTime[v], so u
 *   is always popped and finalised before v. That guarantees ways[u] is already
 *   final at the moment it is copied or added into ways[v]. Reset on better,
 *   accumulate on equal - that single rule is the whole template, and it
 *   transfers to "count the shortest paths / cheapest schedules" variants.
 *
 * COMPLEXITY
 *   Time  O(E log V)   standard Dijkstra; counting adds O(1) per relaxation
 *   Space O(V + E)     adjacency list, minTime, ways and the heap
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why must the equal case not push onto the heap?
 *   - What breaks if a road could have weight 0, or a negative weight?
 *   - Return the longest of the shortest routes by edge count as a tie-break.
 *   - Count the second-shortest paths instead of the shortest ones.
 *
 * RUN
 *   main() runs 4 cases (the LeetCode example, two symmetric routes, a single
 *   road, and the trivial one-node edge case) and prints actual vs expected.
 */

import java.util.*;

class NumberOfWaysToArriveAtDestination {

    private static final long MOD = 1_000_000_007L;

    public int countPaths(int n, List<List<Integer>> roads) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());

        // roads are bidirectional, so each one is stored twice
        for (List<Integer> road : roads) {
            int u = road.get(0), v = road.get(1), time = road.get(2);
            adj.get(u).add(new int[]{v, time});
            adj.get(v).add(new int[]{u, time});
        }

        long[] minTime = new long[n];
        Arrays.fill(minTime, Long.MAX_VALUE);
        long[] ways = new long[n];

        minTime[0] = 0;
        ways[0] = 1;   // one way to stand at the start: do nothing

        // heap entries are {timeSoFar, node}, cheapest time first
        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));
        pq.add(new long[]{0, 0});

        while (!pq.isEmpty()) {
            long[] polled = pq.poll();
            long time = polled[0];
            int node = (int) polled[1];

            // stale entry left over from an earlier, worse relaxation
            if (time > minTime[node]) continue;

            for (int[] edge : adj.get(node)) {
                int adjNode = edge[0];
                long arrival = time + edge[1];

                if (arrival < minTime[adjNode]) {
                    // strictly better: every previously counted route is obsolete
                    minTime[adjNode] = arrival;
                    ways[adjNode] = ways[node];
                    pq.add(new long[]{arrival, adjNode});
                } else if (arrival == minTime[adjNode]) {
                    // another family of equally short routes joins the count.
                    // No push: the distance is unchanged, and re-queueing this
                    // node would let the same routes be counted twice.
                    ways[adjNode] = (ways[adjNode] + ways[node]) % MOD;
                }
            }
        }
        return (int) (ways[n - 1] % MOD);
    }

    /* ---------- demo ---------- */
    public static void main(String[] args) {
        NumberOfWaysToArriveAtDestination sol = new NumberOfWaysToArriveAtDestination();

        // typical: the LeetCode sample, four distinct 7-minute routes to node 6
        List<List<Integer>> seven = roads(
                new int[][]{{0, 6, 7}, {0, 1, 2}, {1, 2, 3}, {1, 3, 3}, {6, 3, 3},
                        {3, 5, 1}, {6, 5, 1}, {2, 5, 1}, {0, 4, 5}, {4, 6, 2}});
        print("typical n=7", sol.countPaths(7, seven), 4);

        // tricky: a perfect diamond, two routes tie at time 2
        List<List<Integer>> diamond = roads(
                new int[][]{{0, 1, 1}, {0, 2, 1}, {1, 3, 1}, {2, 3, 1}});
        print("diamond, tie", sol.countPaths(4, diamond), 2);

        // tricky: a detour exists but is slower, so only one route is shortest
        List<List<Integer>> detour = roads(
                new int[][]{{0, 1, 1}, {1, 2, 1}, {0, 2, 3}});
        print("slower detour ignored", sol.countPaths(3, detour), 1);

        // edge: a single intersection, start and destination are the same node
        print("edge: n=1", sol.countPaths(1, new ArrayList<>()), 1);
    }

    /** Turn a compact int[][] of {u, v, time} into the List form the method takes. */
    private static List<List<Integer>> roads(int[][] raw) {
        List<List<Integer>> out = new ArrayList<>();
        for (int[] r : raw) out.add(Arrays.asList(r[0], r[1], r[2]));
        return out;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

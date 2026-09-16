import java.util.*;

// https://leetcode.com/problems/number-of-ways-to-arrive-at-destination/description/
// roads where roads[i] = [ui, vi, timei]

/**
 * You want to know in how many ways you can travel from
 * <p>
 * intersection 0 to intersection n - 1 in the shortest amount of time.
 * <p>
 * Return the number of ways you can arrive at your destination in the
 * <p>
 * shortest amount of time. Since the answer may be large, return it modulo 109 + 7
 */
class NumberOfWaysToArriveAtDestination {

    /* Function to get the number of ways to arrive
    at destinations in the shortest possible time */
    public int countPaths(int n, List<List<Integer>> roads) {

        long mod = 1000_000_007;

        // List<int[]>
        List<int[]>[] adj = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
        }

        // building graph from edges
        for (List<Integer> it : roads) {
            adj[it.get(0)].add(new int[]{it.get(1), it.get(2)});
            adj[it.get(1)].add(new int[]{it.get(0), it.get(2)});
        }

        long[] minTime = new long[n];
        Arrays.fill(minTime, Long.MAX_VALUE);

        long[] ways = new long[n];
        // Priority queue to store: {time, node}
        // amount of time taken to reach node
        PriorityQueue<long[]> pq =
                new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));

        // Initial configuration
        minTime[0] = 0;
        ways[0] = 1;
        pq.add(new long[]{0, 0});

        // Until the priority queue is empty
        while (!pq.isEmpty()) {
            long[] p = pq.poll();
            long time = p[0];
            int node = (int) p[1];

            if (time > minTime[node]) continue;

            for (int[] it : adj[node]) {
                int adjNode = it[0];
                int travelTime = it[1];

                if (minTime[adjNode] > time + travelTime) {
                    // here we are updaing ways and minTime both
                    minTime[adjNode] = time + travelTime;
                    ways[adjNode] = ways[node];
                    pq.add(new long[]{minTime[adjNode], adjNode});
                } else if (minTime[adjNode] == time + travelTime) {
                    ways[adjNode] = (ways[adjNode] + ways[node]) % mod;
                }
            }
        }
        return (int) (ways[n - 1] % mod);
    }

    public static void main(String[] args) {
        int n = 7, m = 20;
        List<List<Integer>> roads = Arrays.asList(
                Arrays.asList(0, 6, 7),
                Arrays.asList(0, 1, 2),
                Arrays.asList(1, 2, 3),
                Arrays.asList(1, 3, 3),
                Arrays.asList(6, 3, 3),
                Arrays.asList(3, 5, 1),
                Arrays.asList(6, 5, 1),
                Arrays.asList(2, 5, 1),
                Arrays.asList(0, 4, 5),
                Arrays.asList(4, 6, 2)
        );
        NumberOfWaysToArriveAtDestination sol = new NumberOfWaysToArriveAtDestination();
        int ans = sol.countPaths(n, roads);// 4
        System.out.println("The number of ways to arrive at destinations in shortest possible time is: " + ans);
    }
}

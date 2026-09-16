import java.util.*;

class CheapestFlight {

    // src and destination not included in stops
    public int cheapestFlight(int n, int[][] flights, int src, int dst, int k) {

        if (src == dst) return 0;

        List<List<int[]>> adjList = new ArrayList<>();

        for (int i = 0; i < n; i++) adjList.add(new ArrayList<>());

        for (int[] flight : flights)
            adjList.get(flight[0]).add(new int[]{flight[1], flight[2]});

        PriorityQueue<int[]> queue =
                new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));

        // {node,costTaken,stopsTaken}
        queue.add(new int[]{src, 0, 0});
        int[] bestStops = new int[n];
        Arrays.fill(bestStops, Integer.MAX_VALUE);

        while (!queue.isEmpty()) {
            int[] polled = queue.poll();
            int node = polled[0];
            int costTaken = polled[1];
            int stopsTaken = polled[2];

            if (node == dst) return costTaken;
            if (stopsTaken > k || stopsTaken > bestStops[node]) continue;
            bestStops[node] = stopsTaken;
            for (int[] next : adjList.get(node)) {
                queue.add(new int[]{next[0], costTaken + next[1], stopsTaken + 1});
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int n = 4;
        int[][] flights = {
                {0, 1, 100},
                {1, 2, 100},
                {2, 0, 100},
                {1, 3, 600},
                {2, 3, 200}
        };
        int src = 0, dst = 3, k = 1;
        CheapestFlight sol = new CheapestFlight();
        int ans = sol.cheapestFlight(n, flights, src, dst, k);

        System.out.println("The cheapest flight from source to destination within K stops is: " + ans);
    }
}

/*
 * =====================================================================
 *  Shortest Path in a Weighted Undirected Graph     GFG | Medium
 * =====================================================================
 *
 * PROBLEM
 *   An undirected graph has nodes 1..n and m weighted edges {u, v, w} with
 *   w >= 0. Return the cheapest route from node 1 to node n as a list whose
 *   first element is the total weight and whose remaining elements are the
 *   nodes visited in order. Return [-1] when node n cannot be reached.
 *
 * EXAMPLE
 *   n = 5, edges = [[1,2,2],[2,5,5],[2,3,4],[1,4,1],[4,3,3],[3,5,1]]
 *     -> [5, 1, 4, 3, 5]     1->4 costs 1, 4->3 costs 3, 3->5 costs 1
 *   n = 3, edges = [[1,2,1]]        -> [-1]       node 3 is isolated
 *   n = 1, edges = []               -> [0, 1]     already at the destination
 *
 * APPROACH  (Dijkstra plus a parent array for reconstruction)
 *   1. Build adjacency lists, adding each undirected edge in both directions.
 *   2. dist[] starts at infinity except dist[1] = 0. parent[i] starts at i, so
 *      "parent[x] == x" means "x is the start, or x was never reached".
 *   3. Pop the closest unsettled node from a min-heap keyed by distance. Skip it
 *      if the popped distance is worse than dist[node] - that entry is stale.
 *   4. Relax each neighbour. Whenever dist improves, ALSO set parent[neighbour]
 *      to the node you came from, then push the neighbour with its new distance.
 *   5. If dist[n] is still infinity, return [-1]. Otherwise walk parent[] back
 *      from n until parent[node] == node, reverse it, and put dist[n] in front.
 *
 * KEY INSIGHT
 *   The path costs nothing extra to recover: one int per node, written at the
 *   exact moment a shorter route is found, is enough to replay the whole route
 *   backwards. Because Dijkstra only ever overwrites dist[v] with a better
 *   value, parent[v] always names the predecessor on the BEST route known, so
 *   at the end the parent chain is a shortest-path tree rooted at the source.
 *   Reach for this parent-array habit for "print the path", "count the routes"
 *   or "reconstruct the sequence" follow-ups on any BFS or Dijkstra.
 *
 * COMPLEXITY
 *   Time  O((n + m) log n)  every edge can push once; each heap op is log n.
 *   Space O(n + m)          adjacency lists, dist, parent and the heap.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Count how many distinct shortest paths exist (carry a ways[] array).
 *   - One negative edge appears: why does Dijkstra break, and what replaces it?
 *   - Return the shortest path from 1 to every node, not just to n.
 *   - Break ties towards the path with the fewest edges.
 *
 * RUN
 *   main() runs 3 cases: typical graph, unreachable target, single node.
 */

import java.util.*;

class ShortestPath {

    public List<Integer> shortestPath(int n, int m, int[][] edges) {
        // index 0 is unused so node ids and array slots line up
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adj.get(edge[0]).add(new int[]{edge[1], edge[2]});
            adj.get(edge[1]).add(new int[]{edge[0], edge[2]});   // undirected
        }

        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);

        /* parent[i] == i marks "no predecessor yet", which is true for the
           source and stays true for anything we never reach. */
        int[] parent = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }
        dist[1] = 0;

        // heap entries are {distanceSoFar, node}, ordered by distance
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.add(new int[]{0, 1});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int dis = curr[0];
            int node = curr[1];

            // a better route to this node was found after this entry was pushed
            if (dis > dist[node]) continue;

            for (int[] neighbour : adj.get(node)) {
                int adjNode = neighbour[0];
                int edgeWeight = neighbour[1];
                if (dis + edgeWeight < dist[adjNode]) {
                    dist[adjNode] = dis + edgeWeight;
                    // remember who gave us this better route
                    parent[adjNode] = node;
                    pq.add(new int[]{dist[adjNode], adjNode});
                }
            }
        }

        if (dist[n] == Integer.MAX_VALUE) {
            return Arrays.asList(-1);   // destination was never reached
        }

        // walk the parent chain backwards from the destination to the source
        List<Integer> path = new ArrayList<>();
        int node = n;
        while (parent[node] != node) {
            path.add(node);
            node = parent[node];
        }
        path.add(1);
        Collections.reverse(path);

        path.add(0, dist[n]);   // total weight goes in front, as the problem asks
        return path;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        ShortestPath sol = new ShortestPath();

        int[][] edges = {
                {1, 2, 2}, {2, 5, 5}, {2, 3, 4},
                {1, 4, 1}, {4, 3, 3}, {3, 5, 1}
        };
        // 1->2->5 costs 7, but 1->4->3->5 costs only 5
        print("case 1 typical     ", sol.shortestPath(5, edges.length, edges), "[5, 1, 4, 3, 5]");

        // edge: node 3 has no incident edge at all
        int[][] broken = {{1, 2, 1}};
        print("case 2 unreachable ", sol.shortestPath(3, broken.length, broken), "[-1]");

        // edge: source and destination are the same node
        print("case 3 single node ", sol.shortestPath(1, 0, new int[0][0]), "[0, 1]");
    }
}

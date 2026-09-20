/*
 * =====================================================================
 *  Dijkstra's Shortest Path           GFG / LC 743 style | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a weighted graph with NON-NEGATIVE edge weights and a source vertex,
 *   return the shortest distance from the source to every vertex. Vertices the
 *   source cannot reach keep distance "infinity" (Integer.MAX_VALUE here).
 *   Negative weights are out of scope - use Bellman-Ford (A07_BellmanFord).
 *
 * EXAMPLE
 *   Undirected, V=5: 0-1 (9), 0-2 (6), 0-3 (5), 0-4 (3), 2-1 (2), 2-3 (4)
 *   source 0  ->  [0, 8, 6, 5, 3]
 *                  vertex 1 is 8, not the direct 9, because 0 -> 2 -> 1 = 6 + 2
 *   V=4 with edges 0-1 (4) and 2-3 (1), source 0  ->  [0, 4, INF, INF]
 *   V=1 with no edges, source 0                   ->  [0]
 *
 * APPROACH  (BFS with a min-heap instead of a plain queue)
 *   1. dist[] starts at infinity, dist[src] = 0. Push (src, 0).
 *   2. Repeatedly take the UNSETTLED vertex with the smallest tentative
 *      distance. With non-negative weights, that distance can never improve
 *      again, so the vertex is settled the moment it is polled.
 *   3. Relax every outgoing edge: if dist[u] + w < dist[v], write the new
 *      distance and push v back into the heap.
 *   4. Stop when the heap drains. Two ways to keep the heap honest:
 *      - dijkstraWithPQ:      LAZY deletion. Push a new entry and leave the
 *        stale one behind; skip it on poll when its key exceeds dist[u].
 *      - dijkstraWithTreeSet: EAGER deletion. Remove the old (dist, vertex)
 *        entry before inserting the new one, so the set always holds exactly
 *        one entry per reachable-but-unsettled vertex.
 *
 * KEY INSIGHT
 *   Dijkstra is BFS where the queue is replaced by a min-heap keyed on distance
 *   so far. That swap is the whole algorithm. It is correct only because edge
 *   weights are non-negative: extending a path can never make it shorter, so
 *   the cheapest thing in the heap is already final. Introduce one negative
 *   edge and that guarantee - and therefore Dijkstra - collapses.
 *   The stale-entry skip (current.weight > distances[u]) is what lets you avoid
 *   a decrease-key operation, and it is the line interviewers look for.
 *
 * COMPLEXITY
 *   Time  O((V + E) log V)  each edge can push at most one heap entry, and each
 *         heap operation is log of the heap size.
 *   Space O(V + E)  adjacency list, plus up to O(E) entries in the lazy PQ
 *         (the TreeSet version is bounded at O(V) entries instead).
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the path, not just the cost: keep a parent[] and walk it back
 *     (C11_ShortestPath).
 *   - Count the number of shortest paths: carry ways[] next to dist[]
 *     (C14_NumberOfWaysToArriveAtDestination).
 *   - Add a constraint dimension, e.g. at most K stops (LC 787,
 *     C12_CheapestFlight) - plain Dijkstra breaks, you order by stops instead.
 *   - All edges weigh 0 or 1: skip the heap, use 0-1 BFS with a deque
 *     (C15_GridTeleportationTraversal).
 *   - Negative edges or a negative cycle: Bellman-Ford (A07_BellmanFord) or
 *     Floyd-Warshall (A08_FloydWarshallAlgorithm).
 *
 * RUN
 *   main() runs 3 cases (the 5-vertex graph above, a graph with an unreachable
 *   component, and a single isolated vertex) through BOTH implementations and
 *   prints actual vs expected.
 */
import java.util.*;

class DjikstrasAlgoPQ {
    private final int vertices;
    private final List<List<Node>> adjList;

    /**
     * Holds (vertex, weight). In the adjacency list "weight" is the edge weight;
     * inside the PQ / TreeSet it is the tentative distance from the source.
     */
    static class Node implements Comparable<Node> {
        int vertex;
        int weight;

        Node(int vertex, int weight) {
            this.vertex = vertex;
            this.weight = weight;
        }

        // Ascending by weight. Tie-break on vertex: TreeSet uses compareTo for EQUALITY,
        // so without the tie-break two different vertices at the same distance would be
        // treated as the same element and one of them silently dropped.
        // (The PQ does not need the tie-break, but it does no harm there.)
        @Override
        public int compareTo(Node b) {
            if (this.weight != b.weight) return Integer.compare(this.weight, b.weight);
            return Integer.compare(this.vertex, b.vertex);
        }
    }

    DjikstrasAlgoPQ(int vertices) {
        this.vertices = vertices;
        adjList = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) adjList.add(new ArrayList<>());
    }

    void addEdge(int u, int v, int wt) {
        adjList.get(u).add(new Node(v, wt));
        adjList.get(v).add(new Node(u, wt)); // undirected graph: store both directions
    }

    // ---------------------------------------------------------------------------------------
    // Approach 1: PriorityQueue with lazy deletion
    // ---------------------------------------------------------------------------------------
    int[] dijkstraWithPQ(int src) {
        int[] distances = new int[vertices];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[src] = 0;

        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(src, 0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int u = current.vertex;

            // Stale entry: a shorter distance for u was already found and processed.
            // Skipping it is what makes "lazy deletion" correct and keeps the loop cheap.
            if (current.weight > distances[u]) continue;

            for (Node edge : adjList.get(u)) {
                // distances[u] = cost already paid to reach u
                // edge.weight  = cost still to pay from u to edge.vertex
                int newDist = distances[u] + edge.weight;
                if (newDist < distances[edge.vertex]) {
                    distances[edge.vertex] = newDist;
                    pq.add(new Node(edge.vertex, newDist)); // old entry stays in the PQ (lazy)
                }
            }
        }
        return distances;
    }

    // ---------------------------------------------------------------------------------------
    // Approach 2: TreeSet with eager remove-then-reinsert
    // ---------------------------------------------------------------------------------------
    int[] dijkstraWithTreeSet(int src) {
        int[] dist = new int[vertices];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        // Ordered set of (distance, vertex); pollFirst() gives the closest unsettled vertex.
        TreeSet<Node> set = new TreeSet<>();
        set.add(new Node(src, 0));

        while (!set.isEmpty()) {
            Node uNode = set.pollFirst();
            int u = uNode.vertex;

            for (Node edge : adjList.get(u)) {
                int v = edge.vertex;
                int newDist = dist[u] + edge.weight;

                if (newDist < dist[v]) {
                    // Remove the old (dist[v], v) entry if present. remove() relies on compareTo,
                    // which is why Node must compare on both weight and vertex.
                    // If dist[v] is still infinity there is no such entry and this is a no-op.
                    set.remove(new Node(v, dist[v]));
                    dist[v] = newDist;
                    set.add(new Node(v, newDist));
                }
            }
        }
        return dist;
    }

    // ------------------------------------------------------------------ demo helpers

    /** Renders unreachable vertices as INF instead of 2147483647. */
    static String format(int[] distances) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < distances.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(distances[i] == Integer.MAX_VALUE ? "INF" : distances[i]);
        }
        return sb.append("]").toString();
    }

    private static void check(String label, DjikstrasAlgoPQ graph, int src, String expected) {
        String byPQ = format(graph.dijkstraWithPQ(src));
        String byTreeSet = format(graph.dijkstraWithTreeSet(src));
        System.out.println(label + " PQ " + byPQ + ", TreeSet " + byTreeSet
                + "   expected " + expected + " for both");
    }

    public static void main(String[] args) {
        // case 1: typical - the cheapest route to vertex 1 goes through vertex 2,
        // so the direct edge of weight 9 must be beaten by 6 + 2 = 8
        DjikstrasAlgoPQ graph = new DjikstrasAlgoPQ(5);
        graph.addEdge(0, 1, 9);
        graph.addEdge(0, 2, 6);
        graph.addEdge(0, 3, 5);
        graph.addEdge(0, 4, 3);
        graph.addEdge(2, 1, 2);
        graph.addEdge(2, 3, 4);
        check("case 1 five vertices, src 0 :", graph, 0, "[0, 8, 6, 5, 3]");

        // case 2: edge case - vertices 2 and 3 form a component the source cannot reach
        DjikstrasAlgoPQ split = new DjikstrasAlgoPQ(4);
        split.addEdge(0, 1, 4);
        split.addEdge(2, 3, 1);
        check("case 2 unreachable half, src 0:", split, 0, "[0, 4, INF, INF]");

        // case 3: edge case - a single vertex with no edges at all
        check("case 3 single vertex, src 0 :", new DjikstrasAlgoPQ(1), 0, "[0]");
    }
}

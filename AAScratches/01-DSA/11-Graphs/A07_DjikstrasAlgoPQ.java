import java.util.*;

/**
 * Problem: Single-source shortest path on a weighted graph with non-negative edges (Dijkstra).
 * Approaches:
 *   1. dijkstraWithPQ      - PriorityQueue, "lazy" deletion: on relaxation push a new entry, never
 *                            remove the stale one; stale entries are skipped when polled.
 *   2. dijkstraWithTreeSet - TreeSet, "eager" deletion: on relaxation remove the old (dist, vertex)
 *                            entry and insert the new one, so the set always holds exactly one
 *                            entry per reachable-but-unsettled vertex.
 *
 * Both are O((V + E) log V). PQ is the usual interview default (simpler, no remove).
 * TreeSet is what you reach for when you must be able to update/delete an entry in O(log V).
 *
 * Expected output for the undirected graph in main (source 0):
 *   Vertex   Distance from Source
 *   0        0
 *   1        8      (0 -> 2 -> 1 : 6 + 2)
 *   2        6
 *   3        5
 *   4        3
 */
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
        for (int i = 0; i < vertices; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    void addEdge(int u, int v, int wt) {
        adjList.get(u).add(new Node(v, wt));
        adjList.get(v).add(new Node(u, wt)); // undirected graph
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
                // distances[u] = distance covered from source to u
                // edge.weight  = distance still to cover from u to edge.vertex
                int newDist = distances[u] + edge.weight;
                if (newDist < distances[edge.vertex]) {
                    distances[edge.vertex] = newDist;
                    pq.add(new Node(edge.vertex, newDist)); // old entry stays in PQ (lazy)
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
                    set.remove(new Node(v, dist[v]));
                    dist[v] = newDist;
                    set.add(new Node(v, newDist));
                }
            }
        }
        return dist;
    }

    void printDistances(String label, int[] distances) {
        System.out.println(label);
        System.out.println("Vertex \t Distance from Source");
        for (int i = 0; i < vertices; i++) {
            System.out.println(i + " \t\t " + distances[i]);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        DjikstrasAlgoPQ graph = new DjikstrasAlgoPQ(5);
        graph.addEdge(0, 1, 9);
        graph.addEdge(0, 2, 6);
        graph.addEdge(0, 3, 5);
        graph.addEdge(0, 4, 3);
        graph.addEdge(2, 1, 2);
        graph.addEdge(2, 3, 4);

        int src = 0;
        int[] byPQ = graph.dijkstraWithPQ(src);
        int[] byTreeSet = graph.dijkstraWithTreeSet(src);

        graph.printDistances("[PriorityQueue, lazy deletion] from vertex " + src, byPQ);
        graph.printDistances("[TreeSet, eager remove+reinsert] from vertex " + src, byTreeSet);
        System.out.println("Both approaches agree: " + Arrays.equals(byPQ, byTreeSet));
    }
}

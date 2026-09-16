import java.util.*;

/**
 * Vertex 	 Distance from Source
 * 0 		 0
 * 1 		 8
 * 2 		 6
 * 3 		 5
 * 4 		 3
 */
class DjikstrasAlgoPQ {
    private int vertices;
    private List<List<Node>> adjList;

    static class Node implements Comparable<Node> {
        int vertex;
        int weight;

        Node(int vertex, int weight) {
            this.vertex = vertex;
            this.weight = weight;
        }

        @Override
        //ascending order
        public int compareTo(Node b) {
            return Integer.compare(this.weight, b.weight);
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
        // for undirected graph
        adjList.get(v).add(new Node(u, wt));
    }

    void dijkstra(int src) {
        int[] distances = new int[vertices];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[src] = 0;

        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(src, 0));

        while (!pq.isEmpty()) {
            Node currentNode = pq.poll();
            int u = currentNode.vertex;
            for (Node v : adjList.get(u)) {
                // distances[u]-> distance covered from source so u
                // weight distance to be covered to reach from u to v
                int newDist = distances[u] + v.weight;
                if (newDist < distances[v.vertex]) {
                    distances[v.vertex] = newDist;
                    pq.add(new Node(v.vertex, newDist));
                }
            }
        }
        printDistances(distances);
    }

    void printDistances(int[] distances) {
        System.out.println("Vertex \t Distance from Source");
        for (int i = 0; i < vertices; i++) {
            System.out.println(i + " \t\t " + distances[i]);
        }
    }

    public static void main(String[] args) {
        DjikstrasAlgoPQ graph = new DjikstrasAlgoPQ(5);
        graph.addEdge(0, 1, 9);
        graph.addEdge(0, 2, 6);
        graph.addEdge(0, 3, 5);
        graph.addEdge(0, 4, 3);
        graph.addEdge(2, 1, 2);
        graph.addEdge(2, 3, 4);

        int sourceVertex = 0;
        System.out.println("Dijkstra's algorithm starting from vertex " + sourceVertex + ":");
        graph.dijkstra(sourceVertex);
    }
}

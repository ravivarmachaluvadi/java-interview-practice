import java.util.*;

// Class to represent the edge between two nodes
class Node implements Comparable<Node> {
    int vertex;
    int distance;

    public Node(int vertex, int distance) {
        this.vertex = vertex;
        this.distance = distance;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.distance, other.distance);
    }
}

class DijkstraUsingSet {

    // Function to perform Dijkstra's algorithm
    public static int[] dijkstra(int V, List<List<Node>> adj, int src) {
        // Distance array to store the shortest distance from the source to each vertex
        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        // Set to store (distance, vertex) pairs for selecting the minimum distance vertex
        TreeSet<Node> set = new TreeSet<>();

        // Add the source vertex to the set
        set.add(new Node(src, 0));

        while (!set.isEmpty()) {
            // Get the vertex with the minimum distance
            Node uNode = set.pollFirst();
            int u = uNode.vertex;

            // Traverse through all the adjacent vertices of u
            for (Node vNode : adj.get(u)) {
                int v = vNode.vertex;
                int uTOvDistance = vNode.distance;

                // Check if a shorter path to v exists
                int currentDist = dist[u] + uTOvDistance;

                if (currentDist < dist[v]) {
                    // If a shorter path is found, remove the old distance if present
                    set.remove(new Node(v, dist[v]));

                    // Update the distance and add the vertex to the set
                    dist[v] = currentDist;
                    set.add(new Node(v, dist[v]));
                }
            }
        }

        return dist;
    }

    public static void main(String[] args) {
        int V = 5;  // Number of vertices

        // Adjacency list representation of the graph
        List<List<Node>> adj = new ArrayList<>();

        // Initialize the adjacency list
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }

        // Add edges (example graph)
        adj.get(0).add(new Node(1, 2));
        adj.get(0).add(new Node(4, 1));
        adj.get(1).add(new Node(2, 3));
        adj.get(4).add(new Node(2, 2));
        adj.get(4).add(new Node(3, 5));
        adj.get(2).add(new Node(3, 1));

        // Source vertex
        int src = 0;

        // Perform Dijkstra's algorithm
        int[] distances = dijkstra(V, adj, src);

        // Print the shortest distances from the source
        System.out.println("Vertex \t Distance from Source");
        for (int i = 0; i < distances.length; i++) {
            System.out.println(i + " \t\t " + distances[i]);
        }
    }
}

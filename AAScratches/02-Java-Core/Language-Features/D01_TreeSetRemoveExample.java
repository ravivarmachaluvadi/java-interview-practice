import java.util.*;

class TreeSetRemoveExample {

    // Custom Node class representing a graph node
    static class Node implements Comparable<Node> {
        int vertex;
        int distance;

        public Node(int vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            // Compare nodes based on distance (ascending order)
            return Integer.compare(this.distance, other.distance);
        }

        @Override
        public String toString() {
            return "Node{vertex=" + vertex + ", distance=" + distance + "}";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return vertex == node.vertex;
        }

        @Override
        public int hashCode() {
            return Objects.hash(vertex);
        }
    }

    public static void main(String[] args) {
        // TreeSet to store nodes (automatically sorted by distance)
        TreeSet<Node> set = new TreeSet<>();

        // Add some nodes to the set
        set.add(new Node(0, 10));
        set.add(new Node(1, 20));
        set.add(new Node(2, 15));
        set.add(new Node(3, 5));

        System.out.println("Initial TreeSet: " + set);

        // Suppose we found a shorter distance for vertex 2
        int updatedDistance = 7;

        // Remove the old entry (vertex 2 with distance 15)
        set.remove(new Node(2, 15));

        // Add the updated entry (vertex 2 with distance 7)

//        set.add ( new Node ( 2 , updatedDistance ) );

        System.out.println("Updated TreeSet: " + set);
    }
}

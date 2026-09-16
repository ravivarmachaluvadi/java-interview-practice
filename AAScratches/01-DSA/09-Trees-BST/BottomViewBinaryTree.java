import java.util.*;

class BottomViewBinaryTree {

    // Node definition
    static class Node {
        int data;
        Node left, right;

        Node(int data) {
            this.data = data;
            left = right = null;
        }
    }

    // Pair to store node with its horizontal distance
    static class Pair {
        Node node;
        int hd;

        Pair(Node node, int hd) {
            this.node = node;
            this.hd = hd;
        }
    }

    /**
     * Keep track of horizontal distance (HD) from the root:
     * <p>
     * Root has HD = 0
     * <p>
     * Left child → HD - 1
     * <p>
     * Right child → HD + 1
     * <p>
     * Use a TreeMap<Integer, Integer> to map HD → node value.
     * <p>
     * In level order traversal, the last node encountered at each HD is the bottommost node.
     */
    public static void bottomView(Node root) {
        if (root == null) return;

        // Map to store last node at each horizontal distance
        Map<Integer, Integer> map = new TreeMap<>();

        // Queue for BFS
        Queue<Pair> queue = new LinkedList<>();
        queue.add(new Pair(root, 0));
        // level order traversal
        while (!queue.isEmpty()) {
            Pair curr = queue.poll();
            Node node = curr.node;
            int hd = curr.hd;

            // overwrite value at horizantol distance
            // (bottom-most node will remain)
            map.put(hd, node.data);

            if (node.left != null) queue.add(new Pair(node.left, hd - 1));
            if (node.right != null) queue.add(new Pair(node.right, hd + 1));
        }

        // Print bottom view
        for (int value : map.values()) {
            System.out.print(value + " ");
        }
    }

    // Example usage
    public static void main(String[] args) {
        /*
                  20
                 /  \
               8     22
              / \      \
             5   3      25
                / \
               10 14
        */

        Node root = new Node(20);
        root.left = new Node(8);
        root.right = new Node(22);
        root.left.left = new Node(5);
        root.left.right = new Node(3);
        root.right.right = new Node(25);
        root.left.right.left = new Node(10);
        root.left.right.right = new Node(14);

        System.out.println("Bottom View of Binary Tree:");
        bottomView(root);
    }
}

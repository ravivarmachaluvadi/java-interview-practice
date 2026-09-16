/**
 * Problem: Compute the diameter of a binary tree, defined as the number of nodes on
 * the longest path between any two leaves (or endpoints). The example tree has a
 * diameter of 3.
 *
 * Approach: Perform a depth‑first traversal that returns the height of each subtree.
 * While computing heights, update a global maximum with the sum of left and right
 * depths at every node. This sum represents the path length passing through that node.
 *
 * Complexity:
 *   Time O(n) – each node is visited once.
 *   Space O(h) – recursion stack depth equals tree height (worst‑case O(n)).
 */
class DiameterOfBinaryTree {
    private int maxDia = 0;

    public int diameterOfBinaryTree(Node root) {
        findDia(root);
        return maxDia; // Diameter of the binary tree is: 3
    }

    private int findDia(Node root) {
        if (root == null) return 0;

        int leftDepth = findDia(root.left);
        int rightDepth = findDia(root.right);
        int currDia = leftDepth + rightDepth;

        // Update max diameter
        maxDia = Math.max(maxDia, currDia);

        // Return height of this subtree
        return 1 + Math.max(leftDepth, rightDepth);
    }

    // ✅ Test the code here
    public static void main(String[] args) {
        /*
                  1
                 / \
                2   3
               / \
              4   5
         */

        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.left.left = new Node(4);
        root.left.right = new Node(5);

        DiameterOfBinaryTree tree = new DiameterOfBinaryTree();
        int diameter = tree.diameterOfBinaryTree(root);

        System.out.println("Diameter of the binary tree is: " + diameter);
    }
}

class Node {
    int data;
    Node left;
    Node right;

    Node(int val) {
        data = val;
        left = null;
        right = null;
    }
}

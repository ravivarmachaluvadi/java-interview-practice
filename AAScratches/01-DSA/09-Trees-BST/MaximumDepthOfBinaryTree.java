class MaximumDepthOfBinaryTree {

    // Tree node definition
    static class Node {
        int data;
        Node left, right;

        Node(int data) {
            this.data = data;
            left = right = null;
        }
    }

    // Function to find maximum depth
    static int maxDepth(Node root) {
        if (root == null)
            return 0;

        int leftDepth = maxDepth(root.left);
        int rightDepth = maxDepth(root.right);

        return 1 + Math.max(leftDepth, rightDepth);
    }

    public static void main(String[] args) {
        /*
                1
               / \
              2   3
             / \
            4   5
                   \
                    6
        */

        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.left.left = new Node(4);
        root.left.right = new Node(5);
        root.left.right.right = new Node(6);

        // Maximum Depth of Binary Tree: 4
        System.out.println("Maximum Depth of Binary Tree: " + maxDepth(root));
    }
}

// 124. Binary Tree Maximum Path Sum
// https://leetcode.com/problems/binary-tree-maximum-path-sum
/**
 * Constraints:
 * <p>
 * The number of nodes in the tree is in the range [1, 3 * 104].
 * <p>
 * -1000 <= Node.val <= 1000
 */
class MaximumSumPathInBinaryTree {
    int maxPathSum = Integer.MIN_VALUE;

    public int maxPathSum(Node root) {
        calculateMaxPathSum(root);
        return maxPathSum;
    }

    private int calculateMaxPathSum(Node node) {
        if (node == null) return 0;

        int leftSum = Math.max(calculateMaxPathSum(node.left), 0);
        int rightSum = Math.max(calculateMaxPathSum(node.right), 0);

        int currentPathSum = node.data + leftSum + rightSum;
        maxPathSum = Math.max(maxPathSum, currentPathSum);

        // Return the maximum sum of paths from the current node
        return node.data + Math.max(leftSum, rightSum);
    }

    public static void main(String[] args) {
        // Build a small binary tree:
        //        1
        //       / \
        //     -2   3
        Node root = new Node(1);
        root.left = new Node(-2);
        root.right = new Node(3);

        MaximumSumPathInBinaryTree solver = new MaximumSumPathInBinaryTree();

        int result = solver.maxPathSum(root);

        System.out.println("Input tree (root node value): " + root.data);
        System.out.println("Left child: " + (root.left != null ? root.left.data : "null"));
        System.out.println("Right child: " + (root.right != null ? root.right.data : "null"));
        System.out.println("Maximum path sum output: " + result);
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

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

// https://leetcode.com/problems/binary-tree-maximum-path-sum/

/**
 * Constraints:
 * <p>
 * -1000 <= Node.val <= 1000
 */
class BinaryTreeMaxPathSum {
    // OR we can have it as int[1]; parameter
    int maxSum = Integer.MIN_VALUE;

    public int maxPathSum(TreeNode root) {
        calculateMaxPathSum(root);
        return maxSum;
    }

    private int calculateMaxPathSum(TreeNode node) {
        if (node == null) return 0;

        int leftSum = Math.max(calculateMaxPathSum(node.left), 0);
        int rightSum = Math.max(calculateMaxPathSum(node.right), 0);

        int currentMax = node.val + leftSum + rightSum;
        maxSum = Math.max(maxSum, currentMax);
        return node.val + Math.max(leftSum, rightSum);
    }
}

class TreeNode {
    TreeNode left, right;
    int val;
}

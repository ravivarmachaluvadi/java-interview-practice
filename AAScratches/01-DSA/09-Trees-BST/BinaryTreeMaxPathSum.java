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

    public static void main(String[] args) {
        // Build a sample binary tree:
        //        -10
        //        /  \
        //       9   20
        //           / \
        //          15  7
        TreeNode root = new TreeNode();
        root.val = -10;
        root.left = new TreeNode();
        root.left.val = 9;
        root.right = new TreeNode();
        root.right.val = 20;
        root.right.left = new TreeNode();
        root.right.left.val = 15;
        root.right.right = new TreeNode();
        root.right.right.val = 7;

        BinaryTreeMaxPathSum solver = new BinaryTreeMaxPathSum();
        int result = solver.maxPathSum(root);

        System.out.println("Input tree (pre-order):");
        printPreOrder(root);
        System.out.println("\nMaximum path sum: " + result);
    }

    private static void printPreOrder(TreeNode node) {
        if (node == null) return;
        System.out.print(node.val + " ");
        printPreOrder(node.left);
        printPreOrder(node.right);
    }
}

class TreeNode {
    TreeNode left, right;
    int val;
}

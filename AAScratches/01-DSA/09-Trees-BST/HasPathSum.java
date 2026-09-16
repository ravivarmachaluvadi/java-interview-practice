/**
 * Given the root of a binary tree and an integer targetSum,
 * return true if the tree has a root-to-leaf path
 * such that adding up all the values along the path equals targetSum.
 * <p>
 * Example:
 * Input:
 * 5
 * / \
 * 4   8
 * /   / \
 * 11  13  4
 * / \       \
 * 7  2        1
 * <p>
 * targetSum = 22
 * Output: true (5 → 4 → 11 → 2 = 22)
 */

class HasPathSum {

    public boolean hasPathSum(TreeNode root, int targetSum) {
        if (root == null) return false;

        // If it's a leaf node, check if value equals targetSum
        if (root.left == null && root.right == null && root.val == targetSum)
            return true;

        // Recurse left and right with reduced targetSum
        return hasPathSum(root.left, targetSum - root.val) ||
                hasPathSum(root.right, targetSum - root.val);
    }

    public static void main(String[] args) {
        // Build example tree
        TreeNode root = new TreeNode(5);
        root.left = new TreeNode(4);
        root.right = new TreeNode(8);
        root.left.left = new TreeNode(11);
        root.left.left.left = new TreeNode(7);
        root.left.left.right = new TreeNode(2);
        root.right.left = new TreeNode(13);
        root.right.right = new TreeNode(4);
        root.right.right.right = new TreeNode(1);

        HasPathSum solution = new HasPathSum();

        int targetSum = 22;
        boolean result = solution.hasPathSum(root, targetSum);

        System.out.println("Does the tree have a path sum of " + targetSum + "? " + result);
    }
}

class TreeNode {
    TreeNode left, right;
    int val;

    TreeNode(int val) {
        this.val = val;
    }
}

/**
 * Problem: Determine whether a binary tree is height-balanced.
 *
 * Approach: Recursively compute subtree heights; if any subtree is unbalanced,
 * propagate -1 upward to short-circuit further checks. A node is balanced
 * when the absolute difference of left and right subtree heights ≤ 1.
 *
 * Time Complexity: O(n) – each node visited once.
 * Space Complexity: O(h) – recursion stack depth, where h is tree height (worst‑case O(n)).
 */
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}

//abs∣height(left_subtree)−height(right_subtree)∣≤1
class IsBalancedBinaryTree {

    public boolean isBalanced(TreeNode root) {
        return checkHeight(root) != -1;
    }

    private int checkHeight(TreeNode node) {
        if (node == null) return 0;

        int leftHeight = checkHeight(node.left);
        if (leftHeight == -1) return -1;

        int rightHeight = checkHeight(node.right);
        if (rightHeight == -1) return -1;

        if (Math.abs(leftHeight - rightHeight) > 1)
            return -1;

        return Math.max(leftHeight, rightHeight) + 1;
    }

    public static void main(String[] args) {
        IsBalancedBinaryTree tree = new IsBalancedBinaryTree();

        // Example 1: Balanced tree
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.left = new TreeNode(4);
        root1.left.right = new TreeNode(5);
        root1.right.right = new TreeNode(6);

        System.out.println("Is the tree balanced? " + tree.isBalanced(root1)); // Output: true

        // Example 2: Unbalanced tree
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(2);
        root2.left.left = new TreeNode(3);
        root2.left.left.left = new TreeNode(4);

        System.out.println("Is the tree balanced? " + tree.isBalanced(root2)); // Output: false
    }
}

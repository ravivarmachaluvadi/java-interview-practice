/**
 * Problem: Compute the maximum depth (height) of a binary tree.
 *
 * Approach: Recursively compute the height of each subtree and return
 * 1 plus the greater of the left or right subtree heights. A null node
 * contributes zero to the depth.
 *
 * Time Complexity: O(n), where n is the number of nodes in the tree,
 * since each node is visited once.
 *
 * Space Complexity: O(h), where h is the height of the tree, due to
 * recursion stack usage (worst-case O(n) for a skewed tree).
 */

class HeightOfBinaryTree {
    public int maxDepth(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }

    public static void main(String[] args) {
        // Build a small binary tree:
        //        1
        //       / \
        //      2   3
        //     /
        //    4
        TreeNode node4 = new TreeNode(4);
        TreeNode node2 = new TreeNode(2, node4, null);
        TreeNode node3 = new TreeNode(3);
        TreeNode root = new TreeNode(1, node2, node3);

        HeightOfBinaryTree solver = new HeightOfBinaryTree();

        int depth = solver.maxDepth(root);

        System.out.println("Input tree (pre-order): 1 -> 2 -> 4, 3");
        System.out.println("Maximum depth: " + depth);
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

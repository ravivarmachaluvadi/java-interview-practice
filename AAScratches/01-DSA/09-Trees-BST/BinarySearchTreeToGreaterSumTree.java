class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
}

// 1038. Binary Search Tree to Greater Sum Tree
// https://leetcode.com/problems/binary-search-tree-to-greater-sum-tree/
class BinarySearchTreeToGreaterSumTree {
    private int sum = 0;

    public TreeNode bstToGst(TreeNode root) {
        if (root != null) {
            bstToGst(root.right);  // Traverse the right subtree
            sum += root.val;  // Update the sum
            root.val = sum;  // Update the current node's value
            bstToGst(root.left);  // Traverse the left subtree
        }
        return root;
    }

    public static void main(String[] args) {
        // Example usage:
        TreeNode root = new TreeNode();
        root.val = 4;
        root.left = new TreeNode();
        root.left.val = 1;
        root.right = new TreeNode();
        root.right.val = 6;
        root.left.left = new TreeNode();
        root.left.left.val = 0;
        root.left.right = new TreeNode();
        root.left.right.val = 2;
        root.left.right.right = new TreeNode();
        root.left.right.right.val = 3;
        root.right.left = new TreeNode();
        root.right.left.val = 5;
        root.right.right = new TreeNode();
        root.right.right.val = 7;
        root.right.right.right = new TreeNode();
        root.right.right.right.val = 8;

        BinarySearchTreeToGreaterSumTree converter = new BinarySearchTreeToGreaterSumTree();
        TreeNode newRoot = converter.bstToGst(root);

        // The tree is now converted to a Greater Sum Tree.
    }
}

/**
 * Given a binary tree root, a node X in the tree is named good
 * <p>
 * if in the path from root to X there are no nodes with a value greater than X.
 * <p>
 * Return the number of good nodes in the binary tree.
 */
// https://leetcode.com/problems/count-good-nodes-in-binary-tree/
class CountGoodNodes {
    public int goodNodes(TreeNode root) {
        return countGoodNodes(root, Integer.MIN_VALUE);
    }

    private int countGoodNodes(TreeNode node, int maxVal) {
        if (node == null)
            return 0;

        // Update the max value seen so far if the current node's value is greater
        int good = node.val >= maxVal ? 1 : 0;
        // Update maxVal for the next recursive calls
        maxVal = Math.max(maxVal, node.val);
        // Count good nodes in the left and right subtree
        good += countGoodNodes(node.left, maxVal);
        good += countGoodNodes(node.right, maxVal);
        return good;
    }
}

class TreeNode {
    TreeNode left, right;
    int val;
}

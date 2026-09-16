class GetMinimumDifference {
    private int result;
    private Integer previous;

    public int getMinimumDifference(TreeNode root) {
        result = Integer.MAX_VALUE;
        previous = null;
        inOrder(root);
        return result;
    }

    // in-order traversal
    private void inOrder(TreeNode node) {
        if (node == null)
            return;

        inOrder(node.left);
        // remember prevNode or value not null
        if (previous != null)
            result = Math.min(result, node.val - previous);

        previous = node.val;
        inOrder(node.right);
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
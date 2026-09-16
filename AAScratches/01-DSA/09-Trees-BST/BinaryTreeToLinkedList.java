class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }
}

class BinaryTreeToLinkedList {
    public void flatten(TreeNode root) {
        flattenAndGetTail(root);
    }

    private TreeNode flattenAndGetTail(TreeNode root) {
        if (root == null) {
            return null;
        }

        TreeNode leftTail = flattenAndGetTail(root.left);
        TreeNode rightTail = flattenAndGetTail(root.right);

        if (leftTail != null) {
            leftTail.right = root.right;
            root.right = root.left;
            root.left = null;
        }

        if (rightTail != null) {
            return rightTail;
        }

        if (leftTail != null) {
            return leftTail;
        }

        return root;
    }
}
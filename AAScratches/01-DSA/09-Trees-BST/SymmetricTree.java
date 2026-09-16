class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int x) {
        val = x;
        left = right = null;
    }
}

// Creating a symmetric binary tree:
// check whether it is a mirror of itself
//       1
//      / \
//     2   2
//    / \ / \
//   3  4 4  3
class SymmetricTree {
    // Method to check if the tree is symmetric
    public boolean isSymmetric(TreeNode root) {
        if (root == null) {
            return true;
        }
        return isMirror(root.left, root.right);
    }

    // Helper method to check if two subtrees are mirror images
    private boolean isMirror(TreeNode left, TreeNode right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        return (left.val == right.val)
                && isMirror(left.left, right.right)
                && isMirror(left.right, right.left);
    }

    // Main method to test the isSymmetric function
    public static void main(String[] args) {
        SymmetricTree tree = new SymmetricTree();

        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(2);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(4);
        root.right.left = new TreeNode(4);
        root.right.right = new TreeNode(3);

        System.out.println("Is the tree symmetric? " + tree.isSymmetric(root));

        // Creating an asymmetric binary tree:
        //       1
        //      / \
        //     2   2
        //      \    \
        //       3    3
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(2);
        root2.left.right = new TreeNode(3);
        root2.right.right = new TreeNode(3);

        System.out.println("Is the tree symmetric? " + tree.isSymmetric(root2));
    }
}

// Tree, Depth-First Search, Breadth-First Search, Binary Tree
// https://leetcode.com/problems/invert-binary-tree
class InvertTree {
    public static void main(String[] args) {
        // Create the binary tree
        //         4
        //       /   \
        //      2     7
        //     / \   / \
        //    1   3 6   9

        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(7);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(9);

        System.out.println("Original tree (inorder):");
        printInorder(root);

        InvertTree invertTree = new InvertTree();
        invertTree.invertTree(root);

        System.out.println("\n\nInverted tree (inorder):");
        printInorder(root);
    }

    public TreeNode invertTree(TreeNode root) {
        if (root == null) return null;

        // Swap the left and right subtrees
        TreeNode temp = root.left;
        root.left = invertTree(root.right);
        root.right = invertTree(temp);

        return root;
    }

    // Helper function to print tree inorder
    private static void printInorder(TreeNode node) {
        if (node == null) return;
        printInorder(node.left);
        System.out.print(node.val + " ");
        printInorder(node.right);
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}

class DeleteNodeInBST {

    public TreeNode deleteNode(TreeNode root, int key) {
        if (root == null) return null;
        if (key < root.val) {
            root.left = deleteNode(root.left, key);
        } else if (key > root.val) {
            root.right = deleteNode(root.right, key);
        } else {
            // Node with only one child or no child
            if (root.left == null) return root.right;
            else if (root.right == null) return root.left;

            // Node with two children:
            //Get the inorder successor (smallest in the right subtree)
            root.val = minValue(root.right);

            // Delete the inorder successor
            // as current value deleted by overriding with smallest in right
            // subtree now delete current val in right subtree
            root.right = deleteNode(root.right, root.val);
        }
        return root;
    }

    private int minValue(TreeNode node) {
        int minValue = node.val;
        while (node.left != null) {
            node = node.left;
            minValue = node.val;
        }
        return minValue;
    }

    public void inorderTraversal(TreeNode root) {
        if (root != null) {
            inorderTraversal(root.left);
            System.out.print(root.val + " ");
            inorderTraversal(root.right);
        }
    }

    public static void main(String[] args) {
        DeleteNodeInBST tree = new DeleteNodeInBST();

        // Example: Construct a BST
        TreeNode root = new TreeNode(5);
        root.left = new TreeNode(3);
        root.right = new TreeNode(6);
        root.left.left = new TreeNode(2);
        root.left.right = new TreeNode(4);
        root.right.right = new TreeNode(7);

        System.out.println("Original BST (inorder):");
        tree.inorderTraversal(root);
        System.out.println();

        // Delete a node with key 3
        int key = 3;
        System.out.println("Deleting node with key " + key + ":");
        root = tree.deleteNode(root, key);

        System.out.println("BST after deletion (inorder):");
        tree.inorderTraversal(root);
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}

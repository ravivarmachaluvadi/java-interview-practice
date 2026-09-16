import org.w3c.dom.Node;

class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int val) {
        this.val = val;
    }
}

class BSTtoDLLInPlace {

    // instance variables
    private TreeNode head = null; // Head of the DLL
    private TreeNode prev = null; // Previous node in the DLL

    // Convert BST to DLL in place
    // (root means as current Node)
    // // Perform in-order traversal
    public TreeNode bstToDLL(TreeNode root) {
        if (root == null) {
            return null;
        }
        bstToDLL(root.left);
        // First node becomes the head of the DLL
        if (prev == null) {
            head = root;
        } else {
            // Link previous node to current
            prev.right = root;
            // Link current node back to previous
            root.left = prev;
        }
        prev = root; // Update prev to current
        bstToDLL(root.right);
        return head;
    }

    // Print DLL for verification
    public void printDLL(TreeNode head) {
        TreeNode current = head;
        while (current != null) {
            System.out.print(current.val + " ");
            current = current.right;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // Create a sample BST
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(6);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);
        root.right.left = new TreeNode(5);
        root.right.right = new TreeNode(7);

        BSTtoDLLInPlace converter = new BSTtoDLLInPlace();
        TreeNode dllHead = converter.bstToDLL(root);

        // Print the resulting DLL
        converter.printDLL(dllHead);
    }
}

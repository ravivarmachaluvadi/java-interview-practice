/**
 * Problem: Convert a Binary Search Tree (BST) into a sorted doubly linked list (DLL)
 * in place, using each node's left and right pointers as prev and next links.
 *
 * Approach: Perform an in-order traversal of the BST. While traversing,
 * link each visited node to its predecessor (prev). The first visited node
 * becomes the head of the DLL. Update 'prev' after linking to maintain the chain.
 *
 * Time Complexity: O(n) – each node is visited once during recursion.
 * Space Complexity: O(h) – recursion stack depth equals tree height (worst‑case O(n)).
 */
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

/**
 * Problem: Convert a binary tree into a flattened linked list in-place.
 * The resulting structure follows the preorder traversal, using only right
 * child pointers as "next" links and setting all left pointers to null.
 *
 * Approach:
 * Recursively flatten the left and right subtrees. After recursion,
 * if a left subtree exists, attach its tail to the original right subtree,
 * then move the entire left subtree to the right side of the current node
 * and clear the left pointer. Return the tail of the flattened subtree.
 *
 * Time Complexity: O(n) – each node is visited once.
 * Space Complexity: O(h) – recursion stack depth, where h is tree height
 * (worst case O(n), average O(log n)). 
 */
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

    // A recursive helper must be a method: a lambda cannot reference the local
    // variable it is being assigned to (it is not definitely assigned yet).
    private static void printPreorder(TreeNode node) {
        if (node == null) return;
        System.out.print(node.val + " ");
        printPreorder(node.left);
        printPreorder(node.right);
    }

    public static void main(String[] args) {
        // Build a small binary tree:
        //        1
        //       / \
        //      2   5
        //     / \   \
        //    3   4   6
        TreeNode root = new TreeNode();
        root.val = 1;
        root.left = new TreeNode(); root.left.val = 2;
        root.right = new TreeNode(); root.right.val = 5;
        root.left.left = new TreeNode(); root.left.left.val = 3;
        root.left.right = new TreeNode(); root.left.right.val = 4;
        root.right.right = new TreeNode(); root.right.right.val = 6;

        // Helper to print the flattened list (right pointers only)
        java.util.function.Consumer<TreeNode> printList = node -> {
            while (node != null) {
                System.out.print(node.val + " ");
                node = node.right;
            }
        };

        System.out.println("Input tree (preorder):");
        printPreorder(root);
        System.out.println();

        BinaryTreeToLinkedList converter = new BinaryTreeToLinkedList();
        converter.flatten(root);

        System.out.println("Flattened list (right pointers only):");
        printList.accept(root);
        System.out.println();
    }
}
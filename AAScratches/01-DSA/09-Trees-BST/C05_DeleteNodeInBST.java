/**
 * Problem: LeetCode 450 - Delete a node with a given key from a BST, keeping the BST property.
 *
 * Approaches:
 * 1. deleteRecursiveSuccessorCopy - classic recursion: for a node with two children, copy the
 *    inorder successor's value (min of right subtree) into it, then recursively delete that
 *    successor from the right subtree. Time O(h), space O(h) recursion stack.
 * 2. deleteIterativeSplice - iterate with a parent pointer; for a node with two children, hang
 *    the whole left subtree under the leftmost node of the right subtree and return the right
 *    subtree (no value copying). Time O(h), space O(1). Trade-off: the tree can become taller
 *    (skewed) because the left subtree is pushed one level deeper.
 */
class DeleteNodeInBST {

    // ---------------------------------------------------------------
    // Approach 1: recursive, replace with inorder successor's value
    // ---------------------------------------------------------------
    public TreeNode deleteRecursiveSuccessorCopy(TreeNode root, int key) {
        if (root == null) return null;
        if (key < root.val) {
            root.left = deleteRecursiveSuccessorCopy(root.left, key);
        } else if (key > root.val) {
            root.right = deleteRecursiveSuccessorCopy(root.right, key);
        } else {
            // Node with only one child or no child: bypass it
            if (root.left == null) return root.right;
            else if (root.right == null) return root.left;

            // Node with two children:
            // Get the inorder successor (smallest in the right subtree)
            root.val = minValue(root.right);

            // The successor's value now lives here, so delete the successor
            // itself from the right subtree (it has at most one child, so this ends fast)
            root.right = deleteRecursiveSuccessorCopy(root.right, root.val);
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

    // ---------------------------------------------------------------
    // Approach 2: iterative parent walk + splice left subtree under
    //             the leftmost node of the right subtree
    // ---------------------------------------------------------------

    // Given the node to remove, return the subtree that should replace it.
    private TreeNode connector(TreeNode root) {
        // Case 1: no left child -> right subtree takes its place
        if (root.left == null) return root.right;

        // Case 2: no right child -> left subtree takes its place
        if (root.right == null) return root.left;

        /*
        Case 3: both children:
        1. Save the left subtree.
        2. Find the leftmost node in the right subtree (the inorder successor).
        3. Attach the left subtree as that node's left child.
           Every value in the left subtree is smaller than the successor, so BST order holds. */
        TreeNode leftChild = root.left;
        TreeNode leftmostChildInRightSubtree = root.right;
        while (leftmostChildInRightSubtree.left != null) {
            leftmostChildInRightSubtree = leftmostChildInRightSubtree.left;
        }
        leftmostChildInRightSubtree.left = leftChild;

        // The right subtree is the new root of this part of the tree.
        return root.right;
    }

    public TreeNode deleteIterativeSplice(TreeNode root, int key) {
        if (root == null) return null;

        // Root itself is the target: no parent to fix, just return the replacement
        if (root.val == key) {
            return connector(root);
        }

        // Walk down keeping `node` as the parent of the candidate,
        // so we can rewire node.left / node.right when we find the key.
        TreeNode node = root;
        while (node != null) {
            if (node.val > key) {
                if (node.left != null && node.left.val == key) {
                    node.left = connector(node.left);
                    break;
                }
                node = node.left;
            } else {
                if (node.right != null && node.right.val == key) {
                    node.right = connector(node.right);
                    break;
                }
                node = node.right;
            }
        }
        return root;
    }

    // ---------------------------------------------------------------
    // Helpers for main
    // ---------------------------------------------------------------
    private static String inorder(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        inorder(root, sb);
        return sb.toString().trim();
    }

    private static void inorder(TreeNode root, StringBuilder sb) {
        if (root == null) return;
        inorder(root.left, sb);
        sb.append(root.val).append(' ');
        inorder(root.right, sb);
    }

    // Both approaches mutate the tree, so each test builds a fresh copy.
    //        5
    //      /   \
    //     3     6
    //    / \     \
    //   2   4     7
    private static TreeNode buildTree() {
        TreeNode root = new TreeNode(5);
        root.left = new TreeNode(3);
        root.right = new TreeNode(6);
        root.left.left = new TreeNode(2);
        root.left.right = new TreeNode(4);
        root.right.right = new TreeNode(7);
        return root;
    }

    public static void main(String[] args) {
        DeleteNodeInBST tree = new DeleteNodeInBST();
        System.out.println("Original BST (inorder): " + inorder(buildTree()));

        // 3 -> two children, 6 -> one child, 2 -> leaf, 5 -> root with two children, 9 -> absent
        int[] keys = {3, 6, 2, 5, 9};
        for (int key : keys) {
            System.out.println("Delete " + key + ":");
            System.out.println("  recursive successor-copy -> " + inorder(tree.deleteRecursiveSuccessorCopy(buildTree(), key)));
            System.out.println("  iterative splice         -> " + inorder(tree.deleteIterativeSplice(buildTree(), key)));
        }
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

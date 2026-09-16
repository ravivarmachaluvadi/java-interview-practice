/**
 * Problem:
 *   Compute the post-order traversal of a binary tree and return the node values
 *   as a list in left-right-root order.
 *
 * Approach:
 *   Recursively visit the left subtree, then the right subtree,
 *   and finally add the current node's value to the result list.
 *
 * Complexity:
 *   Time:  O(n) – each node is visited once.
 *   Space: O(h) – recursion stack depth equals tree height (worst-case O(n)).
 */
import java.util.ArrayList;
import java.util.List;

class TreeNode {
    int data;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        data = val;
        left = null;
        right = null;
    }
}

class RecursivePostorder {
    private void recursivePostorder(TreeNode root, List<Integer> arr) {
        if (root == null) {
            return;
        }
        recursivePostorder(root.left, arr);
        recursivePostorder(root.right, arr);
        arr.add(root.data);
    }

    public List<Integer> postorder(TreeNode root) {
        List<Integer> arr = new ArrayList<>();
        recursivePostorder(root, arr);
        return arr;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(7);

        RecursivePostorder tree = new RecursivePostorder();
        List<Integer> result = tree.postorder(root);
        System.out.println("Postorder Traversal: " + result);
        // Postorder Traversal: [4, 5, 2, 6, 7, 3, 1]
    }
}

/**
 * Problem: Determine whether two binary trees are structurally identical and have the same node values.
 *
 * Approach: Recursively compare corresponding nodes of both trees:
 *   - If both nodes are null, they match.
 *   - If one is null or their values differ, trees are not the same.
 *   - Otherwise, recursively check left subtrees and right subtrees.
 *
 * Time Complexity: O(n) – each node is visited once.
 * Space Complexity: O(h) – recursion stack depth equals tree height (worst case O(n), average O(log n)).
 */
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}

class SameTreeExample {

    // Function to check if two trees are the same
    public static boolean isSameTree(TreeNode p, TreeNode q) {
        // Both are null → same
        if (p == null && q == null)
            return true;

        // One is null and the other is not → not same
        if (p == null || q == null)
            return false;

        // Values differ → not same
        if (p.val != q.val)
            return false;

        // Recursively check left and right subtrees
        return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }

    public static void main(String[] args) {
        // Tree 1
        TreeNode tree1 = new TreeNode(1);
        tree1.left = new TreeNode(2);
        tree1.right = new TreeNode(3);

        // Tree 2 (same structure and values)
        TreeNode tree2 = new TreeNode(1);
        tree2.left = new TreeNode(2);
        tree2.right = new TreeNode(3);

        // Tree 3 (different structure/values)
        TreeNode tree3 = new TreeNode(1);
        tree3.left = new TreeNode(3);

        // Check comparisons
        System.out.println("Tree1 and Tree2 same? " + isSameTree(tree1, tree2)); // true
        System.out.println("Tree1 and Tree3 same? " + isSameTree(tree1, tree3)); // false
    }
}

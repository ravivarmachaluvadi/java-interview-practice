/**
 * Problem: Construct a Binary Search Tree (BST) from its preorder traversal.
 *
 * Approach:
 * Use recursion with bounds to consume the preorder array in order.
 * Maintain an index pointer `i`. For each call, if the current value lies
 * outside the allowed range [l, u], return null. Otherwise create a node,
 * increment `i`, and recursively build left subtree with upper bound = val
 * and right subtree with lower bound = val.
 *
 * Time Complexity: O(n) – each element is processed once.
 * Space Complexity: O(h) – recursion stack depth equals tree height (worst‑case O(n)).
 */
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}

// 1008. Construct Binary Search Tree from Preorder Traversal
// https://leetcode.com/problems/construct-binary-search-tree-from-preorder-traversal/description/
class ConstructBinarySearchTreeFromPreorderTraversal {
    int i = 0;

    public TreeNode bstFromPreorder(int[] preorder) {
        return helper(preorder, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private TreeNode helper(int[] preorder, int l, int u) {
        if (i == preorder.length) return null;
        int val = preorder[i];

        // remember this
        if (val < l || val > u) return null;
        i++;

        TreeNode root = new TreeNode(val);
        root.left = helper(preorder, l, val);
        root.right = helper(preorder, val, u);
        return root;
    }

    public static void main(String[] args) {
        ConstructBinarySearchTreeFromPreorderTraversal solution = new ConstructBinarySearchTreeFromPreorderTraversal();
        int[] preorder = {8, 5, 1, 7, 10, 12};
        TreeNode root = solution.bstFromPreorder(preorder);
        // The tree can be printed or verified here if needed
    }
}
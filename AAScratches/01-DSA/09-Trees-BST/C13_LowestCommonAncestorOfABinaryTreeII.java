// https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree-ii/description/
// 1644. Lowest Common Ancestor of a Binary Tree II

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}

class LowestCommonAncestorOfABinaryTreeII {
    boolean pFound = false;
    boolean qFound = false;

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode LCA = LCA(root, p, q);
        return pFound && qFound ? LCA : null;
    }

    public TreeNode LCA(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) return root;
        TreeNode left = LCA(root.left, p, q);
        TreeNode right = LCA(root.right, p, q);
        if (root == p) {
            pFound = true;
            return root;
        }
        if (root == q) {
            qFound = true;
            return root;
        }
        return left == null ? right : right == null ? left : root;
    }

    // Example main to test
    public static void main(String[] args) {
        LowestCommonAncestorOfABinaryTreeII sol = new LowestCommonAncestorOfABinaryTreeII();

        // Construct example tree:
        //       3
        //      / \
        //     5   1
        //    / \   \
        //   6   2   8
        //      / \
        //     7   4
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(5);
        root.right = new TreeNode(1);
        root.left.left = new TreeNode(6);
        root.left.right = new TreeNode(2);
        root.left.right.left = new TreeNode(7);
        root.left.right.right = new TreeNode(4);
        root.right.right = new TreeNode(8);

        TreeNode p = root.left;              // node 5
        TreeNode q = root.left.right.right;  // node 4

        TreeNode lca = sol.lowestCommonAncestor(root, p, q);
        System.out.println(lca != null ? lca.val : "null");  // expected output: 5

        TreeNode q2 = new TreeNode(42);  // node not in tree
        TreeNode lca2 = sol.lowestCommonAncestor(root, p, q2);
        System.out.println(lca2 != null ? lca2.val : "null"); // expected output: null
    }
}

class LCA {

    public static TreeNode lowestCommonAncestor(TreeNode root,
                                                TreeNode p,
                                                TreeNode q) {
        //base case
        if (root == null || root == p || root == q)
            return root;

        // Recursively search for p and q in the left and right subtrees
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        // ensure root have both left and right to be the LCA
        if (left != null && right != null) return root;

        // return the non-null & found left or right
        return (left != null) ? left : right;
    }

    public static void main(String[] args) {
        TreeNode treeNode = lowestCommonAncestor(new TreeNode(), new TreeNode(), new TreeNode());
        System.out.println(treeNode);
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

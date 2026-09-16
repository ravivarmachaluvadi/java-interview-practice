class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
}

// 1026. Maximum Difference Between Node and Ancestor
// https://leetcode.com/problems/maximum-difference-between-node-and-ancestor/description/
class MaximumDifferenceBetweenNodeAndAncestor {
    public int diff = 0;

    public int maxAncestorDiff(TreeNode root) {
        if (root == null) return 0;
        int min = root.val, max = root.val;
        diff(root, min, max);
        return diff;
    }

    public void diff(TreeNode root, int min, int max) {
        if (root == null) return;
        diff = Math.max(diff, Math.max(Math.abs(min - root.val), Math.abs(max - root.val)));
        min = Math.min(min, root.val);
        max = Math.max(max, root.val);
        diff(root.left, min, max);
        diff(root.right, min, max);
    }

    public static void main(String[] args) {
        MaximumDifferenceBetweenNodeAndAncestor sol =
                new MaximumDifferenceBetweenNodeAndAncestor();
        TreeNode root = new TreeNode();
        root.val = 8;
        root.left = new TreeNode();
        root.left.val = 3;
        root.right = new TreeNode();
        root.right.val = 10;
        root.left.left = new TreeNode();
        root.left.left.val = 1;
        root.left.right = new TreeNode();
        root.left.right.val = 6;
        root.left.right.left = new TreeNode();
        root.left.right.left.val = 4;
        root.left.right.right = new TreeNode();
        root.left.right.right.val = 7;
        root.right.right = new TreeNode();
        root.right.right.val = 14;
        root.right.right.left = new TreeNode();
        root.right.right.left.val = 13;

        int result = sol.maxAncestorDiff(root);
        System.out.println(result); // Expected output: 7
    }
}

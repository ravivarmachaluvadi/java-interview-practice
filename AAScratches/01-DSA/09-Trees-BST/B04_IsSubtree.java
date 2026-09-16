// https://leetcode.com/problems/subtree-of-another-tree/description/
class IsSubtree {
    public static boolean isSubtree(TreeNode root, TreeNode subRoot) {
        if (root == null) {
            return false;
        }
        if (checkSame(root, subRoot)) {
            return true;
        }
        return isSubtree(root.left, subRoot) || isSubtree(root.right, subRoot);
    }

    public static boolean checkSame(TreeNode root, TreeNode subRoot) {
        if (root == null && subRoot == null) {
            return true;
        }
        if (root == null || subRoot == null) {
            return false;
        }
        if (root.val != subRoot.val) {
            return false;
        }
        return checkSame(root.left, subRoot.left) && checkSame(root.right, subRoot.right);
    }

    public static void main(String[] args) {
        boolean flag=isSubtree(null, null);
    }
}

class TreeNode {
    TreeNode left, right;
    int val;
}


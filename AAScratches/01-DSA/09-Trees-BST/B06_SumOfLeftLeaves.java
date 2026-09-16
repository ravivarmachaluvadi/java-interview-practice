// https://leetcode.com/problems/sum-of-left-leaves/
class SumOfLeftLeaves {
    public int sumOfLeftLeaves(TreeNode root) {
        if (root == null) return 0;

        int sum = 0;
        if (
                root.left != null
                        && root.left.left == null
                        && root.left.right == null) {
            sum += root.left.val;
        }
        sum += sumOfLeftLeaves(root.left);
        sum += sumOfLeftLeaves(root.right);

        return sum;
    }

    public static void main(String[] args) {
        // Build a small concrete binary tree:
        //        3
        //       / \
        //      9  20
        //         / \
        //        15  7
        TreeNode root = new TreeNode();
        root.val = 3;
        root.left = new TreeNode();
        root.left.val = 9;          // left leaf
        root.right = new TreeNode();
        root.right.val = 20;
        root.right.left = new TreeNode();
        root.right.left.val = 15;
        root.right.right = new TreeNode();
        root.right.right.val = 7;

        SumOfLeftLeaves solver = new SumOfLeftLeaves();

        int result = solver.sumOfLeftLeaves(root);

        System.out.println("Input tree (pre-order):");
        printTreePreOrder(root);
        System.out.println("\nSum of left leaves: " + result);
    }

    private static void printTreePreOrder(TreeNode node) {
        if (node == null) return;
        System.out.print(node.val + " ");
        printTreePreOrder(node.left);
        printTreePreOrder(node.right);
    }
}

class TreeNode {
    TreeNode left, right;
    int val;
}

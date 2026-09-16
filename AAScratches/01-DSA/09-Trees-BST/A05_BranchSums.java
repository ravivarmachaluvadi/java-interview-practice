import java.util.ArrayList;
import java.util.List;

/*
       1
     /   \
    2     3
   / \   / \
  4   5 6   7

 */
/**
 * all possible root to leaf sums possible
 */
class BranchSums {
    public static List<Integer> branchSums(TreeNode root) {
        List<Integer> sums = new ArrayList<>();
        calculateBranchSums(root, 0, sums);
        return sums;
    }

    private static void calculateBranchSums(TreeNode node, int runningSum, List<Integer> sums) {
        if (node == null) return;

        // remember this , updating sum at recursive calls won't work
        int newRunningSum = runningSum + node.value;

        // Check if we have reached a leaf node
        if (node.left == null && node.right == null) {
            sums.add(newRunningSum);
            return;
        }

        // Recursive calls for left and right children
        calculateBranchSums(node.left, newRunningSum, sums);
        calculateBranchSums(node.right, newRunningSum, sums);
    }

    public static void main(String[] args) {
        // Example: creating a sample binary tree
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(7);

        List<Integer> result = branchSums(root);
        System.out.println("Branch sums: " + result); // Output: [7, 8, 10, 11]
    }
}

class TreeNode {
    int value;
    TreeNode left;
    TreeNode right;

    TreeNode(int value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
}
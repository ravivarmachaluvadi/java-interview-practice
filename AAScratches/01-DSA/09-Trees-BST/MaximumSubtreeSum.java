import java.util.*;

// Definition for a binary tree node
class TreeNode {
    int data;
    TreeNode left, right;

    TreeNode(int x) {
        data = x;
        left = right = null;
    }
}

class Solution {
    private int maxSum = Integer.MIN_VALUE;  // Store the maximum sum found
    private int maxNode = -1;  // Store the node with the maximum subtree sum

    // DFS function to calculate the sum of subtrees
    private int dfs(TreeNode node, int[] nums) {
        if (node == null) return 0;

        int leftSum = dfs(node.left, nums);  // Calculate left subtree sum
        int rightSum = dfs(node.right, nums);  // Calculate right subtree sum

        // Correct index lookup: node.data gives the index, nums[node.data] gives the value at that index
        int nodeVal = nums[node.data];  // Get the value associated with the node
        int totalSum = leftSum + rightSum + nodeVal;  // Total sum for the current subtree

        // Update the maximum sum and the corresponding node
        if (totalSum > maxSum) {
            maxSum = totalSum;
            maxNode = node.data;
        }

        return totalSum;  // Return the total sum for the current subtree
    }

    // Function to return the node with the maximum subtree sum and its sum
    public int[] maxSubtreeSum(TreeNode root, int[] nums) {
        maxSum = Integer.MIN_VALUE;
        maxNode = -1;
        dfs(root, nums);  // Perform DFS to find the maximum subtree sum
        return new int[]{maxNode, maxSum};  // Return the result
    }
}

// Main class to test the Solution class
class MaximumSubtreeSum {
    public static void main(String[] args) {
        // Test case input: binary tree and nums array
        TreeNode root = new TreeNode(0);
        root.left = new TreeNode(1);
        root.right = new TreeNode(2);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(4);
        root.right.left = new TreeNode(5);

        int[] nums = {10, 20, 30, 40, 50, 60};  // Values associated with each node by index
        Solution sol = new Solution();
        int[] result = sol.maxSubtreeSum(root, nums);

        System.out.println("Node with max subtree sum: " + result[0] + ", Max sum: " + result[1]);
    }
}

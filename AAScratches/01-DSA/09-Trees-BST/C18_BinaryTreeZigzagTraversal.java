// LeetCode Problem: https://leetcode.com/problems/binary-tree-zigzag-level-order-traversal/

import java.util.*;

/**
 * [3]
 * [20, 9]
 * [15, 7]
 */
class BinaryTreeZigzagTraversal {
    public static List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode current = queue.poll();
                if (leftToRight) {
                    level.add(current.val);
                } else {
                    level.add(0, current.val);
                }

                TreeNode left = current.left;
                TreeNode right = current.right;
                if (left != null) queue.offer(left);
                if (right != null) queue.offer(right);
            }
            result.add(level);
            // Toggle direction
            leftToRight = !leftToRight;
        }
        return result;
    }

    // Main method for testing
    public static void main(String[] args) {
        // Construct the binary tree
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);

        // Call the zigzagLevelOrder method
        List<List<Integer>> result = zigzagLevelOrder(root);

        // Print the result
        System.out.println("Zigzag Level Order Traversal:");
        for (List<Integer> level : result) {
            System.out.println(level);
        }
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}

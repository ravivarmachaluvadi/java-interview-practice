import java.util.*;

// Definition for a binary tree node.
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}

// https://leetcode.com/problems/find-leaves-of-binary-tree/description/
// 366. Find Leaves of Binary Tree
class FindLeavesOfBinaryTree {

    public static List<List<Integer>> findLeaves(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        dfs(root, result);
        return result;
    }

    // returns the height of the node: 0 for leaves, 1 for their parents, etc.
    private static int dfs(TreeNode node, List<List<Integer>> result) {
        if (node == null) {
            return -1;  // so that leaf children produce 0
        }
        int leftHeight = dfs(node.left, result);
        int rightHeight = dfs(node.right, result);
        int height = Math.max(leftHeight, rightHeight) + 1;

        // ensure the result list has a list for this height
        if (height >= result.size()) {
            result.add(new ArrayList<>());
        }
        result.get(height).add(node.val);
        return height;
    }

    // Example / test in main
    public static void main(String[] args) {
        /*
         * Example tree:
         *     1
         *    / \
         *   2   3
         *  / \
         * 4   5
         *
         * The leaves are [4,5,3] (height 0),
         * then the next level (height 1) is [2],
         * then the root (height 2) is [1].
         *
         * So output: [[4,5,3],[2],[1]]
         */
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);

        List<List<Integer>> result = findLeaves(root);
        System.out.println(result);
    }
}

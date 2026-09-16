import java.util.*;

// https://leetcode.com/problems/binary-tree-level-order-traversal/
class LevelOrderTraversal {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> currentLevel = new ArrayList<>();
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                currentLevel.add(node.val);
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            result.add(currentLevel);
        }
        return result;
    }

    public static void main(String[] args) {
        // Build a small binary tree:
        //        1
        //       / \
        //      2   3
        //     / \   \
        //    4   5   6
        TreeNode root = new TreeNode();
        root.val = 1;
        root.left = new TreeNode(); root.left.val = 2;
        root.right = new TreeNode(); root.right.val = 3;
        root.left.left = new TreeNode(); root.left.left.val = 4;
        root.left.right = new TreeNode(); root.left.right.val = 5;
        root.right.right = new TreeNode(); root.right.right.val = 6;

        LevelOrderTraversal solver = new LevelOrderTraversal();
        List<List<Integer>> output = solver.levelOrder(root);

        System.out.println("Input tree (root value): " + root.val);
        System.out.println("Level order traversal result: " + output);
    }
}

class TreeNode {
    TreeNode left, right;
    int val;
}

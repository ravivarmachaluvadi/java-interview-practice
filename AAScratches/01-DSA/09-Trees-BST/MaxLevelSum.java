import java.util.LinkedList;
import java.util.Queue;

// https://leetcode.com/problems/maximum-level-sum-of-a-binary-tree/
class MaxLevelSum {

    public int maxLevelSum(TreeNode root) {
        if (root == null) return 0;

        int maxSum = Integer.MIN_VALUE, maxLevel = 0, level = 0;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int sum = 0;
            int n = queue.size();
            level++;
            for (int i = 0; i < n; i++) {
                TreeNode currNode = queue.poll();
                sum += currNode.val;
                if (currNode.left != null)
                    queue.add(currNode.left);

                if (currNode.right != null)
                    queue.add(currNode.right);
            }
            if (sum > maxSum) {
                maxSum = sum;
                maxLevel = level;
            }
        }
        return maxLevel;
    }
}

class TreeNode {
    TreeNode left, right;
    int val;
}

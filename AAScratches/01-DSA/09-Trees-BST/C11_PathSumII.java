import java.util.*;

// https://leetcode.com/problems/path-sum-ii/
class PathSumII {
    public static List<List<Integer>> pathSum(TreeNode root, int targetSum) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> currentPath = new ArrayList<>();
        pathSumHelper(root, targetSum, result, currentPath);
        return result;
    }

    private static void pathSumHelper(TreeNode node,
                               int targetSum,
                               List<List<Integer>> result,
                               List<Integer> currentPath) {

        if (node == null) return;

        currentPath.add(node.val);
        if (node.left == null && node.right == null && targetSum == node.val) {
            result.add(new ArrayList<>(currentPath));
        } else {
            pathSumHelper(node.left, targetSum - node.val, result, currentPath);
            pathSumHelper(node.right, targetSum - node.val, result, currentPath);
        }
        currentPath.remove(currentPath.size() - 1);
    }

    public static void main(String[] args) {
        pathSum(null, 22);
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }
}

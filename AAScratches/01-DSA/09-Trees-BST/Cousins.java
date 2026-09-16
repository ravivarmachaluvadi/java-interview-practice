import java.util.LinkedList;
import java.util.Queue;

/**
 * Given the root of a binary tree with unique values and the values of two
 * <p>
 * different nodes of the tree x and y, return true if the nodes corresponding
 * <p>
 * to the values x and y in the tree are cousins, or false otherwise.
 * <p>
 * Two nodes of a binary tree are cousins if they have the same depth with different parents.
 * <p>
 * Note that in a binary tree, the root node is at the depth 0, and children
 * <p>
 * of each depth k node are at the depth k + 1.
 */

// https://leetcode.com/problems/cousins-in-binary-tree/
class Cousins {
    public boolean isCousins(TreeNode root, int A, int B) {
        if (root == null) return false;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int currentLevelSize = queue.size();
            boolean isAexist = false;
            boolean isBexist = false;
            for (int i = 0; i < currentLevelSize; i++) {
                TreeNode cur = queue.poll();
                if (cur.val == A) isAexist = true;
                if (cur.val == B) isBexist = true;
                // if immediate siblings return false
                if (cur.left != null && cur.right != null) {
                    if (cur.left.val == A && cur.right.val == B)
                        return false;
                    if (cur.left.val == B && cur.right.val == A)
                        return false;
                }
                if (cur.left != null) queue.offer(cur.left);
                if (cur.right != null) queue.offer(cur.right);
            }
            // current level checked if found return true
            if (isAexist && isBexist) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        // Build a small binary tree:
        //        1
        //       / \
        //      2   3
        //     /   /
        //    4   5
        TreeNode root = new TreeNode();
        root.val = 1;
        TreeNode node2 = new TreeNode(); node2.val = 2;
        TreeNode node3 = new TreeNode(); node3.val = 3;
        TreeNode node4 = new TreeNode(); node4.val = 4;
        TreeNode node5 = new TreeNode(); node5.val = 5;
        root.left = node2; root.right = node3;
        node2.left = node4;
        node3.left = node5;

        Cousins solver = new Cousins();

        int A = 4, B = 5;
        boolean result = solver.isCousins(root, A, B);

        System.out.println("Input tree: 1-2-3 with children 4 under 2 and 5 under 3");
        System.out.println("Checking if nodes " + A + " and " + B + " are cousins.");
        System.out.println("Output: " + result);
    }
}

class TreeNode {
    TreeNode left, right;
    int val;
}
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
}

class TreeNode {
    TreeNode left, right;
    int val;
}
/*
 * =====================================================================
 *  Reverse Odd Levels of Binary Tree         LeetCode 2415 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   You are given the root of a PERFECT binary tree (every level completely filled).
 *   Reverse the node VALUES at every odd-numbered level, counting the root as level 0.
 *   The shape of the tree never changes - only values move. Return the root.
 *
 * EXAMPLE
 *         1                 1            level 0 (even) untouched
 *       /   \      ->     /   \          level 1 [2, 3] reversed to [3, 2]
 *      2     3           3     2         level 2 (even) untouched
 *     / \   / \         / \   / \
 *    4   5 6   7       4   5 6   7
 *   level order after: 1 3 2 4 5 6 7
 *
 *   4-level tree 1..15  ->  1 3 2 4 5 6 7 15 14 13 12 11 10 9 8   (level 3 reversed too)
 *   single node 1       ->  1                                     (no odd level exists)
 *
 * APPROACH  (paired symmetric DFS, the same pairing as Symmetric Tree)
 *   1. Start the recursion with the pair (root.left, root.right) at depth 0. These two nodes are
 *      the outermost pair of level 1, so depth d in the recursion means tree level d + 1.
 *   2. If depth is even the tree level is odd, so swap the values of the two paired nodes.
 *   3. Recurse on mirrored pairs: (left.left, right.right) and (left.right, right.left).
 *      Mirroring is what guarantees the two nodes handed to each call are the pair that must
 *      trade places - first with last, second with second-last, and so on.
 *   4. Stop as soon as either node is null (in a perfect tree both go null together).
 *
 * KEY INSIGHT
 *   You do not need BFS to do a per-level operation. Walking two nodes in mirrored lockstep
 *   visits exactly the pairs that a level reversal has to swap, so a plain DFS reverses whole
 *   levels without ever materialising one. This is Symmetric Tree's pairing reused to MUTATE
 *   instead of to COMPARE - remember the pairing, not the traversal, is the variable.
 *
 * COMPLEXITY
 *   Time  O(n)       every node is part of exactly one pair and is touched once
 *   Space O(log n)   recursion depth equals the height, and a perfect tree has height log2(n)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it with BFS instead: collect each level, and when the level index is odd write the
 *     values back reversed. O(n) time but O(n) space for the widest level.
 *   - What if the tree is NOT perfect? The mirrored pairing breaks down (pairs can be missing),
 *     so the BFS version is the safe answer - it reverses whatever nodes the level actually has.
 *   - Reverse even levels instead: change the depth test from level % 2 == 0 to != 0.
 *   - Compare with Symmetric Tree (LeetCode 101), which uses the identical pairing to compare.
 *
 * RUN
 *   main() runs 4 cases (3-level perfect tree, single node, 4-level tree so a deeper odd level
 *   is exercised, and a 2-level tree) and prints the level order actual vs expected.
 */

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringJoiner;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}

class ReverseOddLevelsOfBinaryTree {

    public TreeNode reverseOddLevels(TreeNode root) {
        if (root == null) return null;   // LeetCode guarantees a node, but do not crash on null
        // depth 0 of the recursion is level 1 of the tree - the first level that gets reversed.
        swapMirroredPairs(root.left, root.right, 0);
        return root;
    }

    private void swapMirroredPairs(TreeNode leftChild, TreeNode rightChild, int depth) {
        if (leftChild == null || rightChild == null) return;

        // depth even  =>  tree level odd  =>  this pair must trade values.
        if (depth % 2 == 0) {
            int temp = leftChild.val;
            leftChild.val = rightChild.val;
            rightChild.val = temp;
        }

        // Mirrored pairing: outermost with outermost, innermost with innermost.
        swapMirroredPairs(leftChild.left, rightChild.right, depth + 1);
        swapMirroredPairs(leftChild.right, rightChild.left, depth + 1);
    }

    // ---------- test scaffolding ----------

    /** Perfect tree holding 1..nodeCount, laid out heap-style (children of i are 2i+1, 2i+2). */
    private static TreeNode buildPerfectTree(int nodeCount) {
        TreeNode[] nodes = new TreeNode[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            nodes[i] = new TreeNode(i + 1);
        }
        for (int i = 0; i < nodeCount; i++) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            if (left < nodeCount) nodes[i].left = nodes[left];
            if (right < nodeCount) nodes[i].right = nodes[right];
        }
        return nodes[0];
    }

    /** Level order as a single space-separated string, so a case fits on one printed line. */
    private static String levelOrder(TreeNode root) {
        StringJoiner joiner = new StringJoiner(" ");
        if (root == null) return joiner.toString();

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode current = queue.poll();
            joiner.add(String.valueOf(current.val));
            if (current.left != null) queue.add(current.left);
            if (current.right != null) queue.add(current.right);
        }
        return joiner.toString();
    }

    public static void main(String[] args) {
        ReverseOddLevelsOfBinaryTree solution = new ReverseOddLevelsOfBinaryTree();

        print("case 1 three levels",
                levelOrder(solution.reverseOddLevels(buildPerfectTree(7))),
                "1 3 2 4 5 6 7");

        print("case 2 single node ",
                levelOrder(solution.reverseOddLevels(buildPerfectTree(1))),
                "1");

        print("case 3 four levels ",
                levelOrder(solution.reverseOddLevels(buildPerfectTree(15))),
                "1 3 2 4 5 6 7 15 14 13 12 11 10 9 8");

        print("case 4 two levels  ",
                levelOrder(solution.reverseOddLevels(buildPerfectTree(3))),
                "1 3 2");
    }

    private static void print(String label, String actual, String expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

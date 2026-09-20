/*
 * =====================================================================
 *  Binary Tree Level Order Traversal        LeetCode 102 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, return its node values level by level,
 *   left to right, as a list of lists (one inner list per depth).
 *   The tree may be empty; values are ordinary ints, nothing is sorted.
 *
 * EXAMPLE
 *         1
 *        / \
 *       2   3          ->  [[1], [2, 3], [4, 5, 6]]
 *      / \    \
 *     4   5    6
 *
 *   root = null        ->  []            (empty tree, main() runs this)
 *   root = single node ->  [[42]]
 *
 * APPROACH  (BFS with a level-size loop)
 *   1. If the root is null, return the empty list immediately.
 *   2. Push the root into a queue.
 *   3. While the queue is non-empty:
 *        a. Snapshot levelSize = queue.size() BEFORE touching the queue.
 *           That count is exactly the nodes of the current level.
 *        b. Poll exactly levelSize nodes, collect their values into one list,
 *           and enqueue each non-null child (they form the next level).
 *        c. Append the collected list to the result.
 *   4. The loop ends when no node enqueued a child, i.e. the last level.
 *
 * KEY INSIGHT
 *   A plain BFS queue mixes two levels together, so you cannot tell where a
 *   level ends. Taking "int levelSize = queue.size()" at the top of each
 *   iteration freezes the boundary: the queue holds one complete level and
 *   nothing else at that moment. Recognise this loop - right view, zigzag,
 *   max level sum, bottom view and "average of levels" are all this template
 *   with one line changed inside the inner for-loop.
 *
 * COMPLEXITY
 *   Time  O(n)  every node is enqueued once and polled once.
 *   Space O(w)  w = widest level; a near-complete tree has w ~ n/2 at the
 *               bottom, so worst case O(n) for the queue plus O(n) output.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Bottom-up level order (LC 107): same loop, then Collections.reverse,
 *     or insert each level at index 0 of a LinkedList.
 *   - Zigzag order (LC 103): flip a boolean per level and reverse that level.
 *   - Do it with DFS instead: pass depth down, and append to result.get(depth),
 *     creating the inner list when depth == result.size().
 *   - Right side view (LC 199): keep only the last value of each level.
 *
 * RUN
 *   main() runs 3 cases (typical tree, empty tree, single node) and prints
 *   actual vs expected.
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class LevelOrderTraversal {

    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            // Freeze the boundary: right now the queue holds exactly one level.
            int levelSize = queue.size();
            List<Integer> currentLevel = new ArrayList<>(levelSize);

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                currentLevel.add(node.val);
                // Children go to the back, so they form the NEXT level's block.
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            result.add(currentLevel);
        }
        return result;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        LevelOrderTraversal solver = new LevelOrderTraversal();

        //        1
        //       / \
        //      2   3
        //     / \   \
        //    4   5   6
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.right = new TreeNode(6);

        print("case 1 typical tree", solver.levelOrder(root), "[[1], [2, 3], [4, 5, 6]]");
        print("case 2 empty tree  ", solver.levelOrder(null), "[]");
        print("case 3 single node ", solver.levelOrder(new TreeNode(42)), "[[42]]");
    }
}

class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int val) {
        this.val = val;
    }
}

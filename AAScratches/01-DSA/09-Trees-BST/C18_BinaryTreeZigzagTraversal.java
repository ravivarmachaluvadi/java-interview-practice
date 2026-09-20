/*
 * =====================================================================
 *  Binary Tree Zigzag Level Order Traversal     LeetCode 103 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Return the node values level by level, but alternate the direction: level 0 (the root)
 *   left to right, level 1 right to left, level 2 left to right, and so on.
 *   The result is a list of lists, one inner list per level. An empty tree gives an empty list.
 *
 * EXAMPLE
 *         3
 *        / \
 *       9   20
 *          /  \
 *         15   7
 *   ->  [[3], [20, 9], [15, 7]]    level 1 is reversed, level 2 is left to right again
 *   root = null      ->  []        empty tree, no levels at all
 *   root = single 1  ->  [[1]]     one level, direction never matters
 *
 * APPROACH  (level-order BFS with an alternating direction flag)
 *   1. Standard BFS: push the root, then repeat while the queue is non-empty.
 *   2. At the top of each round snapshot levelSize = queue.size(). Those are exactly the nodes
 *      of the current level, so the inner for-loop processes one level and nothing else.
 *   3. Keep a boolean leftToRight. Append normally when it is true, insert at index 0 when false.
 *   4. Push each node's children (always left then right - the queue order never changes),
 *      add the finished level to the result, flip the flag.
 *
 * KEY INSIGHT
 *   The traversal does NOT change direction. The queue is always filled left to right; only the
 *   way the level is written down flips. Separating "how the tree is walked" from "how the level
 *   is recorded" is the whole trick, and it is why this is the plain level-order template with a
 *   single toggle added. Recognise it for right view, level averages, and level sums too.
 *
 * COMPLEXITY
 *   Time  O(n) for zigzagLevelOrderDeque; zigzagLevelOrderInsertFirst is O(n * w) in the worst
 *         case because ArrayList.add(0, x) shifts the whole level (w = widest level)
 *   Space O(w)  the queue holds at most one full level, which is up to n/2 nodes
 *
 * INTERVIEW FOLLOW-UPS
 *   - "Which is better, add(0, x) or collect then reverse?" Both are shown below; the ArrayDeque
 *     addFirst version is O(1) per node, the ArrayList insert version is O(level size) per node.
 *   - Bottom-up zigzag (LeetCode 107 style): build the same list, then reverse the outer list.
 *   - Do it with two stacks instead of a queue - then the walk direction really does alternate,
 *     and children must be pushed in the opposite order on alternate levels.
 *   - Vertical order traversal (LeetCode 987) swaps the level key for a horizontal-distance key.
 *
 * RUN
 *   main() runs 4 cases (typical 3-level tree, empty tree, single node, left-skewed tree)
 *   against both implementations and prints actual vs expected.
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class BinaryTreeZigzagTraversal {

    /** The author's version: append left to right, but insert at the front on odd levels. */
    public static List<List<Integer>> zigzagLevelOrderInsertFirst(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();      // snapshot: exactly the nodes of this level
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode current = queue.poll();
                if (leftToRight) {
                    level.add(current.val);
                } else {
                    level.add(0, current.val); // O(level size) shift - see the deque version below
                }
                // Children always go in left-then-right; only the recording order flips.
                if (current.left != null) queue.offer(current.left);
                if (current.right != null) queue.offer(current.right);
            }
            result.add(level);
            leftToRight = !leftToRight;
        }
        return result;
    }

    /**
     * Same answer, O(1) per node: write the level into a deque and pick the end to write at.
     * This is the version to give when the interviewer asks about the cost of add(0, x).
     */
    public static List<List<Integer>> zigzagLevelOrderDeque(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            Deque<Integer> level = new ArrayDeque<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode current = queue.poll();
                if (leftToRight) {
                    level.addLast(current.val);
                } else {
                    level.addFirst(current.val);
                }
                if (current.left != null) queue.offer(current.left);
                if (current.right != null) queue.offer(current.right);
            }
            result.add(new ArrayList<>(level));
            leftToRight = !leftToRight;
        }
        return result;
    }

    public static void main(String[] args) {
        //     3
        //    / \
        //   9   20
        //      /  \
        //     15   7
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);

        // 1 -> 2 -> 3 down the left spine: one node per level, so direction is invisible.
        TreeNode skewed = new TreeNode(1);
        skewed.left = new TreeNode(2);
        skewed.left.left = new TreeNode(3);

        System.out.println("-- insert-at-front version --");
        print("case 1 typical    ", zigzagLevelOrderInsertFirst(root), "[[3], [20, 9], [15, 7]]");
        print("case 2 empty tree ", zigzagLevelOrderInsertFirst(null), "[]");
        print("case 3 single node", zigzagLevelOrderInsertFirst(new TreeNode(1)), "[[1]]");
        print("case 4 left skewed", zigzagLevelOrderInsertFirst(skewed), "[[1], [2], [3]]");

        System.out.println("-- deque version (same answers, O(1) per node) --");
        print("case 5 typical    ", zigzagLevelOrderDeque(root), "[[3], [20, 9], [15, 7]]");
        print("case 6 empty tree ", zigzagLevelOrderDeque(null), "[]");
        print("case 7 single node", zigzagLevelOrderDeque(new TreeNode(1)), "[[1]]");
        print("case 8 left skewed", zigzagLevelOrderDeque(skewed), "[[1], [2], [3]]");
    }

    private static void print(String label, Object actual, String expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
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

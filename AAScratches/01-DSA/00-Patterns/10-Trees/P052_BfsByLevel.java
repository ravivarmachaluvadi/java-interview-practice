/*
 * =====================================================================
 *  P052 Tree BFS Level by Level   Canonical LC 102 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 102, Binary Tree Level Order Traversal)
 *   Return the node values level by level, left to right.
 *
 * EXAMPLE
 *   [3,9,20,null,null,15,7]  ->  [[3],[9,20],[15,7]]
 *   [1]                      ->  [[1]]
 *   []                       ->  []
 *
 * RECOGNIZE WHEN
 *   - Anything "per level / per depth / per row": right view, zigzag, averages, widest
 *     level, connect next pointers.
 *   - "minimum depth", "nearest leaf": BFS reaches the shallowest answer first.
 *   Not this if: the answer combines children's results (height, diameter, path sums) ->
 *   P053_BottomUpDfs; you carry a path from the root -> P054_TopDownDfsPath.
 *
 * TEMPLATE
 *   queue = [root]
 *   while queue:
 *       size = queue.size()                 // freeze the size: exactly this level's nodes
 *       for i in 0..size-1:
 *           node = queue.poll()
 *           use node (i == 0 is leftmost, i == size - 1 is rightmost)
 *           queue.add(children)
 *       finish the level (store list, sum, max, ...)
 *
 * APPROACH
 *   1. The queue starts with the root.
 *   2. Each round, read the current queue size and pop exactly that many nodes: they are
 *      one level. Their children form the next level.
 *
 * KEY INSIGHT
 *   A FIFO queue visits nodes in distance order from the root. Snapshotting the queue size
 *   at the start of each round draws a clean line between levels without storing depths.
 *
 * COMPLEXITY
 *   Time O(n), space O(w) where w is the widest level (up to n / 2).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 199  Right Side View          record the LAST node of each level
 *   [coded] LC 103  Zigzag Level Order       reverse every other level (or add at the front)
 *   [coded] LC 111  Minimum Depth            return at the FIRST leaf you pop
 *   [coded] LC 637  Average of Levels        sum / size per level, in long
 *           LC 515  Largest Value per Row    max per level
 *           LC 1161 Max Level Sum            index of the level with the largest sum
 *           LC 116  Populating Next Pointers link each node to the next one in its level
 *           LC 662  Max Width                queue of (node, index); width = last - first + 1
 *           LC 513  Bottom Left Value        first node of the last level
 *
 * PITFALLS
 *   - Read queue.size() ONCE before the inner loop; the queue grows while you pop.
 *   - LC 111: a node with one child is not a leaf; DFS that takes min(left, right) gets
 *     this wrong, BFS stops at the first real leaf.
 *   - Return an empty list (not [[]]) for an empty tree.
 *
 * DEEP DIVE
 *   A03_LevelOrderTraversal, C18_RightView, C20_BinaryTreeZigzagTraversal,
 *   C17_MaxLevelSum (09-Trees-BST)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

class BfsByLevel {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val) {
            this.val = val;
        }

        // Builds a tree from LeetCode's level-order list (null = missing child).
        static TreeNode of(Integer... v) {
            if (v.length == 0 || v[0] == null) {
                return null;
            }
            TreeNode root = new TreeNode(v[0]);
            Deque<TreeNode> queue = new ArrayDeque<>();
            queue.add(root);
            int i = 1;
            while (i < v.length) {
                TreeNode n = queue.poll();
                if (v[i] != null) {
                    n.left = new TreeNode(v[i]);
                    queue.add(n.left);
                }
                i++;
                if (i < v.length && v[i] != null) {
                    n.right = new TreeNode(v[i]);
                    queue.add(n.right);
                }
                i++;
            }
            return root;
        }
    }

    // Canonical LC 102.
    static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> out = new ArrayList<>();
        if (root == null) {
            return out;
        }
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode n = queue.poll();
                level.add(n.val);
                if (n.left != null) {
                    queue.add(n.left);
                }
                if (n.right != null) {
                    queue.add(n.right);
                }
            }
            out.add(level);
        }
        return out;
    }

    // LC 199.
    static List<Integer> rightSideView(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        for (List<Integer> level : levelOrder(root)) {
            out.add(level.get(level.size() - 1));
        }
        return out;
    }

    // LC 103.
    static List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> levels = levelOrder(root);
        for (int d = 1; d < levels.size(); d += 2) {
            Collections.reverse(levels.get(d));
        }
        return levels;
    }

    // LC 111: the first leaf popped is the shallowest.
    static int minDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        int depth = 1;
        while (true) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode n = queue.poll();
                if (n.left == null && n.right == null) {
                    return depth;
                }
                if (n.left != null) {
                    queue.add(n.left);
                }
                if (n.right != null) {
                    queue.add(n.right);
                }
            }
            depth++;
        }
    }

    // LC 637.
    static List<Double> averageOfLevels(TreeNode root) {
        List<Double> out = new ArrayList<>();
        for (List<Integer> level : levelOrder(root)) {
            long sum = 0;
            for (int v : level) {
                sum += v;
            }
            out.add((double) sum / level.size());
        }
        return out;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        Integer[] t = {3, 9, 20, null, null, 15, 7};
        check("LC 102 three levels", levelOrder(TreeNode.of(t)), "[[3], [9, 20], [15, 7]]");
        check("LC 102 single", levelOrder(TreeNode.of(1)), "[[1]]");
        check("LC 102 empty", levelOrder(TreeNode.of()), "[]");

        check("LC 199 [1,2,3,null,5,null,4]",
                rightSideView(TreeNode.of(1, 2, 3, null, 5, null, 4)), "[1, 3, 4]");
        check("LC 199 left branch deeper",
                rightSideView(TreeNode.of(1, 2, 3, 4, null, null, null, 5)),
                "[1, 3, 4, 5]");

        check("LC 103 zigzag", zigzagLevelOrder(TreeNode.of(t)), "[[3], [20, 9], [15, 7]]");

        check("LC 111 [3,9,20,null,null,15,7]", minDepth(TreeNode.of(t)), 2);
        check("LC 111 one-sided chain",
                minDepth(TreeNode.of(2, null, 3, null, 4, null, 5, null, 6)), 5);

        check("LC 637 averages", averageOfLevels(TreeNode.of(t)), "[3.0, 14.5, 11.0]");
        check("LC 637 big values", averageOfLevels(TreeNode.of(2147483647, 2147483647, 2147483647)),
                "[2.147483647E9, 2.147483647E9]");
    }
}

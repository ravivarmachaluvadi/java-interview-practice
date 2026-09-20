/*
 * =====================================================================
 *  Binary Tree Right Side View             LeetCode 199 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Standing to the right of a binary tree, return the values you can see,
 *   top to bottom - i.e. the rightmost node of every depth. "Rightmost" means
 *   last in left-to-right order at that depth, NOT "keep taking right children":
 *   a node reached only through left pointers is visible if nothing sits to its
 *   right on that level. The tree may be empty.
 *
 * EXAMPLE
 *         1
 *       /   \
 *      2     3        right view -> [1, 3, 4]
 *       \     \       left  view -> [1, 2, 5]
 *        5     4
 *
 *   1 -> left 2 -> left 3 (no right children anywhere)  ->  [1, 2, 3]
 *   root = null                                         ->  []
 *
 * APPROACH A  (DFS, right child first, record once per depth)
 *   1. Recurse carrying the current depth, starting at 0.
 *   2. If depth == result.size(), this is the FIRST node seen at that depth,
 *      so append its value. The list length doubles as "deepest level seen".
 *   3. Recurse right BEFORE left, which guarantees the first node reached at
 *      any depth is the rightmost one.
 *
 * APPROACH B  (BFS, last node of each level)
 *   1. Standard queue loop with levelSize = queue.size() frozen per level.
 *   2. Inside the level loop, record the value when i == levelSize - 1.
 *   3. Changing that test to i == 0 gives the LEFT view for free - included
 *      here as leftViewBfs to make the symmetry obvious.
 *
 * KEY INSIGHT
 *   Two different engines, one idea: pick exactly one node per depth. BFS knows
 *   the depth boundary from the queue size; DFS knows it from comparing depth
 *   with result.size(). The "depth == result.size()" trick is the reusable one -
 *   it is how any DFS records a first-per-level fact without a level map.
 *   Interviewers almost always ask for the second solution after the first, so
 *   be able to write both.
 *
 * COMPLEXITY
 *   Time  O(n) for both - every node is visited exactly once.
 *   Space DFS  O(h) recursion stack (h = height; O(n) if skewed).
 *         BFS  O(w) queue (w = widest level; up to n/2 near the bottom).
 *         Output is O(h) either way - one value per level.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Left side view: BFS with i == 0, or DFS recursing left before right.
 *   - Top / bottom view: needs a horizontal-distance coordinate, not depth.
 *   - Boundary traversal (left boundary + leaves + reversed right boundary).
 *   - "Why does DFS work here?" - because right-first ordering makes the first
 *     visit at each depth the rightmost; reversing the order flips the answer.
 *
 * RUN
 *   main() runs 3 cases (typical tree, empty tree, left-skewed tree) through
 *   BOTH implementations and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}

class RightView {

    // ---------- Approach A: DFS, right child first ----------

    public static List<Integer> rightSideViewDfs(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        dfs(root, result, 0);
        return result;
    }

    private static void dfs(TreeNode node, List<Integer> result, int depth) {
        if (node == null) return;

        // result.size() is "how many levels have been recorded so far", so this
        // is true only for the FIRST node reached at this depth.
        if (depth == result.size()) {
            result.add(node.val);
        }

        // Right before left, so the first node reached at a depth is the rightmost.
        dfs(node.right, result, depth + 1);
        dfs(node.left, result, depth + 1);
    }

    // ---------- Approach B: BFS, last node of each level ----------

    public static List<Integer> rightSideViewBfs(TreeNode root) {
        return bfsView(root, true);
    }

    public static List<Integer> leftViewBfs(TreeNode root) {
        return bfsView(root, false);
    }

    /** One level-order loop; takeLast picks the right view, otherwise the left view. */
    private static List<Integer> bfsView(TreeNode root, boolean takeLast) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size(); // freeze the level boundary
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();

                boolean visible = takeLast ? (i == levelSize - 1) : (i == 0);
                if (visible) {
                    result.add(node.val);
                }

                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
        }
        return result;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        //       1
        //     /   \
        //    2     3
        //     \     \
        //      5     4
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.right = new TreeNode(5);
        root.right.right = new TreeNode(4);

        print("case 1 right DFS   ", rightSideViewDfs(root), "[1, 3, 4]");
        print("case 1 right BFS   ", rightSideViewBfs(root), "[1, 3, 4]");
        print("case 1 left  BFS   ", leftViewBfs(root), "[1, 2, 5]");

        print("case 2 empty DFS   ", rightSideViewDfs(null), "[]");
        print("case 2 empty BFS   ", rightSideViewBfs(null), "[]");

        // Tricky: no right children at all, yet every node is visible from the right.
        TreeNode skewed = new TreeNode(1);
        skewed.left = new TreeNode(2);
        skewed.left.left = new TreeNode(3);
        print("case 3 skewed DFS  ", rightSideViewDfs(skewed), "[1, 2, 3]");
        print("case 3 skewed BFS  ", rightSideViewBfs(skewed), "[1, 2, 3]");
    }
}

/*
 * =====================================================================
 *  Add One Row to Tree                            LeetCode 623 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, a value v and a depth d (the root is depth 1), insert a
 *   new row of nodes holding v at depth d. Every node that was at depth d-1 gets two fresh
 *   children: its old left subtree hangs under the new LEFT node, its old right subtree under
 *   the new RIGHT node. If d == 1 the whole tree becomes the left child of a brand new root.
 *
 * EXAMPLE
 *   v = 1, d = 2
 *         4                 4
 *        / \               / \
 *       2   6     ->      1   1
 *      / \  /            /     \
 *     3   1 5           2       6
 *                      / \     /
 *                     3   1   5
 *   level order  [4,2,6,3,1,5]  ->  [4,1,1,2,null,null,6,3,1,5]
 *   v = 1, d = 1 on [4,2,null,3,1]  ->  [1,4,null,2,null,3,1]   (old tree goes LEFT)
 *   v = 9, d = 5 on a 2-level tree  ->  unchanged: no node sits at depth 4
 *
 * APPROACH  (DFS carrying the current depth)
 *   1. Handle d == 1 first: build a new root holding v, hang the old tree on its left, return
 *      the new root. There is no parent to rewire, so the root itself has to change.
 *   2. Otherwise walk down, passing currentDepth (root = 1).
 *   3. When currentDepth == d - 1 you are standing on a parent of the new row. Save the two
 *      existing children, give the node two brand new v nodes, and re-attach: old left under
 *      newLeft.left, old right under newRight.right. Do NOT recurse below this node.
 *   4. Above depth d - 1, just recurse into both children. A null node returns immediately,
 *      which is what makes a d deeper than the tree a silent no-op.
 *   addOneRowBfs() is the same idea with the level-size queue loop: stop the BFS while the
 *   queue holds exactly the depth d-1 nodes, then rewire every node in it.
 *
 * KEY INSIGHT
 *   The work happens at depth d-1, not at depth d, because only a parent can re-point a child
 *   reference. Save both old children BEFORE overwriting node.left, or the second assignment
 *   loses the subtree the first one was meant to keep. Side matters: the old left subtree must
 *   stay on the left of the new row, the old right on the right.
 *
 * COMPLEXITY
 *   Time  O(n)  every node above depth d is visited once, nothing is revisited
 *   Space O(h)  recursion stack (the BFS version uses O(w), w = widest level)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it iteratively: BFS to level d-1, then rewire (addOneRowBfs below)
 *   - Why is d == 1 special-cased, and why does the old tree go left and not right?
 *   - Remove the row you just added: at depth d-1, replace each child with its same-side child
 *   - Insert only under nodes matching a predicate: same walk, one extra condition
 *
 * RUN
 *   main() runs 4 cases (typical, d == 1, row under the leaves, d past the tree) through BOTH
 *   the recursive and the BFS versions and prints actual vs expected level order.
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}

class AddRowToTree {

    // v = value for the new row, d = depth the new row lands on (root is depth 1).
    public TreeNode addOneRow(TreeNode root, int v, int d) {
        if (d == 1) {
            // No parent exists to rewire, so the root itself is replaced.
            TreeNode newRoot = new TreeNode(v);
            newRoot.left = root;
            return newRoot;
        }

        insertAtDepth(root, v, d, 1);
        return root;
    }

    private void insertAtDepth(TreeNode node, int v, int d, int currentDepth) {
        if (node == null) return;   // tree shorter than d-1: nothing to do on this branch

        if (currentDepth == d - 1) {
            // Grab both children first; overwriting node.left would otherwise lose node.right's
            // partner in the next line if the two were ever read out of order.
            TreeNode oldLeft = node.left;
            TreeNode oldRight = node.right;

            node.left = new TreeNode(v);
            node.left.left = oldLeft;     // old left subtree stays on the left

            node.right = new TreeNode(v);
            node.right.right = oldRight;  // old right subtree stays on the right
            return;                       // the subtree below is already correct
        }

        // Still above the target row: keep descending.
        insertAtDepth(node.left, v, d, currentDepth + 1);
        insertAtDepth(node.right, v, d, currentDepth + 1);
    }

    // Same result without recursion: BFS until the queue holds the depth d-1 nodes.
    public TreeNode addOneRowBfs(TreeNode root, int v, int d) {
        if (d == 1) {
            TreeNode newRoot = new TreeNode(v);
            newRoot.left = root;
            return newRoot;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        if (root != null) queue.add(root);

        int depth = 1;
        while (!queue.isEmpty() && depth < d - 1) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
            depth++;
        }

        // The queue now holds every node at depth d-1 (empty if the tree never reaches it).
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            TreeNode oldLeft = node.left;
            TreeNode oldRight = node.right;

            node.left = new TreeNode(v);
            node.left.left = oldLeft;

            node.right = new TreeNode(v);
            node.right.right = oldRight;
        }
        return root;
    }

    public static void main(String[] args) {
        AddRowToTree solver = new AddRowToTree();

        // case 1: typical, new row under the root
        run(solver, "case 1 typical d=2    ", AddRowToTree::buildTypical, 1, 2,
                "[4, 1, 1, 2, null, null, 6, 3, 1, 5]");

        // case 2: edge, d == 1 replaces the root and the old tree becomes its LEFT child
        run(solver, "case 2 new root d=1   ", AddRowToTree::buildLeftLeaning, 1, 1,
                "[1, 4, null, 2, null, 3, 1]");

        // case 3: edge, the new row lands directly under the leaves
        run(solver, "case 3 under leaves   ", AddRowToTree::buildThreeNodes, 9, 3,
                "[1, 2, 3, 9, 9, 9, 9]");

        // case 4: tricky, d is deeper than the tree, so no node sits at depth d-1
        run(solver, "case 4 d past the tree", AddRowToTree::buildThreeNodes, 9, 5,
                "[1, 2, 3]");
    }

    // Both implementations mutate the tree they are handed, so each gets its own fresh build.
    private static void run(AddRowToTree solver, String label, Supplier<TreeNode> tree, int v,
                            int d, String expected) {
        String dfs = serialize(solver.addOneRow(tree.get(), v, d));
        String bfs = serialize(solver.addOneRowBfs(tree.get(), v, d));
        System.out.println(label + " dfs: " + dfs + "   expected " + expected);
        System.out.println(label + " bfs: " + bfs + "   expected " + expected);
    }

    //      4
    //     / \
    //    2   6
    //   / \  /
    //  3   1 5
    private static TreeNode buildTypical() {
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(6);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(1);
        root.right.left = new TreeNode(5);
        return root;
    }

    //      4
    //     /
    //    2
    //   / \
    //  3   1
    private static TreeNode buildLeftLeaning() {
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(1);
        return root;
    }

    //    1
    //   / \
    //  2   3
    private static TreeNode buildThreeNodes() {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        return root;
    }

    // Level order with nulls, trailing nulls trimmed: the LeetCode array form.
    private static String serialize(TreeNode root) {
        if (root == null) return "[]";

        List<String> cells = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();   // LinkedList, because it accepts nulls
        queue.add(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node == null) {
                cells.add("null");
                continue;
            }
            cells.add(String.valueOf(node.val));
            queue.add(node.left);
            queue.add(node.right);
        }

        while (!cells.isEmpty() && cells.get(cells.size() - 1).equals("null")) {
            cells.remove(cells.size() - 1);
        }
        return cells.toString();
    }
}

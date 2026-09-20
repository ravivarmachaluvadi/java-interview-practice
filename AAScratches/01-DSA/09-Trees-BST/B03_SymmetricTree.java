/*
 * =====================================================================
 *  Symmetric Tree                                         LeetCode 101 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, return true if the tree is a mirror of itself around its
 *   centre, i.e. the left subtree is the mirror image of the right subtree. An empty tree and a
 *   single node are both symmetric.
 *
 * EXAMPLE
 *         1                         1
 *        / \                       / \
 *       2   2     -> true         2   2     -> false   (both 3s hang on the right)
 *      / \ / \                     \   \
 *     3  4 4  3                     3   3
 *   []  -> true        [1]  -> true
 *
 * APPROACH  (mirrored parallel recursion)
 *   1. isSymmetric: empty tree is symmetric; otherwise return isMirror(root.left, root.right).
 *   2. isMirror(a, b): both null -> true; exactly one null -> false; values differ -> false.
 *   3. Recurse crosswise: isMirror(a.left, b.right) AND isMirror(a.right, b.left).
 *
 * KEY INSIGHT
 *   This is SameTree with the child pairing crossed. SameTree pairs left-with-left; here you
 *   pair outer-with-outer and inner-with-inner. The traversal is identical, only WHICH two
 *   children you compare changes. The iterative version makes that explicit: a queue of node
 *   pairs where you enqueue (a.left, b.right) and then (a.right, b.left).
 *
 * COMPLEXITY
 *   Time  O(n)  every node is compared once
 *   Space O(h)  recursion depth is the height (O(n) for a skewed tree); the queue version is O(w)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it iteratively - see isSymmetricIterative.
 *   - Why "inorder equals its reverse" is NOT a valid test (shape can differ with equal values).
 *   - Invert a copy of the tree and compare with the original (works, costs an extra tree).
 *
 * RUN
 *   main() runs 4 cases (symmetric, asymmetric with equal values, empty, single node) through
 *   both methods and prints actual vs expected.
 */

import java.util.ArrayDeque;
import java.util.Deque;

class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int x) {
        val = x;
    }
}

class SymmetricTree {

    public boolean isSymmetric(TreeNode root) {
        if (root == null) return true;
        return isMirror(root.left, root.right);
    }

    /** True when the two subtrees are mirror images of each other. */
    private boolean isMirror(TreeNode left, TreeNode right) {
        if (left == null && right == null) return true;
        if (left == null || right == null) return false;
        return left.val == right.val
                && isMirror(left.left, right.right)   // outer pair
                && isMirror(left.right, right.left);  // inner pair
    }

    /** Same pairing, driven by a queue of node pairs instead of recursion. */
    public boolean isSymmetricIterative(TreeNode root) {
        if (root == null) return true;
        Deque<TreeNode> queue = new ArrayDeque<>();
        // ArrayDeque rejects null, so wrap pairs in a tiny array
        Deque<TreeNode[]> pairs = new ArrayDeque<>();
        pairs.add(new TreeNode[]{root.left, root.right});
        while (!pairs.isEmpty()) {
            TreeNode[] pair = pairs.poll();
            TreeNode a = pair[0], b = pair[1];
            if (a == null && b == null) continue;
            if (a == null || b == null || a.val != b.val) return false;
            pairs.add(new TreeNode[]{a.left, b.right});
            pairs.add(new TreeNode[]{a.right, b.left});
        }
        return true;
    }

    // ---------------------------------------------------------------- test helpers

    /** Builds a tree from a LeetCode-style level-order array; null means "no node here". */
    static TreeNode fromLevelOrder(Integer... vals) {
        if (vals.length == 0 || vals[0] == null) return null;
        TreeNode root = new TreeNode(vals[0]);
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        int i = 1;
        while (!queue.isEmpty() && i < vals.length) {
            TreeNode node = queue.poll();
            if (i < vals.length && vals[i] != null) {
                node.left = new TreeNode(vals[i]);
                queue.add(node.left);
            }
            i++;
            if (i < vals.length && vals[i] != null) {
                node.right = new TreeNode(vals[i]);
                queue.add(node.right);
            }
            i++;
        }
        return root;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        SymmetricTree sol = new SymmetricTree();

        //       1
        //      / \
        //     2   2
        //    / \ / \
        //   3  4 4  3
        TreeNode symmetric = fromLevelOrder(1, 2, 2, 3, 4, 4, 3);
        print("case 1 symmetric, recursive       ", sol.isSymmetric(symmetric), true);
        print("case 1 symmetric, iterative       ", sol.isSymmetricIterative(symmetric), true);

        //       1
        //      / \
        //     2   2
        //      \    \
        //       3    3
        TreeNode lopsided = fromLevelOrder(1, 2, 2, null, 3, null, 3);
        print("case 2 equal values, wrong shape  ", sol.isSymmetric(lopsided), false);
        print("case 2 equal values, iterative    ", sol.isSymmetricIterative(lopsided), false);

        print("case 3 empty tree                 ", sol.isSymmetric(null), true);
        print("case 4 single node                ", sol.isSymmetric(fromLevelOrder(7)), true);
        print("case 4 single node, iterative     ",
                sol.isSymmetricIterative(fromLevelOrder(7)), true);
    }
}

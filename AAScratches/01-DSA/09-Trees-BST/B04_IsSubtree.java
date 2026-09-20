/*
 * =====================================================================
 *  Subtree of Another Tree                                LeetCode 572 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given the roots of two binary trees root and subRoot, return true if some node of root has
 *   a subtree identical (same shape, same values) to subRoot. A subtree is a node plus ALL of
 *   its descendants, so matching only the top part is not enough. subRoot has at least one node.
 *
 * EXAMPLE
 *   root = [3,4,5,1,2], subRoot = [4,1,2]                       -> true   node 4's subtree matches
 *   root = [3,4,5,1,2,null,null,null,null,0], subRoot = [4,1,2] -> false
 *       node 4 has an extra leaf 0
 *   root = [], subRoot = [1]                                    -> false  nothing to search
 *   root = [1,2,3], subRoot = [1,2,3]                           -> true   the whole tree matches
 *
 * APPROACH  (SameTree run at every node)
 *   1. isSubtree(root, subRoot): empty root -> false, since subRoot is never empty.
 *   2. If checkSame(root, subRoot), the match starts at this node -> true.
 *   3. Otherwise try the left child OR the right child as the starting point.
 *   4. checkSame is the lockstep comparison from SameTree: both null / one null / value mismatch
 *      as base cases, then recurse on both child pairs.
 *
 * KEY INSIGHT
 *   An outer traversal picks a candidate start node, an inner comparison checks it fully. The
 *   classic trap is the "extra leaf" case: the top of node 4 looks right, but the subtree must
 *   match down to the leaves, which is exactly why checkSame compares nulls too.
 *
 * COMPLEXITY
 *   Time  O(n * m)  n nodes in root, m in subRoot; every candidate start may cost up to O(m)
 *   Space O(h)      recursion depth of the outer walk plus the inner comparison
 *
 * INTERVIEW FOLLOW-UPS
 *   - Get to O(n + m): serialise both trees in preorder with null markers and delimiters, then
 *     run KMP to find subRoot's string inside root's string.
 *   - Alternative: hash every subtree bottom-up (Merkle style) and compare hashes.
 *   - What changes if a partial (top-only) match were allowed? The null checks in checkSame.
 *
 * RUN
 *   main() runs 4 cases (typical match, extra-leaf trap, empty root, whole tree) and prints
 *   actual vs expected.
 */

import java.util.ArrayDeque;
import java.util.Deque;

class IsSubtree {

    public static boolean isSubtree(TreeNode root, TreeNode subRoot) {
        if (root == null) return false;             // ran out of candidate start nodes
        if (checkSame(root, subRoot)) return true;  // match starts here
        return isSubtree(root.left, subRoot) || isSubtree(root.right, subRoot);
    }

    /** SameTree: both subtrees identical in shape and values. */
    public static boolean checkSame(TreeNode a, TreeNode b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;   // one has a node where the other has none
        if (a.val != b.val) return false;
        return checkSame(a.left, b.left) && checkSame(a.right, b.right);
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
        TreeNode sub = fromLevelOrder(4, 1, 2);

        //       3
        //      / \
        //     4   5
        //    / \
        //   1   2
        print("case 1 [3,4,5,1,2] contains [4,1,2]     ",
                isSubtree(fromLevelOrder(3, 4, 5, 1, 2), sub), true);

        //       3
        //      / \
        //     4   5
        //    / \
        //   1   2
        //      /
        //     0          <- extra leaf under 2 breaks the match
        print("case 2 extra leaf under node 2          ",
                isSubtree(fromLevelOrder(3, 4, 5, 1, 2, null, null, null, null, 0), sub), false);

        print("case 3 empty root                       ", isSubtree(null, sub), false);

        print("case 4 whole tree equals subRoot        ",
                isSubtree(fromLevelOrder(1, 2, 3), fromLevelOrder(1, 2, 3)), true);
    }
}

class TreeNode {
    TreeNode left, right;
    int val;

    TreeNode(int val) {
        this.val = val;
    }
}

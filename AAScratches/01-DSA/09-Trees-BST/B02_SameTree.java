/*
 * =====================================================================
 *  Same Tree                                              LeetCode 100 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given the roots of two binary trees, return true if they are structurally identical and
 *   every corresponding node holds the same value. Two empty trees count as the same.
 *
 * EXAMPLE
 *   [1,2,3]  vs  [1,2,3]      ->  true
 *   [1,2]    vs  [1,null,2]   ->  false   same values, different shape
 *   [1,2,3]  vs  [1,3]        ->  false   left child differs, right child missing
 *   []       vs  []           ->  true
 *
 * APPROACH  (parallel two-tree recursion)
 *   1. Both nodes null: this position matches, return true.
 *   2. Exactly one null: the shapes differ, return false.
 *   3. Values differ: return false.
 *   4. Otherwise recurse on (p.left, q.left) and (p.right, q.right); both must be true.
 *
 * KEY INSIGHT
 *   Walk the two trees in lockstep and compare position by position. The three base cases
 *   (both null / one null / value mismatch) ARE the problem; the recursion just repeats them at
 *   every position. This lockstep helper is what SymmetricTree and IsSubtree reuse, only the
 *   pairing of children changes there.
 *
 * COMPLEXITY
 *   Time  O(min(n, m))  stops at the first mismatch, otherwise visits every node of the smaller
 *   Space O(min(hp, hq)) recursion depth is bounded by the shallower tree's height
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it iteratively with a queue of node pairs.
 *   - Symmetric tree: same helper, but pair left with right (LeetCode 101).
 *   - Subtree of another tree: run this check from every node (LeetCode 572).
 *
 * RUN
 *   main() runs 4 cases (identical, same values different shape, differing values, both empty)
 *   and prints actual vs expected.
 */

import java.util.ArrayDeque;
import java.util.Deque;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}

class SameTreeExample {

    public static boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null && q == null) return true;   // both ran out together: match
        if (p == null || q == null) return false;  // one ran out early: shapes differ
        if (p.val != q.val) return false;
        return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
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
        print("case 1 [1,2,3] vs [1,2,3]    ",
                isSameTree(fromLevelOrder(1, 2, 3), fromLevelOrder(1, 2, 3)), true);

        // same values, but 2 hangs left in one tree and right in the other
        print("case 2 [1,2] vs [1,null,2]   ",
                isSameTree(fromLevelOrder(1, 2), fromLevelOrder(1, null, 2)), false);

        print("case 3 [1,2,3] vs [1,3]      ",
                isSameTree(fromLevelOrder(1, 2, 3), fromLevelOrder(1, 3)), false);

        print("case 4 [] vs []              ", isSameTree(null, null), true);
    }
}

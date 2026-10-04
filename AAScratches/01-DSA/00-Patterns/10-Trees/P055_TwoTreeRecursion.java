/*
 * =====================================================================
 *  P055 Tree Recursion on Two Trees at Once   Canonical LC 100 | Easy
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 100, Same Tree)
 *   Return whether two binary trees have the same shape and the same values.
 *
 * EXAMPLE
 *   [1,2,3] vs [1,2,3]     ->  true
 *   [1,2]   vs [1,null,2]  ->  false     same values, different shape
 *   [1,2,1] vs [1,1,2]     ->  false
 *
 * RECOGNIZE WHEN
 *   - Compare, mirror, merge or match TWO trees (or two subtrees of one tree): same tree,
 *     symmetric, subtree, flip-equivalent, merge.
 *   - Transform a tree in place by recursing on both children (invert / mirror).
 *   Not this if: one tree and a value flowing down or up -> P054_TopDownDfsPath or
 *   P053_BottomUpDfs.
 *
 * TEMPLATE
 *   same(a, b):
 *       if a == null and b == null: return true
 *       if a == null or  b == null: return false        // shapes differ
 *       return a.val == b.val and same(a.left, b.left) and same(a.right, b.right)
 *   mirror: compare a.left with b.right and a.right with b.left
 *
 * APPROACH
 *   1. Two empty trees are equal; one empty and one not are different.
 *   2. Otherwise the roots must match and both pairs of children must match.
 *
 * KEY INSIGHT
 *   Walk both trees in lock-step with ONE recursion over pairs of nodes. Handling the two
 *   null cases first makes every later line safe to dereference, and changing which
 *   children you pair (left-left vs left-right) turns "same" into "mirror".
 *
 * COMPLEXITY
 *   Time O(min(n, m)) for same / symmetric; O(n * m) for LC 572 (O(n + m) with hashing or
 *   serialisation). Space O(h).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 101  Symmetric Tree           mirror(root.left, root.right)
 *   [coded] LC 572  Subtree of Another Tree  same(node, sub) for every node of the big tree
 *   [coded] LC 226  Invert Binary Tree       swap the children, recurse on both
 *           LC 617  Merge Two Binary Trees   new node with a.val + b.val; recurse on pairs
 *           LC 951  Flip Equivalent Trees    (same order) OR (swapped order) at each pair
 *           LC 1367 Linked List in Tree      LC 572 where the small "tree" is a list
 *
 * PITFALLS
 *   - Compare values only after both nodes are known to be non-null.
 *   - LC 572 needs the WHOLE subtree to match, down to the leaves, not just a prefix.
 *   - Invert: save one child before overwriting it (or swap with a temp).
 *
 * DEEP DIVE
 *   B02_SameTree, B03_SymmetricTree, B04_IsSubtree, B01_InvertTree (09-Trees-BST)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

class TwoTreeRecursion {

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

        // Level-order list with trailing nulls trimmed, as LeetCode prints trees.
        static String levels(TreeNode root) {
            List<String> out = new ArrayList<>();
            LinkedList<TreeNode> queue = new LinkedList<>();
            queue.add(root);
            while (!queue.isEmpty()) {
                TreeNode n = queue.poll();
                out.add(n == null ? "null" : String.valueOf(n.val));
                if (n != null) {
                    queue.add(n.left);
                    queue.add(n.right);
                }
            }
            while (!out.isEmpty() && out.get(out.size() - 1).equals("null")) {
                out.remove(out.size() - 1);
            }
            return "[" + String.join(",", out) + "]";
        }
    }

    // Canonical LC 100.
    static boolean isSameTree(TreeNode a, TreeNode b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.val == b.val && isSameTree(a.left, b.left) && isSameTree(a.right, b.right);
    }

    // LC 101.
    static boolean isSymmetric(TreeNode root) {
        return root == null || isMirror(root.left, root.right);
    }

    private static boolean isMirror(TreeNode a, TreeNode b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.val == b.val && isMirror(a.left, b.right) && isMirror(a.right, b.left);
    }

    // LC 572.
    static boolean isSubtree(TreeNode root, TreeNode sub) {
        if (root == null) {
            return sub == null;
        }
        return isSameTree(root, sub) || isSubtree(root.left, sub) || isSubtree(root.right, sub);
    }

    // LC 226.
    static TreeNode invertTree(TreeNode node) {
        if (node == null) {
            return null;
        }
        TreeNode left = node.left;
        node.left = invertTree(node.right);
        node.right = invertTree(left);
        return node;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 100 identical", isSameTree(TreeNode.of(1, 2, 3), TreeNode.of(1, 2, 3)), true);
        check("LC 100 different shape",
                isSameTree(TreeNode.of(1, 2), TreeNode.of(1, null, 2)), false);
        check("LC 100 different values",
                isSameTree(TreeNode.of(1, 2, 1), TreeNode.of(1, 1, 2)), false);
        check("LC 100 both empty", isSameTree(TreeNode.of(), TreeNode.of()), true);

        check("LC 101 symmetric", isSymmetric(TreeNode.of(1, 2, 2, 3, 4, 4, 3)), true);
        check("LC 101 not symmetric", isSymmetric(TreeNode.of(1, 2, 2, null, 3, null, 3)), false);

        check("LC 572 match", isSubtree(TreeNode.of(3, 4, 5, 1, 2), TreeNode.of(4, 1, 2)), true);
        TreeNode deeper = TreeNode.of(3, 4, 5, 1, 2, null, null, null, null, 0);
        check("LC 572 extra node below",
                isSubtree(deeper, TreeNode.of(4, 1, 2)),
                false);

        check("LC 226 seven nodes", TreeNode.levels(invertTree(TreeNode.of(4, 2, 7, 1, 3, 6, 9))),
                "[4,7,2,9,6,3,1]");
        check("LC 226 [2,1,3]", TreeNode.levels(invertTree(TreeNode.of(2, 1, 3))), "[2,3,1]");
        check("LC 226 empty", TreeNode.levels(invertTree(TreeNode.of())), "[]");
    }
}

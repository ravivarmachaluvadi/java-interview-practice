/*
 * =====================================================================
 *  P058 Lowest Common Ancestor   Canonical LC 236 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 236, Lowest Common Ancestor of a Binary Tree)
 *   Given a binary tree (not a BST) and two nodes p and q that both exist in it, return
 *   their lowest common ancestor. A node is an ancestor of itself.
 *
 * EXAMPLE
 *   [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1  ->  3
 *   [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 4  ->  5     p is q's ancestor
 *   [1,2], p = 1, q = 2                          ->  1
 *
 * RECOGNIZE WHEN
 *   - "lowest common ancestor", "first shared manager", "smallest region containing both",
 *     "path between two nodes" (it goes up to the LCA and back down).
 *   - Distance between two nodes = depth(p) + depth(q) - 2 * depth(LCA).
 *   Not this if: the tree is a BST -> use the ordering (P056_BstProperty, LC 235).
 *
 * TEMPLATE
 *   lca(node, p, q):
 *       if node is null or node == p or node == q: return node
 *       L = lca(node.left, p, q); R = lca(node.right, p, q)
 *       if L and R: return node               // p and q are on different sides
 *       return L if L else R                  // pass up whichever side found something
 *   parent pointers: like LC 160 -- walk up from both, switching to the other start
 *
 * APPROACH
 *   1. Each call reports "the LCA if both are below me, else whichever of p / q is below me".
 *   2. The first node that hears back from BOTH sides is the LCA.
 *
 * KEY INSIGHT
 *   One post-order pass answers it: a subtree returns null (nothing found), p or q (one
 *   found), or the LCA (both found). The node where two non-null answers meet is the
 *   answer. Returning early at p is safe ONLY because q is guaranteed to exist.
 *
 * COMPLEXITY
 *   Time O(n), space O(h). Parent-pointer version O(h) time, O(1) space.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 1644 LCA II (may not exist)   do not stop at p / q; count how many were
 *                                            found and return null unless both were
 *   [coded] LC 1650 LCA III (parent links)   two pointers walking up, switching start:
 *                                            the intersection of two lists (P034_GapPointers)
 *   [coded] LC 1676 LCA IV (many nodes)      return node if it is in the target SET
 *           LC 2096 Step-By-Step Directions  LCA, then 'U' * depth(start) + path to dest
 *           LC 1123 LCA of Deepest Leaves    bottom-up (depth, lca) pairs
 *           LC 1483 Kth Ancestor of a Node   binary lifting: up[j][v] = 2^j-th ancestor
 *           LC 1257 Smallest Common Region   parent map + LC 1650
 *
 * PITFALLS
 *   - The early return at p is wrong when q might not be in the tree (LC 1644).
 *   - Compare NODES (references), not values, if values can repeat.
 *   - Recursion depth on skewed trees: mention the iterative parent-map version.
 *
 * DEEP DIVE
 *   C12_LCA, C13_LowestCommonAncestorOfABinaryTreeII, C15_LowestCommonAncestorOfaBinaryTreeIII,
 *   C14_LowestCommonAncestorIV, C16_SmallestCommonRegion (09-Trees-BST)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

class LowestCommonAncestor {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode parent;

        TreeNode(int val) {
            this.val = val;
        }

        // Builds a tree from LeetCode's level-order list and links parent pointers.
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
                    n.left.parent = n;
                    queue.add(n.left);
                }
                i++;
                if (i < v.length && v[i] != null) {
                    n.right = new TreeNode(v[i]);
                    n.right.parent = n;
                    queue.add(n.right);
                }
                i++;
            }
            return root;
        }

        // The node holding value v anywhere in the tree, or null.
        static TreeNode find(TreeNode root, int v) {
            if (root == null || root.val == v) {
                return root;
            }
            TreeNode left = find(root.left, v);
            return left != null ? left : find(root.right, v);
        }
    }

    // Canonical LC 236.
    static TreeNode lowestCommonAncestor(TreeNode node, TreeNode p, TreeNode q) {
        if (node == null || node == p || node == q) {
            return node;
        }
        TreeNode left = lowestCommonAncestor(node.left, p, q);
        TreeNode right = lowestCommonAncestor(node.right, p, q);
        if (left != null && right != null) {
            return node;
        }
        return left != null ? left : right;
    }

    private static int found;

    // LC 1644: p or q may be missing; visit everything and count.
    static TreeNode lowestCommonAncestorII(TreeNode root, TreeNode p, TreeNode q) {
        found = 0;
        TreeNode answer = search(root, p, q);
        return found == 2 ? answer : null;
    }

    private static TreeNode search(TreeNode node, TreeNode p, TreeNode q) {
        if (node == null) {
            return null;
        }
        TreeNode left = search(node.left, p, q);
        TreeNode right = search(node.right, p, q);
        if (node == p || node == q) {
            found++;
            return node;                           // checked AFTER the children: no early stop
        }
        if (left != null && right != null) {
            return node;
        }
        return left != null ? left : right;
    }

    // LC 1650: nodes know their parents; walk up like two linked lists.
    static TreeNode lowestCommonAncestorIII(TreeNode p, TreeNode q) {
        TreeNode a = p;
        TreeNode b = q;
        while (a != b) {
            a = a == null ? q : a.parent;
            b = b == null ? p : b.parent;
        }
        return a;
    }

    // LC 1676: LCA of a whole set of nodes (all present).
    static TreeNode lowestCommonAncestorIV(TreeNode node, Set<TreeNode> targets) {
        if (node == null || targets.contains(node)) {
            return node;
        }
        TreeNode left = lowestCommonAncestorIV(node.left, targets);
        TreeNode right = lowestCommonAncestorIV(node.right, targets);
        if (left != null && right != null) {
            return node;
        }
        return left != null ? left : right;
    }

    static Integer val(TreeNode n) {
        return n == null ? null : n.val;
    }

    static Set<TreeNode> nodes(TreeNode root, int... values) {
        Set<TreeNode> set = new HashSet<>();
        for (int v : values) {
            set.add(TreeNode.find(root, v));
        }
        return set;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        TreeNode t = TreeNode.of(3, 5, 1, 6, 2, 0, 8, null, null, 7, 4);
        TreeNode n5 = TreeNode.find(t, 5);
        TreeNode n1 = TreeNode.find(t, 1);
        TreeNode n4 = TreeNode.find(t, 4);
        TreeNode n6 = TreeNode.find(t, 6);
        check("LC 236 p=5 q=1", val(lowestCommonAncestor(t, n5, n1)), 3);
        check("LC 236 p=5 q=4", val(lowestCommonAncestor(t, n5, n4)), 5);
        check("LC 236 p=6 q=4", val(lowestCommonAncestor(t, n6, n4)), 5);
        TreeNode small = TreeNode.of(1, 2);
        check("LC 236 [1,2]", val(lowestCommonAncestor(small, small, small.left)), 1);

        TreeNode stranger = new TreeNode(10);
        check("LC 1644 p=5 q=1", val(lowestCommonAncestorII(t, n5, n1)), 3);
        check("LC 1644 p=5 q=4", val(lowestCommonAncestorII(t, n5, n4)), 5);
        check("LC 1644 q missing", val(lowestCommonAncestorII(t, n5, stranger)), null);

        check("LC 1650 p=5 q=1", val(lowestCommonAncestorIII(n5, n1)), 3);
        check("LC 1650 p=5 q=4", val(lowestCommonAncestorIII(n5, n4)), 5);

        check("LC 1676 {4,7}", val(lowestCommonAncestorIV(t, nodes(t, 4, 7))), 2);
        check("LC 1676 {1}", val(lowestCommonAncestorIV(t, nodes(t, 1))), 1);
        check("LC 1676 {7,6,2,4}", val(lowestCommonAncestorIV(t, nodes(t, 7, 6, 2, 4))), 5);
    }
}

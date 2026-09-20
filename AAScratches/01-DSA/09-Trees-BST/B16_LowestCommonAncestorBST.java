/*
 * =====================================================================
 *  Lowest Common Ancestor of a BST            LeetCode 235 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a BST and two nodes p and q that both exist in it, return their
 *   lowest common ancestor: the deepest node that has both p and q as descendants.
 *   A node counts as its own descendant, so if p is an ancestor of q the answer is p.
 *   All values are distinct.
 *
 * EXAMPLE
 *   Tree: 6 (2 (0, 4 (3, 5)), 8 (7, 9))
 *   p = 2, q = 8  ->  6    they sit on opposite sides of the root
 *   p = 2, q = 4  ->  2    p is itself an ancestor of q
 *   p = 3, q = 5  ->  4    both under 4, on different sides of it
 *   p = 0, q = 9  ->  6    the two extremes straddle the root
 *
 * APPROACH  (walk down to the split point using value comparison)
 *   1. Start at the root.
 *   2. If both p and q are smaller than the node, the LCA is in the left subtree: go left.
 *   3. If both are larger, go right.
 *   4. Otherwise the values straddle the node (or one equals it): this node is the LCA.
 *   The file has the iterative version (a while loop) and the recursive version;
 *   they are the same three comparisons.
 *
 * KEY INSIGHT
 *   In a BST "both are in the left subtree" is decided by comparing values, not by
 *   searching. The first node whose value lies between p and q (inclusive) is where
 *   their two root paths diverge, and that is the LCA by definition. Contrast with a
 *   general binary tree (LeetCode 236), where you must recurse into both children and
 *   combine what comes back.
 *
 * COMPLEXITY
 *   Time  O(h)   one root-to-node walk, h = tree height (log n balanced, n skewed)
 *   Space O(1)   iterative; O(h) recursive for the call stack
 *
 * INTERVIEW FOLLOW-UPS
 *   - General binary tree (LC 236): postorder recursion, return the non-null side or self.
 *   - p or q may be missing (LC 1644): this code would return a wrong node; verify existence.
 *   - Parent pointers available (LC 1650): two-pointer walk up, like linked-list intersection.
 *   - LCA of a set of nodes (LC 1676): compare each node with the min and max of the set.
 *
 * RUN
 *   main() runs 4 cases (straddle root, one node is the ancestor, deep pair, extremes)
 *   through both methods and prints actual vs expected.
 */
class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int val) {
        this.val = val;
    }
}

class LowestCommonAncestorBST {

    /** Iterative: descend until p and q are no longer on the same side of the node. */
    public static TreeNode lowestCommonAncestorIterative(TreeNode root, TreeNode p, TreeNode q) {
        while (root != null) {
            if (p.val < root.val && q.val < root.val) {
                root = root.left;          // both smaller: LCA is somewhere on the left
            } else if (p.val > root.val && q.val > root.val) {
                root = root.right;         // both larger: LCA is somewhere on the right
            } else {
                return root;               // split point (or root equals p or q)
            }
        }
        return null;
    }

    /** Recursive: identical comparisons, recursion replaces the loop. */
    public static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) return null;

        if (p.val < root.val && q.val < root.val) {
            return lowestCommonAncestor(root.left, p, q);
        }
        if (p.val > root.val && q.val > root.val) {
            return lowestCommonAncestor(root.right, p, q);
        }
        // one on each side, or one of them is this node: this is the LCA
        return root;
    }

    public static void main(String[] args) {
        /*
                    6
                   / \
                  2   8
                 / \ / \
                0  4 7  9
                  / \
                 3   5
        */
        TreeNode root = new TreeNode(6);
        root.left = new TreeNode(2);
        root.right = new TreeNode(8);
        root.left.left = new TreeNode(0);
        root.left.right = new TreeNode(4);
        root.left.right.left = new TreeNode(3);
        root.left.right.right = new TreeNode(5);
        root.right.left = new TreeNode(7);
        root.right.right = new TreeNode(9);

        TreeNode n0 = root.left.left;
        TreeNode n2 = root.left;
        TreeNode n3 = root.left.right.left;
        TreeNode n4 = root.left.right;
        TreeNode n5 = root.left.right.right;
        TreeNode n8 = root.right;
        TreeNode n9 = root.right.right;

        check("case 1 p=2 q=8 (straddle root)   ", root, n2, n8, 6);
        check("case 2 p=2 q=4 (p is ancestor)   ", root, n2, n4, 2);
        check("case 3 p=3 q=5 (deep pair)       ", root, n3, n5, 4);
        check("case 4 p=0 q=9 (extremes)        ", root, n0, n9, 6);
    }

    private static void check(String label, TreeNode root, TreeNode p, TreeNode q, int expected) {
        int recursive = lowestCommonAncestor(root, p, q).val;
        int iterative = lowestCommonAncestorIterative(root, p, q).val;
        System.out.println(label + ": recursive=" + recursive + " iterative=" + iterative
                + "   expected " + expected);
    }
}

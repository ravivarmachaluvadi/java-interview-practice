/*
 * =====================================================================
 *  Lowest Common Ancestor of a Binary Tree   LeetCode 236 | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree and two nodes p and q, return their lowest common
 *   ancestor: the deepest node that has both p and q somewhere in its subtree. A node
 *   counts as an ancestor of itself. This is a PLAIN binary tree - there is no ordering
 *   to exploit and no parent pointers. The problem guarantees both p and q exist.
 *
 *   Fixed: main() called the solver with three bare "new TreeNode()" objects, which did
 *   not even compile (TreeNode only has an int constructor). It now builds a real tree.
 *
 * EXAMPLE
 *            3
 *          /   \
 *         5     1        lca(5, 1)  ->  3   they split at the root
 *        / \   / \       lca(5, 4)  ->  5   5 is an ancestor of itself
 *       6   2 0   8      lca(7, 4)  ->  2   both sit under 2
 *          / \           lca(6, 6)  ->  6   p == q
 *         7   4          lca on an empty tree  ->  null
 *
 * APPROACH  (postorder search, propagate the non-null answer upward)
 *   1. Base case: if the node is null, or IS p, or IS q, return it. Returning early at p
 *      is what makes "a node is its own ancestor" fall out for free.
 *   2. Otherwise recurse into both subtrees. Each call answers "did you find p or q, or
 *      the answer itself, down there?" and hands back a node or null.
 *   3. If BOTH sides came back non-null, p and q are in different subtrees, so THIS node
 *      is the split point - it is the LCA. Return it.
 *   4. If only one side is non-null, everything interesting lives on that side. Pass that
 *      value straight up unchanged.
 *   5. If both are null, return null.
 *
 * KEY INSIGHT
 *   The return value is deliberately overloaded: it means "p or q" low in the tree and "the
 *   LCA" once the split has happened - and the code never has to tell the two apart, because
 *   once a node sees two non-null children it can only be the split point. That single
 *   trick is why this is four lines instead of a path-comparison algorithm. It works ONLY
 *   because both nodes are guaranteed present; if q is missing the same code happily
 *   returns p (see the LCA II variant, which adds found-flags to fix exactly this).
 *   Identity (==) is compared, not value, so duplicate values do not confuse it.
 *
 * COMPLEXITY
 *   Time  O(n)  every node is visited at most once
 *   Space O(h)  recursion stack, h = height, O(n) for a skewed tree
 *
 * INTERVIEW FOLLOW-UPS
 *   - LCA of a BST (LC 235): no recursion needed - descend until p and q straddle the node
 *   - LCA II (LC 1644): p or q may be absent - count how many targets you actually found
 *   - LCA III (LC 1650): parent pointers, no root - becomes linked-list intersection
 *   - LCA IV (LC 1676): a whole set of nodes - the same recursion with a set membership test
 *   - Many queries on one fixed tree? Preprocess with binary lifting for O(log n) per query
 *
 * RUN
 *   main() runs 5 cases (split at root, ancestor of itself, deep split, p == q, empty)
 *   and prints actual vs expected.
 */
class LCA {

    public static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        // Base case: nothing here, or we just bumped into one of the targets.
        // Returning p or q on sight is what makes a node its own ancestor.
        if (root == null || root == p || root == q) return root;

        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        // Targets found on both sides => this node is where they split => it is the LCA.
        if (left != null && right != null) return root;

        // Otherwise pass up whichever side found something (or null if neither did).
        return (left != null) ? left : right;
    }

    public static void main(String[] args) {
        //            3
        //          /   \
        //         5     1
        //        / \   / \
        //       6   2 0   8
        //          / \
        //         7   4
        TreeNode root = new TreeNode(3);
        TreeNode n5 = new TreeNode(5);
        TreeNode n1 = new TreeNode(1);
        TreeNode n6 = new TreeNode(6);
        TreeNode n2 = new TreeNode(2);
        TreeNode n0 = new TreeNode(0);
        TreeNode n8 = new TreeNode(8);
        TreeNode n7 = new TreeNode(7);
        TreeNode n4 = new TreeNode(4);

        root.left = n5;
        root.right = n1;
        n5.left = n6;
        n5.right = n2;
        n1.left = n0;
        n1.right = n8;
        n2.left = n7;
        n2.right = n4;

        print("case 1 split at root  ", lowestCommonAncestor(root, n5, n1), 3);
        print("case 2 own ancestor   ", lowestCommonAncestor(root, n5, n4), 5);
        print("case 3 deep split     ", lowestCommonAncestor(root, n7, n4), 2);
        print("case 4 p == q         ", lowestCommonAncestor(root, n6, n6), 6);
        print("case 5 empty tree     ", lowestCommonAncestor(null, n5, n1), "null");
    }

    private static void print(String label, TreeNode actual, Object expected) {
        String shown = (actual == null) ? "null" : String.valueOf(actual.val);
        System.out.println(label + ": " + shown + "   expected " + expected);
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}

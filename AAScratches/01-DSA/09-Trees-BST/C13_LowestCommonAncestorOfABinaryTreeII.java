/*
 * =====================================================================
 *  Lowest Common Ancestor of a Binary Tree II         LeetCode 1644 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree and two node references p and q, return their lowest
 *   common ancestor. Unlike LC236, p or q may NOT exist in the tree; if either is missing,
 *   return null. All node values are unique.
 *
 * EXAMPLE
 *   tree = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 4      ->  5     (5 is an ancestor of 4)
 *   tree = [3,5,1,6,2,0,8,null,null,7,4], p = 7, q = 8      ->  3
 *   tree = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 42     ->  null  (42 is not in the tree)
 *
 * APPROACH  (LC236 postorder recursion + existence flags)
 *   1. Run the classic LC236 recursion: return the node if it is p or q, else combine the
 *      left and right results (both non-null -> this node is the LCA; else pass up the one
 *      that is non-null).
 *   2. Crucially, recurse into BOTH children BEFORE testing "is this node p or q". That way
 *      the whole tree is always visited, and a target hiding under the other target is seen.
 *   3. Set pFound / qFound when the matching node is visited.
 *   4. After the recursion, return the candidate only if both flags are true, else null.
 *   Fixed: the flags are instance fields and were never reset between calls, so a second
 *   query on the same solver reused stale "found" values and returned 5 instead of null.
 *
 * KEY INSIGHT
 *   LC236 is only correct because it ASSUMES both nodes exist: it returns early the moment it
 *   sees p, never checking whether q is really below. Drop that assumption and you must
 *   (a) keep traversing after a hit, and (b) carry proof of existence up separately from the
 *   candidate node. Pattern: when a recursion's shortcut relies on a guarantee, removing the
 *   guarantee usually means "visit everything and add a flag".
 *
 * COMPLEXITY
 *   Time  O(n)  every node is visited exactly once (no early exit)
 *   Space O(h)  recursion depth; O(n) for a skewed tree
 *
 * INTERVIEW FOLLOW-UPS
 *   - LC236: both nodes guaranteed present -> early return on first hit is safe.
 *   - LC1650: nodes carry a parent pointer -> two-runner trick, no root needed.
 *   - LC1676: LCA of an arbitrary set of nodes -> same recursion with a HashSet membership test.
 *   - Can you return a single object instead of using mutable fields? Yes: return a small
 *     record {candidate, foundP, foundQ} from the recursion.
 *
 * RUN
 *   main() runs 3 cases (typical, ancestor-is-target, missing node) and prints actual vs
 *   expected.
 */

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}

class LowestCommonAncestorOfABinaryTreeII {
    private boolean pFound;
    private boolean qFound;

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        // Reset per query: the flags are instance state and would otherwise leak between calls.
        pFound = false;
        qFound = false;
        TreeNode candidate = lca(root, p, q);
        return pFound && qFound ? candidate : null;
    }

    private TreeNode lca(TreeNode node, TreeNode p, TreeNode q) {
        if (node == null) return null;

        // Recurse first so the whole tree is always covered, even below a matching node.
        TreeNode left = lca(node.left, p, q);
        TreeNode right = lca(node.right, p, q);

        if (node == p) {
            pFound = true;
            return node;
        }
        if (node == q) {
            qFound = true;
            return node;
        }
        if (left != null && right != null) return node; // targets split here: this is the LCA
        return left != null ? left : right;              // pass the single hit upward
    }

    // ---- test scaffolding --------------------------------------------------------------

    private static String show(TreeNode n) {
        return n == null ? "null" : String.valueOf(n.val);
    }

    private static void print(String label, TreeNode actual, String expected) {
        System.out.println(label + ": " + show(actual) + "   expected " + expected);
    }

    public static void main(String[] args) {
        LowestCommonAncestorOfABinaryTreeII sol = new LowestCommonAncestorOfABinaryTreeII();

        //         3
        //        / \
        //       5   1
        //      / \   \
        //     6   2   8
        //        / \
        //       7   4
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(5);
        root.right = new TreeNode(1);
        root.left.left = new TreeNode(6);
        root.left.right = new TreeNode(2);
        root.left.right.left = new TreeNode(7);
        root.left.right.right = new TreeNode(4);
        root.right.right = new TreeNode(8);

        TreeNode n5 = root.left;
        TreeNode n4 = root.left.right.right;
        TreeNode n7 = root.left.right.left;
        TreeNode n8 = root.right.right;
        TreeNode notInTree = new TreeNode(42);

        // case 1: one target is an ancestor of the other
        print("case 1 (5, 4)", sol.lowestCommonAncestor(root, n5, n4), "5");
        // case 2: targets in different subtrees, LCA is the root
        print("case 2 (7, 8)", sol.lowestCommonAncestor(root, n7, n8), "3");
        // case 3: q is not in the tree; must return null, even after earlier successful calls
        print("case 3 (5, 42)", sol.lowestCommonAncestor(root, n5, notInTree), "null");
    }
}

/*
 * =====================================================================
 *  Balanced Binary Tree                                  LeetCode 110 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, return true if it is height-balanced: at EVERY node the
 *   heights of the left and right subtrees differ by at most 1. An empty tree is balanced.
 *
 * EXAMPLE
 *   [1,2,3,4,5,null,6]           ->  true    every node's children differ in height by <= 1
 *   [1,2,null,3,null,4]          ->  false   left chain of depth 3, right side empty
 *   []                           ->  true    empty tree is balanced by definition
 *   [1,2,2,3,null,null,3,4,null,null,4]  ->  false   the root's two subtrees have equal
 *                                                     height (3), so a root-only check
 *                                                     passes, but node 2 on the left has
 *                                                     heights 2 vs 0
 *
 * APPROACH  (height recursion with a -1 failure sentinel)
 *   1. checkHeight(node) returns the height of the subtree, or -1 if it is unbalanced.
 *   2. Base case: null has height 0.
 *   3. Compute the left height; if it is -1, return -1 immediately (short-circuit).
 *   4. Same for the right height.
 *   5. If |left - right| > 1 this node is the culprit: return -1.
 *   6. Otherwise return 1 + max(left, right), the normal height.
 *   7. isBalanced(root) is simply checkHeight(root) != -1.
 *
 * KEY INSIGHT
 *   The naive answer calls height() at every node and re-walks each subtree: O(n^2).
 *   Encoding "unbalanced" as the impossible height -1 lets ONE postorder pass both compute
 *   the height and carry the failure upward. Pattern: when a recursion must return a value
 *   AND a yes/no flag, fold the flag into an out-of-range value instead of a second pass.
 *
 * COMPLEXITY
 *   Time  O(n)  every node is visited once; the sentinel stops work as soon as a failure appears
 *   Space O(h)  recursion stack, h = tree height (O(n) for a degenerate chain)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is the top-down "height(left) vs height(right) at each node" version O(n^2)?
 *   - Can you do it without the sentinel (e.g. return a small Pair(height, balanced))?
 *   - Iterative version: postorder with an explicit stack and a height map.
 *   - Related: Diameter of Binary Tree (B09_DiameterOfBinaryTree) uses the same return-height
 *   skeleton.
 *
 * RUN
 *   main() runs 4 cases (balanced, skewed, empty, subtrees-balanced-but-root-not) and prints
 *   actual vs expected.
 */
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}

class IsBalancedBinaryTree {

    public boolean isBalanced(TreeNode root) {
        return checkHeight(root) != -1;
    }

    // Returns the subtree height, or -1 as soon as any subtree is found unbalanced.
    private int checkHeight(TreeNode node) {
        if (node == null) return 0;

        int leftHeight = checkHeight(node.left);
        if (leftHeight == -1) return -1;           // failure already found below: stop early

        int rightHeight = checkHeight(node.right);
        if (rightHeight == -1) return -1;

        if (Math.abs(leftHeight - rightHeight) > 1) return -1;   // this node breaks the rule

        return Math.max(leftHeight, rightHeight) + 1;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        IsBalancedBinaryTree solver = new IsBalancedBinaryTree();

        // case 1: balanced      1
        //                      / \
        //                     2   3
        //                    / \   \
        //                   4   5   6
        TreeNode balanced = new TreeNode(1);
        balanced.left = new TreeNode(2);
        balanced.right = new TreeNode(3);
        balanced.left.left = new TreeNode(4);
        balanced.left.right = new TreeNode(5);
        balanced.right.right = new TreeNode(6);
        print("case 1 balanced", solver.isBalanced(balanced), true);

        // case 2: left chain 1 -> 2 -> 3 -> 4, nothing on the right
        TreeNode skewed = new TreeNode(1);
        skewed.left = new TreeNode(2);
        skewed.left.left = new TreeNode(3);
        skewed.left.left.left = new TreeNode(4);
        print("case 2 skewed chain", solver.isBalanced(skewed), false);

        // case 3: empty tree
        print("case 3 empty tree", solver.isBalanced(null), true);

        // case 4: [1,2,2,3,null,null,3,4,null,null,4]
        //   root's two subtrees have equal height (3), but node 2 on the left has
        //   left height 2 and right height 0. Only a per-node check catches this.
        TreeNode tricky = new TreeNode(1);
        tricky.left = new TreeNode(2);
        tricky.right = new TreeNode(2);
        tricky.left.left = new TreeNode(3);
        tricky.right.right = new TreeNode(3);
        tricky.left.left.left = new TreeNode(4);
        tricky.right.right.right = new TreeNode(4);
        print("case 4 root heights equal, inner node not", solver.isBalanced(tricky), false);
    }
}

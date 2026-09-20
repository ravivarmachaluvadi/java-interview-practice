/*
 * =====================================================================
 *  Construct BST from Preorder Traversal     LeetCode 1008 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the preorder traversal of a binary search tree with distinct
 *   values, rebuild the tree and return its root. Preorder means every value
 *   appears before all values in its subtrees (root, left, right).
 *
 * EXAMPLE
 *   [8,5,1,7,10,12]  ->  [8,5,10,1,7,null,12]   (level order)
 *   [1,3]            ->  [1,null,3]             3 > 1, so it goes right
 *   [5,4,3,2,1]      ->  a left-leaning chain, in-order 1 2 3 4 5
 *
 * APPROACH  (one pass with a shared index and (lower, upper) bounds)
 *   1. Keep a single cursor `i` into the preorder array. It only ever moves
 *      forward: each value is consumed exactly once, by the node it creates.
 *   2. helper(l, u) asks "does the value at the cursor belong in this slot?".
 *   3. If the cursor is past the end, or preorder[i] falls outside [l, u],
 *      this slot is empty - return null WITHOUT advancing the cursor, so the
 *      value stays available for an ancestor's right subtree.
 *   4. Otherwise take the value, advance the cursor, and recurse: the left
 *      child must be below val, so bounds (l, val); the right child must be
 *      above val, so bounds (val, u).
 *   Fixed: `i` was an instance field that was never reset, so calling
 *   bstFromPreorder twice on the same object returned null the second time.
 *   It is now reset at the start of every call.
 *
 * KEY INSIGHT
 *   This is ValidateBST run backwards. There the (min, max) window checked
 *   whether a value was allowed at a position; here the same window decides
 *   whether a value is *placed* at a position. Recognise the pair: a legality
 *   window that validates can usually be turned around to construct. The
 *   second idea is the non-advancing return - failing to consume the value is
 *   how the recursion unwinds to the right ancestor.
 *
 * COMPLEXITY
 *   Time  O(n)  the cursor never moves backwards, so each value is handled once
 *   Space O(h)  recursion stack, h = height (O(n) for a sorted input chain)
 *
 * INTERVIEW FOLLOW-UPS
 *   - The naive version rescans for the first value above the root at every
 *     node: why is that O(n^2), and on which input?
 *   - Build the same tree from a postorder array (consume from the right).
 *   - A general binary tree needs two traversals (LC 105); one is ambiguous.
 *   - Bounds here are inclusive, so duplicates land on the first branch tried.
 *
 * RUN
 *   main() runs 4 cases (typical, two nodes, single node, sorted-descending)
 *   plus a reuse check, and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}

// 1008. Construct Binary Search Tree from Preorder Traversal
// https://leetcode.com/problems/construct-binary-search-tree-from-preorder-traversal/
class ConstructBinarySearchTreeFromPreorderTraversal {

    private int i = 0; // cursor into the preorder array, shared by all frames

    public TreeNode bstFromPreorder(int[] preorder) {
        i = 0; // reset so the same instance can be reused for another array
        return helper(preorder, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private TreeNode helper(int[] preorder, int l, int u) {
        if (i == preorder.length) {
            return null; // every value has been placed
        }

        int val = preorder[i];

        // The value does not fit this slot, so the slot is empty. Do NOT
        // advance the cursor: some ancestor's right subtree still wants it.
        if (val < l || val > u) {
            return null;
        }
        i++; // this node consumes the value

        TreeNode root = new TreeNode(val);
        root.left = helper(preorder, l, val);  // everything left must be < val
        root.right = helper(preorder, val, u); // everything right must be > val
        return root;
    }

    // ---------------------------------------------------------------
    // Helpers for main
    // ---------------------------------------------------------------

    private static final TreeNode NULL_MARKER = new TreeNode(0);

    /** LeetCode-style level order with nulls, trailing nulls trimmed. */
    private static String levelOrder(TreeNode root) {
        List<String> out = new ArrayList<>();
        if (root != null) {
            Deque<TreeNode> queue = new ArrayDeque<>();
            queue.add(root);
            while (!queue.isEmpty()) {
                TreeNode node = queue.poll();
                if (node == NULL_MARKER) {
                    out.add("null");
                    continue; // markers have no children to enqueue
                }
                out.add(String.valueOf(node.val));
                queue.add(node.left == null ? NULL_MARKER : node.left);
                queue.add(node.right == null ? NULL_MARKER : node.right);
            }
            while (!out.isEmpty() && out.get(out.size() - 1).equals("null")) {
                out.remove(out.size() - 1);
            }
        }
        return "[" + String.join(", ", out) + "]";
    }

    /** In-order of a correctly built BST must come out sorted ascending. */
    private static String inorder(TreeNode root) {
        List<String> out = new ArrayList<>();
        collectInorder(root, out);
        return String.join(" ", out);
    }

    private static void collectInorder(TreeNode node, List<String> out) {
        if (node == null) return;
        collectInorder(node.left, out);
        out.add(String.valueOf(node.val));
        collectInorder(node.right, out);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        ConstructBinarySearchTreeFromPreorderTraversal solution =
                new ConstructBinarySearchTreeFromPreorderTraversal();

        // Case 1 - typical: 8 is the root, 5 and 10 its children.
        TreeNode typical = solution.bstFromPreorder(new int[]{8, 5, 1, 7, 10, 12});
        print("case 1 level order", levelOrder(typical), "[8, 5, 10, 1, 7, null, 12]");
        print("case 1 in-order   ", inorder(typical), "1 5 7 8 10 12");

        // Case 2 - reuse check: the same solver must work a second time.
        // Before the cursor reset this printed [] because i was left at 6.
        TreeNode reused = solution.bstFromPreorder(new int[]{1, 3});
        print("case 2 reuse [1,3]", levelOrder(reused), "[1, null, 3]");

        // Case 3 - edge: a single value is the whole tree.
        print("case 3 single node", levelOrder(solution.bstFromPreorder(new int[]{5})), "[5]");

        // Case 4 - tricky: strictly decreasing input degenerates into a
        // left-leaning chain of height 5, the worst case for the recursion depth.
        TreeNode chain = solution.bstFromPreorder(new int[]{5, 4, 3, 2, 1});
        print("case 4 descending ", levelOrder(chain), "[5, 4, null, 3, null, 2, null, 1]");
        print("case 4 in-order   ", inorder(chain), "1 2 3 4 5");
    }
}

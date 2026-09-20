/*
 * =====================================================================
 *  Convert Sorted Array to Height-Balanced BST   LeetCode 108 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array sorted in ascending order, build a binary search
 *   tree whose in-order traversal reproduces the array and whose left and
 *   right subtree heights never differ by more than one at any node.
 *   Any valid balanced tree is accepted; this file always picks the lower
 *   middle element, so its output is deterministic.
 *
 * EXAMPLE
 *   [-10, -3, 0, 5, 9]  ->  [0, -10, 5, null, -3, null, 9]   (level order)
 *   [1, 2, 3, 4]        ->  [2, 1, 3, null, null, null, 4]
 *   []                  ->  []            (empty input, null root)
 *
 * APPROACH  (divide and conquer: middle element becomes the root)
 *   1. Work on the inclusive window [left, right] of the array.
 *   2. If left > right the window is empty, so return null.
 *   3. mid = left + (right - left) / 2 -- the same overflow-safe midpoint as
 *      binary search. Make nums[mid] the root of this window.
 *   4. Recurse on [left, mid - 1] for the left child and [mid + 1, right]
 *      for the right child, and attach both.
 *
 * KEY INSIGHT
 *   This is the inverse of an in-order traversal. In-order flattens a BST to a
 *   sorted array; choosing the midpoint as the root splits the array into two
 *   halves whose sizes differ by at most one, which is exactly height balance.
 *   Picking the first element instead still gives a valid BST, but a degenerate
 *   one. Pattern to recognise: "sorted input + balance required" -> take the middle.
 *
 * COMPLEXITY
 *   Time  O(n)      each array element creates exactly one node
 *   Space O(log n)  recursion stack, which is the height of a balanced tree
 *                   (the returned tree itself is O(n), not counted as extra)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Sorted linked list instead (LC 109): no random access, so find the middle
 *     with slow/fast pointers, or build bottom-up in in-order sequence.
 *   - Why mid = left + (right - left) / 2 rather than (left + right) / 2?
 *   - Pick the upper middle: still balanced, a different but equally valid tree.
 *   - How would you verify balance? Height recursion returning -1 on violation.
 *
 * RUN
 *   main() runs 4 cases (typical odd length, even length, single element,
 *   empty array) and prints level order plus in-order against expected.
 */

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

class SortedArrayToBST {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val) {
            this.val = val;
        }
    }

    public static TreeNode sortedArrayToBST(int[] nums) {
        return buildBST(nums, 0, nums.length - 1);
    }

    /** Builds the balanced BST for the inclusive window nums[left..right]. */
    private static TreeNode buildBST(int[] nums, int left, int right) {
        if (left > right) {
            return null; // empty window
        }

        // Overflow-safe midpoint; the lower middle for even-sized windows.
        int mid = left + (right - left) / 2;
        TreeNode node = new TreeNode(nums[mid]);

        node.left = buildBST(nums, left, mid - 1);
        node.right = buildBST(nums, mid + 1, right);

        return node;
    }

    // ---------------------------------------------------------------
    // Helpers for printing and checking
    // ---------------------------------------------------------------

    /** In-order traversal as a string; must reproduce the input array. */
    private static String inorder(TreeNode root) {
        List<String> values = new ArrayList<>();
        collectInorder(root, values);
        return String.join(", ", values);
    }

    private static void collectInorder(TreeNode node, List<String> out) {
        if (node == null) {
            return;
        }
        collectInorder(node.left, out);
        out.add(String.valueOf(node.val));
        collectInorder(node.right, out);
    }

    /** LeetCode-style level order with nulls, trailing nulls trimmed. */
    private static String levelOrder(TreeNode root) {
        List<String> out = new ArrayList<>();
        if (root != null) {
            Deque<TreeNode> queue = new ArrayDeque<>();
            queue.add(root);
            // ArrayDeque rejects null, so wrap absent children in a marker node.
            while (!queue.isEmpty()) {
                TreeNode node = queue.poll();
                if (node == NULL_MARKER) {
                    out.add("null");
                    continue;
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

    private static final TreeNode NULL_MARKER = new TreeNode(0);

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1 - typical odd-length array
        TreeNode odd = sortedArrayToBST(new int[]{-10, -3, 0, 5, 9});
        print("case 1 level order", levelOrder(odd), "[0, -10, 5, null, -3, null, 9]");
        print("case 1 in-order   ", inorder(odd), "-10, -3, 0, 5, 9");

        // Case 2 - even length: the lower middle wins, so 2 is the root of [1,2,3,4]
        TreeNode even = sortedArrayToBST(new int[]{1, 2, 3, 4});
        print("case 2 level order", levelOrder(even), "[2, 1, 3, null, null, null, 4]");
        print("case 2 in-order   ", inorder(even), "1, 2, 3, 4");

        // Case 3 - edge: a single element becomes the root with no children
        print("case 3 single elem", levelOrder(sortedArrayToBST(new int[]{7})), "[7]");

        // Case 4 - edge: an empty array must produce a null root, not a crash
        print("case 4 empty array", levelOrder(sortedArrayToBST(new int[]{})), "[]");
    }
}

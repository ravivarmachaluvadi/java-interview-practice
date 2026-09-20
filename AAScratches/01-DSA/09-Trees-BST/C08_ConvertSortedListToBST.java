/*
 * =====================================================================
 *  Convert Sorted List to Binary Search Tree             LeetCode 109 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a singly linked list whose values are in ascending order, build a
 *   height-balanced BST (for every node, the heights of its two subtrees differ by at most 1).
 *   The list has no random access, so you cannot just index the middle element.
 *
 * EXAMPLE
 *   list = -10 -> -3 -> 0 -> 5 -> 9   ->  [0, -3, 9, -10, null, 5]   (level order)
 *   list = 7                          ->  [7]
 *   list = (empty)                    ->  []
 *   list = 1 -> 2                     ->  [2, 1]   (slow/fast picks the second of two)
 *
 * APPROACH  (slow/fast midpoint, then divide and conquer)
 *   1. buildBST(start, end) builds the subtree for the half-open range [start, end):
 *      the nodes from start up to but NOT including end. end == null means "to the tail".
 *   2. If start == end the range is empty: return null (base case).
 *   3. findMiddle moves slow one step and fast two steps until fast hits end; slow is
 *      then the middle node. Make its value the root of this subtree.
 *   4. root.left  = buildBST(start, mid)       everything before mid
 *      root.right = buildBST(mid.next, end)    everything after mid
 *
 * KEY INSIGHT
 *   Middle of a sorted sequence as root gives balance for free, because each half is at
 *   most half the size. With a list instead of an array the midpoint costs a walk, so use
 *   slow/fast pointers, and keep the ranges half-open [start, end) so that the loop test
 *   "fast != end && fast.next != end" stops exactly at the boundary instead of at null.
 *
 * COMPLEXITY
 *   Time  O(n log n)  every recursion level walks all n nodes to find midpoints, log n levels
 *   Space O(log n)    recursion depth of a balanced tree (output tree itself is O(n))
 *
 * INTERVIEW FOLLOW-UPS
 *   - Make it O(n): count the length once, then build in in-order fashion, advancing a
 *     shared head pointer each time a node is "visited" (build left, take head, build right).
 *   - Why not copy the list into an array first? Costs O(n) extra space; fine if allowed
 *     (that is LeetCode 108, Sorted Array to BST).
 *   - How would you verify the output? in-order must equal the input, subtree heights
 *     must differ by at most 1 at every node.
 *
 * RUN
 *   main() runs 4 cases (typical, single node, empty, two nodes) and prints the level-order
 *   form of the BST and its in-order traversal against the expected values.
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class ConvertSortedListToBST {

    public static void main(String[] args) {
        Solution solution = new Solution();

        // case 1: typical, odd length
        TreeNode t1 = solution.sortedListToBST(buildLinkedList(new int[]{-10, -3, 0, 5, 9}));
        print("case 1 level-order", toLevelOrder(t1), "[0, -3, 9, -10, null, 5]");
        print("case 1 in-order   ", toInOrder(t1), "[-10, -3, 0, 5, 9]");

        // case 2: single node
        TreeNode t2 = solution.sortedListToBST(buildLinkedList(new int[]{7}));
        print("case 2 level-order", toLevelOrder(t2), "[7]");

        // case 3: empty list
        TreeNode t3 = solution.sortedListToBST(null);
        print("case 3 level-order", toLevelOrder(t3), "[]");

        // case 4: two nodes, the slow/fast walk picks the second one as root
        TreeNode t4 = solution.sortedListToBST(buildLinkedList(new int[]{1, 2}));
        print("case 4 level-order", toLevelOrder(t4), "[2, 1]");
        print("case 4 in-order   ", toInOrder(t4), "[1, 2]");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    private static ListNode buildLinkedList(int[] arr) {
        if (arr.length == 0) return null;
        ListNode head = new ListNode(arr[0]);
        ListNode current = head;
        for (int i = 1; i < arr.length; i++) {
            current.next = new ListNode(arr[i]);
            current = current.next;
        }
        return head;
    }

    /** LeetCode-style level order with nulls for missing children, trailing nulls trimmed. */
    private static List<Integer> toLevelOrder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node == null) {
                out.add(null);
                continue;
            }
            out.add(node.val);
            queue.add(node.left);
            queue.add(node.right);
        }
        while (!out.isEmpty() && out.get(out.size() - 1) == null) out.remove(out.size() - 1);
        return out;
    }

    private static List<Integer> toInOrder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        inOrder(root, out);
        return out;
    }

    private static void inOrder(TreeNode root, List<Integer> out) {
        if (root == null) return;
        inOrder(root.left, out);
        out.add(root.val);
        inOrder(root.right, out);
    }
}

class Solution {
    public TreeNode sortedListToBST(ListNode head) {
        if (head == null) return null;
        return buildBST(head, null);
    }

    /** Builds the subtree for the half-open range [start, end); end is excluded. */
    private TreeNode buildBST(ListNode start, ListNode end) {
        // empty range: nothing to build
        if (start == end) return null;

        // the middle element becomes the root so both halves have (almost) equal size
        ListNode mid = findMiddle(start, end);
        TreeNode root = new TreeNode(mid.val);

        root.left = buildBST(start, mid);       // nodes before mid
        root.right = buildBST(mid.next, end);   // nodes after mid
        return root;
    }

    /** Slow/fast walk bounded by end (not by null): slow stops on the middle of [start, end). */
    private ListNode findMiddle(ListNode start, ListNode end) {
        ListNode slow = start;
        ListNode fast = start;
        // compare against end, not null, because this range may stop mid-list
        while (fast != end && fast.next != end) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
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

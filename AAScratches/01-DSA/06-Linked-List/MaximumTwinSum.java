/**
 * Problem: Given an even‑length singly linked list, find the maximum sum of any pair of twin nodes.
 * A node at index i is a twin with the node at index n-1-i (0‑based indexing).
 *
 * Approach:
 * 1. Use fast and slow pointers to locate the middle of the list.
 * 2. Reverse the second half of the list in place.
 * 3. Traverse both halves simultaneously, computing sums of corresponding nodes
 *    and tracking the maximum sum.
 *
 * Time Complexity: O(n) – one pass to find the middle, one pass to reverse,
 *                    and one pass to compute sums.
 * Space Complexity: O(1) – only a few pointers are used; no additional data structures.
 */
class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
    }
}
// https://leetcode.com/problems/maximum-twin-sum-of-a-linked-list/description/
// 2130. Maximum Twin Sum of a Linked List

/**
 * In a linked list of size n, where n is even, the ith node (0-indexed) of the linked list is
 * <p>
 * known as the twin of the (n-1-i)th node, if 0 <= i <= (n / 2) - 1.
 */
class MaximumTwinSum {

    public static int pairSum(ListNode head) {
        // Step 1: Find the middle of the linked list
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // Step 2: Reverse the second half of the list
        ListNode secondHalf = reverseList(slow);

        // Step 3: Calculate the twin sum
        int maxSum = 0;
        ListNode firstHalf = head;
        while (secondHalf != null) {
            maxSum = Math.max(maxSum, firstHalf.val + secondHalf.val);
            firstHalf = firstHalf.next;
            secondHalf = secondHalf.next;
        }
        return maxSum;
    }

    // Helper method to reverse a linked list
    private static ListNode reverseList(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode nextNode = head.next;
            head.next = prev;
            prev = head;
            head = nextNode;
        }
        return prev;
    }

    public static void main(String[] args) {
        // Example 1
        ListNode head1 = new ListNode(5);
        head1.next = new ListNode(4);
        head1.next.next = new ListNode(2);
        head1.next.next.next = new ListNode(1);
        System.out.println("Example 1: " + pairSum(head1)); // Output: 6

        // Example 2
        ListNode head2 = new ListNode(1);
        head2.next = new ListNode(100000);
        System.out.println("Example 2: " + pairSum(head2)); // Output: 100001
    }
}

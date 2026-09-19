/**
 * Problem: Reverse a singly linked list (LeetCode 206).
 *
 * Approaches:
 *   1. reverseIterative  - prev/curr/next pointer loop.        O(n) time, O(1) space.
 *   2. reverseRecursive  - reverse the tail, then hook head on. O(n) time, O(n) stack.
 *
 * Interview note: the iterative version is the one to write first; the recursive
 * version is asked as a follow-up ("can you do it recursively?") and its stack
 * depth equals the list length, so it can overflow on very long lists.
 */
class ReverseLinkedList {

    /**
     * Approach 1: Iterative.
     * Walk the list once. At each node, save its successor, point the node
     * backwards to prev, then advance both pointers. When curr falls off the
     * end, prev is the new head.
     *
     *   prev  curr  next            prev  curr  next
     *   null   1  -> 2 -> 3   ==>   null <- 1    2 -> 3
     */
    public ListNode reverseIterative(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;
        while (curr != null) {
            ListNode next = curr.next; // save before we overwrite curr.next
            curr.next = prev;          // flip the pointer
            prev = curr;               // advance prev
            curr = next;               // advance curr
        }
        return prev;
    }

    /**
     * Approach 2: Recursive.
     * Base case: empty list or single node is already reversed.
     * Otherwise reverse everything after head first; head.next is then the
     * LAST node of that reversed tail, so head.next.next = head appends head
     * to the end, and head.next = null makes it the new tail.
     *
     *   head=1, tail 2->3 reversed to 3->2 ; then 2.next = 1, 1.next = null
     *   result 3 -> 2 -> 1
     */
    public ListNode reverseRecursive(ListNode head) {
        if (head == null || head.next == null)
            return head;

        // Reverse the rest of the list; revHead is the new head and never changes
        ListNode revHead = reverseRecursive(head.next);

        // Make the current head the last node
        head.next.next = head;

        // Update the next of current head to NULL
        head.next = null;

        // Return the new head of the reversed list
        return revHead;
    }

    public static void main(String[] args) {
        ReverseLinkedList reverser = new ReverseLinkedList();

        // Each approach mutates the list in place, so build a fresh 1..5 for each run.
        System.out.print("Original list      : ");
        printList(build(1, 2, 3, 4, 5));

        System.out.print("Iterative reverse  : ");
        printList(reverser.reverseIterative(build(1, 2, 3, 4, 5)));

        System.out.print("Recursive reverse  : ");
        printList(reverser.reverseRecursive(build(1, 2, 3, 4, 5)));

        // Edge cases: empty and single-node lists
        System.out.print("Iterative on empty : ");
        printList(reverser.reverseIterative(null));
        System.out.print("Recursive on single: ");
        printList(reverser.reverseRecursive(build(42)));
    }

    /** Builds a list from the given values, e.g. build(1,2,3) -> 1 -> 2 -> 3 */
    private static ListNode build(int... vals) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        for (int v : vals) {
            tail.next = new ListNode(v);
            tail = tail.next;
        }
        return dummy.next;
    }

    private static void printList(ListNode head) {
        if (head == null) {
            System.out.println("(empty)");
            return;
        }
        ListNode current = head;
        while (current != null) {
            System.out.print(current.val + " ");
            current = current.next;
        }
        System.out.println();
    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
    }
}

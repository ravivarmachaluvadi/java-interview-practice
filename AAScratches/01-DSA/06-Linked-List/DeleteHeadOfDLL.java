/**
 * Problem: Remove the first node from a doubly linked list and return the new head.
 *
 * Approach:
 * 1. If the list is empty or has only one node, return null.
 * 2. Detach the original head by moving the head pointer to its next node,
 *    setting the new head's prev reference to null, and clearing the old
 *    head's next reference.
 *
 * Time Complexity: O(1) – constant time operations regardless of list size.
 * Space Complexity: O(1) – no additional data structures are used.
 */
class DeleteHeadOfDLL {
    public ListNode deleteHead(ListNode head) {
        if (head == null || head.next == null) return null;

        ListNode prev = head;
        head = head.next;
        head.prev = null;
        prev.next = null;
        return head;
    }

    public static void main(String[] args) {
        // Build a small doubly linked list: 1 <-> 2 <-> 3
        ListNode n1 = new ListNode();
        ListNode n2 = new ListNode();
        ListNode n3 = new ListNode();
        n1.val = 1; n2.val = 2; n3.val = 3;
        n1.next = n2; n2.prev = n1;
        n2.next = n3; n3.prev = n2;

        // Keep a reference to the original head for printing
        ListNode originalHead = n1;

        // Print input list
        System.out.print("Input list: ");
        ListNode cur = originalHead;
        while (cur != null) {
            System.out.print(cur.val);
            if (cur.next != null) System.out.print(" <-> ");
            cur = cur.next;
        }
        System.out.println();

        // Call the method to delete head
        DeleteHeadOfDLL solver = new DeleteHeadOfDLL();
        ListNode newHead = solver.deleteHead(originalHead);

        // Print output list
        System.out.print("Output list: ");
        cur = newHead;
        while (cur != null) {
            System.out.print(cur.val);
            if (cur.next != null) System.out.print(" <-> ");
            cur = cur.next;
        }
        System.out.println();
    }
}

class ListNode {
    int val;
    ListNode next;
    ListNode prev;
}
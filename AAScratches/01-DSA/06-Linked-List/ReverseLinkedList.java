/**
 * Problem: Reverse a singly linked list.
 *
 * Approach: Iterate through the list, reassigning each node's next pointer to its
 * predecessor while maintaining references to the current and next nodes.
 * After traversal, return the last processed node as the new head.
 *
 * Time Complexity: O(n) – each node is visited once.
 * Space Complexity: O(1) – only a constant amount of extra pointers are used.
 */
class ReverseLinkedList {

    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;
        while (curr != null) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }

    public static void main(String[] args) {
        // Create the linked list: 1 -> 2 -> 3 -> 4 -> 5
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);

        System.out.println("Original list:");
        printList(head);

        ReverseLinkedList reverser = new ReverseLinkedList();
        ListNode reversedHead = reverser.reverseList(head);

        System.out.println("Reversed list:");
        printList(reversedHead);
    }

    private static void printList(ListNode head) {
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

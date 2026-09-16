/**
 * Problem:
 *   Given the head of a singly linked list, return the middle node.
 *   If the list has an even number of nodes, return the first node
 *   of the two middle nodes (i.e., the lower median).
 *
 * Approach:
 *   Use the fast and slow pointer technique.  Both pointers start at the head;
 *   move `fast` two steps and `slow` one step per iteration until `fast`
 *   reaches the end or has no next node.  At that point, `slow` points to
 *   the middle node.
 *
 * Complexity:
 *   Time:  O(n) – each node is visited at most once by the fast pointer.
 *   Space: O(1) – only a constant number of pointers are used.
 */
class Node {
    int data;
    Node next;

    Node(int data) {
        this.data = data;
        this.next = null;
    }
}

class FindMiddleOfLinkedList {

    static Node findMiddle(Node head) {
        Node slow = head;
        Node fast = head;
        // 1->2->3
        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
        }
        return slow;
    }

    public static void main(String[] args) {
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(4);
        head.next.next.next.next = new Node(5);
        head.next.next.next.next.next = new Node(6);

        Node middleNode = findMiddle(head);

        System.out.println("The middle node value is: " + middleNode.data);
    }
}


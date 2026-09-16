// Recursive Java program to reverse a linked list
class Node {
    int data;
    Node next;

    Node(int x) {
        data = x;
        next = null;
    }
}

class ZReverseLinkedListRecursive {

    // Function to reverse the linked list
    static Node reverseList(Node head) {
        if (head == null || head.next == null)
            return head;

        // Reverse the rest of the list
        Node revHead = reverseList(head.next);

        // Make the current head the last node
        head.next.next = head;

        // Update the next of current head to NULL
        head.next = null;

        // Return the new head of the reversed list
        return revHead;
    }

    static void printList(Node curr) {
        while (curr != null) {
            System.out.print(curr.data + " ");
            curr = curr.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // Create a hard-coded linked list:
        // 1 -> 2 -> 3 -> 4 -> 5
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(4);
        head.next.next.next.next = new Node(5);

        head = reverseList(head);
        printList(head);
    }
}

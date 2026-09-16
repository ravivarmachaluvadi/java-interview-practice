// Node class for a Doubly Linked List
class Node {
    int data;
    Node next;
    Node prev;

    public Node(int data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}

class DoublyLinkedListReverse {
    Node head;
    // Function to reverse the doubly linked list using recursion
    public Node reverse(Node node) {
        // Base case: if the list is empty or reached the end of the list
        if (node == null) return null;

        // Swap the next and prev pointers
        Node next = node.next;
        node.next = node.prev;
        node.prev = next;

        // If the previous node (which was next before swapping) is null,
        // we are at the end of the list
        if (node.prev == null) {
            // This node becomes the new head of the reversed list
            return node;
        }

        // Recur for the next node in the original list (which is now node.prev)
        return reverse(node.prev);
    }

    // Utility function to insert a node at the beginning
    public void push(int newData) {
        Node newNode = new Node(newData);
        newNode.next = head;
        if (head != null) {
            head.prev = newNode;
        }
        head = newNode;
    }

    // Function to print the doubly linked list
    public void printList(Node node) {
        while (node != null) {
            System.out.print(node.data + " ");
            node = node.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        DoublyLinkedListReverse dll = new DoublyLinkedListReverse();

        // Add elements to the list
        dll.push(10);
        dll.push(8);
        dll.push(6);
        dll.push(4);
        dll.push(2);

        System.out.println("Original list:");
        dll.printList(dll.head);

        // Reverse the doubly linked list using recursion
        dll.head = dll.reverse(dll.head);

        System.out.println("Reversed list:");
        dll.printList(dll.head);
    }
}

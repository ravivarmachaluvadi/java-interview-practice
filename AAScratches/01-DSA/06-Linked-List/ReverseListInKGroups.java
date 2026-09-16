class Node {
    int data;
    Node next;

    Node(int data) {
        this.data = data;
        this.next = null;
    }
}

class ReverseListInKGroups {
    static Node reverseLinkedList(Node head) {
        Node curr = head;
        Node prev = null;
        while (curr != null) {
            Node next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }

    static Node getKthNode(Node currHead, int k) {
        k -= 1;
        while (currHead != null && k > 0) {
            k--;
            currHead = currHead.next;
        }
        return currHead;
    }

    static Node kReverse(Node head, int k) {
        Node currentGroupHead = head;
        Node tailOfLastGroup = null;

        while (currentGroupHead != null) {
            Node kThNode = getKthNode(currentGroupHead, k);
            if (kThNode == null) {
                if (tailOfLastGroup != null) {
                    // not reversed for last partial group
                    tailOfLastGroup.next = currentGroupHead;
                }
                break;
            }
            Node nextGroupHead = kThNode.next;
            kThNode.next = null;
            reverseLinkedList(currentGroupHead);
            //groupHead reversed
            if (currentGroupHead == head) {
                // assigning updated head for newly formed group reversed list
                head = kThNode;
            } else {
                // tail of last group pointing to new head of next group after reversal
                // kThNode will become head of current reversed group
                tailOfLastGroup.next = kThNode;
            }
            // after reversal head becomes tail , tail becomes head
            tailOfLastGroup = currentGroupHead;
            currentGroupHead = nextGroupHead;
        }
        return head;
    }

    static void printLinkedList(Node head) {
        Node temp = head;
        while (temp != null) {
            System.out.print(temp.data + " ");
            temp = temp.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Node head = new Node(5);
        head.next = new Node(4);
        head.next.next = new Node(3);
        head.next.next.next = new Node(7);
        head.next.next.next.next = new Node(9);
        head.next.next.next.next.next = new Node(2);

        System.out.print("Original Linked List: ");
        printLinkedList(head);

        head = kReverse(head, 4);

        System.out.print("Reversed Linked List: ");
        printLinkedList(head);
    }
}

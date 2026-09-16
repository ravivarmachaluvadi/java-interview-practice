/**
 * Detects the starting node of a loop in a singly linked list.
 *
 * The algorithm uses Floyd’s cycle‑finding technique: two pointers move
 * through the list at different speeds; when they meet, a loop exists.
 * A second phase resets one pointer to the head and moves both one step
 * at a time until they collide again – that node is the loop entry point.
 *
 * Time Complexity: O(n) – each node is visited at most a constant number of times.
 * Space Complexity: O(1) – only two pointers are used, regardless of list size.
 */
class LinkedListLoopDetection {
    public static Node firstNode(Node head) {
        Node slow = head;
        Node fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                slow = head;
                while (slow != fast) {
                    slow = slow.next;
                    fast = fast.next;
                }
                return slow;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Node node1 = new Node(1);
        Node node2 = new Node(2);
        node1.next = node2;
        Node node3 = new Node(3);
        node2.next = node3;
        Node node4 = new Node(4);
        node3.next = node4;
        Node node5 = new Node(5);
        node4.next = node5;
        node5.next = node2;
        Node head = node1;
        Node loopStartNode = firstNode(head);
        if (loopStartNode != null) {
            System.out.println("Loop detected. Starting node of the loop is: " + loopStartNode.data);
        } else {
            System.out.println("No loop detected in the linked list.");
        }
    }
}

class Node {
    int data;
    Node next;
    Node(int data1, Node next1) {
        data = data1;
        next = next1;
    }
    Node(int data1) {
        data = data1;
        next = null;
    }
}
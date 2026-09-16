/**
 * Problem: Given a singly linked list that may contain a cycle, determine the number of nodes
 * in the loop (cycle). If no loop exists, return 0.
 *
 * Approach: Use Floyd’s Tortoise and Hare algorithm to detect a meeting point inside the loop.
 * Once detected, count the length by traversing from the meeting node until returning to it.
 *
 * Time Complexity: O(n) – each pointer moves at most once through the list.
 * Space Complexity: O(1) – only a few pointers are used regardless of input size.
 */
class LinkedListLoopLength {

    static int findLength(Node slow, Node fast) {
        int cnt = 1;
        fast = fast.next;
        while (slow != fast) {
            cnt++;
            fast = fast.next;
        }
        return cnt;
    }

    static int lengthOfLoop(Node head) {
        Node slow = head;
        Node fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast)
                return findLength(slow, fast);
        }
        return 0;
    }

    public static void main(String[] args) {
        Node head = new Node(1);
        Node second = new Node(2);
        Node third = new Node(3);
        Node fourth = new Node(4);
        Node fifth = new Node(5);

        head.next = second;
        second.next = third;
        third.next = fourth;
        fourth.next = fifth;
        fifth.next = second;

        int loopLength = lengthOfLoop(head);
        if (loopLength > 0) {
            System.out.println("Length of the loop: " + loopLength);
        } else {
            System.out.println("No loop found in the linked list.");
        }
    }
}

class Node {
    int data;
    Node next;

    Node(int data1) {
        data = data1;
        next = null;
    }
}

/*
 * =====================================================================
 *  Length of Loop in Linked List           GeeksforGeeks | Easy-Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a singly linked list that may contain a cycle, return the number
 *   of nodes in the cycle. Return 0 if there is no cycle. O(1) extra space.
 *
 * EXAMPLE
 *   1 -> 2 -> 3 -> 4 -> 5 -> (back to 2)   ->  4     nodes 2,3,4,5 form the loop
 *   1 -> 2 -> 3 -> null                     ->  0     no loop
 *   1 -> (back to 1)                        ->  1     single node self-loop
 *   1 -> 2 -> 3 -> (back to 1)              ->  3     whole list is the loop
 *
 * APPROACH  (Floyd meeting point, then count one lap around the ring)
 *   1. Run slow (1 step) and fast (2 steps). If fast hits null, return 0.
 *   2. When slow == fast, that node is inside the loop. Remember it.
 *   3. Start a counter at 1 and step one pointer from the meeting node until it comes
 *      back to the meeting node, incrementing each step. That count is the loop length.
 *
 * KEY INSIGHT
 *   The meeting point does not tell you WHERE the loop starts (that needs the second
 *   phase from C07), but any node inside a ring is enough to measure the ring: walk
 *   around once and count. Recognise which question is asked - entry node vs length -
 *   because the follow-up after "detect" is always one of those two.
 *
 * COMPLEXITY
 *   Time  O(n)  detection is O(n), the counting lap is O(loop length) <= n
 *   Space O(1)  two pointers and a counter
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the entry node instead (LeetCode 142, C07).
 *   - Number of nodes NOT in the loop: total length - loop length, once the entry is known.
 *   - Break the loop: find the entry, walk to the node whose next is the entry, set null.
 *   - Explain why slow and fast are guaranteed to meet (the gap shrinks by 1 each step).
 *
 * RUN
 *   main() runs 4 cases (loop in the middle, no loop, self-loop, full-list loop)
 *   and prints actual vs expected.
 */
class LinkedListLoopLength {

    // Walk one full lap from the meeting node and count how many steps it takes to return.
    static int countLoop(Node meetingPoint) {
        int count = 1;
        Node cur = meetingPoint.next;
        while (cur != meetingPoint) {
            count++;
            cur = cur.next;
        }
        return count;
    }

    static int lengthOfLoop(Node head) {
        Node slow = head;
        Node fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                return countLoop(slow);
            }
        }
        return 0; // fast fell off the end: no cycle
    }

    // ---- test helpers ----

    // Builds 1..n and, if loopTo >= 1, points the tail at node number loopTo (1-based).
    static Node buildWithLoop(int n, int loopTo) {
        Node head = null;
        Node tail = null;
        Node loopTarget = null;
        for (int i = 1; i <= n; i++) {
            Node node = new Node(i);
            if (head == null) {
                head = node;
            } else {
                tail.next = node;
            }
            tail = node;
            if (i == loopTo) loopTarget = node;
        }
        if (tail != null) tail.next = loopTarget; // stays null when loopTo is 0
        return head;
    }

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 loop 5->2      ", lengthOfLoop(buildWithLoop(5, 2)), 4);
        print("case 2 no loop        ", lengthOfLoop(buildWithLoop(3, 0)), 0);
        print("case 3 self loop      ", lengthOfLoop(buildWithLoop(1, 1)), 1);
        print("case 4 whole list loop", lengthOfLoop(buildWithLoop(3, 1)), 3);
    }
}

class Node {
    int data;
    Node next;

    Node(int data) {
        this.data = data;
    }
}

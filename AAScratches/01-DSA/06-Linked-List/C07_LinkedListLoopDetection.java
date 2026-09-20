/*
 * =====================================================================
 *  Linked List Cycle II (loop entry node)      LeetCode 142 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a singly linked list, return the node where a cycle begins,
 *   or null if there is no cycle. Do it without modifying the list and in O(1) space.
 *
 * EXAMPLE
 *   1 -> 2 -> 3 -> 4 -> 5 -> (back to 2)   ->  node 2
 *   1 -> 2 -> 3 -> null                     ->  null      no cycle
 *   1 -> (back to 1)                        ->  node 1    single node pointing at itself
 *   empty list                              ->  null
 *
 * APPROACH  (Floyd tortoise and hare, two phases)
 *   1. slow moves 1 step, fast moves 2 steps. If fast hits null there is no cycle.
 *   2. If slow == fast they met somewhere inside the loop (not necessarily at the entry).
 *   3. Reset one pointer to head. Now move BOTH pointers 1 step at a time.
 *   4. The node where they meet again is the cycle entry. Return it.
 *
 * KEY INSIGHT
 *   Let the distance head -> entry be a, entry -> meeting point be b, and the loop
 *   length be L. When they meet, slow has walked a + b and fast has walked 2(a + b),
 *   so the extra a + b is a whole number of loops: a + b = n * L. Rearranged:
 *   a = n * L - b, meaning "a steps from the meeting point" lands on the entry too.
 *   So a pointer from head and a pointer from the meeting point, both stepping by one,
 *   collide exactly at the entry after a steps. That is the justification an
 *   interviewer wants to hear, not just the recipe.
 *
 * COMPLEXITY
 *   Time  O(n)  phase 1 meets within one lap of slow entering the cycle; phase 2 is a steps
 *   Space O(1)  two pointers
 *
 * INTERVIEW FOLLOW-UPS
 *   - Just detect a cycle (LeetCode 141): stop after phase 1.
 *   - Length of the cycle: count steps from the meeting node back to itself (C08).
 *   - Remove the cycle: walk from the entry to the node whose next is the entry, null it.
 *   - HashSet of visited nodes is O(n) space; say why Floyd is preferred.
 *
 * RUN
 *   main() runs 4 cases (loop in the middle, no loop, self-loop, empty)
 *   and prints actual vs expected.
 */
class LinkedListLoopDetection {

    public static Node firstNode(Node head) {
        Node slow = head;
        Node fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                return findEntry(head, slow);
            }
        }
        return null; // fast fell off the end: no cycle
    }

    // Phase 2: one pointer from head, one from the meeting point, both stepping by 1.
    // They collide at the entry (see KEY INSIGHT for why).
    private static Node findEntry(Node head, Node meetingPoint) {
        Node fromHead = head;
        Node fromMeeting = meetingPoint;
        while (fromHead != fromMeeting) {
            fromHead = fromHead.next;
            fromMeeting = fromMeeting.next;
        }
        return fromHead;
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

    static String describe(Node entry) {
        return entry == null ? "null" : "node " + entry.data;
    }

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 loop 5->2  ", describe(firstNode(buildWithLoop(5, 2))), "node 2");
        print("case 2 no loop    ", describe(firstNode(buildWithLoop(3, 0))), "null");
        print("case 3 self loop  ", describe(firstNode(buildWithLoop(1, 1))), "node 1");
        print("case 4 empty      ", describe(firstNode(null)), "null");
    }
}

class Node {
    int data;
    Node next;

    Node(int data) {
        this.data = data;
    }
}

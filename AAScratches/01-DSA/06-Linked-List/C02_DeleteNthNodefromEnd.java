/*
 * =====================================================================
 *  Remove Nth Node From End of List           LeetCode 19 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a singly linked list and an integer N, remove the N-th node counted from
 *   the END and return the head. N is guaranteed to be between 1 and the list length, so the
 *   node always exists. Do it in one pass.
 *
 * EXAMPLE
 *   [1,2,3,4,5], N = 3  ->  [1,2,4,5]     the 3rd from the end is 3
 *   [1], N = 1          ->  []            deleting the only node
 *   [1,2], N = 2        ->  [2]           N == length means delete the head
 *
 * APPROACH  (fixed-gap two pointers)
 *   1. Start fast and slow at head. Advance fast N steps so it is N nodes ahead of slow.
 *   2. If fast is now null, the node to delete is the head itself: return head.next.
 *   3. Otherwise walk both one step at a time while fast.next != null. When fast is on the
 *      LAST node, slow is on the node just BEFORE the target.
 *   4. Unlink: slow.next = slow.next.next. Return head.
 *
 * KEY INSIGHT
 *   Two pointers with a constant gap of N: when the leader hits the end, the follower is exactly
 *   N from the end. To DELETE you need the predecessor, so stop the leader on the last node
 *   (fast.next != null), not one past it. The head-deletion case has no predecessor, which is
 *   why it is checked separately (or avoided entirely with a dummy head, see follow-ups).
 *
 * COMPLEXITY
 *   Time  O(L)  a single pass; fast travels the whole list exactly once
 *   Space O(1)  two pointers
 *
 * INTERVIEW FOLLOW-UPS
 *   - Use a dummy node before head so the "delete head" special case disappears: start both
 *     pointers at dummy, return dummy.next.
 *   - What if N > length? Constraints forbid it; defensively, stop advancing fast when it hits
 *     null and return head unchanged.
 *   - Same gap idea gives "find the Nth from end" (return slow.next) and "check if a list is
 *     longer than N".
 *
 * RUN
 *   main() runs 3 cases (middle delete, single-node list, delete head) and prints actual vs
 *   expected.
 */
class DeleteNthNodefromEnd {

    public static Node deleteNthNodefromEnd(Node head, int N) {
        Node fast = head;
        Node slow = head;

        // open a gap of exactly N nodes between fast and slow
        for (int i = 0; i < N; i++) fast = fast.next;

        // fast fell off the end: the target is the head itself (it has no predecessor)
        if (fast == null) return head.next;

        // stop when fast is ON the last node, so slow lands just before the target
        while (fast.next != null) {
            fast = fast.next;
            slow = slow.next;
        }

        slow.next = slow.next.next; // unlink the target
        return head;
    }

    public static void main(String[] args) {
        print("case 1 [1,2,3,4,5] N=3", deleteNthNodefromEnd(build(1, 2, 3, 4, 5), 3),
                "[1, 2, 4, 5]");
        print("case 2 [1] N=1        ", deleteNthNodefromEnd(build(1), 1), "[]");
        print("case 3 [1,2] N=2      ", deleteNthNodefromEnd(build(1, 2), 2), "[2]");
    }

    static void print(String label, Node actual, String expected) {
        System.out.println(label + ": " + toStr(actual) + "   expected " + expected);
    }

    static Node build(int... vals) {
        Node dummy = new Node(0);
        Node tail = dummy;
        for (int v : vals) {
            tail.next = new Node(v);
            tail = tail.next;
        }
        return dummy.next;
    }

    static String toStr(Node head) {
        StringBuilder sb = new StringBuilder("[");
        for (Node n = head; n != null; n = n.next) {
            if (n != head) sb.append(", ");
            sb.append(n.data);
        }
        return sb.append("]").toString();
    }
}

class Node {
    public int data;
    public Node next;

    public Node(int data, Node next) {
        this.data = data;
        this.next = next;
    }

    public Node(int data) {
        this(data, null);
    }
}

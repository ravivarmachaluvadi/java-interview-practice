/*
 * =====================================================================
 *  Reverse Linked List                     LeetCode 206 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a singly linked list, reverse it in place and return
 *   the new head. The list may be empty or have a single node. Must run in
 *   O(n) time; the iterative version must use O(1) extra space.
 *
 * EXAMPLE
 *   1 -> 2 -> 3 -> 4 -> 5   ->  5 -> 4 -> 3 -> 2 -> 1
 *   (empty)                 ->  (empty)
 *   42                      ->  42
 *   1 -> 2                  ->  2 -> 1
 *
 * APPROACH  (iterative prev/curr/next)
 *   1. prev = null, curr = head.
 *   2. Save next = curr.next BEFORE touching curr.next, or the tail is lost.
 *   3. Flip: curr.next = prev.
 *   4. Advance both: prev = curr, curr = next.
 *   5. When curr is null, prev is the new head.
 *
 *      prev  curr  next            prev  curr  next
 *      null   1  -> 2 -> 3   ==>   null <- 1    2 -> 3
 *
 * APPROACH  (recursive, shown as a second method)
 *   1. Base case: null or single node is already reversed.
 *   2. Reverse everything after head; head.next is now the LAST node of
 *      that reversed tail.
 *   3. head.next.next = head appends head; head.next = null makes it the tail.
 *
 * KEY INSIGHT
 *   Three handles, one save-before-overwrite. This loop is the subroutine
 *   inside palindrome, reorder, twin-sum, reverse-in-k-groups and DLL reverse.
 *   Recognise it whenever a problem says "in place" and "reverse a segment".
 *
 * COMPLEXITY
 *   Time  O(n)  every node is visited once
 *   Space O(1)  iterative; O(n) call stack for the recursive version
 *
 * INTERVIEW FOLLOW-UPS
 *   - Reverse only positions [left, right] (LeetCode 92).
 *   - Reverse in groups of k, leave the remainder untouched (LeetCode 25).
 *   - Why can the recursive version overflow, and at roughly what length?
 *   - Reverse a doubly linked list: swap prev/next on each node.
 *
 * RUN
 *   main() runs 5 cases (typical, empty, single, two nodes) through both
 *   methods and prints actual vs expected.
 */
class ReverseLinkedList {

    /** Approach 1: iterative, O(1) space. Write this one first in an interview. */
    public ListNode reverseIterative(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;
        while (curr != null) {
            ListNode next = curr.next; // save before we overwrite curr.next
            curr.next = prev;          // flip the pointer
            prev = curr;               // advance prev
            curr = next;               // advance curr
        }
        return prev;
    }

    /** Approach 2: recursive, O(n) stack. The usual follow-up. */
    public ListNode reverseRecursive(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        // revHead is the head of the reversed tail and never changes on the way up
        ListNode revHead = reverseRecursive(head.next);
        head.next.next = head; // head.next is the tail of the reversed part; hook head on
        head.next = null;      // head is now the last node
        return revHead;
    }

    /** Builds a list from the given values, e.g. build(1, 2, 3) -> 1 -> 2 -> 3. */
    private static ListNode build(int... vals) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        for (int v : vals) {
            tail.next = new ListNode(v);
            tail = tail.next;
        }
        return dummy.next;
    }

    private static String listToString(ListNode head) {
        StringBuilder sb = new StringBuilder("[");
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (sb.length() > 1) sb.append(", ");
            sb.append(cur.val);
        }
        return sb.append("]").toString();
    }

    private static void print(String label, ListNode actual, String expected) {
        System.out.println(label + ": " + listToString(actual) + "   expected " + expected);
    }

    public static void main(String[] args) {
        ReverseLinkedList r = new ReverseLinkedList();

        // Each call mutates the list in place, so build a fresh list per run.
        print("case 1 iter 1..5  ", r.reverseIterative(build(1, 2, 3, 4, 5)), "[5, 4, 3, 2, 1]");
        print("case 1 rec  1..5  ", r.reverseRecursive(build(1, 2, 3, 4, 5)), "[5, 4, 3, 2, 1]");
        print("case 2 iter empty ", r.reverseIterative(null), "[]");
        print("case 3 rec  single", r.reverseRecursive(build(42)), "[42]");
        print("case 4 iter two   ", r.reverseIterative(build(1, 2)), "[2, 1]");
        print("case 5 rec  two   ", r.reverseRecursive(build(1, 2)), "[2, 1]");
    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
    }
}

/*
 * =====================================================================
 *  Odd Even Linked List                            LeetCode 328 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a singly linked list, group all nodes at odd POSITIONS (1st, 3rd, 5th...)
 *   followed by all nodes at even positions (2nd, 4th...), keeping the relative order inside
 *   each group. Positions are 1-based and refer to index, not value. O(1) extra space, O(n) time.
 *
 * EXAMPLE
 *   [1,2,3,4,5]      ->  [1,3,5,2,4]
 *   [2,1,3,5,6,4,7]  ->  [2,3,6,7,1,5,4]   odd positions: 2,3,6,7  even positions: 1,5,4
 *   []               ->  []
 *   [1]              ->  [1]
 *   [1,2]            ->  [1,2]
 *
 * APPROACH  (weave two chains in place, no sentinels)
 *   1. odd = head, even = head.next, and remember evenHead = even for the final splice.
 *   2. Loop while even != null && even.next != null (the guard is on EVEN because even is
 *      always one step ahead of odd, so it reaches the end first).
 *   3. Each round: odd.next = even.next; odd = odd.next;   (odd skips over even)
 *                  even.next = odd.next; even = even.next; (even skips over the new odd)
 *   4. After the loop odd is the last odd-position node: odd.next = evenHead. Return head.
 *
 * KEY INSIGHT
 *   The two pointers leapfrog each other; every assignment reads a pointer that has NOT yet
 *   been overwritten this round, so no temp variable is needed. The order matters: advance odd
 *   first (it reads even.next), then even (it reads the freshly advanced odd.next). Same
 *   split-and-splice family as Partition List, but without sentinel heads.
 *
 * COMPLEXITY
 *   Time  O(n)  each node visited once
 *   Space O(1)  three pointers
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why guard on even and not odd? With an odd-length list odd reaches the tail last; guarding
 *     on even.next handles both parities without a special case.
 *   - Group by VALUE parity instead of position: that becomes Partition List (C03) with the
 *     predicate val % 2 == 0.
 *   - Copy List with Random Pointer (C13) uses the same interleave-then-split weave in O(1)
 *     space.
 *
 * RUN
 *   main() runs 5 cases (odd length, LeetCode sample, empty, single, two nodes) and prints
 *   actual vs expected.
 */
class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
    }
}

class OddEvenLinkedList {

    public static ListNode oddEvenList(ListNode head) {
        if (head == null) return null;
        ListNode odd = head;
        ListNode even = head.next;
        ListNode evenHead = even;   // needed to splice the even chain after the odd chain
        while (even != null && even.next != null) {
            odd.next = even.next;   // odd skips over even
            odd = odd.next;
            even.next = odd.next;   // even skips over the new odd
            even = even.next;
        }
        odd.next = evenHead;        // join: last odd node -> first even node
        return head;
    }

    public static void main(String[] args) {
        print("case 1 [1,2,3,4,5]    ", oddEvenList(build(1, 2, 3, 4, 5)), "[1, 3, 5, 2, 4]");
        print("case 2 [2,1,3,5,6,4,7]", oddEvenList(build(2, 1, 3, 5, 6, 4, 7)),
                "[2, 3, 6, 7, 1, 5, 4]");
        print("case 3 []             ", oddEvenList(build()), "[]");
        print("case 4 [1]            ", oddEvenList(build(1)), "[1]");
        print("case 5 [1,2]          ", oddEvenList(build(1, 2)), "[1, 2]");
    }

    static void print(String label, ListNode actual, String expected) {
        System.out.println(label + ": " + toStr(actual) + "   expected " + expected);
    }

    static ListNode build(int... vals) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        for (int v : vals) {
            tail.next = new ListNode(v);
            tail = tail.next;
        }
        return dummy.next;
    }

    static String toStr(ListNode head) {
        StringBuilder sb = new StringBuilder("[");
        for (ListNode n = head; n != null; n = n.next) {
            if (n != head) sb.append(", ");
            sb.append(n.val);
        }
        return sb.append("]").toString();
    }
}

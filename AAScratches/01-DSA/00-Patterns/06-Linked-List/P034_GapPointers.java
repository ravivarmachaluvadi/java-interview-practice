/*
 * =====================================================================
 *  P034 Linked List: Gap Pointers (n-th from End)   Canonical LC 19 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 19, Remove Nth Node From End of List)
 *   Remove the n-th node from the end of the list (1 = last) in one pass and return the
 *   head. n is always valid.
 *
 * EXAMPLE
 *   [1, 2, 3, 4, 5], n = 2  ->  [1, 2, 3, 5]
 *   [1],             n = 1  ->  []
 *   [1, 2],          n = 2  ->  [2]        the trap: the head itself is removed
 *
 * RECOGNIZE WHEN
 *   - "k-th / n-th from the END", "last k nodes", "rotate right by k" on a singly linked
 *     list, ideally in one pass.
 *   - Two lists of different lengths must be walked "aligned at the end" (intersection).
 *   Not this if: you need the middle or a cycle -> P031_FastSlowPointers.
 *
 * TEMPLATE
 *   dummy.next = head; lead = trail = dummy
 *   move lead n steps ahead                    // the gap is now n
 *   while lead.next: lead = lead.next; trail = trail.next
 *   trail.next is the n-th from the end         // trail is the node BEFORE it
 *
 * APPROACH
 *   1. Put lead n nodes ahead of trail, both starting at a dummy.
 *   2. Move both until lead is on the last node; trail now sits just before the target.
 *   3. Unlink trail.next.
 *
 * KEY INSIGHT
 *   You cannot count from the end of a singly linked list, but a fixed GAP between two
 *   pointers carries the count for you: when the leader hits the end, the follower is
 *   exactly n behind. Starting at a dummy lets the follower stop BEFORE the target, even
 *   when the target is the head.
 *
 * COMPLEXITY
 *   Time O(L), one pass; space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 61   Rotate List              k %= length; the new tail is (length - k)
 *                                            from the start; close into a ring, cut it
 *   [coded] LC 160  Intersection of Lists    walk a then b, and b then a: both travel
 *                                            m + n and meet at the junction (or null)
 *           LC 1721 Swapping Nodes           k-th from start and k-th from end via the gap
 *           LC 2095 Delete Middle Node       -> P031_FastSlowPointers
 *           Kth to last (CTCI 2.2)           the same gap walk
 *
 * PITFALLS
 *   - Without the dummy, removing the head needs a special case.
 *   - LC 61: k can exceed the length; and k % length == 0 means "unchanged".
 *   - LC 160: compare NODES (references), not values.
 *
 * DEEP DIVE
 *   C02_DeleteNthNodefromEnd, C06_RotateRightList, B02_IntersectionOfTwoLinkedLists
 *   (06-Linked-List)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class GapPointers {

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }

        static ListNode of(int... vals) {
            ListNode dummy = new ListNode(0);
            ListNode cur = dummy;
            for (int v : vals) {
                cur.next = new ListNode(v);
                cur = cur.next;
            }
            return dummy.next;
        }
    }

    static String show(ListNode head) {
        StringBuilder sb = new StringBuilder("[");
        for (ListNode n = head; n != null; n = n.next) {
            sb.append(n.val).append(n.next == null ? "" : ", ");
        }
        return sb.append("]").toString();
    }

    // Canonical LC 19.
    static ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode lead = dummy;
        ListNode trail = dummy;
        for (int i = 0; i < n; i++) {
            lead = lead.next;
        }
        while (lead.next != null) {
            lead = lead.next;
            trail = trail.next;
        }
        trail.next = trail.next.next;
        return dummy.next;
    }

    // LC 61: rotate right by k.
    static ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null) {
            return head;
        }
        int length = 1;
        ListNode tail = head;
        while (tail.next != null) {
            tail = tail.next;
            length++;
        }
        k %= length;
        if (k == 0) {
            return head;
        }
        ListNode newTail = head;
        for (int i = 1; i < length - k; i++) {
            newTail = newTail.next;
        }
        ListNode newHead = newTail.next;
        newTail.next = null;
        tail.next = head;
        return newHead;
    }

    // LC 160: both pointers walk m + n nodes, so they line up at the junction.
    static ListNode getIntersectionNode(ListNode a, ListNode b) {
        ListNode p = a;
        ListNode q = b;
        while (p != q) {
            p = p == null ? b : p.next;
            q = q == null ? a : q.next;
        }
        return p;                                  // the junction, or null
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 19 n=2", show(removeNthFromEnd(ListNode.of(1, 2, 3, 4, 5), 2)), "[1, 2, 3, 5]");
        check("LC 19 only node", show(removeNthFromEnd(ListNode.of(1), 1)), "[]");
        check("LC 19 remove the head", show(removeNthFromEnd(ListNode.of(1, 2), 2)), "[2]");
        check("LC 19 remove the tail", show(removeNthFromEnd(ListNode.of(1, 2), 1)), "[1]");

        check("LC 61 k=2", show(rotateRight(ListNode.of(1, 2, 3, 4, 5), 2)), "[4, 5, 1, 2, 3]");
        check("LC 61 k=4 > length", show(rotateRight(ListNode.of(0, 1, 2), 4)), "[2, 0, 1]");
        check("LC 61 k=length unchanged", show(rotateRight(ListNode.of(1, 2, 3), 3)), "[1, 2, 3]");

        ListNode shared = ListNode.of(8, 4, 5);
        ListNode a = ListNode.of(4, 1);
        a.next.next = shared;
        ListNode b = ListNode.of(5, 6, 1);
        b.next.next.next = shared;
        check("LC 160 meet at 8", getIntersectionNode(a, b).val, 8);
        check("LC 160 no junction",
                getIntersectionNode(ListNode.of(2, 6, 4), ListNode.of(1, 5)), null);
    }
}

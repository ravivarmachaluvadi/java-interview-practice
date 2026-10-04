/*
 * =====================================================================
 *  P033 Linked List: Dummy Head, Merge and Build   Canonical LC 21 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 21, Merge Two Sorted Lists)
 *   Merge two sorted linked lists into one sorted list by splicing their nodes together,
 *   and return its head.
 *
 * EXAMPLE
 *   [1, 2, 4] + [1, 3, 4]  ->  [1, 1, 2, 3, 4, 4]
 *   []        + []         ->  []
 *   []        + [0]        ->  [0]
 *
 * RECOGNIZE WHEN
 *   - You BUILD an output list node by node (merge, add numbers, partition, filter).
 *   - The head itself might change or be deleted (removing nodes, duplicates at the front).
 *   - Splitting one list into several (by value, by parity) and joining them back.
 *   Not this if: you reverse links -> P032_InPlaceReversal; you need the n-th from the end
 *   -> P034_GapPointers; more than two sorted lists -> P044_KWayMerge.
 *
 * TEMPLATE
 *   dummy = new Node(0); tail = dummy          // dummy.next will be the real head
 *   while inputs remain:
 *       pick / create the next node
 *       tail.next = node; tail = node
 *   tail.next = rest (or null)                  // terminate, or a cycle survives
 *   return dummy.next
 *
 * APPROACH
 *   1. tail starts at a dummy node, so the first real node needs no special case.
 *   2. Attach the smaller head of the two lists, advance that list and tail.
 *   3. When one list ends, attach the rest of the other one.
 *
 * KEY INSIGHT
 *   A dummy (sentinel) node turns "the list might be empty" and "the head might change"
 *   into the general case: there is always a node before the one you are working on.
 *   Most linked-list bugs are head special cases, and the dummy deletes them.
 *
 * COMPLEXITY
 *   Time O(m + n), space O(1) (the nodes are reused; LC 2 creates max(m, n) + 1 nodes).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 2    Add Two Numbers          one new node per digit; loop while either list
 *                                            OR the carry remains
 *   [coded] LC 86   Partition List           two dummies (less, rest), join, end with null
 *   [coded] LC 82   Remove Duplicates II     prev starts at dummy; skip every run of equal
 *                                            values entirely
 *           LC 83   Remove Duplicates        keep one of each: compare with next, unlink
 *           LC 203  Remove Linked List Elements  dummy, unlink matching nodes
 *           LC 328  Odd Even Linked List     two tails (odd, even), join at the end
 *           LC 148  Sort List                merge sort: P031_FastSlowPointers middle + merge
 *           LC 23   Merge k Sorted Lists     -> P044_KWayMerge
 *
 * PITFALLS
 *   - Finish with tail.next = null when you reuse nodes (LC 86), or the old links form a
 *     cycle.
 *   - LC 2: do not forget the final carry ([5] + [5] = [0, 1]).
 *   - Return dummy.next, never dummy.
 *
 * DEEP DIVE
 *   A04_MergeTwoSortedLists, C01_AddTwoNumbers, C03_PartitionList,
 *   B01_RemoveDuplicatesInList (06-Linked-List)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class DummyHeadMerge {

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

    // Canonical LC 21.
    static ListNode mergeTwoLists(ListNode a, ListNode b) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        while (a != null && b != null) {
            if (a.val <= b.val) {
                tail.next = a;
                a = a.next;
            } else {
                tail.next = b;
                b = b.next;
            }
            tail = tail.next;
        }
        tail.next = a != null ? a : b;
        return dummy.next;
    }

    // LC 2: digits stored in reverse order.
    static ListNode addTwoNumbers(ListNode a, ListNode b) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        int carry = 0;
        while (a != null || b != null || carry != 0) {
            int sum = carry;
            if (a != null) {
                sum += a.val;
                a = a.next;
            }
            if (b != null) {
                sum += b.val;
                b = b.next;
            }
            tail.next = new ListNode(sum % 10);
            tail = tail.next;
            carry = sum / 10;
        }
        return dummy.next;
    }

    // LC 86: nodes < x first, keeping the original order inside each part.
    static ListNode partition(ListNode head, int x) {
        ListNode lessDummy = new ListNode(0);
        ListNode restDummy = new ListNode(0);
        ListNode less = lessDummy;
        ListNode rest = restDummy;
        for (ListNode n = head; n != null; n = n.next) {
            if (n.val < x) {
                less.next = n;
                less = n;
            } else {
                rest.next = n;
                rest = n;
            }
        }
        rest.next = null;                          // the old link might point backwards
        less.next = restDummy.next;
        return lessDummy.next;
    }

    // LC 82: delete every value that appears more than once (sorted list).
    static ListNode deleteDuplicates(ListNode head) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prev = dummy;
        ListNode cur = head;
        while (cur != null) {
            if (cur.next != null && cur.next.val == cur.val) {
                int dup = cur.val;
                while (cur != null && cur.val == dup) {
                    cur = cur.next;
                }
                prev.next = cur;                   // drop the whole run
            } else {
                prev = cur;
                cur = cur.next;
            }
        }
        return dummy.next;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 21 two lists", show(mergeTwoLists(ListNode.of(1, 2, 4), ListNode.of(1, 3, 4))),
                "[1, 1, 2, 3, 4, 4]");
        check("LC 21 both empty", show(mergeTwoLists(null, null)), "[]");
        check("LC 21 one empty", show(mergeTwoLists(null, ListNode.of(0))), "[0]");

        check("LC 2 342 + 465",
                show(addTwoNumbers(ListNode.of(2, 4, 3), ListNode.of(5, 6, 4))), "[7, 0, 8]");
        check("LC 2 0 + 0", show(addTwoNumbers(ListNode.of(0), ListNode.of(0))), "[0]");
        check("LC 2 final carry",
                show(addTwoNumbers(ListNode.of(9, 9, 9, 9, 9, 9, 9), ListNode.of(9, 9, 9, 9))),
                "[8, 9, 9, 9, 0, 0, 0, 1]");

        check("LC 86 x=3", show(partition(ListNode.of(1, 4, 3, 2, 5, 2), 3)), "[1, 2, 2, 4, 3, 5]");
        check("LC 86 x=2", show(partition(ListNode.of(2, 1), 2)), "[1, 2]");

        check("LC 82 [1,2,3,3,4,4,5]",
                show(deleteDuplicates(ListNode.of(1, 2, 3, 3, 4, 4, 5))), "[1, 2, 5]");
        check("LC 82 duplicates at the head",
                show(deleteDuplicates(ListNode.of(1, 1, 1, 2, 3))), "[2, 3]");
        check("LC 82 all duplicates", show(deleteDuplicates(ListNode.of(1, 1))), "[]");
    }
}

/*
 * =====================================================================
 *  Merge Two Sorted Lists                   LeetCode 21 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the heads of two sorted singly linked lists, splice them into one
 *   sorted list by re-linking the existing nodes (no new nodes for data).
 *   Either list may be empty. Values may repeat across the two lists.
 *
 * EXAMPLE
 *   [1, 3, 5] + [2, 4, 6]  ->  [1, 2, 3, 4, 5, 6]
 *   [] + [0]               ->  [0]
 *   [] + []                ->  []
 *   [1, 2, 4] + [1, 3, 4]  ->  [1, 1, 2, 3, 4, 4]   (ties take list2 first)
 *
 * APPROACH  (dummy head + two pointers)
 *   1. Create a sentinel `dummy`; `current` is the tail of the result so far.
 *   2. While both lists are non-empty, attach the smaller head to
 *      current.next, advance that list, then advance current.
 *   3. When one list runs out, attach the remainder of the other in one step
 *      (it is already sorted).
 *   4. Return dummy.next, skipping the sentinel.
 *
 * KEY INSIGHT
 *   The sentinel removes the "is this the first node?" special case, so the
 *   loop body is identical for every node. This exact loop is the merge step
 *   of merge sort on a list and the two-list base case of merge k lists.
 *
 * COMPLEXITY
 *   Time  O(n + m)  each node of both lists is attached once
 *   Space O(1)      only pointers; nodes are reused, not copied
 *
 * INTERVIEW FOLLOW-UPS
 *   - Merge k sorted lists: min-heap of heads, O(N log k) (LeetCode 23).
 *   - Do it recursively: pick smaller head, recurse on the rest; O(n+m) stack.
 *   - Why is `<` vs `<=` a stability question? With `<`, equal values take
 *     list2's node first.
 *   - Sort a linked list with this merge as the inner step (LeetCode 148).
 *
 * RUN
 *   main() runs 4 cases (typical, one empty, both empty, duplicates) and
 *   prints actual vs expected.
 */
class MergeTwoSortedLists {

    /** Definition for singly-linked list. */
    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }

    public static ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(-1); // sentinel: avoids a special case for the head
        ListNode current = dummy;          // tail of the merged list so far

        while (list1 != null && list2 != null) {
            if (list1.val < list2.val) {
                current.next = list1;
                list1 = list1.next;
            } else {
                current.next = list2;
                list2 = list2.next;
            }
            current = current.next;
        }

        // At most one list still has nodes; it is already sorted, so attach it whole.
        current.next = (list1 != null) ? list1 : list2;

        return dummy.next; // skip the sentinel
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

    static String listToString(ListNode head) {
        StringBuilder sb = new StringBuilder("[");
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (sb.length() > 1) sb.append(", ");
            sb.append(cur.val);
        }
        return sb.append("]").toString();
    }

    static void print(String label, ListNode actual, String expected) {
        System.out.println(label + ": " + listToString(actual) + "   expected " + expected);
    }

    public static void main(String[] args) {
        ListNode merged1 = mergeTwoLists(build(1, 3, 5), build(2, 4, 6));
        print("case 1 typical   ", merged1, "[1, 2, 3, 4, 5, 6]");
        print("case 2 one empty ", mergeTwoLists(null, build(0)), "[0]");
        print("case 3 both empty", mergeTwoLists(null, null), "[]");
        ListNode merged4 = mergeTwoLists(build(1, 2, 4), build(1, 3, 4));
        print("case 4 duplicates", merged4, "[1, 1, 2, 3, 4, 4]");
    }
}

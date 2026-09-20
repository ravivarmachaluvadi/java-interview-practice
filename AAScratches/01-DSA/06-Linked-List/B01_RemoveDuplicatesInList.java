/*
 * =====================================================================
 *  Remove Duplicates from Sorted List                 LeetCode 83 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a sorted singly linked list, delete every duplicate so that each
 *   value appears exactly once, and return the list still sorted. Because the list is
 *   sorted, all copies of a value are adjacent.
 *
 * EXAMPLE
 *   1 -> 2 -> 3 -> 3 -> 4 -> 4 -> 5  ->  1 -> 2 -> 3 -> 4 -> 5
 *   1 -> 1 -> 1                      ->  1                      (all equal, keep one)
 *   []                               ->  []                     (empty list)
 *
 * APPROACH  (skip-forward on a sorted list)
 *   1. Start current at head.
 *   2. While current and current.next both exist:
 *        - if current.val == current.next.val, unlink the next node by pointing
 *          current.next at current.next.next; do NOT move current, because the new
 *          next node may be yet another copy of the same value;
 *        - otherwise the next value is different, so advance current.
 *   3. Return head; it never changes because the first node is always kept.
 *
 * KEY INSIGHT
 *   At each step there are exactly two choices: unlink or advance, never both. If you
 *   advance in the same iteration you unlinked, a run of three or more equal values
 *   (1,1,1) leaves a duplicate behind. Staying put after an unlink makes the loop
 *   re-check the same position until the neighbour is genuinely different.
 *
 * COMPLEXITY
 *   Time  O(n)  each node is looked at once as current or as current.next
 *   Space O(1)  only one moving pointer
 *
 * INTERVIEW FOLLOW-UPS
 *   - LeetCode 82: remove ALL nodes that have duplicates (needs a dummy head and a
 *     look-ahead run so the first copy is dropped too).
 *   - Unsorted list: use a HashSet of seen values, still O(n) time but O(n) space.
 *   - Doubly linked list: also fix the prev pointer of the node after the unlinked one.
 *
 * RUN
 *   main() runs 3 cases (typical, all equal, empty) and prints actual vs expected.
 */

class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
    }

    static ListNode fromArray(int[] values) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        for (int v : values) {
            tail.next = new ListNode(v);
            tail = tail.next;
        }
        return dummy.next;
    }

    static String toString(ListNode head) {
        StringBuilder sb = new StringBuilder("[");
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (sb.length() > 1) sb.append(", ");
            sb.append(cur.val);
        }
        return sb.append("]").toString();
    }
}

// https://leetcode.com/problems/remove-duplicates-from-sorted-list/description/
class RemoveDuplicatesInList {

    public ListNode deleteDuplicates(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode current = head;
        while (current != null && current.next != null) {
            if (current.val == current.next.val) {
                // Unlink the duplicate and stay on current: the new next may be another copy
                current.next = current.next.next;
            } else {
                current = current.next;
            }
        }
        return head;
    }

    static void runCase(String label, int[] input, String expected) {
        ListNode result = new RemoveDuplicatesInList().deleteDuplicates(ListNode.fromArray(input));
        System.out.println(label + ListNode.toString(result) + "   expected " + expected);
    }

    public static void main(String[] args) {
        runCase("case 1 (typical):   ", new int[]{1, 2, 3, 3, 4, 4, 5}, "[1, 2, 3, 4, 5]");
        runCase("case 2 (all equal): ", new int[]{1, 1, 1}, "[1]");
        runCase("case 3 (empty):     ", new int[]{}, "[]");
    }
}

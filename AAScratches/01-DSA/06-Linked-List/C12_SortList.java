/*
 * =====================================================================
 *  Sort List                                        LeetCode 148 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a singly linked list, return the list sorted in ascending order.
 *   The follow-up (and the real question) asks for O(n log n) time with O(1) extra space
 *   beyond the recursion stack; you cannot index into a list, so quicksort/heapsort are
 *   awkward and merge sort is the natural fit.
 *
 * EXAMPLE
 *   4 -> 2 -> 1 -> 3            ->  1 -> 2 -> 3 -> 4
 *   -1 -> 5 -> 3 -> 4 -> 0      ->  -1 -> 0 -> 3 -> 4 -> 5   (negatives, odd length)
 *   (empty)                     ->  (empty)
 *   2 -> 2 -> 2                 ->  2 -> 2 -> 2              (all equal; must terminate)
 *
 * APPROACH  (top-down merge sort on a linked list)
 *   1. Base case: a list of 0 or 1 nodes is already sorted.
 *   2. getMid: slow/fast walk while tracking prev (the node before slow). Cut the list at
 *      prev.next = null so the two halves are independent lists, and return slow as the
 *      head of the second half.
 *   3. Recursively sort both halves.
 *   4. merge: dummy-headed two-pointer merge (same as Merge Two Sorted Lists, A05). Relink
 *      the existing nodes; do not allocate new ones.
 *
 * KEY INSIGHT
 *   Merge sort needs only two things a linked list is good at: splitting by walking to
 *   the middle, and merging by relinking. The one subtlety is that finding the middle is
 *   not enough - you must UNLINK (prev.next = null), otherwise sortList(head) still sees
 *   the whole list and recurses forever. Track prev, cut, then recurse.
 *
 * COMPLEXITY
 *   Time  O(n log n)  log n levels of splitting, each level merges all n nodes once
 *   Space O(log n)    recursion depth; no arrays or extra nodes beyond one dummy per merge
 *
 * INTERVIEW FOLLOW-UPS
 *   - True O(1) space: bottom-up merge sort. Merge runs of size 1, 2, 4, ... iteratively
 *     with a length counter and a split(head, size) helper. Same time, no recursion.
 *   - Why not quicksort? Partitioning is fine, but random pivots need indexing and
 *     worst-case is O(n^2) on sorted input, which is common in practice.
 *   - Stability: merge uses "<" so equal keys keep left-half-first order, i.e. it is stable.
 *
 * RUN
 *   main() runs 4 cases (typical, negatives with odd length, empty, all equal) and prints
 *   actual vs expected.
 */
class SortList {

    public static void main(String[] args) {
        SortList sorter = new SortList();
        print("case 1 typical  ", sorter.sortList(fromArray(new int[]{4, 2, 1, 3})), "1 2 3 4");
        print("case 2 negatives", sorter.sortList(fromArray(new int[]{-1, 5, 3, 4, 0})),
                "-1 0 3 4 5");
        print("case 3 empty    ", sorter.sortList(null), "");
        print("case 4 all equal", sorter.sortList(fromArray(new int[]{2, 2, 2})), "2 2 2");
    }

    public ListNode sortList(ListNode head) {
        // base case: 0 or 1 nodes is already sorted (also stops the recursion)
        if (head == null || head.next == null) return head;

        // Step 1: split into two independent halves; mid is the head of the second half
        ListNode mid = getMid(head);
        ListNode left = sortList(head);
        ListNode right = sortList(mid);

        // Step 2: merge the two sorted halves
        return merge(left, right);
    }

    /** Walks to the middle, cuts the list there, and returns the head of the second half. */
    private ListNode getMid(ListNode head) {
        ListNode prev = null;   // node just before slow, needed to cut the link
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            prev = slow;
            slow = slow.next;
            fast = fast.next.next;
        }

        // cut: without this the recursion on the first half would see the whole list
        if (prev != null) prev.next = null;
        return slow;
    }

    /** Dummy-headed merge of two sorted lists by relinking the existing nodes. */
    private ListNode merge(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val < list2.val) {
                tail.next = list1;
                list1 = list1.next;
            } else {
                tail.next = list2;
                list2 = list2.next;
            }
            tail = tail.next;
        }
        // one side ran out; the other is already sorted, so attach it whole
        tail.next = (list1 != null) ? list1 : list2;

        return dummy.next;
    }

    // ---- helpers for main ------------------------------------------------

    private static ListNode fromArray(int[] values) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        for (int value : values) {
            current.next = new ListNode(value);
            current = current.next;
        }
        return dummy.next;
    }

    private static String toString(ListNode head) {
        StringBuilder sb = new StringBuilder();
        for (ListNode n = head; n != null; n = n.next) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(n.val);
        }
        return sb.toString();
    }

    private static void print(String label, ListNode actual, String expected) {
        System.out.println(label + ": [" + toString(actual) + "]   expected [" + expected + "]");
    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
    }
}

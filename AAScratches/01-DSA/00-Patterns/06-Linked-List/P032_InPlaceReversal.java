/*
 * =====================================================================
 *  P032 Linked List In-Place Reversal   Canonical LC 206 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 206, Reverse Linked List)
 *   Reverse a singly linked list and return the new head. Do it iteratively and
 *   recursively.
 *
 * EXAMPLE
 *   [1, 2, 3, 4, 5]  ->  [5, 4, 3, 2, 1]
 *   [1, 2]           ->  [2, 1]
 *   []               ->  []
 *
 * RECOGNIZE WHEN
 *   - "reverse" a whole list, a sublist [left, right], or every group of k nodes.
 *   - Compare a list with itself backwards (palindrome) or interleave the front with the
 *     back (reorder) in O(1) extra space.
 *   Not this if: the reversal is of an array or string -> P009_ReverseTricks; you only need
 *   to read backwards and O(n) space is fine -> push onto a stack.
 *
 * TEMPLATE
 *   prev = null, cur = head
 *   while cur:
 *       next = cur.next       // 1. save the rest
 *       cur.next = prev       // 2. flip one pointer
 *       prev = cur            // 3. advance prev
 *       cur = next            // 4. advance cur
 *   return prev
 *   sublist: walk to the node BEFORE left (use a dummy), reverse right - left + 1 nodes,
 *            reconnect both ends
 *
 * APPROACH
 *   1. Keep prev (already reversed part) and cur (rest of the list).
 *   2. Save cur.next, point cur back at prev, move both one step.
 *
 * KEY INSIGHT
 *   Reversal is just "flip one arrow at a time" while holding the three nodes around it
 *   (prev, cur, next). Every harder reversal problem is this loop plus careful bookkeeping
 *   of the node before the section and the node after it.
 *
 * COMPLEXITY
 *   Iterative O(n) time, O(1) space; recursive O(n) time, O(n) stack.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 92   Reverse Linked List II   dummy, walk to left - 1, reverse the section by
 *                                            moving each next node to the section's front
 *   [coded] LC 25   Reverse Nodes in k-Group check k nodes exist, reverse them, recurse on
 *                                            the rest; leave a short tail as it is
 *   [coded] LC 234  Palindrome Linked List   middle (P031_FastSlowPointers), reverse the
 *                                            second half, compare, (restore)
 *           LC 24   Swap Nodes in Pairs      LC 25 with k = 2
 *           LC 143  Reorder List             middle, reverse the second half, merge
 *                                            alternately
 *           LC 2130 Max Twin Sum             reverse the second half, add pairs
 *
 * PITFALLS
 *   - Save next BEFORE changing cur.next, or the rest of the list is lost.
 *   - Sublists: a dummy head avoids the "left == 1" special case.
 *   - LC 25: never reverse a final group shorter than k.
 *
 * DEEP DIVE
 *   A02_ReverseLinkedList, D01_ReverseListInKGroups, B03_PalindromeLinkedList,
 *   C10_ReorderList (06-Linked-List)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class InPlaceReversal {

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

    // Canonical LC 206, iterative.
    static ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode cur = head;
        while (cur != null) {
            ListNode next = cur.next;
            cur.next = prev;
            prev = cur;
            cur = next;
        }
        return prev;
    }

    // Canonical LC 206, recursive: reverse the rest, then hang head after its old next.
    static ListNode reverseRecursive(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode newHead = reverseRecursive(head.next);
        head.next.next = head;
        head.next = null;
        return newHead;
    }

    // LC 92: reverse positions left..right (1-based).
    static ListNode reverseBetween(ListNode head, int left, int right) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode before = dummy;
        for (int i = 1; i < left; i++) {
            before = before.next;
        }
        ListNode tail = before.next;               // becomes the section's last node
        for (int i = 0; i < right - left; i++) {
            ListNode moved = tail.next;            // move the next node to the front
            tail.next = moved.next;
            moved.next = before.next;
            before.next = moved;
        }
        return dummy.next;
    }

    // LC 25.
    static ListNode reverseKGroup(ListNode head, int k) {
        ListNode probe = head;
        for (int i = 0; i < k; i++) {
            if (probe == null) {
                return head;                       // fewer than k left: keep as is
            }
            probe = probe.next;
        }
        ListNode prev = reverseKGroup(probe, k);   // the already-processed rest
        ListNode cur = head;
        for (int i = 0; i < k; i++) {
            ListNode next = cur.next;
            cur.next = prev;
            prev = cur;
            cur = next;
        }
        return prev;
    }

    // LC 234.
    static boolean isPalindrome(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode second = reverseList(slow);
        ListNode a = head;
        ListNode b = second;
        boolean same = true;
        while (b != null) {
            if (a.val != b.val) {
                same = false;
                break;
            }
            a = a.next;
            b = b.next;
        }
        reverseList(second);                       // put the list back as it was
        return same;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 206 iterative", show(reverseList(ListNode.of(1, 2, 3, 4, 5))), "[5, 4, 3, 2, 1]");
        check("LC 206 iterative [1,2]", show(reverseList(ListNode.of(1, 2))), "[2, 1]");
        check("LC 206 iterative empty", show(reverseList(null)), "[]");
        check("LC 206 recursive",
                show(reverseRecursive(ListNode.of(1, 2, 3, 4, 5))), "[5, 4, 3, 2, 1]");

        check("LC 92 left=2 right=4",
                show(reverseBetween(ListNode.of(1, 2, 3, 4, 5), 2, 4)), "[1, 4, 3, 2, 5]");
        check("LC 92 single node", show(reverseBetween(ListNode.of(5), 1, 1)), "[5]");
        check("LC 92 whole list", show(reverseBetween(ListNode.of(3, 5), 1, 2)), "[5, 3]");

        check("LC 25 k=2", show(reverseKGroup(ListNode.of(1, 2, 3, 4, 5), 2)), "[2, 1, 4, 3, 5]");
        check("LC 25 k=3", show(reverseKGroup(ListNode.of(1, 2, 3, 4, 5), 3)), "[3, 2, 1, 4, 5]");
        check("LC 25 k=1 unchanged", show(reverseKGroup(ListNode.of(1, 2, 3), 1)), "[1, 2, 3]");

        check("LC 234 [1,2,2,1]", isPalindrome(ListNode.of(1, 2, 2, 1)), true);
        check("LC 234 [1,2]", isPalindrome(ListNode.of(1, 2)), false);
        check("LC 234 [1,2,3,2,1] odd", isPalindrome(ListNode.of(1, 2, 3, 2, 1)), true);
        ListNode kept = ListNode.of(1, 2, 2, 1);
        isPalindrome(kept);
        check("LC 234 list restored afterwards", show(kept), "[1, 2, 2, 1]");
    }
}

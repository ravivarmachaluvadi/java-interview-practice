/*
 * =====================================================================
 *  Reorder List                                     LeetCode 143 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a singly linked list L0 -> L1 -> ... -> Ln, reorder it in place to
 *   L0 -> Ln -> L1 -> Ln-1 -> L2 -> Ln-2 -> ... Only node links may change, not node values.
 *   Return nothing extra: the list is rewired under the same head.
 *
 * EXAMPLE
 *   1 -> 2 -> 3 -> 4 -> 5   ->  1 -> 5 -> 2 -> 4 -> 3     (odd length, middle stays last)
 *   1 -> 2 -> 3 -> 4        ->  1 -> 4 -> 2 -> 3          (even length)
 *   7                       ->  7                         (single node, nothing to do)
 *
 * APPROACH  (middle, reverse second half, interleave)
 *   1. Slow/fast walk to the middle. For odd length slow lands on the exact middle; for
 *      even length it lands on the FIRST node of the second half. Either way, cutting at
 *      slow.next leaves the first half longer than the reversed second half by one node
 *      (odd length) or two (even length), which is what the interleave needs.
 *   2. Cut after slow: secondHalf = slow.next; slow.next = null. Reverse secondHalf in place.
 *   3. Interleave: take one node from the first half, one from the reversed second half,
 *      appending to a dummy-headed result. Save both .next pointers BEFORE overwriting
 *      either, because appending a node destroys its old link.
 *   4. The leftover first-half chain (one node for odd length, two for even) is appended
 *      as the tail.
 *
 * KEY INSIGHT
 *   The target order is "first half forwards" zipped with "second half backwards". A
 *   singly linked list cannot walk backwards, so you physically reverse the second half
 *   and then a plain two-list zip gives the answer. Whenever a problem wants nodes from
 *   both ends of a list at once, think: find middle + reverse one half + walk both.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass to find the middle, one to reverse, one to zip
 *   Space O(1)  only pointer variables; the dummy is a single extra node
 *
 * INTERVIEW FOLLOW-UPS
 *   - Same trick family: B03_PalindromeLinkedList and C09_MaximumTwinSum reverse a half too.
 *   - Can you do it without a dummy node? Yes: zip in place starting from head, but the
 *     dummy makes the "append and advance" loop symmetric and harder to get wrong.
 *   - Why cut AFTER slow (secondHalf = slow.next) and not before it? The node slow sits
 *     on must stay in the first half, so for odd length the true middle ends up last.
 *
 * RUN
 *   main() runs 3 cases (odd length, even length, single node) and prints actual vs expected.
 */
class ReorderList {

    public static void main(String[] args) {
        print("case 1 odd   ", reorderList(fromArray(new int[]{1, 2, 3, 4, 5})), "1 5 2 4 3");
        print("case 2 even  ", reorderList(fromArray(new int[]{1, 2, 3, 4})), "1 4 2 3");
        print("case 3 single", reorderList(fromArray(new int[]{7})), "7");
    }

    static ListNode reorderList(ListNode head) {
        if (head == null || head.next == null) return head;

        // Step 1: slow stops on the middle (odd) or the first node of the second half (even)
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // Step 2: cut the list after slow and reverse the second half
        ListNode secondHalf = slow.next;
        slow.next = null;
        secondHalf = reverse(secondHalf);

        // Step 3: zip the two halves, one node from each in turn
        ListNode first = head;
        ListNode dummy = new ListNode(-1);
        ListNode tail = dummy;
        while (first != null && secondHalf != null) {
            ListNode nextFirst = first.next;        // save before we overwrite first.next
            ListNode nextSecond = secondHalf.next;  // save before we overwrite secondHalf.next

            tail.next = first;
            tail = first;
            tail.next = secondHalf;
            tail = secondHalf;

            first = nextFirst;
            secondHalf = nextSecond;
        }

        // Step 4: the leftover first-half chain (one or two nodes) becomes the tail
        tail.next = (first != null) ? first : secondHalf;

        return dummy.next;
    }

    /** Standard iterative reversal: returns the new head. */
    private static ListNode reverse(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }
        return prev;
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
        System.out.println(label + ": " + toString(actual) + "   expected " + expected);
    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
    }
}

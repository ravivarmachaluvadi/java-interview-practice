/*
 * =====================================================================
 *  Rotate List                                    LeetCode 61 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a singly linked list and a non-negative integer k, rotate the list
 *   to the right by k places: the last k nodes move to the front, order preserved.
 *   k may be larger than the list length; the list may be empty or a single node.
 *
 * EXAMPLE
 *   [1, 2, 3, 4, 5], k = 2   ->  [4, 5, 1, 2, 3]
 *   [0, 1, 2],       k = 4   ->  [2, 0, 1]        because 4 % 3 = 1, so rotate once
 *   [1],             k = 99  ->  [1]              single node never changes
 *   [1, 2, 3],       k = 3   ->  [1, 2, 3]        k % length = 0, no work
 *
 * APPROACH  (close into a ring, cut at length - k)
 *   1. Walk to the tail once, counting the length.
 *   2. Point tail.next at head: the list is now a ring, so every rotation is just
 *      a choice of where to cut it.
 *   3. Normalise k = k % length. If it is 0 the ring is cut where it was joined.
 *   4. The new tail is the (length - k)-th node from the head. Walk there.
 *   5. newHead = newTail.next; set newTail.next = null to open the ring; return newHead.
 *
 * KEY INSIGHT
 *   Rotating right by k is the same as "cut after node length - k and swap the two
 *   pieces". Making the list circular first turns that swap into a single null
 *   assignment, and k % length removes every full-circle rotation for free.
 *   Pattern: any "move a suffix to the front" question on a list is ring-and-cut.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass to find the tail, at most one more to find the cut point
 *   Space O(1)  three pointers and an int
 *
 * INTERVIEW FOLLOW-UPS
 *   - Rotate LEFT by k: same ring, but cut after node k instead of length - k.
 *   - Why normalise k before walking? k can be 2 * 10^9 while length is 500.
 *   - Do it in one pass without counting: keep a k-gap pair like C02_DeleteNthNodefromEnd
 *     (but k must still be reduced, so the length pass is usually unavoidable).
 *   - Same problem on an array is C09_RotateArray in 01-Arrays (three reversals).
 *
 * RUN
 *   main() runs 5 cases (typical, k > length, single node, k == length, empty)
 *   and prints actual vs expected.
 */
class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
    }
}

// https://leetcode.com/problems/rotate-list/description/
class RotateRightList {

    public static ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null || k == 0) {
            return head;
        }

        // Step 1: find the tail and the length in one walk
        ListNode tail = head;
        int length = 1;
        while (tail.next != null) {
            tail = tail.next;
            length++;
        }

        // Step 2: close the list into a ring so rotation becomes "pick where to cut"
        tail.next = head;

        // Step 3: drop full-circle rotations; k == 0 means the ring is cut where it was joined
        k = k % length;
        if (k == 0) {
            tail.next = null;
            return head;
        }

        // Step 4: the new tail is the (length - k)-th node counted from the head
        int stepsToNewTail = length - k;
        ListNode newTail = head;
        for (int i = 1; i < stepsToNewTail; i++) {
            newTail = newTail.next;
        }

        // Step 5: open the ring right after the new tail
        ListNode newHead = newTail.next;
        newTail.next = null;
        return newHead;
    }

    // ---- test helpers ----

    static ListNode build(int... vals) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        for (int v : vals) {
            cur.next = new ListNode(v);
            cur = cur.next;
        }
        return dummy.next;
    }

    static String toList(ListNode head) {
        StringBuilder sb = new StringBuilder("[");
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (sb.length() > 1) sb.append(", ");
            sb.append(cur.val);
        }
        return sb.append("]").toString();
    }

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    static String rotated(ListNode head, int k) {
        return toList(rotateRight(head, k));
    }

    public static void main(String[] args) {
        print("case 1 typical      [1,2,3,4,5] k=2 ", rotated(build(1, 2, 3, 4, 5), 2),
                "[4, 5, 1, 2, 3]");
        print("case 2 k > length   [0,1,2]     k=4 ", rotated(build(0, 1, 2), 4), "[2, 0, 1]");
        print("case 3 single node  [1]         k=99", rotated(build(1), 99), "[1]");
        print("case 4 k == length  [1,2,3]     k=3 ", rotated(build(1, 2, 3), 3), "[1, 2, 3]");
        print("case 5 empty        []          k=5 ", rotated(null, 5), "[]");
    }
}

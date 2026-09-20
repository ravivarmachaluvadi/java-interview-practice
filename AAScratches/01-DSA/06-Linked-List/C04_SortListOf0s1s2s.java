/*
 * =====================================================================
 *  Sort a Linked List of 0s, 1s and 2s              GfG / Striver | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a singly linked list whose node values are only 0, 1 or 2, sort it in
 *   ascending order. Return the new head. Do it in one pass, preferably by relinking nodes
 *   rather than overwriting values.
 *
 * EXAMPLE
 *   [1,2,0,1,2,0,1]  ->  [0,0,1,1,1,2,2]
 *   [2,0,2,0]        ->  [0,0,2,2]      no 1s: the middle chain is empty
 *   [2,2]            ->  [2,2]          no 0s and no 1s
 *   [1]              ->  [1]
 *
 * APPROACH  (Dutch National Flag with three chains)
 *   1. Create three sentinel nodes zeroHead, oneHead, twoHead with tail pointers zero, one, two.
 *   2. Walk the list once; append each node to the chain matching its value (tail.next = node;
 *      tail = node). Relative order inside each chain is preserved.
 *   3. Join: zero.next = (ones exist ? oneHead.next : twoHead.next);  one.next = twoHead.next;
 *      two.next = null. Return zeroHead.next.
 *   The join must tolerate an EMPTY middle chain: if there are no 1s, the zero chain must
 *   connect straight to the 2s, and if there are no 0s, zeroHead.next is set by that same line.
 *
 * KEY INSIGHT
 *   This is Partition List (C03) with three buckets instead of two. All the new difficulty is
 *   in the join: each chain's tail must point to the head of the NEXT NON-EMPTY chain, and the
 *   last chain must be null-terminated to cut any stale link back into the old list.
 *
 *   A second method, sortByCounting, shows the simpler two-pass answer: count 0s/1s/2s, then
 *   overwrite values. Interviewers usually accept it, then ask for the relink version because
 *   "you must not change node values" (nodes may carry other data).
 *
 * COMPLEXITY
 *   Time  O(n)  one pass to distribute, O(1) to join  (counting version: two passes, still O(n))
 *   Space O(1)  three sentinels and three tail pointers
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not the array Dutch-flag swap (low/mid/high)? Singly linked lists have no O(1)
 *     backward move or index swap; the chain approach is the list-native equivalent.
 *   - Generalise to k distinct values: k sentinels, or a bucket array of (head, tail) pairs.
 *   - What if the values were arbitrary, not 0/1/2? Then you need merge sort on the list
 *     (C12 in this folder), O(n log n).
 *
 * RUN
 *   main() runs 4 cases for the relink method and the same 4 for the counting method, printing
 *   actual vs expected for each.
 */
class Solution {

    /** Relink nodes into three chains and join them; node values are never modified. */
    public ListNode sortList(ListNode head) {
        if (head == null || head.next == null) return head;

        ListNode zeroHead = new ListNode(-1);
        ListNode oneHead = new ListNode(-1);
        ListNode twoHead = new ListNode(-1);
        // tails of the three chains; they advance as nodes are appended
        ListNode zero = zeroHead;
        ListNode one = oneHead;
        ListNode two = twoHead;

        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (cur.val == 0) {
                zero.next = cur;
                zero = cur;
            } else if (cur.val == 1) {
                one.next = cur;
                one = cur;
            } else {
                two.next = cur;
                two = cur;
            }
        }

        // join: skip the 1s chain when it is empty, and always cut the tail of the 2s chain
        zero.next = (oneHead.next != null) ? oneHead.next : twoHead.next;
        one.next = twoHead.next;
        two.next = null;

        return zeroHead.next;
    }

    /** Two-pass alternative: count each value, then overwrite values in order. */
    public ListNode sortByCounting(ListNode head) {
        int[] count = new int[3];
        for (ListNode cur = head; cur != null; cur = cur.next) count[cur.val]++;

        ListNode cur = head;
        for (int value = 0; value <= 2; value++) {
            for (int i = 0; i < count[value]; i++) {
                cur.val = value;
                cur = cur.next;
            }
        }
        return head;
    }
}

class SortaLLof0s1sand2s {

    public static void main(String[] args) {
        Solution sol = new Solution();

        System.out.println("-- relink into three chains --");
        print("case 1 [1,2,0,1,2,0,1]", sol.sortList(build(1, 2, 0, 1, 2, 0, 1)),
                "[0, 0, 1, 1, 1, 2, 2]");
        print("case 2 [2,0,2,0]      ", sol.sortList(build(2, 0, 2, 0)), "[0, 0, 2, 2]");
        print("case 3 [2,2]          ", sol.sortList(build(2, 2)), "[2, 2]");
        print("case 4 [1]            ", sol.sortList(build(1)), "[1]");

        System.out.println("-- count then overwrite --");
        print("case 1 [1,2,0,1,2,0,1]", sol.sortByCounting(build(1, 2, 0, 1, 2, 0, 1)),
                "[0, 0, 1, 1, 1, 2, 2]");
        print("case 2 [2,0,2,0]      ", sol.sortByCounting(build(2, 0, 2, 0)), "[0, 0, 2, 2]");
        print("case 3 [2,2]          ", sol.sortByCounting(build(2, 2)), "[2, 2]");
        print("case 4 [1]            ", sol.sortByCounting(build(1)), "[1]");
    }

    static void print(String label, ListNode actual, String expected) {
        System.out.println(label + ": " + toStr(actual) + "   expected " + expected);
    }

    static ListNode build(int... vals) {
        ListNode dummy = new ListNode();
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

class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}

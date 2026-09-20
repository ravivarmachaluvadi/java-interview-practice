/*
 * =====================================================================
 *  Partition List                                  LeetCode 86 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a linked list and a value x, rearrange it so every node with value < x
 *   comes before every node with value >= x. The relative order inside each group must be
 *   preserved (this is a STABLE partition, unlike quicksort's partition step).
 *
 * EXAMPLE
 *   [1,4,3,2,5,2], x = 3  ->  [1,2,2,4,3,5]   less: 1,2,2  greater-or-equal: 4,3,5
 *   [2,1], x = 2          ->  [1,2]
 *   [4,5], x = 1          ->  [4,5]           "less" chain is empty
 *   [], x = 0             ->  []
 *
 * APPROACH  (two dummy chains, stable relink)
 *   1. Create two sentinel nodes, lessHead and greaterHead, with tail pointers less and greater.
 *   2. Walk the original list once. Append each node to the less chain if val < x, otherwise to
 *      the greater chain. Appending means tail.next = node; tail = node. Order is preserved
 *      because we only ever append.
 *   3. After the walk: greater.next = null (cut the stale link into the old list),
 *      less.next = greaterHead.next (splice the chains), return lessHead.next.
 *
 * KEY INSIGHT
 *   Reuse the existing nodes: you are not building a new list, you are re-threading the old
 *   one into two chains and joining them. The one trap is step 3: the last node of the greater
 *   chain still points at whatever followed it originally, and if that node ended up in the less
 *   chain you now have a CYCLE. Always null-terminate the second chain before splicing.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass Space O(1)  two sentinels and two tail pointers; no new data nodes
 *
 * INTERVIEW FOLLOW-UPS
 *   - Sort a list of 0s, 1s and 2s: same idea with three chains (C04_SortListOf0s1s2s in this
 *   folder).
 *   - Why not swap values in place? Stability requires moving nodes, not values, and an
 *     array-style in-place stable partition is O(n^2) or needs extra memory.
 *   - Odd/Even list (LeetCode 328) is the same split-and-splice without sentinels.
 *
 * RUN
 *   main() runs 4 cases (typical, two nodes, empty less-chain, empty list) and prints actual
 *   vs expected.
 */
class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
        this.next = null;
    }
}

class PartitionList {

    public ListNode partition(ListNode head, int x) {
        ListNode lessHead = new ListNode(0);
        ListNode greaterHead = new ListNode(0);
        ListNode less = lessHead;       // tail of the "< x" chain
        ListNode greater = greaterHead; // tail of the ">= x" chain

        while (head != null) {
            if (head.val < x) {
                less.next = head;
                less = less.next;
            } else {
                greater.next = head;
                greater = greater.next;
            }
            head = head.next;
        }
        greater.next = null;            // cut the stale link, otherwise a cycle is possible
        less.next = greaterHead.next;   // splice: less chain, then greater chain
        return lessHead.next;
    }

    public static void main(String[] args) {
        PartitionList solution = new PartitionList();

        print("case 1 [1,4,3,2,5,2] x=3", solution.partition(build(1, 4, 3, 2, 5, 2), 3),
                "[1, 2, 2, 4, 3, 5]");
        print("case 2 [2,1] x=2        ", solution.partition(build(2, 1), 2), "[1, 2]");
        print("case 3 [4,5] x=1        ", solution.partition(build(4, 5), 1), "[4, 5]");
        print("case 4 [] x=0           ", solution.partition(build(), 0), "[]");
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

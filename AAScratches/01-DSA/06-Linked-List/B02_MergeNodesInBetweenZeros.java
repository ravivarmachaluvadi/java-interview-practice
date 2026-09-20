/*
 * =====================================================================
 *  Merge Nodes in Between Zeros                    LeetCode 2181 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   A singly linked list starts and ends with 0 and has no two consecutive zeros, so
 *   the zeros act as separators. Replace every run of non-zero values between two zeros
 *   with one node holding their sum, drop all zeros, and return the new head.
 *   Constraints: 3 <= length <= 2 * 10^5, 0 <= val <= 1000.
 *
 * EXAMPLE
 *   0 -> 3 -> 1 -> 0 -> 4 -> 5 -> 2 -> 0  ->  4 -> 11        (3+1, 4+5+2)
 *   0 -> 1 -> 0 -> 3 -> 0 -> 2 -> 2 -> 0  ->  1 -> 3 -> 4
 *   0 -> 5 -> 0                            ->  5              (single segment)
 *
 * APPROACH  (accumulate each segment, reuse its first node)
 *   1. Skip the leading zero: modifyHead = head.next is the first node of segment 1 and
 *      will be reused to hold that segment's sum. sumHead walks ahead from there.
 *   2. Walk sumHead forward adding values until it lands on a zero.
 *   3. Store the sum in modifyHead.val, then step sumHead past the zero: it is now either
 *      null (list finished) or the first node of the next segment.
 *   4. Link modifyHead.next = sumHead and move modifyHead there, so the skipped nodes
 *      and the zero fall out of the chain. Repeat until sumHead is null.
 *   5. Return head.next, the reused first node.
 *
 * KEY INSIGHT
 *   You do not need a new list. The first node of each segment is recycled as the
 *   "sum node", and re-pointing its next to the start of the following segment removes
 *   the rest of the segment and the separating zero in one assignment. The pattern is
 *   the same as in-place array compaction: a slow "write" pointer and a fast "read"
 *   pointer over the same storage.
 *
 * COMPLEXITY
 *   Time  O(n)  sumHead visits every node once; modifyHead only visits segment starts
 *   Space O(1)  no allocation, only two pointers and an int
 *
 * INTERVIEW FOLLOW-UPS
 *   - Same problem but allocate a fresh list: dummy head + append a new node per segment.
 *   - Return the maximum segment sum instead of the merged list (same walk, no relinking).
 *   - What if the list did not start with 0? Handle a leading segment before the loop.
 *
 * RUN
 *   main() runs 3 cases (typical, several short segments, single segment) and prints
 *   actual vs expected.
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

// https://leetcode.com/problems/merge-nodes-in-between-zeros/description/
class MergeNodesInBetweenZeros {

    public ListNode mergeNodes(ListNode head) {
        // modifyHead = first node after the leading zero; it is reused to hold the segment sum
        ListNode modifyHead = head.next;
        ListNode sumHead = modifyHead;

        while (sumHead != null) {
            int sum = 0;
            // Add up every node until the next zero (guaranteed to exist: list ends with 0)
            while (sumHead.val != 0) {
                sum += sumHead.val;
                sumHead = sumHead.next;
            }
            modifyHead.val = sum;
            sumHead = sumHead.next;          // step past the zero: null, or next segment start

            modifyHead.next = sumHead;       // drop the rest of this segment and the zero
            modifyHead = modifyHead.next;
        }
        return head.next;
    }

    static void runCase(String label, int[] input, String expected) {
        ListNode result = new MergeNodesInBetweenZeros().mergeNodes(ListNode.fromArray(input));
        System.out.println(label + ListNode.toString(result) + "   expected " + expected);
    }

    public static void main(String[] args) {
        runCase("case 1 (typical):        ", new int[]{0, 3, 1, 0, 4, 5, 2, 0}, "[4, 11]");
        runCase("case 2 (short segments): ", new int[]{0, 1, 0, 3, 0, 2, 2, 0}, "[1, 3, 4]");
        runCase("case 3 (single segment): ", new int[]{0, 5, 0}, "[5]");
    }
}

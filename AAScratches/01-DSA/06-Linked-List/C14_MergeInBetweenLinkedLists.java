/*
 * =====================================================================
 *  Merge In Between Linked Lists                  LeetCode 1669 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given list1, two 0-based indices a and b, and list2: remove list1's nodes from index
 *   a through b inclusive and put all of list2 in their place. Return list1's head.
 *   Constraints guarantee 1 <= a <= b < list1.length - 1, so the head and the tail of
 *   list1 are never removed, and list2 has at least one node.
 *
 * EXAMPLE
 *   list1 = [0,1,2,3,4,5], a = 2, b = 4, list2 = [1000000,1000001,1000002]
 *       ->  [0, 1, 1000000, 1000001, 1000002, 5]
 *   list1 = [0,1,2], a = 1, b = 1, list2 = [9]         ->  [0, 9, 2]   (remove one node)
 *   list1 = [0,1,2,3,4,5], a = 1, b = 4, list2 = [7,8] ->  [0, 7, 8, 5] (cut to the edges)
 *
 * APPROACH  (index-walk to two cut points, then splice)
 *   1. Walk cur from list1's head until index == a - 1. That node is firstLink, the last
 *      node we keep before the gap.
 *   2. Keep walking the same cur until index == b. Its next node is secondLink, the first
 *      node we keep after the gap.
 *   3. Point firstLink.next at list2's head: the removed nodes are now unreachable.
 *   4. Walk to the last node of list2 and point its next at secondLink.
 *   5. Return list1's original head (it is never removed).
 *
 * KEY INSIGHT
 *   You only ever need two nodes from list1: the one just BEFORE the gap (index a-1) and
 *   the one just AFTER it (index b+1). Everything else is a matter of reaching them with
 *   a plain counter. Off-by-one errors come from stopping at a instead of a-1, or at b-1
 *   instead of b; write the stopping condition as "index == a - 1" and "index == b"
 *   rather than juggling < and <=.
 *
 * COMPLEXITY
 *   Time  O(b + len(list2))  one walk to index b in list1, one walk to the end of list2
 *   Space O(1)  three pointers and a counter
 *
 * INTERVIEW FOLLOW-UPS
 *   - What if a could be 0 (remove the head)? Use a dummy node in front of list1.
 *   - What if list2 could be empty? firstLink.next = secondLink directly.
 *   - Return the removed segment as its own list too: null-terminate it at node b.
 *
 * RUN
 *   main() runs 3 cases (typical, remove a single node, cut right up to both edges)
 *   and prints actual vs expected.
 */

// https://leetcode.com/problems/merge-in-between-linked-lists/description/
class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
    }

    // Helper to build a list from array
    static ListNode fromArray(int[] arr) {
        if (arr == null || arr.length == 0) return null;
        ListNode head = new ListNode(arr[0]);
        ListNode curr = head;
        for (int i = 1; i < arr.length; i++) {
            curr.next = new ListNode(arr[i]);
            curr = curr.next;
        }
        return head;
    }

    // Prints the whole chain from this node, e.g. [0, 1, 2]
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (ListNode curr = this; curr != null; curr = curr.next) {
            if (sb.length() > 1) sb.append(", ");
            sb.append(curr.val);
        }
        return sb.append("]").toString();
    }
}

class Solution {

    public ListNode mergeInBetween(ListNode list1, int a, int b, ListNode list2) {
        ListNode head = list1;
        ListNode cur = head;

        int index = 0;
        // Stop just before index a: this is the last node kept in front of the gap
        while (index != a - 1) {
            cur = cur.next;
            index++;
        }
        ListNode firstLink = cur;

        // Continue to index b: the node after it is the first node kept after the gap
        while (index != b) {
            cur = cur.next;
            index++;
        }
        ListNode secondLink = cur.next;

        // Splice list2 in: nodes a..b become unreachable
        firstLink.next = list2;

        // Walk to the last node of list2 and reattach the tail of list1
        cur = list2;
        while (cur.next != null) {
            cur = cur.next;
        }
        cur.next = secondLink;

        return head;
    }
}

class MergeInBetweenLinkedLists {

    static void runCase(String label, int[] list1, int a, int b, int[] list2, String expected) {
        ListNode merged = new Solution().mergeInBetween(
                ListNode.fromArray(list1), a, b, ListNode.fromArray(list2));
        System.out.println(label + merged + "   expected " + expected);
    }

    public static void main(String[] args) {
        runCase("case 1 (typical):      ", new int[]{0, 1, 2, 3, 4, 5}, 2, 4,
                new int[]{1000000, 1000001, 1000002}, "[0, 1, 1000000, 1000001, 1000002, 5]");
        runCase("case 2 (a == b):       ", new int[]{0, 1, 2}, 1, 1,
                new int[]{9}, "[0, 9, 2]");
        runCase("case 3 (cut to edges): ", new int[]{0, 1, 2, 3, 4, 5}, 1, 4,
                new int[]{7, 8}, "[0, 7, 8, 5]");
    }
}

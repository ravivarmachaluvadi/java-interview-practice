/*
 * =====================================================================
 *  Intersection of Two Linked Lists                  LeetCode 160 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Two singly linked lists may share a tail: from some node onward they are the SAME
 *   nodes (a Y shape), not just equal values. Return that first shared node, or null if
 *   the lists never meet. The lists must be left unchanged and O(1) extra space is
 *   expected.
 *
 * EXAMPLE
 *   A = 4 -> 1 -> [8 -> 4 -> 5],  B = 5 -> 6 -> 1 -> [8 -> 4 -> 5]   ->  node 8
 *   A = 2 -> 6 -> 4,              B = 1 -> 5                          ->  null
 *   A = B = [3 -> 7]  (same head)                                     ->  node 3
 *
 * APPROACH  (length difference, then walk together)
 *   1. Count the nodes in A (count1) and in B (count2).
 *   2. Advance the head of the longer list by |count1 - count2| nodes, so both pointers
 *      now have the same number of nodes left to the end.
 *   3. Move both pointers one step at a time while h1 != h2. They are compared by
 *      reference, so they stop at the first shared node, or both reach null together.
 *   4. Return h1 (null when there is no intersection).
 *   getIntersectionNodeTwoPointer() shows the interview follow-up: run two pointers,
 *   and when one falls off the end restart it at the OTHER list's head; both then travel
 *   lenA + lenB steps and meet at the junction (or at null).
 *
 * KEY INSIGHT
 *   After the shared node, both lists are identical, so the tails have the same length.
 *   Any difference in total length is entirely in the private prefixes. Skipping that
 *   difference aligns the two pointers so that they reach the junction at the same step.
 *   Compare nodes with ==, never with .val: equal values on different nodes are not an
 *   intersection.
 *
 * COMPLEXITY
 *   Time  O(n + m)  one pass to count each list, one aligned pass to find the junction
 *   Space O(1)  a handful of pointers and counters
 *
 * INTERVIEW FOLLOW-UPS
 *   - The pointer-swap trick (two pointers, restart at the other head) without lengths.
 *   - HashSet of A's nodes, then scan B: O(n) space, but the first idea to mention.
 *   - What if one list has a cycle? Detect it first; the plain walks would never end.
 *
 * RUN
 *   main() runs 3 cases (Y junction, no intersection, identical lists) through both
 *   methods and prints the value of the returned node vs expected.
 */

class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
    }
}

// https://leetcode.com/problems/intersection-of-two-linked-lists/description/
class IntersectionOfTwoLinkedLists {

    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        int count1 = length(headA);
        int count2 = length(headB);

        // Skip the first |diff| nodes of the longer list so both have equal nodes remaining
        ListNode h1 = headA, h2 = headB;
        if (count1 < count2) {
            h2 = advance(h2, count2 - count1);
        } else {
            h1 = advance(h1, count1 - count2);
        }

        // Now walk in lock-step; reference equality stops at the Y junction or at null
        while (h1 != h2) {
            h1 = h1.next;
            h2 = h2.next;
        }
        return h1;
    }

    // Follow-up version: no lengths. Each pointer walks its own list then the other one,
    // so both cover lenA + lenB nodes and line up at the junction (or both hit null).
    public ListNode getIntersectionNodeTwoPointer(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) return null;
        ListNode p1 = headA, p2 = headB;
        while (p1 != p2) {
            p1 = (p1 == null) ? headB : p1.next;
            p2 = (p2 == null) ? headA : p2.next;
        }
        return p1;
    }

    private static int length(ListNode node) {
        int count = 0;
        for (ListNode cur = node; cur != null; cur = cur.next) count++;
        return count;
    }

    private static ListNode advance(ListNode node, int steps) {
        for (int i = 0; i < steps; i++) node = node.next;
        return node;
    }
}

class Main {

    static String describe(ListNode node) {
        return node == null ? "null" : "node " + node.val;
    }

    static void runCase(String label, ListNode headA, ListNode headB, String expected) {
        IntersectionOfTwoLinkedLists solution = new IntersectionOfTwoLinkedLists();
        ListNode byLength = solution.getIntersectionNode(headA, headB);
        ListNode byTwoPointer = solution.getIntersectionNodeTwoPointer(headA, headB);
        System.out.println(label + " length-diff: " + describe(byLength)
                + "   expected " + expected);
        System.out.println(label + " two-pointer: " + describe(byTwoPointer)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1: List A: 4 -> 1 -> 8 -> 4 -> 5, List B: 5 -> 6 -> 1 -> 8 -> 4 -> 5, shared from 8
        ListNode shared = new ListNode(8);
        shared.next = new ListNode(4);
        shared.next.next = new ListNode(5);

        ListNode headA = new ListNode(4);
        headA.next = new ListNode(1);
        headA.next.next = shared;

        ListNode headB = new ListNode(5);
        headB.next = new ListNode(6);
        headB.next.next = new ListNode(1);
        headB.next.next.next = shared;
        runCase("case 1 (Y junction):    ", headA, headB, "node 8");

        // case 2: no shared nodes, even though both lists exist
        ListNode noA = new ListNode(2);
        noA.next = new ListNode(6);
        noA.next.next = new ListNode(4);
        ListNode noB = new ListNode(1);
        noB.next = new ListNode(5);
        runCase("case 2 (no intersection):", noA, noB, "null");

        // case 3: both heads are the same node, so the intersection is the head itself
        ListNode same = new ListNode(3);
        same.next = new ListNode(7);
        runCase("case 3 (same head):     ", same, same, "node 3");
    }
}

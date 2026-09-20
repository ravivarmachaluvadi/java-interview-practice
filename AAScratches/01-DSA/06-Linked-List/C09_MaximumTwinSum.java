/*
 * =====================================================================
 *  Maximum Twin Sum of a Linked List           LeetCode 2130 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a singly linked list of EVEN length n, node i (0-based) is the twin of node
 *   n - 1 - i for 0 <= i < n / 2. Return the largest twin sum: the maximum over all
 *   pairs (i, n - 1 - i) of node[i].val + node[n - 1 - i].val.
 *   Constraints: 2 <= n <= 10^5, n is even, values are positive.
 *
 * EXAMPLE
 *   [5, 4, 2, 1]   ->  6        pairs (5,1) = 6 and (4,2) = 6
 *   [4, 2, 2, 3]   ->  7        pairs (4,3) = 7 and (2,2) = 4
 *   [1, 100000]    ->  100001   smallest list, one pair
 *
 * APPROACH  (middle, reverse second half, pair walk)
 *   1. slow/fast: when fast reaches the end, slow sits on the first node of the second
 *      half (for even n the loop guard leaves slow at index n/2).
 *   2. Reverse the second half in place starting from slow; keep its new head.
 *   3. Walk the first half from head and the reversed half together. Each step lines
 *      up node i with node n - 1 - i. Track the maximum sum.
 *
 * KEY INSIGHT
 *   You cannot index a linked list, so "pair node i with node n-1-i" is done by
 *   reversing the second half and walking both halves forward. This is the same
 *   primitive as B03_PalindromeLinkedList and C10_ReorderList; twin sum is the gentlest
 *   version because n is even (clean split) and the list does not need to be restored.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass to find the middle, one to reverse, one to pair up
 *   Space O(1)  pointer surgery only; the array or stack version costs O(n)
 *
 * INTERVIEW FOLLOW-UPS
 *   - O(n) space alternative: push the first half on a stack, then pop while walking on.
 *   - Restore the list afterwards: reverse the second half again (see
 *     B03_PalindromeLinkedList, which always restores before returning).
 *   - Odd-length variant: define what happens to the lone middle node first.
 *   - Return the twin pair itself, not just the sum: track the pair when the max updates.
 *
 * RUN
 *   main() runs 3 cases (typical with a tie, typical, two-node edge)
 *   and prints actual vs expected.
 */
class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
    }
}

// https://leetcode.com/problems/maximum-twin-sum-of-a-linked-list/description/
class MaximumTwinSum {

    public static int pairSum(ListNode head) {
        // Step 1: slow lands on the first node of the second half (n is even)
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // Step 2: reverse the second half so twin nodes line up
        ListNode secondHalf = reverseList(slow);

        // Step 3: walk both halves together, tracking the largest pair sum
        int maxSum = 0;
        ListNode firstHalf = head;
        while (secondHalf != null) {
            maxSum = Math.max(maxSum, firstHalf.val + secondHalf.val);
            firstHalf = firstHalf.next;
            secondHalf = secondHalf.next;
        }
        return maxSum;
    }

    // Standard iterative reversal (A02_ReverseLinkedList) applied from the middle onward.
    private static ListNode reverseList(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode nextNode = head.next;
            head.next = prev;
            prev = head;
            head = nextNode;
        }
        return prev;
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

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical   [5,4,2,1] ", pairSum(build(5, 4, 2, 1)), 6);
        print("case 2 typical   [4,2,2,3] ", pairSum(build(4, 2, 2, 3)), 7);
        print("case 3 two nodes [1,100000]", pairSum(build(1, 100000)), 100001);
    }
}

/**
 * Problem: Given a singly linked list that starts and ends with 0 and has no two consecutive zeros,
 * replace each sublist of non‑zero values between zeros with a single node whose value is the sum
 * of those values, returning the modified list without the leading zero.
 *
 * Approach: Iterate through the list starting after the first zero. For each segment until the next
 * zero, accumulate the sum in a temporary variable. Assign this sum to the current node, then link
 * it to the node following the zero and continue from there. This reuses existing nodes, avoiding
 * extra allocations.
 *
 * Time Complexity: O(n), where n is the number of nodes, since each node is visited once.
 * Space Complexity: O(1) auxiliary space; only a few pointers and an integer are used.
 */
class ListNode {
    int val;
    ListNode next;

}

/**
 * Constraints:
 * The number of nodes in the list is in the range [3, 2 * 105].
 * <p>
 * 0 <= Node.val <= 1000
 * <p>
 * There are no two consecutive nodes with Node.val == 0.
 * <p>
 * The beginning and end of the linked list have Node.val == 0
 */
class MergeNodesInBetweenZeros {

    public ListNode mergeNodes(ListNode head) {
        // Initialize a sentinel/dummy node with the first non-zero value.
        ListNode modifyHead = head.next;
        ListNode sumHead = modifyHead;

        while (sumHead != null) {
            int sum = 0;
            // Find the sum of all nodes until you encounter a 0.
            while (sumHead.val != 0) {
                sum += sumHead.val;
                sumHead = sumHead.next;
            }
            modifyHead.val = sum;
            sumHead = sumHead.next;

            modifyHead.next = sumHead;
            modifyHead = modifyHead.next;
        }
        return head.next;
    }

    public static void main(String[] args) {
        // Example usage:
        ListNode head = new ListNode();
        head.val = 0;
        head.next = new ListNode();
        head.next.val = 3;
        head.next.next = new ListNode();
        head.next.next.val = 1;
        head.next.next.next = new ListNode();
        head.next.next.next.val = 0;
        head.next.next.next.next = new ListNode();
        head.next.next.next.next.val = 4;
        head.next.next.next.next.next = new ListNode();
        head.next.next.next.next.next.val = 5;
        head.next.next.next.next.next.next = new ListNode();
        head.next.next.next.next.next.next.val = 2;
        head.next.next.next.next.next.next.next = new ListNode();
        head.next.next.next.next.next.next.next.val = 0;

        MergeNodesInBetweenZeros solution = new MergeNodesInBetweenZeros();
        ListNode result = solution.mergeNodes(head);

        // Print the modified list
        while (result != null) {
            System.out.print(result.val + " ");
            result = result.next;
        }
    }
}
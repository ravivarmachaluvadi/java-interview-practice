/**
 * Problem:
 * Given the head of a singly linked list and an integer x, reorder the list so that all nodes with values less than x come before nodes with values greater than or equal to x.
 * The relative order among nodes in each partition must remain unchanged.
 *
 * Approach:
 * Create two dummy heads for "less" and "greater/equal" partitions. Traverse the original list once,
 * appending each node to the appropriate partition based on its value. After traversal, terminate
 * the greater partition with null and link the less partition to the head of the greater partition.
 *
 * Complexity:
 * Time:  O(n) – single pass through n nodes.
 * Space: O(1) – only a few pointers are used; no additional data structures proportional to input size.
 */
class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
        this.next = null;
    }
}
/**
 * Given the head of a linked list and a value x, partition it such
 * <p>
 * that all nodes less than x come before nodes greater than or equal to x.
 * <p>
 * You should preserve the original relative order of the nodes in each of the two partitions.
 */

// https://leetcode.com/problems/partition-list/description/
class PartitionList {
    public ListNode partition(ListNode head, int x) {
        ListNode lessHead = new ListNode(0);
        ListNode greaterHead = new ListNode(0);
        ListNode less = lessHead;
        ListNode greater = greaterHead;

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
        greater.next = null;
        less.next = greaterHead.next;
        return lessHead.next;
    }

    public static void main(String[] args) {
        // Example usage
        PartitionList solution = new PartitionList();
        ListNode head = new ListNode(1);
        head.next = new ListNode(4);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(2);
        head.next.next.next.next = new ListNode(5);
        head.next.next.next.next.next = new ListNode(2);

        ListNode result = solution.partition(head, 3);

        // Print the result
        while (result != null) {
            System.out.print(result.val + " ");
            result = result.next;
        }
        // Output: 1 2 2 4 3 5
    }
}

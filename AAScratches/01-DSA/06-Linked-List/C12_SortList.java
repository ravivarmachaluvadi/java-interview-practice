/**
 * Problem: Sort a singly linked list in ascending order.
 *
 * Approach:
 * 1. Use merge sort on the linked list by recursively splitting it into halves
 *    using fast/slow pointers until sublists of size one are reached.
 * 2. Merge the sorted halves with a two‑pointer technique, building a new list.
 *
 * Time Complexity: O(n log n) – each level splits the list in half and merges all nodes once.
 * Space Complexity: O(log n) – recursion depth for merge sort; no extra data structures used.
 */
class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
        next = null;
    }
}

class SortList {

    // Method to sort the linked list
    public ListNode sortList(ListNode head) {
        // remember this base condition in recusion call
        if (head == null || head.next == null) return head;

        // Step 1: Split the list into two halves
        // mid is head of second half of linked list
        ListNode mid = getMid(head);
        ListNode left = sortList(head);
        ListNode right = sortList(mid);

        // Step 2: Merge the sorted halves
        return merge(left, right);
    }

    private ListNode getMid(ListNode head) {
        //here prev is pointer before mid of linked list
        ListNode prev = null;
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            prev = slow;
            slow = slow.next;
            fast = fast.next.next;
        }

        // (UNILINK) SPLIT THE LIST
        if (prev != null) prev.next = null;
        // now slow is head of second half of the linked list
        return slow;
    }

    // Method to merge two sorted lists
    private ListNode merge(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val < list2.val) {
                tail.next = list1;
                list1 = list1.next;
            } else {
                tail.next = list2;
                list2 = list2.next;
            }
            tail = tail.next;
        }
        // one of the two halfs ran out
        if (list1 != null) tail.next = list1;
        else tail.next = list2;

        return dummy.next;
    }

    // Helper method to print the list
    public void printList(ListNode head) {
        ListNode current = head;
        while (current != null) {
            System.out.print(current.val + " ");
            current = current.next;
        }
        System.out.println();
    }

    // Main method to test the sortList method
    public static void main(String[] args) {
        SortList sorter = new SortList();

        // Example list: 4 -> 2 -> 1 -> 3
        ListNode head = new ListNode(4);
        head.next = new ListNode(2);
        head.next.next = new ListNode(1);
        head.next.next.next = new ListNode(3);

        System.out.println("Original List:");
        sorter.printList(head);

        // Sort the list
        ListNode sortedHead = sorter.sortList(head);

        System.out.println("Sorted List:");
        sorter.printList(sortedHead);
    }
}


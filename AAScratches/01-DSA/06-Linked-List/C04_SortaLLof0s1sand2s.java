/**
 * Problem: Sort a singly linked list that contains only the values 0, 1, and 2
 * (the Dutch National Flag problem for linked lists).
 *
 * Approach: Create three dummy head nodes for zeroes, ones, and twos.
 * Traverse the original list once, appending each node to its corresponding
 * sublist. After traversal, link the three sublists together and terminate
 * the final list with null. Return the merged list starting after the zero
 * dummy head.
 *
 * Time Complexity: O(n) – one pass through the list.
 * Space Complexity: O(1) – only a few pointers are used; no extra nodes
 * beyond the three dummy heads.
 */
import java.util.*;

class Solution {

    public ListNode sortList(ListNode head) {
        if (head == null || head.next == null)
            return head;
        ListNode zeroHead = new ListNode(-1);
        ListNode oneHead = new ListNode(-1);
        ListNode twoHead = new ListNode(-1);
        // will be moved and hold tail pointers
        ListNode zero = zeroHead;
        ListNode one = oneHead;
        ListNode two = twoHead;
        // represents given slingle linked list
        ListNode temp = head;

        // temp representing single linked list which
        // comprises of 0,1,2 as node's values
        while (temp != null) {
            if (temp.val == 0) {
                zero.next = temp;
                zero = temp;
            } else if (temp.val == 1) {
                one.next = temp;
                one = temp;
            } else if (temp.val == 2) {
                two.next = temp;
                two = temp;
            }
            temp = temp.next;
        }

        zero.next = (oneHead.next != null) ? oneHead.next : twoHead.next;
        one.next = twoHead.next;
        two.next = null;

        ListNode newHead = zeroHead.next;
        return newHead;
    }
}

class SortaLLof0s1sand2s {
    public static void printList(ListNode head) {
        while (head != null) {
            System.out.print(head.val + " ");
            head = head.next;
        }
        System.out.println();
    }

    // inserting new node
    public static ListNode newNode(int data) {
        return new ListNode(data);
    }

    public static void main(String[] args) {
        ListNode head = newNode(1);
        head.next = newNode(2);
        head.next.next = newNode(0);
        head.next.next.next = newNode(1);
        head.next.next.next.next = newNode(2);
        head.next.next.next.next.next = newNode(0);
        head.next.next.next.next.next.next = newNode(1);

        System.out.print("Original list: ");
        printList(head);

        Solution sol = new Solution();
        head = sol.sortList(head);

        System.out.print("Sorted list: ");
        printList(head);
    }
}


class ListNode {
    int val;
    ListNode next;

    ListNode() {
        val = 0;
        next = null;
    }

    ListNode(int data1) {
        val = data1;
        next = null;
    }

    ListNode(int data1, ListNode next1) {
        val = data1;
        next = next1;
    }
}
// https://leetcode.com/problems/merge-in-between-linked-lists/description/
// 1669. Merge In Between Linked Lists
class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
        next = null;
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

    // Helper to convert list to string for printing
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        ListNode curr = this;
        while (curr != null) {
            sb.append(curr.val);
            if (curr.next != null) sb.append("->");
            curr = curr.next;
        }
        return sb.toString();
    }
}

class Solution {

    public ListNode mergeInBetween(ListNode list1, int a, int b, ListNode list2) {
        //list starts with index 0
        //iterate through list1 until index a, assign a ptr1 to this node
        //continue to iterate through list1 until index b, assign a prt2 to this node
        //cut off the connection between these 2 nodes
        //append list2 to ptr1
        //iterate through list2 until last node
        //append pt2 to this last node
        //return head of list1

        ListNode head = list1;
        ListNode cur = head;

        int index = 0;
        //stop just before index a
        while (index != a - 1) {
            cur = cur.next;
            index++;
        }

        ListNode firstLink = cur;//node before a

        while (index != b) {
            cur = cur.next;
            index++;
        }

        ListNode secondLink = cur.next;//node after b

        //delete the nodes from index a to b
        firstLink.next = null;
        cur.next = null;

        //connect first part of list1 to list2
        firstLink.next = list2;

        //start cur from head of list2
        cur = list2;
        //stop at last node in list2

        while (cur.next != null) {
            cur = cur.next;
        }

        //connect end of list2 to second half of list1
        cur.next = secondLink;

        return head;

        //edge case: a index can be first node in list1? -> No
        //edge case: b index can be last node in list1? -> No
    }
}

class MergeInBetweenLinkedLists {
    public static void main(String[] args) {
        // Example:
        // list1 = [0,1,2,3,4,5]
        // a = 2, b = 4
        // list2 = [1000000,1000001,1000002]
        //
        // After merge: [0,1,1000000,1000001,1000002,5]

        ListNode list1 = ListNode.fromArray(new int[]{0, 1, 2, 3, 4, 5});
        ListNode list2 = ListNode.fromArray(new int[]{1000000, 1000001, 1000002});
        int a = 2;
        int b = 4;

        System.out.println("Before merge:");
        System.out.println("list1 = " + list1);
        System.out.println("list2 = " + list2);

        Solution sol = new Solution();
        ListNode merged = sol.mergeInBetween(list1, a, b, list2);

        System.out.println("After merge:");
        System.out.println("merged = " + merged);
    }
}

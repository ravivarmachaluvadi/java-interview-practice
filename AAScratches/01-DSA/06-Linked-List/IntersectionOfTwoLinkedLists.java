class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
        next = null;
    }
}

// 160. Intersection of Two Linked Lists
//https://leetcode.com/problems/intersection-of-two-linked-lists/description/
class IntersectionOfTwoLinkedLists {

    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {

        // get lengths of two lists and get diff of two
        // lengths , skip first diff nodes in longer list
        int count1 = 0, count2 = 0;

        ListNode l1 = headA, l2 = headB;
        while (l1 != null) {
            count1++;
            l1 = l1.next;
        }

        while (l2 != null) {
            count2++;
            l2 = l2.next;
        }

        ListNode h1 = headA, h2 = headB;
        if (count1 < count2) {
            int diff = count2 - count1;
            int count = 0;
            while (count != diff) {
                count++;
                h2 = h2.next;
            }
        } else {
            int diff = count1 - count2;
            int count = 0;
            while (count != diff) {
                count++;
                h1 = h1.next;
            }
        }

        while (h1 != h2) {
            h1 = h1.next;
            h2 = h2.next;
        }
        // insersection node like in y junction
        return h1;
    }
}

class Main {
    public static void main(String[] args) {
        // Create intersecting linked lists
        // List A: 4 -> 1 -> 8 -> 4 -> 5
        // List B: 5 -> 6 -> 1 -> 8 -> 4 -> 5
        // Intersection at node with value 8

        // Common part
        ListNode intersect = new ListNode(8);
        intersect.next = new ListNode(4);
        intersect.next.next = new ListNode(5);

        // List A
        ListNode headA = new ListNode(4);
        headA.next = new ListNode(1);
        headA.next.next = intersect;

        // List B
        ListNode headB = new ListNode(5);
        headB.next = new ListNode(6);
        headB.next.next = new ListNode(1);
        headB.next.next.next = intersect;

        IntersectionOfTwoLinkedLists solution = new IntersectionOfTwoLinkedLists();
        ListNode result = solution.getIntersectionNode(headA, headB);

        if (result != null) {
            System.out.println("Intersection Node Value: " + result.val);
        } else {
            System.out.println("No intersection.");
        }
    }
}
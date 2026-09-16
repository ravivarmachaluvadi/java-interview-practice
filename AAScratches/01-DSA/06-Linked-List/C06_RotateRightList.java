class ListNode {
    int val;
    ListNode next;
}

// https://leetcode.com/problems/rotate-list/description/
class RotateRightList {
    public static ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null || k == 0) {
            return head;
        }
        // Step 1: Find the length of the list and the last node
        ListNode current = head;
        int length = 1;
        while (current.next != null) {
            current = current.next;
            length++;
        }

        // Step 2: Make the list circular by connecting the last node to the head
        ListNode lastNode = current;
        lastNode.next = head;
        // Step 3: Normalize k to avoid unnecessary full rotations
        k = k % length;
        if (k == 0) {
            // Break the cycle before returning original list
            lastNode.next = null;
            return head;
        }
        // Step 4: Find the point where the rotation should occur (length - k)
        int stepsToNewHead = length - k;
        ListNode newTail = head;
        for (int i = 1; i < stepsToNewHead; i++) {
            newTail = newTail.next;
        }
        // Step 5: Break the circular list and return the new head
        ListNode newHead = newTail.next;
        newTail.next = null; // Break the cycle
        return newHead;
    }

    public static void main(String[] args) {
        // Create an example linked list: 1 -> 2 -> 3 -> 4 -> 5
        ListNode head = new ListNode();
        head.val = 1;
        head.next = new ListNode();
        head.next.val = 2;
        head.next.next = new ListNode();
        head.next.next.val = 3;
        head.next.next.next = new ListNode();
        head.next.next.next.val = 4;
        head.next.next.next.next = new ListNode();
        head.next.next.next.next.val = 5;

        // Rotate the list by k = 2
        int k = 2;
        RotateRightList rotateRightList = new RotateRightList();
        ListNode newHead = rotateRightList.rotateRight(head, k);

        // Print the rotated list
        System.out.print("Rotated list: ");
        printList(newHead);
    }
    // Helper method to print the list
    public static void printList(ListNode head) {
        ListNode current = head;
        while (current != null) {
            System.out.print(current.val + " -> ");
            current = current.next;
        }
        System.out.println("null");
    }
}

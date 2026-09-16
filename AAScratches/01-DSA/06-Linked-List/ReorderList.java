class ReorderList {

    public static void main(String[] args) {
        int[] values = {1, 2, 3, 4, 5};
        ListNode head = createLinkedList(values);

        System.out.print("Original list: ");
        printLinkedList(head);

        head = reorderList(head);

        System.out.print("Reordered list: ");
        printLinkedList(head);
    }

    private static ListNode reorderList(ListNode head) {
        if (head == null || head.next == null) return head;

        // Step 1: Find middle
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // Step 2: Reverse second half
        ListNode list2 = slow.next;
        slow.next = null;
        ListNode prev = null;
        while (list2 != null) {
            ListNode next = list2.next;
            list2.next = prev;
            prev = list2;
            list2 = next;
        }
        list2 = prev; // reversed second half

        // Step 3: Merge using your dummy + merged pointer safely
        ListNode dummy = new ListNode(-1);
        ListNode merged = dummy;

        while (head != null && list2 != null) {
            ListNode next1 = head.next;
            ListNode next2 = list2.next;

            // connect head, then disconnect its next
            merged.next = head;
            head.next = null; // disconnect old link
            merged = merged.next;

            // connect list2, then disconnect its next
            merged.next = list2;
            list2.next = null; // disconnect old link
            merged = merged.next;

            // move pointers
            head = next1;
            list2 = next2;
        }

        // Attach the remainder (if any)
        if (head != null) merged.next = head;
        if (list2 != null) merged.next = list2;

        return dummy.next;
    }

    // Helpers
    private static ListNode createLinkedList(int[] values) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        for (int value : values) {
            current.next = new ListNode(value);
            current = current.next;
        }
        return dummy.next;
    }

    private static void printLinkedList(ListNode head) {
        while (head != null) {
            System.out.print(head.val + " ");
            head = head.next;
        }
        System.out.println();
    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
    }
}

class ListNode {
    int val;
    ListNode next;
}
// https://leetcode.com/problems/remove-duplicates-from-sorted-list/description/
// 83. Remove Duplicates from Sorted List
class RemoveDuplicatesInList {

    public ListNode deleteDuplicates(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode current = head;
        while (current != null && current.next != null) {
            if (current.val == current.next.val) {
                //current remains same skipping current.next as it's
                // duplicate value as prior
                //hoping next.next pointer won't have duplicate value
                current.next = current.next.next;
            } else {
                current = current.next;
            }
        }
        return head;
    }

    public static void main(String[] args) {
        // Build input list: 1 -> 2 -> 3 -> 3 -> 4 -> 4 -> 5
        ListNode head = new ListNode();
        head.val = 1;
        head.next = new ListNode(); head.next.val = 2;
        head.next.next = new ListNode(); head.next.next.val = 3;
        head.next.next.next = new ListNode(); head.next.next.next.val = 3;
        head.next.next.next.next = new ListNode(); head.next.next.next.next.val = 4;
        head.next.next.next.next.next = new ListNode(); head.next.next.next.next.next.val = 4;
        head.next.next.next.next.next.next = new ListNode(); head.next.next.next.next.next.next.val = 5;

        // Print input list
        System.out.print("Input: ");
        for (ListNode cur = head; cur != null; cur = cur.next) {
            System.out.print(cur.val + (cur.next == null ? "" : " -> "));
        }
        System.out.println();

        // Call deleteDuplicates
        RemoveDuplicatesInList solver = new RemoveDuplicatesInList();
        ListNode result = solver.deleteDuplicates(head);

        // Print output list
        System.out.print("Output: ");
        for (ListNode cur = result; cur != null; cur = cur.next) {
            System.out.print(cur.val + (cur.next == null ? "" : " -> "));
        }
        System.out.println();
    }
}

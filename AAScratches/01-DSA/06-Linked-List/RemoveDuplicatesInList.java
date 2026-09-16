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
}

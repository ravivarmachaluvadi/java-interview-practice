class DeleteHeadOfDLL {
    public ListNode deleteHead(ListNode head) {
        if (head == null || head.next == null) return null;

        ListNode prev = head;
        head = head.next;
        head.prev = null;
        prev.next = null;
        return head;
    }
}

class ListNode {
    int val;
    ListNode next;
    ListNode prev;
}
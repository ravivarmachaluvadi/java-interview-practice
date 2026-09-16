class ListNodeReferencing {

    public static void main(String[] args) {
        ListNode a = new ListNode(10);
        ListNode b = new ListNode(11);
        ListNode c = new ListNode(12);

        ListNode temp = a;
        a = new ListNode(13);
        System.out.println(temp.val);// 10
        System.out.println(a.val); // 13
        temp.val = 9;
        System.out.println(temp.val);// 9
    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
    }
}

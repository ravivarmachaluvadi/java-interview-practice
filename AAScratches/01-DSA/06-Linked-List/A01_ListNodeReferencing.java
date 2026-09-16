/**
 * Demonstrates how Java handles object references and assignment.
 *
 * The program creates three ListNode objects, then reassigns the variable
 * 'a' to a new node while keeping a separate reference 'temp' pointing to
 * the original node. It prints values to show that:
 * - 'temp.val' reflects the original node's value (10),
 * - 'a.val' shows the new node's value (13),
 * - modifying 'temp.val' changes the original node, not the new one.
 *
 * Approach: Use simple object creation and reassignment to illustrate
 * reference semantics. No complex data structures or algorithms are used.
 *
 * Time Complexity: O(1) – constant time operations only.
 * Space Complexity: O(1) – a fixed number of ListNode objects allocated.
 */
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

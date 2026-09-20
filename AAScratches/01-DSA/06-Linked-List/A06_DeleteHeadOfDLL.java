/*
 * =====================================================================
 *  Delete Head of a Doubly Linked List            Building block | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a doubly linked list (each node has prev and next),
 *   remove the first node and return the new head. The list may be empty or
 *   have a single node, in which case the result is an empty list. The
 *   removed node must be fully detached so it holds no reference into the list.
 *
 * EXAMPLE
 *   1 <-> 2 <-> 3   ->  2 <-> 3   (and 2.prev == null, old 1.next == null)
 *   1               ->  (empty)
 *   (empty)         ->  (empty)
 *
 * APPROACH  (two-way detach)
 *   1. If head is null or has no next, return null (nothing left).
 *   2. Remember the old head, then move head to head.next.
 *   3. new head.prev = null   -> forward list no longer points back at old head.
 *   4. old head.next = null   -> old head no longer points into the list.
 *   5. Return the new head.
 *
 * KEY INSIGHT
 *   A doubly linked node has TWO links, so a correct removal is two writes,
 *   not one. Forgetting prev = null leaves a dangling back-pointer that a
 *   backward walk (or an LRU cache eviction) will follow into a removed node.
 *   Always detach both directions before returning.
 *
 * COMPLEXITY
 *   Time  O(1)  fixed number of pointer writes
 *   Space O(1)  no extra structures
 *
 * INTERVIEW FOLLOW-UPS
 *   - Delete the tail of a DLL (mirror image: tail = tail.prev; tail.next = null).
 *   - Delete an arbitrary node given only that node: node.prev.next = node.next
 *     and node.next.prev = node.prev, guarding the head and tail cases.
 *   - Why does an LRU cache use a DLL? O(1) remove-from-middle given the node.
 *   - Using a sentinel head and tail removes all the null checks above.
 *
 * RUN
 *   main() runs 3 cases (typical, single node, empty), prints the list walked
 *   forward and backward (to verify prev links) plus detach checks, each
 *   with its expected value.
 */
class DeleteHeadOfDLL {

    public ListNode deleteHead(ListNode head) {
        if (head == null || head.next == null) return null;

        ListNode oldHead = head;
        head = head.next;
        head.prev = null;     // forward list no longer references the removed node
        oldHead.next = null;  // removed node no longer references the list
        return head;
    }

    /** Builds a doubly linked list, e.g. build(1, 2, 3) -> 1 <-> 2 <-> 3. */
    static ListNode build(int... vals) {
        ListNode head = null;
        ListNode tail = null;
        for (int v : vals) {
            ListNode node = new ListNode(v);
            if (head == null) {
                head = node;
            } else {
                tail.next = node;
                node.prev = tail;
            }
            tail = node;
        }
        return head;
    }

    /** Walks next pointers: "1 <-> 2 <-> 3", or "(empty)". */
    static String forward(ListNode head) {
        if (head == null) return "(empty)";
        StringBuilder sb = new StringBuilder();
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (sb.length() > 0) sb.append(" <-> ");
            sb.append(cur.val);
        }
        return sb.toString();
    }

    /** Walks to the tail, then follows prev pointers back: verifies the prev links. */
    static String backward(ListNode head) {
        if (head == null) return "(empty)";
        ListNode tail = head;
        while (tail.next != null) tail = tail.next;
        StringBuilder sb = new StringBuilder();
        for (ListNode cur = tail; cur != null; cur = cur.prev) {
            if (sb.length() > 0) sb.append(" <-> ");
            sb.append(cur.val);
        }
        return sb.toString();
    }

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        DeleteHeadOfDLL solver = new DeleteHeadOfDLL();

        // case 1: typical 1 <-> 2 <-> 3
        ListNode oldHead = build(1, 2, 3);
        ListNode newHead = solver.deleteHead(oldHead);
        print("case 1 forward      ", forward(newHead), "2 <-> 3");
        print("case 1 backward     ", backward(newHead), "3 <-> 2");
        print("case 1 newHead.prev ", newHead.prev == null, true);
        print("case 1 oldHead.next ", oldHead.next == null, true);

        // case 2: single node -> empty
        print("case 2 single       ", forward(solver.deleteHead(build(1))), "(empty)");

        // case 3: empty -> empty
        print("case 3 empty        ", forward(solver.deleteHead(null)), "(empty)");
    }
}

class ListNode {
    int val;
    ListNode next;
    ListNode prev;

    ListNode(int val) {
        this.val = val;
    }
}

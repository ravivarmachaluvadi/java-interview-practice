/*
 * =====================================================================
 *  ListNode Referencing                          Building block | Easy
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   A Java variable of a class type is a HANDLE to an object, not the object
 *   itself. Assignment copies the handle. Two handles to the same node see
 *   each other's mutations; re-pointing one handle does not touch the other.
 *   Every linked-list problem in this folder (reverse, merge, delete, cycle)
 *   is nothing but re-pointing these handles, so this model must be automatic.
 *
 * WHAT YOU WILL SEE
 *   case 1: temp = a; a = new node(13)   -> temp.val still 10, a.val 13
 *   case 2: alias = b; alias.val = 99    -> b.val is 99 (same object)
 *   case 3: walker = head; walker.next = null  -> head's chain is cut too
 *   case 4: method reassigns its parameter -> caller's handle unchanged;
 *           method mutates param.val       -> caller sees the new value
 *
 * HOW IT WORKS
 *   1. `ListNode temp = a` copies the reference; both now point to node(10).
 *   2. `a = new ListNode(13)` rebinds only `a`. temp still holds node(10).
 *   3. `alias.val = 99` writes through the handle into the shared object.
 *   4. `walker.next = null` mutates the node that head also reaches, so the
 *      cut is visible from head even though head itself never changed.
 *   5. Java passes references BY VALUE: a callee can mutate the object, but
 *      reassigning its parameter is invisible to the caller.
 *
 * KEY INSIGHT
 *   "prev = curr" and "curr = curr.next" move handles. "curr.next = prev"
 *   changes the node. Keep those two operations apart in your head and every
 *   pointer diagram becomes readable. When in doubt, draw the boxes and arrows.
 *
 * GOTCHAS
 *   - Rebinding `head` inside a method does not change the caller's head;
 *     return the new head instead (see every method that returns ListNode).
 *   - Overwriting curr.next before saving it loses the rest of the list.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Is Java pass-by-reference? No: pass-by-value of the reference. Explain.
 *   - What does == compare on objects vs .equals()?
 *   - Why can a method delete a middle node but not (without returning) the head?
 *
 * RUN
 *   main() runs 4 cases and prints actual vs expected.
 */
class ListNodeReferencing {

    /** Reassigning the parameter only rebinds the local handle. */
    static void tryToReplace(ListNode node) {
        node = new ListNode(-1); // caller never sees this
    }

    /** Mutating through the parameter changes the shared object. */
    static void mutateValue(ListNode node) {
        node.val = 77;
    }

    /** Builds a chain from values, e.g. build(1, 2, 3) -> 1 -> 2 -> 3. */
    static ListNode build(int... vals) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        for (int v : vals) {
            tail.next = new ListNode(v);
            tail = tail.next;
        }
        return dummy.next;
    }

    static String listToString(ListNode head) {
        StringBuilder sb = new StringBuilder("[");
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (sb.length() > 1) sb.append(", ");
            sb.append(cur.val);
        }
        return sb.append("]").toString();
    }

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1: copying a handle, then rebinding the original
        ListNode a = new ListNode(10);
        ListNode temp = a;          // temp and a point to the same node(10)
        a = new ListNode(13);       // a now points to a different node
        print("case 1 temp.val", temp.val, 10);
        print("case 1 a.val   ", a.val, 13);

        // case 2: mutating through an alias
        ListNode b = new ListNode(11);
        ListNode alias = b;
        alias.val = 99;             // writes into the object b also points to
        print("case 2 b.val   ", b.val, 99);
        print("case 2 alias==b", alias == b, true);

        // case 3: a walker cutting the chain is visible from head
        ListNode head = build(1, 2, 3, 4);
        ListNode walker = head;
        walker = walker.next;       // moves the walker handle only
        walker.next = null;         // mutates node(2): the chain is cut here
        print("case 3 head    ", listToString(head), "[1, 2]");

        // case 4: pass-by-value of the reference
        ListNode c = new ListNode(12);
        tryToReplace(c);
        print("case 4 replace ", c.val, 12);
        mutateValue(c);
        print("case 4 mutate  ", c.val, 77);
    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
    }
}

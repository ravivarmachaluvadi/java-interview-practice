/*
 * =====================================================================
 *  Reverse a Doubly Linked List                     GfG classic | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given the head of a doubly linked list (each node has prev and next), reverse it in
 *   place and return the new head. Both directions must stay consistent afterwards:
 *   walking next from the new head and walking prev from the new tail give mirror images.
 *
 * EXAMPLE
 *   2 <-> 4 <-> 6 <-> 8 <-> 10  ->  10 <-> 8 <-> 6 <-> 4 <-> 2
 *   []                          ->  []          (empty list, head stays null)
 *   [7]                         ->  [7]         (single node, both links stay null)
 *
 * APPROACH  (swap prev/next per node, recursively)
 *   1. If node is null the list is empty: return null.
 *   2. Swap node.next and node.prev. After the swap, node.prev points to what used to
 *      be the next node, i.e. the rest of the original list.
 *   3. If node.prev is now null we just swapped the last original node: it becomes the
 *      new head, so return it.
 *   4. Otherwise recurse on node.prev (the original next) and return whatever it returns.
 *   reverseIterative() does the same walk with a loop and a "last visited" pointer.
 *
 * KEY INSIGHT
 *   On a doubly linked list, reversal is nothing more than swapping the two pointers in
 *   every node. There is no prev/curr/next juggling like the singly linked version,
 *   because each node already stores both neighbours. The only subtlety is that after
 *   the swap the "next node to visit" lives in node.prev, not node.next.
 *
 * COMPLEXITY
 *   Time  O(n)  every node is visited exactly once
 *   Space O(n)  recursion depth equals list length (iterative version is O(1))
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it iteratively to avoid stack overflow on long lists (reverseIterative below).
 *   - Reverse a singly linked list: needs the three-pointer prev/curr/next dance.
 *   - Reverse only a sub-range [left, right] of the DLL, keeping the outer links correct.
 *
 * RUN
 *   main() runs 3 cases (typical, empty, single) through both methods and prints
 *   the forward walk, the backward walk (via prev), and the expected result.
 */

// Node class for a Doubly Linked List
class Node {
    int data;
    Node next;
    Node prev;

    public Node(int data) {
        this.data = data;
    }
}

class DoublyLinkedListReverse {
    Node head;

    // Recursive version: swap the two pointers, then follow the *original* next (now prev).
    public Node reverse(Node node) {
        if (node == null) return null;

        // Swap the next and prev pointers
        Node originalNext = node.next;
        node.next = node.prev;
        node.prev = originalNext;

        // If there was no original next, this was the last node: it is the new head
        if (node.prev == null) {
            return node;
        }

        // Recur for the next node in the original list (which is now node.prev)
        return reverse(node.prev);
    }

    // Iterative version: same swap, but tracks the last node touched as the new head.
    public Node reverseIterative(Node node) {
        Node newHead = null;
        Node current = node;
        while (current != null) {
            Node originalNext = current.next;
            current.next = current.prev;
            current.prev = originalNext;
            newHead = current;          // last node we swap is the new head
            current = originalNext;     // keep walking the original direction
        }
        return newHead;
    }

    // Insert a node at the beginning (so push(10), push(8) ... builds 8 <-> 10)
    public void push(int newData) {
        Node newNode = new Node(newData);
        newNode.next = head;
        if (head != null) {
            head.prev = newNode;
        }
        head = newNode;
    }

    static DoublyLinkedListReverse fromArray(int[] values) {
        DoublyLinkedListReverse list = new DoublyLinkedListReverse();
        for (int i = values.length - 1; i >= 0; i--) {
            list.push(values[i]);
        }
        return list;
    }

    // Forward walk using next
    static String forward(Node node) {
        StringBuilder sb = new StringBuilder("[");
        for (Node cur = node; cur != null; cur = cur.next) {
            if (sb.length() > 1) sb.append(", ");
            sb.append(cur.data);
        }
        return sb.append("]").toString();
    }

    // Backward walk using prev, starting from the tail: proves the prev links are correct too
    static String backward(Node node) {
        Node tail = node;
        while (tail != null && tail.next != null) tail = tail.next;
        StringBuilder sb = new StringBuilder("[");
        for (Node cur = tail; cur != null; cur = cur.prev) {
            if (sb.length() > 1) sb.append(", ");
            sb.append(cur.data);
        }
        return sb.append("]").toString();
    }

    static void runCase(String label, int[] input,
                        String expectedForward, String expectedBackward) {
        DoublyLinkedListReverse recursive = fromArray(input);
        recursive.head = recursive.reverse(recursive.head);
        System.out.println(label + " recursive forward:  " + forward(recursive.head)
                + "   expected " + expectedForward);
        System.out.println(label + " recursive backward: " + backward(recursive.head)
                + "   expected " + expectedBackward);

        DoublyLinkedListReverse iterative = fromArray(input);
        iterative.head = iterative.reverseIterative(iterative.head);
        System.out.println(label + " iterative forward:  " + forward(iterative.head)
                + "   expected " + expectedForward);
        System.out.println(label + " iterative backward: " + backward(iterative.head)
                + "   expected " + expectedBackward);
    }

    public static void main(String[] args) {
        runCase("case 1 (typical):", new int[]{2, 4, 6, 8, 10},
                "[10, 8, 6, 4, 2]", "[2, 4, 6, 8, 10]");
        runCase("case 2 (empty):  ", new int[]{}, "[]", "[]");
        runCase("case 3 (single): ", new int[]{7}, "[7]", "[7]");
    }
}

/*
 * =====================================================================
 *  Copy List with Random Pointer                    LeetCode 138 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Each node has an int val, a next pointer, and a random pointer that may point to any
 *   node in the list or to null. Return a deep copy: brand-new nodes whose next and random
 *   pointers point only at new nodes, in the same shape as the original. The original list
 *   must be unchanged when you are done.
 *
 * EXAMPLE
 *   vals [7, 13, 11, 10, 1], random targets [null, 0, 4, 2, 0]  ->  same shape, new nodes
 *   vals [1], random target [0]   (single node whose random points to itself)
 *   vals [1, 2], random targets [1, 1]   (two nodes both pointing at the last node)
 *   empty list  ->  null
 *
 * APPROACH  (HashMap from original node to its copy)
 *   1. Pass 1: walk the original list and put(original, new Node(original.val)) for each.
 *      Only values are copied; pointers are left null because the target copies may not
 *      exist yet.
 *   2. Pass 2: walk again. For each original, set copy.next = map.get(original.next) and
 *      copy.random = map.get(original.random). map.get(null) returns null, which handles
 *      the end of the list and null random pointers with no special-casing.
 *   3. Return map.get(head).
 *
 * APPROACH 2  (interleave copies, then split: O(1) extra space)
 *   1. Insert each copy directly after its original: A -> A' -> B -> B' -> ...
 *   2. For each original, A'.random = A.random.next (the copy of A's random target).
 *   3. Unweave: restore original.next and stitch the copies together. Both are in this
 *      file as copyRandomListInterleaved and run from main.
 *
 * KEY INSIGHT
 *   The hard part is that random can point forwards to a node whose copy does not exist
 *   yet. A map solves this by creating ALL copies first, then wiring; the interleave
 *   trick solves it by making "the copy of X" reachable as X.next without any map. Both
 *   are two-pass; the second one trades the map for temporary pointer surgery.
 *
 * COMPLEXITY
 *   Time  O(n)  two (map) or three (interleave) linear passes
 *   Space O(n)  for the map; O(1) extra for the interleave version (output aside)
 *
 * INTERVIEW FOLLOW-UPS
 *   - O(1) extra space? Interleave-then-split (approach 2 in this file).
 *   - What about a graph with arbitrary pointers? Same map idea: Clone Graph (LC 133),
 *     BFS/DFS with a visited-to-copy map.
 *   - Why not recursion with a map? It works and is elegant, but recursion depth is n.
 *
 * RUN
 *   main() runs 4 cases through both methods and checks vals, random targets (as indices),
 *   and that no copied node is the same object as an original.
 */
import java.util.HashMap;
import java.util.Map;

class CopyRandomList {

    public static void main(String[] args) {
        int[][][] cases = {
                // {vals}, {random target index, -1 for null}, one case per row
                {{7, 13, 11, 10, 1}, {-1, 0, 4, 2, 0}},
                {{1}, {0}},
                {{1, 2}, {1, 1}},
                {{}, {}},
        };
        for (int i = 0; i < cases.length; i++) {
            Node original = build(cases[i][0], cases[i][1]);
            String expected = describe(original);

            String expectedCopy = expected + " distinct=true";

            Node copy1 = copyRandomList(original);
            print("case " + (i + 1) + " map        ", describeCopy(original, copy1), expectedCopy);

            Node copy2 = copyRandomListInterleaved(original);
            print("case " + (i + 1) + " interleave ", describeCopy(original, copy2), expectedCopy);

            // the interleaved version must leave the original untouched
            print("case " + (i + 1) + " orig intact", describe(original), expected);
        }
    }

    /** Approach 1: map each original node to its copy, then wire pointers in a second pass. */
    public static Node copyRandomList(Node head) {
        if (head == null) return null;

        Map<Node, Node> originalToCopy = new HashMap<>();

        // pass 1: create every copy so that any pointer target already has a copy
        for (Node curr = head; curr != null; curr = curr.next) {
            originalToCopy.put(curr, new Node(curr.val));
        }

        // pass 2: wire next and random; get(null) == null covers list end and null randoms
        for (Node curr = head; curr != null; curr = curr.next) {
            Node copy = originalToCopy.get(curr);
            copy.next = originalToCopy.get(curr.next);
            copy.random = originalToCopy.get(curr.random);
        }
        return originalToCopy.get(head);
    }

    /** Approach 2: interleave copies into the original, wire randoms, then split. O(1) extra. */
    public static Node copyRandomListInterleaved(Node head) {
        if (head == null) return null;

        // pass 1: A -> A' -> B -> B' -> ...  (each copy sits right after its original)
        for (Node curr = head; curr != null; curr = curr.next.next) {
            Node copy = new Node(curr.val);
            copy.next = curr.next;
            curr.next = copy;
        }

        // pass 2: the copy of X is always X.next, so copy.random = original.random.next
        for (Node curr = head; curr != null; curr = curr.next.next) {
            if (curr.random != null) curr.next.random = curr.random.next;
        }

        // pass 3: unweave into two separate lists, restoring the original as we go
        Node copyHead = head.next;
        for (Node curr = head; curr != null; ) {
            Node copy = curr.next;
            curr.next = copy.next;                       // restore original link
            copy.next = (curr.next != null) ? curr.next.next : null;
            curr = curr.next;
        }
        return copyHead;
    }

    // ---- helpers for main ------------------------------------------------

    /** Builds a list from values and random targets given as indices (-1 = null). */
    private static Node build(int[] vals, int[] randomIdx) {
        Node[] nodes = new Node[vals.length];
        for (int i = 0; i < vals.length; i++) nodes[i] = new Node(vals[i]);
        for (int i = 0; i < vals.length; i++) {
            nodes[i].next = (i + 1 < vals.length) ? nodes[i + 1] : null;
            nodes[i].random = (randomIdx[i] >= 0) ? nodes[randomIdx[i]] : null;
        }
        return vals.length == 0 ? null : nodes[0];
    }

    /** Renders "val(randomIndex)" per node, e.g. "7(-) 13(0) 11(4)"; "-" means random is null. */
    private static String describe(Node head) {
        Map<Node, Integer> index = new HashMap<>();
        int i = 0;
        for (Node n = head; n != null; n = n.next) index.put(n, i++);

        StringBuilder sb = new StringBuilder("[");
        for (Node n = head; n != null; n = n.next) {
            if (sb.length() > 1) sb.append(' ');
            sb.append(n.val).append('(');
            // "?" would mean random points outside this list, which is a bug in the copy
            if (n.random == null) sb.append('-');
            else if (index.containsKey(n.random)) sb.append(index.get(n.random));
            else sb.append('?');
            sb.append(')');
        }
        return sb.append(']').toString();
    }

    /** describe() plus a check that the copy shares no node objects with the original. */
    private static String describeCopy(Node original, Node copy) {
        return describe(copy) + " distinct=" + noSharedNodes(original, copy);
    }

    /** True when no node object of the copy is also a node of the original. */
    private static boolean noSharedNodes(Node original, Node copy) {
        Map<Node, Boolean> originals = new HashMap<>();
        for (Node n = original; n != null; n = n.next) originals.put(n, true);
        for (Node n = copy; n != null; n = n.next) {
            if (originals.containsKey(n)) return false;
        }
        return true;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

class Node {
    int val;
    Node next;
    Node random;

    public Node(int val) {
        this.val = val;
    }
}

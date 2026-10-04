/*
 * =====================================================================
 *  P035 Deep Copy with an Old -> New Map   Canonical LC 138 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 138, Copy List with Random Pointer)
 *   Each node has next and random (random may point anywhere in the list, or null).
 *   Return a deep copy: brand-new nodes whose next and random mirror the original's.
 *
 * EXAMPLE
 *   [[7,null],[13,0],[11,4],[10,2],[1,0]]  ->  the same shape, all nodes new
 *   (each pair is [value, index that random points to])
 *
 * RECOGNIZE WHEN
 *   - "deep copy", "clone", "duplicate the structure" where pointers can go anywhere
 *     (random pointers, graph edges, cycles).
 *   - You meet a node through one pointer before you have created its copy.
 *   Not this if: the structure is a plain list or tree with no extra pointers -> a simple
 *   recursive copy (no map) is enough.
 *
 * TEMPLATE
 *   copyOf = {}                                       // original node -> its clone
 *   clone(node):
 *       if node is null: return null
 *       if node in copyOf: return copyOf[node]        // already made (also stops cycles)
 *       c = new Node(node.val); copyOf[node] = c      // register BEFORE recursing
 *       wire c's pointers with clone(...) of the original's pointers
 *       return c
 *
 * APPROACH
 *   1. First pass: create a copy of every node, map original -> copy.
 *   2. Second pass: copy.next = map[orig.next], copy.random = map[orig.random].
 *
 * KEY INSIGHT
 *   A pointer can name a node whose copy does not exist yet. The map answers "what is the
 *   copy of X?" whenever it is needed, and storing the copy BEFORE following any pointer
 *   is what makes cycles safe. The O(1)-space trick stores the same mapping inside the
 *   list itself: put each copy right after its original.
 *
 * COMPLEXITY
 *   Time O(n), space O(n) for the map (O(1) extra with interleaving). Graph: O(V + E).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 138 interleaved               A -> A' -> B -> B'; A'.random = A.random.next;
 *                                            then unzip the two lists
 *   [coded] LC 133  Clone Graph              DFS with the map; neighbours instead of next
 *           LC 1485 Clone Tree w/ Random     the same map over a binary tree
 *           LC 1490 Clone N-ary Tree         no sharing, so no map needed
 *
 * PITFALLS
 *   - Registering the copy AFTER recursing loops forever on a cycle.
 *   - Interleaving: restore the original list's next pointers while unzipping.
 *   - Copy random == null as null, not as "copy of null".
 *
 * DEEP DIVE
 *   C12_CopyRandomList (06-Linked-List)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

class CloneWithMap {

    static class Node {
        int val;
        Node next;
        Node random;

        Node(int val) {
            this.val = val;
        }

        // pairs[i] = {value, randomIndex or -1}
        static Node build(int[][] pairs) {
            List<Node> nodes = new ArrayList<>();
            for (int[] p : pairs) {
                nodes.add(new Node(p[0]));
            }
            for (int i = 0; i < nodes.size(); i++) {
                nodes.get(i).next = i + 1 < nodes.size() ? nodes.get(i + 1) : null;
                nodes.get(i).random = pairs[i][1] < 0 ? null : nodes.get(pairs[i][1]);
            }
            return nodes.isEmpty() ? null : nodes.get(0);
        }
    }

    static class GraphNode {
        int val;
        List<GraphNode> neighbors = new ArrayList<>();

        GraphNode(int val) {
            this.val = val;
        }
    }

    // Prints [[val, randomIndex], ...] so a copy can be compared with the original.
    static String show(Node head) {
        Map<Node, Integer> index = new IdentityHashMap<>();
        int i = 0;
        for (Node n = head; n != null; n = n.next) {
            index.put(n, i++);
        }
        StringBuilder sb = new StringBuilder("[");
        for (Node n = head; n != null; n = n.next) {
            sb.append("[").append(n.val).append(",")
                    .append(n.random == null ? "null" : index.get(n.random)).append("]");
            if (n.next != null) {
                sb.append(",");
            }
        }
        return sb.append("]").toString();
    }

    // True when no node of the copy is a node of the original.
    static boolean shareNothing(Node a, Node b) {
        Map<Node, Boolean> seen = new IdentityHashMap<>();
        for (Node n = a; n != null; n = n.next) {
            seen.put(n, true);
        }
        for (Node n = b; n != null; n = n.next) {
            if (seen.containsKey(n)) {
                return false;
            }
        }
        return true;
    }

    // Canonical LC 138 with a map.
    static Node copyRandomList(Node head) {
        Map<Node, Node> copyOf = new HashMap<>();
        for (Node n = head; n != null; n = n.next) {
            copyOf.put(n, new Node(n.val));
        }
        for (Node n = head; n != null; n = n.next) {
            copyOf.get(n).next = copyOf.get(n.next);
            copyOf.get(n).random = copyOf.get(n.random);
        }
        return copyOf.get(head);
    }

    // LC 138 in O(1) extra space: interleave, set randoms, unzip.
    static Node copyRandomListInterleaved(Node head) {
        for (Node n = head; n != null; n = n.next.next) {
            Node c = new Node(n.val);
            c.next = n.next;
            n.next = c;
        }
        for (Node n = head; n != null; n = n.next.next) {
            n.next.random = n.random == null ? null : n.random.next;
        }
        Node dummy = new Node(0);
        Node tail = dummy;
        for (Node n = head; n != null; n = n.next) {
            Node c = n.next;
            n.next = c.next;                       // restore the original
            tail.next = c;
            tail = c;
        }
        return dummy.next;
    }

    // LC 133: register the clone before visiting neighbours (graphs have cycles).
    static GraphNode cloneGraph(GraphNode node) {
        return clone(node, new HashMap<>());
    }

    private static GraphNode clone(GraphNode node, Map<GraphNode, GraphNode> copyOf) {
        if (node == null) {
            return null;
        }
        GraphNode done = copyOf.get(node);
        if (done != null) {
            return done;
        }
        GraphNode c = new GraphNode(node.val);
        copyOf.put(node, c);
        for (GraphNode nb : node.neighbors) {
            c.neighbors.add(clone(nb, copyOf));
        }
        return c;
    }

    // Adjacency list by value (1-based, as LeetCode prints it); also checks node identity.
    static String showGraph(GraphNode start, GraphNode original) {
        Map<Integer, GraphNode> byVal = new HashMap<>();
        collect(start, byVal);
        Map<Integer, GraphNode> origByVal = new HashMap<>();
        collect(original, origByVal);
        StringBuilder sb = new StringBuilder("[");
        for (int v = 1; v <= byVal.size(); v++) {
            List<Integer> nbs = new ArrayList<>();
            for (GraphNode nb : byVal.get(v).neighbors) {
                nbs.add(nb.val);
            }
            sb.append(nbs.toString().replace(" ", "")).append(v < byVal.size() ? "," : "");
            if (byVal.get(v) == origByVal.get(v)) {
                return "shared node " + v;
            }
        }
        return sb.append("]").toString();
    }

    private static void collect(GraphNode n, Map<Integer, GraphNode> byVal) {
        if (n == null || byVal.containsKey(n.val)) {
            return;
        }
        byVal.put(n.val, n);
        for (GraphNode nb : n.neighbors) {
            collect(nb, byVal);
        }
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] shape = {{7, -1}, {13, 0}, {11, 4}, {10, 2}, {1, 0}};
        Node original = Node.build(shape);
        Node copy = copyRandomList(original);
        check("LC 138 map, same shape", show(copy), "[[7,null],[13,0],[11,4],[10,2],[1,0]]");
        check("LC 138 map, all nodes new", shareNothing(original, copy), true);

        Node original2 = Node.build(shape);
        Node copy2 = copyRandomListInterleaved(original2);
        check("LC 138 interleaved, same shape",
                show(copy2), "[[7,null],[13,0],[11,4],[10,2],[1,0]]");
        check("LC 138 interleaved, original restored", show(original2),
                "[[7,null],[13,0],[11,4],[10,2],[1,0]]");
        check("LC 138 interleaved, all nodes new", shareNothing(original2, copy2), true);
        check("LC 138 empty list", copyRandomList(null), null);

        GraphNode[] g = new GraphNode[5];
        for (int i = 1; i <= 4; i++) {
            g[i] = new GraphNode(i);
        }
        int[][] adj = {{}, {2, 4}, {1, 3}, {2, 4}, {1, 3}};
        for (int i = 1; i <= 4; i++) {
            for (int nb : adj[i]) {
                g[i].neighbors.add(g[nb]);
            }
        }
        check("LC 133 square graph",
                showGraph(cloneGraph(g[1]), g[1]), "[[2,4],[1,3],[2,4],[1,3]]");
        GraphNode lone = new GraphNode(1);
        check("LC 133 single node", showGraph(cloneGraph(lone), lone), "[[]]");
    }
}

/*
 * =====================================================================
 *  Lowest Common Ancestor of a Binary Tree III   LeetCode 1650 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   You are handed two nodes p and q of a binary tree. Every node carries a parent
 *   pointer, and you are NOT given the root. Return their lowest common ancestor: the
 *   deepest node that is an ancestor of both, where a node is an ancestor of itself.
 *   Both nodes are guaranteed to be in the same tree, so an answer always exists.
 *
 *   Fixed: node 0 was created in main() but never attached to the tree (dead object), and
 *   two of the three methods were the same algorithm written twice. They are now one
 *   method, and a genuinely different third approach (depth alignment) was added.
 *
 * EXAMPLE
 *            3
 *          /   \
 *         5     1        lca(5, 1)  ->  3   they meet only at the root
 *        / \   / \       lca(5, 4)  ->  5   5 is an ancestor of itself
 *       6   2 0   8      lca(6, 7)  ->  5   different branches under 5
 *          / \           lca(6, 6)  ->  6   p == q, answered before any walking
 *         7   4          lca(3, 4)  ->  3   one node is the root
 *
 * DESIGN  (three ways to intersect two upward chains)
 *   Without a root, "climb to the root" is the only move available, so each node's
 *   ancestor chain is really a linked list ending at the root. The question collapses to
 *   "find where two linked lists intersect".
 *
 *   lcaTwoRunner     - two pointers, one from p and one from q. When a pointer falls off
 *                      the top (null), restart it at the OTHER node. Both then walk
 *                      exactly depth(p) + depth(q) steps, so they arrive together.
 *   lcaAncestorSet   - walk up from p storing every node in a HashSet, then walk up from q
 *                      and return the first node already in the set.
 *   lcaByDepth       - measure both depths, advance the deeper pointer by the difference
 *                      so both sit on the same level, then step up in lockstep.
 *
 * KEY DECISIONS
 *   - Compare by identity (==), never by value. Values can repeat; node objects cannot.
 *   - lcaTwoRunner is the answer to give: O(1) extra space and six lines. The switch to
 *     the other node is what cancels the depth difference, and it is also why it
 *     terminates even when p and q are at wildly different depths.
 *   - lcaAncestorSet is the one to mention when the interviewer says "the tree is huge and
 *     nodes are not comparable" - it is the easiest to get right under pressure.
 *   - lcaByDepth is the version that generalises: it is the building block of binary
 *     lifting, which answers many LCA queries on a fixed tree in O(log n) each.
 *
 * COMPLEXITY
 *   lcaTwoRunner    Time O(h1 + h2)  Space O(1)   h = distance from a node to the root
 *   lcaAncestorSet  Time O(h1 + h2)  Space O(h1)  the set holds p's whole ancestor chain
 *   lcaByDepth      Time O(h1 + h2)  Space O(1)   two passes: measure, then climb
 *
 * INTERVIEW FOLLOW-UPS
 *   - LCA I (LC 236): no parent pointers, you get the root - postorder null-propagation
 *   - Why does the two-runner switch work? Both paths are p-then-q and q-then-p, equal length
 *   - What if p and q can be in different trees? Both runners hit null together; detect it
 *   - Many queries on one tree: binary lifting, or Tarjan's offline LCA with union-find
 *
 * RUN
 *   main() runs 5 cases through all three methods and prints actual vs expected.
 */

import java.util.HashSet;
import java.util.Set;

// Definition for a Node, with a parent pointer.
class Node {
    public int val;
    public Node left;
    public Node right;
    public Node parent;

    public Node(int val) {
        this.val = val;
    }
}

class LowestCommonAncestorOfaBinaryTreeIII {

    /**
     * Two runners, O(1) space. Each pointer walks its own chain, then the other one.
     * Both cover exactly depth(p) + depth(q) steps, so they land on the meeting node
     * at the same moment.
     */
    public Node lcaTwoRunner(Node p, Node q) {
        Node a = p, b = q;
        while (a != b) {
            // Falling off the top restarts the runner on the opposite chain,
            // which is what cancels the difference in depth.
            a = (a == null) ? q : a.parent;
            b = (b == null) ? p : b.parent;
        }
        return a;   // also correct when p == q, or when both walks end at null
    }

    /** Remember p's whole ancestor chain, then climb from q until a node is already known. */
    public Node lcaAncestorSet(Node p, Node q) {
        Set<Node> ancestorsOfP = new HashSet<>();
        for (Node cur = p; cur != null; cur = cur.parent) {
            ancestorsOfP.add(cur);
        }
        for (Node cur = q; cur != null; cur = cur.parent) {
            if (ancestorsOfP.contains(cur)) return cur;
        }
        return null;   // only reachable if p and q live in different trees
    }

    /** Level the two nodes by depth first, then step upward together. */
    public Node lcaByDepth(Node p, Node q) {
        int depthP = depth(p);
        int depthQ = depth(q);

        // Pull the deeper node up until both are on the same level.
        while (depthP > depthQ) {
            p = p.parent;
            depthP--;
        }
        while (depthQ > depthP) {
            q = q.parent;
            depthQ--;
        }

        // Same level now, so they must meet at the same time.
        while (p != q) {
            p = p.parent;
            q = q.parent;
        }
        return p;
    }

    private int depth(Node node) {
        int d = 0;
        for (Node cur = node; cur != null; cur = cur.parent) d++;
        return d;
    }

    public static void main(String[] args) {
        //            3
        //          /   \
        //         5     1
        //        / \   / \
        //       6   2 0   8
        //          / \
        //         7   4
        Node root = new Node(3);
        Node n5 = new Node(5);
        Node n1 = new Node(1);
        Node n6 = new Node(6);
        Node n2 = new Node(2);
        Node n0 = new Node(0);
        Node n8 = new Node(8);
        Node n7 = new Node(7);
        Node n4 = new Node(4);

        link(root, n5, n1);
        link(n5, n6, n2);
        link(n1, n0, n8);
        link(n2, n7, n4);

        LowestCommonAncestorOfaBinaryTreeIII solver = new LowestCommonAncestorOfaBinaryTreeIII();

        check(solver, "case 1 meet at root ", n5, n1, 3);
        check(solver, "case 2 own ancestor ", n5, n4, 5);
        check(solver, "case 3 deep split   ", n6, n7, 5);
        check(solver, "case 4 p == q       ", n6, n6, 6);
        check(solver, "case 5 p is the root", root, n4, 3);
    }

    /** Attaches children and sets their parent pointers in one step. */
    private static void link(Node parent, Node left, Node right) {
        parent.left = left;
        parent.right = right;
        if (left != null) left.parent = parent;
        if (right != null) right.parent = parent;
    }

    /** Runs all three methods on the same pair so their answers can be compared. */
    private static void check(LowestCommonAncestorOfaBinaryTreeIII solver,
                              String label, Node p, Node q, int expected) {
        System.out.println(label + ": twoRunner=" + valueOf(solver.lcaTwoRunner(p, q))
                + " set=" + valueOf(solver.lcaAncestorSet(p, q))
                + " byDepth=" + valueOf(solver.lcaByDepth(p, q))
                + "   expected " + expected + " from all three");
    }

    private static String valueOf(Node node) {
        return (node == null) ? "null" : String.valueOf(node.val);
    }
}

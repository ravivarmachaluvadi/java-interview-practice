/*
 * =====================================================================
 *  Binary Search Tree: insert, search, in-order      Building block | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Build an unbalanced BST over ints supporting put(value), contains(value)
 *   and an in-order walk. The BST invariant is: for every node, every value in
 *   its left subtree is strictly smaller and every value in its right subtree
 *   is strictly larger. Duplicates are ignored (put of an existing value is a
 *   no-op), so the tree holds a set, not a multiset.
 *
 * DESIGN  (classes and why)
 *   Node                  - value plus left/right child pointers. Nothing else;
 *                           no parent pointer, no size counter, no colour.
 *   BinarySearchTree      - owns only the root. Every public method is a thin
 *                           wrapper over a private static-shaped recursion, so
 *                           the recursive helper can take the "current" node.
 *   BinarySearchTreeOperations - the driver with main(); holds no state.
 *
 * KEY DECISIONS
 *   1. putRecursive RETURNS the subtree root and the caller reassigns
 *      current.left / current.right. This "return the new subtree" style makes
 *      the empty-tree case (root == null) fall out of the same code path
 *      instead of needing a special first-insert branch.
 *   2. Equal values hit neither the < nor the > branch, so they fall through
 *      and return the existing node unchanged - that is how duplicates are
 *      dropped without an explicit check.
 *   3. contains does ONE comparison per level and then discards the entire
 *      other subtree. That single fact is the whole reason a BST exists.
 *   4. In-order (left, node, right) emits values in sorted ascending order.
 *      This is not a coincidence; it is the BST invariant read out loud, and
 *      it is the basis of kth-smallest, validate-BST and BST-to-sorted-list.
 *
 * COMPLEXITY
 *   Time  put / contains  O(h). h = log n if inserts arrive in a balanced
 *         order, but h = n if they arrive already sorted (the tree degenerates
 *         into a linked list). No rebalancing here - that is what AVL and
 *         red-black trees add.
 *   Time  in-order  O(n), every node visited once.
 *   Space O(h) recursion stack for all three operations.
 *
 * INTERVIEW FOLLOW-UPS
 *   - "What if I insert 1..n in order?" -> a chain, O(n) lookups; name AVL /
 *     red-black / treap as the fix, and Java's TreeMap as the library answer.
 *   - Delete is the hard sibling: three cases (leaf, one child, two children
 *     -> replace with the in-order successor). See C05_DeleteNodeInBST.
 *   - Rewrite put/contains iteratively to drop the O(h) stack.
 *   - Validate that an arbitrary tree is a BST: min/max bounds, NOT a local
 *     parent-vs-children check. See C01_ValidateBST.
 *
 * RUN
 *   main() runs 3 cases (balanced-ish inserts, duplicate insert, degenerate
 *   sorted inserts) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class BinarySearchTree {
    Node root;

    public void put(int value) {
        root = putRecursive(root, value);
    }

    /** Returns the (possibly new) root of this subtree, so the caller just reassigns. */
    private Node putRecursive(Node current, int value) {
        if (current == null) {
            return new Node(value);
        }
        if (value < current.value) {
            current.left = putRecursive(current.left, value);
        } else if (value > current.value) {
            current.right = putRecursive(current.right, value);
        }
        // value == current.value falls through: duplicates are ignored.
        return current;
    }

    public boolean contains(int value) {
        return containsRecursive(root, value);
    }

    private boolean containsRecursive(Node current, int value) {
        if (current == null) {
            return false;
        }
        if (value == current.value) {
            return true;
        }
        // One comparison throws away an entire subtree - the point of a BST.
        return value < current.value
                ? containsRecursive(current.left, value)
                : containsRecursive(current.right, value);
    }

    /** In-order walk collected into a list; for a BST this comes out sorted. */
    public List<Integer> inOrderValues() {
        List<Integer> out = new ArrayList<>();
        inOrderRecursive(root, out);
        return out;
    }

    private void inOrderRecursive(Node node, List<Integer> out) {
        if (node == null) return;
        inOrderRecursive(node.left, out);
        out.add(node.value);
        inOrderRecursive(node.right, out);
    }

    /** Height, used below to show how sorted input degenerates the tree. */
    public int height() {
        return height(root);
    }

    private int height(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }
}

class BinarySearchTreeOperations {

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree();
        for (int v : new int[]{50, 30, 70, 20, 40, 60, 80}) {
            bst.put(v);
        }

        print("case 1 in-order      ", bst.inOrderValues(), "[20, 30, 40, 50, 60, 70, 80]");
        print("case 1 contains 40   ", bst.contains(40), "true");
        print("case 1 contains 90   ", bst.contains(90), "false");
        print("case 1 height        ", bst.height(), "3");

        // Edge case: inserting a duplicate must not change the tree.
        bst.put(40);
        print("case 2 after dup 40  ", bst.inOrderValues(), "[20, 30, 40, 50, 60, 70, 80]");

        // Tricky: sorted inserts degenerate the BST into a right-leaning chain,
        // so height == n and lookups become O(n).
        BinarySearchTree degenerate = new BinarySearchTree();
        for (int v : new int[]{1, 2, 3, 4, 5}) {
            degenerate.put(v);
        }
        print("case 3 sorted inserts", degenerate.inOrderValues(), "[1, 2, 3, 4, 5]");
        print("case 3 height (chain)", degenerate.height(), "5");
    }
}

class Node {
    int value;
    Node left, right;

    public Node(int value) {
        this.value = value;
    }
}

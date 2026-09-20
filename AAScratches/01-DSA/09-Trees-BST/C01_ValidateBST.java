/*
 * =====================================================================
 *  Validate Binary Search Tree        LeetCode 98 | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, decide whether it is a valid BST.
 *   Valid means: every value in a node's left subtree is strictly smaller
 *   than the node, every value in its right subtree is strictly larger, and
 *   both subtrees are themselves valid BSTs. Duplicates are not allowed.
 *
 * EXAMPLE
 *   [7,5,10,3,6,9,15]   ->  true
 *   [5,1,4,null,null,3,6]
 *                       ->  false  (3 sits in 5's right subtree but 3 < 5)
 *   [2147483647]        ->  true   (single node at Integer.MAX_VALUE)
 *   [2,2,null]          ->  false  (duplicate value, equality is not allowed)
 *
 * APPROACH A  (bounds propagation - the one to say out loud)
 *   1. Every node carries an open interval (min, max) it must fall inside.
 *   2. The root starts with (-infinity, +infinity).
 *   3. Going left tightens the upper bound to the node's value; going right
 *      tightens the lower bound to the node's value.
 *   4. A node outside its interval fails immediately; null is vacuously valid.
 *   5. Bounds are longs so a node at Integer.MIN_VALUE or MAX_VALUE still has
 *      a strictly wider sentinel around it.
 *
 * APPROACH B  (in-order must be strictly increasing)
 *   Walk in-order and keep the previously emitted value. A valid BST emits
 *   ascending values, so the first time previous >= current the tree is invalid.
 *
 * KEY INSIGHT
 *   The near-universal wrong answer is to compare each node only with its two
 *   children. That is a local check; the BST property is global - a node must
 *   beat every ancestor it descended past, not just its parent. Passing the
 *   (min, max) window down is how a local recursion enforces a global rule,
 *   and the exact same window is reused to *build* a BST from preorder (LC 1008).
 *
 * COMPLEXITY
 *   Time  O(n)  each node is visited once in both approaches
 *   Space O(h)  recursion stack, h = height (O(n) for a skewed tree)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why long bounds? Try Integer.MIN_VALUE as the root value with int bounds.
 *   - Allow duplicates on one side: which comparison relaxes from < to <=?
 *   - Do it iteratively with an explicit stack (in-order is the easiest to convert).
 *   - Largest BST subtree instead (LC 333): return min, max and size together.
 *
 * RUN
 *   main() runs 4 cases (valid, grandchild violation, MAX_VALUE single node,
 *   duplicate) through both approaches and prints actual vs expected.
 */
class Solution {

    // ---------------- Approach A: bounds propagation ----------------

    public boolean isValidBST(Node root) {
        return checkBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /** True when the subtree at `node` fits strictly inside (minVal, maxVal). */
    private boolean checkBST(Node node, long minVal, long maxVal) {
        if (node == null) {
            return true; // an empty subtree never breaks the ordering
        }

        // Strict comparisons on both sides, so equal values are rejected.
        if (node.data <= minVal || node.data >= maxVal) {
            return false;
        }

        // Going left caps the ceiling at this node; going right lifts the floor.
        return checkBST(node.left, minVal, node.data)
                && checkBST(node.right, node.data, maxVal);
    }

    // ---------------- Approach B: in-order must ascend ----------------

    private Integer previous; // last value emitted by the in-order walk

    public boolean isValidBstInorder(Node root) {
        previous = null;
        return inorderAscends(root);
    }

    private boolean inorderAscends(Node node) {
        if (node == null) {
            return true;
        }
        if (!inorderAscends(node.left)) {
            return false; // failure found on the left, stop early
        }
        if (previous != null && previous >= node.data) {
            return false; // not strictly increasing
        }
        previous = node.data;
        return inorderAscends(node.right);
    }
}

class ValidateBST {

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    private static void check(Solution solver, String label, Node root, boolean expected) {
        print(label + " bounds  ", solver.isValidBST(root), expected);
        print(label + " in-order", solver.isValidBstInorder(root), expected);
    }

    public static void main(String[] args) {
        Solution solver = new Solution();

        // Case 1 - a genuinely valid BST
        //        7
        //       / \
        //      5   10
        //     / \  / \
        //    3  6 9  15
        Node valid = new Node(7);
        valid.left = new Node(5);
        valid.left.left = new Node(3);
        valid.left.right = new Node(6);
        valid.right = new Node(10);
        valid.right.left = new Node(9);
        valid.right.right = new Node(15);
        check(solver, "case 1 valid tree   ", valid, true);

        // Case 2 - tricky: every parent/child pair is fine, but 3 is a
        // grandchild on the right of 5 while being smaller than 5.
        //        5
        //       / \
        //      1   4
        //         / \
        //        3   6
        Node grandchildViolation = new Node(5);
        grandchildViolation.left = new Node(1);
        grandchildViolation.right = new Node(4);
        grandchildViolation.right.left = new Node(3);
        grandchildViolation.right.right = new Node(6);
        check(solver, "case 2 bad grandchild", grandchildViolation, false);

        // Case 3 - edge: a lone node at Integer.MAX_VALUE. This is why the
        // bounds are longs; int sentinels would report false here.
        check(solver, "case 3 MAX_VALUE node", new Node(Integer.MAX_VALUE), true);

        // Case 4 - edge: duplicates are not allowed in a strict BST
        Node duplicate = new Node(2);
        duplicate.left = new Node(2);
        check(solver, "case 4 duplicate 2s  ", duplicate, false);
    }
}

class Node {
    int data;
    Node left, right;

    Node(int data) {
        this.data = data;
        left = null;
        right = null;
    }
}

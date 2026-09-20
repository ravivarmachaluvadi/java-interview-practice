/*
 * =====================================================================
 *  Flatten Binary Tree to Linked List            LeetCode 114 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Rewire a binary tree in place into a "linked list": every node's left pointer must
 *   end up null, and following right pointers from the root must visit the nodes in the
 *   tree's original preorder. Nothing may be allocated - the same node objects are reused.
 *
 * EXAMPLE
 *         1                 1
 *        / \                 \
 *       2   5      ->         2 -> 3 -> 4 -> 5 -> 6   (all left pointers null)
 *      / \   \
 *     3   4   6
 *   single node 1  ->  1 left-only chain 1 -> 2 -> 3 (as left children)  ->  1 -> 2 -> 3 (as right
 *   children)
 *
 * APPROACH  (postorder rewiring; the recursion returns the TAIL of what it flattened)
 *   1. Flatten the left subtree and the right subtree first, each returning its last node.
 *   2. If a left subtree existed, splice it in: leftTail.right = root.right, then
 *      root.right = root.left, then root.left = null. Order matters - save the old right
 *      link into leftTail BEFORE overwriting root.right.
 *   3. Return this subtree's tail: the right tail if there was one, else the left tail,
 *      else the node itself (a leaf is its own tail).
 *   A second method, flattenIterative, does the same with O(1) extra space.
 *
 * KEY INSIGHT
 *   The caller needs something the natural return value does not give it: the LAST node
 *   of the flattened subtree, so it can splice the next chunk on. Returning a tail (or a
 *   head/tail pair) is the standard move for every in-place pointer-surgery problem.
 *   The iterative version is the same idea without a stack: for each node with a left
 *   child, find that child's rightmost descendant - the preorder predecessor of the old
 *   right subtree - and hang the old right subtree there.
 *
 * COMPLEXITY
 *   Recursive:  Time O(n), Space O(h) for the call stack (O(n) when skewed).
 *   Iterative:  Time O(n) amortised (each edge is walked a constant number of times),
 *               Space O(1) - no stack, no recursion.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it in O(1) space (the flattenIterative method here) and justify the time bound.
 *   - Flatten to a doubly linked list in INORDER instead (BST-to-DLL).
 *   - Flatten using reverse preorder (right, left, root) and one "previous" pointer.
 *   - Can you undo it - rebuild a balanced tree from the flattened list?
 *
 * RUN
 *   main() flattens four trees with the recursive method (typical, single node, left
 *   chain, null) and the same typical tree with the iterative method, printing the right
 *   chain and whether all left pointers are null, each against its expected value.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }
}

class BinaryTreeToLinkedList {

    public void flatten(TreeNode root) {
        flattenAndGetTail(root);
    }

    /** Flattens the subtree and returns its last node, so the caller can splice on. */
    private TreeNode flattenAndGetTail(TreeNode root) {
        if (root == null) {
            return null;
        }

        TreeNode leftTail = flattenAndGetTail(root.left);
        TreeNode rightTail = flattenAndGetTail(root.right);

        if (leftTail != null) {
            leftTail.right = root.right; // park the old right subtree behind the left one
            root.right = root.left;
            root.left = null;
        }

        if (rightTail != null) {
            return rightTail;
        }
        if (leftTail != null) {
            return leftTail;
        }
        return root; // a leaf is its own tail
    }

    /** Same result, O(1) extra space: no recursion and no explicit stack. */
    public void flattenIterative(TreeNode root) {
        TreeNode current = root;
        while (current != null) {
            if (current.left != null) {
                // The rightmost node of the left subtree is the preorder predecessor
                // of the current right subtree, so the old right subtree hangs there.
                TreeNode predecessor = current.left;
                while (predecessor.right != null) {
                    predecessor = predecessor.right;
                }
                predecessor.right = current.right;
                current.right = current.left;
                current.left = null;
            }
            current = current.right;
        }
    }

    // ---------- test helpers ----------

    /**
     *        1
     *       / \
     *      2   5
     *     / \   \
     *    3   4   6
     */
    private static TreeNode sampleTree() {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(5);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(4);
        root.right.right = new TreeNode(6);
        return root;
    }

    private static List<Integer> rightChain(TreeNode node) {
        List<Integer> out = new ArrayList<>();
        while (node != null) {
            out.add(node.val);
            node = node.right;
        }
        return out;
    }

    private static boolean allLeftPointersNull(TreeNode node) {
        while (node != null) {
            if (node.left != null) {
                return false;
            }
            node = node.right;
        }
        return true;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    private static void report(String label, TreeNode flattened, List<Integer> expected) {
        print(label + " chain", rightChain(flattened), expected);
        print(label + " no left links", allLeftPointersNull(flattened), true);
    }

    public static void main(String[] args) {
        BinaryTreeToLinkedList solver = new BinaryTreeToLinkedList();

        TreeNode typical = sampleTree();
        System.out.println("input tree (preorder): " + Arrays.asList(1, 2, 3, 4, 5, 6));
        solver.flatten(typical);
        report("case 1 recursive typical", typical, Arrays.asList(1, 2, 3, 4, 5, 6));

        TreeNode single = new TreeNode(1);
        solver.flatten(single);
        report("case 2 single node      ", single, Arrays.asList(1));

        // Every node is a left child - the whole tree has to swing to the right.
        TreeNode leftChain = new TreeNode(1);
        leftChain.left = new TreeNode(2);
        leftChain.left.left = new TreeNode(3);
        solver.flatten(leftChain);
        report("case 3 left-only chain  ", leftChain, Arrays.asList(1, 2, 3));

        solver.flatten(null); // must not throw
        print("case 4 null tree        ", rightChain(null), Collections.<Integer>emptyList());

        TreeNode iterative = sampleTree();
        solver.flattenIterative(iterative);
        report("case 5 iterative typical", iterative, Arrays.asList(1, 2, 3, 4, 5, 6));
    }
}

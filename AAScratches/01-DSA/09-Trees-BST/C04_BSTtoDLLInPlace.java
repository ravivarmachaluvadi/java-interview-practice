/*
 * =====================================================================
 *  BST to Sorted Doubly Linked List (in place)   LeetCode 426 variant (non-circular) | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a BST, rewire the nodes into a sorted doubly linked list without
 *   allocating new nodes: reuse `left` as the prev pointer and `right` as the next pointer.
 *   Return the head (smallest value). The head's prev and the tail's next must be null.
 *
 * EXAMPLE
 *   tree 4 / (2: 1, 3) \ (6: 5, 7)   ->  forward 1 2 3 4 5 6 7, backward 7 6 5 4 3 2 1
 *   single node 4                    ->  forward 4, backward 4
 *   right chain 1 -> 2 -> 3          ->  forward 1 2 3, backward 3 2 1
 *   empty tree                       ->  head null, forward ""
 *
 * APPROACH  (in-order traversal with a `prev` pointer doing the rewiring)
 *   1. In-order traversal visits BST nodes in ascending order, which is the list order we want.
 *   2. Keep two fields across the recursion: `head` (first node visited) and `prev` (the node
 *      visited just before the current one).
 *   3. On visiting a node: if prev is null this is the smallest node, so it becomes head;
 *      otherwise link prev.right = node and node.left = prev.
 *   4. Set prev = node, then recurse into the right subtree.
 *   5. Order matters: recurse LEFT before touching the current node's pointers, because the
 *      left child must still be reachable when we descend. After the left subtree is fully
 *      processed, overwriting node.left is safe.
 *
 * KEY INSIGHT
 *   "In-order + prev pointer" is the reusable skeleton (same as GetMinimumDifference); here
 *   the action performed on (prev, current) is pointer surgery instead of a comparison.
 *   The classic mistake is losing the head: it is only known at the FIRST visit, so capture it
 *   when prev == null and never touch it again.
 *
 * COMPLEXITY
 *   Time  O(n)  each node visited once
 *   Space O(H)  recursion stack, H = tree height (O(n) worst case for a skewed tree)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Make it circular (LeetCode 426): after the walk set head.left = prev, prev.right = head.
 *   - Do it in O(1) extra space: Morris in-order traversal does the rewiring without a stack.
 *   - Reverse direction (BST -> DLL descending): reverse in-order, same code with left/right
 *     swapped.
 *   - Convert back: sorted DLL -> balanced BST (LeetCode 109 style, in-order construction).
 *
 * RUN
 *   main() runs 4 cases (typical, single node, right chain, empty) and prints the list walked
 *   forward and backward vs expected.
 */
class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int val) {
        this.val = val;
    }
}

class BSTtoDLLInPlace {

    private TreeNode head;   // first node visited in order = smallest value
    private TreeNode prev;   // node visited just before the current one

    public TreeNode bstToDLL(TreeNode root) {
        head = null;         // reset so one converter instance can be reused across calls
        prev = null;
        inorderRewire(root);
        return head;
    }

    private void inorderRewire(TreeNode node) {
        if (node == null) return;

        inorderRewire(node.left);        // must finish the left subtree before node.left is reused

        if (prev == null) {
            head = node;                 // first node in sorted order
        } else {
            prev.right = node;           // prev.next = node
            node.left = prev;            // node.prev = prev
        }
        prev = node;

        inorderRewire(node.right);
    }

    // ---------------------------------------------------------------
    // Helpers for main
    // ---------------------------------------------------------------
    private static String forward(TreeNode head) {
        StringBuilder sb = new StringBuilder();
        for (TreeNode cur = head; cur != null; cur = cur.right) sb.append(cur.val).append(' ');
        return sb.toString().trim();
    }

    // walks to the tail via `right`, then back via `left`; proves the prev links are correct
    private static String backward(TreeNode head) {
        if (head == null) return "";
        TreeNode tail = head;
        while (tail.right != null) tail = tail.right;
        StringBuilder sb = new StringBuilder();
        for (TreeNode cur = tail; cur != null; cur = cur.left) sb.append(cur.val).append(' ');
        return sb.toString().trim();
    }

    //        4
    //      /   \
    //     2     6
    //    / \   / \
    //   1   3 5   7
    private static TreeNode buildTree() {
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(6);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);
        root.right.left = new TreeNode(5);
        root.right.right = new TreeNode(7);
        return root;
    }

    private static TreeNode buildRightChain() {
        TreeNode root = new TreeNode(1);
        root.right = new TreeNode(2);
        root.right.right = new TreeNode(3);
        return root;
    }

    private static void check(String label, TreeNode head,
                              String expectedForward, String expectedBackward) {
        System.out.println(label + " forward: " + forward(head)
                + "   expected " + expectedForward);
        System.out.println(label + " backward: " + backward(head)
                + "   expected " + expectedBackward);
    }

    public static void main(String[] args) {
        BSTtoDLLInPlace converter = new BSTtoDLLInPlace();

        check("case 1 (typical)", converter.bstToDLL(buildTree()),
                "1 2 3 4 5 6 7", "7 6 5 4 3 2 1");
        check("case 2 (single)", converter.bstToDLL(new TreeNode(4)), "4", "4");
        check("case 3 (right chain)", converter.bstToDLL(buildRightChain()), "1 2 3", "3 2 1");
        check("case 4 (empty)", converter.bstToDLL(null), "", "");
        System.out.println("case 4 head is null: " + (converter.bstToDLL(null) == null)
                + "   expected true");
    }
}

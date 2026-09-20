/*
 * =====================================================================
 *  Nth Largest Element in a BST            LeetCode 230 variant | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary search tree and an integer N (1-based), return the Nth largest
 *   value in the tree. If the tree has fewer than N nodes, return -1.
 *   (LeetCode 230 asks for the kth SMALLEST; it is the same code with left/right swapped.)
 *
 * EXAMPLE
 *   tree = 50 / (30: 20, 40) \ (70: 60, 80),  N = 3  ->  60   descending: 80, 70, 60
 *   same tree, N = 1                                  ->  80   the maximum
 *   same tree, N = 7                                  ->  20   the minimum (last node)
 *   same tree, N = 8                                  ->  -1   fewer than N nodes
 *
 * APPROACH  (reverse in-order traversal with a counter and early stop)
 *   1. In-order (left, node, right) on a BST yields ascending order, so reverse in-order
 *      (right, node, left) yields descending order.
 *   2. Walk right-node-left, incrementing a visit counter each time a node is "visited".
 *   3. When the counter reaches N, record that node's value and stop.
 *   4. Every recursive call first checks "count >= N" so no work is done after the answer
 *      is found. The counter and result are reset at the start of each query.
 *
 * KEY INSIGHT
 *   The BST's sorted order is free via in-order traversal; an order statistic (kth
 *   smallest/largest) is just "visit in sorted order and count". Pattern: any time a
 *   problem says "kth ... in a BST", think in-order (or reverse in-order) + counter + early exit.
 *
 * COMPLEXITY
 *   Time  O(H + N)  descend H levels to reach the maximum, then visit N nodes; O(n) worst case
 *   Space O(H)      recursion stack; H = height (log n balanced, n skewed)
 *
 * INTERVIEW FOLLOW-UPS
 *   - kth smallest instead: identical code with left and right swapped (LeetCode 230).
 *   - Frequent queries with inserts/deletes: store subtree sizes in each node -> O(H) per query.
 *   - Iterative version: explicit stack, push all right children first; avoids deep recursion.
 *   - Fixed: counter and result were never reset, so a second query on the same object
 *     returned the stale result of the first.
 *
 * RUN
 *   main() runs 4 cases (typical, largest, smallest, N larger than tree size) and prints
 *   actual vs expected.
 */
class NthLargestInBST {
    private int visited;      // how many nodes have been emitted in descending order so far
    private int result;       // the Nth value once found, else -1

    public int findNthLargest(TreeNode root, int n) {
        visited = 0;          // reset per query so the same instance can be reused
        result = -1;
        reverseInorder(root, n);
        return result;
    }

    // right -> node -> left visits the BST in descending order
    private void reverseInorder(TreeNode node, int n) {
        if (node == null || visited >= n) return;   // already found the answer: stop early
        reverseInorder(node.right, n);
        visited++;
        if (visited == n) {
            result = node.val;
            return;
        }
        reverseInorder(node.left, n);
    }

    //        50
    //      /    \
    //    30      70
    //   /  \    /  \
    //  20  40  60  80
    private static TreeNode buildTree() {
        TreeNode root = new TreeNode(50);
        root.left = new TreeNode(30);
        root.right = new TreeNode(70);
        root.left.left = new TreeNode(20);
        root.left.right = new TreeNode(40);
        root.right.left = new TreeNode(60);
        root.right.right = new TreeNode(80);
        return root;
    }

    public static void main(String[] args) {
        NthLargestInBST bst = new NthLargestInBST();
        TreeNode root = buildTree();

        System.out.println("case 1 (N=3): " + bst.findNthLargest(root, 3) + "   expected 60");
        System.out.println("case 2 (N=1): " + bst.findNthLargest(root, 1) + "   expected 80");
        System.out.println("case 3 (N=7): " + bst.findNthLargest(root, 7) + "   expected 20");
        System.out.println("case 4 (N=8): " + bst.findNthLargest(root, 8) + "   expected -1");
    }
}

class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int x) {
        val = x;
    }
}

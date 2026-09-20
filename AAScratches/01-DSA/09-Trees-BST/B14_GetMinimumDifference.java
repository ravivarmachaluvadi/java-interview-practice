/*
 * =====================================================================
 *  Minimum Absolute Difference in BST          LeetCode 530 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a Binary Search Tree, return the smallest absolute
 *   difference between the values of any two different nodes in the tree.
 *   The tree has at least two nodes and all values are non-negative ints.
 *
 * EXAMPLE
 *   [4,2,6,1,3,5,7]          ->  1   (1 and 2, or any other neighbour pair)
 *   [1,null,5]               ->  4   (only two nodes exist)
 *   [236,104,701,null,227,null,911]
 *                            ->  9   (227 and 236, which are NOT parent/child)
 *
 * APPROACH  (in-order traversal with a remembered previous value)
 *   1. In a BST, an in-order walk (left, node, right) emits values in
 *      ascending order.
 *   2. The closest pair in a sorted sequence is always two *adjacent*
 *      entries, so only neighbouring pairs need to be compared.
 *   3. Walk in-order, keep the previously visited value in a field, and at
 *      every node take min(best, node.val - previous).
 *   4. `previous` is an Integer so null means "first node visited" - safer
 *      than an Integer.MIN_VALUE sentinel, which would overflow when subtracted.
 *
 * KEY INSIGHT
 *   Do not think "tree", think "sorted stream". The BST plus in-order gives
 *   you sorted order for free, and then this is the one-dimensional problem
 *   of finding the closest pair in a sorted array: compare neighbours only.
 *   The stateful `prev` pointer carried across in-order visits is the same
 *   device used by BST-to-doubly-linked-list and the greater-sum tree.
 *
 * COMPLEXITY
 *   Time  O(n)  every node is visited exactly once
 *   Space O(h)  recursion stack, h = height (O(log n) balanced, O(n) skewed)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Same question on an arbitrary binary tree: sort all values first, O(n log n).
 *   - Do it iteratively with an explicit stack to avoid deep recursion.
 *   - Return the actual pair of nodes, not just the difference.
 *
 * RUN
 *   main() runs 3 cases (balanced tree, two-node minimum, non-adjacent answer)
 *   and prints actual vs expected.
 */
class GetMinimumDifference {

    private int result;
    private Integer previous; // null until the first node is visited

    public int getMinimumDifference(TreeNode root) {
        result = Integer.MAX_VALUE;
        previous = null;
        inOrder(root);
        return result;
    }

    private void inOrder(TreeNode node) {
        if (node == null) {
            return;
        }

        inOrder(node.left);

        // In-order means node.val >= previous, so the subtraction is already
        // the absolute difference. Skip the very first node: it has no left neighbour.
        if (previous != null) {
            result = Math.min(result, node.val - previous);
        }
        previous = node.val;

        inOrder(node.right);
    }

    // ---------------------------------------------------------------
    // Test harness
    // ---------------------------------------------------------------

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        GetMinimumDifference solver = new GetMinimumDifference();

        // Case 1 - typical balanced BST
        //        4
        //       / \
        //      2   6
        //     / \ / \
        //    1  3 5  7
        TreeNode balanced = new TreeNode(4,
                new TreeNode(2, new TreeNode(1), new TreeNode(3)),
                new TreeNode(6, new TreeNode(5), new TreeNode(7)));
        print("case 1 balanced [1..7]", solver.getMinimumDifference(balanced), 1);

        // Case 2 - edge: the smallest legal tree, only two nodes
        TreeNode twoNodes = new TreeNode(1, null, new TreeNode(5));
        print("case 2 two nodes 1 and 5", solver.getMinimumDifference(twoNodes), 4);

        // Case 3 - tricky: the closest pair (227, 236) is not a parent/child pair.
        //   236 is the root and 227 is the right child of the root's left child.
        TreeNode skewedPairs = new TreeNode(236,
                new TreeNode(104, null, new TreeNode(227)),
                new TreeNode(701, null, new TreeNode(911)));
        print("case 3 non-adjacent pair", solver.getMinimumDifference(skewedPairs), 9);
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

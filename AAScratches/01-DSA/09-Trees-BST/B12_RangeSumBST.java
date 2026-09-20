/*
 * =====================================================================
 *  Range Sum of BST                            LeetCode 938 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary SEARCH tree and an inclusive range [low, high],
 *   return the sum of the values of all nodes whose value lies in that range.
 *   Node values are distinct. The tree may be empty; the range may match
 *   nothing at all, in which case the answer is 0.
 *
 * EXAMPLE
 *          10
 *        /    \
 *       5      15        low = 7, high = 15  ->  32   (7 + 10 + 15)
 *      / \       \
 *     3   7       18
 *
 *   low = 11, high = 14  ->  0    (no node in range; main() runs this)
 *   low = 3,  high = 18  ->  58   (whole tree)
 *
 * APPROACH  (BST-guided pruning)
 *   1. Null node -> contribute 0.
 *   2. If low <= node.data <= high, add node.data to the running sum.
 *   3. Recurse LEFT only if node.data > low. If node.data <= low, every value
 *      in the left subtree is smaller than node.data and therefore smaller than
 *      low, so the whole subtree is out of range - skip it.
 *   4. Recurse RIGHT only if node.data < high. Symmetric argument.
 *   5. Return the accumulated sum.
 *
 * KEY INSIGHT
 *   This is the first problem where the BST property is used to SKIP work
 *   rather than to locate one node. The two guard conditions are the whole
 *   lesson: a node's value is an upper bound on its entire left subtree and a
 *   lower bound on its entire right subtree, so one comparison can discard a
 *   whole subtree without visiting it. main() prints how many nodes were
 *   actually touched to make the pruning visible. Without the guards the code
 *   still returns the right answer - it just becomes a full O(n) traversal.
 *
 * COMPLEXITY
 *   Time  O(n) worst case (a wide range touches everything). For a balanced
 *         tree and a narrow range it is O(h + k) = O(log n + k), where k is the
 *         number of in-range nodes, because pruning cuts off both flanks.
 *   Space O(h) recursion stack; O(log n) balanced, O(n) for a degenerate chain.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Count nodes in range instead of summing: same guards, add 1 not the value.
 *   - Return the in-range values in sorted order: same pruning, in-order visit.
 *   - Make it iterative with an explicit stack (same two guard conditions).
 *   - Many range queries on a static tree: precompute subtree sums, or use an
 *     order-statistic tree / Fenwick tree over the sorted values.
 *   - What if it is NOT a BST? The guards become invalid; you must visit all n.
 *
 * RUN
 *   main() runs 4 cases (typical range, empty range, whole tree, empty tree)
 *   and prints actual vs expected, plus the pruned visit count.
 */

class TreeNode {
    int data;
    TreeNode left, right;

    TreeNode(int x) {
        data = x;
    }
}

class RangeSumBST {

    /** Nodes actually visited by the last rangeSumBST call - evidence of pruning. */
    private int nodesVisited;

    public int rangeSumBST(TreeNode root, int low, int high) {
        nodesVisited = 0;
        return sum(root, low, high);
    }

    public int visitedCount() {
        return nodesVisited;
    }

    private int sum(TreeNode node, int low, int high) {
        if (node == null) return 0;
        nodesVisited++;

        int total = 0;
        if (node.data >= low && node.data <= high) {
            total += node.data;
        }

        // Left subtree holds only values < node.data. If node.data <= low they
        // are all below the range, so the whole subtree can be skipped.
        if (node.data > low) {
            total += sum(node.left, low, high);
        }

        // Mirror image: right subtree holds only values > node.data.
        if (node.data < high) {
            total += sum(node.right, low, high);
        }
        return total;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        //        10
        //      /    \
        //     5      15
        //    / \       \
        //   3   7       18
        TreeNode root = new TreeNode(10);
        root.left = new TreeNode(5);
        root.right = new TreeNode(15);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(7);
        root.right.right = new TreeNode(18);

        RangeSumBST solution = new RangeSumBST();

        print("case 1 range [7, 15] ", solution.rangeSumBST(root, 7, 15), "32");
        // 3 and 18 are never touched - both flanks are pruned by the guards.
        print("case 1 nodes visited ", solution.visitedCount() + " of 6", "4 of 6");

        print("case 2 range [11, 14]", solution.rangeSumBST(root, 11, 14), "0");
        print("case 2 nodes visited ", solution.visitedCount() + " of 6", "2 of 6");

        print("case 3 range [3, 18] ", solution.rangeSumBST(root, 3, 18), "58");
        // A range covering everything prunes nothing, so this degrades to full O(n).
        print("case 3 nodes visited ", solution.visitedCount() + " of 6", "6 of 6");

        print("case 4 empty tree    ", solution.rangeSumBST(null, 1, 100), "0");
    }
}

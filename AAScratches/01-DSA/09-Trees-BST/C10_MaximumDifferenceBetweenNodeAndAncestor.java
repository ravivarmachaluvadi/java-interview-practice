/*
 * =====================================================================
 *  Maximum Difference Between Node and Ancestor   LeetCode 1026 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, find the largest value of |a.val - b.val| where a is
 *   an ancestor of b (a is on the root-to-b path and a != b). Every node has an ancestor
 *   except the root, and the tree has at least 2 nodes in the real problem, so the answer
 *   is always achieved by some pair. This file returns 0 for empty and single-node trees.
 *
 *   Fixed: the running answer lived in a field that was never reset, so a second call on
 *   a different tree returned the previous (larger) result. It is now reset per call.
 *
 * EXAMPLE
 *            8
 *          /   \
 *         3     10        ->  7   because |8 - 1| = 7, and 8 is an ancestor of 1
 *        / \      \
 *       1   6      14
 *          / \    /
 *         4   7  13
 *   tree = [7]            ->  0   no ancestor pair exists
 *   tree = 1 -> 2 -> 3 -> 4 (a right chain)  ->  3   the two ends of the chain
 *   tree = [-10, 5, -20]  ->  15  negative values work the same, |-10 - 5| = 15
 *
 * APPROACH  (carry the path min and the path max downward)
 *   1. The best partner for a node is always the smallest OR the largest value above it,
 *      so you never need to remember the whole path - two numbers are enough.
 *   2. DFS from the root carrying (minOnPath, maxOnPath) for the path down to the parent.
 *   3. At each node, the best pair ending here is max(|minOnPath - val|, |maxOnPath - val|).
 *      Record it against the running answer.
 *   4. Widen the window for the children: min = min(min, val), max = max(max, val).
 *   5. Seed the recursion with min = max = root.val so the root scores 0 against itself.
 *
 * KEY INSIGHT
 *   Count Good Nodes (C09) carries one value down; this carries two, and the cost is the
 *   same. That is the lesson: widening the downward state is free, so ask "what is the
 *   smallest summary of the path above me that answers the question?" Here the answer is
 *   the extremes, because |x - val| is maximised at an endpoint of the ancestor range.
 *
 * COMPLEXITY
 *   Time  O(n)  one visit per node, O(1) work each
 *   Space O(h)  recursion stack, h = height, O(n) for a skewed tree
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the two node values that achieve the maximum, not just the difference
 *   - Maximum difference where the ancestor must be STRICTLY above by k levels
 *   - Count Good Nodes (LC 1448): the same downward carry with one value instead of two
 *   - Do it without recursion: push (node, min, max) triples onto an explicit stack
 *
 * RUN
 *   main() runs 4 cases (typical, single node, right chain, negative values) and prints
 *   actual vs expected.
 */
class MaximumDifferenceBetweenNodeAndAncestor {

    private int maxDiff;

    public int maxAncestorDiff(TreeNode root) {
        maxDiff = 0;                 // reset so one solver can serve several trees
        if (root == null) return 0;
        dfs(root, root.val, root.val);
        return maxDiff;
    }

    /** minOnPath / maxOnPath summarise the root-to-parent path above this node. */
    private void dfs(TreeNode node, int minOnPath, int maxOnPath) {
        if (node == null) return;

        // The farthest ancestor value is always one of the two extremes seen so far.
        int best = Math.max(Math.abs(minOnPath - node.val), Math.abs(maxOnPath - node.val));
        maxDiff = Math.max(maxDiff, best);

        // Widen the window before descending; each branch gets its own copy.
        int minForChildren = Math.min(minOnPath, node.val);
        int maxForChildren = Math.max(maxOnPath, node.val);

        dfs(node.left, minForChildren, maxForChildren);
        dfs(node.right, minForChildren, maxForChildren);
    }

    public static void main(String[] args) {
        MaximumDifferenceBetweenNodeAndAncestor solver =
                new MaximumDifferenceBetweenNodeAndAncestor();

        // case 1: typical - the winning pair is the root 8 and its descendant 1
        //            8
        //          /   \
        //         3     10
        //        / \      \
        //       1   6      14
        //          / \    /
        //         4   7  13
        TreeNode typical = new TreeNode(8);
        typical.left = new TreeNode(3);
        typical.right = new TreeNode(10);
        typical.left.left = new TreeNode(1);
        typical.left.right = new TreeNode(6);
        typical.left.right.left = new TreeNode(4);
        typical.left.right.right = new TreeNode(7);
        typical.right.right = new TreeNode(14);
        typical.right.right.left = new TreeNode(13);
        print("case 1 typical        ", solver.maxAncestorDiff(typical), 7);

        // case 2: no ancestor pair exists at all
        print("case 2 single node    ", solver.maxAncestorDiff(new TreeNode(7)), 0);

        // case 3: a right-leaning chain 1 -> 2 -> 3 -> 4; the ends win
        TreeNode chain = new TreeNode(1);
        chain.right = new TreeNode(2);
        chain.right.right = new TreeNode(3);
        chain.right.right.right = new TreeNode(4);
        print("case 3 right chain    ", solver.maxAncestorDiff(chain), 3);

        // case 4: negatives - |-10 - 5| = 15 beats |-10 - (-20)| = 10
        //      -10
        //      /  \
        //     5   -20
        TreeNode negatives = new TreeNode(-10);
        negatives.left = new TreeNode(5);
        negatives.right = new TreeNode(-20);
        print("case 4 negative values", solver.maxAncestorDiff(negatives), 15);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}

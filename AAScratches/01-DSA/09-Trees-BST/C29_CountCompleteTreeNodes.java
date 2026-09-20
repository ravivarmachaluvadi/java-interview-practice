/*
 * =====================================================================
 *  Count Complete Tree Nodes                      LeetCode 222 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Count the nodes of a COMPLETE binary tree: every level is full except possibly the last,
 *   and the last level is filled left to right. Walking the whole tree is O(n) and trivially
 *   correct; the interviewer wants better than O(n), which is only possible because the shape
 *   is guaranteed. Return 0 for an empty tree.
 *
 * EXAMPLE
 *         1                         1
 *        / \                       / \
 *       2   3     ->  6           2   3     ->  7   (perfect: 2^3 - 1, counted without
 *      / \  /                    / \ / \             visiting a single child)
 *     4  5 6                    4 5 6  7
 *   tree = []   ->  0        tree = [1]  ->  1
 *
 * APPROACH  (perfect-subtree shortcut)
 *   1. Walk left-only from the node counting nodes: that is leftHeight, the depth of the
 *      leftmost path. Walk right-only the same way: rightHeight.
 *   2. If they are equal the subtree is PERFECT (complete plus a full last level), so it holds
 *      exactly 2^height - 1 nodes. Return (1 << height) - 1 and stop: no recursion at all.
 *   3. If they differ, the last level is only partly filled here. Return
 *      1 + countNodes(left) + countNodes(right) and let the recursion find the perfect parts.
 *   4. Because the tree is complete, at every level at most ONE of the two children can be
 *      imperfect, so the recursion follows a single root-to-leaf path, not the whole tree.
 *
 * KEY INSIGHT
 *   Comparing the leftmost and rightmost depths is a two-pointer test for "is this subtree
 *   perfect", and a perfect subtree is counted by arithmetic instead of traversal. That is the
 *   whole speed-up: O(log n) levels, each costing O(log n) for the two height walks, giving
 *   O(log^2 n) instead of O(n). Recognise the shape: when the structure is guaranteed, closed
 *   form beats traversal.
 *
 * COMPLEXITY
 *   Time  O(log^2 n)  one recursive path of O(log n) levels, each doing two O(log n) walks
 *   Space O(log n)    recursion stack, the tree is balanced by definition
 *   (countNodesNaive below is the O(n) baseline, kept so main() can cross-check the shortcut.)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Prove the O(log^2 n) bound: why can only one child per level be imperfect?
 *   - Solve it by binary searching the last level and testing whether index k exists,
 *     using the bits of k as a left/right path from the root: also O(log^2 n)
 *   - What breaks if the tree is merely balanced, not complete? The shortcut over-counts
 *   - Find the k-th node in level order without a queue: same bit-path descent
 *
 * RUN
 *   main() runs 5 cases (empty, single node, partly filled last level, perfect, larger
 *   partly filled) and prints the shortcut, the O(n) baseline and the expected count.
 */

class CountCompleteTreeNodes {

    public static int countNodes(TreeNode root) {
        if (root == null) {
            return 0;
        }

        // Depth of the leftmost path and of the rightmost path, measured in nodes.
        int leftHeight = heightGoingLeft(root);
        int rightHeight = heightGoingRight(root);

        if (leftHeight == rightHeight) {
            // Equal depths on a complete tree means the last level is full: it is perfect.
            return (1 << leftHeight) - 1;   // 2^height - 1
        }

        // Imperfect here, so recurse. Completeness guarantees one side is already perfect,
        // and that call returns immediately at step 2 above.
        return 1 + countNodes(root.left) + countNodes(root.right);
    }

    private static int heightGoingLeft(TreeNode node) {
        int height = 0;
        while (node != null) {
            height++;
            node = node.left;
        }
        return height;
    }

    private static int heightGoingRight(TreeNode node) {
        int height = 0;
        while (node != null) {
            height++;
            node = node.right;
        }
        return height;
    }

    // The obvious O(n) count, kept only as a reference to check the shortcut against.
    private static int countNodesNaive(TreeNode node) {
        if (node == null) return 0;
        return 1 + countNodesNaive(node.left) + countNodesNaive(node.right);
    }

    public static void main(String[] args) {
        // case 1: edge, empty tree
        check("case 1 empty            ", buildComplete(0), 0);

        // case 2: edge, single node
        check("case 2 single node      ", buildComplete(1), 1);

        // case 3: typical, last level partly filled (the original example)
        //        1
        //       / \
        //      2   3
        //     / \  /
        //    4  5 6
        check("case 3 partial last row ", buildComplete(6), 6);

        // case 4: perfect tree, answered by 2^3 - 1 with no recursion
        check("case 4 perfect tree     ", buildComplete(7), 7);

        // case 5: tricky, deeper tree whose last level stops mid-way
        check("case 5 twelve nodes     ", buildComplete(12), 12);
    }

    private static void check(String label, TreeNode root, int expected) {
        System.out.println(label + ": shortcut " + countNodes(root)
                + ", naive " + countNodesNaive(root) + "   expected " + expected);
    }

    // Builds the complete tree holding values 1..n: node i has children 2i and 2i+1.
    private static TreeNode buildComplete(int n) {
        if (n < 1) return null;

        TreeNode[] nodes = new TreeNode[n + 1];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new TreeNode(i);
        }
        for (int i = 1; i <= n; i++) {
            if (2 * i <= n) nodes[i].left = nodes[2 * i];
            if (2 * i + 1 <= n) nodes[i].right = nodes[2 * i + 1];
        }
        return nodes[1];
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

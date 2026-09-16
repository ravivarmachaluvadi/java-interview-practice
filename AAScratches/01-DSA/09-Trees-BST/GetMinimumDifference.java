/**
 * Problem: Given the root of a Binary Search Tree (BST), find the minimum absolute difference
 * between values of any two different nodes in the tree.
 *
 * Approach: Perform an in-order traversal to visit nodes in ascending order. Keep track of the
 * previously visited node value; for each current node, compute the difference with the previous
 * value and update the running minimum result. This works because adjacent nodes in in-order
 * are the closest values in a BST.
 *
 * Time Complexity: O(n), where n is the number of nodes (each node visited once).
 * Space Complexity: O(h) for recursion stack, where h is the tree height (worst-case O(n),
 * average-case O(log n)).
 */
class GetMinimumDifference {
    private int result;
    private Integer previous;

    public int getMinimumDifference(TreeNode root) {
        result = Integer.MAX_VALUE;
        previous = null;
        inOrder(root);
        return result;
    }

    // in-order traversal
    private void inOrder(TreeNode node) {
        if (node == null)
            return;

        inOrder(node.left);
        // remember prevNode or value not null
        if (previous != null)
            result = Math.min(result, node.val - previous);

        previous = node.val;
        inOrder(node.right);
    }

    public static void main(String[] args) {
        // Construct a small BST:
        //        4
        //       / \
        //      2   6
        //     / \ / \
        //    1  3 5 7
        TreeNode root = new TreeNode(4,
                new TreeNode(2, new TreeNode(1), new TreeNode(3)),
                new TreeNode(6, new TreeNode(5), new TreeNode(7)));

        GetMinimumDifference solver = new GetMinimumDifference();
        int minDiff = solver.getMinimumDifference(root);

        System.out.println("Input tree (in-order): 1, 2, 3, 4, 5, 6, 7");
        System.out.println("Minimum difference between successive nodes: " + minDiff);
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
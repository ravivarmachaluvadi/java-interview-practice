/*
 * =====================================================================
 *  Maximum Depth (Height) of Binary Tree    LeetCode 104 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, return its maximum depth: the number of nodes on
 *   the longest path from the root down to a leaf. An empty tree has depth 0.
 *
 * EXAMPLE
 *   tree:    1              ->  3   (path 1 -> 2 -> 4)
 *           / \
 *          2   3
 *         /
 *        4
 *   tree: (empty)           ->  0
 *   tree: single node 7     ->  1
 *   tree: 1 -> 2 -> 3 -> 4 (right-skewed)  ->  4   (depth equals the node count)
 *
 * APPROACH  (recursive height, 1 + max(children))
 *   1. A null node has height 0. This base case is what makes the formula right
 *      for a leaf: 1 + max(0, 0) = 1.
 *   2. Otherwise ask each child for its height (postorder: children first).
 *   3. Return 1 (this node) + the larger of the two child heights.
 *   A second method, maxDepthBfs, counts levels with a queue: each pass over the
 *   current queue size handles one level, so the number of passes is the depth.
 *
 * KEY INSIGHT
 *   This is the first tree recursion that RETURNS a computed value instead of just
 *   visiting. "Ask both children, combine, return" is the skeleton reused verbatim in
 *   diameter, balanced-tree check, subtree sums and counting complete-tree nodes.
 *   Depth in nodes = 1 + max(child depths); depth in edges would return -1 for null.
 *
 * COMPLEXITY
 *   Time  O(n)  each node is visited once
 *   Space O(h)  recursion stack depth is the tree height; O(n) for a skewed tree.
 *               maxDepthBfs instead holds one whole level, O(w), which is about
 *               n/2 for a complete tree even though h is only log n
 *
 * INTERVIEW FOLLOW-UPS
 *   - Minimum depth (LC 111): the trap is that a node with ONE child is not a leaf.
 *   - Iterative version: BFS by levels (shown) or a stack of (node, depth) pairs.
 *   - Diameter (LC 543) and balanced check (LC 110) are this recursion plus one line.
 *   - Very deep skewed tree: recursion can overflow the call stack, so prefer BFS.
 *
 * RUN
 *   main() runs 4 cases (typical, empty, single node, skewed) through both methods
 *   and prints actual vs expected.
 */
import java.util.ArrayDeque;
import java.util.Queue;

class HeightOfBinaryTree {

    /** Recursive: height of a node is 1 + the taller of its two subtrees. */
    public int maxDepth(TreeNode root) {
        if (root == null) return 0;   // base case: nothing below a null, height 0
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }

    /** Iterative: BFS level by level; the number of levels drained is the depth. */
    public int maxDepthBfs(TreeNode root) {
        if (root == null) return 0;
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        int depth = 0;
        while (!queue.isEmpty()) {
            int levelSize = queue.size();   // everything in the queue right now is one level
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            depth++;
        }
        return depth;
    }

    public static void main(String[] args) {
        HeightOfBinaryTree solver = new HeightOfBinaryTree();

        //        1
        //       / \
        //      2   3
        //     /
        //    4
        TreeNode typical = new TreeNode(1, new TreeNode(2, new TreeNode(4), null), new TreeNode(3));

        TreeNode single = new TreeNode(7);

        // 1 -> 2 -> 3 -> 4, every node has only a right child
        TreeNode rightSkewed = new TreeNode(1, null,
                new TreeNode(2, null,
                        new TreeNode(3, null,
                                new TreeNode(4))));

        print("case 1 typical,      recursive", solver.maxDepth(typical), 3);
        print("case 1 typical,      bfs      ", solver.maxDepthBfs(typical), 3);
        print("case 2 empty,        recursive", solver.maxDepth(null), 0);
        print("case 2 empty,        bfs      ", solver.maxDepthBfs(null), 0);
        print("case 3 single node,  recursive", solver.maxDepth(single), 1);
        print("case 3 single node,  bfs      ", solver.maxDepthBfs(single), 1);
        print("case 4 right-skewed, recursive", solver.maxDepth(rightSkewed), 4);
        print("case 4 right-skewed, bfs      ", solver.maxDepthBfs(rightSkewed), 4);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
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

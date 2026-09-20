/*
 * =====================================================================
 *  Binary Search Tree to Greater Sum Tree                  LeetCode 1038 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a BST, change every node's value to the sum of all values in the
 *   original tree that are greater than or equal to it (its own value included). Return the
 *   root. The tree shape does not change. Values are distinct.
 *
 * EXAMPLE
 *   BST with values 0..8 (root 4)          ->  in-order [36, 36, 35, 33, 30, 26, 21, 15, 8]
 *     e.g. node 4 becomes 4+5+6+7+8 = 30, node 8 stays 8, node 0 becomes 0+1+...+8 = 36
 *   single node [5]                        ->  [5]
 *   left chain 3 -> 2 -> 1                 ->  [6, 5, 3]
 *   empty tree                             ->  []
 *
 * APPROACH  (reverse in-order traversal carrying a running sum)
 *   1. Visit nodes right -> node -> left, i.e. from the largest value down to the smallest.
 *   2. Keep one running total. On visiting a node, add its value to the total, then overwrite
 *      the node's value with the total.
 *   3. Because every larger value has already been visited, the total at that moment is
 *      exactly "sum of all values >= this node".
 *   4. Reset the running total at the start of each call so the converter is reusable.
 *
 * KEY INSIGHT
 *   The traversal order IS the algorithm. Reverse in-order hands you the values sorted
 *   descending, so a single accumulator answers "sum of everything bigger than me" with no
 *   extra pass and no extra data structure. Pattern: "each BST node depends on all larger
 *   (or smaller) values" -> reverse in-order (or in-order) with carried state.
 *
 * COMPLEXITY
 *   Time  O(n)  every node visited exactly once
 *   Space O(H)  recursion stack, H = tree height (O(n) for a skewed tree)
 *
 * INTERVIEW FOLLOW-UPS
 *   - "Sum of strictly greater values" (exclusive): overwrite before adding, not after.
 *   - Do it without recursion: iterative reverse in-order with an explicit stack, or a
 *     reverse Morris traversal for O(1) extra space.
 *   - Why reverse in-order rather than a HashMap of suffix sums? Same O(n) time but O(1)
 *     extra state and one pass.
 *
 * RUN
 *   main() runs 4 cases (LeetCode example, single node, left chain, empty) and prints the
 *   in-order values of the converted tree vs expected.
 */
import java.util.ArrayList;
import java.util.List;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}

class BinarySearchTreeToGreaterSumTree {
    private int runningSum;   // sum of all values visited so far (all of them >= current node)

    public TreeNode bstToGst(TreeNode root) {
        runningSum = 0;       // reset so the converter can be called more than once
        reverseInorder(root);
        return root;
    }

    private void reverseInorder(TreeNode node) {
        if (node == null) return;
        reverseInorder(node.right);   // larger values first
        runningSum += node.val;
        node.val = runningSum;        // now equals sum of every value >= original node.val
        reverseInorder(node.left);    // smaller values last
    }

    // ---------------------------------------------------------------
    // Helpers for main
    // ---------------------------------------------------------------
    private static List<Integer> inorder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        inorder(root, out);
        return out;
    }

    private static void inorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        inorder(node.left, out);
        out.add(node.val);
        inorder(node.right, out);
    }

    // LeetCode 1038 example 1:
    //            4
    //         /     \
    //        1       6
    //       / \     / \
    //      0   2   5   7
    //           \       \
    //            3       8
    private static TreeNode buildLeetCodeExample() {
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(1);
        root.right = new TreeNode(6);
        root.left.left = new TreeNode(0);
        root.left.right = new TreeNode(2);
        root.left.right.right = new TreeNode(3);
        root.right.left = new TreeNode(5);
        root.right.right = new TreeNode(7);
        root.right.right.right = new TreeNode(8);
        return root;
    }

    // 3 -> 2 -> 1 hanging off the left every time
    private static TreeNode buildLeftChain() {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(2);
        root.left.left = new TreeNode(1);
        return root;
    }

    public static void main(String[] args) {
        BinarySearchTreeToGreaterSumTree converter = new BinarySearchTreeToGreaterSumTree();

        System.out.println("case 1 (LC example): " + inorder(converter.bstToGst(buildLeetCodeExample()))
                + "   expected [36, 36, 35, 33, 30, 26, 21, 15, 8]");
        System.out.println("case 2 (single node): " + inorder(converter.bstToGst(new TreeNode(5)))
                + "   expected [5]");
        System.out.println("case 3 (left chain): " + inorder(converter.bstToGst(buildLeftChain()))
                + "   expected [6, 5, 3]");
        System.out.println("case 4 (empty): " + inorder(converter.bstToGst(null))
                + "   expected []");
    }
}

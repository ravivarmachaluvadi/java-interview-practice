/*
 * =====================================================================
 *  Binary Tree Postorder Traversal          LeetCode 145 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, return the values of its nodes in postorder:
 *   left subtree, then right subtree, then the node itself. The tree may be empty.
 *
 * EXAMPLE
 *   tree:      1              ->  [4, 5, 2, 6, 7, 3, 1]
 *            /   \
 *           2     3
 *          / \   / \
 *         4   5 6   7
 *   tree: (empty)               ->  []
 *   tree: 1 -> 2 -> 3 (left-skewed)  ->  [3, 2, 1]
 *
 * APPROACH  (recursive DFS, postorder)
 *   1. If the node is null, return (the base case that ends the recursion).
 *   2. Recurse into the left child, then into the right child.
 *   3. Only after both children are done, add this node's value to the list.
 *   A second method, iterativePostorder, does the same with an explicit stack:
 *   walk root-right-left (a mirrored preorder) and reverse the list at the end.
 *
 * KEY INSIGHT
 *   The three DFS orders differ only in WHERE the "visit" line sits relative to the
 *   two recursive calls: before both = preorder, between = inorder, after both =
 *   postorder. Postorder is "children before parent", which is why every problem that
 *   aggregates a subtree (height, sum, diameter, balance) is really a postorder walk.
 *
 * COMPLEXITY
 *   Time  O(n)  every node is visited exactly once
 *   Space O(h)  the recursion (or explicit) stack is as deep as the tree; O(n) if skewed
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it iteratively: the reverse-of-root-right-left trick shown here.
 *   - Do it iteratively with ONE stack and a "last visited" pointer (harder).
 *   - Morris traversal: O(1) extra space by temporarily re-wiring right pointers.
 *   - Which order deletes a tree safely or evaluates an expression tree? Postorder.
 *
 * RUN
 *   main() runs 3 cases (full tree, empty tree, left-skewed) through both methods
 *   and prints actual vs expected.
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

class TreeNode {
    int data;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        data = val;
    }
}

class RecursivePostorder {

    /** Recursive postorder: left subtree, right subtree, then the node itself. */
    public List<Integer> postorder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        collectPostorder(root, result);
        return result;
    }

    private void collectPostorder(TreeNode node, List<Integer> result) {
        if (node == null) {
            return;
        }
        collectPostorder(node.left, result);
        collectPostorder(node.right, result);
        result.add(node.data);   // visit AFTER both children; where this line sits defines the order
    }

    /** Iterative postorder: produce root-right-left with a stack, then reverse it. */
    public List<Integer> iterativePostorder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            result.add(node.data);
            // push left first so right is popped (visited) first: that gives root-right-left
            if (node.left != null) stack.push(node.left);
            if (node.right != null) stack.push(node.right);
        }
        Collections.reverse(result);   // reverse of root-right-left is left-right-root
        return result;
    }

    public static void main(String[] args) {
        RecursivePostorder solver = new RecursivePostorder();

        //        1
        //      /   \
        //     2     3
        //    / \   / \
        //   4   5 6   7
        TreeNode full = new TreeNode(1);
        full.left = new TreeNode(2);
        full.right = new TreeNode(3);
        full.left.left = new TreeNode(4);
        full.left.right = new TreeNode(5);
        full.right.left = new TreeNode(6);
        full.right.right = new TreeNode(7);

        // 1 -> 2 -> 3 where every node has only a left child
        TreeNode leftSkewed = new TreeNode(1);
        leftSkewed.left = new TreeNode(2);
        leftSkewed.left.left = new TreeNode(3);

        print("case 1 full tree,   recursive", solver.postorder(full), "[4, 5, 2, 6, 7, 3, 1]");
        print("case 1 full tree,   iterative", solver.iterativePostorder(full), "[4, 5, 2, 6, 7, 3, 1]");
        print("case 2 empty tree,  recursive", solver.postorder(null), "[]");
        print("case 2 empty tree,  iterative", solver.iterativePostorder(null), "[]");
        print("case 3 left-skewed, recursive", solver.postorder(leftSkewed), "[3, 2, 1]");
        print("case 3 left-skewed, iterative", solver.iterativePostorder(leftSkewed), "[3, 2, 1]");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

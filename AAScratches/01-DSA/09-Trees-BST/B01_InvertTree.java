/*
 * =====================================================================
 *  Invert Binary Tree                                     LeetCode 226 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, swap the left and right children of every node so the tree
 *   becomes its mirror image. Return the (mutated) root. The tree may be empty.
 *
 * EXAMPLE
 *         4                 4
 *       /   \             /   \
 *      2     7    ->     7     2       level order [4,2,7,1,3,6,9] -> [4,7,2,9,6,3,1]
 *     / \   / \         / \   / \
 *    1   3 6   9       9   6 3   1
 *   empty tree                     ->  empty tree
 *   1 -> 2 -> 3 (left-only chain)  ->  right-only chain: [1,2,null,3] -> [1,null,2,null,3]
 *
 * APPROACH  (swap children recursively)
 *   1. Null node: nothing to invert, return null.
 *   2. Invert the right subtree and hang it on the left; invert the left subtree and hang it on
 *      the right. A temp holds the original left so it is not lost when left is overwritten.
 *   3. Return the node; everything below it is already mirrored.
 *
 * KEY INSIGHT
 *   Inverting a tree is "swap the two children" applied at every node. The visit order does
 *   not matter, so any traversal works: the second method does the same swap driven by a queue
 *   (BFS) instead of recursion, which is the standard "now do it without recursion" follow-up.
 *
 * COMPLEXITY
 *   Time  O(n)  every node is visited once and does O(1) work
 *   Space O(h)  recursion depth is the height (O(n) for a skewed tree); BFS holds one level, O(w)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it iteratively (queue or stack) - see invertTreeIterative.
 *   - Invert without mutating the input (build a new mirrored tree).
 *   - Check whether a tree is a mirror of itself without inverting (SymmetricTree, LC 101).
 *
 * RUN
 *   main() runs 3 cases (full tree, empty, left-only chain) through both methods and prints the
 *   level-order form of the result against the expected one.
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class InvertTree {

    /** Recursive: swap the two (already inverted) subtrees at every node. */
    public TreeNode invertTree(TreeNode root) {
        if (root == null) return null;

        TreeNode originalLeft = root.left;          // keep it, the next line overwrites root.left
        root.left = invertTree(root.right);
        root.right = invertTree(originalLeft);
        return root;
    }

    /** Iterative (BFS): visit every node once and swap its children in place. */
    public TreeNode invertTreeIterative(TreeNode root) {
        if (root == null) return null;

        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            TreeNode tmp = node.left;
            node.left = node.right;
            node.right = tmp;
            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }
        return root;
    }

    // ---------------------------------------------------------------- test helpers

    /** Builds a tree from a LeetCode-style level-order array; null means "no node here". */
    static TreeNode fromLevelOrder(Integer... vals) {
        if (vals.length == 0 || vals[0] == null) return null;
        TreeNode root = new TreeNode(vals[0]);
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        int i = 1;
        while (!queue.isEmpty() && i < vals.length) {
            TreeNode node = queue.poll();
            if (i < vals.length && vals[i] != null) {
                node.left = new TreeNode(vals[i]);
                queue.add(node.left);
            }
            i++;
            if (i < vals.length && vals[i] != null) {
                node.right = new TreeNode(vals[i]);
                queue.add(node.right);
            }
            i++;
        }
        return root;
    }

    /** Level-order string in LeetCode style, trailing nulls trimmed, e.g. [1, null, 2]. */
    static String toLevelOrder(TreeNode root) {
        if (root == null) return "[]";
        List<String> out = new ArrayList<>();
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node == null) {
                out.add("null");
                continue;
            }
            out.add(String.valueOf(node.val));
            queue.add(node.left);
            queue.add(node.right);
        }
        while (out.get(out.size() - 1).equals("null")) out.remove(out.size() - 1);
        return out.toString();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        InvertTree sol = new InvertTree();

        //         4
        //       /   \
        //      2     7
        //     / \   / \
        //    1   3 6   9
        Integer[] full = {4, 2, 7, 1, 3, 6, 9};
        print("case 1 full tree, recursive ",
                toLevelOrder(sol.invertTree(fromLevelOrder(full))), "[4, 7, 2, 9, 6, 3, 1]");
        print("case 1 full tree, iterative ",
                toLevelOrder(sol.invertTreeIterative(fromLevelOrder(full))), "[4, 7, 2, 9, 6, 3, 1]");

        print("case 2 empty, recursive     ", toLevelOrder(sol.invertTree(null)), "[]");
        print("case 2 empty, iterative     ", toLevelOrder(sol.invertTreeIterative(null)), "[]");

        // 1 -> 2 -> 3 all on the left becomes all on the right
        Integer[] chain = {1, 2, null, 3};
        print("case 3 left chain, recursive",
                toLevelOrder(sol.invertTree(fromLevelOrder(chain))), "[1, null, 2, null, 3]");
        print("case 3 left chain, iterative",
                toLevelOrder(sol.invertTreeIterative(fromLevelOrder(chain))), "[1, null, 2, null, 3]");
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

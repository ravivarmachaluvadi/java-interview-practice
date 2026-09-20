/*
 * =====================================================================
 *  Find Leaves of Binary Tree                    LeetCode 366 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Repeatedly strip all the leaves off a binary tree and record each harvest, until the
 *   tree is empty. Return the list of harvests in order: the first list is the original
 *   leaves, the second is the leaves of what remains, and so on down to the root.
 *
 * EXAMPLE
 *       1            round 1 removes 4, 5, 3
 *      / \           round 2 removes 2
 *     2   3          round 3 removes 1
 *    / \             ->  [[4, 5, 3], [2], [1]]
 *   4   5
 *   single node 1               ->  [[1]]
 *   right chain 1 -> 2 -> 3     ->  [[3], [2], [1]]
 *   null                        ->  []
 *
 * APPROACH  (one postorder pass; a node's height IS its removal round)
 *   1. dfs(node) returns the node's height: -1 for null, so a leaf gets 0.
 *   2. height = max(leftHeight, rightHeight) + 1, computed after both children, which is
 *      what makes this postorder - a parent cannot know its round before its children do.
 *   3. Append node.val to result[height], creating that bucket the first time it is used.
 *      A parent's height is always at least one more than a child's, so buckets are
 *      created in order and result.size() is never short by more than one.
 *   4. The result is returned as-is: index i holds the nodes removed in round i + 1.
 *
 * KEY INSIGHT
 *   Do not simulate the peeling. A node is removed in round h + 1 exactly when its height
 *   is h, because it survives until its deepest descendant is gone. That turns an
 *   O(n^2) repeat-until-empty simulation into a single O(n) traversal where height is
 *   used as an array index. "A computed value can be the bucket key" is the reframing
 *   to remember - the same trick answers "group nodes by depth, by column, by height".
 *
 * COMPLEXITY
 *   Time  O(n)  every node is visited exactly once.
 *   Space O(n)  the output lists, plus O(h) recursion stack (O(n) for a skewed tree).
 *
 * INTERVIEW FOLLOW-UPS
 *   - Actually detach the nodes so the caller's tree really shrinks - what changes?
 *   - Why is the naive "find leaves, delete, repeat" O(n^2) in the worst case?
 *   - Same shape: group nodes by DEPTH instead of height (level order) - what differs?
 *   - Do it iteratively with an explicit stack, keeping the postorder guarantee.
 *
 * RUN
 *   main() runs four trees (typical, single node, right-leaning chain, null) and prints
 *   each harvest list against the expected one.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Definition for a binary tree node.
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}

// https://leetcode.com/problems/find-leaves-of-binary-tree/description/
// 366. Find Leaves of Binary Tree
class FindLeavesOfBinaryTree {

    public static List<List<Integer>> findLeaves(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        dfs(root, result);
        return result;
    }

    /** Returns the height of the node: 0 for a leaf, -1 for null, parent = max + 1. */
    private static int dfs(TreeNode node, List<List<Integer>> result) {
        if (node == null) {
            return -1; // so that a leaf, whose children are both null, comes out as 0
        }
        int leftHeight = dfs(node.left, result);
        int rightHeight = dfs(node.right, result);
        int height = Math.max(leftHeight, rightHeight) + 1;

        if (height == result.size()) {
            result.add(new ArrayList<>()); // first node ever seen at this height
        }
        result.get(height).add(node.val); // height == the round this node is removed in
        return height;
    }

    // ---------- test helpers ----------

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        //     1
        //    / \
        //   2   3
        //  / \
        // 4   5
        TreeNode typical = new TreeNode(1);
        typical.left = new TreeNode(2);
        typical.right = new TreeNode(3);
        typical.left.left = new TreeNode(4);
        typical.left.right = new TreeNode(5);
        print("case 1 typical   ", findLeaves(typical),
                Arrays.asList(Arrays.asList(4, 5, 3), Arrays.asList(2), Arrays.asList(1)));

        print("case 2 single    ", findLeaves(new TreeNode(1)),
                Arrays.asList(Arrays.asList(1)));

        // A chain has one node per round, deepest first.
        TreeNode chain = new TreeNode(1);
        chain.right = new TreeNode(2);
        chain.right.right = new TreeNode(3);
        print("case 3 right chain", findLeaves(chain),
                Arrays.asList(Arrays.asList(3), Arrays.asList(2), Arrays.asList(1)));

        print("case 4 null tree ", findLeaves(null), Collections.emptyList());
    }
}

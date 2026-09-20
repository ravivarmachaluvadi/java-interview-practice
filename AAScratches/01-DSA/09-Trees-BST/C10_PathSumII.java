/*
 * =====================================================================
 *  Path Sum II                        LeetCode 113 | Medium    MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree and an int targetSum, return every ROOT-TO-LEAF path
 *   whose values add up to targetSum. A leaf is a node with no children, so a path must
 *   end at a leaf - stopping halfway does not count. Values may be negative, so you
 *   cannot prune a branch just because the running total already passed the target.
 *
 * EXAMPLE
 *            5
 *          /   \
 *         4     8
 *        /     / \        targetSum = 22  ->  [[5, 4, 11, 2], [5, 8, 4, 5]]
 *      11    13   4
 *     /  \       / \
 *    7    2     5   1
 *   tree = [], target = 0       ->  []          nothing to walk
 *   tree = [0], target = 0      ->  [[0]]       the root is itself a leaf
 *   tree = [1, 2], target = 1   ->  []          1 matches but the root is not a leaf
 *   tree = -2 -> -3, target -5  ->  [[-2, -3]]  negatives are fine
 *
 * APPROACH  (DFS with add - recurse - remove backtracking)
 *   1. Keep one shared list, currentPath, holding the nodes from the root to where we are.
 *   2. On entering a node: currentPath.add(node.val), and subtract node.val from the
 *      target that gets passed to the children - so "remaining" always means
 *      "what the rest of this path still has to supply".
 *   3. If the node is a leaf AND the remaining target equals its value, this path works.
 *      Copy it: new ArrayList<>(currentPath). Without the copy you store a reference to a
 *      list that later calls will mutate, and every answer ends up identical or empty.
 *   4. Otherwise recurse left, then right.
 *   5. On leaving the node, ALWAYS remove the last element. This is the backtracking step.
 *
 * KEY INSIGHT
 *   Add - recurse - remove is the whole technique behind every "return all paths /
 *   combinations / subsets" question. The single mutable buffer is what keeps it O(h)
 *   memory instead of copying a list at every node. Two bugs define this problem and both
 *   are classics: forgetting the remove (paths leak into siblings) and forgetting the copy
 *   (all results alias the same buffer). Say both out loud in the interview.
 *
 * COMPLEXITY
 *   Time  O(n^2) worst case: O(n) nodes visited, and copying a matching path costs O(h),
 *         which is O(n) in a skewed tree where many paths can match
 *   Space O(n)   recursion stack plus currentPath, excluding the output list itself
 *
 * INTERVIEW FOLLOW-UPS
 *   - Path Sum I (LC 112): just return true/false - no buffer, no copy, early exit
 *   - Path Sum III (LC 437): any downward path, not just root-to-leaf - prefix-sum map
 *   - Return paths to ANY node, not just leaves: record at every node, drop the leaf test
 *   - All values positive? Then you may prune when the remaining target goes negative
 *
 * RUN
 *   main() runs 5 cases (typical, empty, single node, non-leaf match, negatives) and
 *   prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class PathSumII {

    public static List<List<Integer>> pathSum(TreeNode root, int targetSum) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> currentPath = new ArrayList<>();
        pathSumHelper(root, targetSum, result, currentPath);
        return result;
    }

    /** targetSum is what the path from THIS node down to a leaf still has to add up to. */
    private static void pathSumHelper(TreeNode node,
                                      int targetSum,
                                      List<List<Integer>> result,
                                      List<Integer> currentPath) {

        if (node == null) return;

        currentPath.add(node.val);

        if (node.left == null && node.right == null && targetSum == node.val) {
            // Copy: currentPath is a shared buffer that later calls will mutate.
            result.add(new ArrayList<>(currentPath));
        } else {
            pathSumHelper(node.left, targetSum - node.val, result, currentPath);
            pathSumHelper(node.right, targetSum - node.val, result, currentPath);
        }

        // Backtrack: undo this node before returning to the parent.
        currentPath.remove(currentPath.size() - 1);
    }

    public static void main(String[] args) {
        // case 1: typical - two matching root-to-leaf paths
        //            5
        //          /   \
        //         4     8
        //        /     / \
        //      11    13   4
        //     /  \       / \
        //    7    2     5   1
        TreeNode typical = new TreeNode(5);
        typical.left = new TreeNode(4);
        typical.right = new TreeNode(8);
        typical.left.left = new TreeNode(11);
        typical.left.left.left = new TreeNode(7);
        typical.left.left.right = new TreeNode(2);
        typical.right.left = new TreeNode(13);
        typical.right.right = new TreeNode(4);
        typical.right.right.left = new TreeNode(5);
        typical.right.right.right = new TreeNode(1);
        print("case 1 typical       ", pathSum(typical, 22), "[[5, 4, 11, 2], [5, 8, 4, 5]]");

        // case 2: empty tree - nothing to walk
        print("case 2 empty tree    ", pathSum(null, 0), "[]");

        // case 3: a lone root IS a leaf, so it can match
        print("case 3 single node   ", pathSum(new TreeNode(0), 0), "[[0]]");

        // case 4: the classic trap - 1 hits the target but the root is not a leaf
        //    1
        //     \
        //      2
        TreeNode nonLeaf = new TreeNode(1);
        nonLeaf.right = new TreeNode(2);
        print("case 4 match mid-path", pathSum(nonLeaf, 1), "[]");

        // case 5: negative values still work; you just cannot prune early
        //   -2
        //     \
        //      -3
        TreeNode negatives = new TreeNode(-2);
        negatives.right = new TreeNode(-3);
        print("case 5 negatives     ", pathSum(negatives, -5), "[[-2, -3]]");
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

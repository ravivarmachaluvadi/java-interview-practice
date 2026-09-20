/*
 * =====================================================================
 *  Branch Sums (all root-to-leaf sums)          AlgoExpert classic | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, return the sum of every root-to-leaf
 *   branch, ordered left to right (the order a normal pre-order walk meets
 *   the leaves). A leaf is a node with no left AND no right child.
 *   The tree may be empty, in which case there are no branches at all.
 *
 * EXAMPLE
 *         1
 *       /   \
 *      2     3          ->  [7, 8, 10, 11]
 *     / \   / \              1+2+4, 1+2+5, 1+3+6, 1+3+7
 *    4   5 6   7
 *
 *   root = null          ->  []      (no branches; main() runs this)
 *   root = 5 with only a left child 3  ->  [8]   (5 is NOT a leaf)
 *
 * APPROACH  (root-to-leaf accumulation, state carried DOWN)
 *   1. Recurse with an extra parameter: the sum of everything above this node.
 *   2. At a null node, return - nothing to add.
 *   3. Compute newRunningSum = runningSum + node.value. This is a fresh local,
 *      never a mutation of the caller's value.
 *   4. If the node is a leaf, record newRunningSum and stop.
 *   5. Otherwise recurse left then right, both with newRunningSum.
 *
 * KEY INSIGHT
 *   The running total travels DOWN as a parameter, not UP as a return value.
 *   Because each call makes its own local copy, backtracking is automatic:
 *   when the left branch returns, the right branch still sees the untouched
 *   parent sum, so there is nothing to undo. Compare with PathSumII, which
 *   must carry a mutable list and therefore MUST remove the last element
 *   after recursing. Same skeleton, and the difference is exactly "immutable
 *   int copy" versus "shared mutable list".
 *
 * COMPLEXITY
 *   Time  O(n)  each node is visited once.
 *   Space O(h)  recursion stack, h = height (O(log n) balanced, O(n) skewed);
 *               plus O(leaves) for the output list.
 *
 * INTERVIEW FOLLOW-UPS
 *   - HasPathSum (LC 112): stop as soon as one branch matches a target.
 *   - PathSumII (LC 113): return the paths themselves, which forces
 *     add-recurse-remove backtracking on a shared list.
 *   - Sum root-to-leaf numbers (LC 129): carry total*10 + value instead.
 *   - Longest/max branch sum: keep a single best instead of a list.
 *
 * RUN
 *   main() runs 3 cases (full tree, empty tree, one-child chain) and prints
 *   actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class BranchSums {

    public static List<Integer> branchSums(TreeNode root) {
        List<Integer> sums = new ArrayList<>();
        collect(root, 0, sums);
        return sums;
    }

    private static void collect(TreeNode node, int runningSum, List<Integer> sums) {
        if (node == null) return;

        // A fresh local, NOT a mutation: the caller's runningSum stays intact,
        // so the sibling branch is unaffected. This is why no undo step exists.
        int newRunningSum = runningSum + node.value;

        // A branch ends only at a real leaf - a node with a single child is not one.
        if (node.left == null && node.right == null) {
            sums.add(newRunningSum);
            return;
        }

        collect(node.left, newRunningSum, sums);
        collect(node.right, newRunningSum, sums);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        //        1
        //      /   \
        //     2     3
        //    / \   / \
        //   4   5 6   7
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(7);

        print("case 1 full tree   ", branchSums(root), "[7, 8, 10, 11]");
        print("case 2 empty tree  ", branchSums(null), "[]");

        // Tricky: 5 has one child, so 5 alone is NOT a branch. Only 5+3 counts.
        TreeNode chain = new TreeNode(5);
        chain.left = new TreeNode(3);
        print("case 3 one-child   ", branchSums(chain), "[8]");
    }
}

class TreeNode {
    int value;
    TreeNode left;
    TreeNode right;

    TreeNode(int value) {
        this.value = value;
    }
}

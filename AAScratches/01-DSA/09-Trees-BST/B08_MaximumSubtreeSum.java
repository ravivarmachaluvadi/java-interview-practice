/*
 * =====================================================================
 *  Maximum Subtree Sum                          Classic (GfG) | Easy
 * =====================================================================
 *
 * PROBLEM
 *   A binary tree has n nodes labelled 0..n-1; node i stores its label, and nums[i]
 *   holds the value attached to node i. Find the subtree (a node plus everything under
 *   it) whose values add up to the largest total. Return {nodeLabel, sum}. Values may
 *   be negative, so the answer is not always the root.
 *
 * EXAMPLE
 *   tree =      0           nums = [10,20,30,40,50,60]   ->  [0, 210]  (whole tree)
 *              / \
 *             1   2         nums = [-1,-2,-3,-4,-5,-6]   ->  [3, -4]   (best single leaf)
 *            / \  /
 *           3  4 5          nums = [-100,20,30,40,50,60] ->  [1, 110]  (root drags it down)
 *
 * APPROACH  (postorder sum with a global max)
 *   1. Postorder DFS: get the sum of the left subtree, then of the right subtree.
 *   2. This subtree's sum = left + right + nums[node.data]. If it beats the best seen so
 *      far, record both the sum and this node's label.
 *   3. Return this subtree's sum to the parent, which needs it for its own total.
 *   4. Ties keep the first subtree found (strict >), i.e. the earliest in postorder.
 *
 * KEY INSIGHT
 *   Two different things happen at each node: what you RETURN upward (this subtree's
 *   sum, which the parent needs) and what you RECORD globally (the best answer so far).
 *   They are not the same number. This is the gentlest version of the pattern that
 *   Diameter (B09) and MaxPathSum (D01) use, so make sure the split feels natural here.
 *
 * COMPLEXITY
 *   Time  O(n)  each node visited once
 *   Space O(h)  recursion stack equals tree height
 *
 * INTERVIEW FOLLOW-UPS
 *   - Same problem on a general tree given as an edge list (the GfG form): DFS from root
 *     over an adjacency list, skip the parent
 *   - Return the subtree with the largest AVERAGE (LC 1120): carry (sum, count) pairs up
 *   - Count subtrees whose sum equals k, or the most frequent subtree sum (LC 508)
 *   - Why not "max over root only"? A negative root makes a child subtree the winner
 *
 * RUN
 *   main() runs 3 cases (all positive, all negative, negative root) and prints actual
 *   vs expected.
 */

import java.util.Arrays;

class TreeNode {
    int data;          // the node's label, used as an index into nums
    TreeNode left, right;

    TreeNode(int x) {
        data = x;
    }
}

class Solution {
    private int maxSum;
    private int maxNode;

    // Returns the node label and sum of the heaviest subtree, as {node, sum}.
    public int[] maxSubtreeSum(TreeNode root, int[] nums) {
        maxSum = Integer.MIN_VALUE;   // reset so the same Solution can be reused
        maxNode = -1;
        subtreeSum(root, nums);
        return new int[]{maxNode, maxSum};
    }

    // Postorder: returns this subtree's total, records the best total seen so far.
    private int subtreeSum(TreeNode node, int[] nums) {
        if (node == null) return 0;

        int leftSum = subtreeSum(node.left, nums);
        int rightSum = subtreeSum(node.right, nums);
        int total = leftSum + rightSum + nums[node.data];

        if (total > maxSum) {   // strict >, so ties keep the first subtree found
            maxSum = total;
            maxNode = node.data;
        }
        return total;           // the parent needs this, regardless of whether it won
    }
}

class MaximumSubtreeSum {

    // Builds the tree used by every case:
    //        0
    //       / \
    //      1   2
    //     / \  /
    //    3  4 5
    private static TreeNode buildTree() {
        TreeNode root = new TreeNode(0);
        root.left = new TreeNode(1);
        root.right = new TreeNode(2);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(4);
        root.right.left = new TreeNode(5);
        return root;
    }

    public static void main(String[] args) {
        Solution solver = new Solution();
        TreeNode root = buildTree();

        // case 1: all positive, so the whole tree wins
        int[] allPositive = {10, 20, 30, 40, 50, 60};
        print("case 1 all positive ", solver.maxSubtreeSum(root, allPositive), new int[]{0, 210});

        // case 2: all negative, so the least-negative single leaf wins (node 3 = -4)
        int[] allNegative = {-1, -2, -3, -4, -5, -6};
        print("case 2 all negative ", solver.maxSubtreeSum(root, allNegative), new int[]{3, -4});

        // case 3: a very negative root; node 1's subtree (20+40+50) beats the root's 100
        int[] negativeRoot = {-100, 20, 30, 40, 50, 60};
        print("case 3 negative root", solver.maxSubtreeSum(root, negativeRoot), new int[]{1, 110});
    }

    private static void print(String label, int[] actual, int[] expected) {
        System.out.println(label + ": " + Arrays.toString(actual)
                + "   expected " + Arrays.toString(expected));
    }
}

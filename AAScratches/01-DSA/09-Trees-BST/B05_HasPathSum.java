/*
 * =====================================================================
 *  Path Sum                                          LeetCode 112 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree and an integer targetSum, return true if some
 *   root-to-leaf path has node values adding up to targetSum. A leaf has no children,
 *   and the path must end at a leaf, not just anywhere. Node values may be negative,
 *   so you cannot stop early because "the sum already went past the target".
 *
 * EXAMPLE
 *   tree = [5,4,8,11,null,13,4,7,2,null,null,null,1], target = 22  ->  true
 *       (5 -> 4 -> 11 -> 2 = 22)
 *   tree = [], target = 0      ->  false  (no leaf, so no path; the classic trap)
 *   tree = [1,2], target = 1   ->  false  (1 is the root, not a leaf; only 1 -> 2 counts)
 *   tree = [-3], target = -3   ->  true   (a lone node is a leaf)
 *
 * APPROACH  (root-to-leaf DFS with a shrinking target)
 *   1. null node: return false. An empty subtree has no path in it.
 *   2. Leaf node: the path ends here, so the answer is root.val == remaining target.
 *   3. Otherwise subtract root.val from the target and ask each child. Return true if
 *      either side finds a path; || short-circuits so the right side is skipped once
 *      the left has succeeded.
 *
 * KEY INSIGHT
 *   Pass the REMAINING target down instead of the running sum, so a leaf needs one
 *   equality check. The leaf test must be left == null AND right == null: treating a
 *   node with one missing child as a leaf, or returning true when the sum matches at an
 *   inner node, are the two mistakes people make under pressure. Same shape as
 *   BranchSums (A05), plus early exit; PathSumII (C11) adds backtracking on top.
 *
 * COMPLEXITY
 *   Time  O(n)  every node may be visited once in the worst case
 *   Space O(h)  recursion depth is the tree height (O(n) for a skewed tree)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Path Sum II (LC 113): return every matching path -> add/recurse/remove backtracking
 *   - Path Sum III (LC 437): path may start and end anywhere -> prefix-sum map along the path
 *   - Iterative version: a stack of (node, remaining) pairs with the same leaf test
 *   - Why not prune when remaining < 0? Negative values further down can bring it back
 *
 * RUN
 *   main() runs 4 cases (typical, empty tree, root-is-not-a-leaf, single negative node)
 *   and prints actual vs expected.
 */

class HasPathSum {

    public boolean hasPathSum(TreeNode root, int targetSum) {
        if (root == null) return false;

        boolean isLeaf = root.left == null && root.right == null;
        if (isLeaf) return root.val == targetSum;

        // Shrink the target on the way down; the leaf then only compares one number.
        int remaining = targetSum - root.val;
        return hasPathSum(root.left, remaining) || hasPathSum(root.right, remaining);
    }

    public static void main(String[] args) {
        HasPathSum solver = new HasPathSum();

        // case 1: the LeetCode example, path 5 -> 4 -> 11 -> 2 = 22
        //           5
        //          / \
        //         4   8
        //        /   / \
        //       11  13  4
        //      /  \      \
        //     7    2      1
        TreeNode example = new TreeNode(5);
        example.left = new TreeNode(4);
        example.right = new TreeNode(8);
        example.left.left = new TreeNode(11);
        example.left.left.left = new TreeNode(7);
        example.left.left.right = new TreeNode(2);
        example.right.left = new TreeNode(13);
        example.right.right = new TreeNode(4);
        example.right.right.right = new TreeNode(1);
        print("case 1 typical, target 22 ", solver.hasPathSum(example, 22), true);

        // case 2: empty tree has no leaf, so even target 0 is false
        print("case 2 empty tree, target 0", solver.hasPathSum(null, 0), false);

        // case 3: root value equals the target but the root is not a leaf
        TreeNode twoNodes = new TreeNode(1);
        twoNodes.left = new TreeNode(2);
        print("case 3 [1,2], target 1     ", solver.hasPathSum(twoNodes, 1), false);

        // case 4: single negative node is itself the whole path
        print("case 4 [-3], target -3     ", solver.hasPathSum(new TreeNode(-3), -3), true);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

class TreeNode {
    TreeNode left, right;
    int val;

    TreeNode(int val) {
        this.val = val;
    }
}

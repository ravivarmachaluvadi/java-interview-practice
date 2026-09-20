/*
 * =====================================================================
 *  Sum of Left Leaves                                LeetCode 404 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, return the sum of all left leaves. A left leaf is
 *   a node with no children that is the LEFT child of its parent. The root is never a
 *   left leaf, even when it is the only node in the tree.
 *
 * EXAMPLE
 *   tree = [3,9,20,null,null,15,7]           ->  24   (left leaves are 9 and 15)
 *   tree = [1]                               ->  0    (root is nobody's left child)
 *   tree = 1 -> 2 -> 3, every link to the left ->  3    (2 has a child, so only 3 counts)
 *   tree = [1,null,2]  (right child only)    ->  0
 *
 * APPROACH  (leaf test performed by the parent)
 *   1. null: contribute 0.
 *   2. Look at root.left. If it exists and has no children of its own, it is a left
 *      leaf: add its value.
 *   3. Recurse into both children and add whatever left leaves they find further down.
 *      Recursing into a left child that is a leaf is harmless: it returns 0.
 *
 * KEY INSIGHT
 *   "Left leaf" is a fact a node cannot know about itself: it sees its own children,
 *   not which side of its parent it hangs from. So the PARENT runs the test. The other
 *   way to do it is to carry an isLeft flag down (dfs(node, isLeft)) and add node.val
 *   when isLeft && leaf; this file shows both. Either way, context that a child cannot
 *   compute has to come from above, a shape you meet again in CountGoodNodes and Cousins.
 *
 * COMPLEXITY
 *   Time  O(n)  each node visited once
 *   Space O(h)  recursion stack equals tree height
 *
 * INTERVIEW FOLLOW-UPS
 *   - Sum of right leaves, or of all leaves: change or drop the side condition
 *   - Iterative version: BFS queue, apply the same parent-side check on each dequeued node
 *   - Bottom-left value (LC 513): related, but wants the deepest leftmost node, so use BFS
 *
 * RUN
 *   main() runs 4 cases (typical, single node, left chain, right-only) through both
 *   methods and prints actual vs expected.
 */

class SumOfLeftLeaves {

    // Approach 1: the parent inspects its left child.
    public int sumOfLeftLeaves(TreeNode root) {
        if (root == null) return 0;

        int sum = 0;
        if (isLeaf(root.left)) {
            sum += root.left.val;
        }
        sum += sumOfLeftLeaves(root.left);
        sum += sumOfLeftLeaves(root.right);
        return sum;
    }

    // Approach 2: carry an "am I a left child" flag down; the leaf decides for itself.
    public int sumOfLeftLeavesWithFlag(TreeNode root) {
        return dfs(root, false);   // the root is not a left child of anything
    }

    private int dfs(TreeNode node, boolean isLeftChild) {
        if (node == null) return 0;
        if (isLeaf(node)) return isLeftChild ? node.val : 0;
        return dfs(node.left, true) + dfs(node.right, false);
    }

    private static boolean isLeaf(TreeNode node) {
        return node != null && node.left == null && node.right == null;
    }

    public static void main(String[] args) {
        SumOfLeftLeaves solver = new SumOfLeftLeaves();

        // case 1: typical
        //        3
        //       / \
        //      9  20
        //         / \
        //        15  7
        TreeNode typical = new TreeNode(3);
        typical.left = new TreeNode(9);
        typical.right = new TreeNode(20);
        typical.right.left = new TreeNode(15);
        typical.right.right = new TreeNode(7);
        runBoth(solver, "case 1 typical    ", typical, 24);

        // case 2: a single node is a leaf but not a left child
        runBoth(solver, "case 2 single node", new TreeNode(1), 0);

        // case 3: left chain 1 -> 2 -> 3; only 3 is a leaf
        TreeNode chain = new TreeNode(1);
        chain.left = new TreeNode(2);
        chain.left.left = new TreeNode(3);
        runBoth(solver, "case 3 left chain ", chain, 3);

        // case 4: only a right child, which is a leaf but on the wrong side
        TreeNode rightOnly = new TreeNode(1);
        rightOnly.right = new TreeNode(2);
        runBoth(solver, "case 4 right only ", rightOnly, 0);
    }

    private static void runBoth(SumOfLeftLeaves solver, String label, TreeNode root, int expected) {
        System.out.println(label + " parent-check: " + solver.sumOfLeftLeaves(root)
                + "   flag-down: " + solver.sumOfLeftLeavesWithFlag(root)
                + "   expected " + expected);
    }
}

class TreeNode {
    TreeNode left, right;
    int val;

    TreeNode(int val) {
        this.val = val;
    }
}

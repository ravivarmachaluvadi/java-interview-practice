/*
 * =====================================================================
 *  Binary Tree Maximum Path Sum           LeetCode 124 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   A path is any sequence of nodes where each consecutive pair is connected by an edge, and
 *   no node appears twice. The path does not have to touch the root. Return the largest sum
 *   of node values along any such path. There is always at least one node, and values can be
 *   negative (-1000 <= node.val <= 1000), so the answer can be negative too.
 *
 * EXAMPLE
 *        -10                              -5
 *        /  \                            /  \
 *       9    20      ->  42             4    -3   ->  17
 *           /  \        (15+20+7)      / \        (6+4+7, the root is skipped)
 *          15   7                     6   7
 *   tree = [-3]           ->  -3    single node, the whole answer is that node
 *   tree = [-10,-9,-20]   ->  -9    every path is negative, take the least bad one
 *
 * APPROACH  (return the best arm, track the best bend)
 *   1. Postorder DFS. For each node compute the best downward path starting at each child.
 *   2. Clamp each child's result at 0: max(childResult, 0). A negative arm is never worth
 *      attaching, and 0 means "attach nothing", which is always allowed.
 *   3. The best path that BENDS at this node is node.val + leftArm + rightArm. Compare that
 *      with the running maximum and keep the larger. This is the only place the answer moves.
 *   4. RETURN node.val + max(leftArm, rightArm): a straight path going down through one child,
 *      which is the only shape the parent can extend. A bent path has no free end to attach to.
 *   5. The answer is the running maximum, not what the root returned.
 *
 * KEY INSIGHT
 *   What you return up is not what you are answering: you return one arm so the parent can build
 *   its own path, and you record two arms joined in a side field. This is the Diameter
 *   (B09_DiameterOfBinaryTree) skeleton with two extra moves - clamp negative arms to zero, and add
 *   node values instead of counting edges. Seeing that equivalence turns LC 124 from a hard problem
 *   into a variation you already own. Seeding the maximum with Integer.MIN_VALUE, not 0, is what
 *   keeps the all-negative tree correct.
 *
 * COMPLEXITY
 *   Time  O(n)  each node is visited exactly once
 *   Space O(h)  recursion stack, h = tree height, O(n) for a skewed tree
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the actual path, not the sum: remember the node where the best bend happened
 *   - Restrict the path to root-to-leaf, or to downward-only: drop one arm from the combine
 *   - Same question on an N-ary tree: keep the two largest clamped child results
 *   - Why clamp at 0 instead of skipping negatives with an if? Same thing, fewer branches
 *
 * RUN
 *   main() runs 4 cases (typical, single negative node, all-negative tree, best path that
 *   skips the root) and prints actual vs expected.
 */

class BinaryTreeMaxPathSum {
    // A field, so the recursion can return one thing and record another.
    // An int[1] passed as a parameter would work identically and stay thread-safe.
    private int maxSum;

    public int maxPathSum(TreeNode root) {
        maxSum = Integer.MIN_VALUE;   // reset, so one solver can answer several trees
        bestDownwardPath(root);
        return maxSum;
    }

    // Returns the best path that STARTS at this node and only goes down.
    // Records the best bent path seen anywhere as a side effect.
    private int bestDownwardPath(TreeNode node) {
        if (node == null) return 0;

        // A negative arm is never worth attaching; 0 means "take nothing from this side".
        int leftArm = Math.max(bestDownwardPath(node.left), 0);
        int rightArm = Math.max(bestDownwardPath(node.right), 0);

        // The path that bends here uses both arms. It cannot be extended upward.
        int bendHere = node.val + leftArm + rightArm;
        maxSum = Math.max(maxSum, bendHere);

        // The parent can only extend a straight path, so give it a single arm.
        return node.val + Math.max(leftArm, rightArm);
    }

    public static void main(String[] args) {
        BinaryTreeMaxPathSum solver = new BinaryTreeMaxPathSum();

        // case 1: typical, best path is 15 -> 20 -> 7 and never reaches the root
        //        -10
        //        /  \
        //       9    20
        //            / \
        //           15  7
        TreeNode typical = new TreeNode(-10);
        typical.left = new TreeNode(9);
        typical.right = new TreeNode(20);
        typical.right.left = new TreeNode(15);
        typical.right.right = new TreeNode(7);
        print("case 1 typical       ", solver.maxPathSum(typical), 42);

        // case 2: edge, a single negative node: the answer must be that node, not 0
        print("case 2 single node   ", solver.maxPathSum(new TreeNode(-3)), -3);

        // case 3: edge, every value negative: the best path is the single least bad node
        TreeNode allNegative = new TreeNode(-10);
        allNegative.left = new TreeNode(-9);
        allNegative.right = new TreeNode(-20);
        print("case 3 all negative  ", solver.maxPathSum(allNegative), -9);

        // case 4: tricky, the best path bends at 4 and skips the root entirely (6 + 4 + 7)
        //        -5
        //       /  \
        //      4    -3
        //     / \
        //    6   7
        TreeNode bendsLow = new TreeNode(-5);
        bendsLow.left = new TreeNode(4);
        bendsLow.right = new TreeNode(-3);
        bendsLow.left.left = new TreeNode(6);
        bendsLow.left.right = new TreeNode(7);
        print("case 4 bends off root", solver.maxPathSum(bendsLow), 17);
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

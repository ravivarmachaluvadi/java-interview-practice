/*
 * =====================================================================
 *  Count Good Nodes in Binary Tree            LeetCode 1448 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, a node X is "good" if no node on the path from the
 *   root down to X has a value STRICTLY greater than X. The root is always good.
 *   Return how many good nodes the tree contains. An empty tree has 0.
 *
 * EXAMPLE
 *        3
 *       / \
 *      1   4        ->  4   good = 3(root), 3(under 1), 4, 5
 *     / \   \           1 fails (3 above it), 2 fails (3 above it)
 *    3   2   5
 *   tree = []   ->  0          tree = [7]  ->  1
 *   tree = [2,2,2]  ->  3      ties count as good, the test is ">=" not ">"
 *   tree = [5,4,null,3]  ->  1  a strictly decreasing chain: only the root is good
 *
 * APPROACH  (carry the max-so-far downward)
 *   1. Walk the tree with DFS, passing one extra argument: maxOnPath, the largest value
 *      seen from the root down to (but not including) the current node.
 *   2. At each node, it is good when node.val >= maxOnPath. Ties count, so use >=.
 *   3. Widen the window for the children: maxOnPath = max(maxOnPath, node.val).
 *   4. Return 1-or-0 for this node plus the counts from both subtrees.
 *   5. Start with maxOnPath = Integer.MIN_VALUE so the root is always good.
 *
 * KEY INSIGHT
 *   This is the mirror image of the Diameter pattern. In Diameter the information flows
 *   UP as a return value; here it flows DOWN as a parameter. Whenever the question is
 *   "relative to everything above me on this path", the answer is an extra argument, not
 *   a global. Each branch gets its own copy, so recursion does the backtracking for free
 *   - no need to undo anything when the call returns.
 *
 * COMPLEXITY
 *   Time  O(n)  every node is visited exactly once, O(1) work per node
 *   Space O(h)  recursion stack only; h is the height, O(n) for a skewed tree
 *
 * INTERVIEW FOLLOW-UPS
 *   - Max Difference Between Node and Ancestor (LC 1026): carry min AND max down
 *   - Return the good nodes themselves, not the count: append node.val instead of ++
 *   - Count nodes strictly greater than every ancestor: change >= to >, root still counts
 *   - Do it iteratively: push (node, maxOnPath) pairs on an explicit stack
 *
 * RUN
 *   main() runs 5 cases (typical, empty, single node, all equal, decreasing chain) and
 *   prints actual vs expected.
 */
class CountGoodNodes {

    public int goodNodes(TreeNode root) {
        // MIN_VALUE makes the root pass the >= test, so the root is always good.
        return countGoodNodes(root, Integer.MIN_VALUE);
    }

    /** maxOnPath = largest value on the root-to-parent path above this node. */
    private int countGoodNodes(TreeNode node, int maxOnPath) {
        if (node == null) return 0;

        // ">=" not ">": a node equal to the best ancestor is still good.
        int good = node.val >= maxOnPath ? 1 : 0;

        // Each child gets its own widened copy, so branches never pollute each other.
        int maxForChildren = Math.max(maxOnPath, node.val);

        return good
                + countGoodNodes(node.left, maxForChildren)
                + countGoodNodes(node.right, maxForChildren);
    }

    public static void main(String[] args) {
        CountGoodNodes solver = new CountGoodNodes();

        // case 1: typical - good nodes are 3(root), 3, 4, 5
        //        3
        //       / \
        //      1   4
        //     / \   \
        //    3   2   5
        TreeNode typical = new TreeNode(3);
        typical.left = new TreeNode(1);
        typical.right = new TreeNode(4);
        typical.left.left = new TreeNode(3);
        typical.left.right = new TreeNode(2);
        typical.right.right = new TreeNode(5);
        print("case 1 typical         ", solver.goodNodes(typical), 4);

        // case 2 and 3: the two smallest trees there are
        print("case 2 empty tree      ", solver.goodNodes(null), 0);
        print("case 3 single node     ", solver.goodNodes(new TreeNode(7)), 1);

        // case 4: all values equal - ties are good, so every node counts
        //      2
        //     / \
        //    2   2
        TreeNode allEqual = new TreeNode(2);
        allEqual.left = new TreeNode(2);
        allEqual.right = new TreeNode(2);
        print("case 4 all equal       ", solver.goodNodes(allEqual), 3);

        // case 5: strictly decreasing chain - nothing below the root can ever win
        //    5
        //   /
        //  4
        //   \
        //    3
        TreeNode decreasing = new TreeNode(5);
        decreasing.left = new TreeNode(4);
        decreasing.left.right = new TreeNode(3);
        print("case 5 decreasing chain", solver.goodNodes(decreasing), 1);
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

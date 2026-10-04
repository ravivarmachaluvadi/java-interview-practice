/*
 * =====================================================================
 *  P062 DP on Trees (State per Node)   Canonical LC 337 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 337, House Robber III)
 *   Houses form a binary tree. Robbing two directly linked houses (parent and child) sets
 *   off the alarm. Return the most money you can rob.
 *
 * EXAMPLE
 *   [3,2,3,null,3,null,1]  ->  7     3 + 3 + 1
 *   [3,4,5,1,3,null,1]     ->  9     4 + 5
 *
 * RECOGNIZE WHEN
 *   - A choice per node (take / skip, colour, place a camera) with a rule that links a node
 *     to its parent or children, and you optimise over the whole tree.
 *   - The 1D version is a known DP (house robber, max independent set) but on a tree.
 *   Not this if: there is no per-node choice, just an aggregate -> P053_BottomUpDfs.
 *
 * TEMPLATE
 *   dfs(node) returns an array: best value for each STATE of this node
 *       e.g. {best if node is taken, best if node is skipped}
 *   combine children's arrays according to the rule:
 *       taken   = node.val + left.skipped + right.skipped
 *       skipped = max(left) + max(right)
 *   answer = max over the root's states
 *
 * APPROACH
 *   1. For each node compute two numbers: rob it, or do not.
 *   2. If you rob it, its children must be skipped; if not, each child may do either.
 *   3. Post-order, so children are solved first.
 *
 * KEY INSIGHT
 *   Returning ALL states of a subtree (not just its best) lets the parent apply its rule
 *   without re-visiting anything. This is the tree version of P082_LinearTakeSkip, and it
 *   avoids the exponential "grandchildren" recursion.
 *
 * COMPLEXITY
 *   Time O(n), space O(h).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 968  Binary Tree Cameras      greedy states: 0 = needs cover, 1 = has camera,
 *                                            2 = covered; place a camera when a child needs one
 *   [coded] LC 979  Distribute Coins         return the excess (coins - 1) of each subtree;
 *                                            moves += |excess| on every edge
 *           LC 1372 Longest ZigZag Path      states: arriving from left / from right
 *           Max independent set on a tree    exactly LC 337 with weights 1
 *           LC 2246 Longest Path, Diff Chars bottom-up best two children with a condition
 *
 * PITFALLS
 *   - The naive "rob node + grandchildren vs children" recursion is exponential without
 *     memo; the two-state return is O(n).
 *   - LC 968: if the root still "needs cover" at the end, add one camera.
 *   - LC 979: count moves on edges with |excess|, not the excess itself.
 *
 * DEEP DIVE
 *   B03_HouseRobber (12-Dynamic-Programming)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.Deque;

class TreeDp {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val) {
            this.val = val;
        }

        // Builds a tree from LeetCode's level-order list (null = missing child).
        static TreeNode of(Integer... v) {
            if (v.length == 0 || v[0] == null) {
                return null;
            }
            TreeNode root = new TreeNode(v[0]);
            Deque<TreeNode> queue = new ArrayDeque<>();
            queue.add(root);
            int i = 1;
            while (i < v.length) {
                TreeNode n = queue.poll();
                if (v[i] != null) {
                    n.left = new TreeNode(v[i]);
                    queue.add(n.left);
                }
                i++;
                if (i < v.length && v[i] != null) {
                    n.right = new TreeNode(v[i]);
                    queue.add(n.right);
                }
                i++;
            }
            return root;
        }
    }

    // Canonical LC 337.
    static int rob(TreeNode root) {
        int[] r = robStates(root);
        return Math.max(r[0], r[1]);
    }

    // {best if this node is robbed, best if it is not}
    private static int[] robStates(TreeNode node) {
        if (node == null) {
            return new int[]{0, 0};
        }
        int[] l = robStates(node.left);
        int[] r = robStates(node.right);
        int taken = node.val + l[1] + r[1];
        int skipped = Math.max(l[0], l[1]) + Math.max(r[0], r[1]);
        return new int[]{taken, skipped};
    }

    private static int cameras;

    // LC 968: fewest cameras so every node is watched (a camera covers parent and children).
    static int minCameraCover(TreeNode root) {
        cameras = 0;
        if (cover(root) == 0) {
            cameras++;                             // the root itself is still unwatched
        }
        return cameras;
    }

    // 0 = not covered, 1 = has a camera, 2 = covered without a camera.
    private static int cover(TreeNode node) {
        if (node == null) {
            return 2;                              // nothing to watch
        }
        int l = cover(node.left);
        int r = cover(node.right);
        if (l == 0 || r == 0) {
            cameras++;
            return 1;
        }
        return (l == 1 || r == 1) ? 2 : 0;
    }

    private static int moves;

    // LC 979: n nodes, n coins in total; one move shifts a coin along one edge.
    static int distributeCoins(TreeNode root) {
        moves = 0;
        excess(root);
        return moves;
    }

    private static int excess(TreeNode node) {
        if (node == null) {
            return 0;
        }
        int l = excess(node.left);
        int r = excess(node.right);
        moves += Math.abs(l) + Math.abs(r);        // coins crossing the two child edges
        return node.val + l + r - 1;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 337 [3,2,3,null,3,null,1]", rob(TreeNode.of(3, 2, 3, null, 3, null, 1)), 7);
        check("LC 337 [3,4,5,1,3,null,1]", rob(TreeNode.of(3, 4, 5, 1, 3, null, 1)), 9);
        check("LC 337 skip two levels", rob(TreeNode.of(4, 1, null, 2, null, 3)), 7);

        check("LC 968 [0,0,null,0,0]", minCameraCover(TreeNode.of(0, 0, null, 0, 0)), 1);
        check("LC 968 chain of five",
                minCameraCover(TreeNode.of(0, 0, null, 0, null, 0, null, null, 0)), 2);
        check("LC 968 single node", minCameraCover(TreeNode.of(0)), 1);

        check("LC 979 [3,0,0]", distributeCoins(TreeNode.of(3, 0, 0)), 2);
        check("LC 979 [0,3,0]", distributeCoins(TreeNode.of(0, 3, 0)), 3);
    }
}

/*
 * =====================================================================
 *  P053 Tree DFS Bottom-Up (Return Info from Children)   Canonical LC 543 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 543, Diameter of Binary Tree)
 *   Return the number of EDGES on the longest path between any two nodes. The path does
 *   not have to pass through the root.
 *
 * EXAMPLE
 *   [1,2,3,4,5]  ->  3      4 -> 2 -> 1 -> 3 (or 5 -> 2 -> 1 -> 3)
 *   [1,2]        ->  1
 *
 * RECOGNIZE WHEN
 *   - The answer at a node is built from answers of its subtrees: height, size, balance,
 *     sums, "best path going down from here".
 *   - The global answer may "bend" at some node (diameter, max path sum): combine the two
 *     child results there, but return only ONE side to the parent.
 *   Not this if: information flows from the root down (path so far, max so far) ->
 *   P054_TopDownDfsPath; you need per-level answers -> P052_BfsByLevel.
 *
 * TEMPLATE
 *   best = initial
 *   dfs(node):                               // returns what the PARENT needs
 *       if node is null: return base
 *       L = dfs(node.left); R = dfs(node.right)
 *       best = combine(best, L, R, node)     // the answer that bends here
 *       return extend(max(L, R), node)       // a path to the parent can use one side only
 *
 * APPROACH
 *   1. dfs returns the height (in nodes) of the subtree.
 *   2. The longest path that bends at a node has L + R edges; keep the maximum.
 *   3. Return 1 + max(L, R) so the parent can extend the longer side.
 *
 * KEY INSIGHT
 *   Separate "what I report upward" from "what I record as a candidate answer". A parent
 *   can only continue ONE downward branch, but the best path may use both branches at the
 *   node where it bends; a shared variable collects that while the return value stays
 *   single-branch.
 *
 * COMPLEXITY
 *   Time O(n), space O(h) recursion.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 104  Maximum Depth            return 1 + max(L, R); nothing to record
 *   [coded] LC 110  Balanced Binary Tree     return -1 as a "not balanced" sentinel
 *   [coded] LC 124  Max Path Sum             return node + max(0, L, R); record
 *                                            node + max(0, L) + max(0, R)
 *           LC 687  Longest Univalue Path    extend a side only if the child has the same value
 *           LC 1372 Longest ZigZag Path      return (best going left, best going right)
 *           LC 563  Binary Tree Tilt         return subtree sum, record |L - R|
 *           LC 1120 Max Average Subtree      return (sum, count)
 *           LC 865  Subtree of Deepest Nodes return (depth, node)
 *
 * PITFALLS
 *   - Diameter is in EDGES: L + R with heights counted in nodes.
 *   - LC 124: clamp negative child gains to 0 (do not extend into a loss), but start best
 *     at the smallest value, since all nodes may be negative.
 *   - Do not call height() inside another recursion (O(n^2)); return it once.
 *
 * DEEP DIVE
 *   B09_DiameterOfBinaryTree, A02_HeightOfBinaryTree, B10_IsBalancedBinaryTree,
 *   D01_BinaryTreeMaxPathSum (09-Trees-BST)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.Deque;

class BottomUpDfs {

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

    private static int bestDiameter;

    // Canonical LC 543.
    static int diameterOfBinaryTree(TreeNode root) {
        bestDiameter = 0;
        height(root);
        return bestDiameter;
    }

    private static int height(TreeNode node) {
        if (node == null) {
            return 0;
        }
        int l = height(node.left);
        int r = height(node.right);
        bestDiameter = Math.max(bestDiameter, l + r);   // the path bending here
        return 1 + Math.max(l, r);                      // one side for the parent
    }

    // LC 104.
    static int maxDepth(TreeNode node) {
        return node == null ? 0 : 1 + Math.max(maxDepth(node.left), maxDepth(node.right));
    }

    // LC 110: -1 means "some subtree is unbalanced".
    static boolean isBalanced(TreeNode root) {
        return balancedHeight(root) != -1;
    }

    private static int balancedHeight(TreeNode node) {
        if (node == null) {
            return 0;
        }
        int l = balancedHeight(node.left);
        int r = balancedHeight(node.right);
        if (l == -1 || r == -1 || Math.abs(l - r) > 1) {
            return -1;
        }
        return 1 + Math.max(l, r);
    }

    private static int bestPath;

    // LC 124.
    static int maxPathSum(TreeNode root) {
        bestPath = Integer.MIN_VALUE;
        gain(root);
        return bestPath;
    }

    private static int gain(TreeNode node) {
        if (node == null) {
            return 0;
        }
        int l = Math.max(0, gain(node.left));          // drop a branch that only loses
        int r = Math.max(0, gain(node.right));
        bestPath = Math.max(bestPath, node.val + l + r);
        return node.val + Math.max(l, r);
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 543 [1,2,3,4,5]", diameterOfBinaryTree(TreeNode.of(1, 2, 3, 4, 5)), 3);
        check("LC 543 [1,2]", diameterOfBinaryTree(TreeNode.of(1, 2)), 1);
        TreeNode bent = TreeNode.of(1, 2, null, 3, 4, 5, null, null, 6, 7, null, null, 8);
        check("LC 543 not through the root",
                diameterOfBinaryTree(bent),
                6);

        check("LC 104 three levels", maxDepth(TreeNode.of(3, 9, 20, null, null, 15, 7)), 3);
        check("LC 104 [1,null,2]", maxDepth(TreeNode.of(1, null, 2)), 2);

        check("LC 110 balanced", isBalanced(TreeNode.of(3, 9, 20, null, null, 15, 7)), true);
        check("LC 110 unbalanced", isBalanced(TreeNode.of(1, 2, 2, 3, 3, null, null, 4, 4)), false);
        check("LC 110 empty", isBalanced(TreeNode.of()), true);

        check("LC 124 [1,2,3]", maxPathSum(TreeNode.of(1, 2, 3)), 6);
        check("LC 124 [-10,9,20,null,null,15,7]",
                maxPathSum(TreeNode.of(-10, 9, 20, null, null, 15, 7)), 42);
        check("LC 124 single negative", maxPathSum(TreeNode.of(-3)), -3);
    }
}

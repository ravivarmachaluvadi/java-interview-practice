/*
 * =====================================================================
 *  Lowest Common Ancestor of a Binary Tree IV         LeetCode 1676 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree and an array of distinct nodes that all exist in the
 *   tree, return the lowest common ancestor of ALL of them. Node values are unique. The
 *   array has at least one node; if it has exactly one, that node is its own LCA.
 *
 * EXAMPLE
 *   tree = [3,5,1,6,2,0,8,null,null,7,4], nodes = [4, 7]         ->  2
 *   tree = [3,5,1,6,2,0,8,null,null,7,4], nodes = [7, 6, 2, 4]   ->  5
 *   tree = [3,5,1,6,2,0,8,null,null,7,4], nodes = [1]            ->  1
 *   tree = [3,5,1,6,2,0,8,null,null,7,4], nodes = [3, 8]         ->  3   (root is a target)
 *
 * APPROACH  (LC236 recursion with a set membership test)
 *   1. Put every target value into a HashSet so "is this node a target" is O(1).
 *   2. dfs(node): null -> null. If node is a target, return it immediately (anything below
 *      it is irrelevant: the LCA can be no lower than this node).
 *   3. Otherwise recurse left and right. If both sides return non-null, targets live on both
 *      sides and this node is the LCA. Else pass up whichever side is non-null.
 *   4. The value returned at the root is the answer.
 *
 * KEY INSIGHT
 *   The LC236 recursion never actually used "two": it asks "does my left subtree contain a
 *   target? does my right?" and the first node where both answer yes is the LCA. Replacing
 *   "node == p || node == q" with "set.contains(node)" is the entire change. Early return at
 *   a target is safe here for the same reason as in LC236: every target is guaranteed to be in
 *   the tree, so a target found above others is automatically their ancestor.
 *
 * COMPLEXITY
 *   Time  O(n + k)  build the set from k nodes, then visit each tree node at most once
 *   Space O(h + k)  recursion depth plus the set
 *
 * INTERVIEW FOLLOW-UPS
 *   - What if some targets might be missing (LC1644)? Then the early return is unsafe:
 *     visit everything and count how many targets were actually seen.
 *   - Why match on value rather than reference? Values are unique here; matching on the
 *     reference works too and avoids relying on that constraint.
 *   - Deepest leaves LCA (LC1123) is the same recursion where the "targets" are all nodes
 *     at maximum depth.
 *
 * RUN
 *   main() runs 4 cases (typical, larger set, single node, root among targets) and prints
 *   actual vs expected.
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

class LowestCommonAncestorIV {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    public static class Solution {
        private Set<Integer> targetValues;

        public TreeNode lowestCommonAncestor(TreeNode root, TreeNode[] nodes) {
            targetValues = new HashSet<>();
            for (TreeNode n : nodes) {
                targetValues.add(n.val);
            }
            return dfs(root);
        }

        private TreeNode dfs(TreeNode node) {
            if (node == null) return null;
            // A target can never have its LCA below itself, so stop here.
            if (targetValues.contains(node.val)) return node;

            TreeNode left = dfs(node.left);
            TreeNode right = dfs(node.right);

            if (left != null && right != null) return node; // targets on both sides: LCA found
            return left != null ? left : right;              // pass the single hit upward
        }
    }

    // ---- test scaffolding --------------------------------------------------------------

    /** Builds a tree from a LeetCode-style level-order array with nulls. */
    static TreeNode buildTree(Integer[] arr) {
        if (arr.length == 0 || arr[0] == null) return null;
        TreeNode root = new TreeNode(arr[0]);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int i = 1;
        while (i < arr.length && !queue.isEmpty()) {
            TreeNode curr = queue.poll();
            if (i < arr.length && arr[i] != null) {
                curr.left = new TreeNode(arr[i]);
                queue.offer(curr.left);
            }
            i++;
            if (i < arr.length && arr[i] != null) {
                curr.right = new TreeNode(arr[i]);
                queue.offer(curr.right);
            }
            i++;
        }
        return root;
    }

    private static void fillMap(TreeNode node, Map<Integer, TreeNode> map) {
        if (node == null) return;
        map.put(node.val, node);
        fillMap(node.left, map);
        fillMap(node.right, map);
    }

    private static TreeNode[] pick(Map<Integer, TreeNode> byValue, int... values) {
        TreeNode[] nodes = new TreeNode[values.length];
        for (int i = 0; i < values.length; i++) nodes[i] = byValue.get(values[i]);
        return nodes;
    }

    private static void print(String label, TreeNode actual, String expected) {
        String shown = actual == null ? "null" : String.valueOf(actual.val);
        System.out.println(label + ": " + shown + "   expected " + expected);
    }

    public static void main(String[] args) {
        //         3
        //        / \
        //       5   1
        //      / \ / \
        //     6  2 0  8
        //       / \
        //      7   4
        TreeNode root = buildTree(new Integer[]{3, 5, 1, 6, 2, 0, 8, null, null, 7, 4});
        Map<Integer, TreeNode> byValue = new HashMap<>();
        fillMap(root, byValue);

        Solution sol = new Solution();
        print("case 1 [4, 7]", sol.lowestCommonAncestor(root, pick(byValue, 4, 7)), "2");
        print("case 2 [7, 6, 2, 4]",
                sol.lowestCommonAncestor(root, pick(byValue, 7, 6, 2, 4)), "5");
        print("case 3 [1]", sol.lowestCommonAncestor(root, pick(byValue, 1)), "1");
        print("case 4 [3, 8]", sol.lowestCommonAncestor(root, pick(byValue, 3, 8)), "3");
    }
}

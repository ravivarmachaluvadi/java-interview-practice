/*
 * =====================================================================
 *  P054 Tree DFS Top-Down (Carry State to the Children)   Canonical LC 113 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 113, Path Sum II)
 *   Return every ROOT-TO-LEAF path whose node values sum to targetSum.
 *
 * EXAMPLE
 *   [5,4,8,11,null,13,4,7,2,null,null,5,1], target 22  ->  [[5,4,11,2],[5,8,4,5]]
 *   [1,2,3], target 5                                  ->  []
 *
 * RECOGNIZE WHEN
 *   - The decision at a node depends on its ANCESTORS: remaining sum, max so far, the
 *     number formed so far, the path itself.
 *   - "root-to-leaf", "path from the root", "good node (no bigger ancestor)".
 *   - Downward paths that may start anywhere: combine with a prefix-sum map.
 *   Not this if: the node needs its children's results -> P053_BottomUpDfs.
 *
 * TEMPLATE
 *   dfs(node, state):
 *       if node is null: return
 *       state' = update(state, node)              // e.g. remaining - node.val, path.add
 *       if node is a leaf: check state'
 *       dfs(node.left, state'); dfs(node.right, state')
 *       undo the update if state is a shared mutable list / map (backtrack)
 *
 * APPROACH
 *   1. Walk down with the remaining sum and the current path.
 *   2. At a leaf with remaining == 0, copy the path into the answer.
 *   3. Remove the node from the path on the way back up.
 *
 * KEY INSIGHT
 *   Pass the ancestors' information DOWN as parameters. Immutable values (ints) undo
 *   themselves when the call returns; shared mutable state (the path list, a prefix-sum
 *   map) must be undone explicitly, which is the same backtracking step as P076_Subsets.
 *
 * COMPLEXITY
 *   Time O(n) visits (O(n * h) with path copying), space O(h).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 112  Path Sum                 boolean; no path list needed
 *   [coded] LC 1448 Count Good Nodes         carry the max on the path so far
 *   [coded] LC 129  Sum Root to Leaf Numbers carry number * 10 + digit
 *   [coded] LC 437  Path Sum III             paths may start anywhere: prefix-sum map along
 *                                            the root path (P002_PrefixSumHashMap on a tree)
 *           LC 1026 Max Diff Node / Ancestor carry the min and max on the path
 *           LC 257  Binary Tree Paths        carry the path string
 *           LC 988  Smallest String from Leaf carry the reversed string, compare at leaves
 *
 * PITFALLS
 *   - "Leaf" means no children; a node with one child is not an end point.
 *   - Copy the path when you record it (new ArrayList<>(path)), or later edits change it.
 *   - LC 437: remove the current prefix from the map after both children return.
 *
 * DEEP DIVE
 *   C10_PathSumII, B05_HasPathSum, C08_CountGoodNodes,
 *   C09_MaximumDifferenceBetweenNodeAndAncestor (09-Trees-BST)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TopDownDfsPath {

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

    // Canonical LC 113.
    static List<List<Integer>> pathSum(TreeNode root, int target) {
        List<List<Integer>> out = new ArrayList<>();
        collect(root, target, new ArrayList<>(), out);
        return out;
    }

    private static void collect(TreeNode node, int remaining, List<Integer> path,
                                List<List<Integer>> out) {
        if (node == null) {
            return;
        }
        path.add(node.val);
        remaining -= node.val;
        if (node.left == null && node.right == null && remaining == 0) {
            out.add(new ArrayList<>(path));
        }
        collect(node.left, remaining, path, out);
        collect(node.right, remaining, path, out);
        path.remove(path.size() - 1);              // backtrack
    }

    // LC 112.
    static boolean hasPathSum(TreeNode node, int target) {
        if (node == null) {
            return false;
        }
        if (node.left == null && node.right == null) {
            return target == node.val;
        }
        int rest = target - node.val;
        return hasPathSum(node.left, rest) || hasPathSum(node.right, rest);
    }

    // LC 1448: a node is good if no node on its root path is bigger.
    static int goodNodes(TreeNode root) {
        return countGood(root, Integer.MIN_VALUE);
    }

    private static int countGood(TreeNode node, int maxSoFar) {
        if (node == null) {
            return 0;
        }
        int good = node.val >= maxSoFar ? 1 : 0;
        int max = Math.max(maxSoFar, node.val);
        return good + countGood(node.left, max) + countGood(node.right, max);
    }

    // LC 129.
    static int sumNumbers(TreeNode root) {
        return sumFrom(root, 0);
    }

    private static int sumFrom(TreeNode node, int number) {
        if (node == null) {
            return 0;
        }
        number = number * 10 + node.val;
        if (node.left == null && node.right == null) {
            return number;
        }
        return sumFrom(node.left, number) + sumFrom(node.right, number);
    }

    // LC 437: downward paths starting anywhere; prefix sums along the current root path.
    static int pathSumIII(TreeNode root, int target) {
        Map<Long, Integer> prefixCount = new HashMap<>();
        prefixCount.put(0L, 1);
        return countPaths(root, 0L, target, prefixCount);
    }

    private static int countPaths(TreeNode node, long sum, int target,
                                  Map<Long, Integer> prefixCount) {
        if (node == null) {
            return 0;
        }
        sum += node.val;
        int found = prefixCount.getOrDefault(sum - target, 0);
        prefixCount.merge(sum, 1, Integer::sum);
        found += countPaths(node.left, sum, target, prefixCount);
        found += countPaths(node.right, sum, target, prefixCount);
        prefixCount.merge(sum, -1, Integer::sum);  // leave this branch
        return found;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        Integer[] t = {5, 4, 8, 11, null, 13, 4, 7, 2, null, null, 5, 1};
        check("LC 113 target 22", pathSum(TreeNode.of(t), 22), "[[5, 4, 11, 2], [5, 8, 4, 5]]");
        check("LC 113 none", pathSum(TreeNode.of(1, 2, 3), 5), "[]");
        check("LC 113 not a leaf", pathSum(TreeNode.of(1, 2), 1), "[]");

        check("LC 112 target 22", hasPathSum(TreeNode.of(t), 22), true);
        check("LC 112 [1,2,3] target 5", hasPathSum(TreeNode.of(1, 2, 3), 5), false);
        check("LC 112 empty tree", hasPathSum(TreeNode.of(), 0), false);

        check("LC 1448 [3,1,4,3,null,1,5]", goodNodes(TreeNode.of(3, 1, 4, 3, null, 1, 5)), 4);
        check("LC 1448 [3,3,null,4,2]", goodNodes(TreeNode.of(3, 3, null, 4, 2)), 3);

        check("LC 129 [1,2,3]", sumNumbers(TreeNode.of(1, 2, 3)), 25);
        check("LC 129 [4,9,0,5,1]", sumNumbers(TreeNode.of(4, 9, 0, 5, 1)), 1026);

        check("LC 437 target 8",
                pathSumIII(TreeNode.of(10, 5, -3, 3, 2, null, 11, 3, -2, null, 1), 8), 3);
        check("LC 437 target 22", pathSumIII(TreeNode.of(t), 22), 3);
    }
}

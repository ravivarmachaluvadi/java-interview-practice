/*
 * =====================================================================
 *  P061 Tree as an Undirected Graph (Parent Map + BFS)   Canonical LC 863 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 863, All Nodes Distance K in Binary Tree)
 *   Return the values of all nodes exactly k edges away from the target node, in any
 *   order. Paths may go UP through parents as well as down.
 *
 * EXAMPLE
 *   [3,5,1,6,2,0,8,null,null,7,4], target 5, k 2  ->  [7,4,1]
 *   [1], target 1, k 3                            ->  []
 *
 * RECOGNIZE WHEN
 *   - Distances or spreading that start at an ARBITRARY node (not the root): "distance k",
 *     "infection / fire spreading", "closest leaf to a node".
 *   - A path may need to go up through a parent.
 *   Not this if: everything starts at the root -> plain BFS / DFS (P052_BfsByLevel); you need
 *   the LCA or a path between two nodes -> P058_LowestCommonAncestor.
 *
 * TEMPLATE
 *   parent = {}                                  // one DFS: child -> parent
 *   BFS from start over neighbours {left, right, parent}, with a visited set
 *   level by level until distance k (or until the queue empties for "time to spread")
 *
 * APPROACH
 *   1. Record every node's parent, turning the tree into an undirected graph.
 *   2. BFS from the target over children and parent, marking visited nodes.
 *   3. The nodes in the queue after k levels are the answer.
 *
 * KEY INSIGHT
 *   A tree is a graph whose edges you can only follow downward. Adding parent links makes
 *   every edge two-way, and then any "distance from node X" question is a standard BFS.
 *   The visited set matters now, because the parent edge leads back.
 *
 * COMPLEXITY
 *   Time O(n), space O(n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 2385 Time to Infect the Tree  BFS from start; answer = number of levels - 1
 *           LC 742  Closest Leaf to Target   BFS from the target until the first leaf
 *           LC 2096 Directions Between Nodes LCA path instead -> P058_LowestCommonAncestor
 *           Burn a tree (GfG)                identical to LC 2385
 *           One-pass alternative             DFS returning the distance to the target
 *                                            (no parent map), harder to get right
 *
 * PITFALLS
 *   - Without a visited set, BFS walks child -> parent -> child forever.
 *   - k == 0 returns the target itself.
 *   - Store the parent of the ROOT as nothing (null), not as itself.
 *
 * DEEP DIVE
 *   C01_AllNodesDistanceKInBinaryTree (11-Graphs)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class TreeAsGraph {

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

        // The node holding value v anywhere in the tree, or null.
        static TreeNode find(TreeNode root, int v) {
            if (root == null || root.val == v) {
                return root;
            }
            TreeNode left = find(root.left, v);
            return left != null ? left : find(root.right, v);
        }
    }

    private static void mapParents(TreeNode node, TreeNode parent,
                                   Map<TreeNode, TreeNode> parents) {
        if (node == null) {
            return;
        }
        parents.put(node, parent);
        mapParents(node.left, node, parents);
        mapParents(node.right, node, parents);
    }

    // BFS over {left, right, parent}; returns the levels (lists of nodes) from start.
    private static List<List<TreeNode>> levelsFrom(TreeNode root, TreeNode start) {
        Map<TreeNode, TreeNode> parents = new HashMap<>();
        mapParents(root, null, parents);
        List<List<TreeNode>> levels = new ArrayList<>();
        Set<TreeNode> seen = new HashSet<>();
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(start);
        seen.add(start);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<TreeNode> level = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode n = queue.poll();
                level.add(n);
                for (TreeNode next : new TreeNode[]{n.left, n.right, parents.get(n)}) {
                    if (next != null && seen.add(next)) {
                        queue.add(next);
                    }
                }
            }
            levels.add(level);
        }
        return levels;
    }

    // Canonical LC 863.
    static List<Integer> distanceK(TreeNode root, TreeNode target, int k) {
        List<List<TreeNode>> levels = levelsFrom(root, target);
        List<Integer> out = new ArrayList<>();
        if (k < levels.size()) {
            for (TreeNode n : levels.get(k)) {
                out.add(n.val);
            }
        }
        Collections.sort(out);                     // any order is accepted; sorted to print
        return out;
    }

    // LC 2385: minutes until the infection from `start` reaches every node.
    static int amountOfTime(TreeNode root, int start) {
        return levelsFrom(root, TreeNode.find(root, start)).size() - 1;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        TreeNode t = TreeNode.of(3, 5, 1, 6, 2, 0, 8, null, null, 7, 4);
        check("LC 863 target 5 k=2", distanceK(t, TreeNode.find(t, 5), 2), "[1, 4, 7]");
        check("LC 863 k=0 is the target", distanceK(t, TreeNode.find(t, 5), 0), "[5]");
        TreeNode one = TreeNode.of(1);
        check("LC 863 k larger than the tree", distanceK(one, one, 3), "[]");
        check("LC 863 up and back down", distanceK(t, TreeNode.find(t, 6), 3), "[1, 4, 7]");

        check("LC 2385 start 3", amountOfTime(TreeNode.of(1, 5, 3, null, 4, 10, 6, 9, 2), 3), 4);
        check("LC 2385 single node", amountOfTime(TreeNode.of(1), 1), 0);
    }
}

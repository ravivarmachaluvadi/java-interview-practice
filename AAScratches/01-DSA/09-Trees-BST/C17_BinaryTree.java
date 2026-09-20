/*
 * =====================================================================
 *  All Nodes Distance K in Binary Tree        LeetCode 863 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, a target node inside it, and an integer k, return the
 *   values of every node that is exactly k edges away from the target. Distance counts edges
 *   in any direction - down into the subtree, or up through the parent and back down.
 *   k can be 0 (the answer is then the target itself). Any output order is accepted.
 *
 * EXAMPLE
 *            1
 *          /   \
 *         2     3
 *        / \   / \
 *       4   5 6   7
 *      /
 *     8
 *   target = 5, k = 2  ->  [1, 4]   5 -> 2 is one edge, then 2 -> 1 and 2 -> 4
 *   target = 5, k = 0  ->  [5]      zero edges away is the target itself
 *   target = 5, k = 3  ->  [3, 8]   up to 1 then down to 3, and up to 2 then down 4 -> 8
 *
 * APPROACH  (parent map, then BFS from the target)
 *   1. DFS the tree once and store child -> parent in a HashMap. A tree node can already reach
 *      its two children; the parent link is the third edge that makes the tree undirected.
 *   2. BFS outward from the target. Each node has up to three neighbours: left, right, parent.
 *   3. Keep a visited set so the search never walks back into where it came from.
 *   4. Expand exactly k levels. Whatever is still sitting in the queue is the answer.
 *
 * KEY INSIGHT
 *   A binary tree is a graph the moment you add parent edges, and "distance k" on a graph is
 *   plain BFS. The level-count loop stopping at k is the same one used for level-order traversal;
 *   only the neighbour list changed. Recognise this whenever a tree question asks about
 *   distance, spreading, or anything that has to travel upward as well as downward.
 *
 * COMPLEXITY
 *   Time  O(n)  one DFS to build the parent map, one BFS that visits each node at most once
 *   Space O(n)  parent map + visited set + queue; the queue alone can hold a whole level
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it without the parent map: a DFS that returns the depth of the target and, on the way
 *     back up, collects nodes at (k - depth) in the sibling subtree.
 *   - LeetCode 2385, "amount of time for a binary tree to be infected": the same BFS, but you
 *     report the number of levels instead of stopping at k.
 *   - What if two nodes hold the same value? The target is given as a node reference, not a
 *     value, so identity comparison is what matters - findTargetNode() is only test scaffolding.
 *   - Same recipe for "all nodes WITHIN distance k": collect every level, not just the last.
 *
 * RUN
 *   main() runs 6 cases: typical, k = 0, a case that must travel up then down, a k larger than
 *   the tree, the root as target, and a one-node tree. Each prints actual vs expected. Results
 *   are sorted before printing so expectations read cleanly; BFS order itself is radial.
 */

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}

class BinaryTree {

    public static List<Integer> distanceK(TreeNode root, TreeNode target, int k) {
        List<Integer> result = new ArrayList<>();
        if (root == null || target == null || k < 0) return result;

        Map<TreeNode, TreeNode> parentMap = new HashMap<>();
        recordParents(root, parentMap);

        Queue<TreeNode> queue = new ArrayDeque<>();
        Set<TreeNode> visited = new HashSet<>();
        queue.offer(target);
        visited.add(target);

        // Expand the frontier k times; each pass moves every node in the queue one edge outward.
        for (int level = 0; level < k && !queue.isEmpty(); level++) {
            int frontierSize = queue.size();
            for (int i = 0; i < frontierSize; i++) {
                TreeNode node = queue.poll();
                visitNeighbour(node.left, queue, visited);
                visitNeighbour(node.right, queue, visited);
                visitNeighbour(parentMap.get(node), queue, visited); // null when node is the root
            }
        }

        // Whatever survived k expansions is exactly k edges from the target.
        while (!queue.isEmpty()) {
            result.add(queue.poll().val);
        }
        return result;
    }

    /** Enqueue a neighbour the first time we reach it - visited is what stops us going backwards. */
    private static void visitNeighbour(TreeNode neighbour, Queue<TreeNode> queue, Set<TreeNode> visited) {
        if (neighbour != null && visited.add(neighbour)) {
            queue.offer(neighbour);
        }
    }

    /** DFS that gives every node a link back to its parent, turning the tree into a graph. */
    private static void recordParents(TreeNode node, Map<TreeNode, TreeNode> parentMap) {
        if (node == null) return;
        if (node.left != null) {
            parentMap.put(node.left, node);
            recordParents(node.left, parentMap);
        }
        if (node.right != null) {
            parentMap.put(node.right, node);
            recordParents(node.right, parentMap);
        }
    }

    // ---------- test scaffolding ----------

    private static TreeNode buildSampleTree() {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(7);
        root.left.left.left = new TreeNode(8);
        return root;
    }

    /** Finds a node by value so main() can hand distanceK a real node reference. */
    private static TreeNode findTargetNode(TreeNode node, int value) {
        if (node == null) return null;
        if (node.val == value) return node;
        TreeNode found = findTargetNode(node.left, value);
        return found != null ? found : findTargetNode(node.right, value);
    }

    public static void main(String[] args) {
        TreeNode root = buildSampleTree();
        TreeNode nodeFive = findTargetNode(root, 5);

        print("case 1 k=2 from 5   ", distanceK(root, nodeFive, 2), "[1, 4]");
        print("case 2 k=0 from 5   ", distanceK(root, nodeFive, 0), "[5]");
        print("case 3 k=3 from 5   ", distanceK(root, nodeFive, 3), "[3, 8]");
        print("case 4 k=9 too far  ", distanceK(root, nodeFive, 9), "[]");
        print("case 5 k=1 from root", distanceK(root, root, 1), "[2, 3]");

        TreeNode single = new TreeNode(42);
        print("case 6 single node  ", distanceK(single, single, 0), "[42]");
    }

    private static void print(String label, List<Integer> actual, String expected) {
        List<Integer> sorted = new ArrayList<>(actual);
        Collections.sort(sorted); // BFS order is radial; sort only so expectations read cleanly
        System.out.println(label + ": " + sorted + "   expected " + expected);
    }
}

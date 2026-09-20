/*
 * =====================================================================
 *  All Nodes Distance K in Binary Tree   LeetCode 863 | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given the root of a binary tree, a reference to one node in it (target) and
 *   an integer k, return the values of every node whose distance from target is
 *   exactly k. Distance is the number of edges on the path, and the path may run
 *   upward through the parent as well as downward through the children.
 *   Node values are unique; the answer may be returned in any order.
 *
 * EXAMPLE
 *   tree = [3,5,1,6,2,0,8,null,null,7,4], target = 5, k = 2  ->  [7, 4, 1]
 *   same tree, target = 5, k = 0                             ->  [5]
 *   same tree, target = 5, k = 10                            ->  []   (edge case)
 *
 * APPROACH  (parent map turns the tree into an undirected graph)
 *   1. One BFS over the whole tree fills parentTrack: child -> its parent.
 *      The root is the only node missing from that map.
 *   2. Now every node has up to three neighbours: left, right and parent. That is
 *      an ordinary undirected graph, so distance k is just "k BFS levels away".
 *   3. BFS out from target, level by level, with a visited set so the walk never
 *      goes back the way it came (this is what makes the parent edge safe).
 *   4. Stop the moment currLevel reaches k. Whatever is still sitting in the
 *      queue at that point is exactly the set of nodes at distance k.
 *
 * KEY INSIGHT
 *   A tree only stores downward edges, which is why "distance k" looks hard: the
 *   answer can be above the target. Adding back-edges (the parent map) converts
 *   the tree into a graph and reduces the problem to plain BFS. Recognise the
 *   move whenever a tree question needs to travel upward - burning trees, minimum
 *   time to spread, closest leaf - the fix is always "give every node a parent
 *   pointer, then BFS".
 *
 * COMPLEXITY
 *   Time  O(n)  one BFS to build the parent map, one BFS bounded by n nodes
 *   Space O(n)  parent map, visited set and queue each hold up to n entries
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return nodes at distance AT MOST k: collect at every level instead of only
 *     the last one.
 *   - Amount of Time for Binary Tree to Be Infected (LC 2385): same parent map,
 *     but count the levels until the queue empties.
 *   - Target given as a value instead of a node reference: search for it first.
 *   - Can it be done without the map? Yes, a recursive version returns the depth
 *     of target from each node and collects on the other side, but it is far
 *     easier to get wrong under interview pressure.
 *
 * RUN
 *   main() runs 4 cases (typical, k = 0, target at the root, k past the tree
 *   height) and prints actual vs expected, both sorted so order does not matter.
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}

class AllNodesDistanceKInBinaryTree {

    /** One BFS that records, for every node except the root, who its parent is. */
    void markParents(TreeNode root, HashMap<TreeNode, TreeNode> parentTrack) {
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            TreeNode parent = queue.poll();
            if (parent.left != null) {
                parentTrack.put(parent.left, parent);
                queue.add(parent.left);
            }
            if (parent.right != null) {
                parentTrack.put(parent.right, parent);
                queue.add(parent.right);
            }
        }
    }

    List<Integer> distanceK(TreeNode root, TreeNode target, int k) {
        HashMap<TreeNode, TreeNode> parentTrack = new HashMap<>();
        markParents(root, parentTrack);

        Set<TreeNode> visited = new HashSet<>();
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(target);
        visited.add(target); // never walk back onto the target itself
        int currLevel = 0;

        while (!queue.isEmpty()) {
            if (currLevel == k) break; // the queue now holds exactly distance k
            int size = queue.size();
            currLevel++;
            for (int i = 0; i < size; i++) {
                TreeNode current = queue.poll();

                if (current.left != null && visited.add(current.left)) {
                    queue.add(current.left);
                }
                if (current.right != null && visited.add(current.right)) {
                    queue.add(current.right);
                }
                // the back-edge: the root has no parent, so this lookup can be null
                TreeNode parent = parentTrack.get(current);
                if (parent != null && visited.add(parent)) {
                    queue.add(parent);
                }
            }
        }

        // If the loop ended because the queue ran dry, k is past the far end of
        // the tree and this list is empty, which is the correct answer.
        List<Integer> result = new ArrayList<>();
        for (TreeNode node : queue) {
            result.add(node.val);
        }
        return result;
    }

    /** Builds the LeetCode sample tree [3,5,1,6,2,0,8,null,null,7,4]. */
    private static TreeNode sampleTree() {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(5);
        root.right = new TreeNode(1);
        root.left.left = new TreeNode(6);
        root.left.right = new TreeNode(2);
        root.right.left = new TreeNode(0);
        root.right.right = new TreeNode(8);
        root.left.right.left = new TreeNode(7);
        root.left.right.right = new TreeNode(4);
        return root;
    }

    /** Level-order dump so the case output can be checked against the picture. */
    private static String levelOrder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            out.add(node.val);
            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }
        return out.toString();
    }

    private static void check(String label, TreeNode root, TreeNode target, int k,
                              List<Integer> expected) {
        AllNodesDistanceKInBinaryTree sol = new AllNodesDistanceKInBinaryTree();
        List<Integer> actual = new ArrayList<>(sol.distanceK(root, target, k));
        Collections.sort(actual);
        List<Integer> wanted = new ArrayList<>(expected);
        Collections.sort(wanted);
        System.out.println(label + " target=" + target.val + " k=" + k
                + " -> " + actual + "   expected " + wanted);
    }

    public static void main(String[] args) {
        TreeNode root = sampleTree();
        System.out.println("tree (level order): " + levelOrder(root));

        // typical: from node 5, two steps reaches 7 and 4 below, and 1 via the root
        check("case 1 typical   ", root, root.left, 2, Arrays.asList(7, 4, 1));

        // edge case: distance 0 is the target itself
        check("case 2 k = 0     ", root, root.left, 0, Arrays.asList(5));

        // tricky: target is the root, so the walk is purely downward
        check("case 3 root      ", root, root, 3, Arrays.asList(7, 4));

        // edge case: k larger than anything in the tree -> empty answer
        check("case 4 k too big ", root, root.left, 10, Collections.emptyList());
    }
}

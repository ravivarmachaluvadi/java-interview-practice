/**
 * Problem: Given a binary tree, find all nodes that are exactly K edges away from a specified target node.
 *
 * Approach:
 * 1. Perform a DFS to map each child to its parent for upward traversal.
 * 2. Use BFS starting at the target node, exploring left, right, and parent neighbors while tracking visited nodes.
 * 3. Stop when the desired distance K is reached; collect remaining nodes in the queue as results.
 *
 * Time Complexity: O(N) – each node is processed once during DFS and at most once during BFS.
 * Space Complexity: O(N) – for the parent map, visited set, and BFS queue (worst‑case all nodes).
 */
import java.util.*;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) {
        this.val = val;
        this.left = null;
        this.right = null;
    }
}

class BinaryTree {

    public static TreeNode buildTree() {
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

    public static List<Integer> distanceK(TreeNode root, TreeNode target, int K) {
        Map<TreeNode, TreeNode> parentMap = new HashMap<>();
        findParents(root, parentMap);
        List<Integer> result = new ArrayList<>();
        Set<TreeNode> visited = new HashSet<>();
        bfs(target, parentMap, visited, K, result);
        return result;
    }

    // DFS to find all parents of each node
    private static void findParents(TreeNode root, Map<TreeNode, TreeNode> parentMap) {
        if (root == null) return;
        if (root.left != null) {
            parentMap.put(root.left, root);
            findParents(root.left, parentMap);
        }
        if (root.right != null) {
            parentMap.put(root.right, root);
            findParents(root.right, parentMap);
        }
    }

    // BFS to find all nodes at distance K from the target node
    private static void bfs(TreeNode target, Map<TreeNode, TreeNode> parentMap, Set<TreeNode> visited,
                            int K, List<Integer> result) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(target);
        visited.add(target);
        int level = 0;

        while (!queue.isEmpty() && level < K) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                if (node.left != null && !visited.contains(node.left)) {
                    // remember adding to visited
                    visited.add(node.left);
                    queue.offer(node.left);
                }
                if (node.right != null && !visited.contains(node.right)) {
                    visited.add(node.right);
                    queue.offer(node.right);
                }
                if (parentMap.containsKey(node) && !visited.contains(parentMap.get(node))) {
                    visited.add(parentMap.get(node));
                    queue.offer(parentMap.get(node));
                }
            }
            level++;
        }

        while (!queue.isEmpty()) {
            result.add(queue.poll().val);
        }
    }

    // Method to find the target node
    public static TreeNode findTarget(TreeNode root, int targetVal) {
        if (root == null) return null;
        if (root.val == targetVal) return root;

        TreeNode left = findTarget(root.left, targetVal);
        if (left != null) return left;

        return findTarget(root.right, targetVal);
    }

    // Main method to test the code
    public static void main(String[] args) {
        TreeNode root = buildTree();
        int targetValue = 5;
        int K = 2;

        TreeNode targetNode = findTarget(root, targetValue);
        if (targetNode != null) {
            List<Integer> result = distanceK(root, targetNode, K);
            System.out.println("Nodes at distance " + K + " from node " + targetValue + ": " + result);
        } else {
            System.out.println("Target node not found.");
        }
    }
}

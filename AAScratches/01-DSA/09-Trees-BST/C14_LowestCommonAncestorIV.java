// https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree-iv/description/
// 1676. Lowest Common Ancestor of a Binary Tree IV
class LowestCommonAncestorIV {
    // Definition for a binary tree node.
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) {
            val = x;
        }
    }

    public static class Solution {
        private java.util.Set<Integer> targetValues;

        public TreeNode lowestCommonAncestor(TreeNode root, TreeNode[] nodes) {
            targetValues = new java.util.HashSet<>();
            for (TreeNode n : nodes) {
                targetValues.add(n.val);
            }
            return dfs(root);
        }

        private TreeNode dfs(TreeNode node) {
            if (node == null) {
                return null;
            }
            if (targetValues.contains(node.val)) {
                return node;
            }
            TreeNode left = dfs(node.left);
            TreeNode right = dfs(node.right);

            if (left != null && right != null) {
                return node;
            }
            return (left != null) ? left : right;
        }
    }

    // Helper to build a tree from array (for the example; uses level-order with nulls)
    public static TreeNode buildTree(Integer[] arr) {
        if (arr.length == 0 || arr[0] == null) return null;
        TreeNode root = new TreeNode(arr[0]);
        java.util.Queue<TreeNode> queue = new java.util.LinkedList<>();
        queue.offer(root);
        int i = 1;
        while (i < arr.length) {
            TreeNode curr = queue.poll();
            if (curr == null) continue;
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

    public static void main(String[] args) {
        // Example: root = [3,5,1,6,2,0,8,null,null,7,4], nodes = [4,7]
        Integer[] arr = {3, 5, 1, 6, 2, 0, 8, null, null, 7, 4};
        TreeNode root = buildTree(arr);

        // Let's find references to nodes 4 and 7 — we need to actually locate them
        // For simplicity, we can traverse to find.
        java.util.Map<Integer, TreeNode> map = new java.util.HashMap<>();
        fillMap(root, map);
        TreeNode node4 = map.get(4);
        TreeNode node7 = map.get(7);

        TreeNode[] targets = new TreeNode[]{node4, node7};

        Solution sol = new Solution();
        TreeNode lca = sol.lowestCommonAncestor(root, targets);
        System.out.println("LCA of nodes 4 and 7 is: " + (lca != null ? lca.val : "null"));
        // Expected output: 2
    }

    private static void fillMap(TreeNode node, java.util.Map<Integer, TreeNode> map) {
        if (node == null) return;
        map.put(node.val, node);
        fillMap(node.left, map);
        fillMap(node.right, map);
    }
}

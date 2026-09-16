import java.util.*;

// You will be given binary tree with unique values and values to be deleted
// after deleting nodes in the tree cause chaos leave multple sub trees as tree
// return roots of all those sub-trees
class DeleteNodesAndReturnForest {
    class TreeNode {
        int val;
        TreeNode left, right;

        TreeNode(int v) {
            val = v;
        }
    }

    class Solution {
        public List<TreeNode> delNodes(TreeNode root, int[] to_delete) {
            List<TreeNode> forest = new ArrayList<>();
            Set<Integer> toDelete = new HashSet<>();
            for (int num : to_delete) toDelete.add(num);
            dfs(root, true, toDelete, forest);
            return forest;
        }

        private TreeNode dfs(TreeNode node, boolean isRoot, Set<Integer> toDelete, List<TreeNode> forest) {
            if (node == null) return null;

            boolean inDeleted = toDelete.contains(node.val);
            if (isRoot && !inDeleted) {
                forest.add(node);
            }

            node.left = dfs(node.left, inDeleted, toDelete, forest);
            node.right = dfs(node.right, inDeleted, toDelete, forest);
            return inDeleted ? null : node;
        }
    }

    // Helper to print tree in pre-order (for demonstration purposes)
    static void printTree(TreeNode root) {
        if (root == null) return;
        System.out.print(root.val + " ");
        printTree(root.left);
        printTree(root.right);
    }

    public static void main(String[] args) {
        // Build example tree:
        //          1
        //         / \
        //        2   3
        //           / \
        //          4   5
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.right.left = new TreeNode(4);
        root.right.right = new TreeNode(5);

        int[] to_delete = {3};

        Solution sol = new Solution();
        List<TreeNode> forest = sol.delNodes(root, to_delete);

        System.out.println("Resulting forest roots (pre-order):");
        for (TreeNode tree : forest) {
            printTree(tree);
            System.out.println();
        }
    }
}

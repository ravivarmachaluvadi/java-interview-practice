/**
 * Problem: Given a perfect binary tree, reverse the node values at every odd level
 * (levels 1,3,5,... counting root as level 0). The structure of the tree remains unchanged.
 *
 * Approach: Perform a depth‑first traversal pairing symmetric nodes from left and right subtrees.
 * At each even recursion depth (which corresponds to an odd level in the original tree),
 * swap the values of the paired nodes. Recurse on the children with swapped positions
 * to continue the process for deeper levels.
 *
 * Time Complexity: O(n) – every node is visited once.
 * Space Complexity: O(h) – recursion stack depth equals tree height (O(log n) for a perfect binary tree).
 */
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

}

class ReverseOddLevelsOfBinaryTree {

    public TreeNode reverseOddLevels(TreeNode root) {
        traverseDFS(root.left, root.right, 0);
        return root;
    }

    private void traverseDFS(TreeNode leftChild, TreeNode rightChild, int level) {

        if (leftChild == null || rightChild == null) {
            return;
        }
//If the current level is odd (means previous level is even), swap the values of the children.
        if (level % 2 == 0) {
            int temp = leftChild.val;
            leftChild.val = rightChild.val;
            rightChild.val = temp;
        }

        //order of below lines can be reversed
        traverseDFS(leftChild.left, rightChild.right, level + 1);
        traverseDFS(leftChild.right, rightChild.left, level + 1);
    }

    public static void main(String[] args) {
        // Construct the binary tree
        TreeNode root = new TreeNode();
        root.val = 1;
        root.left = new TreeNode();
        root.left.val = 2;
        root.right = new TreeNode();
        root.right.val = 3;
        root.left.left = new TreeNode();
        root.left.left.val = 4;
        root.left.right = new TreeNode();
        root.left.right.val = 5;
        root.right.left = new TreeNode();
        root.right.left.val = 6;
        root.right.right = new TreeNode();
        root.right.right.val = 7;

        ReverseOddLevelsOfBinaryTree solution = new ReverseOddLevelsOfBinaryTree();
        TreeNode modifiedRoot = solution.reverseOddLevels(root);

        // Print the modified tree in level order to verify the result
        printLevelOrder(modifiedRoot);
    }

    private static void printLevelOrder(TreeNode modifiedRoot) {
        if (modifiedRoot == null) {
            return;
        }
        java.util.Queue<TreeNode> queue = new java.util.LinkedList<>();
        queue.add(modifiedRoot);
        while (!queue.isEmpty()) {
            TreeNode current = queue.poll();
            System.out.print(current.val + " ");
            if (current.left != null) {
                queue.add(current.left);
            }
            if (current.right != null) {
                queue.add(current.right);
            }
        }
    }
}
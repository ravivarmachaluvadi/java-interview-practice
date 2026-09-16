/**
 * Problem: Given a binary search tree (BST), find the Nth largest element in the tree.
 *
 * Approach: Perform a reverse inorder traversal (right → node → left) which visits nodes
 * in descending order. Keep a counter of visited nodes; when the counter equals N, record
 * the current node's value as the result and stop further recursion.
 *
 * Time Complexity: O(H + N) where H is the tree height; in worst case O(n) for skewed trees,
 * but typically O(log n) on balanced BSTs. The traversal stops after visiting N nodes.
 *
 * Space Complexity: O(H) due to recursion stack depth, which is at most the height of the tree.
 */
class NthLargestInBST {
    private int count = 0;
    private int result = -1;

    public int findNthLargest(TreeNode root, int N) {
        reverseInorder(root, N);
        return result; // 60
    }

    private void reverseInorder(TreeNode node, int N) {
        if (node == null || count >= N) return;
        reverseInorder(node.right, N);
        count++;
        if (count == N) {
            result = node.val;
            return;
        }
        reverseInorder(node.left, N);
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(50);
        root.left = new TreeNode(30);
        root.right = new TreeNode(70);
        root.left.left = new TreeNode(20);
        root.left.right = new TreeNode(40);
        root.right.left = new TreeNode(60);
        root.right.right = new TreeNode(80);

        NthLargestInBST bst = new NthLargestInBST();
        int N = 3;  // For example, find the 3rd largest element.
        System.out.println("The " + N + "th largest element is: " + bst.findNthLargest(root, N));
    }
}

class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int x) {
        val = x;
        left = right = null;
    }
}

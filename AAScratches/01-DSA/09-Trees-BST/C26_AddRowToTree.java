/**
 * Problem: Given a binary tree, insert a new row of nodes with value `v` at depth `d`.
 * The new nodes become the children of all nodes currently at depth `d-1`,
 * pushing existing subtrees down one level.
 *
 * Approach: Perform a DFS traversal keeping track of the current depth. When
 * reaching depth `d-1`, replace each node's left and right child with a new
 * node containing `v` and attach the original children as its own left or right
 * subtree respectively. Handle the special case when `d == 1` by creating a
 * new root.
 *
 * Time Complexity: O(n) – every node is visited once.
 * Space Complexity: O(h) – recursion stack depth, where h is tree height (worst‑case O(n)).
 */
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

/*

       4
     /   \
    2     6
   / \   /
  3   1 5


       4
     /   \
    2     6
   / \   / \
  1   1 5   1
 /      \
3        1

 */

/**
 *Original Tree:
 * 3 2 1 4 5 6
 * Tree after adding one row:
 * 3 1 2 1 1 4 5 1 6 1
 */
class AddRowToTree {

    // v-> value to add at the level d nothing but depth
    public TreeNode addOneRow(TreeNode root, int v, int d) {
        if (d == 1) {
            TreeNode newRoot = new TreeNode(v);
            newRoot.left = root;
            return newRoot;
        }

        addRowRecursive(root, v, d, 1);
        return root;
    }

    private void addRowRecursive(TreeNode node, int v, int d, int currentLevel) {
        if (node == null) return;
        // When we reach level d-1, insert the new row
        if (currentLevel == d - 1) {
            TreeNode leftChild = node.left;
            TreeNode rightChild = node.right;

            node.left = new TreeNode(v);
            node.left.left = leftChild;

            node.right = new TreeNode(v);
            node.right.right = rightChild;
        }
        // remember recurive calls in else block
        else {
            addRowRecursive(node.left, v, d, currentLevel + 1);
            addRowRecursive(node.right, v, d, currentLevel + 1);
        }
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(6);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(1);
        root.right.left = new TreeNode(5);

        System.out.println("Original Tree:");
        inOrderTraversal(root);

        AddRowToTree solution = new AddRowToTree();
        root = solution.addOneRow(root, 1, 3);

        System.out.println("\nTree after adding one row:");
        inOrderTraversal(root);
    }

    public static void inOrderTraversal(TreeNode root) {
        if (root != null) {
            inOrderTraversal(root.left);
            System.out.print(root.val + " ");
            inOrderTraversal(root.right);
        }
    }
}

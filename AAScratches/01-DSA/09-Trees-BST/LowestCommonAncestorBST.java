/**
 * Finds the Lowest Common Ancestor (LCA) of two nodes in a Binary Search Tree.
 *
 * The algorithm exploits BST properties: if both target values are less than
 * the current node, the LCA lies in the left subtree; if both are greater,
 * it lies in the right subtree; otherwise the current node is the LCA.
 *
 * Iterative and recursive implementations are provided. Both run in O(h)
 * time where h is the tree height (O(log n) for balanced trees). The
 * space complexity is O(1) iteratively and O(h) recursively due to call stack.
 */
class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int val) {
        this.val = val;
    }
}

class LowestCommonAncestorBST {

    public static TreeNode lowestCommonAncestorIterative(TreeNode root, TreeNode p, TreeNode q) {
        while (root != null) {
            if (p.val < root.val && q.val < root.val)
                root = root.left;
            else if (p.val > root.val && q.val > root.val)
                root = root.right;
            else
                return root;
        }
        return null;
    }

    // Function to find LCA in BST
    public static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        // Base condition
        if (root == null) return null;

        // If both p and q are smaller than root, LCA lies in left subtree
        if (p.val < root.val && q.val < root.val) {
            return lowestCommonAncestor(root.left, p, q);
        }

        // If both p and q are greater than root, LCA lies in right subtree
        else if (p.val > root.val && q.val > root.val) {
            return lowestCommonAncestor(root.right, p, q);
        }

        // Else one is on left and other is on right (or one equals root)
        // → this root is the LCA
        else {
            return root;
        }
    }

    public static void main(String[] args) {
        // Construct BST
        /*
                    6
                   / \
                  2   8
                 / \ / \
                0  4 7  9
                  / \
                 3   5
        */

        TreeNode root = new TreeNode(6);
        root.left = new TreeNode(2);
        root.right = new TreeNode(8);
        root.left.left = new TreeNode(0);
        root.left.right = new TreeNode(4);
        root.left.right.left = new TreeNode(3);
        root.left.right.right = new TreeNode(5);
        root.right.left = new TreeNode(7);
        root.right.right = new TreeNode(9);

        TreeNode p = root.left;        // Node with value 2
        TreeNode q = root.left.right;  // Node with value 4

        TreeNode lca = lowestCommonAncestor(root, p, q);
        System.out.println("LCA of " + p.val + " and " + q.val + " = " + lca.val);

    }
}

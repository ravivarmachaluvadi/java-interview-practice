class MinimumHeightBST {
    private TreeNode root;

    // Method to construct the Minimum Height BST from a sorted array
    public TreeNode buildMinimumHeightBST(int[] sortedArray) {
        return buildBST(sortedArray, 0, sortedArray.length - 1);
    }

    // Recursive method to build the BST
    private TreeNode buildBST(int[] sortedArray, int start, int end) {
        if (start > end)
            return null;

        int mid = (start + end) / 2; // Choose the middle element as root
        TreeNode node = new TreeNode(sortedArray[mid]);

        // Recursively build the left and right subtrees
        node.left = buildBST(sortedArray, start, mid - 1);
        node.right = buildBST(sortedArray, mid + 1, end);

        return node;
    }

    // In-order traversal to verify the tree
    public void inOrder(TreeNode node) {
        if (node != null) {
            inOrder(node.left);
            System.out.print(node.value + " ");
            inOrder(node.right);
        }
    }

    public static void main(String[] args) {
        MinimumHeightBST bst = new MinimumHeightBST();
        int[] sortedArray = {1, 2, 3, 4, 5, 6, 7};

        // Build the Minimum Height BST
        TreeNode root = bst.buildMinimumHeightBST(sortedArray);

        // Print the in-order traversal of the tree
        System.out.println("In-order traversal of Minimum Height BST:");
        bst.inOrder(root);
    }
}

class TreeNode {
    int value;
    TreeNode left;
    TreeNode right;

    TreeNode(int value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
}

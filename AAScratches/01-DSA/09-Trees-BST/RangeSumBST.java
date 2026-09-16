class TreeNode {
    int data;
    TreeNode left, right;
    TreeNode(int x) {
        data = x;
    }
}

class RangeSumBST {
    // Method to find the sum of values within a given range in a binary search tree (BST)
    public int rangeSumBST(TreeNode root, int low, int high) {
        // Base case: if the current node is null, return 0
        if (root == null) return 0;

        // Initialize the sum to 0
        int sum = 0;

        // If the current node's value is within the range, add it to the sum
        if (root.data >= low && root.data <= high) {
            sum += root.data;
        }

        // Recursively sum the left subtree if the current node's value is greater than low
        if (root.data > low) {
            sum += rangeSumBST(root.left, low, high);
        }

        // Recursively sum the right subtree if the current node's value is less than high
        if (root.data < high) {
            sum += rangeSumBST(root.right, low, high);
        }
        // Return the computed sum
        return sum; // 32
    }

    public static void main(String[] args) {
        // Example usage:
        TreeNode root = new TreeNode(10);
        root.left = new TreeNode(5);
        root.right = new TreeNode(15);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(7);
        root.right.right = new TreeNode(18);

        RangeSumBST solution = new RangeSumBST();
        int low = 7, high = 15;
        int result = solution.rangeSumBST(root, low, high);
        System.out.println("Sum of values in range [" + low + ", " + high + "] is: " + result);
    }

}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) {
        this.val = val;
    }
}

//         4
//       / \
//      2   6
//     / \  /
//    3   1 5
class BinaryTreeFromString {

    public static TreeNode str2tree(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        return buildTree(s, new int[]{0});
    }

    private static TreeNode buildTree(String s, int[] index) {
        if (index[0] >= s.length()) {
            return null;
        }
        // Read the current number
        int sign = 1;
        if (s.charAt(index[0]) == '-') {
            sign = -1;
            index[0]++;
        }
        int num = 0;
        while (index[0] < s.length() && Character.isDigit(s.charAt(index[0]))) {
            num = num * 10 + (s.charAt(index[0]) - '0');
            index[0]++;
        }
        num *= sign;

        TreeNode node = new TreeNode(num);

        // If there is a left subtree, recursively build it
        if (index[0] < s.length() && s.charAt(index[0]) == '(') {
            index[0]++; // skip '('
            node.left = buildTree(s, index);
            index[0]++; // skip ')'
        }

        // If there is a right subtree, recursively build it
        if (index[0] < s.length() && s.charAt(index[0]) == '(') {
            index[0]++; // skip '('
            node.right = buildTree(s, index);
            index[0]++; // skip ')'
        }

        return node;
    }

    // Helper method to print the tree in Preorder (for testing)
    public static void preorder(TreeNode root) {
        if (root != null) {
            System.out.print(root.val + " ");
            preorder(root.left);
            preorder(root.right);
        }
    }

    public static void main(String[] args) {
        String s = "4(2(3)(1))(6(5))";
        TreeNode root = str2tree(s);

        System.out.println("Preorder traversal of the tree:");
        // 4 2 3 1 6 5
        preorder(root);
    }
}

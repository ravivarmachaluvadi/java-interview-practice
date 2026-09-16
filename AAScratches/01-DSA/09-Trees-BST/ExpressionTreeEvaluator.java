/*
       +
      / \
     3   *
        / \
       2   5

 */
class ExpressionTreeEvaluator {
    public static int evaluateExpressionTree(TreeNode root) {
        if (root == null) return 0;

        // If the node is a leaf, it is an operand, so return its integer value
        if (root.left == null && root.right == null) {
            return Integer.parseInt(root.value);
        }

        // Recursively evaluate the left and right subtrees
        int leftValue = evaluateExpressionTree(root.left);
        int rightValue = evaluateExpressionTree(root.right);

        // Apply the operator
        switch (root.value) {
            case "+":
                return leftValue + rightValue;
            case "-":
                return leftValue - rightValue;
            case "*":
                return leftValue * rightValue;
            case "/":
                return leftValue / rightValue;
            default:
                throw new IllegalArgumentException("Invalid operator: " + root.value);
        }
    }

    public static void main(String[] args) {
        // Example: construct an expression tree for (3 + (2 * 5))
        TreeNode root = new TreeNode("+");
        root.left = new TreeNode("3");
        root.right = new TreeNode("*");
        root.right.left = new TreeNode("2");
        root.right.right = new TreeNode("5");

        int result = evaluateExpressionTree(root);
        System.out.println("Result of expression: " + result); // Output: 13
    }
}

class TreeNode {
    String value;
    TreeNode left;
    TreeNode right;

    TreeNode(String value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
}

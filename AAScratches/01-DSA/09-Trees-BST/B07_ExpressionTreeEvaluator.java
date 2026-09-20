/*
 * =====================================================================
 *  Evaluate Expression Tree            Classic (AlgoExpert) | Easy
 * =====================================================================
 *
 * PROBLEM
 *   A binary expression tree stores operands ("3", "5") in its leaves and operators
 *   ("+", "-", "*", "/") in its inner nodes, each of which has exactly two children.
 *   Return the integer value of the whole expression. Division is integer division
 *   and truncates toward zero, like Java's / on ints.
 *
 * EXAMPLE
 *         +
 *        / \
 *       3   *        ->  3 + (2 * 5) = 13
 *          / \
 *         2   5
 *   single leaf "42"                         ->  42
 *   ((2 - 10) / (1 + 1))                     ->  -8 / 2 = -4   (left before right)
 *
 * APPROACH  (postorder evaluation)
 *   1. null tree: return 0 (a design choice; the problem never gives an empty tree).
 *   2. Leaf: it is an operand, so parse and return its number.
 *   3. Inner node: evaluate the LEFT child, then the RIGHT child, then apply this
 *      node's operator to the two results. Unknown operator: throw.
 *
 * KEY INSIGHT
 *   This is postorder traversal made concrete: children first, parent last, because a
 *   parent's value is a function of its children's values. Every "aggregate a subtree"
 *   problem in this folder (subtree sum, height, diameter, max path sum) is the same
 *   skeleton with a different combine step. Order matters for - and /: left operand is
 *   always the left subtree.
 *
 * COMPLEXITY
 *   Time  O(n)  each node evaluated once
 *   Space O(h)  recursion depth is the tree height
 *
 * INTERVIEW FOLLOW-UPS
 *   - Build the tree from a postfix (RPN) string with a stack, then evaluate (LC 150 idea)
 *   - LC 1628: design it with a Node interface and per-operator classes (Strategy pattern)
 *   - Print the tree as a parenthesised infix string (toInfix below is that helper)
 *   - Handle unary minus, or n-ary operators, or division by zero
 *
 * RUN
 *   main() runs 3 cases (typical, single leaf, subtraction/division order) and prints
 *   actual vs expected.
 */

class ExpressionTreeEvaluator {

    public static int evaluateExpressionTree(TreeNode root) {
        if (root == null) return 0;

        // A leaf is an operand: just parse it.
        if (root.left == null && root.right == null) {
            return Integer.parseInt(root.value);
        }

        // Postorder: both children before this node's operator.
        int leftValue = evaluateExpressionTree(root.left);
        int rightValue = evaluateExpressionTree(root.right);

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

    // Inorder walk that prints the expression with explicit parentheses, for the labels.
    static String toInfix(TreeNode root) {
        if (root == null) return "";
        if (root.left == null && root.right == null) return root.value;
        return "(" + toInfix(root.left) + " " + root.value + " " + toInfix(root.right) + ")";
    }

    // Small builder so main() reads like the expression itself.
    static TreeNode op(String operator, TreeNode left, TreeNode right) {
        TreeNode node = new TreeNode(operator);
        node.left = left;
        node.right = right;
        return node;
    }

    static TreeNode num(int value) {
        return new TreeNode(String.valueOf(value));
    }

    public static void main(String[] args) {
        // case 1: typical, 3 + (2 * 5)
        TreeNode typical = op("+", num(3), op("*", num(2), num(5)));
        print("case 1 " + toInfix(typical), evaluateExpressionTree(typical), 13);

        // case 2: a single operand leaf, no operator at all
        TreeNode single = num(42);
        print("case 2 " + toInfix(single), evaluateExpressionTree(single), 42);

        // case 3: order matters for - and /, and integer division truncates
        TreeNode ordered = op("/", op("-", num(2), num(10)), op("+", num(1), num(1)));
        print("case 3 " + toInfix(ordered), evaluateExpressionTree(ordered), -4);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + " = " + actual + "   expected " + expected);
    }
}

class TreeNode {
    String value;
    TreeNode left;
    TreeNode right;

    TreeNode(String value) {
        this.value = value;
    }
}

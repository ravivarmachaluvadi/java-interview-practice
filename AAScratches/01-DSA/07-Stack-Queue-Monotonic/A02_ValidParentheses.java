import java.util.Stack;

/**
 * Problem: Valid Parentheses (LeetCode 20) - is every opening bracket closed by the
 *          matching type, in the right order?
 *
 * Approaches:
 *  1. isValidWithStack   - all three bracket types "(){}[]". Push openers, pop on a matching
 *                          closer. O(n) time, O(n) space. The general solution.
 *  2. isValidWithCounter - ONLY '(' and ')'. A single int balance counter replaces the stack.
 *                          O(n) time, O(1) space. Classic follow-up: "can you drop the stack
 *                          if there is only one bracket type?"
 *
 * Why the counter cannot handle multiple types: "([)]" ends with balance 0 for every
 * type, yet it is invalid because the ORDER is wrong. Only a stack remembers order.
 */
class ValidParentheses {

    // Parallel strings: OPEN.charAt(i) is the opener that matches CLOSE.charAt(i)
    private static final String OPEN = "([{";
    private static final String CLOSE = ")]}";

    // Approach 1: Stack. Works for any mix of (), [], {}.
    static boolean isValidWithStack(String str) {
        Stack<Character> stack = new Stack<>();
        for (char c : str.toCharArray()) {
            int closeIdx = CLOSE.indexOf(c);
            if (OPEN.indexOf(c) >= 0) {
                stack.push(c);
            } else if (closeIdx >= 0) {
                // Closing bracket: stack must be non-empty and its top must be the matching opener
                if (stack.isEmpty() || stack.pop() != OPEN.charAt(closeIdx)) {
                    return false;
                }
            }
        }
        // Leftover openers mean something was never closed
        return stack.isEmpty();
    }

    // Approach 2: Balance counter. Only valid when the string has a single bracket type.
    static boolean isValidWithCounter(String s) {
        int balance = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
            }
            // Negative balance = a ')' arrived before its '(' -> fail fast.
            // A positive balance mid-way is fine: the matching ')' may still come.
            if (balance < 0) {
                return false;
            }
        }
        // Zero at the end = every '(' found its ')'
        return balance == 0;
    }

    public static void main(String[] args) {
        String[] inputs = {"()()()()", "(()", "())(", "[])", "{[()]}", "([)]"};
        for (String in : inputs) {
            boolean singleType = in.chars().allMatch(ch -> ch == '(' || ch == ')');
            System.out.println("input=" + in
                    + "  stack=" + isValidWithStack(in)
                    + "  counter=" + isValidWithCounter(in)
                    + (singleType ? "" : "  (counter N/A: multi-type input)"));
        }
    }
}

import java.util.*;

// https://leetcode.com/problems/evaluate-reverse-polish-notation/description/
// 150. Evaluate Reverse Polish Notation
class EvaluateReversePolishNotation {

    /**
     * ✨ Improvements:
     * <p>
     * Uses -> arrow syntax (clearer, avoids fall-through).
     * <p>
     * No need for break statements.
     * <p>
     * You can also use yield if you want the switch itself to return a value,
     * but here push-based actions fit better.
     * <p>
     * 👉 case is still required, but you replace : with ->.
     */
    public static int evalRPN(String[] tokens) {
        Stack<Integer> stack = new Stack<>();

        for (String token : tokens) {
            switch (token) {
                case "+" -> stack.push(stack.pop() + stack.pop());
                case "-" -> {
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(a - b);
                }
                case "*" -> stack.push(stack.pop() * stack.pop());
                case "/" -> {
                    int divisor = stack.pop();
                    int dividend = stack.pop();
                    stack.push(dividend / divisor);
                }
                default -> stack.push(Integer.parseInt(token));
            }
        }

        return stack.pop();
    }


    public static void main(String[] args) {
        String[] tokens = {"2", "1", "+", "3", "*"};

        System.out.println("Input tokens: " + Arrays.toString(tokens));

        int result = evalRPN(tokens);
        System.out.println("Result: " + result); // 9
    }
}

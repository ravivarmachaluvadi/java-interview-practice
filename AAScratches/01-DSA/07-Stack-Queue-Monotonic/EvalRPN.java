import java.util.Stack;
/**
 * Input: tokens = ["2","1","+","3","*"]
 * <p>
 * Output: 9
 * <p>
 * Explanation: ((2 + 1) * 3) = 9
 */
// https://leetcode.com/problems/evaluate-reverse-polish-notation/
class EvalRPN {
    public int evalRPN(String[] tokens) {
        Stack<Integer> stack = new Stack<>();
        for (String token : tokens) {
            if (token.equals("+")) {
                int num2 = stack.pop();
                int num1 = stack.pop();
                stack.push(num1 + num2);
            } else if (token.equals("-")) {
                int num2 = stack.pop();
                int num1 = stack.pop();
                stack.push(num1 - num2);
            } else if (token.equals("*")) {
                int num2 = stack.pop();
                int num1 = stack.pop();
                stack.push(num1 * num2);
            } else if (token.equals("/")) {
                int num2 = stack.pop();
                int num1 = stack.pop();
                stack.push(num1 / num2);
            } else {
                stack.push(Integer.parseInt(token));
            }
        }
        return stack.pop();
    }

    public static void main(String[] args) {
        String[] tokens = {"2","1","+","3","*"};
        EvalRPN evaluator = new EvalRPN();
        int result = evaluator.evalRPN(tokens);
        System.out.println("Input: " + java.util.Arrays.toString(tokens));
        System.out.println("Output: " + result);
    }
}

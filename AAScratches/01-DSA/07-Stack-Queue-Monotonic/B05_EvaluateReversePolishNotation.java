/*
 * =====================================================================
 *  Evaluate Reverse Polish Notation                 LeetCode 150 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   tokens is a valid postfix (RPN) expression: integers and the operators
 *   + - * /. Operands come before their operator. Division truncates toward zero
 *   and never divides by zero. Every intermediate value fits in a 32-bit int.
 *   Return the value of the expression.
 *
 * EXAMPLE
 *   ["2","1","+","3","*"]                 ->  9    (2 + 1) * 3
 *   ["4","13","5","/","+"]                ->  6    4 + (13 / 5) = 4 + 2, truncation
 *   ["10","6","9","3","+","-11","*","/","*","17","+","5","+"]  ->  22
 *   ["18"]                                ->  18   single operand, nothing to do
 *
 * APPROACH  (operand stack, arrow-form switch)
 *   1. Walk the tokens left to right with a stack of ints.
 *   2. Number: push it.
 *   3. Operator: pop the RIGHT operand first, then the LEFT, apply, push the result.
 *      For + and * the order does not matter, so pop() op pop() is fine inline.
 *      For - and / it does: b = pop(), a = pop(), push(a - b) and (a / b).
 *   4. When the tokens are exhausted, the single value left on the stack is the answer.
 *
 * KEY INSIGHT
 *   Postfix needs no precedence rules and no parentheses: the stack IS the
 *   evaluation order. The only trap is operand order for the non-commutative
 *   operators. Java's arrow-form switch (case "x" -> ...) has no fall-through and
 *   needs no break, which removes the classic bug of a missing break.
 *
 * COMPLEXITY
 *   Time  O(n)  each token is pushed once and popped at most once
 *   Space O(n)  the operand stack in the worst case (all numbers first)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Basic Calculator (LC 224/227): infix input, so you must also handle precedence
 *   - Convert infix to postfix (shunting-yard) and then evaluate with this function
 *   - What breaks with Integer.parseInt for "-11"? Nothing: the minus is part of the
 *     token, so the default branch handles negative literals correctly
 *
 * RUN
 *   main() runs 4 cases (typical, truncating division, long with negatives, single
 *   operand) and prints actual vs expected.
 */
import java.util.Arrays;
import java.util.Stack;

class EvaluateReversePolishNotation {

    public static int evalRPN(String[] tokens) {
        Stack<Integer> stack = new Stack<>();

        for (String token : tokens) {
            switch (token) {
                case "+" -> stack.push(stack.pop() + stack.pop());
                case "-" -> {
                    int right = stack.pop(); // top of stack is the right operand
                    int left = stack.pop();
                    stack.push(left - right);
                }
                case "*" -> stack.push(stack.pop() * stack.pop());
                case "/" -> {
                    int divisor = stack.pop();
                    int dividend = stack.pop();
                    stack.push(dividend / divisor); // Java int division truncates toward zero
                }
                default -> stack.push(Integer.parseInt(token)); // handles "-11" too
            }
        }
        return stack.pop();
    }

    private static void print(String label, String[] tokens, int expected) {
        System.out.println(label + " " + Arrays.toString(tokens) + " -> " + evalRPN(tokens)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1", new String[]{"2", "1", "+", "3", "*"}, 9);
        print("case 2", new String[]{"4", "13", "5", "/", "+"}, 6);
        String[] longWithNegative =
                {"10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+"};
        print("case 3", longWithNegative, 22);
        print("case 4", new String[]{"18"}, 18);
    }
}

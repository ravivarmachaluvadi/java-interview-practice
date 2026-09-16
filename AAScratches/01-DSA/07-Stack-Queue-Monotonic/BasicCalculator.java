import java.util.*;
// https://leetcode.com/problems/basic-calculator/

/**
 * Example 3:
 * <p>
 * Input: s = "(1+(4+5+2)-3)+(6+8)"<br>
 * Output: 23
 * <p>
 * Constraints:
 * <p>
 * 1 <= s.length <= 3 * 105<br>
 * s consists of digits, '+', '-', '(', ')', and ' '.<br>
 * s represents a valid expression.<br>
 * There will be no two consecutive operators in the input.<br>
 * Every number and running calculation will fit in a signed 32-bit integer.<br>
 */
// stack with if else if solution
class BasicCalculator {

    public static int calculate(String s) {
        // Stack to store intermediate results
        Stack<Integer> stack = new Stack<>();
        int num = 0; // Current number
        int sign = 1; // 1 for positive, -1 for negative
        int result = 0; // Final result

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            if (Character.isDigit(ch)) {
                num = num * 10 + (ch - '0');
            } else if (ch == '+') {
                // applying sign as sign appears before this number constructed
                result += sign * num;
                sign = 1; // Set the sign to positive for next number
                num = 0; // Reset the current number
            } else if (ch == '-') {
                result += sign * num; // Add the previous number to result
                sign = -1;
                num = 0;
            } else if (ch == '(') {
                // Push the current result and sign onto the stack
                stack.push(result);
                stack.push(sign);
                result = 0; // Reset result
                sign = 1; // Reset sign
            } else if (ch == ')') {
                // Add the previous number to result before the parenthesis
                result += sign * num;
                num = 0; // Reset the current number
                result *= stack.pop(); // Multiply by the sign before the parenthesis
                result += stack.pop(); // Add the previous result before the parenthesis
            }
        }

        // Add the last number to the result
        result += sign * num;
        return result;
    }

    public static void main(String[] args) {
        String s1 = "1 + 1";
        String s2 = " 2-1 + 2 ";
        String s3 = "(1+(4+5+2)-3)+(6+8)";
        System.out.println("Result of expression 1: " + calculate(s1)); // Output: 2
        System.out.println("Result of expression 2: " + calculate(s2)); // Output: 3
        System.out.println("Result of expression 3: " + calculate(s3)); // Output: 23
    }
}

/*
 * =====================================================================
 *  Basic Calculator                                LeetCode 224 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Evaluate a string expression made of non-negative integers, '+', '-', '(', ')' and
 *   spaces. No '*' or '/', but parentheses nest to any depth and a '-' may appear right
 *   after '(' (unary minus). Every intermediate value fits in a signed 32-bit int.
 *
 * EXAMPLE
 *   "1 + 1"                  ->  2
 *   " 2-1 + 2 "              ->  3
 *   "(1+(4+5+2)-3)+(6+8)"    ->  23
 *   "-(2+3)"                 ->  -5    unary minus in front of a group
 *   "2-(5-6)"                ->  3     the sign before '(' must flip everything inside
 *   "42"                     ->  42    single number, no operators
 *
 * APPROACH  (sign and result stack parsing)
 *   1. Walk the string once keeping three variables: num (digits being built),
 *      sign (+1/-1 that applies to num) and result (sum so far at this nesting level).
 *   2. Digit: num = num * 10 + digit.
 *   3. '+' or '-': fold the finished number into result (result += sign * num),
 *      then record the new sign and reset num.
 *   4. '(': push result and sign (the enclosing context), then start a fresh
 *      result = 0, sign = +1 for the inner expression.
 *   5. ')': fold the last number, then pop sign and result of the outer level:
 *      result = outerResult + outerSign * innerResult.
 *   6. After the loop fold the trailing number once more.
 *
 * KEY INSIGHT
 *   A '(' does not need recursion: it only needs to remember two things, the outer
 *   running total and the sign that was waiting to be applied. Push both, compute
 *   the inside as if it were a brand-new expression, then apply the saved sign to
 *   the whole inner value on ')'. This is the save/restore pattern from DecodeString
 *   with the operand handling from RPN; the sign stack replaces operator precedence
 *   because '+' and '-' have equal precedence and are left-associative.
 *
 * COMPLEXITY
 *   Time  O(n)  every character is visited once; each push has one matching pop
 *   Space O(n)  the stack grows with nesting depth, worst case all '(' characters
 *
 * INTERVIEW FOLLOW-UPS
 *   - Basic Calculator II (LC 227): add '*' and '/' without parentheses; keep a stack
 *     of signed operands and multiply/divide against the top before pushing.
 *   - Basic Calculator III (LC 772): both operators and parentheses; recurse on '('
 *     or run this stack idea on top of the LC 227 operand stack.
 *   - How would you handle unary minus after '(' ? Already works here: sign=-1 with
 *     num=0 folds to 0, then the real number picks up the -1.
 *   - Why two pushes per '(' instead of an object? Same cost; the pop order (sign
 *     first, then result) is the only thing to get right.
 *
 * RUN
 *   main() runs 6 cases (typical, spaces, nested, unary minus, sign flip,
 *   single number) and prints actual vs expected.
 */
import java.util.Stack;

class BasicCalculator {

    public static int calculate(String s) {
        Stack<Integer> stack = new Stack<>(); // saved (result, sign) pairs of enclosing levels
        int num = 0;                          // number currently being read digit by digit
        int sign = 1;                         // sign that applies to num: +1 or -1
        int result = 0;                       // running total of the current nesting level

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            if (Character.isDigit(ch)) {
                num = num * 10 + (ch - '0');
            } else if (ch == '+') {
                // the sign was recorded before this number was built, so apply it now
                result += sign * num;
                sign = 1;
                num = 0;
            } else if (ch == '-') {
                result += sign * num;
                sign = -1;
                num = 0;
            } else if (ch == '(') {
                // save the outer context, then evaluate the inside as a fresh expression
                stack.push(result);
                stack.push(sign);
                result = 0;
                sign = 1;
            } else if (ch == ')') {
                result += sign * num; // fold the last number of the inner expression
                num = 0;
                result *= stack.pop(); // sign that was waiting in front of the '('
                result += stack.pop(); // outer running total before the '('
            }
            // spaces are skipped
        }

        // the last number has no operator after it to trigger the fold
        result += sign * num;
        return result;
    }

    private static void print(String label, String expr, int expected) {
        System.out.println(label + ": " + calculate(expr) + "   expected " + expected
                + "   (" + expr + ")");
    }

    public static void main(String[] args) {
        print("case 1 typical      ", "1 + 1", 2);
        print("case 2 spaces       ", " 2-1 + 2 ", 3);
        print("case 3 nested       ", "(1+(4+5+2)-3)+(6+8)", 23);
        print("case 4 unary minus  ", "-(2+3)", -5);
        print("case 5 sign flip    ", "2-(5-6)", 3);
        print("case 6 single number", "42", 42);
    }
}

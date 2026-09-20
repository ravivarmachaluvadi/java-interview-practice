/*
 * =====================================================================
 *  Infix to Prefix Conversion                        GfG classic | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an infix expression with single-letter/digit operands, the operators
 *   + - * / ^ and parentheses, return the equivalent prefix (Polish) expression.
 *   + - * / are left-associative, ^ is right-associative, precedence ^ > * / > + -.
 *
 * EXAMPLE
 *   (A-B/C)*(A/K-L)  ->  *-A/BC-/AKL
 *   A-B-C            ->  --ABC      because (A-B)-C, not A-(B-C)
 *   A^B^C            ->  ^A^BC      because A^(B^C), right-associative
 *   A                ->  A          single operand, no operators
 *
 * APPROACH  (reverse + shunting-yard to postfix + reverse)
 *   1. Reverse the infix string and swap every '(' with ')'.
 *   2. Run infix-to-postfix (operator stack) on that reversed string.
 *      Because the string is reversed, associativity flips: for + - * / pop
 *      only while the stack top has STRICTLY higher precedence; for ^ pop
 *      while the top has higher OR EQUAL precedence.
 *   3. Reverse the postfix output; that is the prefix expression.
 *
 * KEY INSIGHT
 *   Prefix of E == reverse(postfix(reverse(E) with brackets swapped)).
 *   The only trap is associativity: reversing the input turns a left-assoc
 *   chain into a right-assoc one, so the "pop on equal precedence" rule that
 *   is correct for plain infix-to-postfix gives the wrong tree here.
 *   Trap: popping on equal precedence for all operators turns A-B-C into
 *   -A-BC (= A-(B-C)) instead of --ABC.
 *
 * COMPLEXITY
 *   Time  O(n)  each character is pushed and popped at most once
 *   Space O(n)  operator stack plus output builder
 *
 * INTERVIEW FOLLOW-UPS
 *   - Infix to postfix directly (same loop, pop on <= for left-assoc, < for ^).
 *   - Evaluate the prefix expression (scan right to left with an operand stack).
 *   - Multi-character operands / numbers: tokenize first, then the same algorithm.
 *   - Unary minus: needs a lookahead to distinguish from binary minus.
 *
 * RUN
 *   main() runs 5 cases (typical, left-assoc chain, right-assoc chain, single
 *   operand, mixed precedence) and prints actual vs expected.
 */

import java.util.Stack;

class InfixToPrefix {

    static int precedence(char ch) {
        switch (ch) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
        }
        return -1;
    }

    static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    static String reverse(String expression) {
        return new StringBuilder(expression).reverse().toString();
    }

    /** Reverse the string and swap '(' with ')' so brackets still read left-to-right. */
    static String reverseAndSwapBrackets(String infix) {
        char[] chars = reverse(infix).toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '(') {
                chars[i] = ')';
            } else if (chars[i] == ')') {
                chars[i] = '(';
            }
        }
        return new String(chars);
    }

    static String infixToPrefix(String infix) {
        String reversedInfix = reverseAndSwapBrackets(infix);
        String postfixOfReversed = reversedInfixToPostfix(reversedInfix);
        return reverse(postfixOfReversed);
    }

    /**
     * Shunting-yard on an already-reversed expression.
     * Reversal flips associativity, so the pop rule is the mirror of the usual one:
     *   + - * /  (left-assoc originally)  -> pop only while top is strictly higher
     *   ^        (right-assoc originally) -> pop while top is higher or equal
     */
    static String reversedInfixToPostfix(String reversedInfix) {
        Stack<Character> operators = new Stack<>();
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < reversedInfix.length(); i++) {
            char c = reversedInfix.charAt(i);

            if (Character.isLetterOrDigit(c)) {
                output.append(c);
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    output.append(operators.pop());
                }
                operators.pop(); // discard the matching '('
            } else if (isOperator(c)) {
                while (!operators.isEmpty() && shouldPopBefore(c, operators.peek())) {
                    output.append(operators.pop());
                }
                operators.push(c);
            }
        }

        while (!operators.isEmpty()) {
            output.append(operators.pop());
        }
        return output.toString();
    }

    /** Pop rule for the reversed expression (see method comment above). */
    static boolean shouldPopBefore(char incoming, char top) {
        if (incoming == '^') {
            return precedence(incoming) <= precedence(top);
        }
        return precedence(incoming) < precedence(top);
    }

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical       ", infixToPrefix("(A-B/C)*(A/K-L)"), "*-A/BC-/AKL");
        print("case 2 left-assoc    ", infixToPrefix("A-B-C"), "--ABC");
        print("case 3 right-assoc   ", infixToPrefix("A^B^C"), "^A^BC");
        print("case 4 single operand", infixToPrefix("A"), "A");
        print("case 5 mixed prec    ", infixToPrefix("A+B*C-D"), "-+A*BCD");
    }
}

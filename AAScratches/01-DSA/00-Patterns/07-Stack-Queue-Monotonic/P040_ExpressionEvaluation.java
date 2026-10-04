/*
 * =====================================================================
 *  P040 Stack: Expression Evaluation and Nested Decoding   Canonical LC 150 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 150, Evaluate Reverse Polish Notation)
 *   tokens is an expression in postfix order with +, -, *, / (integer division truncating
 *   toward zero). Return its value.
 *
 * EXAMPLE
 *   ["2","1","+","3","*"]    ->  9      (2 + 1) * 3
 *   ["4","13","5","/","+"]   ->  6      4 + 13 / 5
 *
 * RECOGNIZE WHEN
 *   - Evaluate a string expression: postfix, infix with precedence, parentheses, unary
 *     minus.
 *   - Nested structures: "k[...]" repeats, nested lists, chemical formulas: work on the
 *     innermost level first and fold back out.
 *   Not this if: you only check that brackets match -> P036_MatchingStack.
 *
 * TEMPLATE
 *   postfix: number -> push; operator -> b = pop, a = pop, push(a op b)
 *   infix with + - * / (no parens): keep `last sign` and the current number; on the next
 *       operator, push +num / -num, or fold * and / into the top of the stack; sum at end
 *   parentheses: on '(' push (result so far, sign) and reset; on ')' pop and combine
 *   nested decode: on '[' push (string so far, count) and reset; on ']' pop and repeat
 *
 * APPROACH
 *   1. Numbers go on the stack.
 *   2. An operator takes the top two (the SECOND pop is the left operand) and pushes the
 *      result.
 *
 * KEY INSIGHT
 *   A stack holds "work suspended until something later arrives": operands waiting for an
 *   operator, a partial sum waiting for a ')', a prefix waiting for a ']'. Higher-precedence
 *   operators are applied immediately to the top; lower ones are deferred by pushing.
 *
 * COMPLEXITY
 *   Time O(n), space O(n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 227  Basic Calculator II      + - push signed numbers; * / combine with the
 *                                            top; answer = sum of the stack
 *   [coded] LC 224  Basic Calculator         + - and parentheses: stack of (result, sign)
 *   [coded] LC 394  Decode String            stacks of counts and string prefixes
 *           LC 772  Basic Calculator III     all of it: recursion on '(' with LC 227 inside
 *           LC 726  Number of Atoms          stack of count maps; multiply on ')'
 *           LC 1106 Parse Boolean Expression stack of chars; evaluate on ')'
 *           Infix to postfix (shunting-yard) operator stack ordered by precedence
 *
 * PITFALLS
 *   - Operand order for - and /: a = second pop, b = first pop, result a - b.
 *   - Java's / already truncates toward zero, as RPN requires.
 *   - Multi-digit numbers: build num = num * 10 + digit; spaces are skipped.
 *   - LC 227: process the LAST number too (treat the end of the string as an operator).
 *
 * DEEP DIVE
 *   C03_EvaluateReversePolishNotation, D05_BasicCalculator, C13_DecodeString,
 *   C15_InfixToPrefix (07-Stack-Queue-Monotonic)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.Deque;

class ExpressionEvaluation {

    // Canonical LC 150.
    static int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (String t : tokens) {
            switch (t) {
                case "+" -> stack.push(stack.pop() + stack.pop());
                case "*" -> stack.push(stack.pop() * stack.pop());
                case "-" -> {
                    int b = stack.pop();
                    stack.push(stack.pop() - b);
                }
                case "/" -> {
                    int b = stack.pop();
                    stack.push(stack.pop() / b);
                }
                default -> stack.push(Integer.parseInt(t));
            }
        }
        return stack.pop();
    }

    // LC 227: + - * / and spaces, no parentheses.
    static int calculateII(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        int num = 0;
        char op = '+';                             // the operator BEFORE num
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            }
            if ((!Character.isDigit(c) && c != ' ') || i == s.length() - 1) {
                switch (op) {
                    case '+' -> stack.push(num);
                    case '-' -> stack.push(-num);
                    case '*' -> stack.push(stack.pop() * num);
                    default -> stack.push(stack.pop() / num);
                }
                op = c;
                num = 0;
            }
        }
        int sum = 0;
        for (int x : stack) {
            sum += x;
        }
        return sum;
    }

    // LC 224: + - and parentheses, unary minus allowed.
    static int calculate(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        int result = 0;
        int sign = 1;
        int num = 0;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            } else if (c == '+' || c == '-') {
                result += sign * num;
                num = 0;
                sign = c == '+' ? 1 : -1;
            } else if (c == '(') {
                stack.push(result);                // suspend the outer sum
                stack.push(sign);                  // and the sign in front of '('
                result = 0;
                sign = 1;
            } else if (c == ')') {
                result += sign * num;
                num = 0;
                result = stack.pop() * result + stack.pop();
            }
        }
        return result + sign * num;
    }

    // LC 394.
    static String decodeString(String s) {
        Deque<Integer> counts = new ArrayDeque<>();
        Deque<StringBuilder> prefixes = new ArrayDeque<>();
        StringBuilder cur = new StringBuilder();
        int k = 0;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                k = k * 10 + (c - '0');
            } else if (c == '[') {
                counts.push(k);
                prefixes.push(cur);
                cur = new StringBuilder();
                k = 0;
            } else if (c == ']') {
                String inner = cur.toString();
                cur = prefixes.pop();
                cur.append(inner.repeat(counts.pop()));
            } else {
                cur.append(c);
            }
        }
        return cur.toString();
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 150 (2+1)*3", evalRPN(new String[]{"2", "1", "+", "3", "*"}), 9);
        check("LC 150 4+13/5", evalRPN(new String[]{"4", "13", "5", "/", "+"}), 6);
        check("LC 150 thirteen tokens",
                evalRPN(new String[]{"10", "6", "9", "3", "+", "-11", "*", "/", "*",
                "17", "+", "5", "+"}), 22);

        check("LC 227 3+2*2", calculateII("3+2*2"), 7);
        check("LC 227 3/2 with spaces", calculateII(" 3/2 "), 1);
        check("LC 227 3+5 / 2", calculateII(" 3+5 / 2 "), 5);
        check("LC 227 14-3/2", calculateII("14-3/2"), 13);

        check("LC 224 1 + 1", calculate("1 + 1"), 2);
        check("LC 224 2-1 + 2", calculate(" 2-1 + 2 "), 3);
        check("LC 224 nested parentheses", calculate("(1+(4+5+2)-3)+(6+8)"), 23);
        check("LC 224 unary minus", calculate("-(2+3)"), -5);

        check("LC 394 3[a]2[bc]", decodeString("3[a]2[bc]"), "aaabcbc");
        check("LC 394 3[a2[c]] nested", decodeString("3[a2[c]]"), "accaccacc");
        check("LC 394 2[abc]3[cd]ef", decodeString("2[abc]3[cd]ef"), "abcabccdcdcdef");
        check("LC 394 10[a] two-digit count", decodeString("10[a]"), "aaaaaaaaaa");
    }
}

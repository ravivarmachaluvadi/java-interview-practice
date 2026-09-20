/*
 * =====================================================================
 *  Valid Parentheses                              LeetCode 20 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a string containing only the characters ( ) [ ] { }, return true if every opening
 *   bracket is closed by a bracket of the same type, in the correct order. An empty string is
 *   valid. Length up to 10^4.
 *
 * EXAMPLE
 *   "()[]{}"  ->  true "([)]"    ->  false   because ']' arrives while '(' is still the innermost
 *   open bracket
 *   "(]"      ->  false   wrong type
 *   ""        ->  true    nothing to mismatch
 *   "("       ->  false   opener never closed
 *   ")("      ->  false   closer with nothing open
 *
 * APPROACH  (Stack as bracket matcher)
 *   1. Walk the string left to right.
 *   2. Opening bracket: push it. It is now the innermost unmatched opener.
 *   3. Closing bracket: the stack must be non-empty and its top must be the matching opener.
 *      Pop it. Anything else means the string is invalid, return false immediately.
 *   4. At the end the stack must be empty; leftover openers were never closed.
 *   Second method (isValidWithCounter): when only ( and ) exist, a single int balance counter
 *   replaces the stack. Go negative once -> invalid; end non-zero -> invalid.
 *
 * KEY INSIGHT
 *   A closing bracket must match the MOST RECENT unmatched opener, and "most recent" is
 *   exactly what a stack top is. The counter works for one type because there is no order to
 *   confuse; with several types "([)]" balances to zero for every type yet is invalid, and only
 *   a stack remembers which opener is innermost. Pattern: push openers, pop on matching closer.
 *
 * COMPLEXITY
 *   Time  O(n)  each character is pushed and popped at most once
 *   Space O(n)  worst case is a string of all openers; counter version is O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Only one bracket type: drop the stack for a counter (shown here).
 *   - Minimum insertions to make a string valid (LeetCode 921): count unmatched of each side.
 *   - Longest valid substring (LeetCode 32): store indices, use a -1 sentinel.
 *   - Streaming input with no upper bound on nesting: the stack is still the answer.
 *
 * RUN
 *   main() runs 9 cases (multi-type valid, wrong order, wrong type, empty, unclosed and
 *   closer-first through the stack; three single-type cases through the counter only), plus
 *   one extra demo line showing where the counter breaks, and prints actual vs expected.
 */
import java.util.Stack;

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

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Stack handles every bracket type
        print("case 1 \"()[]{}\" stack  ", isValidWithStack("()[]{}"), true);
        print("case 2 \"([)]\"   stack  ", isValidWithStack("([)]"), false);
        print("case 3 \"(]\"     stack  ", isValidWithStack("(]"), false);
        print("case 4 \"\"       stack  ", isValidWithStack(""), true);
        print("case 5 \"(\"      stack  ", isValidWithStack("("), false);
        print("case 6 \")(\"     stack  ", isValidWithStack(")("), false);

        // Single bracket type: counter and stack must agree
        print("case 7 \"()()\"   counter", isValidWithCounter("()()"), true);
        print("case 8 \"(()\"    counter", isValidWithCounter("(()"), false);
        print("case 9 \"())(\"   counter", isValidWithCounter("())("), false);

        // Why the counter is NOT enough for multiple types: it wrongly accepts "([)]"
        print("counter on \"([)]\" (wrong, shows the limit)", isValidWithCounter("([)]"), true);
    }
}

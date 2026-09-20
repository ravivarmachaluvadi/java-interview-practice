/*
 * =====================================================================
 *  Longest Valid Parentheses                    LeetCode 32 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a string of only '(' and ')', return the length of the longest
 *   contiguous substring that is a well-formed parentheses sequence.
 *   The string can be empty and can start with unmatched ')' characters.
 *
 * EXAMPLE
 *   ")()())"   ->  4   substring "()()"
 *   "(()"      ->  2   substring "()"
 *   ")(()()))" ->  6   substring "(()())"
 *   ""         ->  0
 *   "()(()"    ->  2   two separate valid pieces, neither joins the other
 *
 * APPROACH  (index stack with a -1 sentinel)
 *   1. Push -1 as the "index of the last unmatched ')'" so the first valid
 *      run has something to measure from and pop never sees an empty stack.
 *   2. On '(' push its index.
 *   3. On ')' pop. If the stack is now empty this ')' is unmatched: push its
 *      index as the new base. Otherwise the valid run ends here and started
 *      just after stack.peek(), so its length is i - stack.peek().
 *
 * APPROACH  (two-pass left/right counter, O(1) space)
 *   1. Scan left to right counting '(' and ')'. When equal, a valid run of
 *      length 2*right just ended. When right > left the run is broken: reset.
 *   2. This misses runs like "(()" where left stays ahead, so repeat right to
 *      left with the roles swapped (reset when left > right).
 *
 * KEY INSIGHT
 *   Store INDICES, not characters. The stack top after a pop is the index of
 *   the last character that is NOT part of the current valid run, so
 *   i - stack.peek() is the run length. The -1 sentinel makes the first run
 *   measure correctly. This "distance from the element below" arithmetic is
 *   the same trick Largest Rectangle in Histogram uses for width.
 *
 * COMPLEXITY
 *   Time  O(n)  each index pushed and popped at most once; the counter does two scans
 *   Space O(n)  for the stack; O(1) for the two-pass counter
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it in O(1) space (the two-pass counter shown here).
 *   - DP version: dp[i] = length of valid run ending at i; dp[i] = dp[i-2]+2 or
 *     dp[i-1] + 2 + dp[i - dp[i-1] - 2].
 *   - Return the substring itself, not just the length.
 *   - Multiple bracket types: the counter trick no longer works; use the stack.
 *
 * RUN
 *   main() runs 5 cases through both methods and prints actual vs expected.
 */

import java.util.Stack;

class ImportantLongestValidParentheses {

    /** Index stack with -1 sentinel. */
    public static int longestValidParenthesesUsingStack(String s) {
        Stack<Integer> lastUnmatched = new Stack<>();
        lastUnmatched.push(-1); // sentinel: base for the first valid run; guards pop() on ")))"
        int maxLen = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                lastUnmatched.push(i);
            } else {
                lastUnmatched.pop(); // matches a '(' or consumes the current base
                if (lastUnmatched.isEmpty()) {
                    lastUnmatched.push(i); // this ')' is unmatched: new base
                } else {
                    maxLen = Math.max(maxLen, i - lastUnmatched.peek());
                }
            }
        }
        return maxLen;
    }

    /** Two-pass counter, O(1) extra space. */
    public static int longestValidParentheses(String s) {
        int max = 0;

        // Left to right: catches runs where ')' never outruns '('.
        int left = 0, right = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') left++; else right++;
            if (left == right) {
                max = Math.max(max, 2 * right);
            } else if (right > left) {
                left = right = 0; // run broken by an unmatched ')'
            }
        }

        // Right to left: catches runs like "(()" where '(' stays ahead.
        left = right = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '(') left++; else right++;
            if (left == right) {
                max = Math.max(max, 2 * left);
            } else if (left > right) {
                left = right = 0; // run broken by an unmatched '('
            }
        }
        return max;
    }

    static void print(String label, String s, int expected) {
        System.out.println(label + " \"" + s + "\": stack=" + longestValidParenthesesUsingStack(s)
                + " counter=" + longestValidParentheses(s) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical ", ")()())", 4);
        print("case 2 open end", "(()", 2);
        print("case 3 nested  ", ")(()()))", 6);
        print("case 4 empty   ", "", 0);
        print("case 5 split   ", "()(()", 2);
    }
}

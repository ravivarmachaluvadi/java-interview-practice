/*
 * =====================================================================
 *  P036 Stack: Matching and Balancing   Canonical LC 20 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 20, Valid Parentheses)
 *   s holds only ()[]{}. It is valid if every opener is closed by the same type, in the
 *   right order. Return whether s is valid.
 *
 * EXAMPLE
 *   "()[]{}"  ->  true
 *   "([)]"    ->  false     wrong nesting order
 *   "{[]}"    ->  true
 *   "("       ->  false     left open at the end
 *
 * RECOGNIZE WHEN
 *   - Brackets, tags, nested blocks: the MOST RECENT unmatched opener must close first.
 *   - "remove adjacent pairs", "cancel", "backspace": the newest item reacts with the next.
 *   - Minimum insertions / deletions to balance.
 *   Not this if: you need the next greater / smaller element -> P037_NextGreaterElement;
 *   you evaluate an expression -> P040_ExpressionEvaluation.
 *
 * TEMPLATE
 *   stack = []
 *   for ch in s:
 *       if opener(ch): push(ch)                     // or push its INDEX if you need lengths
 *       else:
 *           if stack empty or top does not match ch: invalid (or count / mark it)
 *           else pop()
 *   valid iff stack is empty at the end
 *   only one bracket type: a counter replaces the stack
 *
 * APPROACH
 *   1. Push every opener.
 *   2. A closer must match the top; pop it. A mismatch or an empty stack is invalid.
 *   3. Anything left on the stack was never closed.
 *
 * KEY INSIGHT
 *   Nesting is last-in-first-out, which is exactly a stack. Pushing INDICES instead of
 *   characters upgrades "is it valid?" into "which characters are unmatched?" and "how
 *   long is each valid stretch?".
 *
 * COMPLEXITY
 *   Time O(n), space O(n) (O(1) with a counter for one bracket type).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 1249 Min Remove to Make Valid push indices of '('; unmatched ')' and the
 *                                            leftover '(' indices are the ones to delete
 *   [coded] LC 921  Min Add to Make Valid    one type -> counters: open, and needed ')'
 *   [coded] LC 32   Longest Valid Parentheses  stack of indices seeded with -1 (the last
 *                                            unmatched position); length = i - top
 *           LC 1047 Remove Adjacent Dups     push char, pop when equal to top
 *           LC 1209 Remove Adjacent Dups II  stack of (char, run count); pop at k
 *           LC 2390 Removing Stars           '*' pops the top
 *           LC 678  Valid Parenthesis String '*' wildcards: track min and max open counts
 *           LC 856  Score of Parentheses     depth counter: "()" at depth d adds 2^d
 *
 * PITFALLS
 *   - Check the stack is non-empty before peeking on a closer.
 *   - Return stack.isEmpty(), not true, at the end ("((" must fail).
 *   - Use ArrayDeque, not the legacy synchronized Stack class.
 *
 * DEEP DIVE
 *   A02_ValidParentheses, C02_MinimumAddToMakeParenthesesValid,
 *   D01_LongestValidParentheses, B01_RemoveAdjacentDuplicates (07-Stack-Queue-Monotonic)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.Deque;

class MatchingStack {

    // Openers and closers at matching positions.
    static final String OPEN = "([{";
    static final String CLOSE = ")]}";

    // Canonical LC 20.
    static boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (OPEN.indexOf(c) >= 0) {
                stack.push(c);
            } else {
                if (stack.isEmpty() || OPEN.indexOf(stack.pop()) != CLOSE.indexOf(c)) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    // LC 1249: delete the fewest parentheses to make s valid (letters stay).
    static String minRemoveToMakeValid(String s) {
        char[] c = s.toCharArray();
        Deque<Integer> open = new ArrayDeque<>();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '(') {
                open.push(i);
            } else if (c[i] == ')') {
                if (open.isEmpty()) {
                    c[i] = '*';                    // unmatched closer
                } else {
                    open.pop();
                }
            }
        }
        while (!open.isEmpty()) {
            c[open.pop()] = '*';                   // unmatched openers
        }
        StringBuilder sb = new StringBuilder();
        for (char ch : c) {
            if (ch != '*') {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    // LC 921: one bracket type, so counts are enough.
    static int minAddToMakeValid(String s) {
        int open = 0;
        int added = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                open++;
            } else if (open > 0) {
                open--;
            } else {
                added++;                           // a ')' with nothing to close
            }
        }
        return added + open;
    }

    // LC 32: the stack bottom is the index of the last unmatched ')' (or -1).
    static int longestValidParentheses(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(-1);
        int best = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
            } else {
                stack.pop();
                if (stack.isEmpty()) {
                    stack.push(i);                 // new barrier
                } else {
                    best = Math.max(best, i - stack.peek());
                }
            }
        }
        return best;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 20 ()[]{}", isValid("()[]{}"), true);
        check("LC 20 (]", isValid("(]"), false);
        check("LC 20 ([)] wrong order", isValid("([)]"), false);
        check("LC 20 {[]}", isValid("{[]}"), true);
        check("LC 20 ] closer first", isValid("]"), false);
        check("LC 20 ( never closed", isValid("("), false);

        check("LC 1249 lee(t(c)o)de)", minRemoveToMakeValid("lee(t(c)o)de)"), "lee(t(c)o)de");
        check("LC 1249 a)b(c)d", minRemoveToMakeValid("a)b(c)d"), "ab(c)d");
        check("LC 1249 ))(( all go", "[" + minRemoveToMakeValid("))((") + "]", "[]");

        check("LC 921 ())", minAddToMakeValid("())"), 1);
        check("LC 921 (((", minAddToMakeValid("((("), 3);
        check("LC 921 ()))((", minAddToMakeValid("()))(("), 4);

        check("LC 32 (()", longestValidParentheses("(()"), 2);
        check("LC 32 )()())", longestValidParentheses(")()())"), 4);
        check("LC 32 empty", longestValidParentheses(""), 0);
        check("LC 32 ()(()) joined", longestValidParentheses("()(())"), 6);
    }
}

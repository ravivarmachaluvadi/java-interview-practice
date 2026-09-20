/*
 * =====================================================================
 *  Generate Parentheses              LeetCode 22 | Medium | MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given n, return every well-formed string of n pairs of parentheses. A
 *   string is well formed if every '(' is eventually closed and no prefix ever
 *   has more ')' than '('. Any order is fine. n is small (1 to 8 on LeetCode)
 *   because the number of answers grows like 4^n.
 *
 * EXAMPLE
 *   n = 3  ->  [((())), (()()), (())(), ()(()), ()()()]     5 = Catalan(3)
 *   n = 1  ->  [()]
 *   n = 0  ->  [""]        the empty string is well formed (edge case)
 *   n = 4  ->  14 strings  = Catalan(4)
 *
 * APPROACH  (counter-based pruning of invalid branches)
 *   1. Carry two counters: how many '(' and how many ')' are still available.
 *   2. Base case: both counters hit 0, so the string uses all n pairs -- record.
 *   3. Open a bracket whenever left > 0. There is never a reason not to.
 *   4. Close a bracket only when right > left. That inequality means at least
 *      one '(' already placed is still unclosed, so ')' cannot go negative.
 *   5. Recurse on each allowed choice; the recursion explores the whole tree.
 *
 * KEY INSIGHT
 *   The validity rule is not something you check at the end -- it is encoded in
 *   the branch conditions, so every leaf the recursion reaches is already a
 *   valid answer. Generating only legal states instead of generating all 2^(2n)
 *   strings and filtering is the whole lesson, and it is the move that turns a
 *   brute-force answer into an accepted one. "right > left" reads as "there is
 *   an unmatched open bracket waiting to be closed".
 *
 * COMPLEXITY
 *   Time  O(4^n / sqrt(n))  one leaf per Catalan(n) answer, times O(n) to build
 *   Space O(n)              recursion depth is exactly 2n
 *                           (the result list adds O(4^n / sqrt(n) * n) on top)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Count the strings without generating them -> the Catalan number formula.
 *   - Use a StringBuilder and undo instead of string concatenation (second
 *     method below) -- avoids allocating a new String at every node.
 *   - Support several bracket types, ()[]{} -- now you need a stack of openers.
 *   - Valid Parenthesis String with '*' wildcards (LC 678).
 *   - Remove Invalid Parentheses (LC 301) -- the same balance rule, run backward.
 *
 * RUN
 *   main() runs 4 cases (n = 3 through both methods, n = 1, n = 0 edge case,
 *   n = 4 counted) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class GenerateParenthesis {

    // ---------- approach 1: the author's version, immutable strings ----------

    public static List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        generateParenthesisHelper(n, n, "", result);
        return result;
    }

    /**
     * @param left  how many '(' are still available to place
     * @param right how many ')' are still available to place
     */
    private static void generateParenthesisHelper(int left, int right, String expression, List<String> result) {
        if (left == 0 && right == 0) {
            result.add(expression);
            return;
        }

        if (left > 0) {
            generateParenthesisHelper(left - 1, right, expression + "(", result);
        }

        // right > left means some '(' already placed is still unclosed
        if (right > left) {
            generateParenthesisHelper(left, right - 1, expression + ")", result);
        }
    }

    // ---------- approach 2: same tree, StringBuilder with an explicit undo ----------

    public static List<String> generateParenthesisSb(int n) {
        List<String> result = new ArrayList<>();
        build(n, n, new StringBuilder(), result);
        return result;
    }

    private static void build(int left, int right, StringBuilder current, List<String> result) {
        if (left == 0 && right == 0) {
            result.add(current.toString()); // snapshot before current mutates again
            return;
        }

        if (left > 0) {
            current.append('(');
            build(left - 1, right, current, result);
            current.deleteCharAt(current.length() - 1); // undo
        }

        if (right > left) {
            current.append(')');
            build(left, right - 1, current, result);
            current.deleteCharAt(current.length() - 1); // undo
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + " " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1: the standard example, run through both implementations
        print("case 1 n=3 concat:  ", generateParenthesis(3),
                "[((())), (()()), (())(), ()(()), ()()()]");
        print("case 1 n=3 builder: ", generateParenthesisSb(3),
                "[((())), (()()), (())(), ()(()), ()()()]");

        // case 2: smallest non-trivial input
        print("case 2 n=1:         ", generateParenthesis(1), "[()]");

        // case 3: edge -- zero pairs still yields one answer, the empty string
        // it prints as [] because the one element is the empty string -- size proves it
        print("case 3 n=0:         ", generateParenthesis(0), "[]");
        print("case 3 n=0 size:    ", generateParenthesis(0).size(), 1);

        // case 4: the count must be Catalan(4) = 14
        print("case 4 n=4 size:    ", generateParenthesis(4).size(), 14);
    }
}

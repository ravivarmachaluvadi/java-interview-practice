/*
 * =====================================================================
 *  Expression Add Operators                         LeetCode 282 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given a string of digits and an integer target, insert '+', '-' or '*'
 *   between the digits - keeping their order, never reordering - so the resulting
 *   expression evaluates to target. Return every such expression.
 *   Digits may be glued into multi-digit numbers, but a number may not have a
 *   leading zero unless it is exactly "0". num is at most 10 digits long.
 *
 * EXAMPLE
 *   "123", 6  ->  ["1*2*3", "1+2+3"]
 *   "232", 8  ->  ["2*3+2", "2+3*2"]     shows why precedence must be handled
 *   "105", 5  ->  ["1*0+5", "10-5"]      "05" is rejected, "10" is allowed
 *   "0",   0  ->  ["0"]                  edge case: single digit, no operator
 *   "00",  0  ->  ["0*0", "0+0", "0-0"]  edge case: "0" alone is a legal number
 *
 * APPROACH  (segment DFS carrying the previous operand)
 *   1. At position pos, try every prefix num[pos..i] as the next operand. Break out
 *      of the loop as soon as num[pos] == '0' and i > pos - that kills leading zeros.
 *   2. The first operand has no operator in front of it: recurse with
 *      eval = cur and multed = cur.
 *   3. Otherwise branch three ways, where multed is the LAST term that was added
 *      into eval:
 *        '+'  eval + cur                    multed becomes  cur
 *        '-'  eval - cur                    multed becomes -cur
 *        '*'  eval - multed + multed * cur  multed becomes  multed * cur
 *   4. At pos == num.length(), record the expression if eval == target.
 *
 * KEY INSIGHT
 *   '*' binds tighter than the operator that already consumed the previous term,
 *   so you cannot just apply it to the running total. Carrying multed - the exact
 *   amount the last term contributed - lets you UNDO that contribution
 *   (eval - multed) and re-add it multiplied (+ multed * cur). Signed multed is
 *   what makes this work after a '-': 2-3*4 becomes 2 - 3 - (-3)*4 + ... = -10.
 *   Recognise "carry enough state to undo the last step" whenever precedence or a
 *   running aggregate appears inside a DFS.
 *
 * COMPLEXITY
 *   Time  O(4^n * n)  each gap is +, -, * or "glue the digits", and building the
 *                     expression string at a leaf costs O(n)
 *   Space O(n)        recursion depth and the current expression, excluding output
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why long rather than int for eval? A 10-digit operand times another overflows
 *     int; long is safe because num is capped at 10 digits.
 *   - Add '/' - now you must carry a fraction (numerator, denominator) or reject
 *     non-integer results, and guard division by zero.
 *   - Count the expressions instead of listing them: drop the string building and
 *     the answer fits in a counter, but the 4^n search remains.
 *   - Use a StringBuilder with undo instead of string concatenation - same big-O,
 *     far less garbage.
 *
 * RUN
 *   main() runs 6 cases (typical, precedence, leading zero, two edge cases, and a
 *   10-digit case with no solution) and prints sorted actual vs expected.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ExpressionAddOperators {

    public List<String> addOperators(String num, int target) {
        List<String> result = new ArrayList<>();
        if (num == null || num.isEmpty()) {
            return result;
        }
        helper(result, "", num, target, 0, 0, 0);
        return result;
    }

    /**
     * @param expression the text built so far
     * @param pos        first index not yet consumed
     * @param eval       value of expression so far
     * @param multed     signed value the LAST term contributed to eval, so a '*'
     *                   can subtract it back out and re-add it multiplied
     */
    private void helper(List<String> result, String expression, String num, int target,
                        int pos, long eval, long multed) {
        if (pos == num.length()) {
            if (eval == target) {
                result.add(expression);
            }
            return;
        }

        for (int i = pos; i < num.length(); i++) {
            // "0" is fine, "01" and anything longer starting with 0 is not,
            // and no longer prefix can rescue it - so break, not continue.
            if (i != pos && num.charAt(pos) == '0') {
                break;
            }
            long cur = Long.parseLong(num.substring(pos, i + 1));

            if (pos == 0) {
                // The very first operand carries no operator in front of it.
                helper(result, String.valueOf(cur), num, target, i + 1, cur, cur);
            } else {
                helper(result, expression + "+" + cur, num, target, i + 1, eval + cur, cur);
                helper(result, expression + "-" + cur, num, target, i + 1, eval - cur, -cur);
                // undo the last term, then put it back multiplied
                helper(result, expression + "*" + cur, num, target, i + 1,
                        eval - multed + multed * cur, multed * cur);
            }
        }
    }

    /** DFS order is fine but fragile to reason about, so sort before printing. */
    private static List<String> sorted(String num, int target) {
        List<String> out = new ExpressionAddOperators().addOperators(num, target);
        Collections.sort(out);
        return out;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 \"123\", 6", sorted("123", 6), "[1*2*3, 1+2+3]");
        print("case 2 \"232\", 8 (precedence)", sorted("232", 8), "[2*3+2, 2+3*2]");
        print("case 3 \"105\", 5 (leading zero)", sorted("105", 5), "[1*0+5, 10-5]");
        print("case 4 \"0\", 0 (single digit)", sorted("0", 0), "[0]");
        print("case 5 \"00\", 0 (zero is legal alone)", sorted("00", 0), "[0*0, 0+0, 0-0]");
        print("case 6 \"3456237490\", 9191 (no solution)", sorted("3456237490", 9191), "[]");
    }
}

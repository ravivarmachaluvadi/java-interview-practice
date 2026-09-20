/*
 * =====================================================================
 *  Minimum Add to Make Parentheses Valid            LeetCode 921 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   s contains only '(' and ')'. In one move you may insert a parenthesis anywhere.
 *   Return the minimum number of insertions that makes s valid (every opener has a
 *   closer after it, properly nested). s may be empty.
 *
 * EXAMPLE
 *   "()))(("  ->  4   two extra ')' need two '(' before them; two extra '(' need ')'
 *   "())"     ->  1   one unmatched ')'
 *   "((("     ->  3   three unmatched '('
 *   "))(("    ->  4   nothing pairs up at all
 *   ""        ->  0   already valid
 *
 * APPROACH  (two-sided balance counter)
 *   1. Scan left to right with two counters: open = unmatched '(' so far,
 *      close = ')' that arrived when no '(' was available.
 *   2. On '(' : open++.
 *   3. On ')' : if open > 0 it pairs with a pending '(' so open--; else close++.
 *   4. Answer = open + close. Each unmatched symbol needs exactly one insertion.
 *
 * KEY INSIGHT
 *   Valid Parentheses (LC 20) only needs one balance counter and a "did it ever go
 *   negative" check. Here, a ')' that would drive the balance negative is not a
 *   failure, it is a mandatory insertion, so count it separately and reset. The
 *   leftover '(' at the end are the other mandatory insertions. No stack is needed
 *   because there is only one bracket type.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass
 *   Space O(1)  two ints
 *
 * INTERVIEW FOLLOW-UPS
 *   - Minimum Remove to Make Valid Parentheses (LC 1249): same counters, but you must
 *     rebuild the string, so record indices to delete
 *   - Minimum Insertions to Balance (LC 1541): each '(' needs "))", tricky odd cases
 *   - Longest Valid Parentheses (LC 32): needs positions, so a sentinel-index stack
 *
 * RUN
 *   main() runs 5 cases (typical, closer-heavy, opener-heavy, nothing pairs, empty)
 *   and prints actual vs expected. Fixed: main's last label said "()" while it was
 *   testing "))((".
 */
class MinimumAddToMakeParenthesesValid {

    public static int minAddToMakeValid(String s) {
        int unmatchedOpen = 0;  // '(' still waiting for a ')'
        int unmatchedClose = 0; // ')' that arrived with nothing to pair with

        for (char ch : s.toCharArray()) {
            if (ch == '(') {
                unmatchedOpen++;
            } else if (unmatchedOpen > 0) {
                unmatchedOpen--;        // this ')' pairs with a pending '('
            } else {
                unmatchedClose++;       // needs a '(' inserted before it
            }
        }
        return unmatchedOpen + unmatchedClose;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 \"()))((\"", minAddToMakeValid("()))(("), 4);
        print("case 2 \"())\"   ", minAddToMakeValid("())"), 1);
        print("case 3 \"(((\"   ", minAddToMakeValid("((("), 3);
        print("case 4 \"))((\"  ", minAddToMakeValid("))(("), 4);
        print("case 5 \"\" (empty)", minAddToMakeValid(""), 0);
    }
}

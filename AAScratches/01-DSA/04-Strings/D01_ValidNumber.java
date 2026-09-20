/*
 * =====================================================================
 *  Valid Number                                        LeetCode 65 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given a string s, decide whether it is a valid number: an optional sign, then an integer
 *   ("12") or a decimal ("1.", ".5", "1.5"), optionally followed by 'e'/'E', an optional sign
 *   and a mandatory integer exponent. Anything else (a letter, a second dot, a dot inside the
 *   exponent, a sign mid-number) makes it invalid.
 *   Both methods here also trim leading/trailing spaces. That is the LEGACY LC 65 rule: the
 *   current judge restricts s to letters, digits, '+', '-' and '.', so spaces never appear.
 *
 * EXAMPLE
 *   "0.1"   -> true      "3e+7"  -> true      "-1."   -> true       "46.e3"  -> true
 *   "e"     -> false     "."     -> false     "1e+"   -> false      "99e2.5" -> false
 *   "-+3"   -> false     "abc"   -> false     ""      -> false      "+"      -> false
 *
 * APPROACH  (single pass with three flags: seenDigit, seenDot, seenE)
 *   1. trim; empty -> false.
 *   2. For each character decide by its class:
 *        digit -> seenDigit = true '.'   -> invalid if a dot or an 'e' was already seen (no dot
 *        inside the exponent)
 *        'e'   -> invalid if no digit yet, or a second 'e'; then RESET seenDigit because the
 *                 exponent must contain its own digit ("1e" and "1e+" are invalid)
 *        sign  -> legal only at index 0 or directly after 'e'/'E'
 *        other -> false
 *   3. Return seenDigit: whichever group came last (mantissa or exponent) needed a digit.
 *
 *   isNumberDfa() expresses the same rules as an explicit state machine: 8 states, 4 character
 *   classes, one transition table. Both methods run from main().
 *
 * KEY INSIGHT
 *   There is no algorithm here, only ordering rules: dot before e, a digit before e, a sign only
 *   at a group start, a digit somewhere in every group. Resetting seenDigit at 'e' is the trick
 *   that lets one flag validate both groups. When the rule set grows, draw the DFA: the
 *   accepting states are exactly "just saw a digit" (integer, decimal, exponent digits).
 *
 * COMPLEXITY
 *   Time  O(n)  one pass over the characters
 *   Space O(1)  three booleans (the DFA table is a constant)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Draw the DFA and name its accepting states (2, 4, 7 in isNumberDfa).
 *   - Add hex ("0x1F") or thousands separators ("1,000"): which rules or states change?
 *   - Why not a regex? [+-]?(\d+\.?\d*|\.\d+)([eE][+-]?\d+)? works, but the interviewer
 *     wants the hand-written scan and the reasoning behind each rule.
 *   - String to Integer (atoi) and String Compression use the same scan-by-class skeleton.
 *
 * RUN
 *   main() runs 13 cases through both methods and prints actual vs expected.
 */
class ValidNumber {

    /** Flag-based scan: the version to write first in an interview. */
    public static boolean isNumber(String s) {
        s = s.trim();
        if (s.isEmpty()) return false;

        boolean seenDigit = false, seenDot = false, seenE = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                seenDigit = true;
            } else if (c == '.') {
                if (seenDot || seenE) return false;     // one dot, and never inside the exponent
                seenDot = true;
            } else if (c == 'e' || c == 'E') {
                if (!seenDigit || seenE) return false;  // mantissa needs a digit; only one 'e'
                seenE = true;
                seenDigit = false;                      // exponent must supply its own digit
            } else if (c == '+' || c == '-') {
                boolean afterE = i > 0 && (s.charAt(i - 1) == 'e' || s.charAt(i - 1) == 'E');
                if (i != 0 && !afterE) return false;    // sign only at start or right after 'e'
            } else {
                return false;
            }
        }
        return seenDigit;   // false for "1e", "1e+", ".", "+": the last group had no digit
    }

    // ---- Alternative: the same rules as an explicit state machine -------------------------
    //  States: 0 start | 1 sign | 2 integer digits | 3 lone dot | 4 decimal digits
    //          5 'e'   | 6 exponent sign | 7 exponent digits.        Accepting: 2, 4, 7.
    private static final int SIGN = 0, DIGIT = 1, DOT = 2, EXP = 3;
    private static final int[][] NEXT = {
            // SIGN DIGIT DOT EXP
            {   1,   2,   3, -1 },   // 0 start
            {  -1,   2,   3, -1 },   // 1 after leading sign
            {  -1,   2,   4,  5 },   // 2 integer digits
            {  -1,   4,  -1, -1 },   // 3 dot with no digit before it (".5" ok, "." not)
            {  -1,   4,  -1,  5 },   // 4 decimal digits ("1." and "1.5" both land here)
            {   6,   7,  -1, -1 },   // 5 just saw 'e'
            {  -1,   7,  -1, -1 },   // 6 sign after 'e'
            {  -1,   7,  -1, -1 },   // 7 exponent digits
    };

    public static boolean isNumberDfa(String s) {
        int state = 0;
        for (char c : s.trim().toCharArray()) {
            int cls;
            if (c == '+' || c == '-') cls = SIGN;
            else if (Character.isDigit(c)) cls = DIGIT;
            else if (c == '.') cls = DOT;
            else if (c == 'e' || c == 'E') cls = EXP;
            else return false;
            state = NEXT[state][cls];
            if (state == -1) return false;
        }
        return state == 2 || state == 4 || state == 7;
    }

    private static void check(String s, boolean expected) {
        System.out.println("\"" + s + "\": flags=" + isNumber(s) + " dfa=" + isNumberDfa(s)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        // typical
        check("0.1", true);
        check("3e+7", true);
        check("-1.", true);
        check(" 46.e3 ", true);           // legacy variant: spaces trimmed
        // edge
        check("", false);
        check(".", false);
        check("+", false);
        check("e", false);
        // tricky
        check(".1", true);
        check("1e+", false);
        check("99e2.5", false);
        check("-+3", false);
        check("95a54e53", false);
    }
}

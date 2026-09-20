/*
 * =====================================================================
 *  String to Integer (atoi)                        LeetCode 8 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Parse a string into a 32-bit signed int the way C's atoi does: skip leading whitespace,
 *   read an optional '+' or '-', then read digits until the first non-digit. A value that
 *   would fall outside [-2^31, 2^31 - 1] is clamped to the nearer boundary.
 *
 * EXAMPLE
 *   "42"               ->  42
 *   "   -042"          ->  -42            leading spaces and zeros ignored
 *   "1337c0d3"         ->  1337           stops at the first non-digit
 *   "words and 987"    ->  0              no digits before the first letter
 *   "2147483648"       ->  2147483647     clamped to Integer.MAX_VALUE
 *   "-91283472332"     ->  -2147483648    clamped to Integer.MIN_VALUE
 *   ""                 ->  0              edge: empty
 *
 * APPROACH  (digit loop with a pre-multiply overflow guard)
 *   1. strip() the whitespace; an empty result means 0.
 *   2. If the first char is '+' or '-', remember the sign and move past it.
 *   3. For each digit d, BEFORE computing result * 10 + d, check
 *        result > (Integer.MAX_VALUE - d) / 10
 *      If true the next value would overflow, so return the clamp for this sign.
 *   4. Stop at the first non-digit and apply the sign.
 *
 * KEY INSIGHT
 *   You cannot detect int overflow after it happens (the value silently wraps), so rearrange
 *   the inequality result * 10 + d > MAX into result > (MAX - d) / 10, which is safe to
 *   evaluate in int. Because MIN_VALUE = -(MAX_VALUE + 1), the positive-side guard also
 *   handles the negative side: "-2147483648" trips the guard and returns MIN_VALUE, which
 *   happens to be the exact answer.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass over the characters
 *   Space O(1)  a handful of ints
 *
 * INTERVIEW FOLLOW-UPS
 *   - Without the division: accumulate in a long and clamp at the end. Ask first whether a
 *     wider type is allowed; the guard above is the answer when it is not.
 *   - Why not Integer.parseInt in a try/catch? It rejects "1337c0d3" and does not clamp, and
 *     the interviewer wants to see the manual loop anyway.
 *   - Extend to a different base, or to parse a decimal fraction.
 *
 * RUN
 *   main() runs 8 cases (typical, signs, stop-at-letter, no digits, both overflow clamps,
 *   empty) and prints actual vs expected.
 */
class ATOI {

    public static int myAtoi(String s) {
        s = s.strip();
        if (s.isEmpty()) {
            return 0;
        }

        boolean isNegative = false;
        int index = 0;
        char first = s.charAt(0);
        if (first == '-' || first == '+') {
            isNegative = first == '-';
            index++;
        }

        int result = 0;
        while (index < s.length()) {
            char ch = s.charAt(index);
            if (!Character.isDigit(ch)) {
                break;
            }
            int digit = ch - '0';
            // Would result * 10 + digit exceed MAX? Ask before multiplying, not after.
            if (result > (Integer.MAX_VALUE - digit) / 10) {
                return isNegative ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            }
            result = result * 10 + digit;
            index++;
        }
        return isNegative ? -result : result;
    }

    public static void main(String[] args) {
        String[] inputs = {"42", "   -042", "+1", "1337c0d3", "words and 987",
                "2147483648", "-91283472332", ""};
        int[] expected = {42, -42, 1, 1337, 0,
                Integer.MAX_VALUE, Integer.MIN_VALUE, 0};

        for (int i = 0; i < inputs.length; i++) {
            print("case " + (i + 1) + " \"" + inputs[i] + "\"", myAtoi(inputs[i]), expected[i]);
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

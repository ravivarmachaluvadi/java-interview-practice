/*
 * =====================================================================
 *  P108 Careful Parsing and String Simulation   Canonical LC 8 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 8, String to Integer (atoi))
 *   Skip leading spaces, read an optional sign, then digits until a non-digit. Clamp to the
 *   32-bit range. Anything malformed before the digits gives 0.
 *
 * EXAMPLE
 *   "42"              ->  42
 *   "   -042"         ->  -42
 *   "1337c0d3"        ->  1337
 *   "words and 987"   ->  0
 *   "-91283472332"    ->  -2147483648     clamped
 *
 * RECOGNIZE WHEN
 *   - The problem is a precise spec over characters: number formats, compression, layout
 *     (zigzag, justification), comments, IP addresses. No clever algorithm, many edge cases.
 *   - Interviewers grade it on edge cases: empty input, signs, overflow, leading zeros.
 *   Not this if: there is real nesting (brackets, k[...]) -> P040_ExpressionEvaluation; you
 *   search for a pattern -> P106_KmpPrefixFunction.
 *
 * TEMPLATE
 *   one index i walks the string in PHASES (spaces, sign, digits, ...)
 *   each phase: while i < n and s[i] fits the phase: consume
 *   overflow check BEFORE result = result * 10 + d:
 *       if result > (MAX - d) / 10: clamp
 *   state flags (seenDigit, seenDot, seenExp) for validators instead of regexes
 *
 * APPROACH
 *   1. Skip spaces. 2. Read one sign. 3. Read digits, checking overflow before each step.
 *   4. Stop at the first non-digit; return sign * result.
 *
 * KEY INSIGHT
 *   Write the spec as an ordered list of phases and give each phase its own small loop.
 *   Check for overflow before it happens (compare against (MAX - d) / 10), because after it
 *   happens the value is already wrong.
 *
 * COMPLEXITY
 *   Time O(n), space O(1) (O(n) for LC 443's output written in place).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 443  String Compression       read a run, write the char and its count digits
 *                                            in place; return the new length
 *   [coded] LC 65   Valid Number             flags: digit seen, dot seen, exponent seen;
 *                                            a sign only at the start or right after 'e'
 *           LC 6    Zigzag Conversion        one StringBuilder per row, bounce the row index
 *           LC 68   Text Justification       greedy line fill, then spread the spaces
 *           LC 468  Validate IP Address      split carefully (keep trailing empty parts)
 *           LC 722  Remove Comments          a block-comment flag carried across lines
 *           LC 7    Reverse Integer          the same overflow check
 *
 * PITFALLS
 *   - "+-12" is 0: only one sign is allowed.
 *   - Integer.MIN_VALUE has no positive int counterpart; clamp by sign.
 *   - LC 65: "." and "e" alone are invalid, ".1" and "1." are valid.
 *
 * DEEP DIVE
 *   C02_ATOI, C03_StringCompression, D01_ValidNumber, C05_ZigzagConversion (04-Strings)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class CarefulParsing {

    // Canonical LC 8.
    static int myAtoi(String s) {
        int i = 0;
        int n = s.length();
        while (i < n && s.charAt(i) == ' ') {
            i++;
        }
        int sign = 1;
        if (i < n && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
            sign = s.charAt(i) == '-' ? -1 : 1;
            i++;
        }
        int result = 0;
        while (i < n && Character.isDigit(s.charAt(i))) {
            int d = s.charAt(i) - '0';
            if (result > (Integer.MAX_VALUE - d) / 10) {
                return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }
            result = result * 10 + d;
            i++;
        }
        return sign * result;
    }

    // LC 443: compress in place; returns the new length.
    static int compress(char[] chars) {
        int write = 0;
        int read = 0;
        while (read < chars.length) {
            char c = chars[read];
            int start = read;
            while (read < chars.length && chars[read] == c) {
                read++;
            }
            chars[write++] = c;
            int run = read - start;
            if (run > 1) {
                for (char d : String.valueOf(run).toCharArray()) {
                    chars[write++] = d;
                }
            }
        }
        return write;
    }

    // LC 65.
    static boolean isNumber(String s) {
        boolean digit = false;
        boolean dot = false;
        boolean exp = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                digit = true;
            } else if (c == '+' || c == '-') {
                if (i > 0 && s.charAt(i - 1) != 'e' && s.charAt(i - 1) != 'E') {
                    return false;                  // a sign only at the start or after e
                }
            } else if (c == '.') {
                if (dot || exp) {
                    return false;                  // one dot, and never in the exponent
                }
                dot = true;
            } else if (c == 'e' || c == 'E') {
                if (exp || !digit) {
                    return false;                  // one e, with digits before it
                }
                exp = true;
                digit = false;                     // the exponent needs its own digits
            } else {
                return false;
            }
        }
        return digit;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 8 [42]", myAtoi("42"), 42);
        check("LC 8 [   -042]", myAtoi("   -042"), -42);
        check("LC 8 [1337c0d3]", myAtoi("1337c0d3"), 1337);
        check("LC 8 [0-1]", myAtoi("0-1"), 0);
        check("LC 8 [words and 987]", myAtoi("words and 987"), 0);
        check("LC 8 [-91283472332] clamp", myAtoi("-91283472332"), Integer.MIN_VALUE);
        check("LC 8 [2147483648] clamp", myAtoi("2147483648"), Integer.MAX_VALUE);
        check("LC 8 [+-12] two signs", myAtoi("+-12"), 0);

        char[] a = "aabbccc".toCharArray();
        int len = compress(a);
        check("LC 443 aabbccc", len + " " + new String(a, 0, len), "6 a2b2c3");
        char[] b = "a".toCharArray();
        int len2 = compress(b);
        check("LC 443 single", len2 + " " + new String(b, 0, len2), "1 a");
        char[] c = "abbbbbbbbbbbb".toCharArray();
        int len3 = compress(c);
        check("LC 443 run of 12", len3 + " " + new String(c, 0, len3), "4 ab12");

        String[] valid = {"0", "2e10", "-90E3", "-.9", "53.5e93", ".1", "1.", "+.8", "3e+7"};
        String[] invalid = {"e", ".", "99e2.5", "--6", "1e", "+", "6+1", "1a", "e3"};
        boolean allValid = true;
        for (String v : valid) {
            allValid &= isNumber(v);
        }
        boolean noneInvalid = true;
        for (String v : invalid) {
            noneInvalid &= !isNumber(v);
        }
        check("LC 65 nine valid numbers", allValid, true);
        check("LC 65 nine invalid strings", noneInvalid, true);
    }
}

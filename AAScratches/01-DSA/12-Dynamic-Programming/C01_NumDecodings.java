/*
 * =====================================================================
 *  Decode Ways                                          LeetCode 91 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   A message of digits was encoded with 'A'=1 ... 'Z'=26. Given the digit string,
 *   count how many different letter strings could have produced it. A piece may be
 *   one digit (1-9) or two digits (10-26); a leading '0' decodes to nothing, so
 *   "06" is not a valid piece and "0" anywhere with no valid pairing kills the count.
 *
 * EXAMPLE
 *   s = "12"      -> 2    "AB" (1 2) or "L" (12)
 *   s = "226"     -> 3    "BZ" (2 26), "VF" (22 6), "BBF" (2 2 6)
 *   s = "06"      -> 0    a piece may not start with '0'
 *   s = "10"      -> 1    only "J" (10); '1' then '0' is invalid
 *
 * APPROACH  (1D DP with validity checks, Fibonacci shape)
 *   1. dp[i] = number of ways to decode the first i characters.
 *   2. dp[0] = 1 (the empty prefix has exactly one decoding: decode nothing).
 *      dp[1] = 1 unless the first character is '0', in which case 0.
 *   3. For i from 2 to n, look at the last one and last two characters:
 *        one digit  s[i-1]       valid if 1..9   -> add dp[i - 1]
 *        two digits s[i-2..i-1]  valid if 10..26 -> add dp[i - 2]
 *      dp[i] starts at 0, so a position where neither piece is valid stays 0 and
 *      that zero propagates forward, which is exactly what we want.
 *   4. Answer is dp[n].
 *
 * KEY INSIGHT
 *   It is Climbing Stairs with two gates on the steps: step of size 1 is only
 *   allowed when the digit is not '0', step of size 2 only when the pair is 10-26.
 *   Everything interviewers actually score here is the '0' handling, not the
 *   recurrence: '0' can never stand alone, and can only survive as the second
 *   half of "10" or "20".
 *
 * COMPLEXITY
 *   Time  O(n)   one pass, constant work per index
 *   Space O(n)   the dp table; two rolling ints would make it O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Drop the array to two variables for O(1) space.
 *   - Decode Ways II (LeetCode 639): '*' stands for any digit 1-9; count modulo 1e9+7.
 *   - Return one actual decoding, or all of them, instead of the count.
 *   - What if the alphabet went to 'Z'=99? Only the pair range changes.
 *
 * RUN
 *   main() runs 5 cases (typical, branching, leading zero, trailing zero, empty)
 *   and prints actual vs expected.
 */

class NumDecodings {

    public static int numDecodings(String s) {
        if (s == null || s.isEmpty()) return 0;

        int n = s.length();
        int[] dp = new int[n + 1];
        dp[0] = 1;                                       // empty prefix: one way
        dp[1] = s.charAt(0) == '0' ? 0 : 1;              // a leading '0' decodes to nothing

        for (int i = 2; i <= n; i++) {
            // last single character, e.g. "...6" -> 6
            int oneDigit = Integer.parseInt(s.substring(i - 1, i));
            // last two characters read as a number, e.g. "...26" -> 26
            int twoDigits = Integer.parseInt(s.substring(i - 2, i));

            // a single digit decodes only when it is not '0'
            if (oneDigit >= 1 && oneDigit <= 9) {
                dp[i] += dp[i - 1];
            }
            // a pair decodes only in 10..26; "05" is 5 as an int, so the >= 10
            // test also rejects pairs with a leading zero
            if (twoDigits >= 10 && twoDigits <= 26) {
                dp[i] += dp[i - 2];
            }
        }
        return dp[n];
    }

    private static void print(String label, String s, int expected) {
        System.out.println(label + " \"" + s + "\" -> " + numDecodings(s)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1:", "12", 2);
        print("case 2:", "226", 3);
        print("case 3 (leading zero):", "06", 0);
        print("case 4 (trailing zero):", "10", 1);
        print("case 5 (empty):", "", 0);
    }
}

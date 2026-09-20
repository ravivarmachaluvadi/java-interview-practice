/*
 * =====================================================================
 *  Add Strings                                        LeetCode 415 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given two non-negative integers as decimal strings (no leading zeros except "0"
 *   itself), return their sum as a string. The numbers can be far longer than a long
 *   holds, and BigInteger / parseInt are not allowed.
 *
 * EXAMPLE
 *   "456"  + "77"   ->  "533" "999"  + "1"    ->  "1000"    a carry survives past the last digit of
 *   both inputs
 *   "0"    + "0"    ->  "0" "123"  + "987"  ->  "1110"
 *
 * APPROACH  (digit carry simulation)
 *   1. Point i and j at the LAST character of each string, carry = 0.
 *   2. Loop while i >= 0 OR j >= 0 OR carry != 0: read digit i (0 if exhausted), digit j
 *      (0 if exhausted), sum = d1 + d2 + carry.
 *   3. Append sum % 10 to the builder, carry = sum / 10, step i and j left.
 *   4. The builder holds digits least-significant first, so reverse it once at the end.
 *
 * KEY INSIGHT
 *   Treat a missing digit as 0 and keep looping while ANY of (digits left in a, digits
 *   left in b, carry) is non-zero. That single loop condition handles unequal lengths and
 *   the final carry-out without special cases. Multiply Strings and atoi are the same
 *   char - '0' digit loop with different bookkeeping.
 *
 * COMPLEXITY
 *   Time  O(max(n, m))  one pass over the longer string plus at most one carry step
 *   Space O(max(n, m))  the result builder; O(1) auxiliary otherwise
 *
 * INTERVIEW FOLLOW-UPS
 *   - Add Binary (LeetCode 67): identical loop with base 2 instead of 10.
 *   - Multiply Strings (LeetCode 43): partial products land at index i + j and i + j + 1.
 *   - Signed inputs or decimals: normalise signs first, align on the decimal point.
 *
 * RUN
 *   main() runs 4 cases (typical, carry-out, both zero, equal length) and prints actual
 *   vs expected.
 */
class AddStrings {

    static String addStrings(String num1, String num2) {
        StringBuilder result = new StringBuilder();
        int carry = 0;
        int i = num1.length() - 1;
        int j = num2.length() - 1;
        // keep going while either string has digits left OR a carry is pending
        while (i >= 0 || j >= 0 || carry != 0) {
            int digit1 = i >= 0 ? num1.charAt(i) - '0' : 0;
            int digit2 = j >= 0 ? num2.charAt(j) - '0' : 0;
            int sum = digit1 + digit2 + carry;
            result.append(sum % 10);
            carry = sum / 10;
            i--;
            j--;
        }
        // digits were appended least-significant first
        return result.reverse().toString();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 456 + 77", addStrings("456", "77"), "533");
        print("case 2 999 + 1", addStrings("999", "1"), "1000");
        print("case 3 0 + 0", addStrings("0", "0"), "0");
        print("case 4 123 + 987", addStrings("123", "987"), "1110");
    }
}

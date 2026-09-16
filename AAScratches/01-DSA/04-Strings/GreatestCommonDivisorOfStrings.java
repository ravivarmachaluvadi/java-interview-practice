/**
 * Problem: Given two strings, find the largest string that can be concatenated
 * to form both input strings. If no such string exists, return an empty string.
 *
 * Approach:
 * 1. Verify that the two strings are compatible by checking if str1+str2 equals
 *    str2+str1; otherwise, no common divisor exists.
 * 2. Compute the greatest common divisor (GCD) of their lengths using Euclid’s
 *    algorithm.
 * 3. The GCD length determines the candidate substring: return the prefix of
 *    str1 up to that length.
 *
 * Time Complexity: O(n + m), where n and m are the lengths of str1 and str2,
 * because string concatenation and equality check dominate.
 * Space Complexity: O(1) auxiliary space (ignoring input strings).
 */
class GreatestCommonDivisorOfStrings {

    public static String gcdOfStrings(String str1, String str2) {
        if (!(str1 + str2).equals(str2 + str1)) {
            return "";
        }

        int gcdLength = gcd(str1.length(), str2.length());
        return str1.substring(0, gcdLength);
    }

    private static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public static void main(String[] args) {
        String str1 = "ABCABC";
        String str2 = "ABC";
        System.out.println("GCD of Strings (Example 1): " + gcdOfStrings(str1, str2)); // Output: "ABC"

        str1 = "ABABAB";
        str2 = "ABAB";
        System.out.println("GCD of Strings (Example 2): " + gcdOfStrings(str1, str2)); // Output: "AB"

        str1 = "LEET";
        str2 = "CODE";
        System.out.println("GCD of Strings (Example 3): " + gcdOfStrings(str1, str2)); // Output: ""
    }
}

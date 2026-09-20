/*
 * =====================================================================
 *  Greatest Common Divisor of Strings                  LeetCode 1071 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given two non-empty strings str1 and str2, return the largest string x such that
 *   x repeated some number of times equals str1 AND x repeated some (possibly different)
 *   number of times equals str2. If no such x exists, return "".
 *
 * EXAMPLE
 *   str1 = "ABCABC", str2 = "ABC"   ->  "ABC"
 *   str1 = "ABABAB", str2 = "ABAB"  ->  "AB"     (both are repeats of "AB")
 *   str1 = "LEET",   str2 = "CODE"  ->  ""       (no common repeating unit)
 *   str1 = "AAA",    str2 = "AAA"   ->  "AAA"    (identical strings: answer is the string)
 *   str1 = "ABCDEF", str2 = "ABC"   ->  ""       ("ABC" is a prefix but not a divisor)
 *
 * APPROACH  (concat equality plus gcd of lengths)
 *   1. If str1 + str2 != str2 + str1, the strings are not built from one common unit,
 *      so return "" immediately.
 *   2. Otherwise a common unit exists, and the largest one has length gcd(|str1|, |str2|).
 *      Compute that with Euclid's algorithm.
 *   3. Return the prefix of str1 of that length.
 *
 * KEY INSIGHT
 *   Two strings share a repeating unit if and only if str1+str2 equals str2+str1. Once
 *   that holds, every common divisor of both lengths is a valid unit, so the largest is
 *   the gcd of the lengths. Same doubling idea as RotateString (B15_RotateString): concatenation
 *   exposes periodicity. Pattern: "is X built from repeats of Y" -> compare concats.
 *
 * COMPLEXITY
 *   Time  O(n + m)   one concatenation and one equality check dominate; gcd is O(log n)
 *   Space O(n + m)   the two temporary concatenated strings
 *
 * INTERVIEW FOLLOW-UPS
 *   - Prove the if-and-only-if: why does str1+str2 == str2+str1 force a common period?
 *   - Do it without building the concatenations (check the candidate prefix by repetition).
 *   - Related: Repeated Substring Pattern (LC 459) uses (s+s).indexOf(s, 1) < s.length().
 *   - Memory-tight variant: compare characters modulo the candidate length instead.
 *
 * RUN
 *   main() runs 5 cases (typical, edge, tricky) and prints actual vs expected.
 */
class GreatestCommonDivisorOfStrings {

    public static String gcdOfStrings(String str1, String str2) {
        // If the strings are made of the same repeating unit, the order of concatenation
        // cannot matter. If it does matter, there is no common unit.
        if (!(str1 + str2).equals(str2 + str1)) {
            return "";
        }

        int gcdLength = gcd(str1.length(), str2.length());
        return str1.substring(0, gcdLength);
    }

    // Euclid: gcd(a, b) == gcd(b, a mod b), ending when b hits 0.
    private static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": \"" + actual + "\"   expected \"" + expected + "\"");
    }

    public static void main(String[] args) {
        print("case 1 typical            ", gcdOfStrings("ABCABC", "ABC"), "ABC");
        print("case 2 different repeats  ", gcdOfStrings("ABABAB", "ABAB"), "AB");
        print("case 3 no common unit     ", gcdOfStrings("LEET", "CODE"), "");
        print("case 4 identical          ", gcdOfStrings("AAA", "AAA"), "AAA");
        print("case 5 prefix, not divisor", gcdOfStrings("ABCDEF", "ABC"), "");
    }
}

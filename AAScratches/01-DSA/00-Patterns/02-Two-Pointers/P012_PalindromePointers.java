/*
 * =====================================================================
 *  P012 Two Pointers: Palindrome Check   Canonical LC 125 | Easy
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 125, Valid Palindrome)
 *   After lower-casing letters and removing every character that is not a letter or a
 *   digit, does s read the same forwards and backwards?
 *
 * EXAMPLE
 *   "A man, a plan, a canal: Panama"  ->  true
 *   "race a car"                      ->  false
 *   " "                               ->  true     empty after cleaning
 *
 * RECOGNIZE WHEN
 *   - "palindrome", "reads the same", "mirror", "reverse in place" on a string or array.
 *   - Compare / swap the i-th element from the left with the i-th from the right.
 *   - "at most one deletion / change and still a palindrome".
 *   Not this if: you need the longest palindromic SUBSTRING or count all of them ->
 *   P090_PalindromeDp (expand around centre); fewest deletions in general -> P087_TwoStringDp.
 *
 * TEMPLATE
 *   i = 0, j = n - 1
 *   while i < j:
 *       skip characters that do not count on either side
 *       if s[i] != s[j]: return false        // LC 680: try skipping s[i] OR s[j] once
 *       i++, j--
 *   return true
 *
 * APPROACH
 *   1. Move i forward and j backward past non-alphanumeric characters.
 *   2. Compare lower-cased characters; any mismatch ends it.
 *
 * KEY INSIGHT
 *   A palindrome is defined pairwise from the outside in, so one comparison per pair
 *   decides it. Filtering on the fly avoids building a cleaned copy of the string.
 *
 * COMPLEXITY
 *   Time O(n), space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 680  Valid Palindrome II      on the first mismatch, the rest must be a
 *                                            palindrome after dropping s[i] or s[j]
 *   [coded] LC 345  Reverse Vowels           same walk, but SWAP when both sides are vowels
 *           LC 344  Reverse String           swap every pair
 *           LC 917  Reverse Only Letters     LC 345 with "is a letter" as the filter
 *           LC 1216 Valid Palindrome III     up to k deletions -> DP (LPS >= n - k)
 *           LC 234  Palindrome Linked List   -> P032_InPlaceReversal (reverse second half)
 *
 * PITFALLS
 *   - Both skip loops need their own i < j guard, or a string of symbols runs off the end.
 *   - LC 680: only ONE deletion is allowed; the helper must not delete again.
 *
 * DEEP DIVE
 *   A06_PalindromeSpecial (04-Strings), A04_ReverseVowels (02-Two-Pointers-Sliding-Window)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class PalindromePointers {

    // Canonical LC 125.
    static boolean isPalindrome(String s) {
        int i = 0;
        int j = s.length() - 1;
        while (i < j) {
            while (i < j && !Character.isLetterOrDigit(s.charAt(i))) {
                i++;
            }
            while (i < j && !Character.isLetterOrDigit(s.charAt(j))) {
                j--;
            }
            if (Character.toLowerCase(s.charAt(i)) != Character.toLowerCase(s.charAt(j))) {
                return false;
            }
            i++;
            j--;
        }
        return true;
    }

    // LC 680: one deletion allowed.
    static boolean validPalindrome(String s) {
        int i = 0;
        int j = s.length() - 1;
        while (i < j) {
            if (s.charAt(i) != s.charAt(j)) {
                return isRange(s, i + 1, j) || isRange(s, i, j - 1);
            }
            i++;
            j--;
        }
        return true;
    }

    private static boolean isRange(String s, int i, int j) {
        while (i < j) {
            if (s.charAt(i++) != s.charAt(j--)) {
                return false;
            }
        }
        return true;
    }

    // LC 345: walk inward, stop on vowels, swap.
    static String reverseVowels(String s) {
        char[] c = s.toCharArray();
        int i = 0;
        int j = c.length - 1;
        while (i < j) {
            while (i < j && "aeiouAEIOU".indexOf(c[i]) < 0) {
                i++;
            }
            while (i < j && "aeiouAEIOU".indexOf(c[j]) < 0) {
                j--;
            }
            char t = c[i];
            c[i++] = c[j];
            c[j--] = t;
        }
        return new String(c);
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 125 panama", isPalindrome("A man, a plan, a canal: Panama"), true);
        check("LC 125 race a car", isPalindrome("race a car"), false);
        check("LC 125 single space", isPalindrome(" "), true);
        check("LC 125 0P digit vs letter", isPalindrome("0P"), false);

        check("LC 680 aba", validPalindrome("aba"), true);
        check("LC 680 abca delete one", validPalindrome("abca"), true);
        check("LC 680 abc", validPalindrome("abc"), false);
        check("LC 680 trap needs the right-side skip",
                validPalindrome("cuucu"), true);

        check("LC 345 IceCreAm", reverseVowels("IceCreAm"), "AceCreIm");
        check("LC 345 leetcode", reverseVowels("leetcode"), "leotcede");
        check("LC 345 no vowels", reverseVowels("xyz"), "xyz");
    }
}

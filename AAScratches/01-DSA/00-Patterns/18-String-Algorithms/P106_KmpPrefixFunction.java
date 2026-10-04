/*
 * =====================================================================
 *  P106 KMP and the Prefix Function   Canonical LC 28 | Easy (Medium with KMP)
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 28, Find the Index of the First Occurrence in a String)
 *   Return the index of the first occurrence of needle in haystack, or -1. Do it in
 *   O(n + m), not O(n * m).
 *
 * EXAMPLE
 *   "sadbutsad", "sad"  ->  0
 *   "leetcode",  "leeto" ->  -1
 *   "aaaaab",    "aab"  ->  3      the naive scan restarts too far back here
 *
 * RECOGNIZE WHEN
 *   - Find a pattern in a text in linear time, or count occurrences.
 *   - Questions about a string's own borders: "longest prefix that is also a suffix",
 *     "is s a repetition of a smaller block", "shortest palindrome by adding in front".
 *   Not this if: many patterns at once -> P064_TrieDfsBitTrie; repeated substrings of a
 *   given length -> P107_RollingHash; library indexOf is allowed and fine for the interview.
 *
 * TEMPLATE
 *   lps[i] = length of the longest proper prefix of p[0..i] that is also its suffix
 *   build:  k = 0; for i in 1..m-1:
 *               while k > 0 and p[i] != p[k]: k = lps[k - 1]     // fall back along borders
 *               if p[i] == p[k]: k++
 *               lps[i] = k
 *   search: the same loop over the text with k = matched length; k == m -> found at i - m + 1
 *
 * APPROACH
 *   1. Precompute lps for the pattern.
 *   2. Scan the text once; on a mismatch, keep the longest border of what matched so far
 *      instead of restarting.
 *
 * KEY INSIGHT
 *   After matching k characters, the text's last k characters ARE the pattern's first k.
 *   On a mismatch, the next possible match must start at a border of that prefix, and lps
 *   tells you the longest one, so the text pointer never moves back.
 *
 * COMPLEXITY
 *   Time O(n + m), space O(m).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 459  Repeated Substring       n % (n - lps[n - 1]) == 0 and lps[n - 1] > 0
 *   [coded] LC 214  Shortest Palindrome      lps of s + "#" + reverse(s) gives the longest
 *                                            palindromic prefix; prepend the rest reversed
 *   [coded] LC 1392 Longest Happy Prefix     s.substring(0, lps[n - 1])
 *           LC 686  Repeated String Match    repeat a until long enough, then KMP for b
 *           LC 1764 Form Array by Concatenation  KMP each group in order
 *           Z-function                       z[i] = longest match of s and s[i..]; same uses
 *
 * PITFALLS
 *   - Fall back with k = lps[k - 1] in a WHILE loop, not once.
 *   - lps[0] = 0 always (a proper prefix of one character is empty).
 *   - LC 214: the separator '#' stops the border from crossing between the two halves.
 *
 * DEEP DIVE
 *   D02_SingleLoopSubstringCheckKMP (18-Sorting-Searching-Algorithms)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class KmpPrefixFunction {

    static int[] prefixFunction(String p) {
        int[] lps = new int[p.length()];
        int k = 0;
        for (int i = 1; i < p.length(); i++) {
            while (k > 0 && p.charAt(i) != p.charAt(k)) {
                k = lps[k - 1];
            }
            if (p.charAt(i) == p.charAt(k)) {
                k++;
            }
            lps[i] = k;
        }
        return lps;
    }

    // Canonical LC 28.
    static int strStr(String text, String p) {
        if (p.isEmpty()) {
            return 0;
        }
        int[] lps = prefixFunction(p);
        int k = 0;                                 // characters of p matched so far
        for (int i = 0; i < text.length(); i++) {
            while (k > 0 && text.charAt(i) != p.charAt(k)) {
                k = lps[k - 1];
            }
            if (text.charAt(i) == p.charAt(k)) {
                k++;
            }
            if (k == p.length()) {
                return i - k + 1;
            }
        }
        return -1;
    }

    // LC 459.
    static boolean repeatedSubstringPattern(String s) {
        int n = s.length();
        int border = prefixFunction(s)[n - 1];
        return border > 0 && n % (n - border) == 0;
    }

    // LC 214: add characters in FRONT to make s a palindrome, as few as possible.
    static String shortestPalindrome(String s) {
        String rev = new StringBuilder(s).reverse().toString();
        int[] lps = prefixFunction(s + "#" + rev);
        int keep = lps[lps.length - 1];            // longest palindromic prefix of s
        return rev.substring(0, s.length() - keep) + s;
    }

    // LC 1392: longest proper prefix that is also a suffix.
    static String longestPrefix(String s) {
        return s.substring(0, prefixFunction(s)[s.length() - 1]);
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 28 sadbutsad", strStr("sadbutsad", "sad"), 0);
        check("LC 28 leetcode/leeto", strStr("leetcode", "leeto"), -1);
        check("LC 28 aaaaab/aab", strStr("aaaaab", "aab"), 3);
        check("LC 28 needle at the end", strStr("abcabd", "abd"), 3);

        check("LC 459 abab", repeatedSubstringPattern("abab"), true);
        check("LC 459 aba", repeatedSubstringPattern("aba"), false);
        check("LC 459 abcabcabcabc", repeatedSubstringPattern("abcabcabcabc"), true);
        check("LC 459 aabaaba (border but no repeat)", repeatedSubstringPattern("aabaaba"), false);

        check("LC 214 aacecaaa", shortestPalindrome("aacecaaa"), "aaacecaaa");
        check("LC 214 abcd", shortestPalindrome("abcd"), "dcbabcd");
        check("LC 214 empty", "[" + shortestPalindrome("") + "]", "[]");

        check("LC 1392 level", longestPrefix("level"), "l");
        check("LC 1392 ababab", longestPrefix("ababab"), "abab");
    }
}

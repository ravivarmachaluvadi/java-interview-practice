/*
 * =====================================================================
 *  P090 Palindromic Substrings: Expand Around Centre   Canonical LC 5 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 5, Longest Palindromic Substring)
 *   Return the longest contiguous substring of s that reads the same both ways.
 *
 * EXAMPLE
 *   "babad"  ->  "bab"     ("aba" is also accepted)
 *   "cbbd"   ->  "bb"      even-length centre
 *   "a"      ->  "a"
 *
 * RECOGNIZE WHEN
 *   - Palindromic SUBSTRINGS (contiguous): the longest one, how many there are, fewest cuts
 *     so every piece is a palindrome.
 *   - n up to a few thousand, so O(n^2) is fine.
 *   Not this if: palindromic SUBSEQUENCE (not contiguous) -> P087_TwoStringDp (LC 516);
 *   just checking one string -> P012_PalindromePointers.
 *
 * TEMPLATE
 *   expand(l, r): while l >= 0 and r < n and s[l] == s[r]: l--, r++ ; length = r - l - 1
 *   for centre in 0..n-1: expand(c, c) (odd) and expand(c, c + 1) (even)
 *   table form: pal[i][j] = s[i] == s[j] and (j - i < 2 or pal[i+1][j-1])
 *   min cuts:   cuts[j] = min over palindromic s[i..j] of (i == 0 ? 0 : cuts[i-1] + 1)
 *
 * APPROACH
 *   1. Every palindrome has a centre: a character (odd) or a gap (even), 2n - 1 in total.
 *   2. Expand outward from each centre while the ends match; keep the longest.
 *
 * KEY INSIGHT
 *   A palindrome stays a palindrome when you peel one character from each end, so growing
 *   from the centre checks each candidate in O(1) per step and needs no table. The table
 *   form (pal[i][j]) is the same fact written as DP, and it is what min-cut problems reuse.
 *
 * COMPLEXITY
 *   Time O(n^2), space O(1) for expansion (O(n^2) for the table). Manacher: O(n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 647  Palindromic Substrings   count every successful expansion step
 *   [coded] LC 132  Palindrome Partition II  pal[][] table + cuts[j] min over palindromes
 *                                            ending at j
 *           LC 1312 Min Insertions           n - longest palindromic SUBsequence, via
 *                                            P087_TwoStringDp
 *           LC 131  All Palindrome Partitions -> P081_StringPartitionGenerate
 *           Manacher's algorithm             reuse mirror radii for O(n); rarely required
 *
 * PITFALLS
 *   - Forgetting even-length centres misses "bb".
 *   - Return substring(start, start + len), tracking start from the expansion.
 *   - LC 132: a whole-prefix palindrome needs 0 cuts (the i == 0 case).
 *
 * DEEP DIVE
 *   C12_LongestPalindrome, C15_PalindromePartitioning (12-Dynamic-Programming)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class PalindromeDp {

    // Canonical LC 5.
    static String longestPalindrome(String s) {
        int start = 0;
        int best = 0;
        for (int c = 0; c < s.length(); c++) {
            for (int[] lr : new int[][]{{c, c}, {c, c + 1}}) {
                int l = lr[0];
                int r = lr[1];
                while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
                    l--;
                    r++;
                }
                int len = r - l - 1;
                if (len > best) {
                    best = len;
                    start = l + 1;
                }
            }
        }
        return s.substring(start, start + best);
    }

    // LC 647.
    static int countSubstrings(String s) {
        int count = 0;
        for (int c = 0; c < s.length(); c++) {
            for (int[] lr : new int[][]{{c, c}, {c, c + 1}}) {
                int l = lr[0];
                int r = lr[1];
                while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
                    count++;                       // s[l..r] is one more palindrome
                    l--;
                    r++;
                }
            }
        }
        return count;
    }

    // LC 132: fewest cuts so every piece is a palindrome.
    static int minCut(String s) {
        int n = s.length();
        boolean[][] pal = new boolean[n][n];
        int[] cuts = new int[n];
        for (int j = 0; j < n; j++) {
            cuts[j] = j;                           // worst case: cut before every char
            for (int i = 0; i <= j; i++) {
                if (s.charAt(i) == s.charAt(j) && (j - i < 2 || pal[i + 1][j - 1])) {
                    pal[i][j] = true;
                    cuts[j] = Math.min(cuts[j], i == 0 ? 0 : cuts[i - 1] + 1);
                }
            }
        }
        return cuts[n - 1];
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 5 babad", longestPalindrome("babad"), "bab");
        check("LC 5 cbbd even centre", longestPalindrome("cbbd"), "bb");
        check("LC 5 single", longestPalindrome("a"), "a");
        check("LC 5 forgeeksskeegfor", longestPalindrome("forgeeksskeegfor"), "geeksskeeg");

        check("LC 647 abc", countSubstrings("abc"), 3);
        check("LC 647 aaa", countSubstrings("aaa"), 6);

        check("LC 132 aab", minCut("aab"), 1);
        check("LC 132 a", minCut("a"), 0);
        check("LC 132 ab", minCut("ab"), 1);
        check("LC 132 whole string palindrome", minCut("racecar"), 0);
    }
}

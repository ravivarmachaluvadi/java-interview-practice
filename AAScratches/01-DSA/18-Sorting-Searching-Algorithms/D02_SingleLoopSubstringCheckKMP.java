/*
 * =====================================================================
 *  Substring search in one pass (KMP)            LeetCode 28 | Easy-to-Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given a text of length n and a pattern of length m, decide whether the
 *   pattern occurs as a contiguous substring, and report the first index where
 *   it starts (-1 if it never does). An empty pattern matches at index 0.
 *   The scan must be linear: the text index must never move backwards.
 *
 * EXAMPLE
 *   text = "abxabcabcaby", pattern = "abcaby"  ->  true, index 6
 *   text = "aaaaab",       pattern = "aaab"    ->  true, index 2
 *   text = "abcdef",       pattern = "abd"     ->  false, index -1
 *   text = "ab",           pattern = "abc"     ->  false, index -1 (pattern longer)
 *
 * APPROACH  (KMP: prefix table, then a single forward scan)
 *   1. Build lps[] for the pattern. lps[k] = length of the longest proper
 *      prefix of pattern[0..k] that is also a suffix of it.
 *      "abcaby" -> [0, 0, 0, 1, 2, 0]   ("ab" recurs at index 4)
 *   2. Scan the text with i (text) and j (pattern matched length). On a match,
 *      advance both. When j reaches m, the pattern ended at i - 1.
 *   3. On a mismatch with j > 0, do NOT rewind i. Set j = lps[j - 1]: the first
 *      j characters already matched, so the longest border of that prefix is
 *      still aligned with the text, and comparison resumes from there.
 *   4. On a mismatch with j == 0 there is nothing aligned, so just move i.
 *
 * KEY INSIGHT
 *   The characters already matched are themselves part of the pattern, so the
 *   pattern can tell you how far to slide after a failure - no need to re-read
 *   the text. That is the whole idea: i only ever moves forward, giving O(n+m).
 *   The prefix table is the reusable piece (shortest repeating unit, LeetCode
 *   459 Repeated Substring Pattern, 214 Shortest Palindrome).
 *
 * COMPLEXITY
 *   Time  O(n + m)  i never decreases; j only falls as often as it rose.
 *   Space O(m)      the lps table.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return all occurrences, not just the first (on a hit set j = lps[m - 1]).
 *   - Use lps to find the shortest repeating unit: m - lps[m-1] divides m.
 *   - KMP vs Rabin-Karp (C02_RabinKarpAlgorithm): worst-case guarantee vs
 *     expected-case simplicity.
 *   - What does the naive O(n * m) scan actually cost on "aaaa...a" + "aaab"?
 *
 * RUN
 *   main() runs 5 cases (typical, overlapping prefixes, no match, empty
 *   pattern, pattern longer than text). Every case prints the KMP answer, a
 *   naive cross-check, and the expected value.
 *
 * Fixed: the original "single loop" reset j to 0 and then computed
 *        i = i - j + 1, which is i + 1 because j was ALREADY zero. That threw
 *        away every restart position inside the matched run, so the sample
 *        text/pattern above reported "not found" even though it matches at 6.
 *        Replaced with the real KMP fallback j = lps[j - 1].
 */

import java.util.Arrays;

class SingleLoopSubstringCheckKMP {

    public static boolean isSubstring(String text, String pattern) {
        return indexOf(text, pattern) >= 0;
    }

    // First index where pattern occurs in text, or -1.
    public static int indexOf(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        if (m == 0) return 0;  // empty pattern matches at the start
        if (n < m) return -1;  // cannot fit

        int[] lps = buildLps(pattern);

        int i = 0; // text index - never moves backwards
        int j = 0; // how many characters of the pattern currently match
        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) return i - m; // whole pattern matched, report its start
            } else if (j > 0) {
                // Slide the pattern so its longest border lines up; i stays put.
                j = lps[j - 1];
            } else {
                i++; // nothing matched yet, just move along the text
            }
        }
        return -1;
    }

    // lps[k] = length of the longest proper prefix of pattern[0..k] that is
    // also a suffix of pattern[0..k].
    static int[] buildLps(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0; // length of the current border
        for (int i = 1; i < m; i++) {
            while (len > 0 && pattern.charAt(i) != pattern.charAt(len)) {
                len = lps[len - 1]; // fall back to the next shorter border
            }
            if (pattern.charAt(i) == pattern.charAt(len)) len++;
            lps[i] = len;
        }
        return lps;
    }

    // O(n * m) reference used only to cross-check KMP in main().
    static int indexOfNaive(String text, String pattern) {
        int n = text.length(), m = pattern.length();
        for (int start = 0; start + m <= n; start++) {
            int k = 0;
            while (k < m && text.charAt(start + k) == pattern.charAt(k)) k++;
            if (k == m) return start;
        }
        return -1;
    }

    private static void print(String label, String text, String pattern, int expected) {
        System.out.println(label + " text=\"" + text + "\", pattern=\"" + pattern
                + "\" -> kmp " + indexOf(text, pattern)
                + " (contains " + isSubstring(text, pattern) + ")"
                + ", naive " + indexOfNaive(text, pattern)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        System.out.println("lps(\"abcaby\") = " + Arrays.toString(buildLps("abcaby"))
                + "   expected [0, 0, 0, 1, 2, 0]");

        print("case 1 (typical)  ", "abxabcabcaby", "abcaby", 6);
        print("case 2 (overlap)  ", "aaaaab", "aaab", 2);
        print("case 3 (no match) ", "abcdef", "abd", -1);
        print("case 4 (empty pat)", "abcdef", "", 0);
        print("case 5 (pat > txt)", "ab", "abc", -1);
    }
}

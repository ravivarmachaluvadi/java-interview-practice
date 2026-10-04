/*
 * =====================================================================
 *  P088 Pattern Matching DP: Wildcard and Regex   Canonical LC 44 | Hard
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 44, Wildcard Matching)
 *   '?' matches any single character and '*' matches any sequence (including empty).
 *   Return whether pattern p matches the WHOLE string s.
 *
 * EXAMPLE
 *   "aa",    "a"     ->  false
 *   "aa",    "*"     ->  true
 *   "adceb", "*a*b"  ->  true
 *   "acdcb", "a*c?b" ->  false
 *
 * RECOGNIZE WHEN
 *   - A string against a PATTERN with special symbols: '?', '*', '.', 'x*' (zero or more of
 *     the previous char).
 *   - The match must cover the entire string.
 *   Not this if: plain substring search -> P106_KmpPrefixFunction; two ordinary strings
 *   -> P087_TwoStringDp.
 *
 * TEMPLATE
 *   dp[i][j] = does s[0..i) match p[0..j)?    dp[0][0] = true
 *   empty string vs pattern: dp[0][j] is true only while p[0..j) can vanish ('*' / 'x*')
 *   wildcard '*':  dp[i][j] = dp[i][j-1] (match empty) or dp[i-1][j] (eat one more char)
 *   regex 'x*':    dp[i][j] = dp[i][j-2] (zero x) or (x matches s[i-1] and dp[i-1][j])
 *   single char:   dp[i][j] = matches(s[i-1], p[j-1]) and dp[i-1][j-1]
 *
 * APPROACH
 *   1. Fill the first row: an empty string matches a pattern of only '*'s.
 *   2. For '*': either it matches nothing (look left) or it absorbs s[i-1] (look up).
 *   3. For '?' or a letter: the last characters must match and the prefixes too.
 *
 * KEY INSIGHT
 *   A '*' is a choice between "consume nothing" and "consume one more character and stay
 *   on the same star", which is exactly two neighbouring cells of the table. Regex 'x*'
 *   is the same choice, but it is anchored to the character before it.
 *
 * COMPLEXITY
 *   Time O(m * n), space O(m * n) (O(n) with rolling rows).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 10   Regular Expression       '.' any char, 'x*' zero or more x; look two
 *                                            pattern positions back for "zero x"
 *           Wildcard greedy                  two pointers with a remembered star position,
 *                                            O(m * n) worst case but O(1) space
 *           LC 1023 Camelcase Matching       two pointers, lowercase letters may be skipped
 *
 * PITFALLS
 *   - Regex: 'x*' can match zero x's, so "c*a*b" matches "ab" via dp[i][j-2].
 *   - Wildcard first row: only a run of '*'s can match the empty string.
 *   - Recursion without memo is exponential on "aaaa...b" vs "a*a*a*...".
 *
 * DEEP DIVE
 *   D03_RegularExpressionMatchingMemo (12-Dynamic-Programming)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class WildcardRegexDp {

    // Canonical LC 44.
    static boolean isMatchWildcard(String s, String p) {
        int m = s.length();
        int n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;
        for (int j = 1; j <= n && p.charAt(j - 1) == '*'; j++) {
            dp[0][j] = true;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char pc = p.charAt(j - 1);
                if (pc == '*') {
                    dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
                } else {
                    dp[i][j] = (pc == '?' || pc == s.charAt(i - 1)) && dp[i - 1][j - 1];
                }
            }
        }
        return dp[m][n];
    }

    // LC 10.
    static boolean isMatchRegex(String s, String p) {
        int m = s.length();
        int n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;
        for (int j = 2; j <= n; j++) {
            dp[0][j] = p.charAt(j - 1) == '*' && dp[0][j - 2];   // "a*b*" can vanish
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char pc = p.charAt(j - 1);
                if (pc == '*') {
                    char prev = p.charAt(j - 2);
                    boolean zero = dp[i][j - 2];
                    boolean more = (prev == '.' || prev == s.charAt(i - 1)) && dp[i - 1][j];
                    dp[i][j] = zero || more;
                } else {
                    dp[i][j] = (pc == '.' || pc == s.charAt(i - 1)) && dp[i - 1][j - 1];
                }
            }
        }
        return dp[m][n];
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 44 aa vs a", isMatchWildcard("aa", "a"), false);
        check("LC 44 aa vs *", isMatchWildcard("aa", "*"), true);
        check("LC 44 cb vs ?a", isMatchWildcard("cb", "?a"), false);
        check("LC 44 adceb vs *a*b", isMatchWildcard("adceb", "*a*b"), true);
        check("LC 44 acdcb vs a*c?b", isMatchWildcard("acdcb", "a*c?b"), false);
        check("LC 44 empty vs ***", isMatchWildcard("", "***"), true);

        check("LC 10 aa vs a", isMatchRegex("aa", "a"), false);
        check("LC 10 aa vs a*", isMatchRegex("aa", "a*"), true);
        check("LC 10 ab vs .*", isMatchRegex("ab", ".*"), true);
        check("LC 10 aab vs c*a*b", isMatchRegex("aab", "c*a*b"), true);
        check("LC 10 mississippi", isMatchRegex("mississippi", "mis*is*p*."), false);
        check("LC 10 empty vs a*b*", isMatchRegex("", "a*b*"), true);
    }
}

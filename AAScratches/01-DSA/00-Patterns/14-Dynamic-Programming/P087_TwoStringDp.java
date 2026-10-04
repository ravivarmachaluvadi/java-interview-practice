/*
 * =====================================================================
 *  P087 Two-String DP (LCS Family)   Canonical LC 1143 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 1143, Longest Common Subsequence)
 *   Return the length of the longest subsequence present in both text1 and text2.
 *
 * EXAMPLE
 *   "abcde", "ace"  ->  3     "ace"
 *   "abc",   "abc"  ->  3
 *   "abc",   "def"  ->  0
 *
 * RECOGNIZE WHEN
 *   - TWO strings (or arrays) compared character by character: common subsequence, edit
 *     distance, deletions to make them equal, how many times one occurs in the other,
 *     interleaving.
 *   - One string against its own REVERSE (longest palindromic subsequence).
 *   Not this if: substrings must be contiguous -> a different table (reset to 0 on a
 *   mismatch) or a sliding window; pattern with wildcards -> P088_WildcardRegexDp.
 *
 * TEMPLATE
 *   dp[i][j] = answer for prefixes a[0..i) and b[0..j)       // (m + 1) x (n + 1)
 *   base row / column: empty prefix
 *   if a[i-1] == b[j-1]: dp[i][j] = f(dp[i-1][j-1])           // use the match
 *   else:                dp[i][j] = g(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])
 *   LCS:  match -> dp[i-1][j-1] + 1;  else max(dp[i-1][j], dp[i][j-1])
 *   edit: match -> dp[i-1][j-1];      else 1 + min(delete, insert, replace)
 *
 * APPROACH
 *   1. dp[i][j] = LCS of the first i chars of text1 and first j chars of text2.
 *   2. Equal last chars: they pair up, so 1 + dp[i-1][j-1].
 *   3. Otherwise drop the last char of one string or the other and take the better.
 *
 * KEY INSIGHT
 *   Every two-string problem asks the same question about the LAST characters: do they
 *   match, and if not, which one do we drop / change? The table over prefix pairs answers
 *   each pair once. Only the previous row is needed, so space can drop to O(n).
 *
 * COMPLEXITY
 *   Time O(m * n), space O(m * n) (O(min(m, n)) with rolling rows).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 72   Edit Distance            1 + min(insert, delete, replace) on a mismatch
 *   [coded] LC 583  Delete Operation         m + n - 2 * LCS
 *   [coded] LC 115  Distinct Subsequences    count: dp[i-1][j-1] (use it) + dp[i-1][j] (skip)
 *   [coded] LC 516  Longest Palindromic Subseq  LCS of s and reverse(s)
 *           LC 1092 Shortest Common Supersequence  m + n - LCS; rebuild by walking the table
 *           LC 97   Interleaving String      dp[i][j] = can a[0..i) and b[0..j) form c[0..i+j)
 *           LC 712  Min ASCII Delete Sum     LCS weighted by character codes
 *           LC 1035 Uncrossed Lines          exactly LCS on integers
 *           LC 718  Max Length Repeated Subarray  contiguous: dp = prev + 1 or 0
 *
 * PITFALLS
 *   - Off by one: dp is (m + 1) x (n + 1) and compares a[i - 1], b[j - 1].
 *   - Edit distance base cases: dp[i][0] = i, dp[0][j] = j (all deletes / inserts).
 *   - LC 115 counts can overflow int on big inputs; LeetCode promises they fit.
 *
 * DEEP DIVE
 *   A07_CommonSubSequence, C11_EditDistance, D02_CountOfDistinctSubsequences,
 *   C10_LongestPalindromicSubsequence (12-Dynamic-Programming)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class TwoStringDp {

    // Canonical LC 1143.
    static int longestCommonSubsequence(String a, String b) {
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                dp[i][j] = a.charAt(i - 1) == b.charAt(j - 1)
                        ? dp[i - 1][j - 1] + 1
                        : Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        return dp[m][n];
    }

    // LC 72.
    static int minDistance(String a, String b) {
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;                          // delete everything
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;                          // insert everything
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[m][n];
    }

    // LC 583: delete from either string until they are equal.
    static int minDeletions(String a, String b) {
        return a.length() + b.length() - 2 * longestCommonSubsequence(a, b);
    }

    // LC 115: how many subsequences of s equal t.
    static int numDistinct(String s, String t) {
        int m = s.length();
        int n = t.length();
        long[][] dp = new long[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            dp[i][0] = 1;                          // the empty t appears once in any prefix
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                dp[i][j] = dp[i - 1][j];           // do not use s[i - 1]
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] += dp[i - 1][j - 1];  // use it to match t[j - 1]
                }
            }
        }
        return (int) dp[m][n];
    }

    // LC 516.
    static int longestPalindromeSubseq(String s) {
        return longestCommonSubsequence(s, new StringBuilder(s).reverse().toString());
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 1143 abcde/ace", longestCommonSubsequence("abcde", "ace"), 3);
        check("LC 1143 abc/abc", longestCommonSubsequence("abc", "abc"), 3);
        check("LC 1143 abc/def", longestCommonSubsequence("abc", "def"), 0);

        check("LC 72 horse/ros", minDistance("horse", "ros"), 3);
        check("LC 72 intention/execution", minDistance("intention", "execution"), 5);
        check("LC 72 empty/a", minDistance("", "a"), 1);

        check("LC 583 sea/eat", minDeletions("sea", "eat"), 2);
        check("LC 583 leetcode/etco", minDeletions("leetcode", "etco"), 4);

        check("LC 115 rabbbit/rabbit", numDistinct("rabbbit", "rabbit"), 3);
        check("LC 115 babgbag/bag", numDistinct("babgbag", "bag"), 5);

        check("LC 516 bbbab", longestPalindromeSubseq("bbbab"), 4);
        check("LC 516 cbbd", longestPalindromeSubseq("cbbd"), 2);
    }
}

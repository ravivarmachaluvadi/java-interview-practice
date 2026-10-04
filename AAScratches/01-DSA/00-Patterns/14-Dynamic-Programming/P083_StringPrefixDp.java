/*
 * =====================================================================
 *  P083 DP over String Prefixes: Segment and Decode   Canonical LC 139 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 139, Word Break)
 *   Return whether s can be split into a sequence of dictionary words (words may repeat).
 *
 * EXAMPLE
 *   "leetcode",      ["leet","code"]                    ->  true
 *   "applepenapple", ["apple","pen"]                    ->  true
 *   "catsandog",     ["cats","dog","sand","and","cat"]  ->  false
 *
 * RECOGNIZE WHEN
 *   - "can the string be split / decoded / segmented", "how many ways to decode",
 *     "fewest pieces", where each piece must be valid on its own.
 *   - The answer for prefix s[0..i) depends on answers for shorter prefixes plus one last
 *     valid piece.
 *   Not this if: you must LIST every split -> P081_StringPartitionGenerate (backtracking);
 *   pieces must be palindromes and you want the min cuts -> P090_PalindromeDp.
 *
 * TEMPLATE
 *   dp[0] = base (true / 1 / 0)                   // the empty prefix
 *   for i in 1..n:
 *       for each possible last piece s[j..i) (j from i - maxLen to i - 1):
 *           if valid(s[j..i)): dp[i] = combine(dp[i], dp[j])   // or / sum / min + 1
 *   answer = dp[n]
 *
 * APPROACH
 *   1. dp[i] = can s[0..i) be split?
 *   2. dp[i] is true if some j < i has dp[j] true and s[j..i) is a word.
 *   3. Only check j within the longest word length of i.
 *
 * KEY INSIGHT
 *   Ask "what is the LAST piece?" instead of "what is the first?". The prefix before it was
 *   already solved, so every prefix is computed once: O(n * maxWordLength) checks instead
 *   of exponentially many splits.
 *
 * COMPLEXITY
 *   Time O(n * L) substring checks (L = longest word), space O(n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 91   Decode Ways              count: dp[i] += dp[i-1] if s[i-1] != '0';
 *                                            dp[i] += dp[i-2] if s[i-2..i) is 10..26
 *           LC 140  Word Break II            list all splits: backtracking + this dp as memo
 *           LC 639  Decode Ways II           '*' wildcards multiply the cases
 *           LC 472  Concatenated Words       LC 139 per word against the shorter words
 *           LC 2707 Extra Characters         dp[i] = min(dp[i-1] + 1, dp[j] if word)
 *
 * PITFALLS
 *   - dp has n + 1 entries; dp[0] is the empty prefix.
 *   - LC 91: '0' alone is not a letter; "06" is not 6.
 *   - Repeated substring() calls are O(L) each; fine here, but say it.
 *
 * DEEP DIVE
 *   C01_NumDecodings (12-Dynamic-Programming)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class StringPrefixDp {

    // Canonical LC 139.
    static boolean wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        int maxLen = 0;
        for (String w : wordDict) {
            maxLen = Math.max(maxLen, w.length());
        }
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;
        for (int i = 1; i <= s.length(); i++) {
            for (int j = Math.max(0, i - maxLen); j < i && !dp[i]; j++) {
                dp[i] = dp[j] && words.contains(s.substring(j, i));
            }
        }
        return dp[s.length()];
    }

    // LC 91: '1' -> A ... '26' -> Z; count the decodings.
    static int numDecodings(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];
        dp[0] = 1;
        for (int i = 1; i <= n; i++) {
            if (s.charAt(i - 1) != '0') {
                dp[i] += dp[i - 1];                // last piece is one digit
            }
            if (i >= 2) {
                int two = Integer.parseInt(s.substring(i - 2, i));
                if (two >= 10 && two <= 26) {
                    dp[i] += dp[i - 2];            // last piece is two digits
                }
            }
        }
        return dp[n];
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 139 leetcode", wordBreak("leetcode", List.of("leet", "code")), true);
        check("LC 139 applepenapple", wordBreak("applepenapple", List.of("apple", "pen")), true);
        check("LC 139 catsandog",
                wordBreak("catsandog", List.of("cats", "dog", "sand", "and", "cat")), false);
        check("LC 139 needs the later split", wordBreak("aaab", List.of("a", "aab")), true);

        check("LC 91 12", numDecodings("12"), 2);
        check("LC 91 226", numDecodings("226"), 3);
        check("LC 91 06 leading zero", numDecodings("06"), 0);
        check("LC 91 10", numDecodings("10"), 1);
        check("LC 91 2101", numDecodings("2101"), 1);
    }
}

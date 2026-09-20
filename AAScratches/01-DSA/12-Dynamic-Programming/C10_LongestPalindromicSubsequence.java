/*
 * =====================================================================
 *  Longest Palindromic Subsequence                 LeetCode 516 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a string s, return the length of the longest subsequence of s that
 *   reads the same forwards and backwards. A subsequence keeps the original
 *   order but may drop any characters, so it need not be contiguous.
 *
 * EXAMPLE
 *   "bbbab"  ->  4    "bbbb" (drop the 'a')
 *   "cbbd"   ->  2    "bb" "abcde"  ->  1    no character repeats, so any single letter is the best
 *   ""       ->  0    edge case main() runs
 *
 * APPROACH  (interval recursion on [start, end], then memo, then LCS view)
 *   1. Look at the two ends of the window s[start..end].
 *   2. If they are equal, both can sit on the outside of the palindrome:
 *      answer = 2 + solve(start + 1, end - 1).
 *   3. If they differ, at most one of them can survive, so try dropping each:
 *      answer = max(solve(start + 1, end), solve(start, end - 1)).
 *   4. Bases: start > end -> 0 (empty window), start == end -> 1 (one char).
 *   5. The windows repeat, so memo[start][end] turns the exponential tree into
 *      O(n^2) states. lpsViaLcsWithReverse() shows the second framing.
 *
 * KEY INSIGHT
 *   A palindromic subsequence of s is exactly a common subsequence of s and
 *   its reverse, so LPS(s) == LCS(s, reverse(s)). Recognise this whenever a
 *   problem asks for "same read either way": either shrink a window from both
 *   ends, or reverse the input and reuse the LCS grid you already own.
 *
 * COMPLEXITY
 *   Naive recursion  Time O(2^n)   every mismatch forks into two branches
 *   Memoised         Time O(n^2)   one result per (start, end) window
 *                    Space O(n^2)  the memo table plus O(n) recursion depth
 *   LCS with reverse Time O(n^2)   Space O(n^2) for the (i, j) grid
 *
 * INTERVIEW FOLLOW-UPS
 *   - Print the palindrome itself, not just its length (walk the table back).
 *   - Longest palindromic SUBSTRING instead: contiguous, so expand-around-centre.
 *   - Minimum deletions to make s a palindrome = s.length() - LPS(s).
 *   - Reduce space to O(n) by keeping only two rows of the bottom-up table.
 *
 * RUN
 *   main() runs 4 cases (typical, no-repeat, empty, longer) and prints the
 *   naive, memoised and LCS answers against the expected value on one line.
 */
class LongestPalindromicSubsequence {

    /** Author's original approach: plain interval recursion, no memo. */
    public static int findLongestPalindromicSubsequence(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        return findLPS(str, 0, str.length() - 1);
    }

    private static int findLPS(String str, int start, int end) {
        if (start > end) {
            return 0; // empty window
        }
        if (start == end) {
            return 1; // a single character is a palindrome of length 1
        }
        if (str.charAt(start) == str.charAt(end)) {
            // both ends survive and wrap whatever is best inside
            return 2 + findLPS(str, start + 1, end - 1);
        }
        // ends differ, so at most one of them can be kept
        return Math.max(findLPS(str, start + 1, end), findLPS(str, start, end - 1));
    }

    /** Same recursion with a memo table: O(n^2) instead of O(2^n). */
    public static int lpsMemo(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        int n = str.length();
        int[][] memo = new int[n][n];
        for (int[] row : memo) {
            java.util.Arrays.fill(row, -1); // -1 means "window not solved yet"
        }
        return lpsMemo(str, 0, n - 1, memo);
    }

    private static int lpsMemo(String str, int start, int end, int[][] memo) {
        if (start > end) {
            return 0;
        }
        if (start == end) {
            return 1;
        }
        if (memo[start][end] != -1) {
            return memo[start][end];
        }
        int best;
        if (str.charAt(start) == str.charAt(end)) {
            best = 2 + lpsMemo(str, start + 1, end - 1, memo);
        } else {
            best = Math.max(lpsMemo(str, start + 1, end, memo),
                            lpsMemo(str, start, end - 1, memo));
        }
        memo[start][end] = best;
        return best;
    }

    /** Second framing worth saying out loud: LPS(s) == LCS(s, reverse(s)). */
    public static int lpsViaLcsWithReverse(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        String reversed = new StringBuilder(str).reverse().toString();
        return longestCommonSubsequence(str, reversed);
    }

    private static int longestCommonSubsequence(String a, String b) {
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m + 1][n + 1]; // dp[i][j] = LCS of a[0..i) and b[0..j)
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = 1 + dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

    private static void print(String label, String input, int expected) {
        int naive = findLongestPalindromicSubsequence(input);
        int memo = lpsMemo(input);
        int viaLcs = lpsViaLcsWithReverse(input);
        System.out.println(label + " \"" + input + "\" -> naive " + naive
                + ", memo " + memo + ", viaLCS " + viaLcs + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (typical):  ", "bbbab", 4);   // "bbbb"
        print("case 2 (no repeat):", "abcde", 1);   // any single letter
        print("case 3 (empty):    ", "", 0);        // edge case
        print("case 4 (tricky):   ", "agbdba", 5);  // "abdba"
    }
}

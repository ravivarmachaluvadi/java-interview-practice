/**
 * Problem: Longest Common Subsequence (LCS) of two strings - LeetCode 1143.
 *
 * Approaches (same problem, four ways, ordered from brute force to optimal):
 *   1. allLcsByBacktracking  - DFS over every common subsequence; collects ALL distinct
 *                              LCS strings, not just the length.           O(2^(m+n)) time
 *   2. lcsRecursive          - plain top-down recursion, length only.      O(2^(m+n)) time
 *   3. lcsMemoized           - same recursion + 2D memo table.             O(m*n) time / O(m*n) space
 *   4. lcsTabulation         - bottom-up 2D dp table, length only.         O(m*n) time / O(m*n) space
 *      reconstructLcs        - walks the dp table backwards to print one actual LCS string.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class CommonSubSequence {

    // ------------------------------------------------------------------
    // Approach 1: backtracking - enumerate every common subsequence.
    // Only approach here that answers "how many distinct LCS are there?"
    // and "what are they?". Exponential, so only for tiny inputs.
    // ------------------------------------------------------------------

    /** Returns all distinct LCS strings. Length of LCS = length of any element (0 if empty set). */
    static Set<String> allLcsByBacktracking(String s1, String s2) {
        Set<String> lcsSet = new HashSet<>();
        int[] maxLength = {0}; // single-element array so the recursion can update it
        backtrack(s1, s2, 0, 0, new ArrayList<>(), lcsSet, maxLength);
        return lcsSet;
    }

    private static void backtrack(String s1, String s2, int i, int j,
                                  List<Character> path, Set<String> lcsSet, int[] maxLength) {
        // Base case: reached end of either string -> path is one complete common subsequence
        if (i == s1.length() || j == s2.length()) {
            int length = path.size();
            if (length > maxLength[0]) {          // longer than anything so far: reset the set
                maxLength[0] = length;
                lcsSet.clear();
                if (length > 0) lcsSet.add(listToString(path));
            } else if (length == maxLength[0] && length > 0) { // ties: add as another distinct LCS
                lcsSet.add(listToString(path));
            }
            return;
        }

        // If characters match, include it in the subsequence
        if (s1.charAt(i) == s2.charAt(j)) {
            path.add(s1.charAt(i));
            backtrack(s1, s2, i + 1, j + 1, path, lcsSet, maxLength);
            path.remove(path.size() - 1);       // undo choice
        }

        backtrack(s1, s2, i + 1, j, path, lcsSet, maxLength); // skip character from s1
        backtrack(s1, s2, i, j + 1, path, lcsSet, maxLength); // skip character from s2
    }

    private static String listToString(List<Character> list) {
        StringBuilder sb = new StringBuilder();
        for (char c : list) sb.append(c);
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // Approach 2: plain recursion (top-down), length only.
    // f(i, j) = LCS of s1[0..i] and s2[0..j]
    //   match    -> 1 + f(i-1, j-1)
    //   mismatch -> max(f(i, j-1), f(i-1, j))
    // ------------------------------------------------------------------

    static int lcsRecursive(String s1, String s2) {
        return recurse(s1, s2, s1.length() - 1, s2.length() - 1);
    }

    private static int recurse(String s1, String s2, int ind1, int ind2) {
        if (ind1 < 0 || ind2 < 0) return 0;   // one string exhausted

        if (s1.charAt(ind1) == s2.charAt(ind2))
            return 1 + recurse(s1, s2, ind1 - 1, ind2 - 1);

        return Math.max(recurse(s1, s2, ind1, ind2 - 1), recurse(s1, s2, ind1 - 1, ind2));
    }

    // ------------------------------------------------------------------
    // Approach 3: recursion + memo. Same recurrence, each (i, j) solved once.
    // ------------------------------------------------------------------

    static int lcsMemoized(String s1, String s2) {
        int[][] memo = new int[s1.length()][s2.length()];
        for (int[] row : memo) Arrays.fill(row, -1);   // -1 = not computed yet
        return memoRecurse(s1, s2, s1.length() - 1, s2.length() - 1, memo);
    }

    private static int memoRecurse(String s1, String s2, int ind1, int ind2, int[][] memo) {
        if (ind1 < 0 || ind2 < 0) return 0;
        if (memo[ind1][ind2] != -1) return memo[ind1][ind2];

        if (s1.charAt(ind1) == s2.charAt(ind2))
            return memo[ind1][ind2] = 1 + memoRecurse(s1, s2, ind1 - 1, ind2 - 1, memo);

        return memo[ind1][ind2] = Math.max(memoRecurse(s1, s2, ind1, ind2 - 1, memo),
                                           memoRecurse(s1, s2, ind1 - 1, ind2, memo));
    }

    // ------------------------------------------------------------------
    // Approach 4: bottom-up tabulation. dp[i][j] = LCS of first i chars of
    // text1 and first j chars of text2 (1-based so row/col 0 is the empty prefix).
    //
    // text1 = "abcde", text2 = "ace":
    // | i\j   | 0 | 1 (a) | 2 (c) | 3 (e) |
    // | ----- | - | ----- | ----- | ----- |
    // | 0     | 0 | 0     | 0     | 0     |
    // | 1 (a) | 0 | 1     | 1     | 1     |
    // | 2 (b) | 0 | 1     | 1     | 1     |
    // | 3 (c) | 0 | 1     | 2     | 2     |
    // | 4 (d) | 0 | 1     | 2     | 2     |
    // | 5 (e) | 0 | 1     | 2     | 3     |   <- answer dp[m][n] = 3
    // ------------------------------------------------------------------

    static int lcsTabulation(String text1, String text2) {
        return buildDpTable(text1, text2)[text1.length()][text2.length()];
    }

    private static int[][] buildDpTable(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] dp = new int[m + 1][n + 1];   // row 0 / col 0 stay 0 (empty prefix)

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {   // dp index i maps to char i-1
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp;
    }

    /** Walk the dp table from the bottom-right corner back to (0,0) to recover one LCS string. */
    static String reconstructLcs(String text1, String text2) {
        int[][] dp = buildDpTable(text1, text2);
        StringBuilder sb = new StringBuilder();
        int i = text1.length(), j = text2.length();
        while (i > 0 && j > 0) {
            if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                sb.append(text1.charAt(i - 1));          // this char is part of the LCS
                i--; j--;
            } else if (dp[i - 1][j] >= dp[i][j - 1]) {
                i--;                                      // value came from the row above
            } else {
                j--;                                      // value came from the column to the left
            }
        }
        return sb.reverse().toString();                   // built backwards, so reverse
    }

    // ------------------------------------------------------------------

    public static void main(String[] args) {
        String[][] tests = {
                {"abcde", "ace"},     // LCS "ace"  -> 3
                {"abcdef", "ace"},    // LCS "ace"  -> 3
                {"acd", "ced"},       // LCS "cd"   -> 2
                {"abc", "abc"},       // identical  -> 3
                {"abc", "def"},       // nothing in common -> 0
                {"abcbdab", "bdcaba"} // classic CLRS example: several distinct LCS of length 4
        };

        for (String[] t : tests) {
            String s1 = t[0], s2 = t[1];
            System.out.println("s1 = \"" + s1 + "\", s2 = \"" + s2 + "\"");
            Set<String> all = allLcsByBacktracking(s1, s2);
            System.out.println("  backtracking : all distinct LCS = " + all
                    + " (count " + all.size() + ")");
            System.out.println("  recursive    : length = " + lcsRecursive(s1, s2));
            System.out.println("  memoized     : length = " + lcsMemoized(s1, s2));
            System.out.println("  tabulation   : length = " + lcsTabulation(s1, s2)
                    + ", one LCS = \"" + reconstructLcs(s1, s2) + "\"");
        }
    }
}

/*
 * =====================================================================
 *  Longest Common Subsequence                          LeetCode 1143 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given two strings, return the length of their longest common subsequence. A subsequence
 *   keeps the original order but may drop characters; it does not have to be contiguous.
 *   If there is no common subsequence the answer is 0.
 *
 * EXAMPLE
 *   "abcde", "ace"        ->  3   the LCS is "ace"
 *   "abc",   "def"        ->  0   nothing in common
 *   "",      "abc"        ->  0   edge case: one string empty
 *   "abcbdab", "bdcaba"   ->  4   three distinct LCS: "bcab", "bdab", "bcba"
 *
 * APPROACH  (two-pointer recursion over two strings, then the same thing cached and tabulated)
 *   1. Define f(i, j) = LCS of s1[0..i] and s2[0..j], walking from the BACK of both strings.
 *   2. Either index below 0 means one string is exhausted, so the answer is 0.
 *   3. If s1[i] == s2[j] the characters must pair up: 1 + f(i - 1, j - 1).
 *   4. Otherwise drop one character from one string: max(f(i, j - 1), f(i - 1, j)).
 *   5. Only (i, j) varies, so cache it in a 2-D memo, or fill the same table bottom-up with
 *      1-based indices where row 0 and column 0 are the empty prefix.
 *   6. To recover the string itself, walk the finished table backwards from (m, n): on a match
 *      emit the character and move diagonally, else follow whichever neighbour holds the value.
 *   Approach 1 (backtracking) answers a different question - EVERY distinct LCS, not just the
 *   length - so it is kept for tiny inputs and is exponential by design.
 *
 * KEY INSIGHT
 *   On a match the two characters are consumed together and there is never a reason to pair
 *   them with anything else; on a mismatch at least one of the two characters is useless, so
 *   try dropping each. That single "match -> diagonal, mismatch -> max of two neighbours" grid
 *   is the same table behind edit distance, distinct subsequences and regex matching.
 *
 * COMPLEXITY
 *   Time  O(m * n)   each (i, j) state is solved once
 *   Space O(m * n)   the table, plus O(m + n) recursion stack for the memoised version
 *   lcsRecursive and allLcsByBacktracking are O(2^(m+n)) - tiny inputs only.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Reduce the table to two rows, or one row, for O(min(m, n)) space.
 *   - Print one LCS (done here) versus counting how many distinct LCS exist.
 *   - Longest common SUBSTRING: same table, but a mismatch resets the cell to 0.
 *   - Shortest common supersequence = m + n - LCS; minimum insertions/deletions to convert
 *     s1 into s2 = (m - LCS) + (n - LCS).
 *
 * RUN
 *   main() runs 7 cases (typical, identical, disjoint, empty, multiple distinct LCS) and
 *   prints all four approaches against the expected length.
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

        // If characters match, try including the pair
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
        // checked first, so memo is never indexed at -1
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

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + " -> " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // {s1, s2, expected LCS length, expected number of DISTINCT LCS strings}
        Object[][] tests = {
                {"abcde", "ace", 3, 1},        // typical
                {"abcdef", "ace", 3, 1},
                {"acd", "ced", 2, 1},
                {"abc", "abc", 3, 1},          // identical strings
                {"abc", "def", 0, 0},          // nothing in common
                {"", "abc", 0, 0},             // edge case: empty string
                {"abcbdab", "bdcaba", 4, 3}    // CLRS example: several distinct LCS of length 4
        };

        for (int c = 0; c < tests.length; c++) {
            String s1 = (String) tests[c][0], s2 = (String) tests[c][1];
            int expectedLen = (Integer) tests[c][2], expectedCount = (Integer) tests[c][3];

            System.out.println("case " + (c + 1) + ": s1 = \"" + s1 + "\", s2 = \"" + s2 + "\"");
            Set<String> all = allLcsByBacktracking(s1, s2);
            print("  backtracking distinct LCS " + all + ", count", all.size(), expectedCount);
            print("  recursive length ", lcsRecursive(s1, s2), expectedLen);
            print("  memoized  length ", lcsMemoized(s1, s2), expectedLen);
            String one = reconstructLcs(s1, s2);
            print("  tabulation length", lcsTabulation(s1, s2), expectedLen);
            print("  one LCS \"" + one + "\", its length", one.length(), expectedLen);
        }
    }
}

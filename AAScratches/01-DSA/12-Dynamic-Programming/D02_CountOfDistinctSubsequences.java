/*
 * =====================================================================
 *  Distinct Subsequences                          LeetCode 115 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given two strings s and t, count how many distinct subsequences of s equal t.
 *   A subsequence keeps the original order but may drop any characters. Two
 *   subsequences are distinct if they use a different set of positions in s, even
 *   when they spell the same string. The answer is returned modulo 1e9+7.
 *
 * EXAMPLE
 *   s = "babgbag", t = "bag"    ->  5   five different position sets spell "bag"
 *   s = "rabbbit", t = "rabbit" ->  3   the three b's give three ways
 *   s = "abc",     t = ""       ->  1   edge: the empty subsequence, matched once
 *   s = "a",       t = "aa"     ->  0   edge: t is longer than s, impossible
 *
 * APPROACH  (counting on the LCS-style (i, j) grid, scanned right to left)
 *   1. State (ind1, ind2) = number of ways the prefix s[0..ind1] can spell the
 *      prefix t[0..ind2]. Both indices walk backwards.
 *   2. Base case ind2 < 0: all of t has been matched, so this is one complete way -> 1.
 *      Check this BEFORE the ind1 < 0 case, otherwise running out of s at the exact
 *      moment t finishes would wrongly score 0.
 *   3. Base case ind1 < 0 (with t left over): s is exhausted, impossible -> 0.
 *   4. If s[ind1] == t[ind2] the character offers a genuine choice, and both branches
 *      count: use this position (ind1-1, ind2-1) PLUS skip it and match t[ind2] with an
 *      earlier copy (ind1-1, ind2). Add the two.
 *   5. If they differ, s[ind1] is useless here: skip it, (ind1-1, ind2).
 *   6. Memoise on (ind1, ind2). Plain and memoised versions are both below.
 *
 * KEY INSIGHT
 *   This is the LCS grid with the transition changed from max to SUM. The one line that
 *   makes it a counting problem is that a match adds both branches instead of choosing
 *   between them - because "use this 'b'" and "use a later 'b'" are two different
 *   subsequences, not two routes to the same answer. Whenever a problem asks "how many
 *   ways" instead of "what is the best", expect max/min to become +, and expect a
 *   modulus in the signature.
 *
 * COMPLEXITY
 *   Plain recursion: Time O(2^n) - the skip branch re-explores the same suffixes.
 *   Memoised:  Time  O(n * m)  every (ind1, ind2) pair solved once, O(1) work each.
 *              Space O(n * m)  the memo table, plus O(n + m) recursion depth.
 *   n = s.length(), m = t.length().
 *
 * INTERVIEW FOLLOW-UPS
 *   - Convert to bottom-up, then to a single rolling 1D array iterated right to left.
 *   - Why does the answer need a modulus at all? (It grows like C(n, m).)
 *   - Contrast with LC 392 Is Subsequence: same grid, but existence instead of count.
 *   - What changes if subsequences that SPELL the same string must be counted once?
 *
 * RUN
 *   main() runs 4 cases (two typical, empty-t edge, impossible edge) through both the
 *   plain and the memoised solver and prints actual vs expected.
 */

import java.util.Arrays;

class CountOfDistinctSubsequences {

    private static final int MOD = (int) (1e9 + 7);

    /* ---------- 1. Plain recursion (the author's version) ---------- */

    private int countUtil(String s1, String s2, int ind1, int ind2) {
        // all of s2 matched -> one complete way. Must be tested before the ind1 check.
        if (ind2 < 0) return 1;

        // s1 exhausted while s2 still has characters left -> impossible
        if (ind1 < 0) return 0;

        if (s1.charAt(ind1) == s2.charAt(ind2)) {
            int useThisPosition = countUtil(s1, s2, ind1 - 1, ind2 - 1);
            int skipThisPosition = countUtil(s1, s2, ind1 - 1, ind2);
            return (useThisPosition + skipThisPosition) % MOD; // a match counts BOTH
        }
        return countUtil(s1, s2, ind1 - 1, ind2); // mismatch: this character is useless
    }

    public int distinctSubsequences(String s, String t) {
        return countUtil(s, t, s.length() - 1, t.length() - 1);
    }

    /* ---------- 2. Same recursion, memoised on (ind1, ind2) ---------- */

    public int distinctSubsequencesMemo(String s, String t) {
        int n = s.length();
        int m = t.length();
        if (m == 0) return 1; // guards the zero-sized memo table below

        int[][] memo = new int[n][m];
        for (int[] row : memo) Arrays.fill(row, -1); // -1 marks "not computed yet"

        return countMemo(s, t, n - 1, m - 1, memo);
    }

    private int countMemo(String s1, String s2, int ind1, int ind2, int[][] memo) {
        if (ind2 < 0) return 1;
        if (ind1 < 0) return 0;
        if (memo[ind1][ind2] != -1) return memo[ind1][ind2];

        int result;
        if (s1.charAt(ind1) == s2.charAt(ind2)) {
            result = (countMemo(s1, s2, ind1 - 1, ind2 - 1, memo)
                    + countMemo(s1, s2, ind1 - 1, ind2, memo)) % MOD;
        } else {
            result = countMemo(s1, s2, ind1 - 1, ind2, memo);
        }
        return memo[ind1][ind2] = result;
    }

    /* ---------- 3. Driver ---------- */

    private static void check(CountOfDistinctSubsequences sol, String s, String t, int expected) {
        System.out.println("s = \"" + s + "\", t = \"" + t + "\""
                + "   recursion = " + sol.distinctSubsequences(s, t)
                + "   memo = " + sol.distinctSubsequencesMemo(s, t)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        CountOfDistinctSubsequences sol = new CountOfDistinctSubsequences();

        check(sol, "babgbag", "bag", 5);    // typical
        check(sol, "rabbbit", "rabbit", 3); // typical: repeated character gives the count
        check(sol, "abc", "", 1);           // edge: empty target matches exactly once
        check(sol, "a", "aa", 0);           // edge: target longer than the source
    }
}

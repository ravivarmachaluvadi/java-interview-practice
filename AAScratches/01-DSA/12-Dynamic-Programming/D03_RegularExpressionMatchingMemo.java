/*
 * =====================================================================
 *  Regular Expression Matching                    LeetCode 10 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given an input string s and a pattern p, decide whether p matches the ENTIRE
 *   string s. The pattern supports two special characters: '.' matches any single
 *   character, and '*' matches zero or more of the element immediately before it.
 *   A '*' never appears first in the pattern, so p.charAt(j+1) is the only place to
 *   look for it.
 *
 * EXAMPLE
 *   s = "aab",         p = "c*a*b"      ->  true   "c*" takes zero c's, "a*" takes two
 *   s = "mississippi", p = "mis*is*p*." ->  false  "p*." cannot cover "ppi"
 *   s = "ab",          p = ".*"         ->  true   ".*" is any run of any characters
 *   s = "aaa",         p = "a*a"        ->  true   "a*" must give one 'a' back
 *   s = "",            p = "a*"         ->  true   edge: '*' can consume nothing
 *   s = "aa",          p = "a"          ->  false  edge: the match must be complete
 *
 * APPROACH  (two-pointer (i, j) grid over string and pattern, top-down with memo)
 *   1. State (i, j) = does s[i..] match p[j..]? Both pointers only move forward.
 *   2. Base case j == p.length(): the pattern is used up, so this matches only if s is
 *      used up too. That is why the answer is (i == s.length()), not plain true.
 *   3. firstMatch = i is in range AND (s[i] == p[j] or p[j] == '.').
 *   4. If p[j+1] is '*', the pair p[j..j+1] branches two ways:
 *        zero occurrences  -> skip the pair entirely, (i, j + 2)
 *        one more occurrence -> only if firstMatch, consume one char, (i + 1, j)
 *      Note j stays put in the second branch: the same '*' may fire again.
 *   5. Otherwise consume one character from each side: firstMatch && (i + 1, j + 1).
 *   6. The same (i, j) is reached along many branch paths, so memoise on it.
 *
 * KEY INSIGHT
 *   Always look AHEAD for the '*', never at the current pattern character. A '*' is not
 *   a token of its own - it modifies the character before it, so the real unit of the
 *   pattern is the pair p[j..j+1]. Once you treat that pair as one decision with two
 *   outcomes (drop the pair, or eat one character of s and stay), the whole problem
 *   collapses into the same (i, j) grid as LCS and edit distance.
 *
 * GOTCHAS
 *   - Checking i < s.length() inside firstMatch is what stops the '*' branch from
 *     reading past the end of s when the pattern still has "x*" groups to burn.
 *   - "zero occurrences" must be tried even when firstMatch is true: "aaa" vs "a*a"
 *     fails if '*' greedily eats everything.
 *   - Without the memo this is exponential on inputs like "aaaaaaaaaab" vs "a*a*a*a*b".
 *
 * COMPLEXITY
 *   Time  O(n * m)  each (i, j) state is computed once and does O(1) work.
 *   Space O(n * m)  the memo map, plus O(n + m) recursion depth.
 *   n = s.length(), m = p.length().
 *
 * INTERVIEW FOLLOW-UPS
 *   - Rewrite bottom-up: boolean[n+1][m+1] filled from the bottom-right corner.
 *   - Swap the string memo key for a Boolean[n+1][m+1]: same states, no string building.
 *   - Wildcard Matching (LC 44) where '*' is standalone and '?' is one character -
 *     what changes in the transition?
 *   - Support '+' or a bounded {2,4} repeat on top of this recursion.
 *
 * RUN
 *   main() runs 6 cases (typical, failing, greedy trap, empty string, incomplete match)
 *   and prints actual vs expected.
 */

import java.util.HashMap;
import java.util.Map;

class RegularExpressionMatchingMemo {

    public static boolean isMatch(String s, String p) {
        Map<String, Boolean> memo = new HashMap<>();
        return dp(0, 0, s, p, memo);
    }

    /** Does s[i..] match p[j..] ? */
    private static boolean dp(int i, int j, String s, String p, Map<String, Boolean> memo) {
        String key = i + "," + j;
        if (memo.containsKey(key)) return memo.get(key);

        // Pattern exhausted: this is a match only if the string is exhausted too.
        if (j == p.length()) {
            boolean ans = (i == s.length());
            memo.put(key, ans);
            return ans;
        }

        // Do the current characters line up? The i < s.length() guard is essential:
        // the pattern may still have "x*" groups left after s has run out.
        boolean firstMatch = (i < s.length()
                && (s.charAt(i) == p.charAt(j) || p.charAt(j) == '.'));

        boolean ans;
        if (j + 1 < p.length() && p.charAt(j + 1) == '*') {
            // p[j..j+1] is an "x*" group: drop it, or eat one char and keep the group
            ans = dp(i, j + 2, s, p, memo)                      // zero occurrences
                    || (firstMatch && dp(i + 1, j, s, p, memo)); // one more occurrence
        } else {
            ans = firstMatch && dp(i + 1, j + 1, s, p, memo);   // plain single character
        }

        memo.put(key, ans);
        return ans;
    }

    private static void check(String s, String p, boolean expected) {
        System.out.println("s = \"" + s + "\", p = \"" + p + "\""
                + "   actual = " + isMatch(s, p)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("aab", "c*a*b", true);              // typical: two '*' groups
        check("mississippi", "mis*is*p*.", false); // typical: no match
        check("ab", ".*", true);                   // '.' combined with '*'
        check("aaa", "a*a", true);                 // tricky: '*' must not be greedy
        check("", "a*", true);                     // edge: '*' consuming nothing
        check("aa", "a", false);                   // edge: partial match is not a match
    }
}

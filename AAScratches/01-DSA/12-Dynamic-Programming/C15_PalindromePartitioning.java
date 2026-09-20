/*
 * =====================================================================
 *  Palindrome Partitioning                        LeetCode 131 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a string s, cut it into pieces so that every piece is a palindrome,
 *   and return all such cuttings. Pieces must be contiguous and must cover the
 *   whole string, so the answer is a list of lists of substrings.
 *
 * EXAMPLE
 *   "aab"  ->  [[a, a, b], [aa, b]]
 *   "a"    ->  [[a]]                            (edge case main() runs)
 *   "aaa"  ->  [[a, a, a], [a, aa], [aa, a], [aaa]]
 *
 * APPROACH  (front partition + backtracking)
 *   1. Stand at index. Everything before it is already cut into palindromes.
 *   2. Try every end position i from index to the last character. The candidate
 *      piece is s[index..i].
 *   3. If that piece is a palindrome, push it onto the current path and recurse
 *      from i + 1; otherwise skip this end position entirely.
 *   4. When index reaches s.length() the path is a complete valid cutting, so
 *      copy it into the result list (copy, because path keeps mutating).
 *   5. After returning, pop the piece off the path - that is the backtrack step.
 *   6. partitionFast() is the same walk with an isPal[i][j] table precomputed
 *      in O(n^2), so each palindrome test costs O(1) instead of O(n).
 *
 * KEY INSIGHT
 *   "Cut the string at every valid position and recurse on the rest" is the
 *   front-partition pattern. Here we enumerate every cutting; interval DP
 *   (Palindrome Partitioning II, Matrix Chain Multiplication) keeps the exact
 *   same loop but memoises a single best value per suffix instead of listing
 *   the paths. Enumerating cannot be memoised - the output itself is huge.
 *
 * COMPLEXITY
 *   Time  O(n * 2^n)  up to 2^(n-1) cuttings, each costing O(n) to copy; the
 *                     palindrome tests add O(n) each without the table
 *   Space O(n)        recursion depth and the path, excluding the output list
 *
 * INTERVIEW FOLLOW-UPS
 *   - Palindrome Partitioning II: minimum cuts only - that is 1D DP, not a walk.
 *   - Count the partitions instead of listing them - still exponential, no memo.
 *   - Why can this not be memoised like LCS? The answer set, not the count, is
 *     what is asked for, and it is exponential in size.
 *   - Word Break II is the same skeleton with "is in dictionary" as the test.
 *
 * RUN
 *   main() runs 3 cases (typical, single character, all equal) and prints the
 *   plain and table-backed results against the expected list.
 */

import java.util.*;

class PalindromePartitioning {

    /** Author's approach: backtracking with an O(n) palindrome check. */
    public static List<List<String>> partition(String s) {
        List<List<String>> result = new ArrayList<>();
        backtrack(0, s, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(int index, String s, List<String> path,
                                  List<List<String>> result) {
        if (index == s.length()) {
            // Every character is consumed, so path is one complete cutting.
            // Copy it: path is mutated on the way back up.
            result.add(new ArrayList<>(path));
            return;
        }
        for (int end = index; end < s.length(); end++) {
            if (isPalindrome(s, index, end)) {
                path.add(s.substring(index, end + 1));
                backtrack(end + 1, s, path, result);
                path.remove(path.size() - 1); // backtrack
            }
        }
    }

    private static boolean isPalindrome(String s, int start, int end) {
        while (start < end) {
            if (s.charAt(start) != s.charAt(end)) {
                return false;
            }
            start++;
            end--;
        }
        return true;
    }

    /**
     * Same enumeration, but isPal[i][j] is filled first so each test is O(1).
     * Build it by growing the window: s[i..j] is a palindrome when the ends
     * match and the inside s[i+1..j-1] already is.
     */
    public static List<List<String>> partitionFast(String s) {
        int n = s.length();
        boolean[][] isPal = new boolean[n][n];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                isPal[i][j] = s.charAt(i) == s.charAt(j)
                        && (j - i < 2 || isPal[i + 1][j - 1]);
            }
        }
        List<List<String>> result = new ArrayList<>();
        backtrackFast(0, s, isPal, new ArrayList<>(), result);
        return result;
    }

    private static void backtrackFast(int index, String s, boolean[][] isPal,
                                      List<String> path, List<List<String>> result) {
        if (index == s.length()) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int end = index; end < s.length(); end++) {
            if (isPal[index][end]) {
                path.add(s.substring(index, end + 1));
                backtrackFast(end + 1, s, isPal, path, result);
                path.remove(path.size() - 1);
            }
        }
    }

    private static void print(String label, String s, String expected) {
        System.out.println(label + " \"" + s + "\" -> " + partition(s)
                + ", fast " + partitionFast(s) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (typical):  ", "aab", "[[a, a, b], [aa, b]]");
        print("case 2 (one char): ", "a", "[[a]]");
        print("case 3 (all equal):", "aaa", "[[a, a, a], [a, aa], [aa, a], [aaa]]");
    }
}

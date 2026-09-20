/*
 * =====================================================================
 *  Is Subsequence                             LeetCode 392 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given strings s and t, return true if s is a subsequence of t: all of s's
 *   characters appear in t in the same relative order, not necessarily
 *   adjacent. The empty string is a subsequence of anything.
 *
 * EXAMPLE
 *   s = "abc", t = "ahbgdc"  ->  true    a..b..c in order
 *   s = "axc", t = "ahbgdc"  ->  false   no x after the a
 *   s = "",    t = "abc"     ->  true    empty s
 *   s = "abc", t = ""        ->  false   nothing left to match
 *
 * APPROACH  (Two pointers, two strings)
 *   Method 1, isSubsequence (explicit pointers):
 *   1. i walks s, j walks t.
 *   2. If s[i] == t[j], both advance (matched); otherwise only j advances.
 *   3. s is a subsequence exactly when i reaches the end of s.
 *
 *   Method 2, isSubsequenceIndexOf (the author's original):
 *   1. For each character of s, find its next occurrence in t starting from
 *      the position just after the previous match (t.indexOf(ch, from)).
 *   2. Any miss means false; otherwise true. indexOf IS the j pointer,
 *      just hidden inside the library call.
 *
 * KEY INSIGHT
 *   Greedy matching is safe: taking the EARLIEST possible occurrence of each
 *   s character leaves the most of t available for the rest. Two pointers
 *   over two sequences moving at different rates (one only on a match, one
 *   always) is the simplest form of the pattern.
 *
 * COMPLEXITY
 *   Time  O(|t|)  j never moves backwards
 *   Space O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Many queries s against one huge t (LeetCode 392 follow-up): precompute
 *     next[pos][char] for t, then each query is O(|s| log |t|) or O(|s|).
 *   - Count how many strings in a list are subsequences of t (LeetCode 792).
 *   - Longest common subsequence is NOT this: order-only matching needs DP.
 *
 * RUN
 *   main() runs 4 cases (typical true, typical false, empty s, empty t)
 *   through both methods and prints actual vs expected.
 */
class IsSubsequence {

    // Explicit two-pointer walk.
    public static boolean isSubsequence(String s, String t) {
        int i = 0; // position in s (advances only on a match)
        int j = 0; // position in t (always advances)
        while (i < s.length() && j < t.length()) {
            if (s.charAt(i) == t.charAt(j)) {
                i++;
            }
            j++;
        }
        return i == s.length();
    }

    // Same walk; indexOf(ch, from) does the j-pointer scan.
    public static boolean isSubsequenceIndexOf(String s, String t) {
        int from = 0;
        for (char ch : s.toCharArray()) {
            int index = t.indexOf(ch, from);
            if (index == -1) {
                return false;
            }
            from = index + 1; // next search starts after this match
        }
        return true;
    }

    private static void print(String label, String s, String t, boolean expected) {
        System.out.println(label + " pointers=" + isSubsequence(s, t)
                + " indexOf=" + isSubsequenceIndexOf(s, t) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (\"abc\" in \"ahbgdc\"):", "abc", "ahbgdc", true);
        print("case 2 (\"axc\" in \"ahbgdc\"):", "axc", "ahbgdc", false);
        print("case 3 (empty s):           ", "", "abc", true);
        print("case 4 (empty t):           ", "abc", "", false);
    }
}

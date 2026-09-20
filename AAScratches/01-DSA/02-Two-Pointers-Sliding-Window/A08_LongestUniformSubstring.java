/*
 * =====================================================================
 *  Longest Uniform Substring                  Classic | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a string, find the longest run of one repeated character. Return
 *   {startIndex, length}. For an empty string return {-1, 0}. If two runs tie,
 *   return the one that appears first.
 *
 * EXAMPLE
 *   "10000111"      ->  [1, 4]    the run "0000" starts at index 1
 *   "aaabbbbbCdAA"  ->  [3, 5]    the run "bbbbb" starts at index 3
 *   ""              ->  [-1, 0]   empty input
 *   "abab"          ->  [0, 1]    all runs tie at 1, the first wins
 *
 * APPROACH  (Run-length scan with best-so-far)
 *   1. Start with the first character as a run of length 1 and as the best.
 *   2. For each i from 1: if s[i] equals s[i-1], the current run grows by one;
 *      otherwise the current run resets to 1.
 *   3. Whenever the current run is strictly longer than the best, record the
 *      new best length and its end index i.
 *   4. Start of the best run = endIndex - bestLength + 1.
 *
 * KEY INSIGHT
 *   Keep two counters: "current stretch" and "best stretch seen". Grow the
 *   current one while the condition holds, reset it when it breaks, and
 *   compare to the best after every step. This is the minimal shape of every
 *   longest-window problem; the harder ones only change what "condition
 *   holds" means. Using strict > for the comparison is what makes ties resolve
 *   to the earliest run.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass, one comparison per character
 *   Space O(1)  three ints
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the substring itself instead of indices.
 *   - Longest run allowing up to K mismatches (leads to at-most-K windows).
 *   - Run-length encode the whole string (same loop, emit on every reset).
 *
 * RUN
 *   main() runs 4 cases (typical, typical, empty, all-ties) and prints
 *   actual vs expected.
 */
import java.util.Arrays;

class LongestUniformSubstring {

    static int[] longestUniformSubstring(String s) {
        if (s.isEmpty()) {
            return new int[]{-1, 0};
        }

        int bestLength = 1;
        int bestEnd = 0;       // index of the last char of the best run
        int currentLength = 1; // length of the run ending at i

        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == s.charAt(i - 1)) {
                currentLength++;
                if (currentLength > bestLength) { // strict: first run wins ties
                    bestLength = currentLength;
                    bestEnd = i;
                }
            } else {
                currentLength = 1;
            }
        }
        return new int[]{bestEnd - bestLength + 1, bestLength};
    }

    private static void print(String label, int[] actual, int[] expected) {
        System.out.println(label + Arrays.toString(actual)
                + "   expected " + Arrays.toString(expected));
    }

    public static void main(String[] args) {
        String s1 = "10000111", s2 = "aaabbbbbCdAA", s3 = "", s4 = "abab";
        print("case 1 (\"10000111\"):     ", longestUniformSubstring(s1), new int[]{1, 4});
        print("case 2 (\"aaabbbbbCdAA\"): ", longestUniformSubstring(s2), new int[]{3, 5});
        print("case 3 (empty):           ", longestUniformSubstring(s3), new int[]{-1, 0});
        print("case 4 (\"abab\" ties):    ", longestUniformSubstring(s4), new int[]{0, 1});
    }
}

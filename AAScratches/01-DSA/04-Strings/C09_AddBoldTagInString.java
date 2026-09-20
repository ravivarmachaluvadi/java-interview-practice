/*
 * =====================================================================
 *  Add Bold Tag in String                              LeetCode 616 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a string s and a list of words, wrap every substring of s that matches any word
 *   in <b> and </b>. Matches may overlap or touch; overlapping and adjacent bold ranges
 *   must be merged into a single pair of tags. Return the tagged string.
 *
 * EXAMPLE
 *   s = "abcxyz123", words = ["abc","123"]  ->  "<b>abc</b>xyz<b>123</b>"
 *   s = "aaabbb",    words = ["aa","b"]     ->  "<b>aaabbb</b>"   (overlaps and touches merge)
 *   s = "abcabcxyz123", words = ["abc","xyz","123"]  ->  "<b>abcabcxyz123</b>"
 *   s = "abc",       words = ["x"]          ->  "abc"             (no match: unchanged)
 *   s = "",          words = ["a"]          ->  ""                (empty input)
 *
 * APPROACH  (boolean mark array, then merge intervals)
 *   1. Allocate boolean[] bold, one flag per character of s.
 *   2. For each word, find every occurrence with s.indexOf(word, start), and mark the
 *      covered range [start, start + word.length()) as true. Advance start by 1, not by
 *      the word length, so overlapping occurrences (e.g. "aa" in "aaa") are all found.
 *   3. Walk s once more. Open a <b> when a bold char is preceded by a non-bold char (or is
 *      first), close with </b> when a bold char is followed by a non-bold char (or is last).
 *
 * KEY INSIGHT
 *   Do not try to insert tags while searching: overlapping matches would produce nested or
 *   broken tags. Separate "what is covered" (a flag per index) from "where the tag
 *   boundaries are" (transitions in the flag array). The flag array is a merged-intervals
 *   structure in disguise: each run of true is one merged interval. The same trick works
 *   for any "highlight all matches" or "union of ranges" question on a bounded index space.
 *
 * COMPLEXITY
 *   Time  O(n * sum(|w|))   each indexOf scan is O(n * |w|) worst case, run once per word,
 *                           plus O(n) for the tag pass
 *   Space O(n)              the flag array and the output
 *
 * INTERVIEW FOLLOW-UPS
 *   - Same as Merge Intervals (LC 56): collect [start, end) pairs, sort, merge, then tag.
 *   - Many words? Build a Trie of the words and scan s once, marking from each position.
 *   - Why advance start by 1 and not by word.length()? Show the "aa" in "aaaa" case.
 *   - Flag array too big for a huge s? Use a difference array or a sorted interval list.
 *
 * RUN
 *   main() runs 5 cases (typical, edge, tricky) and prints actual vs expected.
 */
import java.util.Arrays;
import java.util.List;

class AddBoldTagInString {

    public static String addBoldTag(String s, List<String> dict) {
        boolean[] bold = new boolean[s.length()];

        for (String word : dict) {
            // Guard: indexOf("", k) always returns k, so an empty word would loop forever.
            // LeetCode guarantees non-empty words; this keeps a scratch run from hanging.
            if (word.isEmpty()) {
                continue;
            }
            int start = 0;
            while ((start = s.indexOf(word, start)) != -1) {
                Arrays.fill(bold, start, start + word.length(), true);
                start += 1;   // step by 1 so overlapping occurrences are marked too
            }
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            boolean startsRun = bold[i] && (i == 0 || !bold[i - 1]);
            boolean endsRun = bold[i] && (i == s.length() - 1 || !bold[i + 1]);
            if (startsRun) {
                result.append("<b>");
            }
            result.append(s.charAt(i));
            if (endsRun) {
                result.append("</b>");
            }
        }

        return result.toString();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical       ", addBoldTag("abcxyz123", Arrays.asList("abc", "123")),
                "<b>abc</b>xyz<b>123</b>");
        print("case 2 overlap merge ", addBoldTag("aaabbb", Arrays.asList("aa", "b")),
                "<b>aaabbb</b>");
        print("case 3 touching runs ",
                addBoldTag("abcabcxyz123", Arrays.asList("abc", "xyz", "123")),
                "<b>abcabcxyz123</b>");
        print("case 4 no match      ", addBoldTag("abc", Arrays.asList("x")), "abc");
        print("case 5 empty input   ", addBoldTag("", Arrays.asList("a")), "");
    }
}

/*
 * =====================================================================
 *  Longest Common Prefix                          LeetCode 14 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of strings, return the longest string that is a prefix of every
 *   element. If there is no common prefix (or the array is empty) return "".
 *
 * EXAMPLE
 *   ["flower", "flow", "flight"]  ->  "fl"
 *   ["dog", "racecar", "car"]     ->  ""       no shared first letter
 *   ["alone"]                     ->  "alone"  single element is its own prefix
 *   []                            ->  ""
 *
 * APPROACH  (Shrinking prefix across the array)
 *   1. Start with prefix = strs[0]; it is the best we could possibly do.
 *   2. For every later string, while it does not START with prefix, chop the last
 *      character off prefix. indexOf(prefix) == 0 is the "starts with" test.
 *   3. If prefix ever becomes empty, stop early and return "".
 *   4. Whatever survives every string is the answer.
 *
 * KEY INSIGHT
 *   The common prefix can only get shorter as you see more strings, never longer.
 *   So keep one candidate and trim it; you never need to rebuild it. The pattern to
 *   recognise: "answer shared by all inputs" -> start from one input and shrink.
 *
 * COMPLEXITY
 *   Time  O(S)  where S = total characters; each char of the prefix is trimmed at
 *               most once per string, and each indexOf costs O(len of prefix)
 *   Space O(1)  besides the returned substring
 *
 * INTERVIEW FOLLOW-UPS
 *   - Vertical scan: compare column by column across all strings; stops at the
 *     first mismatch and never touches characters past it.
 *   - Sort the array, then the LCP of the whole array is the LCP of first and last.
 *   - Trie: insert all words, walk down while every node has exactly one child.
 *   - Binary search on the prefix length when strings are very long.
 *
 * RUN
 *   main() runs 4 cases (typical, no prefix, single, empty) and prints actual vs expected.
 */
class LongestCommonPrefix {

    public static String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) return "";
        String prefix = strs[0];

        for (int i = 1; i < strs.length; i++) {
            // indexOf(prefix) == 0 means strs[i] starts with prefix; otherwise trim
            // one char from the end and test again.
            while (strs[i].indexOf(prefix) != 0) {
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty()) return "";
            }
        }
        return prefix;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": \"" + actual + "\"   expected \"" + expected + "\"");
    }

    public static void main(String[] args) {
        print("case 1 typical",
                longestCommonPrefix(new String[]{"flower", "flow", "flight"}), "fl");
        print("case 2 no prefix", longestCommonPrefix(new String[]{"dog", "racecar", "car"}), "");
        print("case 3 single", longestCommonPrefix(new String[]{"alone"}), "alone");
        print("case 4 empty", longestCommonPrefix(new String[]{}), "");
    }
}

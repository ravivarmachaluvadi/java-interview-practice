/*
 * =====================================================================
 *  Largest Merge of Two Strings                        LeetCode 1754 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given two strings word1 and word2, build a merge by repeatedly taking the first
 *   character of either non-empty word and appending it to the result, until both are
 *   empty. Return the lexicographically largest merge that can be produced this way.
 *
 * EXAMPLE
 *   word1 = "cabaa",  word2 = "bcaaa"    ->  "cbcabaaaaa"
 *   word1 = "abcabc", word2 = "abdcaba"  ->  "abdcabcabcaba"
 *   word1 = "a",      word2 = ""         ->  "a"           (one side empty: copy the other)
 *   word1 = "aaa",    word2 = "aaa"      ->  "aaaaaa"      (all ties: order does not matter)
 *   word1 = "a",      word2 = "ab"       ->  "aba"         (next chars tie; suffix decides)
 *
 * APPROACH  (greedy lexicographic suffix compare)
 *   1. Keep pointers i into word1 and j into word2, and a StringBuilder for the result.
 *   2. While both have characters left, compare the REMAINING SUFFIXES word1[i..] and
 *      word2[j..], not just the next characters.
 *   3. Take the first character of the larger suffix and advance that pointer. On an
 *      exact tie either choice is fine (the suffixes are identical), so take from word2.
 *   4. When one word runs out, append the rest of the other one unchanged.
 *
 * KEY INSIGHT
 *   Comparing only the next character is wrong on ties. With word1 = "a", word2 = "ab",
 *   both start with 'a'; if you take word1's 'a' first you are forced to finish with
 *   "ab" and get "aab", but taking from the larger suffix "ab" first gives "aba", which
 *   is bigger. The larger remaining suffix should always go first, because its
 *   characters will be laid down ahead of the other word's. This is the alternate-merge
 *   skeleton from MergeStringsAlternately (B14) with the fixed turn order replaced by a
 *   suffix comparison. Recognise it as a greedy exchange argument on suffixes.
 *
 * COMPLEXITY
 *   Time  O((n + m) * max(n, m))   each of the n + m steps does a suffix compare that can
 *                                  scan up to max(n, m) characters
 *   Space O(n + m)                 the result; substring() also allocates per step
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is comparing just the next character insufficient? Give the failing input.
 *   - Avoid allocating substrings: compare char-by-char with a helper (same big-O).
 *   - Bring the suffix compare to O(1): suffix array or Z-function on word1 + '#' + word2.
 *   - Contrast with Merge Strings Alternately (LC 1768), where the order is fixed.
 *
 * RUN
 *   main() runs 5 cases (typical, edge, tricky) and prints actual vs expected.
 */
class LargestMergeOfTwoStrings {

    public static String largestMerge(String word1, String word2) {
        int i = 0, j = 0;
        int n = word1.length(), m = word2.length();
        StringBuilder sb = new StringBuilder();

        while (i < n && j < m) {
            // Compare the remaining suffixes, not just the next characters: when the next
            // characters tie, the word whose suffix is larger must go first.
            if (word1.substring(i).compareTo(word2.substring(j)) > 0) {
                sb.append(word1.charAt(i));
                i++;
            } else {
                sb.append(word2.charAt(j));
                j++;
            }
        }

        // One word is exhausted: the other's leftover goes on the end unchanged.
        if (i < n) {
            sb.append(word1.substring(i));
        }
        if (j < m) {
            sb.append(word2.substring(j));
        }

        return sb.toString();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical       ", largestMerge("cabaa", "bcaaa"), "cbcabaaaaa");
        print("case 2 leetcode ex 2 ", largestMerge("abcabc", "abdcaba"), "abdcabcabcaba");
        print("case 3 one empty     ", largestMerge("a", ""), "a");
        print("case 4 all ties      ", largestMerge("aaa", "aaa"), "aaaaaa");
        print("case 5 suffix decides", largestMerge("a", "ab"), "aba");
    }
}

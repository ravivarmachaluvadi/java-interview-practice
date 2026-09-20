/*
 * =====================================================================
 *  Merge Strings Alternately                          LeetCode 1768 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given word1 and word2, build a string by taking one character from word1,
 *   then one from word2, and so on, starting with word1. When one word runs
 *   out, append the rest of the other word unchanged.
 *
 * EXAMPLE
 *   "abc",  "pqr"   ->  "apbqcr"   equal lengths, pure alternation
 *   "ab",   "pqrs"  ->  "apbqrs"   word2 leftover "rs" appended
 *   "abcd", "pq"    ->  "apbqcd"   word1 leftover "cd" appended
 *   "",     "xyz"   ->  "xyz"      empty word1, whole word2 is leftover
 *   "",     ""      ->  ""         both empty
 *
 * APPROACH  (two-pointer merge with leftovers)
 *   1. Keep an index i into word1 and j into word2, both starting at 0.
 *   2. While both indices are in range, append word1[i], then word2[j],
 *      advancing each.
 *   3. Append whatever remains of word1 (only one of the two tails is
 *      non-empty, but writing both loops keeps the code symmetric).
 *   4. Append whatever remains of word2.
 *
 * KEY INSIGHT
 *   This is the merge step of merge sort with "alternate" in place of
 *   "pick the smaller": a shared loop while both sides have input, then drain
 *   the tails. The exact skeleton returns in C08 LargestMerge, where step 2
 *   becomes a greedy choice instead of strict alternation.
 *
 * COMPLEXITY
 *   Time  O(n + m)  every character of both words is appended once
 *   Space O(n + m)  the output builder (no extra working memory)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Merge k strings alternately: round-robin over an array of indices.
 *   - Largest lexicographic merge (LC 1754): compare remaining suffixes and
 *     take from the larger one.
 *   - Can it be done in one loop? Yes: loop to max(n, m) and append each side
 *     only when its index is in range.
 *
 * RUN
 *   main() runs 5 cases (typical, edge, tricky) and prints actual vs expected.
 */
class MergeStringsAlternately {

    public static String mergeAlternately(String word1, String word2) {
        StringBuilder result = new StringBuilder();
        int i = 0, j = 0;

        // alternate while both words still have characters
        while (i < word1.length() && j < word2.length()) {
            result.append(word1.charAt(i++));
            result.append(word2.charAt(j++));
        }

        // at most one of these tails is non-empty
        while (i < word1.length()) {
            result.append(word1.charAt(i++));
        }
        while (j < word2.length()) {
            result.append(word2.charAt(j++));
        }
        return result.toString();
    }

    private static void print(String label, Object actual, Object expected) {
        // quote both sides so an empty result is visible
        System.out.println(label + ": \"" + actual + "\"   expected \"" + expected + "\"");
    }

    public static void main(String[] args) {
        print("case 1 \"abc\" + \"pqr\"", mergeAlternately("abc", "pqr"), "apbqcr");
        print("case 2 \"ab\" + \"pqrs\"", mergeAlternately("ab", "pqrs"), "apbqrs");
        print("case 3 \"abcd\" + \"pq\"", mergeAlternately("abcd", "pq"), "apbqcd");
        print("case 4 \"\" + \"xyz\" (empty word1)", mergeAlternately("", "xyz"), "xyz");
        print("case 5 \"\" + \"\" (both empty)", mergeAlternately("", ""), "");
    }
}

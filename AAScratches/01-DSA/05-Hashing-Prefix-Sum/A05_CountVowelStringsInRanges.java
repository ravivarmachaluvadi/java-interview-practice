/*
 * =====================================================================
 *  Count Vowel Strings in Ranges              LeetCode 2559 | Medium    MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of lowercase words and a list of queries [l, r] (inclusive),
 *   return for each query how many words in words[l..r] both start and end with a
 *   vowel. Word count and query count can each be up to 1e5, so answering each
 *   query by rescanning the range is too slow.
 *
 * EXAMPLE
 *   words = [aba, bcb, ece, aa, e], queries = [[0,2],[1,4],[1,1]]  ->  [2, 3, 0]
 *   words = [a],                    queries = [[0,0]]              ->  [1]
 *   words = [bcd, xyz],             queries = [[0,1]]              ->  [0]
 *
 * APPROACH  (Prefix sum array, range queries)
 *   1. Map each word to 1 if it starts and ends with a vowel, else 0.
 *   2. Build prefix[] of length n + 1 where prefix[i] = number of vowel words among
 *      the first i words. prefix[0] = 0, prefix[i + 1] = prefix[i] + flag(words[i]).
 *   3. Answer query [l, r] in O(1) as prefix[r + 1] - prefix[l].
 *
 * KEY INSIGHT
 *   Precompute once, answer many. Any "how many / what sum in range [l, r]" question
 *   over a static array is prefix[r + 1] - prefix[l]. The extra leading 0 (n + 1
 *   slots) is what makes l = 0 work without a special case. Recognise the shape:
 *   static data + many range queries = prefix sum.
 *
 * COMPLEXITY
 *   Time  O(n + q)  one pass to build prefix, O(1) per query
 *   Space O(n)      the prefix array (plus O(q) for the output)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Words can be updated between queries? Prefix sums go stale; use a Fenwick tree
 *     or segment tree for O(log n) updates and queries.
 *   - Range Sum Query Immutable (LC 303) is the same idea on plain integers.
 *   - 2-D version (LC 304): inclusion-exclusion on a 2-D prefix grid.
 *   - Only one query? Skip the prefix array and scan the range directly, O(r - l).
 *
 * RUN
 *   main() runs 3 cases (LeetCode sample, single word, no vowel words) and prints
 *   actual vs expected.
 */
import java.util.Arrays;

class CountVowelStringsInRanges {

    public static int[] vowelStrings(String[] words, int[][] queries) {
        int n = words.length;
        int[] prefix = new int[n + 1]; // prefix[i] = vowel words among words[0..i-1]

        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + (isVowelWord(words[i]) ? 1 : 0);
        }

        int[] ans = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            int l = queries[i][0];
            int r = queries[i][1];
            ans[i] = prefix[r + 1] - prefix[l]; // count in the inclusive range [l, r]
        }
        return ans;
    }

    private static boolean isVowelWord(String word) {
        char first = word.charAt(0);
        char last = word.charAt(word.length() - 1);
        return isVowel(first) && isVowel(last);
    }

    private static boolean isVowel(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
    }

    private static void check(String label, String[] words, int[][] queries, String expected) {
        System.out.println(label + " words=" + Arrays.toString(words)
                + ", queries=" + Arrays.deepToString(queries)
                + " -> " + Arrays.toString(vowelStrings(words, queries))
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("case 1 (LeetCode sample):",
                new String[]{"aba", "bcb", "ece", "aa", "e"},
                new int[][]{{0, 2}, {1, 4}, {1, 1}},
                "[2, 3, 0]");
        check("case 2 (single word):    ",
                new String[]{"a"},
                new int[][]{{0, 0}},
                "[1]");
        check("case 3 (no vowel words): ",
                new String[]{"bcd", "xyz"},
                new int[][]{{0, 1}},
                "[0]");
    }
}

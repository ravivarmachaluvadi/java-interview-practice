/*
 * =====================================================================
 *  P086 Longest Increasing Subsequence   Canonical LC 300 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 300, Longest Increasing Subsequence)
 *   Return the length of the longest STRICTLY increasing subsequence (not necessarily
 *   contiguous). Follow-up: O(n log n).
 *
 * EXAMPLE
 *   [10, 9, 2, 5, 3, 7, 101, 18]  ->  4     2, 3, 7, 18
 *   [0, 1, 0, 3, 2, 3]            ->  4
 *   [7, 7, 7, 7]                  ->  1     strict: equal values do not chain
 *
 * RECOGNIZE WHEN
 *   - "longest chain / sequence where each next item is bigger / fits after the previous",
 *     order of the input must be kept (subsequence), or you may sort first (envelopes,
 *     pairs, word chains).
 *   - "minimum removals to make it increasing / a mountain" = n - LIS.
 *   Not this if: the run must be contiguous -> one scan like P004_KadaneBestEndingHere;
 *   two sequences are compared -> P087_TwoStringDp.
 *
 * TEMPLATE
 *   O(n^2):   dp[i] = 1 + max(dp[j] for j < i with a[j] < a[i]); answer = max(dp)
 *   O(n log n) patience: tails[k] = smallest possible tail of an increasing subsequence of
 *             length k + 1; for x: replace the first tail >= x (lower bound), or append
 *   2D items: sort by one key ascending (ties: the other key DESCENDING), LIS on the other
 *
 * APPROACH
 *   1. O(n^2): every i extends the best chain ending at a smaller earlier value.
 *   2. O(n log n): keep `tails`; binary search where x fits; length of tails = answer.
 *
 * KEY INSIGHT
 *   tails[] is always sorted, and keeping each length's SMALLEST tail leaves the most room
 *   for later numbers. Replacing (not inserting) keeps its length honest. tails is not the
 *   subsequence itself, only its length is right.
 *
 * COMPLEXITY
 *   O(n^2) / O(n log n) time, O(n) space.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 673  Number of LIS            keep (length, count) per i; equal lengths add
 *                                            counts, longer ones reset them
 *   [coded] LC 354  Russian Doll Envelopes   sort width asc, height DESC; LIS on heights
 *   [coded] LC 1048 Longest String Chain     sort by length; dp[word] = 1 + dp[word minus one
 *                                            letter]
 *           LC 368  Largest Divisible Subset sort; a[i] % a[j] == 0 instead of a[j] < a[i];
 *                                            keep parents to rebuild the set
 *           LC 1671 Min Removals for Mountain LIS from the left and from the right per index
 *           LC 646  Pair Chain               greedy sort-by-end -> P049_SortByEndGreedy
 *           LC 1964 Longest Obstacle Course  non-decreasing: use upper bound instead
 *
 * PITFALLS
 *   - Strict vs non-strict: lower bound (>= x) for strict, upper bound (> x) for
 *     non-decreasing.
 *   - LC 354: without "height descending on equal widths", equal widths would nest.
 *   - Do not read tails[] as an actual subsequence.
 *
 * DEEP DIVE
 *   A08_LongestIncreasingSubsequence, C13_LongestStringChain (12-Dynamic-Programming)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class LongestIncreasingSubsequence {

    // Canonical LC 300, O(n^2).
    static int lengthOfLISQuadratic(int[] a) {
        int[] dp = new int[a.length];
        int best = 0;
        for (int i = 0; i < a.length; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (a[j] < a[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            best = Math.max(best, dp[i]);
        }
        return best;
    }

    // Canonical LC 300, O(n log n) with patience sorting.
    static int lengthOfLIS(int[] a) {
        int[] tails = new int[a.length];
        int size = 0;
        for (int x : a) {
            int lo = 0;
            int hi = size;
            while (lo < hi) {                      // first tail >= x
                int mid = (lo + hi) >>> 1;
                if (tails[mid] >= x) {
                    hi = mid;
                } else {
                    lo = mid + 1;
                }
            }
            tails[lo] = x;
            if (lo == size) {
                size++;
            }
        }
        return size;
    }

    // LC 673.
    static int findNumberOfLIS(int[] a) {
        int n = a.length;
        int[] len = new int[n];
        int[] count = new int[n];
        int bestLen = 0;
        int total = 0;
        for (int i = 0; i < n; i++) {
            len[i] = 1;
            count[i] = 1;
            for (int j = 0; j < i; j++) {
                if (a[j] < a[i]) {
                    if (len[j] + 1 > len[i]) {
                        len[i] = len[j] + 1;
                        count[i] = count[j];
                    } else if (len[j] + 1 == len[i]) {
                        count[i] += count[j];
                    }
                }
            }
            if (len[i] > bestLen) {
                bestLen = len[i];
                total = count[i];
            } else if (len[i] == bestLen) {
                total += count[i];
            }
        }
        return total;
    }

    // LC 354: envelope fits inside another only if BOTH sides are strictly smaller.
    static int maxEnvelopes(int[][] envelopes) {
        int[][] e = envelopes.clone();
        Arrays.sort(e, (x, y) -> x[0] != y[0]
                ? Integer.compare(x[0], y[0])
                : Integer.compare(y[1], x[1]));   // equal widths: taller first
        int[] heights = new int[e.length];
        for (int i = 0; i < e.length; i++) {
            heights[i] = e[i][1];
        }
        return lengthOfLIS(heights);
    }

    // LC 1048: a predecessor is the word with exactly one letter removed.
    static int longestStrChain(String[] words) {
        String[] w = words.clone();
        Arrays.sort(w, (x, y) -> Integer.compare(x.length(), y.length()));
        Map<String, Integer> chain = new HashMap<>();
        int best = 0;
        for (String word : w) {
            int here = 1;
            for (int i = 0; i < word.length(); i++) {
                String prev = word.substring(0, i) + word.substring(i + 1);
                here = Math.max(here, chain.getOrDefault(prev, 0) + 1);
            }
            chain.put(word, here);
            best = Math.max(best, here);
        }
        return best;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] a = {10, 9, 2, 5, 3, 7, 101, 18};
        check("LC 300 n^2", lengthOfLISQuadratic(a), 4);
        check("LC 300 n log n", lengthOfLIS(a), 4);
        check("LC 300 [0,1,0,3,2,3]", lengthOfLIS(new int[]{0, 1, 0, 3, 2, 3}), 4);
        check("LC 300 all equal", lengthOfLIS(new int[]{7, 7, 7, 7}), 1);

        check("LC 673 [1,3,5,4,7]", findNumberOfLIS(new int[]{1, 3, 5, 4, 7}), 2);
        check("LC 673 all equal", findNumberOfLIS(new int[]{2, 2, 2, 2, 2}), 5);

        check("LC 354 four envelopes",
                maxEnvelopes(new int[][]{{5, 4}, {6, 4}, {6, 7}, {2, 3}}), 3);
        check("LC 354 identical", maxEnvelopes(new int[][]{{1, 1}, {1, 1}, {1, 1}}), 1);
        check("LC 354 equal widths do not nest",
                maxEnvelopes(new int[][]{{4, 5}, {4, 6}, {6, 7}}), 2);

        check("LC 1048 six words",
                longestStrChain(new String[]{"a", "b", "ba", "bca", "bda", "bdca"}), 4);
        check("LC 1048 five words",
                longestStrChain(new String[]{"xbc", "pcxbcf", "xb", "cxbc", "pcxbc"}), 5);
        check("LC 1048 no chain", longestStrChain(new String[]{"abcd", "dbqca"}), 1);
    }
}

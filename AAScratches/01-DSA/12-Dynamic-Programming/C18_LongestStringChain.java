/*
 * =====================================================================
 *  Longest String Chain                           LeetCode 1048 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   wordA is a predecessor of wordB if inserting exactly one letter anywhere
 *   in wordA (without reordering the rest) produces wordB. A word chain is a
 *   sequence where each word is a predecessor of the next. Given a list of
 *   words, return the length of the longest chain that can be built from them.
 *
 * EXAMPLE
 *   ["a","b","ba","bca","bda","bdca"]       ->  4   a -> ba -> bda -> bdca
 *   ["xbc","pcxbcf","xb","cxbc","pcxbc"]    ->  5   xb -> xbc -> cxbc -> pcxbc -> pcxbcf
 *   ["abcd","dbqca"]                        ->  1   neither is a predecessor
 *   ["only"]                                ->  1   edge case main() runs
 *
 * APPROACH  (LIS over a partial order, memoised in a HashMap)
 *   1. A predecessor is always exactly one character shorter, so sort the
 *      words by length. Every predecessor of w is then already processed.
 *   2. Walk the sorted words. For word w, chain[w] starts at 1 (w alone).
 *   3. Build every string you get by deleting one character of w - those are
 *      the only possible predecessors. Look each one up in the map.
 *   4. chain[w] = 1 + max(chain[pred]) over the predecessors that exist.
 *   5. Track the running maximum; that is the answer.
 *
 * KEY INSIGHT
 *   This is Longest Increasing Subsequence where "comes before" means "is a
 *   predecessor string". The trick that drops it from O(n^2 * L) to O(n * L^2)
 *   is generating the L candidate predecessors of w and looking them up,
 *   instead of comparing w against every earlier word. Sort by the dimension
 *   the DP moves along, then memoise on the value rather than on an index.
 *
 * COMPLEXITY
 *   Time  O(n log n + n * L^2)  sort, then for each word build L candidate
 *                               predecessors, each costing O(L) to splice/hash
 *   Space O(n * L)              the map holds every word and its chain length
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the chain itself, not its length (store the best predecessor).
 *   - Why is a one-character-delete check enough? Because lengths differ by 1.
 *   - Do it top-down with recursion + memo instead of the sorted sweep.
 *   - What changes if a predecessor may differ by one insert OR one replace?
 *
 * RUN
 *   main() runs 4 cases (typical, longer chain, no chain, single word) and
 *   prints actual vs expected.
 */

import java.util.*;

class LongestStringChain {

    public static int longestStrChain(String[] words) {
        if (words == null || words.length == 0) {
            return 0;
        }
        // Copy first: sorting the caller's array in place would be a surprising
        // side effect, and the order of the input is not ours to change.
        String[] sorted = Arrays.copyOf(words, words.length);
        // A predecessor is one character shorter, so shorter words must be
        // settled before longer ones.
        Arrays.sort(sorted, Comparator.comparingInt(String::length));

        Map<String, Integer> chainEndingAt = new HashMap<>();
        int maxChainLength = 0;

        for (String word : sorted) {
            int best = 1; // the word on its own is already a chain of length 1
            for (int i = 0; i < word.length(); i++) {
                // Delete position i: the only shapes a predecessor can have.
                String predecessor = word.substring(0, i) + word.substring(i + 1);
                Integer prevChain = chainEndingAt.get(predecessor);
                if (prevChain != null) {
                    best = Math.max(best, prevChain + 1);
                }
            }
            chainEndingAt.put(word, best);
            maxChainLength = Math.max(maxChainLength, best);
        }
        return maxChainLength;
    }

    private static void print(String label, String[] words, int expected) {
        System.out.println(label + " " + Arrays.toString(words) + " -> "
                + longestStrChain(words) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (typical):   ", new String[]{"a", "b", "ba", "bca", "bda", "bdca"}, 4);
        print("case 2 (longer):    ", new String[]{"xbc", "pcxbcf", "xb", "cxbc", "pcxbc"}, 5);
        print("case 3 (no chain):  ", new String[]{"abcd", "dbqca"}, 1);
        print("case 4 (one word):  ", new String[]{"only"}, 1);
    }
}

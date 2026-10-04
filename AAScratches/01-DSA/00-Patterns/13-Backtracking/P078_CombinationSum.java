/*
 * =====================================================================
 *  P078 Backtracking: Combinations with a Target   Canonical LC 39 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 39, Combination Sum)
 *   candidates are distinct positive integers. Return every unique combination that sums
 *   to target; each candidate may be used ANY number of times.
 *
 * EXAMPLE
 *   [2,3,6,7], target 7  ->  [[2,2,3],[7]]
 *   [2,3,5],   target 8  ->  [[2,2,2,2],[2,3,3],[3,5]]
 *   [2],       target 1  ->  []
 *
 * RECOGNIZE WHEN
 *   - LIST every combination that hits a target sum / size: "all combinations", "all ways
 *     to pick k numbers", "factor combinations".
 *   - Reuse allowed or not, duplicates in the input or not: four flavours, one template.
 *   Not this if: you only need HOW MANY or the best one -> DP (P084_ZeroOneKnapsack,
 *   P085_UnboundedKnapsack); ORDER matters ("sequences", LC 377) -> P085_UnboundedKnapsack.
 *
 * TEMPLATE
 *   sort candidates (lets you prune and skip duplicates)
 *   backtrack(start, remaining, path):
 *       if remaining == 0: record a copy; return
 *       for i in start..n-1:
 *           if a[i] > remaining: break                         // sorted: nothing later fits
 *           if no-reuse-with-dups and i > start and a[i] == a[i-1]: continue
 *           path.add(a[i]); backtrack(reuse ? i : i + 1, remaining - a[i], path); path.removeLast()
 *
 * APPROACH
 *   1. Sort, then try each candidate from `start` onward.
 *   2. Recurse with the SAME index (reuse allowed) and the reduced target.
 *   3. Stop early when a candidate is bigger than what remains.
 *
 * KEY INSIGHT
 *   Passing `start` (never looking back) makes each combination appear once in
 *   non-decreasing order. Reuse vs no reuse is ONE character: recurse with i or i + 1. Input
 *   duplicates are handled by the same "skip equal value at the same depth" rule as
 *   P076_Subsets.
 *
 * COMPLEXITY
 *   Exponential in the worst case; roughly O(N^(T / minCandidate)) for LC 39. Space O(T).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 40   Combination Sum II       no reuse (i + 1) + skip duplicates at a depth
 *   [coded] LC 216  Combination Sum III      digits 1..9, exactly k numbers, no reuse
 *   [coded] LC 77   Combinations             choose k of 1..n; prune when too few remain
 *           LC 377  Combination Sum IV       COUNT ordered sequences -> DP, not backtracking
 *                                            (P085_UnboundedKnapsack)
 *           LC 698  Partition to K Subsets   fill k buckets by backtracking (or P094_BitmaskDp)
 *           LC 473  Matchsticks to Square    LC 698 with k = 4
 *           LC 254  Factor Combinations      divisors >= the last one used
 *
 * PITFALLS
 *   - Break (not continue) on a[i] > remaining, which is only valid AFTER sorting.
 *   - Duplicate skip uses i > start; with i > 0 you lose answers like [1, 1, 6].
 *   - Copy the path when recording it.
 *
 * DEEP DIVE
 *   C03_CombinationSum, C04_CombinationSumII, A03_CombinationProgram
 *   (14-Backtracking-Recursion)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CombinationSum {

    // Canonical LC 39: reuse allowed.
    static List<List<Integer>> combinationSum(int[] candidates, int target) {
        int[] a = candidates.clone();
        Arrays.sort(a);
        List<List<Integer>> out = new ArrayList<>();
        pick(a, 0, target, new ArrayList<>(), out, true);
        return out;
    }

    // LC 40: each candidate once; the input may repeat values.
    static List<List<Integer>> combinationSum2(int[] candidates, int target) {
        int[] a = candidates.clone();
        Arrays.sort(a);
        List<List<Integer>> out = new ArrayList<>();
        pick(a, 0, target, new ArrayList<>(), out, false);
        return out;
    }

    private static void pick(int[] a, int start, int remaining, List<Integer> path,
                             List<List<Integer>> out, boolean reuse) {
        if (remaining == 0) {
            out.add(new ArrayList<>(path));
            return;
        }
        for (int i = start; i < a.length; i++) {
            if (a[i] > remaining) {
                break;
            }
            if (!reuse && i > start && a[i] == a[i - 1]) {
                continue;
            }
            path.add(a[i]);
            pick(a, reuse ? i : i + 1, remaining - a[i], path, out, reuse);
            path.remove(path.size() - 1);
        }
    }

    // LC 216: k distinct digits from 1..9 summing to n.
    static List<List<Integer>> combinationSum3(int k, int n) {
        List<List<Integer>> out = new ArrayList<>();
        digits(1, k, n, new ArrayList<>(), out);
        return out;
    }

    private static void digits(int start, int k, int remaining, List<Integer> path,
                               List<List<Integer>> out) {
        if (path.size() == k) {
            if (remaining == 0) {
                out.add(new ArrayList<>(path));
            }
            return;
        }
        for (int d = start; d <= 9 && d <= remaining; d++) {
            path.add(d);
            digits(d + 1, k, remaining - d, path, out);
            path.remove(path.size() - 1);
        }
    }

    // LC 77: all k-element combinations of 1..n.
    static List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> out = new ArrayList<>();
        choose(1, n, k, new ArrayList<>(), out);
        return out;
    }

    private static void choose(int start, int n, int k, List<Integer> path,
                               List<List<Integer>> out) {
        if (path.size() == k) {
            out.add(new ArrayList<>(path));
            return;
        }
        int need = k - path.size();
        for (int x = start; x <= n - need + 1; x++) {     // leave room for the rest
            path.add(x);
            choose(x + 1, n, k, path, out);
            path.remove(path.size() - 1);
        }
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 39 [2,3,6,7] t=7", combinationSum(new int[]{2, 3, 6, 7}, 7), "[[2, 2, 3], [7]]");
        check("LC 39 [2,3,5] t=8",
                combinationSum(new int[]{2, 3, 5}, 8), "[[2, 2, 2, 2], [2, 3, 3], [3, 5]]");
        check("LC 39 [2] t=1", combinationSum(new int[]{2}, 1), "[]");

        check("LC 40 [10,1,2,7,6,1,5] t=8", combinationSum2(new int[]{10, 1, 2, 7, 6, 1, 5}, 8),
                "[[1, 1, 6], [1, 2, 5], [1, 7], [2, 6]]");
        check("LC 40 [2,5,2,1,2] t=5",
                combinationSum2(new int[]{2, 5, 2, 1, 2}, 5), "[[1, 2, 2], [5]]");

        check("LC 216 k=3 n=7", combinationSum3(3, 7), "[[1, 2, 4]]");
        check("LC 216 k=3 n=9", combinationSum3(3, 9), "[[1, 2, 6], [1, 3, 5], [2, 3, 4]]");
        check("LC 216 k=4 n=1", combinationSum3(4, 1), "[]");

        check("LC 77 n=4 k=2", combine(4, 2), "[[1, 2], [1, 3], [1, 4], [2, 3], [2, 4], [3, 4]]");
        check("LC 77 n=1 k=1", combine(1, 1), "[[1]]");
    }
}

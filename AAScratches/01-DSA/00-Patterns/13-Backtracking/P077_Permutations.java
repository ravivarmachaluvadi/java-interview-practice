/*
 * =====================================================================
 *  P077 Backtracking: Permutations   Canonical LC 46 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 46, Permutations)
 *   nums has distinct integers. Return all orderings, in any order.
 *
 * EXAMPLE
 *   [1, 2, 3]  ->  [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
 *   [0, 1]     ->  [[0,1],[1,0]]
 *
 * RECOGNIZE WHEN
 *   - "all arrangements / orderings", "every order", n <= ~10 (n! grows fast).
 *   - Each position picks ANY unused element, so order matters (unlike subsets).
 *   - The k-th arrangement in sorted order without listing them all.
 *   Not this if: order does not matter -> P076_Subsets / P078_CombinationSum; just the next
 *   arrangement -> P009_ReverseTricks (LC 31).
 *
 * TEMPLATE
 *   backtrack(path, used):
 *       if path.size == n: record a copy; return
 *       for i in 0..n-1:
 *           if used[i]: continue
 *           if i > 0 and a[i] == a[i-1] and not used[i-1]: continue   // duplicates (sorted)
 *           used[i] = true; path.add(a[i]); backtrack(); path.removeLast(); used[i] = false
 *
 * APPROACH
 *   1. Fill positions left to right; any unused element may go next.
 *   2. Mark it used, recurse, then unmark (backtrack).
 *
 * KEY INSIGHT
 *   Subsets move forward from `start`; permutations restart from 0 each level and use a
 *   `used[]` array instead, because an element later in the input may come first. For
 *   duplicates, only let equal values be placed in their original relative order (skip a
 *   value whose equal left neighbour is not used yet).
 *
 * COMPLEXITY
 *   Time O(n * n!), space O(n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 47   Permutations II          sort; skip a[i] == a[i-1] && !used[i-1]
 *   [coded] LC 60   Kth Permutation Sequence no listing: (k-1) / (n-1)! picks the first
 *                                            digit, recurse on the remainder
 *           LC 31   Next Permutation         -> P009_ReverseTricks
 *           LC 526  Beautiful Arrangement    prune: place value only if it divides / is
 *                                            divided by the position
 *           LC 996  Squareful Permutations   LC 47 + prune when neighbours do not sum to a square
 *           LC 267  Palindrome Permutation II permute half the letters, mirror them
 *
 * PITFALLS
 *   - The duplicate rule needs !used[i-1] (or the opposite, used[i-1]); mixing them up
 *     either repeats or drops permutations.
 *   - LC 60: k is 1-based; use k - 1 for the index arithmetic.
 *   - Swap-based permutation (no used[]) does not output in lexicographic order.
 *
 * DEEP DIVE
 *   A05_Permutations, D02_KthPermutation (14-Backtracking-Recursion)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Permutations {

    // Canonical LC 46 (also LC 47 when skipDup is true and the input is sorted).
    static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> out = new ArrayList<>();
        place(nums, new boolean[nums.length], new ArrayList<>(), out, false);
        return out;
    }

    // LC 47.
    static List<List<Integer>> permuteUnique(int[] nums) {
        int[] a = nums.clone();
        Arrays.sort(a);
        List<List<Integer>> out = new ArrayList<>();
        place(a, new boolean[a.length], new ArrayList<>(), out, true);
        return out;
    }

    private static void place(int[] a, boolean[] used, List<Integer> path, List<List<Integer>> out,
                              boolean skipDup) {
        if (path.size() == a.length) {
            out.add(new ArrayList<>(path));
            return;
        }
        for (int i = 0; i < a.length; i++) {
            if (used[i]) {
                continue;
            }
            if (skipDup && i > 0 && a[i] == a[i - 1] && !used[i - 1]) {
                continue;                          // equal values only in original order
            }
            used[i] = true;
            path.add(a[i]);
            place(a, used, path, out, skipDup);
            path.remove(path.size() - 1);
            used[i] = false;
        }
    }

    // LC 60: the k-th (1-based) permutation of 1..n in lexicographic order.
    static String getPermutation(int n, int k) {
        List<Integer> digits = new ArrayList<>();
        int[] fact = new int[n + 1];
        fact[0] = 1;
        for (int i = 1; i <= n; i++) {
            digits.add(i);
            fact[i] = fact[i - 1] * i;
        }
        k--;                                       // 0-based rank
        StringBuilder sb = new StringBuilder();
        for (int pos = n; pos >= 1; pos--) {
            int block = fact[pos - 1];             // permutations per choice of this digit
            sb.append(digits.remove(k / block));
            k %= block;
        }
        return sb.toString();
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 46 [1,2,3]", permute(new int[]{1, 2, 3}),
                "[[1, 2, 3], [1, 3, 2], [2, 1, 3], [2, 3, 1], [3, 1, 2], [3, 2, 1]]");
        check("LC 46 [0,1]", permute(new int[]{0, 1}), "[[0, 1], [1, 0]]");
        check("LC 46 [1]", permute(new int[]{1}), "[[1]]");

        check("LC 47 [1,1,2]",
                permuteUnique(new int[]{1, 1, 2}), "[[1, 1, 2], [1, 2, 1], [2, 1, 1]]");
        check("LC 47 [2,2,2] one result", permuteUnique(new int[]{2, 2, 2}), "[[2, 2, 2]]");
        check("LC 47 [1,2,3] count", permuteUnique(new int[]{1, 2, 3}).size(), 6);

        check("LC 60 n=3 k=3", getPermutation(3, 3), "213");
        check("LC 60 n=4 k=9", getPermutation(4, 9), "2314");
        check("LC 60 n=3 k=1", getPermutation(3, 1), "123");
        check("LC 60 n=3 k=6 last", getPermutation(3, 6), "321");
    }
}

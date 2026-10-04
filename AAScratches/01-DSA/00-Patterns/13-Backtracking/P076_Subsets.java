/*
 * =====================================================================
 *  P076 Backtracking: Subsets   Canonical LC 78 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 78, Subsets)
 *   nums has distinct integers. Return every subset (the power set), in any order.
 *
 * EXAMPLE
 *   [1, 2, 3]  ->  [[], [1], [1,2], [1,2,3], [1,3], [2], [2,3], [3]]
 *   [0]        ->  [[], [0]]
 *
 * RECOGNIZE WHEN
 *   - "all subsets / all combinations of any size / every way to pick", with n <= ~20.
 *   - Each element is independently IN or OUT.
 *   - Duplicates in the input but the output must not repeat a subset.
 *   Not this if: order matters (arrangements) -> P077_Permutations; a target sum or a fixed
 *   size -> P078_CombinationSum; you only need the COUNT or the best value -> DP
 *   (P084_ZeroOneKnapsack).
 *
 * TEMPLATE
 *   backtrack(start, path):
 *       record a copy of path                       // every node of the tree is a subset
 *       for i in start..n-1:
 *           if i > start and a[i] == a[i-1]: continue   // duplicates (input sorted)
 *           path.add(a[i]); backtrack(i + 1, path); path.removeLast()
 *   bitmask: for mask in 0..2^n-1: subset = { a[i] : bit i of mask is 1 }
 *
 * APPROACH
 *   1. Each call records the current path, then tries every next element AFTER start.
 *   2. Choose, recurse, un-choose.
 *
 * KEY INSIGHT
 *   Moving forward only (i + 1) makes each subset appear exactly once, in one canonical
 *   order. For duplicates, sort first and skip a value equal to its left neighbour at the
 *   SAME depth: picking the second "2" there would rebuild what the first "2" already built.
 *
 * COMPLEXITY
 *   Time O(n * 2^n) (2^n subsets, O(n) to copy each), space O(n) recursion.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 90   Subsets II               sort; skip a[i] == a[i-1] when i > start
 *   [coded] Bitmask enumeration              bit i of mask decides a[i]; no recursion
 *   [coded] LC 784  Letter Case Permutation  each LETTER is in "lower" or "upper" state
 *           LC 1863 Sum of All Subset XORs   enumerate subsets (or: OR of all * 2^(n-1))
 *           LC 2044 Count Max-OR Subsets     enumerate, count those reaching the max OR
 *           LC 77   Combinations (size k)    -> P078_CombinationSum
 *
 * PITFALLS
 *   - Add a COPY of path (new ArrayList<>(path)), not path itself.
 *   - The duplicate skip is "i > start", not "i > 0", or valid subsets like [2,2] vanish.
 *   - 2^n grows fast: n = 20 is about a million subsets; mention it.
 *
 * DEEP DIVE
 *   A04_Subsets, C02_SubsetsII (14-Backtracking-Recursion)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class Subsets {

    // Canonical LC 78.
    static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> out = new ArrayList<>();
        build(nums, 0, new ArrayList<>(), out, false);
        return out;
    }

    // LC 90.
    static List<List<Integer>> subsetsWithDup(int[] nums) {
        int[] a = nums.clone();
        Arrays.sort(a);
        List<List<Integer>> out = new ArrayList<>();
        build(a, 0, new ArrayList<>(), out, true);
        return out;
    }

    private static void build(int[] a, int start, List<Integer> path, List<List<Integer>> out,
                              boolean skipDup) {
        out.add(new ArrayList<>(path));
        for (int i = start; i < a.length; i++) {
            if (skipDup && i > start && a[i] == a[i - 1]) {
                continue;
            }
            path.add(a[i]);
            build(a, i + 1, path, out, skipDup);
            path.remove(path.size() - 1);
        }
    }

    // Bitmask enumeration: mask bit i set means a[i] is in the subset.
    static List<List<Integer>> subsetsBitmask(int[] nums) {
        List<List<Integer>> out = new ArrayList<>();
        for (int mask = 0; mask < (1 << nums.length); mask++) {
            List<Integer> s = new ArrayList<>();
            for (int i = 0; i < nums.length; i++) {
                if ((mask >> i & 1) == 1) {
                    s.add(nums[i]);
                }
            }
            out.add(s);
        }
        return out;
    }

    // LC 784: every letter can be lower or upper case.
    static List<String> letterCasePermutation(String s) {
        List<String> out = new ArrayList<>();
        toggle(s.toCharArray(), 0, out);
        Collections.sort(out);                     // any order is accepted; sorted to print
        return out;
    }

    private static void toggle(char[] c, int i, List<String> out) {
        if (i == c.length) {
            out.add(new String(c));
            return;
        }
        toggle(c, i + 1, out);                     // keep as is
        if (Character.isLetter(c[i])) {
            c[i] ^= 32;                            // flip case (ASCII letters differ by bit 5)
            toggle(c, i + 1, out);
            c[i] ^= 32;                            // undo
        }
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 78 [1,2,3]", subsets(new int[]{1, 2, 3}),
                "[[], [1], [1, 2], [1, 2, 3], [1, 3], [2], [2, 3], [3]]");
        check("LC 78 [0]", subsets(new int[]{0}), "[[], [0]]");

        check("LC 90 [1,2,2]",
                subsetsWithDup(new int[]{1, 2, 2}), "[[], [1], [1, 2], [1, 2, 2], [2], [2, 2]]");
        check("LC 90 unsorted [2,1,2]", subsetsWithDup(new int[]{2, 1, 2}).size(), 6);

        check("bitmask [1,2,3]", subsetsBitmask(new int[]{1, 2, 3}),
                "[[], [1], [2], [1, 2], [3], [1, 3], [2, 3], [1, 2, 3]]");

        check("LC 784 a1b2", letterCasePermutation("a1b2"), "[A1B2, A1b2, a1B2, a1b2]");
        check("LC 784 3z4", letterCasePermutation("3z4"), "[3Z4, 3z4]");
        check("LC 784 digits only", letterCasePermutation("12"), "[12]");
    }
}

/*
 * =====================================================================
 *  Combination Sum II                              LeetCode 40 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of positive integers that MAY CONTAIN DUPLICATES and a target,
 *   return every unique combination summing to target, where each array POSITION
 *   may be used at most once. The answer must not contain the same multiset twice,
 *   so [1,7] from the two different 1s counts only once.
 *
 * EXAMPLE
 *   candidates = [10,1,2,7,6,1,5], target = 8 -> [[1,1,6],[1,2,5],[1,7],[2,6]]
 *   candidates = [1,1],            target = 2 -> [[1,1]]   both 1s used, still one answer
 *   candidates = [2,5,2,1,2],      target = 5 -> [[1,2,2],[5]]
 *   candidates = [3,3],            target = 7 -> []        edge: impossible
 *
 * APPROACH  (sort, then skip duplicates at the same depth)
 *   1. Sort the array. Equal values become neighbours and the array becomes ascending,
 *      which is what makes both the dedupe rule and the pruning break legal.
 *   2. Recurse with a start index, a running list and the remaining target.
 *   3. remaining == 0 -> record a copy of the current list.
 *   4. In the loop, break (not continue) as soon as candidates[i] > remaining: the rest
 *      of the sorted array is even bigger, so the whole tail is dead.
 *   5. DEDUPE RULE: if i > start and candidates[i] == candidates[i-1], skip i. The first
 *      copy at this depth already explored every combination this value can start.
 *   6. Recurse with i + 1 - each position is consumable once - then undo the add.
 *
 * KEY INSIGHT
 *   "i > start", not "i > 0". At a given depth the first candidate is allowed no matter
 *   what; only a REPEAT of the previous value AT THE SAME DEPTH is pruned. Using i > 0
 *   would also kill [1,1,6], where the second 1 is picked one level deeper. This one
 *   condition is the template every "...II" / "duplicates allowed" backtracking problem
 *   reuses: sort, then let only the leftmost of a run of equal values branch at each level.
 *
 * COMPLEXITY
 *   Time  O(2^n * n)  up to 2^n subsets explored, O(n) to copy each recorded one;
 *                     sorting adds O(n log n) and the pruning helps a lot in practice.
 *   Space O(n)        recursion depth plus the current list (result not counted).
 *
 * INTERVIEW FOLLOW-UPS
 *   - No duplicates but unlimited reuse -> Combination Sum I (pass i instead of i + 1).
 *   - Same dedupe on Subsets with duplicates -> Subsets II, identical i > start rule.
 *   - Why sort at all? Without it, equal values are not adjacent and the skip fails.
 *   - Could a HashSet of combinations replace the rule? Yes, but it wastes the work and
 *     the memory; pruning at the branch is strictly better.
 *
 * RUN
 *   main() runs 4 cases (typical, all-duplicates edge, mixed duplicates, impossible)
 *   and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CombinationSumII {

    public static List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        // sorting is what makes equal values adjacent (dedupe) and ascending (break)
        Arrays.sort(candidates);
        backtrack(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(int[] candidates,
                                  int remaining,
                                  int start,
                                  List<Integer> current,
                                  List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(current));   // copy: current keeps mutating
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            // sorted, so once one value overshoots, every later value does too
            if (candidates[i] > remaining) {
                break;
            }
            // skip a repeat of the previous value AT THIS DEPTH; i > start, not i > 0
            if (i > start && candidates[i] == candidates[i - 1]) {
                continue;
            }
            current.add(candidates[i]);
            // i + 1: this position is spent, each element is usable at most once
            backtrack(candidates, remaining - candidates[i], i + 1, current, result);
            current.remove(current.size() - 1);     // undo
        }
    }

    private static void print(String label, Object actual, String expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 [10,1,2,7,6,1,5] t=8",
                combinationSum2(new int[]{10, 1, 2, 7, 6, 1, 5}, 8),
                "[[1, 1, 6], [1, 2, 5], [1, 7], [2, 6]]");

        print("case 2 [1,1] t=2 (edge: all duplicates)",
                combinationSum2(new int[]{1, 1}, 2),
                "[[1, 1]]");

        print("case 3 [2,5,2,1,2] t=5 (tricky: three equal 2s)",
                combinationSum2(new int[]{2, 5, 2, 1, 2}, 5),
                "[[1, 2, 2], [5]]");

        print("case 4 [3,3] t=7 (edge: impossible)",
                combinationSum2(new int[]{3, 3}, 7),
                "[]");
    }
}

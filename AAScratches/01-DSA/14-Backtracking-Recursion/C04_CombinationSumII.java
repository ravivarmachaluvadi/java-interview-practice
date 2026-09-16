/**
 * Problem: Given an integer array candidates that may contain duplicates and a target sum,
 * find all unique combinations where the candidate numbers sum to target.
 * Each number in candidates can be used at most once in each combination.
 *
 * Approach: Sort the array to enable early pruning and duplicate skipping.
 * Use backtracking to explore subsets, adding a number only if it does not exceed
 * the remaining target. Skip equal elements at the same recursion depth to avoid
 * duplicate combinations. When the remaining sum reaches zero, record the current
 * combination.
 *
 * Time Complexity: O(2^n) in worst case (each element either included or excluded),
 * with additional overhead for sorting O(n log n). Duplicate checks keep it practical.
 * Space Complexity: O(n) recursion stack plus result storage; auxiliary space is O(n).
 */
import java.util.*;

class CombinationSumII {
    public static List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        backtrack(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(int[] candidates, int remaining, int start,
                                  List<Integer> current, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            // if the candidate is too large, we can break (since sorted)
            if (candidates[i] > remaining) {
                break;
            }
            // skip duplicates at the same recursion level
            if (i > start && candidates[i] == candidates[i - 1]) {
                continue;
            }
            current.add(candidates[i]);
            // move to i+1 because each number can be used only once
            backtrack(candidates, remaining - candidates[i], i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    public static void main(String[] args) {
        int[] candidates = {10, 1, 2, 7, 6, 1, 5};
        int target = 8;
        List<List<Integer>> combinations = combinationSum2(candidates, target);
        System.out.println("Combinations summing to " + target + ":");
        for (List<Integer> comb : combinations) {
            System.out.println(comb);
        }
    }
}

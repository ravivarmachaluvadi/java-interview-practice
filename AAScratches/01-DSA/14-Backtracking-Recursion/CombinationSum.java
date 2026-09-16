import java.util.*;

// https://leetcode.com/problems/combination-sum

/**
 * The same number may be chosen from candidates an unlimited
 * <p>
 * number of times. Two combinations are unique if the frequency
 * <p>
 * of at least one of the chosen numbers is different.
 * <p>
 * Example 2:
 * <p>
 * Input: candidates = [2,3,5], target = 8
 * <p>
 * Output: [[2,2,2,2],[2,3,3],[3,5]]
 */
class CombinationSum {
    public static List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> current = new ArrayList<>();
        combinationSumHelper(candidates, target, 0, current, result);
        return result;
    }

    // with reatition
    private static void combinationSumHelper(int[] candidates,
                                             int target, int start,
                                             List<Integer> current,
                                             List<List<Integer>> result) {
        if (target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] <= target) {
                current.add(candidates[i]);
                // remember pass i and use arr[i] to subtract from target not start
                combinationSumHelper(candidates, target - candidates[i], i, current, result);
                current.remove(current.size() - 1);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(combinationSum(new int[]{2, 3, 5}, 8));
    }
}

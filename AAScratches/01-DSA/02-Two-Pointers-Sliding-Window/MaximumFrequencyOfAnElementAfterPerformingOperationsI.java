import java.util.*;

// https://leetcode.com/problems/maximum-frequency-of-an-element-after-performing-operations-i/description/
// 3346. Maximum Frequency of an Element After Performing Operations I

/**
 * You are given an integer array nums and two integers k and numOperations.
 * <p>
 * You must perform an operation numOperations times on nums, where in each operation you:
 * <p>
 * Select an index i that was not selected in any previous operations.
 * Add an integer in the range [-k, k] to nums[i].
 * Return the maximum possible frequency of any element in nums after performing the operations.
 * <p>
 * Example 1:
 * <p>
 * Input: nums = [1,4,5], k = 1, numOperations = 2
 * <p>
 * Output: 2
 * <p>
 * Explanation:
 * <p>
 * We can achieve a maximum frequency of two by:
 * <p>
 * Adding 0 to nums[1]. nums becomes [1, 4, 5].
 * Adding -1 to nums[2]. nums becomes [1, 4, 4].
 */
class MaximumFrequencyOfAnElementAfterPerformingOperationsI {
    public static int maxFrequency(int[] nums, int k, int numOperations) {
        Map<Integer, Integer> cnt = new HashMap<>();
        TreeMap<Integer, Integer> diff = new TreeMap<>();
        // Count occurrences of each original number
        for (int x : nums) {
            cnt.merge(x, 1, Integer::sum);
        }
        // Build difference map: each x contributes +1 at (x−k), −1 at (x+k+1)
        for (int x : nums) {
            int start = x - k;
            int end = x + k + 1;  // exclusive boundary
            diff.merge(start, 1, Integer::sum);
            diff.merge(end, -1, Integer::sum);
        }
        int ans = 1;
        int current = 0;  // running sum = how many elements can reach this target
        for (Map.Entry<Integer, Integer> entry : diff.entrySet()) {
            int t = entry.getKey();
            int delta = entry.getValue();
            current += delta;
            int already = cnt.getOrDefault(t, 0);
            int otherReachable = current - already;
            int possible = already + Math.min(numOperations, otherReachable);
            ans = Math.max(ans, possible);
        }
        return ans;
    }

    public static void main(String[] args) {
        int[] nums = {1, 4, 5};
        int k = 2;
        int numOperations = 2;
        int result = maxFrequency(nums, k, numOperations);
        System.out.println("Result = " + result);
        // For example: we can make at most frequency = 2
        // Explanation: you can pick target value 3:
        //   1 → +2 = 3 (1 operation)
        //   4 → −1 = 3 (1 operation)
        //   5 cannot also be made 3 because only 2 operations allowed.
        // So you get two “3”s => freq = 2.
    }
}

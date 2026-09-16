import java.util.*;

// https://leetcode.com/problems/minimum-cost-to-make-array-equal/
// 2448. Minimum Cost to Make Array Equal

/**
 * You are given two 0-indexed arrays nums and cost consisting each of n positive integers.
 * <p>
 * You can do the following operation any number of times:
 * <p>
 * Increase or decrease any element of the array nums by 1.
 * <p>
 * The cost of doing one operation on the ith element is cost[i].
 * <p>
 * Return the minimum total cost such that all the elements of the array nums become equal.
 * <p>
 * Example 1:
 * <p>
 * Input: nums = [1,3,5,2], cost = [2,3,1,14]
 * Output: 8
 * <p>
 * Explanation: We can make all the elements equal to 2 in the following way:
 * <p>
 * - Increase the 0th element one time. The cost is 2.
 * <p>
 * - Decrease the 1st element one time. The cost is 3.
 * <p>
 * - Decrease the 2nd element three times. The cost is 1 + 1 + 1 = 3.
 * <p>
 * The total cost is 2 + 3 + 3 = 8.
 * <p>
 * It can be shown that we cannot make the array equal with a smaller cost.
 *
 */
class MinimumCostToMakeArrayEqual {

    public static long minCost(int[] nums, int[] cost) {
        int low = Integer.MAX_VALUE, high = 0;
        for (int num : nums) {
            high = Math.max(high, num);
            low = Math.min(low, num);
        }
        long answer = Long.MAX_VALUE;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            long cost1 = checkCost(mid, nums, cost);
            long cost2 = checkCost(mid + 1, nums, cost);
            answer = Math.min(answer, Math.min(cost1, cost2));
            if (cost1 > cost2) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return answer;
    }

    public static long checkCost(int target, int[] nums, int[] cost) {
        long totalCost = 0;
        for (int i = 0; i < nums.length; i++) {
            totalCost += (long) Math.abs(nums[i] - target) * cost[i];
        }
        return totalCost;
    }

    // Example main method
    public static void main(String[] args) {
        MinimumCostToMakeArrayEqual sol = new MinimumCostToMakeArrayEqual();

        int[] nums = {1, 3, 5, 2};
        int[] cost = {2, 3, 1, 14};

        long answer = minCost(nums, cost);
        System.out.println("Minimum Cost: " + answer);
    }
}

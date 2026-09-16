/**
 * Problem: Count the number of subsequences in an integer array whose elements sum to a given target value.
 *
 * Approach: Recursively explore each element with two choices—include it (subtracting its value from the remaining sum)
 * or exclude it—and count successful paths that reduce the remaining sum to zero. The recursion stops when the
 * remaining sum is negative or all elements have been considered.
 *
 * Time Complexity: O(2^n) in the worst case, where n is the number of array elements (each element can be chosen or not).
 * Space Complexity: O(n) due to the recursion stack depth.
 */
class CountSubsequenceWithTargetSum {

    private int func(int ind, int sum, int[] nums) {
        if (sum == 0) return 1;

        if (sum < 0 || ind == nums.length) return 0;
        // pick+notPick
        return func(ind + 1, sum - nums[ind], nums) + func(ind + 1, sum, nums);
    }

    public int countSubsequenceWithTargetSum(int[] nums, int target) {
        return func(0, target, nums); // 3
    }

    public static void main(String[] args) {
        CountSubsequenceWithTargetSum counter = new CountSubsequenceWithTargetSum();
        int[] nums = {2, 3, 5, 6, 8, 10};
        int target = 10;
        System.out.println("Count of subsequences with sum " + target + ": " +
                counter.countSubsequenceWithTargetSum(nums, target));
    }
}
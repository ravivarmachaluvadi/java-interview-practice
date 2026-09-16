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
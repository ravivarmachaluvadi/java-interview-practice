class RecursiveArraySum {
    public int arraySum(int[] nums) {
        // Start from index 0
        // started with first element
        return sum(nums, 0);
    }

    private int sum(int[] nums, int ind) {
        if (ind >= nums.length) {
            return 0;
        }
        // Add current element and recurse to next element
        return nums[ind] + sum(nums, ind + 1);
    }
}

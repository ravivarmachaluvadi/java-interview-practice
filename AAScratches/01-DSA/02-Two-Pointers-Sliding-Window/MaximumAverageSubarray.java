class MaximumAverageSubarray {
    // fixed window and sliding window average sum
    public static double findMaxAverage(int[] nums, int k) {
        double maxSum = 0;
        for (int i = 0; i < k; i++) {
            maxSum += nums[i];
        }

        double currentSum = maxSum;
        for (int i = k; i < nums.length; i++) {
            // nums[i] is current val
            // nums[i - k] is value is k places before from current one
            currentSum += nums[i] - nums[i - k];
            maxSum = Math.max(maxSum, currentSum);
        }

        return maxSum / k;
    }

    public static void main(String[] args) {
        int[] nums = {1, 12, -5, -6, 50, 3};
        int k = 4;
        double result = findMaxAverage(nums, k);
        System.out.println("Maximum average of a subarray of length " + k + " is: " + result);// 12.75
    }
}

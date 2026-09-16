import java.util.Arrays;

// Array, Binary Search, Dynamic Programming
// https://leetcode.com/problems/longest-increasing-subsequence
class LengthOfLIS {
    public int lengthOfLIS(int[] nums) {
        if (nums == null || nums.length == 0) return 0;

        int n = nums.length;
        int[] dp = new int[n];
        int maxLength = 1;
        Arrays.fill(dp, 1);

        for (int i = 1; i < n; i++) {
            // Iterate through all indices j less than i
            for (int j = 0; j < i; j++) {
                // If nums[i] > nums[j], update dp[i]
                if (nums[i] > nums[j]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                    maxLength = Math.max(maxLength, dp[i]);
                }
            }
        }
        return maxLength;
    }
}

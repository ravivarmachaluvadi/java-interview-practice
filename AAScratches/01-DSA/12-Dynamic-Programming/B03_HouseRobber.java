/**
 * Problem: Given an array of non‑negative integers where each element represents the amount of money in a house,
 * determine the maximum amount that can be robbed without robbing two adjacent houses.
 *
 * Approach: Use dynamic programming to keep track of the best robbery total up to each house.
 * For each house i, the optimal value is max(dp[i-1], dp[i-2] + nums[i]).
 * A space‑optimized version maintains only the last two results instead of an array.
 *
 * Time Complexity: O(n) – one pass through the houses.
 * Space Complexity: O(1) – constant extra space (or O(n) if using the full DP array).
 */
class HouseRobber {

    public static int rob(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];

        int[] dp = new int[nums.length];
        //a
        dp[0] = nums[0];
        //b
        dp[1] = Math.max(nums[0], nums[1]);

        for (int i = 2; i < nums.length; i++) {
            dp[i] = Math.max(dp[i - 1], dp[i - 2] + nums[i]);
        }
        return dp[nums.length - 1];
    }

    // space optimized
    public static int rob(int[] nums) {
        if (nums == null && nums.length == 0) {
            return 0;
        }
        int n = nums.length;
        if (n == 1) {
            return nums[0];
        }
        int a = nums[0];
        int b = nums[1];
        int c = 0;
        for (int i = 2; i < n; i++) {
            c = Math.max(b, a + nums[i]);
            a = b;
            b = c;
        }
        return c;
    }

    public static void main(String[] args) {
        int[] houses1 = {1, 2, 3, 1};
        System.out.println("Maximum amount that can be robbed: " + rob(houses1)); // Output: 4

        int[] houses2 = {2, 7, 9, 3, 1};
        System.out.println("Maximum amount that can be robbed: " + rob(houses2)); // Output: 12
    }
}

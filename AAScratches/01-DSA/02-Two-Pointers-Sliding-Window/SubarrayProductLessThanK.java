import java.util.*;
// https://leetcode.com/problems/subarray-product-less-than-k/
// 713. Subarray Product Less Than K
public class SubarrayProductLessThanK {
    public static int numSubarrayProductLessThanK(int[] nums, int k) {
        if (k <= 1) return 0;  // since product < k and nums[i] ≥ 1 in this problem
        int count = 0;
        int left = 0;
        long prod = 1;
        for (int right = 0; right < nums.length; right++) {
            prod *= nums[right];
            // shrink window while product is too large
            while (prod >= k && left <= right) {
                prod /= nums[left];
                left++;
            }
            // all subarrays ending at right and starting from
            // any index in [left..right] are valid
            count += (right - left + 1);
        }
        return count;
    }

    public static void main(String[] args) {
        int[] nums = {10, 5, 2, 6};
        int k = 100;
        int result = numSubarrayProductLessThanK(nums, k);
        System.out.println("Number of subarrays with product less than " + k + " = " + result);
        // Expected output: 8
        // Explanation: The 8 subarrays are: [10], [5], [2], [6], [10,5], [5,2], [2,6], [5,2,6]
    }
}

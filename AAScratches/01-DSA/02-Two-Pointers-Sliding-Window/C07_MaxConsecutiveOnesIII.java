/**
 * Problem: Given a binary array and an integer k, find the length of the longest subarray
 * that contains at most k zeros (i.e., can flip up to k zeros to ones).
 *
 * Approach: Use a sliding window with two pointers. Expand the right pointer while counting
 * zeros; when zero count exceeds k, shrink from the left until it is <= k again.
 * Track the maximum window length seen.
 *
 * Time Complexity: O(n), where n is the array length (each element visited at most twice).
 * Space Complexity: O(1) – only a few integer variables are used regardless of input size.
 */
class MaxConsecutiveOnesIII {
    public static void main(String[] args) {
        int[] nums = {1, 1, 0, 0, 1, 1, 1, 0, 1, 1};
        int k = 2;
        int result = longestOnes(nums, k);
        System.out.println("Maximum consecutive ones with up to " + k + " flips: " + result);
        // 7
    }

    public static int longestOnes(int[] nums, int k) {
        int left = 0;
        int maxLength = 0;
        int zeroCount = 0;

        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0) {
                zeroCount++;
            }

            while (zeroCount > k) {
                if (nums[left] == 0) {
                    zeroCount--;
                }
                left++;
            }
            maxLength = Math.max(maxLength, right - left + 1);
        }
        return maxLength;
    }
}

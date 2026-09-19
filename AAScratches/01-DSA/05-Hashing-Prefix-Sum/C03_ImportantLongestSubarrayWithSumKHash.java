/**
 * Problem: Longest contiguous subarray whose elements sum to exactly k (return its length).
 *
 * Approaches:
 *   1) longestWithPrefixSumHash  - prefix sum + HashMap<prefixSum, firstIndex>. O(n) time, O(n) space.
 *                                  Works for ANY integers (negatives allowed). This is the general answer.
 *   2) longestWithSlidingWindow  - two pointers, shrink from the left while sum > k. O(n) time, O(1) space.
 *                                  Only valid when every element is >= 0 (window sum must be monotonic).
 *
 * Interview note: state the constraint first. "Non-negative array" -> sliding window;
 * "can contain negatives" -> prefix-sum hash. Saying both, with the reason, is the full-marks answer.
 */
import java.util.HashMap;
import java.util.Map;

class ImportantLongestSubarrayWithSumKHash {

    /** Approach 1: prefix sum + hash map of the FIRST index at which each prefix sum was seen. */
    public static int longestWithPrefixSumHash(int[] nums, long k) {
        Map<Long, Integer> firstIndexOfPrefixSum = new HashMap<>();
        long prefixSum = 0;               // long: a sum of many ints can overflow int
        int maxLength = 0;

        for (int i = 0; i < nums.length; i++) {
            prefixSum += nums[i];

            // Whole prefix [0..i] sums to k
            if (prefixSum == k) maxLength = i + 1;

            // If some earlier prefix j had sum (prefixSum - k), then (j..i] sums to k
            long remainder = prefixSum - k;
            if (firstIndexOfPrefixSum.containsKey(remainder)) {
                maxLength = Math.max(maxLength, i - firstIndexOfPrefixSum.get(remainder));
            }

            // putIfAbsent, NOT put: keep the earliest index so the subarray is as long as possible.
            // (With put you would overwrite it when a zero-sum stretch repeats a prefix.)
            firstIndexOfPrefixSum.putIfAbsent(prefixSum, i);
        }
        return maxLength;
    }

    /** Approach 2: sliding window. Correct only for non-negative elements. */
    public static int longestWithSlidingWindow(int[] nums, int k) {
        int left = 0, right = 0, windowSum = 0, maxLength = 0;

        while (right < nums.length) {
            windowSum += nums[right];      // expand: add at the right first, advance right at the end

            // "while", not "if" -- a single element may push the sum far past k. Common mistake.
            while (windowSum > k && left <= right) {
                windowSum -= nums[left];
                left++;
            }

            if (windowSum == k) maxLength = Math.max(maxLength, right - left + 1);
            right++;
        }
        return maxLength;
    }

    public static void main(String[] args) {
        int[][] inputs = {
            {1, 2, 3, 7, 5},          // k=12 -> 3  ([2,3,7])
            {4, 1, 1, 1, 2, 3, 5},    // k=5  -> 4  ([1,1,1,2])
            {10, 5, 2, 7, 1, 9},      // k=15 -> 4  ([5,2,7,1])
            {2, 3, 5, 1, 9},          // k=10 -> 3  ([2,3,5])
            {5, 8, -1, 3, 2},         // k=10 -> 3  ([8,-1,3]); window happens to still work here
            {1, -1, 5, -2, 3}         // k=3  -> 4  ([1,-1,5,-2]); window WRONGLY returns 0
        };
        int[] ks = {12, 5, 15, 10, 10, 3};

        for (int t = 0; t < inputs.length; t++) {
            int[] nums = inputs[t];
            int k = ks[t];
            System.out.println("nums=" + java.util.Arrays.toString(nums) + " k=" + k);
            System.out.println("  prefixSumHash  : " + longestWithPrefixSumHash(nums, k));
            System.out.println("  slidingWindow  : " + longestWithSlidingWindow(nums, k)
                    + (hasNegative(nums) ? "   <- has negatives: sliding window is NOT reliable" : ""));
        }
    }

    private static boolean hasNegative(int[] nums) {
        for (int x : nums) if (x < 0) return true;
        return false;
    }
}

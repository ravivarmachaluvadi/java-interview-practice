/**
 * Problem:
 *   Given an integer array and a target sum k, find the length of the longest contiguous subarray whose elements sum to exactly k.
 *
 * Approach:
 *   Two methods are provided:
 *   1) longestSubarrayWithSumKV2 uses a prefix‑sum hash map to record the first index where each cumulative sum occurs.  
 *      For each position i, if (prefixSum - k) exists in the map, a subarray ending at i with sum k is found; its length is compared to the current maximum.
 *   2) longestSubarrayWithSumK employs a sliding‑window (two pointers). The window expands rightwards while adding elements; when the sum exceeds k it shrinks from the left until the sum ≤ k.  
 *      Whenever the sum equals k, the window length is considered for the answer.
 *
 * Complexity:
 *   longestSubarrayWithSumKV2:  O(n) time, O(n) space (hash map).
 *   longestSubarrayWithSumK:    O(n) time, O(1) space.
 */
import java.util.HashMap;
import java.util.Map;

class ImportantLongestSubarrayWithSumKTwoPointer {
    public static int longestSubarrayWithSumKV2(int[] nums, int k) {
        Map<Integer, Integer> prefixSumIndex = new HashMap<>();
        int prefixSum = 0;
        int maxLength = 0;

        for (int i = 0; i < nums.length; i++) {
            prefixSum += nums[i];

            if (prefixSum == k) {
                maxLength = i + 1;
            }

            if (prefixSumIndex.containsKey(prefixSum - k)) {
                maxLength = Math.max(maxLength, i - prefixSumIndex.get(prefixSum - k));
            }

            // Only store first occurrence to maximize length
            prefixSumIndex.putIfAbsent(prefixSum, i);
        }
        return maxLength;
    }

    public static int longestSubarrayWithSumK(int[] nums, int k) {
        int left = 0;
        int right = 0;
        int currentSum = 0;
        int maxLength = 0;
        while (right < nums.length) {
            // remember just sum at first place and increment at last place
            currentSum += nums[right];
            // it's while not if it will be a common mistake
            while (currentSum > k && left <= right) {
                currentSum -= nums[left];
                left++;
            }
            if (currentSum == k)
                maxLength = Math.max(maxLength, right - left + 1);

            right++;
        }
        return maxLength;
    }

    public static void main(String[] args) {
        ImportantLongestSubarrayWithSumKTwoPointer solution = new ImportantLongestSubarrayWithSumKTwoPointer();

        // Example test cases
        int[] nums1 = {1, 2, 3, 7, 5};
        int k1 = 12;
        System.out.println("Longest subarray length with sum " + k1 + ": " + solution.longestSubarrayWithSumK(nums1, k1));
        // Expected output: 2 (subarray [5, 7])

        int[] nums2 = {4, 1, 1, 1, 2, 3, 5};
        int k2 = 5;
        System.out.println("Longest subarray length with sum " + k2 + ": " + solution.longestSubarrayWithSumK(nums2, k2));
        // Expected output: 4 (subarray [1, 1, 1, 2])

        int[] nums3 = {5, 8, -1, 3, 2};
        int k3 = 10;
        System.out.println("Longest subarray length with sum " + k3 + ": " + solution.longestSubarrayWithSumK(nums3, k3));
        // Expected output: 2 (subarray [8, -1, 3])

        int[] nums4 = {10, 5, 2, 7, 1, 9};
        int k4 = 15;
        System.out.println("Longest subarray length with sum " + k4 + ": " + solution.longestSubarrayWithSumK(nums4, k4));
        // Expected output: 4 (subarray [5, 2, 7, 1])
    }
}

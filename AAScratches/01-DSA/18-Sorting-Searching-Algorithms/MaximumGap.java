/**
 * Given an integer array nums, return the maximum difference between two
 * <p>
 * successive elements in its sorted form. If the
 * <p>
 * array contains less than two elements, return 0.
 * <p>
 * You must write an algorithm that runs in linear time and uses linear extra space.
 * <p>
 * Example 1:
 * <p>
 * Input: nums = [3,6,9,1]
 * Output: 3
 * Explanation: The sorted form of the array is [1,3,6,9],
 * <p>
 * either (3,6) or (6,9) has the maximum difference 3.
 */

import java.util.Arrays;
// https://leetcode.com/problems/maximum-gap/description/
// 164. Maximum Gap
class MaximumGap {
    public int maximumGap(int[] nums) {
        int min = nums[0], max = nums[0], n = nums.length;
        for (int x : nums) {
            min = Math.min(min, x);
            max = Math.max(max, x);
        }
        if (min == max) return 0; // All elements are the same
        int bucketSize = (int) Math.ceil((double) (max - min) / (n - 1));

        int[] minBucket = new int[n];
        int[] maxBucket = new int[n];

        Arrays.fill(minBucket, Integer.MAX_VALUE);
        Arrays.fill(maxBucket, Integer.MIN_VALUE);

        for (int x : nums) {
            int idx = (x - min) / bucketSize;
            minBucket[idx] = Math.min(x, minBucket[idx]);
            maxBucket[idx] = Math.max(x, maxBucket[idx]);
        }
        int maxGap = bucketSize; // Maximum gap is always greater or equal to bucketSize
        int previousBucketMax = maxBucket[0]; // We always have 0th bucket

        for (int i = 1; i < n; i++) {
            if (minBucket[i] == Integer.MAX_VALUE) continue; // Skip empty bucket
            maxGap = Math.max(maxGap, minBucket[i] - previousBucketMax);
            previousBucketMax = maxBucket[i];
        }
        return maxGap;
    }
}
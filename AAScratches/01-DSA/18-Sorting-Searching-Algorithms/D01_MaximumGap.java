/*
 * =====================================================================
 *  Maximum Gap                                        LeetCode 164 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given an unsorted int array nums, return the largest difference between two
 *   successive elements once the array is sorted. Return 0 if the array has
 *   fewer than two elements. It must run in linear time and linear extra space,
 *   so sorting first is not allowed as the final answer.
 *
 * EXAMPLE
 *   nums = [3, 6, 9, 1]   ->  3    sorted [1, 3, 6, 9]; gaps 2, 3, 3
 *   nums = [1]            ->  0    fewer than two elements
 *   nums = [7, 7, 7]      ->  0    all equal, every gap is 0
 *   nums = [1, 3, 100]    ->  97   sorted [1, 3, 100]; gaps 2, 97
 *
 * APPROACH  (pigeonhole bucketing by gap size)
 *   1. One pass to get min and max. If min == max every gap is 0.
 *   2. Choose bucketSize = ceil((max - min) / (n - 1)). The n values leave n-1
 *      gaps summing to (max - min), so the AVERAGE gap is (max-min)/(n-1) and
 *      the largest gap is at least that - hence at least bucketSize.
 *   3. Drop each value into bucket (x - min) / bucketSize, keeping only the min
 *      and max seen in each bucket. Two values inside one bucket differ by less
 *      than bucketSize, so no within-bucket pair can be the answer.
 *   4. Walk the buckets left to right, skipping empty ones, and take the best
 *      (min of this bucket - max of the previous non-empty bucket).
 *   5. Start the answer at bucketSize, which is a proven lower bound.
 *
 * KEY INSIGHT
 *   You do not need the sorted order, only the largest hole in it. Sizing the
 *   buckets at the average gap guarantees the winning pair straddles a bucket
 *   boundary, so each bucket collapses to two numbers and the scan is linear.
 *   Same pigeonhole trick as "contains nearby almost-duplicate" (LeetCode 220).
 *
 * COMPLEXITY
 *   Time  O(n)  three linear passes (min/max, fill buckets, scan buckets).
 *   Space O(n)  two arrays of n bucket slots.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is bucketSize the average gap and not something smaller or larger?
 *   - Why is it safe to store only min and max per bucket?
 *   - Radix sort also solves this in O(n) - compare the constants and memory.
 *   - What breaks if values span the full int range? (max - min overflows; use
 *     long, or note LeetCode's 0 <= nums[i] <= 1e9 constraint)
 *
 * RUN
 *   main() runs 5 cases (typical, single element, all equal, two elements,
 *   sparse) and prints the bucket answer, an O(n log n) sorted cross-check,
 *   and the expected value on one line.
 *
 * Fixed: an empty array threw ArrayIndexOutOfBounds on nums[0]; now returns 0
 *        as the problem statement requires for fewer than two elements.
 */

import java.util.Arrays;

// https://leetcode.com/problems/maximum-gap/description/
class MaximumGap {

    // Linear-time answer: bucket by the average gap, compare across buckets.
    public int maximumGap(int[] nums) {
        if (nums == null || nums.length < 2) return 0;

        int n = nums.length;
        int min = nums[0], max = nums[0];
        for (int x : nums) {
            min = Math.min(min, x);
            max = Math.max(max, x);
        }
        if (min == max) return 0; // all elements identical

        // Average gap, rounded up. The true answer is an integer >= this value.
        int bucketSize = (int) Math.ceil((double) (max - min) / (n - 1));

        int[] minBucket = new int[n];
        int[] maxBucket = new int[n];
        Arrays.fill(minBucket, Integer.MAX_VALUE);
        Arrays.fill(maxBucket, Integer.MIN_VALUE);

        for (int x : nums) {
            int idx = (x - min) / bucketSize; // max lands in bucket n-1 at the worst
            minBucket[idx] = Math.min(x, minBucket[idx]);
            maxBucket[idx] = Math.max(x, maxBucket[idx]);
        }

        int maxGap = bucketSize;              // proven lower bound from step 2
        int previousBucketMax = maxBucket[0]; // min lives here, so bucket 0 is never empty

        for (int i = 1; i < n; i++) {
            if (minBucket[i] == Integer.MAX_VALUE) continue; // empty bucket: nothing to compare
            maxGap = Math.max(maxGap, minBucket[i] - previousBucketMax);
            previousBucketMax = maxBucket[i];
        }
        return maxGap;
    }

    // O(n log n) reference used to cross-check the linear answer in main().
    public int maximumGapBySorting(int[] nums) {
        if (nums == null || nums.length < 2) return 0;
        int[] copy = nums.clone(); // do not disturb the caller's array
        Arrays.sort(copy);
        int maxGap = 0;
        for (int i = 1; i < copy.length; i++) {
            maxGap = Math.max(maxGap, copy[i] - copy[i - 1]);
        }
        return maxGap;
    }

    private void print(String label, int[] nums, int expected) {
        System.out.println(label + " " + Arrays.toString(nums)
                + " -> buckets " + maximumGap(nums)
                + ", sorted " + maximumGapBySorting(nums)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        MaximumGap solver = new MaximumGap();

        solver.print("case 1 (typical)  ", new int[]{3, 6, 9, 1}, 3);
        solver.print("case 2 (single)   ", new int[]{1}, 0);
        solver.print("case 3 (all equal)", new int[]{7, 7, 7}, 0);
        solver.print("case 4 (two only) ", new int[]{1, 10000000}, 9999999);
        solver.print("case 5 (sparse)   ", new int[]{1, 3, 100}, 97);
    }
}

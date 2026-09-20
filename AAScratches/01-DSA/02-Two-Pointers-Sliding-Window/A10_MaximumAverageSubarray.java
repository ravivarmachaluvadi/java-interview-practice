/*
 * =====================================================================
 *  Maximum Average Subarray I                 LeetCode 643 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an int array nums and an int k (1 <= k <= nums.length), find the
 *   contiguous subarray of length exactly k with the largest average and
 *   return that average as a double. Values may be negative.
 *
 * EXAMPLE
 *   nums = [1, 12, -5, -6, 50, 3], k = 4  ->  12.75   from [12, -5, -6, 50]
 *   nums = [5], k = 1                     ->  5.0     single element
 *   nums = [-1, -2, -3], k = 2            ->  -1.5    all negative
 *
 * APPROACH  (Fixed-size rolling sum)
 *   1. Sum the first k elements; that is both the current window and the best.
 *   2. For each i from k to n-1: slide the window one step by adding nums[i]
 *      and subtracting nums[i - k], the element that just fell out the left.
 *   3. Keep the maximum window sum seen.
 *   4. Return maxSum / k. Maximising the sum is the same as maximising the
 *      average because k is fixed.
 *
 * KEY INSIGHT
 *   A window of fixed width never needs to be re-summed: each slide changes
 *   exactly two elements (one enters, one leaves), so the sum updates in O(1).
 *   "Add the right, subtract the left" is the fixed-window primitive; every
 *   variable-width window later is this loop plus a rule for when to shrink.
 *
 * COMPLEXITY
 *   Time  O(n)  each element is added once and subtracted once
 *   Space O(1)  two running doubles
 *
 * INTERVIEW FOLLOW-UPS
 *   - Length at least k (LeetCode 644): binary search on the answer with
 *     prefix sums, or the "at least k" trick with a running best prefix.
 *   - Return the window's start index as well as the average.
 *   - Integer overflow: use long for the sum when values are large.
 *
 * RUN
 *   main() runs 3 cases (typical, single element, all negative) and prints
 *   actual vs expected.
 */
class MaximumAverageSubarray {

    public static double findMaxAverage(int[] nums, int k) {
        double windowSum = 0;
        for (int i = 0; i < k; i++) {
            windowSum += nums[i];
        }
        double maxSum = windowSum;

        for (int i = k; i < nums.length; i++) {
            // nums[i] enters the window, nums[i - k] leaves it
            windowSum += nums[i] - nums[i - k];
            maxSum = Math.max(maxSum, windowSum);
        }
        return maxSum / k;
    }

    public static void main(String[] args) {
        System.out.println("case 1 (typical):        "
                + findMaxAverage(new int[]{1, 12, -5, -6, 50, 3}, 4) + "   expected 12.75");
        System.out.println("case 2 (single, k = n):  "
                + findMaxAverage(new int[]{5}, 1) + "   expected 5.0");
        System.out.println("case 3 (all negative):   "
                + findMaxAverage(new int[]{-1, -2, -3}, 2) + "   expected -1.5");
    }
}

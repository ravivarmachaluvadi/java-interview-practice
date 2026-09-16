/**
 * LeetCode 209 — Minimum Size Subarray Sum.
 *
 * <p>Given a positive integer {@code target} and an array of positive integers
 * {@code nums}, return the minimal length of a contiguous subarray whose sum is
 * greater than or equal to {@code target}. If no such subarray exists, return 0.</p>
 *
 * <h2>Approach — variable-size sliding window</h2>
 * <p>Expand the window by moving {@code windowEnd} forward, adding each new element
 * to {@code currentSum}. Whenever the window's sum meets or exceeds {@code target},
 * record its length and then shrink from the left ({@code windowStart}) as far as
 * possible while the sum still qualifies — this greedily looks for a shorter window
 * at the same right edge before advancing further.</p>
 *
 * <p>Because all elements are positive, {@code currentSum} only decreases as the
 * window shrinks and only increases as it grows, so the two-pointer approach is
 * valid: neither pointer ever needs to move backward.</p>
 *
 * <p>Time: {@code O(n)} — each index enters and leaves the window at most once, so
 * total pointer movement is {@code O(n)} despite the nested loop. Space: {@code O(1)}.</p>
 *
 * <p>This class provides two implementations of the identical algorithm, differing
 * only in loop structure and index bookkeeping — included to show both styles are
 * equivalent and to cross-check them against each other.</p>
 */
class SmallestSubarraySum {
    public static int minSubArrayLen(int target, int[] nums) {
        int minLength = Integer.MAX_VALUE;
        int windowStart = 0;
        int currentSum = 0;
        for (int windowEnd = 0; windowEnd < nums.length; windowEnd++) {
            currentSum += nums[windowEnd];
            while (currentSum >= target) {
                minLength = Math.min(minLength, windowEnd - windowStart + 1);
                currentSum -= nums[windowStart];
                windowStart++;
            }
        }
        return minLength == Integer.MAX_VALUE ? 0 : minLength;
    }

    public static void main(String[] args) {
        int target = 7;
        int[] nums = {2, 3, 1, 2, 4, 3};
        int result = minSubArrayLen(target, nums);
        System.out.println("The smallest subarray length is: " + result); // Output: 2
    }
}

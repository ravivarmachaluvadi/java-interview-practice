/*
 * =====================================================================
 *  Minimum Size Subarray Sum                  LeetCode 209 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a positive target and an array of POSITIVE integers, return the length of the
 *   shortest contiguous subarray whose sum is >= target. Return 0 if none exists.
 *
 * EXAMPLE
 *   target = 7,  nums = [2, 3, 1, 2, 4, 3]     ->  2    [4, 3]
 *   target = 4,  nums = [1, 4, 4]              ->  1    one element is enough
 *   target = 15, nums = [1, 2, 3, 4, 5]        ->  5    needs the whole array
 *   target = 11, nums = [1, 1, 1, 1, 1, 1, 1]  ->  0    never reachable
 *   target = 5,  nums = []                     ->  0
 *
 * APPROACH  (variable-size sliding window)
 *   1. Grow the window by moving windowEnd right and adding nums[windowEnd] to the sum.
 *   2. While the sum is >= target, record the window length, then shrink from the left:
 *      subtract nums[windowStart] and advance windowStart.
 *   3. Keep the smallest length seen; return 0 if it was never updated.
 *
 * KEY INSIGHT
 *   Because every element is positive, growing the window only increases the sum and
 *   shrinking only decreases it. So once a window qualifies, no earlier start can give a
 *   shorter qualifying window at this end, and neither pointer ever needs to go back.
 *   The nested while is still O(n): windowStart only ever moves forward, so across the
 *   whole run it advances at most n times. Count pointer moves, not loop nesting.
 *
 * COMPLEXITY
 *   Time  O(n)  each index enters the window once and leaves it once
 *   Space O(1)  two pointers and a running sum
 *
 * INTERVIEW FOLLOW-UPS
 *   - Negatives allowed: the monotonic argument breaks; use prefix sums + monotonic deque.
 *   - O(n log n) alternative: prefix sums + binary search for each start.
 *   - Sum exactly equal to target (not >=): sliding window still works for positives.
 *   - Longest subarray with sum <= target: same window, record on the other condition.
 *
 * RUN
 *   main() runs 5 cases (typical, single-element answer, whole array, unreachable, empty)
 *   and prints actual vs expected.
 */

class SmallestSubarraySum {

    public static int minSubArrayLen(int target, int[] nums) {
        int minLength = Integer.MAX_VALUE;
        int windowStart = 0;
        int currentSum = 0;

        for (int windowEnd = 0; windowEnd < nums.length; windowEnd++) {
            currentSum += nums[windowEnd];

            // Window qualifies: record it, then try to make it shorter from the left.
            while (currentSum >= target) {
                minLength = Math.min(minLength, windowEnd - windowStart + 1);
                currentSum -= nums[windowStart];
                windowStart++;
            }
        }
        return minLength == Integer.MAX_VALUE ? 0 : minLength;
    }

    private static void run(String label, int target, int[] nums, int expected) {
        System.out.println(label + ": " + minSubArrayLen(target, nums) + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 typical      ", 7, new int[]{2, 3, 1, 2, 4, 3}, 2);
        run("case 2 single elem  ", 4, new int[]{1, 4, 4}, 1);
        run("case 3 whole array  ", 15, new int[]{1, 2, 3, 4, 5}, 5);
        run("case 4 unreachable  ", 11, new int[]{1, 1, 1, 1, 1, 1, 1}, 0);
        run("case 5 empty        ", 5, new int[]{}, 0);
    }
}

/*
 * =====================================================================
 *  Maximum Subarray (Kadane)               LeetCode 53 | Medium    MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array nums (length >= 1, may be all negative), find the contiguous
 *   subarray with the largest sum and return that sum. The subarray must contain at least
 *   one element, so an empty subarray with sum 0 is not allowed.
 *
 * EXAMPLE
 *   nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]  ->  6     subarray [4, -1, 2, 1]
 *   nums = [-3, -1, -4, -1, -5]             ->  -1    all negative: best is the lone -1
 *   nums = [7]                              ->  7     single element
 *   nums = [5, 4, -1, 7, 8]                 ->  23    whole array is best
 *
 * APPROACH  (Kadane: best subarray ending here)
 *   maxSubArray (author's reset form):
 *   1. Walk left to right keeping `sum`, the sum of the current candidate subarray.
 *   2. Add num to sum, then record maxSum = max(maxSum, sum) BEFORE any reset, so a lone
 *      negative element is still captured when every element is negative.
 *   3. If sum < 0, reset it to 0: a negative prefix can only drag down whatever follows.
 *   maxSubArrayRecurrence (textbook form, same answers):
 *   1. bestEndingHere = max(num, bestEndingHere + num)  "extend or start fresh".
 *   2. best = max(best, bestEndingHere).
 *
 * KEY INSIGHT
 *   The only decision at each index is: extend the previous best subarray, or start a new
 *   one here. Extending is worth it exactly when the previous best sum is positive.
 *   This "best answer ending at i, derived from best ending at i-1" recurrence is the seed
 *   of every DP-on-arrays problem; recognise it whenever the answer is a contiguous run.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass Space O(1)  two running scalars
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the subarray itself: record a tentative start on each reset and commit start
 *     and end whenever maxSum improves.
 *   - All negative numbers? Update the max before resetting (or use the recurrence form);
 *     initialising max to 0 is the classic bug.
 *   - Maximum product subarray (LeetCode 152): track both max and min ending here, because
 *     a negative times a negative flips sign.
 *   - Circular array (LeetCode 918): answer is max(Kadane max, total - Kadane min), unless
 *     every element is negative.
 *
 * RUN
 *   main() runs 4 cases through both methods and prints actual vs expected.
 */

class KadaneSAlgorithm {

    /** Author's form: running sum reset to 0 when it goes negative; max recorded before reset. */
    public static int maxSubArray(int[] nums) {
        long maxSum = Long.MIN_VALUE;   // sentinel below any int, so the first element always wins
        long sum = 0;
        for (int num : nums) {
            sum += num;
            if (sum > maxSum) {
                maxSum = sum;           // record BEFORE the reset so all-negative inputs work
            }
            if (sum < 0) {
                sum = 0;                // a negative prefix never helps a later subarray
            }
        }
        return (int) maxSum;
    }

    /** Textbook form: best subarray ending at i is either nums[i] alone or extended from i-1. */
    public static int maxSubArrayRecurrence(int[] nums) {
        int bestEndingHere = nums[0];
        int best = nums[0];
        for (int i = 1; i < nums.length; i++) {
            bestEndingHere = Math.max(nums[i], bestEndingHere + nums[i]);   // extend or restart
            best = Math.max(best, bestEndingHere);
        }
        return best;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    private static void runBoth(String label, int[] nums, int expected) {
        print(label + " reset form     ", maxSubArray(nums), expected);
        print(label + " recurrence form", maxSubArrayRecurrence(nums), expected);
    }

    public static void main(String[] args) {
        runBoth("case 1 mixed       ", new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}, 6);
        runBoth("case 2 all negative", new int[]{-3, -1, -4, -1, -5}, -1);
        runBoth("case 3 single      ", new int[]{7}, 7);
        runBoth("case 4 whole array ", new int[]{5, 4, -1, 7, 8}, 23);
    }
}

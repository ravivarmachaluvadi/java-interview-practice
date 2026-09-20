/*
 * =====================================================================
 *  Max Consecutive Ones                              LeetCode 485 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a binary array (only 0s and 1s), return the length of the longest
 *   run of consecutive 1s. An empty or all-zero array gives 0.
 *
 * EXAMPLE
 *   [1, 1, 0, 1, 1, 1]  ->  3   runs of 2 and 3; the last one wins
 *   [1, 0, 1, 1, 0, 1]  ->  2 [1, 0, 1, 1, 1]     ->  3   longest run sits at the very end (the
 *   trap)
 *   [1, 1, 1, 1]        ->  4   whole array is one run
 *   [0, 0, 0]           ->  0   no ones at all
 *   []                  ->  0
 *
 * APPROACH  (running streak with reset)
 *   1. currentRun = length of the run of 1s ending at the current index.
 *   2. On a 1, extend it by one; on a 0, reset it to zero.
 *   3. After every element, maxCount = max(maxCount, currentRun).
 *
 * KEY INSIGHT
 *   Update the best answer after every step, not only when a run ends. A run
 *   that reaches the last index never "ends", so code that records the max
 *   only on a 0 returns 2 for [1, 0, 1, 1, 1]. Comparing on every element
 *   removes the special case entirely. This streak counter is reused as-is
 *   by "count zero-filled subarrays" and the sliding-window problems.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass Space O(1)  two counters
 *
 * INTERVIEW FOLLOW-UPS
 *   - Allowed to flip at most one 0 (LeetCode 487)? Sliding window with a zero budget.
 *   - Allowed to flip k zeros (LeetCode 1004)? Same window, budget k.
 *   - Return the start index of the longest run as well as its length.
 *   - Longest run of any equal value, not just 1s.
 *
 * RUN
 *   main() runs 6 cases (typical, two runs, trailing run, all ones, all zeros,
 *   empty) and prints actual vs expected.
 */
import java.util.Arrays;

class MaxConsecutiveOnes {

    public static int findMaxConsecutiveOnes(int[] nums) {
        if (nums == null) {
            return 0;
        }

        int currentRun = 0;
        int maxCount = 0;

        for (int num : nums) {
            currentRun = (num == 1) ? currentRun + 1 : 0; // extend or reset the streak
            maxCount = Math.max(maxCount, currentRun);    // every step, so a trailing run counts
        }

        return maxCount;
    }

    private static void check(int[] nums, int expected) {
        System.out.println(Arrays.toString(nums) + " -> " + findMaxConsecutiveOnes(nums)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        check(new int[]{1, 1, 0, 1, 1, 1}, 3);   // LeetCode example 1
        check(new int[]{1, 0, 1, 1, 0, 1}, 2);   // LeetCode example 2
        check(new int[]{1, 0, 1, 1, 1}, 3);      // longest run at the end: the classic bug
        check(new int[]{1, 1, 1, 1}, 4);         // whole array is one run
        check(new int[]{0, 0, 0}, 0);            // no ones
        check(new int[]{}, 0);                   // empty
    }
}

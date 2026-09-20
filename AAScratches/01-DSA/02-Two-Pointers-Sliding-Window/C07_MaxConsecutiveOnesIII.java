/*
 * =====================================================================
 *  Max Consecutive Ones III                  LeetCode 1004 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a binary array nums and an integer k, you may flip at most k zeros to
 *   ones. Return the length of the longest run of consecutive 1s you can get.
 *   Equivalently: the longest subarray containing at most k zeros.
 *
 * EXAMPLE
 *   nums = [1,1,0,0,1,1,1,0,1,1], k = 2     ->  7   flip zeros at index 3 and 7
 *   nums = [1,1,1,0,0,0,1,1,1,1,0], k = 2   ->  6   LeetCode example 1
 *   nums = [0,0,0], k = 0                   ->  0   nothing may be flipped
 *   nums = [0,0,0], k = 3                   ->  3   k covers every zero
 *   nums = [1,1,1], k = 0                   ->  3   already all ones
 *
 * APPROACH  (variable window, at most k violations)
 *   1. Walk right over the array; count zeros currently inside [left, right].
 *   2. While zeroCount > k the window is invalid: drop nums[left] (decrement
 *      zeroCount if it was a zero) and move left forward.
 *   3. The window is valid again; record its length right - left + 1.
 *   4. The largest valid window seen is the answer.
 *
 * KEY INSIGHT
 *   Reframe "flip up to k zeros" as "a window may contain at most k zeros".
 *   Carry ONE counter of the rule you are allowed to break, expand freely, and
 *   shrink only while the rule is broken. This "at most K violations" template
 *   is reused by Fruit Into Baskets, Character Replacement and Longest Subarray
 *   After Deleting One (k = 1).
 *
 * COMPLEXITY
 *   Time  O(n)   left and right each advance at most n times
 *   Space O(1)   two indices, a zero counter and the best length
 *
 * INTERVIEW FOLLOW-UPS
 *   - k = 0 reduces to Max Consecutive Ones I; k = 1 is LC 487 / LC 1493.
 *   - At most k distinct values instead of k zeros: replace the counter with a map.
 *   - Non-shrinking window variant: never let the window get smaller, just slide it.
 *   - Return the window's start index as well as the length.
 *
 * RUN
 *   main() runs 5 cases (typical, LeetCode example, k = 0, k covers all, all ones)
 *   and prints actual vs expected.
 */
class MaxConsecutiveOnesIII {

    public static int longestOnes(int[] nums, int k) {
        int left = 0;
        int maxLength = 0;
        int zeroCount = 0; // zeros inside the current window [left, right]

        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0) {
                zeroCount++;
            }
            // Too many zeros: shrink from the left until the window is valid again.
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

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] typical = {1, 1, 0, 0, 1, 1, 1, 0, 1, 1};
        int[] leetcodeExample = {1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0};
        print("case 1 typical, k=2     ", longestOnes(typical, 2), 7);
        print("case 2 leetcode ex, k=2 ", longestOnes(leetcodeExample, 2), 6);
        print("case 3 all zeros, k=0   ", longestOnes(new int[]{0, 0, 0}, 0), 0);
        print("case 4 all zeros, k=3   ", longestOnes(new int[]{0, 0, 0}, 3), 3);
        print("case 5 all ones, k=0    ", longestOnes(new int[]{1, 1, 1}, 0), 3);
    }
}

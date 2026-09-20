/*
 * =====================================================================
 *  132 Pattern                                          LeetCode 456 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array, decide whether there exist indices i < j < k with
 *   nums[i] < nums[k] < nums[j]  (a small value, then the largest, then a
 *   middle value). Return true or false. Arrays shorter than 3 are false.
 *
 * EXAMPLE
 *   [3, 1, 6, 5, 4, 3, 2]  ->  true    1 < 5 < 6  (or 1 < 2 < 6, etc.)
 *   [-1, 3, 2, 0]          ->  true    -1 < 2 < 3
 *   [1, 2, 3, 4]           ->  false   strictly increasing, no "3 then 2"
 *   [4, 3, 2, 1]           ->  false   nothing smaller before a peak
 *   [1, 2]                 ->  false   fewer than 3 elements
 *
 * APPROACH  (right-to-left monotonic stack, track the best "2")
 *   1. Scan from the right. Keep a stack of candidates for the "3" (the peak)
 *      that is decreasing from bottom to top.
 *   2. Keep 'second' = the largest value ever popped: a value that has some
 *      bigger element to its right, i.e. the best "2" seen so far.
 *   3. If nums[i] < second, nums[i] is a valid "1": pattern found.
 *   4. Otherwise pop every stack value smaller than nums[i] into 'second'
 *      (nums[i] becomes their "3"), then push nums[i].
 *
 * KEY INSIGHT
 *   Fix the "3" as the element being scanned and ask: what is the largest "2"
 *   that already has a bigger element to its right? The largest popped value
 *   is exactly that, and a larger "2" only makes the "1 < 2" check easier.
 *   'second' never needs to decrease because pops only happen when a bigger
 *   "3" arrives, so every popped value is a legitimate "2" with a "3" after it.
 *
 * COMPLEXITY
 *   Time  O(n)  each element pushed and popped at most once
 *   Space O(n)  stack in the worst case (increasing input scanned right to left)
 *
 * INTERVIEW FOLLOW-UPS
 *   - O(n^2) baseline: for each j, track min on the left and scan k to the right.
 *   - Return the actual indices (i, j, k) rather than a boolean.
 *   - Why scan right to left? Scanning left to right you know the "1" (prefix
 *     min) but not the "3", and the stack cannot look ahead.
 *   - Count all 132 subsequences: no longer a stack problem, needs BIT/DP.
 *
 * RUN
 *   main() runs 5 cases (typical, negative, increasing, decreasing, too short)
 *   and prints actual vs expected.
 */

import java.util.Arrays;
import java.util.Stack;

class Find132pattern {

    public static boolean find132pattern(int[] nums) {
        int n = nums.length;
        if (n < 3) return false;

        Stack<Integer> peakCandidates = new Stack<>(); // candidates for the "3", decreasing upward
        int second = Integer.MIN_VALUE;                // best "2": largest value popped so far

        for (int i = n - 1; i >= 0; i--) {
            if (nums[i] < second) {
                return true; // nums[i] is the "1" for the already-found "3 ... 2"
            }
            // nums[i] is bigger than these, so it serves as their "3"; remember the largest
            while (!peakCandidates.isEmpty() && nums[i] > peakCandidates.peek()) {
                second = peakCandidates.pop();
            }
            peakCandidates.push(nums[i]);
        }
        return false;
    }

    static void print(String label, int[] nums, boolean expected) {
        System.out.println(label + " " + Arrays.toString(nums) + ": "
                + find132pattern(nums) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical   ", new int[]{3, 1, 6, 5, 4, 3, 2}, true);
        print("case 2 negative  ", new int[]{-1, 3, 2, 0}, true);
        print("case 3 increasing", new int[]{1, 2, 3, 4}, false);
        print("case 4 decreasing", new int[]{4, 3, 2, 1}, false);
        print("case 5 too short ", new int[]{1, 2}, false);
    }
}

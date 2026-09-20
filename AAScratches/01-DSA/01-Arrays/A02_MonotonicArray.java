/*
 * =====================================================================
 *  Monotonic Array                                   LeetCode 896 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   An array is monotonic if it is entirely non-increasing or entirely
 *   non-decreasing. Given an integer array, return true if it is monotonic.
 *   Equal neighbours are allowed in both directions; arrays with 0, 1 or 2
 *   elements are always monotonic.
 *
 * EXAMPLE
 *   [1, 2, 2, 3]  ->  true    non-decreasing
 *   [6, 5, 4, 4]  ->  true    non-increasing
 *   [1, 3, 2]     ->  false   goes up then down
 *   [7, 7, 7]     ->  true    flat counts as both
 *   []            ->  true [1, 2, 2, 1]  ->  false   the plateau hides a turn
 *
 * APPROACH  (two boolean flags, one pass)
 *   1. Start with increasing = true and decreasing = true (both hypotheses alive).
 *   2. For each adjacent pair, a strict rise (nums[i] > nums[i-1]) kills
 *      "decreasing"; a strict fall kills "increasing". Equal pairs kill neither.
 *   3. After the pass, the array is monotonic if either flag survived.
 *
 * KEY INSIGHT
 *   Instead of scanning once for "is it increasing?" and again for "is it
 *   decreasing?", carry both hypotheses through a single scan and let the
 *   data falsify them. Same adjacent-pair loop as A01_ArraySortedOrNot, but testing two
 *   properties at once; a rise and a fall both seen means neither survives.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass over adjacent pairs
 *   Space O(1)  two booleans
 *
 * INTERVIEW FOLLOW-UPS
 *   - Strictly monotonic? Then an equal pair must kill both flags.
 *   - Early exit: return false as soon as both flags are false.
 *   - Compare with the "decide direction from the first unequal pair" version,
 *     which needs an extra scan to find that pair.
 *
 * RUN
 *   main() runs 6 cases (up, down, turn, flat, empty, plateau-then-turn) and
 *   prints actual vs expected.
 */
import java.util.Arrays;

class MonotonicArray {

    public static boolean isMonotonic(int[] nums) {
        if (nums.length <= 2) {
            return true; // 0, 1 or 2 elements can only go one way
        }

        boolean increasing = true;
        boolean decreasing = true;

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] > nums[i - 1]) {
                decreasing = false; // saw a rise, so it cannot be non-increasing
            }
            if (nums[i] < nums[i - 1]) {
                increasing = false; // saw a fall, so it cannot be non-decreasing
            }
            // equal neighbours leave both flags untouched
        }

        return increasing || decreasing;
    }

    private static void check(int[] nums, boolean expected) {
        System.out.println(Arrays.toString(nums) + " -> " + isMonotonic(nums)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        check(new int[]{1, 2, 2, 3}, true);    // non-decreasing
        check(new int[]{6, 5, 4, 4}, true);    // non-increasing
        check(new int[]{1, 3, 2}, false);      // up then down
        check(new int[]{7, 7, 7}, true);       // all equal
        check(new int[]{}, true);              // empty
        check(new int[]{1, 2, 2, 1}, false);   // plateau then a turn
    }
}

/*
 * =====================================================================
 *  Move Zeroes                                LeetCode 283 | Easy    MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an int array nums, move every 0 to the end while keeping the relative order
 *   of the non-zero elements. Must be done in place with no copy of the array.
 *   This file also shows the general form: move every occurrence of any target to the end.
 *
 * EXAMPLE
 *   nums = [0, 1, 0, 3, 12]           ->  [1, 3, 12, 0, 0]
 *   nums = [0]                        ->  [0]                     (single element)
 *   nums = [0, 0, 0]                  ->  [0, 0, 0]               (all target)
 *   nums = [3, 5, 2, 3, 8, 3, 1], target 3  ->  [5, 2, 8, 1, 3, 3, 3]   (general form)
 *
 * APPROACH  (compact then fill tail)
 *   1. writeIndex = 0 is the next slot for a non-target element.
 *   2. Pass 1: scan left to right; copy every non-target element to nums[writeIndex++].
 *      Order is preserved because we scan in order and write in order.
 *   3. After pass 1, indices [writeIndex, n) hold stale values (already copied forward
 *      or targets). Pass 2: overwrite that tail with the target.
 *   4. moveZeroes(nums) is simply moveElementToEnd(nums, 0).
 *
 * KEY INSIGHT
 *   Same write-pointer primitive as Remove Element, plus one extra step: because the
 *   problem wants the removed values to still exist at the end, fill the dead tail
 *   with them. "Compact, then fill the tail" is the shape hidden inside many harder
 *   partition problems, so recognise it on sight.
 *
 * COMPLEXITY
 *   Time  O(n)  two linear passes over the array
 *   Space O(1)  one index, no extra array
 *
 * INTERVIEW FOLLOW-UPS
 *   - Single pass with swaps: when nums[i] != 0, swap nums[i] with nums[writeIndex].
 *     Same order guarantee, fewer writes when zeros are rare.
 *   - Minimise total writes: the two-pass version writes exactly n times; the swap
 *     version writes 2 * (non-zero count) times at worst.
 *   - Move zeros to the FRONT keeping order: scan right to left with a write pointer
 *     starting at n-1.
 *
 * RUN
 *   main() runs 4 cases (LeetCode sample, single element, all zeros, general target)
 *   and prints actual vs expected.
 */

import java.util.Arrays;

class MoveZeroes {

    // LeetCode 283: Move Zeroes. Shown as the target = 0 special case of the general method.
    public static void moveZeroes(int[] nums) {
        moveElementToEnd(nums, 0);
    }

    // Move all occurrences of 'target' to the end of the array, preserving the order of the rest.
    public static void moveElementToEnd(int[] nums, int target) {
        int n = nums.length;
        int writeIndex = 0; // next slot for a non-target element

        // Pass 1: compact every non-target element to the front, preserving order
        for (int i = 0; i < n; i++) {
            if (nums[i] != target) {
                nums[writeIndex++] = nums[i];
            }
        }

        // Pass 2: everything from writeIndex onwards is dead space - fill it with the target
        while (writeIndex < n) {
            nums[writeIndex++] = target;
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] sample = {0, 1, 0, 3, 12};
        moveZeroes(sample);
        print("case 1 LC sample   ", Arrays.toString(sample), "[1, 3, 12, 0, 0]");

        int[] single = {0};
        moveZeroes(single);
        print("case 2 single      ", Arrays.toString(single), "[0]");

        int[] allZero = {0, 0, 0};
        moveZeroes(allZero);
        print("case 3 all zeros   ", Arrays.toString(allZero), "[0, 0, 0]");

        int[] threes = {3, 5, 2, 3, 8, 3, 1};
        moveElementToEnd(threes, 3);
        print("case 4 target = 3  ", Arrays.toString(threes), "[5, 2, 8, 1, 3, 3, 3]");
    }
}

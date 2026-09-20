/*
 * =====================================================================
 *  Sort Array By Parity                             LeetCode 905 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an int array, rearrange it in place so that every even number comes before
 *   every odd number. Any order inside the even group and inside the odd group is fine.
 *   Return the same array. LeetCode guarantees non-negative values; this code relies on
 *   that (Java's % gives -1 for negative odds, which would stall both pointers).
 *
 * EXAMPLE
 *   nums = [3, 1, 2, 4]   ->  [4, 2, 1, 3]     (this implementation's exact output)
 *   nums = [0]            ->  [0]              (single element)
 *   nums = [2, 4, 6]      ->  [2, 4, 6]        (already all even, no swaps)
 *   nums = [1, 3, 5, 2]   ->  [2, 3, 5, 1]     (only one even, it travels to the front)
 *
 * APPROACH  (in-place two-way partition)
 *   1. left = 0, right = n - 1. Invariant: everything before left is even, everything
 *      after right is odd; the unknown region is [left, right].
 *   2. If nums[left] is odd and nums[right] is even (nums[left] % 2 > nums[right] % 2),
 *      swap them. Now nums[left] is even and nums[right] is odd.
 *   3. If nums[left] is even it belongs where it is: left++.
 *   4. If nums[right] is odd it belongs where it is: right--.
 *   5. Loop while left < right. At least one pointer moves each iteration, because after
 *      the optional swap the pair can never be (odd, even) again.
 *
 * KEY INSIGHT
 *   This is the converging pair from Reverse Vowels turned into a PARTITION: instead of
 *   skipping non-matching items, each pointer stops at the first item on the wrong side
 *   and the two wrong items are swapped. It is the one-pivot step behind quicksort's
 *   Hoare partition and the two-boundary step behind Dutch National Flag (Sort Colors).
 *
 * COMPLEXITY
 *   Time  O(n)  each iteration moves left or right at least one step inward
 *   Space O(1)  in place, two indices and one temp
 *
 * INTERVIEW FOLLOW-UPS
 *   - Preserve relative order inside each group: needs the stable write-pointer
 *     approach with an extra array, or O(n^2) shifting in place.
 *   - Sort Array By Parity II (LC 922): evens at even indices, odds at odd indices;
 *     walk an even-index pointer and an odd-index pointer and swap mismatches.
 *   - Three groups (negative, zero, positive or 0/1/2): Dutch National Flag with three
 *     pointers (LC 75 Sort Colors).
 *   - Negative numbers: (-3) % 2 == -1 in Java; use (x & 1) or Math.floorMod for a
 *     parity test that is safe for all ints.
 *
 * RUN
 *   main() runs 4 cases (typical, single element, all even, mostly odd) and prints
 *   actual vs expected.
 */

import java.util.Arrays;

class SortArrayByParity {

    // Evens first, odds later, in place, using converging pointers.
    public static int[] sortArrayByParity(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            // left is odd (1) and right is even (0): both are on the wrong side, swap
            if (nums[left] % 2 > nums[right] % 2) {
                int temp = nums[left];
                nums[left] = nums[right];
                nums[right] = temp;
            }
            // Even on the left is already in place
            if (nums[left] % 2 == 0) {
                left++;
            }
            // Odd on the right is already in place
            if (nums[right] % 2 == 1) {
                right--;
            }
        }
        return nums;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    // Runs the method and formats the result for printing.
    private static String run(int[] nums) {
        return Arrays.toString(sortArrayByParity(nums));
    }

    public static void main(String[] args) {
        print("case 1 typical   ", run(new int[]{3, 1, 2, 4}), "[4, 2, 1, 3]");
        print("case 2 single    ", run(new int[]{0}), "[0]");
        print("case 3 all even  ", run(new int[]{2, 4, 6}), "[2, 4, 6]");
        print("case 4 mostly odd", run(new int[]{1, 3, 5, 2}), "[2, 3, 5, 1]");
    }
}

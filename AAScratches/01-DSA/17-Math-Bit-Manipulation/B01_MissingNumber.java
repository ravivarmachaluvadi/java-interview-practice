/*
 * =====================================================================
 *  Missing Number                          LeetCode 268 | Easy    MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   An array holds n distinct numbers drawn from the range [0, n], so exactly
 *   one value in that range is absent. Return the absent value.
 *   The array length is n, and every element is in [0, n].
 *
 * EXAMPLE
 *   nums = [0, 1, 2, 4]  ->  3     0..4 minus what is present leaves 3
 *   nums = [0]           ->  1     range is [0, 1]; 0 is present, so 1 is missing
 *   nums = [1]           ->  0     the missing value can be 0 itself
 *   nums = [9,6,4,2,3,5,7,0,1] -> 8
 *
 * APPROACH  (sum formula, plus an XOR twin)
 *   1. n = nums.length, so the full range is 0..n.
 *   2. Expected sum of 0..n is n * (n + 1) / 2 (arithmetic series).
 *   3. Subtract the actual sum of the array; the difference is the missing value.
 *   4. XOR version: XOR together every index+1 (that is 1..n) and every element.
 *      Values present in both sides cancel, leaving only the missing one.
 *      0 is safe to ignore because x ^ 0 = x.
 *
 * KEY INSIGHT
 *   You do not need to find the missing number - you compute what the total
 *   should be and let the present numbers cancel themselves out. Sum cancels by
 *   subtraction, XOR cancels by the identity x ^ x = 0. Recognise this pattern
 *   whenever "one element of a known complete set is missing or duplicated".
 *
 * COMPLEXITY
 *   Time  O(n)   one pass over the array for each method
 *   Space O(1)   a couple of accumulators, no extra structure
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why prefer XOR? n * (n + 1) / 2 can overflow int for large n; XOR cannot.
 *   - Two numbers missing instead of one? XOR everything, split on a set bit.
 *   - Can you do it without extra space and without modifying the array? Both do.
 *   - Array sorted instead? Binary search on nums[i] != i gives O(log n).
 *
 * RUN
 *   main() runs 4 cases (typical, two single-element edges, longer shuffle) and
 *   prints actual vs expected for both the sum method and the XOR method.
 */

import java.util.Arrays;

class MissingNumber {

    /** Sum method: expected total of 0..n minus the actual total. */
    public int missingNumber(int[] nums) {
        int n = nums.length;
        int expectedSum = (n * (n + 1)) / 2; // sum of 0..n
        int actualSum = 0;
        for (int num : nums) {
            actualSum += num;
        }
        return expectedSum - actualSum;
    }

    /**
     * XOR method: overflow-free twin of the sum method.
     * xorRange ends up as 1 ^ 2 ^ ... ^ n, xorArray as the XOR of all elements.
     * Every shared value cancels, so the result is the one value only the range has.
     */
    public int missingNumber2(int[] nums) {
        int xorRange = 0;
        int xorArray = 0;
        for (int i = 0; i < nums.length; i++) {
            xorRange ^= (i + 1); // covers 1..n; 0 never changes an XOR
            xorArray ^= nums[i];
        }
        return xorRange ^ xorArray;
    }

    private static void check(int[] nums, int expected, MissingNumber solution) {
        System.out.println("nums = " + Arrays.toString(nums)
                + "   sum -> " + solution.missingNumber(nums)
                + "   xor -> " + solution.missingNumber2(nums)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        MissingNumber solution = new MissingNumber();

        check(new int[]{0, 1, 2, 4}, 3, solution);              // typical
        check(new int[]{0}, 1, solution);                        // edge: missing top of range
        check(new int[]{1}, 0, solution);                        // edge: missing zero
        check(new int[]{9, 6, 4, 2, 3, 5, 7, 0, 1}, 8, solution); // tricky: unsorted, longer
    }
}

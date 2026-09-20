/*
 * =====================================================================
 *  Maximum Product Subarray                        LeetCode 152 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array, return the largest product obtainable from a contiguous
 *   non-empty subarray. The array may hold negatives and zeros, which is the whole
 *   difficulty: a negative can turn the worst prefix into the best one, and a zero
 *   cuts the array into independent pieces.
 *
 * EXAMPLE
 *   arr = [1, 2, -3, 0, -4, -5]  -> 20   subarray [-4, -5]
 *   arr = [2, 3, -2, 4]          -> 6    subarray [2, 3]
 *   arr = [-2, 0, -1]            ->  0   every non-zero subarray is negative
 *   arr = [-2]                   -> -2   single element, must be taken
 *
 * APPROACH  (prefix/suffix running product; Kadane variant shown as a second method)
 *   maxProductSubArray - the author's scan:
 *     1. Sweep a running product "pre" left to right and "suff" right to left at
 *        the same time, taking the best of both at every step.
 *     2. Whenever a running product hits 0, reset it to 1 before multiplying, which
 *        restarts the scan after the zero.
 *     3. Why this is enough: the optimal subarray is bounded by zeros or by the array
 *        ends. Inside such a zero-free block there is an even or odd number of
 *        negatives; if odd, the best answer drops either the first negative or the
 *        last one, and those two candidates are exactly what the prefix scan and the
 *        suffix scan produce.
 *   maxProductKadane - the classic answer to give in an interview:
 *     4. Carry both the maximum and the minimum product ending at i. A negative
 *        arr[i] swaps their roles, so swap them before extending.
 *
 * KEY INSIGHT
 *   Plain Kadane fails because "largest so far" is not enough state: the most
 *   negative product is a candidate for the largest one the moment a negative
 *   arrives. Carry max and min together (or scan from both ends). Recognise this
 *   any time multiplication replaces addition in a running-subarray question.
 *
 * COMPLEXITY
 *   Time  O(n)   one pass in both methods
 *   Space O(1)   a handful of running variables
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the subarray's indices, not just the product.
 *   - Maximum Subarray (LeetCode 53): the additive Kadane this generalises.
 *   - Count the subarrays with a positive product (LeetCode 1567).
 *   - What about overflow on 32-bit ints? LeetCode bounds the answer; otherwise
 *     use long, or track the sign and sum the logarithms of the magnitudes.
 *
 * RUN
 *   main() runs 4 cases (typical, no zeros, all-negative-with-zero, single element)
 *   through both methods and prints actual vs expected.
 */

import java.util.Arrays;

class MaxProductSubArray {

    /** Prefix and suffix running products, each restarted after a zero. */
    public static int maxProductSubArray(int[] arr) {
        if (arr == null || arr.length == 0) return 0;   // defensive; LeetCode never sends this
        int n = arr.length;

        int pre = 1, suff = 1;
        int ans = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            if (pre == 0) pre = 1;                      // a zero ends a block: start fresh
            if (suff == 0) suff = 1;
            pre *= arr[i];              // product of arr[0..i] since the last zero
            suff *= arr[n - 1 - i];     // product of arr[n-1-i..n-1] since the last zero
            ans = Math.max(ans, Math.max(pre, suff));
        }
        return ans;
    }

    /** Kadane with two states: the best and the worst product ending at i. */
    public static int maxProductKadane(int[] arr) {
        if (arr == null || arr.length == 0) return 0;

        int maxEndingHere = arr[0];
        int minEndingHere = arr[0];
        int best = arr[0];

        for (int i = 1; i < arr.length; i++) {
            int value = arr[i];
            if (value < 0) {                            // a negative flips best and worst
                int swap = maxEndingHere;
                maxEndingHere = minEndingHere;
                minEndingHere = swap;
            }
            // either extend the previous subarray or start a new one at i
            maxEndingHere = Math.max(value, maxEndingHere * value);
            minEndingHere = Math.min(value, minEndingHere * value);
            best = Math.max(best, maxEndingHere);
        }
        return best;
    }

    private static void print(String label, int[] arr, int expected) {
        System.out.println(label + " " + Arrays.toString(arr)
                + "  prefixSuffix=" + maxProductSubArray(arr)
                + "  kadane=" + maxProductKadane(arr)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1:", new int[]{1, 2, -3, 0, -4, -5}, 20);
        print("case 2 (no zeros):", new int[]{2, 3, -2, 4}, 6);
        print("case 3 (zero wins):", new int[]{-2, 0, -1}, 0);
        print("case 4 (single):", new int[]{-2}, -2);
    }
}

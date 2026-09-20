/*
 * =====================================================================
 *  Rotate Array                               LeetCode 189 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Rotate nums to the RIGHT by k steps, in place. k can be 0 or larger than the
 *   array length. Output is the same array, rotated.
 *
 * EXAMPLE
 *   [1, 2, 3, 4, 5, 6, 7], k = 3  ->  [5, 6, 7, 1, 2, 3, 4]
 *   [1, 2, 3], k = 4              ->  [3, 1, 2]     k % n = 1
 *   [1], k = 5                    ->  [1]
 *   [], k = 2                     ->  []            must not divide by zero
 *
 * APPROACH  (triple reversal)
 *   1. k = k % n, because rotating by n brings the array back to itself.
 *   2. Reverse the whole array:      [7, 6, 5, 4, 3, 2, 1]
 *   3. Reverse the first k elements: [5, 6, 7, 4, 3, 2, 1]
 *   4. Reverse the remaining n-k:    [5, 6, 7, 1, 2, 3, 4]
 *   The order of the three reversals matters: whole first, then the two halves.
 *
 * KEY INSIGHT
 *   A right rotation by k moves the last k elements to the front, in their original
 *   order. Reversing everything puts them at the front but backwards; reversing each
 *   block separately restores the order inside each block.
 *   Pattern: reverse(range) is a reusable in-place primitive.
 *   C10_NextGreaterPermutation uses the same helper to reverse a suffix.
 *
 * COMPLEXITY
 *   Time  O(n)  three reversals, each element touched at most twice
 *   Space O(1)  in place; the extra-array version costs O(n)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Rotate LEFT by k: same trick with k' = n - (k % n), or reverse the two halves first.
 *   - Extra-array version: result[(i + k) % n] = nums[i]; simplest but O(n) space.
 *   - Cyclic replacement (juggling): O(1) space with gcd(n, k) cycles; harder to get right.
 *   - Why k % n? k may exceed n, and k = n must be a no-op.
 *
 * RUN
 *   main() runs 4 cases (typical, k > n, single, empty) through both the in-place and
 *   the extra-array version and prints actual vs expected.
 *
 * Fixed: k % n threw ArithmeticException on an empty array; now returns early.
 */

import java.util.Arrays;

class RotateArray {

    /** In place, O(1) extra space: reverse everything, then reverse each block. */
    public static void rotate(int[] nums, int k) {
        int n = nums.length;
        if (n == 0) return;    // avoid k % 0
        k = k % n;             // rotating by n is the identity
        reverse(nums, 0, n - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, n - 1);
    }

    /** Reverses nums[s..e] inclusive; a no-op when s >= e. */
    private static void reverse(int[] nums, int s, int e) {
        while (s < e) {
            int temp = nums[s];
            nums[s] = nums[e];
            nums[e] = temp;
            s++;
            e--;
        }
    }

    /** Alternative: O(n) extra space, one pass. Element i moves to (i + k) % n. */
    public static void rotateWithExtraArray(int[] nums, int k) {
        int n = nums.length;
        if (n == 0) return;
        int[] rotated = new int[n];
        for (int i = 0; i < n; i++) {
            rotated[(i + k) % n] = nums[i];
        }
        System.arraycopy(rotated, 0, nums, 0, n);
    }

    private static void run(String label, int[] nums, int k, String expected) {
        int[] copy = nums.clone();
        rotate(nums, k);
        rotateWithExtraArray(copy, k);
        System.out.println(label + ": reversal " + Arrays.toString(nums)
                + " | extraArray " + Arrays.toString(copy) + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 typical", new int[]{1, 2, 3, 4, 5, 6, 7}, 3, "[5, 6, 7, 1, 2, 3, 4]");
        run("case 2 k > n  ", new int[]{1, 2, 3}, 4, "[3, 1, 2]");
        run("case 3 single ", new int[]{1}, 5, "[1]");
        run("case 4 empty  ", new int[]{}, 2, "[]");
    }
}

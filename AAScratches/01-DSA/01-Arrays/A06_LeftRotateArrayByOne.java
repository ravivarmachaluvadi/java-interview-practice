/*
 * =====================================================================
 *  Left Rotate Array By One                          Warm-up | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an int array, rotate it left by one position in place: every element
 *   moves one index to the left and the first element wraps around to the end.
 *   Use O(1) extra space.
 *
 * EXAMPLE
 *   [1, 2, 3, 4, 5]  ->  [2, 3, 4, 5, 1]
 *   [7]              ->  [7]     single element: nothing moves
 *   []               ->  []      empty: nothing to do, must not throw
 *
 * APPROACH  (In-place shift with one temp)
 *   1. Guard: an array of length 0 or 1 is already rotated, return.
 *   2. Save nums[0] in a temp; it is the only value the shift overwrites.
 *   3. For i from 1 to n-1, copy nums[i] into nums[i-1] (shift left).
 *   4. Put the saved value into the last slot nums[n-1].
 *
 * KEY INSIGHT
 *   Shifting left overwrites each slot with the value to its right, so the only
 *   value ever lost is the first one; a single temp is enough to save it.
 *   Direction matters: a LEFT shift walks left-to-right so each slot is read
 *   before it is overwritten (a RIGHT shift must walk right-to-left).
 *   This is the naive baseline that rotate-by-k (C08, triple reversal) beats.
 *
 * COMPLEXITY
 *   Time  O(n)  every element moves exactly once
 *   Space O(1)  one temp variable, rotation is in place
 *
 * INTERVIEW FOLLOW-UPS
 *   - Rotate by k: repeating this k times is O(n*k); triple reversal does it in O(n).
 *   - Rotate right by one: save the last element and walk from the right end.
 *   - Rotate a "view" in O(1) without moving data: keep an offset, read (start+i) % n.
 *
 * RUN
 *   main() runs 3 cases (typical, single element, empty) and prints actual vs expected.
 *
 * Fixed: an empty array used to throw ArrayIndexOutOfBoundsException on nums[0].
 */
import java.util.Arrays;

class LeftRotateArrayByOne {

    public void rotateArrayByOne(int[] nums) {
        if (nums.length <= 1) return;   // nothing to move; also avoids nums[0] on empty
        int first = nums[0];            // the only value the shift overwrites
        for (int i = 1; i < nums.length; i++) {
            nums[i - 1] = nums[i];      // each slot takes the value to its right
        }
        nums[nums.length - 1] = first;  // wrap the saved value to the end
    }

    private static void run(String label, int[] nums, String expected) {
        new LeftRotateArrayByOne().rotateArrayByOne(nums);
        System.out.println(label + ": " + Arrays.toString(nums) + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 typical", new int[]{1, 2, 3, 4, 5}, "[2, 3, 4, 5, 1]");
        run("case 2 single ", new int[]{7}, "[7]");
        run("case 3 empty  ", new int[]{}, "[]");
    }
}

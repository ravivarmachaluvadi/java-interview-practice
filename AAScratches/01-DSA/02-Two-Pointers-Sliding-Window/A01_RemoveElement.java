/*
 * =====================================================================
 *  Remove Element                                    LeetCode 27 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an int array nums and a value val, remove every occurrence of val in place
 *   and return k, the number of elements that remain. The first k slots of nums must hold
 *   the kept elements (order preserved here, though LeetCode does not require it).
 *   Whatever sits beyond index k does not matter.
 *
 * EXAMPLE
 *   nums = [3, 2, 2, 3], val = 3        ->  k = 2, nums[0..2) = [2, 2]
 *   nums = [0, 1, 2, 2, 3, 0, 4, 2], val = 2  ->  k = 5, nums[0..5) = [0, 1, 3, 0, 4]
 *   nums = [],  val = 1                 ->  k = 0                       (empty edge case)
 *   nums = [4, 4, 4], val = 4           ->  k = 0                       (everything removed)
 *
 * APPROACH  (slow write pointer, fast scan)
 *   1. writeIndex starts at 0. It is the next free slot for an element we keep.
 *   2. Scan every index readIndex from 0 to n-1.
 *   3. If nums[readIndex] != val, copy it to nums[writeIndex] and advance writeIndex.
 *   4. Elements equal to val are simply skipped, so the write pointer falls behind
 *      and later kept elements overwrite them.
 *   5. Return writeIndex: it equals the count of kept elements.
 *
 * KEY INSIGHT
 *   writeIndex <= readIndex always, so writing never clobbers something not yet read.
 *   This is the write-pointer primitive: "keep what passes a test, overwrite in place".
 *   Recognise it whenever a problem says "in place" and "return the new length".
 *
 * COMPLEXITY
 *   Time  O(n)  each element is read once and written at most once
 *   Space O(1)  two int indices, no extra array
 *
 * INTERVIEW FOLLOW-UPS
 *   - If order does not matter and val is rare: swap nums[i] with nums[n-1] and shrink n
 *     (fewer writes when most elements are kept).
 *   - Same primitive with the test "!= previous kept" gives Remove Duplicates (LC 26).
 *   - Same primitive plus a fill-the-tail pass gives Move Zeroes (LC 283).
 *
 * RUN
 *   main() runs 4 cases (typical, LeetCode sample, empty, all removed) and prints
 *   actual vs expected.
 */

import java.util.Arrays;

class RemoveElement {

    public int removeElement(int[] nums, int val) {
        int writeIndex = 0; // next slot for an element we keep
        for (int readIndex = 0; readIndex < nums.length; readIndex++) {
            if (nums[readIndex] != val) {
                nums[writeIndex] = nums[readIndex];
                writeIndex++;
            }
        }
        return writeIndex; // number of kept elements
    }

    // Formats "k=<count> kept=[...]" so a single line shows both outputs of the method.
    private static String run(int[] nums, int val) {
        int k = new RemoveElement().removeElement(nums, val);
        return "k=" + k + " kept=" + Arrays.toString(Arrays.copyOf(nums, k));
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical    ", run(new int[]{3, 2, 2, 3}, 3), "k=2 kept=[2, 2]");
        print("case 2 LC sample  ", run(new int[]{0, 1, 2, 2, 3, 0, 4, 2}, 2),
                "k=5 kept=[0, 1, 3, 0, 4]");
        print("case 3 empty      ", run(new int[]{}, 1), "k=0 kept=[]");
        print("case 4 all removed", run(new int[]{4, 4, 4}, 4), "k=0 kept=[]");
    }
}

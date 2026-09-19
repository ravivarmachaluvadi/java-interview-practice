import java.util.Arrays;

/*
 * Problem: Move every occurrence of a target value to the end of the array, keeping the
 *          relative order of the other elements. In place, O(n) time, O(1) space.
 * Approaches:
 *   - moveZeroes(nums)              : LeetCode 283 "Move Zeroes" - the target is fixed at 0.
 *   - moveElementToEnd(nums, target): same write-pointer compaction, generalised to any target.
 * Both are the same algorithm; moveZeroes is simply moveElementToEnd with target = 0.
 */
class MoveZeroes {

    // LeetCode 283: Move Zeroes. Shown as the target = 0 special case of the general method.
    public static void moveZeroes(int[] nums) {
        moveElementToEnd(nums, 0);
    }

    // Move all occurrences of 'target' to the end of the array.
    // pIndex = next slot to write a non-target element (the "write pointer").
    public static void moveElementToEnd(int[] nums, int target) {
        int n = nums.length;
        int pIndex = 0;

        // Pass 1: compact every non-target element to the front, preserving order
        for (int i = 0; i < n; i++) {
            if (nums[i] != target)
                nums[pIndex++] = nums[i];
        }

        // Pass 2: everything from pIndex onwards is dead space - fill it with the target
        while (pIndex < n)
            nums[pIndex++] = target;
    }

    public static void main(String[] args) {
        // Input 1: LeetCode 283 sample. Expected [1, 3, 12, 0, 0]
        int[] zeros = {0, 1, 0, 3, 12};
        System.out.println("Input            : " + Arrays.toString(zeros));
        int[] a = zeros.clone();
        moveZeroes(a);
        System.out.println("moveZeroes       : " + Arrays.toString(a));
        int[] b = zeros.clone();
        moveElementToEnd(b, 0);
        System.out.println("moveElementToEnd : " + Arrays.toString(b) + " (target = 0)");

        // Input 2: arbitrary target. Expected [5, 2, 8, 1, 3, 3, 3]
        int[] threes = {3, 5, 2, 3, 8, 3, 1};
        System.out.println("Input            : " + Arrays.toString(threes));
        int[] c = threes.clone();
        moveElementToEnd(c, 3);
        System.out.println("moveElementToEnd : " + Arrays.toString(c) + " (target = 3)");
    }
}

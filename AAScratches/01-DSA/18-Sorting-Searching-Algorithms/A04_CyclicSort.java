/**
 * Problem:
 * Sort an array of integers where each integer is in the range 1 to n (n = array length) by placing
 * each number at its correct index (value - 1). The input array contains a permutation of 1..n.
 *
 * Approach:
 * Iterate through the array. For each element, compute its target index as value-1.
 * If the current element is not in its target position, swap it with the element at that
 * target index. Continue until all elements are correctly positioned.
 *
 * Time Complexity:
 * O(n) – Each element is swapped at most once to reach its correct spot.
 *
 * Space Complexity:
 * O(1) – In-place sorting using only a few auxiliary variables.
 */
import java.util.Arrays;

class CyclicSort {

    public static void cyclicSort(int[] nums) {
        int i = 0;
        while (i < nums.length) {
            int correctIndex = nums[i] - 1;
            if (nums[i] != nums[correctIndex]) {
                swap(nums, i, correctIndex);
            } else {
                i++;
            }
        }
    }

    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    public static void main(String[] args) {
        int[] nums = {3, 1, 5, 4, 2};
        System.out.println("Original Array: " + Arrays.toString(nums));
        cyclicSort(nums);
        System.out.println("Sorted Array: " + Arrays.toString(nums));
    }
}


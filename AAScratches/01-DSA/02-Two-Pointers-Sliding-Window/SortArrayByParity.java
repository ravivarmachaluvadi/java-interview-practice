import java.util.Arrays;

class SortArrayByParity {
    /**
     * Original Array: [3, 1, 2, 4]
     * <p>
     * Sorted Array by Parity: [4, 2, 1, 3]
     * <p>
     * “Sort Array By Parity” (evens first, odds later) using a two‑pointer in‑place approach.
     */
    public static int[] sortArrayByParity(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            if (nums[left] % 2 > nums[right] % 2) {
                int temp = nums[left];
                nums[left] = nums[right];
                nums[right] = temp;
            }
            // Move left pointer if it is even
            if (nums[left] % 2 == 0) {
                left++;
            }
            // Move right pointer if it is odd
            if (nums[right] % 2 == 1) {
                right--;
            }
        }

        return nums;
    }

    public static void main(String[] args) {
        int[] nums = {3, 1, 2, 4};
        System.out.println("Original Array: " + Arrays.toString(nums));

        int[] sortedArray = sortArrayByParity(nums);
        System.out.println("Sorted Array by Parity: " + Arrays.toString(sortedArray));
    }
}

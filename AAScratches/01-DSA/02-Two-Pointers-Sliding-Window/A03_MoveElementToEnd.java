import java.util.Arrays;

class MoveElementToEnd {
    // Method to move all occurrences of 'target' to the end of the array
    public static void moveElementToEnd(int[] nums, int target) {
        int n = nums.length;
        int pIndex = 0; // Pointer for the position to place non-target elements

        // Traverse the array
        for (int i = 0; i < n; i++) {
            if (nums[i] != target)
                nums[pIndex++] = nums[i]; // Move non-target elements to the front
        }

        // Fill the remaining positions with the target element
        while (pIndex < n)
            nums[pIndex++] = target;
    }

    public static void main(String[] args) {
        int[] nums = {3, 5, 2, 3, 8, 3, 1};
        int target = 3;

        System.out.println("Original array: " + Arrays.toString(nums));
        moveElementToEnd(nums, target);
        System.out.println("Modified array: " + Arrays.toString(nums));
        // Modified array: [5, 2, 8, 1, 3, 3, 3]
    }
}


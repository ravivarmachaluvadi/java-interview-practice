/**
 * An array is monotonic if it is either monotone increasing
 * or monotone decreasing.
 */
// Method to check if the array is monotonic
public static boolean isMonotonic(int[] nums) {
    if (nums.length <= 2)
        return true; // An array with 0, 1, or 2 elements is always monotonic

    boolean increasing = true;
    boolean decreasing = true;

    for (int i = 1; i < nums.length; i++) {
        if (nums[i] > nums[i - 1])
            decreasing = false; // Not decreasing

        if (nums[i] < nums[i - 1])
            increasing = false; // Not increasing
    }

    return increasing || decreasing; // Return true if either is true
}

void main() {
    int[] nums1 = {1, 2, 2, 3}; // Non-decreasing
    int[] nums2 = {6, 5, 4, 4}; // Non-increasing
    int[] nums3 = {1, 3, 2}; // Not monotonic

    IO.println("Is nums1 monotonic? " + isMonotonic(nums1)); // true
    IO.println("Is nums2 monotonic? " + isMonotonic(nums2)); // true
    IO.println("Is nums3 monotonic? " + isMonotonic(nums3)); // false
}


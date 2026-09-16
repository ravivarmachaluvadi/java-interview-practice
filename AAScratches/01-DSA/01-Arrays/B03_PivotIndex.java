/**
 * Problem: Find the pivot index in an integer array where the sum of elements on the left equals the sum on the right.
 *
 * Approach: First compute the total sum of all elements. Then iterate through the array, maintaining a running leftSum. At each index i,
 * check if leftSum equals (totalSum - leftSum - nums[i]); if so, return i as the pivot. Update leftSum by adding nums[i] and continue.
 *
 * Time Complexity: O(n) – two linear passes over the array.
 * Space Complexity: O(1) – only a few integer variables are used regardless of input size.
 */
class PivotIndex {
    public int pivotIndex(int[] nums) {
        int totalSum = 0;
        int leftSum = 0;

        for (int num : nums)
            totalSum += num;

        for (int i = 0; i < nums.length; i++) {
            if (leftSum == totalSum - leftSum - nums[i]) {
                return i;
            }
            leftSum += nums[i];
        }
        return -1;
    }

    // Main method to test the functionality
    public static void main(String[] args) {
        PivotIndex solution = new PivotIndex();

        // Example 1:
        int[] nums1 = {1, 7, 3, 6, 5, 6};
        System.out.println("Pivot index for nums1: " + solution.pivotIndex(nums1)); // Output: 3

        // Example 2:
        int[] nums2 = {1, 2, 3};
        System.out.println("Pivot index for nums2: " + solution.pivotIndex(nums2)); // Output: -1

        // Example 3:
        int[] nums3 = {2, 1, -1};
        System.out.println("Pivot index for nums3: " + solution.pivotIndex(nums3)); // Output: 0
    }
}

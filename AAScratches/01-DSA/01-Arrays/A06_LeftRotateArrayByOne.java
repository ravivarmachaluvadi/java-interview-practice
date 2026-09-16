/**
 * Problem: Rotate an integer array left by one position.
 *
 * Approach: Store the first element, shift all subsequent elements left by one,
 * and place the stored value at the end of the array. This is done in-place
 * using a single temporary variable.
 *
 * Time Complexity: O(n), where n is the length of the array.
 * Space Complexity: O(1) auxiliary space.
 */
class LeftRotateArrayByOne {
    public void rotateArrayByOne(int[] nums) {
        int temp = nums[0];
        for (int i = 1; i < nums.length; i++)
            nums[i - 1] = nums[i];
        nums[nums.length - 1] = temp;
    }

    public static void main(String[] args) {
        LeftRotateArrayByOne solution = new LeftRotateArrayByOne();
        int[] nums = {1, 2, 3, 4, 5};
        solution.rotateArrayByOne(nums);
        for (int num : nums)
            System.out.print(num + " ");
    }
}
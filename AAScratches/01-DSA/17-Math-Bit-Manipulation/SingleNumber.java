/**
 * Given a non-empty array of integers nums, every
 * <p>
 * element appears twice except for one. Find that single one.
 * <p>
 * You must implement a solution with a linear runtime
 * <p>
 * complexity and use only constant extra space.
 */
// https://leetcode.com/problems/single-number/
class SingleNumber {
    public int singleNumber(int[] nums) {
        int xor = 0;
        for (int val : nums) {
            xor = xor ^ val;
        }
        return xor;
    }

    public static void main(String[] args) {
        // Build a concrete example input
        int[] nums = {4, 1, 2, 1, 2};
    
        // Create an instance of SingleNumber and call the primary method
        SingleNumber solver = new SingleNumber();
        int result = solver.singleNumber(nums);
    
        // Print the input array
        System.out.print("Input: [");
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i]);
            if (i < nums.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    
        // Print the output
        System.out.println("Output: " + result);
    }
}

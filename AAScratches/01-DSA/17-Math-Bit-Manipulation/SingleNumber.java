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
}

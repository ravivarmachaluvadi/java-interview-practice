/**
 * Problem: Determine whether the i-th bit (0-indexed from LSB) in an integer n is set to 1.
 *
 * Approach: Perform a left shift of 1 by i positions to create a mask with only that bit set,
 * then apply a bitwise AND with n. If the result is non-zero, the bit is set; otherwise it is not.
 *
 * Time Complexity: O(1) – constant time operations (shift and AND).
 * Space Complexity: O(1) – no additional space proportional to input size.
 */
class Solution {
    public boolean checkIthBit(int n, int i) {
        return (n & (1 << i)) != 0;
    }
}

class CheckIfTheIthBitIsSetOrNot {
    public static void main(String[] args) {
        Solution sol = new Solution();
        int num = 5;  // Binary: 101
        int bitIndex = 2;

        if (sol.checkIthBit(num, bitIndex)) {
            System.out.println("The " + bitIndex + "-th bit of " + num + " is set (1).");
        } else {
            System.out.println("The " + bitIndex + "-th bit of " + num + " is not set (0).");
        }
    }
}

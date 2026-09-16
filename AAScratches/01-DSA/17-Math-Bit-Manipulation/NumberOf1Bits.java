/**
 * Problem: Count the number of '1' bits (Hamming weight) in the binary representation
 *          of a given integer.
 *
 * Approach: Repeatedly examine the least significant bit using n & 1, increment a counter
 *           if it is set, then right-shift the integer until all bits are processed.
 *
 * Time Complexity: O(k), where k is the number of bits in the integer (constant for fixed-size ints).
 * Space Complexity: O(1) – only a few primitive variables are used.
 */
class NumberOf1Bits {
    public int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            if ((n & 1) == 1) {
                count++;
            }
            n = n >> 1;
        }
        return count;
    }

    public static void main(String[] args) {
        NumberOf1Bits solver = new NumberOf1Bits();
        int input = 11; // binary 1011
        int result = solver.hammingWeight(input);
        System.out.println("Input: " + input + " (binary " + Integer.toBinaryString(input) + ")");
        System.out.println("Number of 1 bits: " + result);
    }
}

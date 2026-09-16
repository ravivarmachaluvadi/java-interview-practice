// https://leetcode.com/problems/minimum-bit-flips-to-convert-number/description/
// 2220. Minimum Bit Flips to Convert Number
class MinimumBitFlipsToConvertNumber {
    // Method solving the problem
    public static int minBitFlips(int start, int goal) {
        // XOR gives bits that differ
        int diff = start ^ goal;
        // Use Java built-in to count set bits
        return Integer.bitCount(diff);
    }

    // Main method with example
    public static void main(String[] args) {
        int start = 10;
        int goal = 7;
        int result = minBitFlips(start, goal);
        System.out.println("start = " + start + ", goal = " + goal);
        System.out.println("Minimum bit flips needed = " + result);
        // Example expected: 3
        // Explanation: 10 in binary = 1010, 7 = 0111. XOR = 1101 → three bits set → 3 flips.
    }
}

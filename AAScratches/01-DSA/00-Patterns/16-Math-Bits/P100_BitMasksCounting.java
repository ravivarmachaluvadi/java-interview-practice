/*
 * =====================================================================
 *  P100 Bit Masks and Bit Counting   Canonical LC 191 | Easy
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 191, Number of 1 Bits)
 *   Return the number of set bits in the binary form of n (treat it as unsigned 32 bits).
 *
 * EXAMPLE
 *   11 (1011)           ->  3
 *   128 (10000000)      ->  1
 *   -3 (0xFFFFFFFD)     ->  31     unsigned view: every bit except one
 *
 * RECOGNIZE WHEN
 *   - Questions about the binary form: count bits, test / set / clear a bit, powers of two,
 *     Hamming distance, reverse the bits.
 *   - A small set (<= 32 items) represented as one integer.
 *   - "without using + or *" style puzzles.
 *   Not this if: pairs cancel out -> P099_XorTricks; the mask is a DP state ->
 *   P094_BitmaskDp.
 *
 * TEMPLATE
 *   test bit i:   (x >> i) & 1          set: x | (1 << i)       clear: x & ~(1 << i)
 *   drop lowest set bit:  x & (x - 1)   lowest set bit: x & -x
 *   power of two:         x > 0 && (x & (x - 1)) == 0
 *   bits(i) = bits(i >> 1) + (i & 1)    // DP over numbers 0..n
 *   unsigned shift in Java: >>>
 *
 * APPROACH
 *   1. n & (n - 1) clears the lowest set bit.
 *   2. Count how many times you can do that before n becomes 0.
 *
 * KEY INSIGHT
 *   Subtracting 1 flips the lowest set bit and every 0 below it, so ANDing with the original
 *   removes exactly that one bit. The loop runs once per set bit, not once per bit position,
 *   and it treats negative numbers correctly because it never shifts.
 *
 * COMPLEXITY
 *   O(number of set bits) per number; LC 338 is O(n) for all of 0..n.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 338  Counting Bits            dp[i] = dp[i >> 1] + (i & 1)
 *   [coded] LC 231  Power of Two             n > 0 and n & (n - 1) == 0
 *   [coded] LC 461  Hamming Distance         bitCount(x ^ y)
 *   [coded] LC 190  Reverse Bits             shift out the low bit, shift it in on the left
 *           LC 371  Sum Without + or -       a ^ b is the sum without carries, (a & b) << 1
 *                                            the carries; repeat until no carry
 *           LC 2220 Min Bit Flips            bitCount(start ^ goal)
 *           LC 401  Binary Watch             enumerate times, keep bitCount(h) + bitCount(m)
 *
 * PITFALLS
 *   - Use >>> (unsigned) when shifting a value that may be negative, or the loop never ends.
 *   - 1 << 31 is negative in Java; compare with != 0, not > 0.
 *   - Integer.bitCount exists; say you know it, then write the loop if asked.
 *
 * DEEP DIVE
 *   A02_NumberOf1Bits, A01_CheckIfTheIthBitIsSetOrNot, B02_MinimumBitFlipsToConvertNumber,
 *   B03_BinaryWatch (17-Math-Bit-Manipulation)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;

class BitMasksCounting {

    // Canonical LC 191 (Brian Kernighan's loop).
    static int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            n &= n - 1;                            // drop the lowest set bit
            count++;
        }
        return count;
    }

    // LC 338.
    static int[] countBits(int n) {
        int[] dp = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            dp[i] = dp[i >> 1] + (i & 1);
        }
        return dp;
    }

    // LC 231.
    static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    // LC 461.
    static int hammingDistance(int x, int y) {
        return hammingWeight(x ^ y);
    }

    // LC 190: reverse the 32 bits.
    static int reverseBits(int n) {
        int r = 0;
        for (int i = 0; i < 32; i++) {
            r = (r << 1) | (n & 1);
            n >>>= 1;
        }
        return r;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 191 n=11", hammingWeight(11), 3);
        check("LC 191 n=128", hammingWeight(128), 1);
        check("LC 191 n=-3 unsigned", hammingWeight(-3), 31);

        check("LC 338 n=2", Arrays.toString(countBits(2)), "[0, 1, 1]");
        check("LC 338 n=5", Arrays.toString(countBits(5)), "[0, 1, 1, 2, 1, 2]");

        check("LC 231 n=1", isPowerOfTwo(1), true);
        check("LC 231 n=16", isPowerOfTwo(16), true);
        check("LC 231 n=3", isPowerOfTwo(3), false);
        check("LC 231 int min is not", isPowerOfTwo(Integer.MIN_VALUE), false);

        check("LC 461 x=1 y=4", hammingDistance(1, 4), 2);
        check("LC 461 x=3 y=1", hammingDistance(3, 1), 1);

        check("LC 190 43261596", reverseBits(43261596), 964176192);
        check("LC 190 0xFFFFFFFD", reverseBits(0xFFFFFFFD), -1073741825);
    }
}

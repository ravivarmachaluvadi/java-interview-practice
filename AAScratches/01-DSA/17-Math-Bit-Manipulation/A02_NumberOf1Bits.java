/*
 * =====================================================================
 *  Number of 1 Bits (Hamming Weight)          LeetCode 191 | Easy  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a 32-bit integer, return how many of its bits are 1.
 *   Java has no unsigned int, so the input is treated as a raw 32-bit
 *   pattern: -1 is thirty-two 1s and must answer 32, not loop forever.
 *
 * EXAMPLE
 *   n = 11          (binary 1011)                    ->  3
 *   n = 0           (binary 0)                       ->  0
 *   n = 128         (binary 10000000)                ->  1
 *   n = -1          (binary 32 ones)                 ->  32
 *   n = 2147483647  (binary 31 ones, MAX_VALUE)      ->  31
 *
 * APPROACH  (popcount by shift and test)
 *   1. Look at the lowest bit with n & 1; add it to the counter.
 *   2. Shift n right by one with >>> so a 0 is fed in at the top.
 *   3. Stop when n becomes 0. Runs at most 32 times.
 *   countBitsKernighan() is the second method: n &= (n - 1) erases the
 *   lowest set bit each round, so it loops once per 1 bit instead of
 *   once per bit position.
 *
 * KEY INSIGHT
 *   Use >>> (logical shift), never >> (arithmetic shift). On a negative
 *   number >> copies the sign bit back in, so -1 >> 1 is still -1 and
 *   the loop never ends. Fixed: the original used >> and hung on any
 *   negative input. Second idea worth reciting: subtracting 1 flips the lowest 1 to 0
 *   and turns every 0 below it into 1, so n & (n - 1) clears exactly
 *   that lowest set bit.
 *
 * COMPLEXITY
 *   Time  O(32) = O(1) for the shift loop; O(number of set bits) for
 *                 the Kernighan loop - faster on sparse inputs
 *   Space O(1)  a counter and the working copy
 *
 * INTERVIEW FOLLOW-UPS
 *   - Count bits for every number 0..n at once (LeetCode 338, DP:
 *     bits[i] = bits[i >> 1] + (i & 1))
 *   - Hamming distance between a and b = popcount(a ^ b)
 *   - What does Integer.bitCount do? (an intrinsic, usually one CPU
 *     instruction - name it, then still write the loop)
 *
 * RUN
 *   main() runs 5 cases through both methods and prints actual vs expected.
 */
class NumberOf1Bits {

    /** Shift-and-test popcount. >>> keeps this terminating on negatives. */
    public int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            count += (n & 1);   // lowest bit is 0 or 1, so just add it
            n = n >>> 1;        // logical shift: feeds 0 in at the top
        }
        return count;
    }

    /** Brian Kernighan: one iteration per set bit, not per bit position. */
    public int countBitsKernighan(int n) {
        int count = 0;
        while (n != 0) {
            n = n & (n - 1);    // clears the lowest set bit
            count++;
        }
        return count;
    }

    public static void main(String[] args) {
        NumberOf1Bits solver = new NumberOf1Bits();
        int[] inputs = {11, 0, 128, -1, Integer.MAX_VALUE};
        int[] expected = {3, 0, 1, 32, 31};

        for (int i = 0; i < inputs.length; i++) {
            int n = inputs[i];
            System.out.println("case " + (i + 1) + ": n=" + n
                    + " shift=" + solver.hammingWeight(n)
                    + " kernighan=" + solver.countBitsKernighan(n)
                    + "   expected " + expected[i]);
        }
    }
}

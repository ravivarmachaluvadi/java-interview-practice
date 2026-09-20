/*
 * =====================================================================
 *  Minimum Bit Flips to Convert Number      LeetCode 2220 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   A bit flip changes one bit of a number's binary form from 0 to 1 or 1 to 0.
 *   Given two non-negative ints start and goal, return the minimum number of
 *   flips needed to turn start into goal. Both fit in 32-bit int.
 *
 * EXAMPLE
 *   start = 10 (1010), goal = 7 (0111)  ->  3   bits 0, 1 and 3 differ
 *   start = 3  (0011), goal = 4 (0100)  ->  3   XOR = 0111
 *   start = 0,         goal = 0         ->  0   nothing differs
 *   start = 0,         goal = 2147483647 -> 31  all 31 value bits must be set
 *
 * APPROACH  (XOR the two numbers, then popcount the result)
 *   1. XOR start and goal. A bit of the result is 1 exactly where the two
 *      numbers disagree, because 0^1 = 1^0 = 1 and 0^0 = 1^1 = 0.
 *   2. Each disagreeing bit needs exactly one flip, and flips are independent.
 *   3. So the answer is the number of set bits in start ^ goal.
 *   4. Two ways to count them are shown: Integer.bitCount (library) and
 *      Kernighan's n & (n - 1) loop, which clears the lowest set bit each turn.
 *
 * KEY INSIGHT
 *   XOR is a "difference detector" for bits, and popcount turns that difference
 *   into a count. Any "how far apart are these two bit patterns" question
 *   (Hamming distance, error-correcting codes) is this same two-step move.
 *
 * COMPLEXITY
 *   Time  O(1)      32-bit input; Kernighan's loop runs once per set bit, <= 32
 *   Space O(1)      a few int locals
 *
 * INTERVIEW FOLLOW-UPS
 *   - Total Hamming distance over a whole array (LeetCode 477): count per bit
 *     column, answer is sum over bits of ones * zeros.
 *   - Why does n & (n - 1) clear the lowest set bit? Subtracting 1 flips that
 *     bit to 0 and everything below it to 1; the AND wipes them all.
 *   - Count set bits without bitCount and without a loop? Use the SWAR
 *     parallel-bit-count trick with masks 0x55555555, 0x33333333 and so on.
 *   - Negative inputs? Use >>> not >> so the sign bit does not refill.
 *
 * RUN
 *   main() runs 4 cases (typical, no-flip edge, tricky all-bits case) and prints
 *   actual vs expected for both counting methods.
 */

class MinimumBitFlipsToConvertNumber {

    /** Library popcount of the differing bits. */
    public static int minBitFlips(int start, int goal) {
        int diff = start ^ goal; // 1 wherever the two numbers disagree
        return Integer.bitCount(diff);
    }

    /** Same answer with a hand-rolled popcount, to show what bitCount does. */
    public static int minBitFlipsManual(int start, int goal) {
        int diff = start ^ goal;
        int flips = 0;
        while (diff != 0) {
            diff &= (diff - 1); // clears the lowest set bit, so one loop per set bit
            flips++;
        }
        return flips;
    }

    private static void check(int start, int goal, int expected) {
        System.out.println("start = " + start + ", goal = " + goal
                + "   bitCount -> " + minBitFlips(start, goal)
                + "   manual -> " + minBitFlipsManual(start, goal)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        check(10, 7, 3);                     // typical: 1010 vs 0111
        check(3, 4, 3);                      // typical: 0011 vs 0100
        check(0, 0, 0);                      // edge: identical, nothing to flip
        check(0, Integer.MAX_VALUE, 31);     // tricky: every value bit differs
    }
}

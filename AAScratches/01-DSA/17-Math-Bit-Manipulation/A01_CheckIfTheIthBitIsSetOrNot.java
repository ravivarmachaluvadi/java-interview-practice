/*
 * =====================================================================
 *  Check If The i-th Bit Is Set Or Not          Building block | Easy  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer n and a bit position i (0-indexed from the least
 *   significant bit), report whether that bit of n is 1.
 *   For a 32-bit int, i ranges 0..31; bit 31 is the sign bit.
 *
 * EXAMPLE
 *   n = 5 (binary 101), i = 2  ->  true    bit 2 is the leading 1
 *   n = 5 (binary 101), i = 1  ->  false   bit 1 is 0
 *   n = 0,              i = 0  ->  false   no bits are set at all
 *   n = -1 (all 32 ones), i = 31 -> true   the sign bit is set
 *
 * APPROACH  (mask and test)
 *   1. Build a mask that has a single 1 at position i:  1 << i.
 *   2. AND it with n. Every other bit of n is forced to 0 by the mask.
 *   3. The result is non-zero exactly when n's bit i was 1, so return
 *      (n & (1 << i)) != 0.
 *
 * KEY INSIGHT
 *   Compare against != 0, never == 1. The surviving bit keeps its own
 *   place value: for i = 2 the AND yields 4, not 1. And for i = 31 the
 *   mask is Integer.MIN_VALUE, so the result is negative - == 1 would be
 *   wrong there too. Shifting n right by i and testing & 1 is the other
 *   spelling; it needs >>> to stay safe on negatives.
 *
 * COMPLEXITY
 *   Time  O(1)  one shift and one AND, regardless of n
 *   Space O(1)  no allocation
 *
 * INTERVIEW FOLLOW-UPS
 *   - Set / clear / toggle bit i:  n | mask,  n & ~mask,  n ^ mask
 *   - Why does 1 << 32 return 1 in Java? (shift count is taken mod 32)
 *   - Test a bit of a long: use 1L << i, otherwise i >= 32 wraps around
 *
 * RUN
 *   main() runs 5 cases (typical, zero, sign bit, shift-count wrap)
 *   and prints actual vs expected.
 */
class Solution {

    /** True when bit i of n (0-indexed from the LSB) is 1. */
    public boolean checkIthBit(int n, int i) {
        int mask = 1 << i;            // single 1 parked at position i
        return (n & mask) != 0;       // != 0, not == 1: the bit keeps its place value
    }
}

class CheckIfTheIthBitIsSetOrNot {

    public static void main(String[] args) {
        Solution sol = new Solution();

        print("case 1: n=5  i=2 ", sol.checkIthBit(5, 2), true);
        print("case 2: n=5  i=1 ", sol.checkIthBit(5, 1), false);
        print("case 3: n=0  i=0 ", sol.checkIthBit(0, 0), false);
        print("case 4: n=-1 i=31", sol.checkIthBit(-1, 31), true);
        // Java masks the shift count with & 31, so 1 << 32 == 1 << 0 == 1.
        print("case 5: n=1  i=32", sol.checkIthBit(1, 32), true);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + " -> " + actual + "   expected " + expected);
    }
}

/*
 * =====================================================================
 *  Power of Ten                             not on LeetCode | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an int n, return true if n is a positive power of ten, that is
 *   n == 10^k for some integer k >= 0. Zero and negatives are never powers of
 *   ten. 10^0 = 1 counts, so 1 returns true.
 *
 * EXAMPLE
 *   n = 1000        ->  true    10^3
 *   n = 1           ->  true    10^0
 *   n = 500         ->  false   500 / 10 = 50, 50 / 10 = 5, and 5 % 10 != 0
 *   n = 0           ->  false   no power of ten is zero
 *   n = -10         ->  false   powers of ten are positive
 *   n = 1000000000  ->  true    10^9, the largest power of ten that fits in int
 *
 * APPROACH  (divide down by ten, checking every remainder)
 *   1. Reject n <= 0 immediately; the loop only makes sense for positives.
 *   2. While n is bigger than 1, require that it ends in a zero (n % 10 == 0).
 *      The moment a non-zero remainder appears, some factor other than 10 is
 *      present and the answer is false.
 *   3. Otherwise chop the trailing zero with n /= 10 and continue.
 *   4. Reaching exactly 1 means the number was a stack of tens and nothing else.
 *
 * KEY INSIGHT
 *   This is the same "divide out the base until nothing is left" loop as
 *   PowerOfThree, just with base 10 - which makes the shared pattern visible
 *   before B06_PowerCheck generalises it to any base. In base 10 it has a neat
 *   reading: a power of ten is a 1 followed by only zeros, so the loop is really
 *   asking "is every digit but the leading one a zero?".
 *
 * COMPLEXITY
 *   Time  O(log n) base 10, which is just the number of digits in n
 *   Space O(1)     one int, reused
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it with strings: the decimal text must be "1" followed by zeros.
 *   - Why not Math.log10(n) % 1 == 0? Floating point rounding misjudges large
 *     inputs; integer division has no such failure mode.
 *   - What about long inputs? Same loop; 10^18 is the largest power in long.
 *   - Generalise the base into a parameter - see B06_PowerCheck in this folder.
 *
 * RUN
 *   main() runs 6 cases (typical, 10^0 edge, non-power, zero, negative, largest
 *   int power) and prints actual vs expected.
 */

class PowerOfTen {

    public static boolean isPowerOfTen(int n) {
        if (n <= 0) {
            return false; // zero and negatives can never be 10^k
        }
        while (n > 1) {
            if (n % 10 != 0) {
                return false; // a non-zero trailing digit means some other factor
            }
            n /= 10; // chop the trailing zero
        }
        return true; // came all the way down to 1
    }

    private static void check(int n, boolean expected) {
        System.out.println("n = " + n
                + "   actual " + isPowerOfTen(n)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        check(1000, true);          // typical
        check(500, false);          // typical non-power
        check(1, true);             // edge: 10^0
        check(0, false);            // edge: zero
        check(-10, false);          // edge: negative
        check(1000000000, true);    // tricky: 10^9, largest that fits in int
    }
}

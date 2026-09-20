/*
 * =====================================================================
 *  Power of Three                           LeetCode 326 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an int n, return true if n is a power of three, that is n == 3^k for
 *   some integer k >= 0. n may be zero or negative, and neither can qualify.
 *   3^0 = 1 counts, so 1 is a power of three.
 *
 * EXAMPLE
 *   n = 27          ->  true    27 = 3^3
 *   n = 36          ->  false   36 = 4 * 9, the 4 is not divisible by 3
 *   n = 1           ->  true    3^0
 *   n = 0           ->  false   no power of three is zero
 *   n = -3          ->  false   powers of three are positive
 *   n = 1162261467  ->  true    3^19, the largest power of three that fits in int
 *
 * APPROACH  (divide out every factor of three and see what is left)
 *   1. Reject n <= 0 up front: powers of three are strictly positive, and n = 0
 *      would otherwise make the loop condition 0 % 3 == 0 run forever.
 *   2. While n is divisible by 3, divide it by 3. This strips one factor of
 *      three per turn, exactly like peeling a digit with /= 10.
 *   3. If nothing but 1 remains, the original number was only threes multiplied
 *      together. Anything else left over means some other prime factor existed.
 *   4. A second method shows the constant-time trick, explained below.
 *
 * KEY INSIGHT
 *   "Is n a power of b" is the same question as "does n factor into nothing but
 *   b". Dividing down until it stops being divisible answers it in O(log n).
 *   The O(1) trick works because 3 is prime: 3^19 = 1162261467 is the biggest
 *   power of three an int holds, and a prime's power has no divisors except
 *   smaller powers of that same prime - so 1162261467 % n == 0 is conclusive.
 *
 *   Fixed: isPowerOfThree was an instance method but main called it statically,
 *   so the file did not compile. It is static now.
 *
 * COMPLEXITY
 *   Time  O(log n) base 3 for the loop, O(1) for the divisor trick
 *   Space O(1)     a single int
 *
 * INTERVIEW FOLLOW-UPS
 *   - Power of two? Then use bits: n > 0 && (n & (n - 1)) == 0.
 *   - Why does the prime trick fail for base 4? 4 is not prime, so 4^k has
 *     divisors such as 2 that are not powers of 4 - but 2 does not divide any
 *     4^k evenly to 1, so you must still test the quotient.
 *   - Logarithm approach: Math.log(n) / Math.log(3) rounded - risky, floating
 *     point rounding misreports values like 243.
 *   - Generalise to any base? See B06_PowerCheck in this folder.
 *
 * RUN
 *   main() runs 6 cases (typical, non-power, both boundary edges, negative,
 *   largest int power) and prints actual vs expected for both methods.
 */

class PowerOfThree {

    /** Largest power of three that fits in a signed 32-bit int: 3^19. */
    private static final int MAX_POWER_OF_THREE = 1162261467;

    /** Strip factors of three until none are left; a true power ends at 1. */
    public static boolean isPowerOfThree(int n) {
        if (n <= 0) {
            return false; // also guards the n == 0 infinite loop: 0 % 3 == 0
        }
        while (n % 3 == 0) {
            n = n / 3;
        }
        return n == 1;
    }

    /**
     * Constant-time twin. Valid only because 3 is prime, so every divisor of
     * 3^19 is itself a power of three.
     */
    public static boolean isPowerOfThreeNoLoop(int n) {
        return n > 0 && MAX_POWER_OF_THREE % n == 0;
    }

    private static void check(int n, boolean expected) {
        System.out.println("n = " + n
                + "   loop -> " + isPowerOfThree(n)
                + "   noLoop -> " + isPowerOfThreeNoLoop(n)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        check(27, true);                   // typical
        check(36, false);                  // typical non-power
        check(1, true);                    // edge: 3^0
        check(0, false);                   // edge: zero
        check(-3, false);                  // edge: negative
        check(MAX_POWER_OF_THREE, true);   // tricky: largest int power of three
    }
}

/*
 * =====================================================================
 *  Power Of An Arbitrary Base                          no LeetCode id | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer value and an integer base, decide whether value is an exact power of
 *   base, that is whether value == base^k for some integer k >= 0.
 *   Only positive inputs can qualify: a positive base raised to any power is never 0 and
 *   never negative.
 *
 * EXAMPLE
 *   value = 64, base = 4  ->  true    4 * 4 * 4 = 64
 *   value = 12, base = 4  ->  false   12 / 4 = 3, and 3 is not 1
 *   value = 1,  base = 4  ->  true    any base to the power 0 is 1
 *   value = 5,  base = 1  ->  false   1^k is always 1, so 1 is the only power of 1
 *
 * APPROACH  (repeated division / divide the base out)
 *   1. Reject the impossible inputs first: value <= 0 or base <= 0.
 *   2. Handle base == 1 separately, otherwise the loop below would never make progress.
 *   3. While base divides value evenly, divide it out: value /= base.
 *   4. If what is left is exactly 1, value was built out of nothing but factors of base.
 *
 * KEY INSIGHT
 *   "Is x a power of b" is the same question as "does x factor into nothing but b".
 *   Stripping one factor of b per iteration ends at 1 exactly when no foreign prime factor
 *   was ever present, and at some other leftover when one was. The loop is only log_b(value)
 *   iterations; all the real difficulty sits in the degenerate bases (0 and 1) and in the
 *   non-positive values, which is exactly what the interviewer is probing.
 *
 * COMPLEXITY
 *   Time  O(log_b n)  every iteration divides value by base at least once
 *   Space O(1)        one accumulator, no recursion, no extra structures
 *
 * INTERVIEW FOLLOW-UPS
 *   - Power of two with no loop at all: n > 0 && (n & (n - 1)) == 0.
 *   - Power of three with no loop: n > 0 && 1162261467 % n == 0 (largest 3^k inside an int).
 *   - Why is Math.log(n) / Math.log(base) a trap? Floating point rounding misjudges values
 *     such as 243 with base 3, so it needs an epsilon and is still fragile.
 *   - Same routine on long inputs, or "is 64 a power of 8 and also of 4" (both true).
 *
 * RUN
 *   main() runs 7 cases: a typical true and false, the k = 0 case, both base-1 cases,
 *   and the non-positive edges.
 */

class PowerCheck {

    /** True when value == base^k for some integer k >= 0. */
    public static boolean isPowerOf(int value, int base) {
        // No power of a positive base can be zero or negative.
        if (value <= 0 || base <= 0) return false;

        // 1^k == 1 for every k, so 1 is the only power of 1; without this the loop below
        // would spin forever because value % 1 is always 0 and value / 1 never shrinks.
        if (base == 1) return value == 1;

        while (value % base == 0) {
            value /= base;          // peel off one factor of base
        }
        return value == 1;          // nothing but factors of base were present
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + " -> actual " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1: isPowerOf(64, 4)  typical true ", isPowerOf(64, 4), true);
        print("case 2: isPowerOf(12, 4)  typical false", isPowerOf(12, 4), false);
        print("case 3: isPowerOf(1, 4)   k == 0       ", isPowerOf(1, 4), true);
        print("case 4: isPowerOf(1, 1)   base 1       ", isPowerOf(1, 1), true);
        print("case 5: isPowerOf(5, 1)   base 1       ", isPowerOf(5, 1), false);
        print("case 6: isPowerOf(0, 2)   zero value   ", isPowerOf(0, 2), false);
        print("case 7: isPowerOf(-8, 2)  negative     ", isPowerOf(-8, 2), false);
    }
}

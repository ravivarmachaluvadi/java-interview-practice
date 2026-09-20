/*
 * =====================================================================
 *  Binary Exponentiation (Fast Power)          Building block | Easy  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Compute b^p for a long base b and a non-negative exponent p, in
 *   O(log p) multiplications instead of the naive p of them. A second
 *   version returns b^p modulo m, which is what large-exponent problems
 *   actually ask for because the plain power overflows almost immediately.
 *
 * EXAMPLE
 *   b = 2, p = 6    ->  64                   because 6 = 110b: 2^4 * 2^2
 *   b = 7, p = 0    ->  1                    edge case: anything^0 is 1
 *   b = 2, p = 62   ->  4611686018427387904  the largest power of 2 in a long
 *   b = 2, p = 100, m = 1000000007  ->  976371285   plain long would overflow
 *
 * APPROACH  (square and multiply, driven by the bits of p)
 *   1. Write p in binary. b^p is the product of b^(2^i) over every set bit i.
 *   2. Keep a running `base` holding b^(2^i) for the current bit position,
 *      starting at b (i = 0), and a running `result` starting at 1.
 *   3. Test the low bit with (p & 1). If it is 1, fold `base` into `result`.
 *   4. Square `base` (moving to the next power of two) and shift p right by 1.
 *   5. Stop when p reaches 0 - that is once per bit, so log2(p) rounds.
 *   6. binaryExponentiationMod() is the same loop with every multiply reduced
 *      modulo m, and both operands widened so the product cannot overflow.
 *
 * KEY INSIGHT
 *   Halving the exponent is the same as squaring the base: b^p = (b*b)^(p/2)
 *   when p is even, and b * b^(p-1) when p is odd. That is why the bit test
 *   from "check if the i-th bit is set" turns a linear loop into a logarithmic
 *   one. Recognise it wherever you repeatedly apply an ASSOCIATIVE operation -
 *   modular power, matrix power (fast Fibonacci), even repeated string doubling.
 *
 * COMPLEXITY
 *   Time  O(log p)  one iteration per bit of the exponent
 *   Space O(1)      two accumulators; O(log p) stack if written recursively
 *
 * GOTCHA
 *   The plain long version overflows silently once b^p exceeds 2^63 - 1, and
 *   `base *= base` can overflow on the last round even when the answer fits.
 *   For anything large, use the modular version; Java wraps, it does not throw.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why must the modular version cast to a wider type before multiplying?
 *   - Negative exponent: return 1.0 / pow(b, -p) as a double (LeetCode 50 Pow(x, n)),
 *     and watch n = Integer.MIN_VALUE, where -n overflows.
 *   - Matrix exponentiation: same loop, matrix multiply instead of *, for Fibonacci.
 *   - Modular inverse via Fermat: a^(m-2) mod m when m is prime.
 *
 * RUN
 *   main() runs 8 cases (typical, exponent 0, base 1, a non-power-of-two
 *   exponent, the long boundary, and three modular cases including m = 1)
 *   and prints actual vs expected.
 */

class BinaryExponentiation {

    /** b^p for non-negative p. Overflows silently once the answer exceeds a long. */
    public static long binaryExponentiation(long b, long p) {
        long result = 1;
        long base = b;
        while (p > 0) {
            // Bit i of p is set -> b^(2^i) belongs in the product.
            if ((p & 1) == 1) {
                result *= base;
            }
            base *= base;   // advance base from b^(2^i) to b^(2^(i+1))
            p >>= 1;        // consume the bit we just handled
        }
        return result;
    }

    /**
     * b^p mod m, same loop with every product reduced. Reducing b first and
     * keeping both factors below m means base * base stays under ~2^60 for a
     * typical m around 1e9, so the long multiply cannot overflow.
     */
    public static long binaryExponentiationMod(long b, long p, long m) {
        if (m == 1) return 0;               // everything is 0 mod 1
        long result = 1;
        long base = b % m;
        while (p > 0) {
            if ((p & 1) == 1) {
                result = (result * base) % m;
            }
            base = (base * base) % m;
            p >>= 1;
        }
        return result;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 2^6              ", binaryExponentiation(2, 6), "64");
        print("case 2 7^0  edge        ", binaryExponentiation(7, 0), "1");
        print("case 3 1^63 edge        ", binaryExponentiation(1, 63), "1");
        print("case 4 7^11             ", binaryExponentiation(7, 11), "1977326743");
        print("case 5 2^62 long limit  ", binaryExponentiation(2, 62), "4611686018427387904");
        long mod = 1000000007L;
        print("case 6 2^100 mod 1e9+7  ", binaryExponentiationMod(2, 100, mod), "976371285");
        print("case 7 3^45  mod 1e9+7  ", binaryExponentiationMod(3, 45, mod), "644897553");
        print("case 8 5^13  mod 1      ", binaryExponentiationMod(5, 13, 1), "0");
    }
}

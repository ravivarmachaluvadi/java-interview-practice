/*
 * =====================================================================
 *  P101 Number Theory Basics: Sieve, Fast Power, GCD   Canonical LC 204 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 204, Count Primes)
 *   Return how many prime numbers are strictly less than n.
 *
 * EXAMPLE
 *   n = 10   ->  4      2, 3, 5, 7
 *   n = 0    ->  0
 *   n = 100  ->  25
 *
 * RECOGNIZE WHEN
 *   - Primes up to a limit, prime factors, divisors.
 *   - x^n for big n, or "answer modulo 1e9 + 7" with exponents.
 *   - Greatest common divisor / least common multiple, fractions, "repeat a pattern".
 *   - Trailing zeros, digit sums and other counting questions with a closed form.
 *   Not this if: the problem is about bits -> P100_BitMasksCounting; counting paths or
 *   arrangements with constraints -> DP.
 *
 * TEMPLATE
 *   sieve:     prime[2..n) = true; for p while p * p < n: if prime[p]: mark p*p, p*p+p, ...
 *   fast pow:  result = 1; while e > 0: if e odd: result *= base; base *= base; e >>= 1
 *   gcd:       gcd(a, b) = b == 0 ? a : gcd(b, a % b);   lcm = a / gcd(a, b) * b
 *   modulo:    (a * b) % m with long; inverse of a mod prime m = a^(m - 2) mod m
 *
 * APPROACH
 *   1. Assume everything from 2 up is prime.
 *   2. For each prime p, cross out its multiples starting at p * p (smaller multiples were
 *      already crossed out by smaller primes).
 *   3. Count what is left.
 *
 * KEY INSIGHT
 *   Each of these tools turns a slow loop into a fast one by reusing structure: the sieve
 *   crosses out composites instead of testing each number, fast power squares the base so
 *   the exponent halves each step, and Euclid replaces the larger number by a remainder.
 *
 * COMPLEXITY
 *   Sieve O(n log log n) time, O(n) space. Fast power O(log n). GCD O(log min(a, b)).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 50   Pow(x, n)                binary exponentiation; negative n -> 1 / x^|n|,
 *                                            using long so -2^31 does not overflow
 *   [coded] GCD and LCM                      Euclid; lcm = a / gcd * b to avoid overflow
 *   [coded] LC 172  Factorial Trailing Zeros count factors of 5: n/5 + n/25 + n/125 + ...
 *           LC 1071 GCD of Strings           if s + t == t + s, the answer has gcd length
 *           LC 7    Reverse Integer          check overflow BEFORE multiplying by 10
 *           nCr mod p                        factorials + modular inverses (Fermat)
 *           LC 1922 Count Good Numbers       fast modular power
 *           LC 365  Water and Jug            target must be a multiple of gcd(x, y)
 *
 * PITFALLS
 *   - Sieve: start crossing at p * p, and loop while p * p < n (use long if n is huge).
 *   - Pow: n = Integer.MIN_VALUE cannot be negated as an int.
 *   - LCM: a * b can overflow; divide first.
 *
 * DEEP DIVE
 *   A08_SieveOfEratosthenes, A10_BinaryExponentiation, A09_LCMOfTwoNumbers, A07_PrimeFactors
 *   (17-Math-Bit-Manipulation)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class NumberTheory {

    // Canonical LC 204.
    static int countPrimes(int n) {
        if (n < 3) {
            return 0;
        }
        boolean[] composite = new boolean[n];
        int count = 0;
        for (int p = 2; p < n; p++) {
            if (composite[p]) {
                continue;
            }
            count++;
            for (long m = (long) p * p; m < n; m += p) {
                composite[(int) m] = true;
            }
        }
        return count;
    }

    // LC 50.
    static double myPow(double x, int n) {
        long e = n;                                // long: -(-2^31) fits
        if (e < 0) {
            x = 1 / x;
            e = -e;
        }
        double result = 1;
        while (e > 0) {
            if ((e & 1) == 1) {
                result *= x;
            }
            x *= x;
            e >>= 1;
        }
        return result;
    }

    static long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    static long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }

    // LC 172: zeros come from 2 * 5 pairs; fives are the scarce factor.
    static int trailingZeroes(int n) {
        int zeros = 0;
        while (n > 0) {
            n /= 5;
            zeros += n;
        }
        return zeros;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 204 n=10", countPrimes(10), 4);
        check("LC 204 n=0", countPrimes(0), 0);
        check("LC 204 n=2 (strictly less)", countPrimes(2), 0);
        check("LC 204 n=100", countPrimes(100), 25);

        check("LC 50 2^10", myPow(2.0, 10), 1024.0);
        check("LC 50 2^-2", myPow(2.0, -2), 0.25);
        check("LC 50 1^(int min)", myPow(1.0, Integer.MIN_VALUE), 1.0);
        check("LC 50 2^(int min)", myPow(2.0, Integer.MIN_VALUE), 0.0);

        check("gcd(12,18)", gcd(12, 18), 6L);
        check("gcd(0,5)", gcd(0, 5), 5L);
        check("lcm(4,6)", lcm(4, 6), 12L);
        check("lcm without overflow", lcm(2_000_000_000L, 3_000_000_000L), 6_000_000_000L);

        check("LC 172 n=3", trailingZeroes(3), 0);
        check("LC 172 n=5", trailingZeroes(5), 1);
        check("LC 172 n=100", trailingZeroes(100), 24);
    }
}

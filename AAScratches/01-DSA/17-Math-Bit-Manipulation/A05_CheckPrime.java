/*
 * =====================================================================
 *  Check Prime                                Building block | Easy  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer n, decide whether it is prime: greater than 1 and
 *   divisible only by 1 and itself. 0, 1 and every negative number are
 *   not prime. The input can be as large as Integer.MAX_VALUE.
 *
 * EXAMPLE
 *   n = 17          ->  true    no divisor in 2..4
 *   n = 25          ->  false   5 * 5, caught exactly at the sqrt bound
 *   n = 1           ->  false   by definition
 *   n = -7          ->  false   negatives are excluded
 *   n = 2147483647  ->  true    MAX_VALUE is prime; the loop runs 46340 times
 *
 * APPROACH  (trial division up to sqrt(n))
 *   1. Return false immediately for n <= 1.
 *   2. Try every candidate divisor i starting at 2.
 *   3. If n % i == 0, n is composite - return false.
 *   4. Stop once i * i exceeds n. Surviving that means n is prime.
 *
 * KEY INSIGHT
 *   Divisors come in pairs: if i divides n then so does n / i, and one
 *   member of every pair is <= sqrt(n). So a divisor above sqrt(n) can
 *   only exist if a matching one below it already did - checking past
 *   sqrt(n) can never find anything new. That single fact turns O(n)
 *   into O(sqrt(n)) and is the bound every divisor, factorisation and
 *   sieve routine in this folder assumes.
 *   Write the stop test as (long) i * i <= n, not i <= Math.sqrt(n).
 *   Math.sqrt is a double call re-evaluated every iteration, and the
 *   long cast is what stops i * i overflowing near MAX_VALUE.
 *
 * COMPLEXITY
 *   Time  O(sqrt(n))  at most sqrt(n) - 1 modulo operations
 *   Space O(1)        one loop counter
 *
 * INTERVIEW FOLLOW-UPS
 *   - Halve the work: test 2, then only odd i. Or use the 6k +/- 1 step
 *     after ruling out 2 and 3.
 *   - Testing many numbers up to N? Use a sieve, O(N log log N) total,
 *     instead of calling this N times.
 *   - Numbers far beyond long? Miller-Rabin, a probabilistic test.
 *
 * RUN
 *   main() runs 8 cases (typical, perfect square, 0/1, negative,
 *   MAX_VALUE) and prints actual vs expected.
 */
class PrimeCheck {

    public static boolean isPrime(int n) {
        if (n <= 1) return false;               // 0, 1 and negatives are not prime

        // (long) i * i avoids int overflow when i approaches 46341.
        for (int i = 2; (long) i * i <= n; i++) {
            if (n % i == 0) return false;       // found a divisor below sqrt(n)
        }
        return true;
    }

    public static void main(String[] args) {
        int[] testNumbers = {17, 25, 1, 0, -7, 2, 49, Integer.MAX_VALUE};
        boolean[] expected = {true, false, false, false, false, true, false, true};

        for (int i = 0; i < testNumbers.length; i++) {
            System.out.println("case " + (i + 1) + ": n=" + testNumbers[i]
                    + " -> " + isPrime(testNumbers[i])
                    + "   expected " + expected[i]);
        }
    }
}

/*
 * =====================================================================
 *  Distinct Prime Factors                      Building block | Easy  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer n >= 2, return its distinct prime factors in ascending
 *   order. Each prime appears once no matter how many times it divides n,
 *   so 8 = 2*2*2 gives [2]. n < 2 has no prime factors: return an empty list.
 *
 * EXAMPLE
 *   n = 60   ->  [2, 3, 5]     because 60 = 2^2 * 3 * 5
 *   n = 97   ->  [97]          prime: the leftover after the loop is itself
 *   n = 64   ->  [2]           repeated factor still listed once
 *   n = 1    ->  []            edge case
 *
 * APPROACH  (trial division, divide out until indivisible)
 *   1. Walk a candidate divisor i upward starting at 2.
 *   2. If i divides n, record i once, then divide n by i repeatedly until it
 *      no longer divides. That strips every copy of i out of n.
 *   3. Because every smaller factor was already stripped, any i that still
 *      divides n must be prime - no primality test is needed.
 *   4. getPrimeFactors() walks i all the way to n (the author's version).
 *      getPrimeFactorsSqrt() stops at sqrt(n) and adds whatever is left,
 *      which is the version to write in an interview.
 *
 * KEY INSIGHT
 *   Dividing n down as you go is what makes plain trial division correct AND
 *   fast: composite candidates can never divide the shrunken n, so the first
 *   divisor you meet is always prime. After the loop passes sqrt(n), anything
 *   greater than 1 still sitting in n is a single large prime - that leftover
 *   is the step people forget, and it is why 2*10^9 factors in microseconds.
 *
 * COMPLEXITY
 *   Time  O(sqrt(n))  for getPrimeFactorsSqrt; the plain loop is O(n) in the
 *                     worst case (n prime) because it walks i up to n itself
 *   Space O(k)        k = number of distinct primes, at most ~9 for an int
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return factors with multiplicity (60 -> [2, 2, 3, 5]): drop the "once".
 *   - Factor many numbers up to N fast: smallest-prime-factor sieve, O(log n) each.
 *   - Count divisors from the factorization: product of (exponent + 1).
 *   - What breaks for 64-bit semiprimes? Trial division dies; Pollard's rho.
 *
 * RUN
 *   main() runs 5 cases (typical, prime, prime power, n = 1, large prime).
 *   n = 60 goes through both implementations, the rest through the sqrt
 *   version only; each line prints actual vs expected.
 */

import java.util.ArrayList;

class PrimeFactors {

    /**
     * Author's version: walks i from 2 up to the (shrinking) n.
     * Correct, but when n is prime the loop runs all the way to n.
     */
    public static ArrayList<Integer> getPrimeFactors(int n) {
        ArrayList<Integer> primeFactors = new ArrayList<>();
        for (int i = 2; i <= n; i++) {
            if (n % i == 0) primeFactors.add(i);   // first divisor found is always prime
            while (n % i == 0) n = n / i;          // strip every copy of i out of n
        }
        return primeFactors;
    }

    /**
     * Interview version: stop at sqrt(n), then whatever is left over is prime.
     * Same answer, O(sqrt(n)) even when n is a large prime.
     */
    public static ArrayList<Integer> getPrimeFactorsSqrt(int n) {
        ArrayList<Integer> primeFactors = new ArrayList<>();
        for (int i = 2; (long) i * i <= n; i++) {
            if (n % i != 0) continue;
            primeFactors.add(i);
            while (n % i == 0) n = n / i;
        }
        // Anything above 1 surviving the loop is a single prime bigger than sqrt.
        if (n > 1) primeFactors.add(n);
        return primeFactors;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 n=60  plain ", getPrimeFactors(60), "[2, 3, 5]");
        print("case 1 n=60  sqrt  ", getPrimeFactorsSqrt(60), "[2, 3, 5]");
        print("case 2 n=97  prime ", getPrimeFactorsSqrt(97), "[97]");
        print("case 3 n=64  power ", getPrimeFactorsSqrt(64), "[2]");
        print("case 4 n=1   edge  ", getPrimeFactorsSqrt(1), "[]");
        print("case 5 n=1000000007", getPrimeFactorsSqrt(1000000007), "[1000000007]");
    }
}

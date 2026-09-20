/*
 * =====================================================================
 *  Sieve of Eratosthenes                       Building block | Easy  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer n, list every prime <= n. The point is to get all of
 *   them in one pass rather than testing each number on its own. n < 2 has
 *   no primes, so the answer there is an empty list.
 *
 * EXAMPLE
 *   n = 30  ->  [2, 3, 5, 7, 11, 13, 17, 19, 23, 29]
 *   n = 2   ->  [2]      smallest n with an answer
 *   n = 1   ->  []       edge case: no primes at or below 1
 *   n = 0   ->  []       edge case: array must still be safe to allocate
 *
 * APPROACH  (sieve: cross out multiples, never divide)
 *   1. Make a boolean array isPrime[0..n], all true, then set index 0 and 1
 *      to false - they are not primes and the array is the real answer.
 *   2. For p = 2, 3, 4, ... while p*p <= n: if isPrime[p] is still true, p is
 *      prime, so cross out p*p, p*p+p, p*p+2p, ... up to n.
 *   3. Start crossing at p*p, not 2p: every multiple k*p with k < p already
 *      carries a smaller prime factor and was crossed out in an earlier round.
 *   4. Stop the outer loop at p*p > n: any composite <= n has a prime factor
 *      <= sqrt(n), so it is already gone.
 *   5. Collect every index still marked true.
 *
 * KEY INSIGHT
 *   Flip the question. Instead of asking "is 91 prime?" n times, generate the
 *   composites directly by walking multiples of each prime - addition only, no
 *   division and no modulo. Recognise this whenever a problem needs primality
 *   repeatedly over a bounded range; per-number trial division is O(n*sqrt(n))
 *   where the sieve is near-linear.
 *
 * COMPLEXITY
 *   Time  O(n log log n)  sum of n/p over primes p <= n converges that slowly
 *   Space O(n) bits       one boolean per number in the range
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why start at p*p and why stop the outer loop at p*p <= n? (Steps 3 and 4.)
 *   - Halve the memory: only track odd numbers, or use a bitset.
 *   - Range too large for one array (primes in [L, R], R ~ 1e12): segmented sieve.
 *   - Store the smallest prime factor instead of a boolean: O(log n) factorization.
 *
 * RUN
 *   main() runs 5 cases (typical, boundary n = 2, n = 1, n = 0, and a
 *   prime-count check for n = 100) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SieveOfEratosthenes {

    /** Every prime <= n, ascending. Empty when n < 2. */
    public static List<Integer> sieveOfEratosthenes(int n) {
        List<Integer> primes = new ArrayList<>();
        if (n < 2) return primes;

        boolean[] isPrime = new boolean[n + 1];
        Arrays.fill(isPrime, true);
        isPrime[0] = false;
        isPrime[1] = false;                       // 0 and 1 are not primes

        for (int p = 2; p * p <= n; p++) {
            if (!isPrime[p]) continue;            // already crossed out: skip its multiples
            // Multiples below p*p carry a smaller prime factor and are already gone.
            for (int multiple = p * p; multiple <= n; multiple += p) {
                isPrime[multiple] = false;
            }
        }

        for (int i = 2; i <= n; i++) {
            if (isPrime[i]) primes.add(i);
        }
        return primes;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 n=30      ", sieveOfEratosthenes(30),
                "[2, 3, 5, 7, 11, 13, 17, 19, 23, 29]");
        print("case 2 n=2 edge  ", sieveOfEratosthenes(2), "[2]");
        print("case 3 n=1 edge  ", sieveOfEratosthenes(1), "[]");
        print("case 4 n=0 edge  ", sieveOfEratosthenes(0), "[]");
        print("case 5 count<=100", sieveOfEratosthenes(100).size(), "25");
    }
}

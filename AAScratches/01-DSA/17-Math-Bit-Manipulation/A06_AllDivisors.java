/*
 * =====================================================================
 *  All Divisors of a Number                    Building block | Easy  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a positive integer n, return every positive divisor of n
 *   (every d >= 1 with n % d == 0). n = 0 or negative has no meaningful
 *   divisor list here, so we return an empty list for those.
 *
 * EXAMPLE
 *   n = 12  ->  [1, 12, 2, 6, 3, 4]      pairing order, not sorted
 *   n = 36  ->  [1, 36, 2, 18, 3, 12, 4, 9, 6]   6 is its own partner
 *   n = 1   ->  [1]                       edge case: sqrt(1) == 1 == 1/1
 *   n = 13  ->  [1, 13]                   prime: only the trivial pair
 *
 * APPROACH  (divisor pairing i with n/i)
 *   1. Divisors come in pairs: if i divides n then so does n/i.
 *   2. In every pair one member is <= sqrt(n), so scan i from 1 to sqrt(n)
 *      only - that scan already meets one member of every pair.
 *   3. When n % i == 0, record i and also record the partner n/i.
 *   4. Skip the partner when i == n/i (a perfect square, e.g. 6 for 36),
 *      otherwise the square root would be added twice.
 *   5. The result comes out in pairing order; sortedDivisors() shows the
 *      one extra O(k log k) step if the caller wants ascending order.
 *
 * KEY INSIGHT
 *   Divisors are symmetric around sqrt(n). Touching only the small half of
 *   each pair turns an O(n) scan into O(sqrt(n)) without losing anything.
 *   Recognise this whenever a problem says "all factors", "count divisors"
 *   or "perfect number" - the sqrt bound plus the i == n/i guard is the
 *   whole trick, and the same bound powers primality and factorization.
 *
 * COMPLEXITY
 *   Time  O(sqrt(n))  one modulo per candidate up to sqrt(n)
 *   Space O(k)        k = number of divisors, the output itself; O(1) extra
 *
 * INTERVIEW FOLLOW-UPS
 *   - Count divisors without listing them: same loop, add 2 (or 1 on a square).
 *   - Sum of divisors / perfect-number check: accumulate instead of collecting.
 *   - Count divisors of many numbers up to N: sieve-style, O(N log N) total.
 *   - Why does (int) Math.sqrt(n) stay safe for large int n? Compare against
 *     the overflow-free loop condition i <= n / i.
 *
 * RUN
 *   main() runs 6 cases (typical, perfect square, n = 1, prime, sorted output,
 *   n = 0) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class AllDivisors {

    /** Divisors of n in pairing order: 1, n, 2, n/2, ... Empty when n < 1. */
    public static ArrayList<Integer> findDivisors(int n) {
        ArrayList<Integer> divisors = new ArrayList<>();
        if (n < 1) return divisors;              // 0 and negatives have no useful list

        // i <= n / i is the overflow-free way to say i <= sqrt(n).
        for (int i = 1; i <= n / i; ++i) {
            if (n % i != 0) continue;
            divisors.add(i);
            int partner = n / i;
            // On a perfect square the two halves of the pair coincide - add once.
            if (partner != i) divisors.add(partner);
        }
        return divisors;
    }

    /** Same divisors, ascending. The sort is the only extra cost. */
    public static List<Integer> sortedDivisors(int n) {
        ArrayList<Integer> divisors = findDivisors(n);
        Collections.sort(divisors);
        return divisors;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 n=12       ", findDivisors(12), "[1, 12, 2, 6, 3, 4]");
        print("case 2 n=36 square", findDivisors(36), "[1, 36, 2, 18, 3, 12, 4, 9, 6]");
        print("case 3 n=1  edge  ", findDivisors(1), "[1]");
        print("case 4 n=13 prime ", findDivisors(13), "[1, 13]");
        print("case 5 sorted n=36", sortedDivisors(36), "[1, 2, 3, 4, 6, 9, 12, 18, 36]");
        print("case 6 n=0  edge  ", findDivisors(0), "[]");
    }
}

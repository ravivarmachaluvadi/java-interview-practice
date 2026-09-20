/*
 * =====================================================================
 *  GCD and LCM of Two Numbers                  Building block | Easy  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given two non-negative integers a and b, return their greatest common
 *   divisor and their least common multiple. LCM(a, b) is the smallest
 *   positive number both divide; by convention LCM is 0 when either input
 *   is 0, since 0 is the only common multiple of 0 and anything.
 *
 * EXAMPLE
 *   a = 3, b = 5              ->  gcd 1,  lcm 15        coprime
 *   a = 12, b = 18            ->  gcd 6,  lcm 36
 *   a = 0, b = 5              ->  gcd 5,  lcm 0         edge case
 *   a = 1000000, b = 999999   ->  gcd 1,  lcm 999999000000   overflows int
 *
 * APPROACH  (Euclid's algorithm, then the GCD-LCM identity)
 *   1. Euclid: gcd(a, b) = gcd(b, a % b). Keep replacing the larger number by
 *      its remainder against the smaller one; when one hits 0 the other is
 *      the GCD. This loop does the same thing without recursion.
 *   2. Identity: a * b = gcd(a, b) * lcm(a, b), so lcm = a * b / gcd.
 *   3. Fixed: the original computed (a * b) / gcd, which overflows int before
 *      the division ever happens. Divide first - (a / gcd) * b - and widen to
 *      long, because the product legitimately can exceed int range.
 *   4. Guard gcd == 0 (only when a and b are both 0) to avoid divide-by-zero.
 *
 * KEY INSIGHT
 *   Euclid works because any common divisor of a and b also divides a % b,
 *   so the set of common divisors never changes while the numbers shrink fast.
 *   And never compute a*b before dividing by the GCD: divide first. That one
 *   reordering is the difference between a correct LCM and a silent overflow,
 *   and it is the follow-up interviewers actually probe.
 *
 * COMPLEXITY
 *   Time  O(log min(a, b))  each modulo at least halves the larger value
 *   Space O(1)              a couple of locals, no recursion stack
 *
 * INTERVIEW FOLLOW-UPS
 *   - Write Euclid recursively, then explain why iterative is safer for huge inputs.
 *   - LCM of a whole array: fold pairwise, still dividing before multiplying.
 *   - Extended Euclid: also return x, y with a*x + b*y = gcd (modular inverses).
 *   - Handle negatives: take absolute values first, or define gcd as non-negative.
 *
 * RUN
 *   main() runs 5 cases (coprime, shared factor, zero, equal inputs, overflow)
 *   and prints actual vs expected.
 */

class LCMOfTwoNumbers {

    /** Euclid's algorithm, iterative. Returns the non-zero survivor. */
    public static int gcd(int a, int b) {
        while (a > 0 && b > 0) {
            // Shrink whichever is larger by taking it modulo the other.
            if (a > b) {
                a = a % b;
            } else {
                b = b % a;
            }
        }
        // Exactly one of them is 0 now (or both, if both inputs were 0).
        return a == 0 ? b : a;
    }

    /**
     * lcm = a * b / gcd, but divided BEFORE multiplying so the intermediate
     * product never overflows. Returns long because the true LCM often does
     * not fit in an int.
     */
    public static long lcm(int a, int b) {
        int g = gcd(a, b);
        if (g == 0) return 0;                 // only when a == b == 0
        return (long) (a / g) * b;            // a / g is exact: g divides a
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 gcd(3, 5)              ", gcd(3, 5), "1");
        print("case 1 lcm(3, 5)              ", lcm(3, 5), "15");
        print("case 2 gcd(12, 18)            ", gcd(12, 18), "6");
        print("case 2 lcm(12, 18)            ", lcm(12, 18), "36");
        print("case 3 gcd(0, 5)   edge       ", gcd(0, 5), "5");
        print("case 3 lcm(0, 5)   edge       ", lcm(0, 5), "0");
        print("case 4 gcd(7, 7)   equal      ", gcd(7, 7), "7");
        print("case 4 lcm(7, 7)   equal      ", lcm(7, 7), "7");
        print("case 5 lcm(1000000, 999999)   ", lcm(1000000, 999999), "999999000000");
        // The old (a * b) / gcd form would have printed this instead:
        print("case 5 overflowing int form   ", (1000000 * 999999) / gcd(1000000, 999999),
                "-728379968  (the bug this file fixes)");
    }
}

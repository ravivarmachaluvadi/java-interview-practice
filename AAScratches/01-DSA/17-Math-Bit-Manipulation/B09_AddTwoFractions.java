/*
 * =====================================================================
 *  Add Two Fractions (lowest terms)                    no LeetCode id | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given two fractions num1/den1 and num2/den2 as integers, return their sum as a string
 *   "n/d" reduced to lowest terms. Denominators are non-zero; numerators and denominators
 *   may be negative. The sign of the result belongs on the numerator, never the denominator.
 *
 * EXAMPLE
 *   1/3 + 2/5    ->  "11/15"    5*1 + 3*2 = 11 over 15, already coprime
 *   1/2 + 1/2    ->  "1/1"      4/4 reduces by gcd 4
 *   -1/2 + 0/1   ->  "-1/2"     the sign stays on top (this case used to print "1/-2")
 *   1/3 + -1/3   ->  "0/1"      gcd(0, 9) = 9, so the zero result reduces to 0/1
 *   1/2 + -1/-2  ->  "1/1"      a negative denominator on the input is normalised away
 *
 * APPROACH  (cross multiply, then reduce by the GCD)
 *   1. Common denominator = den1 * den2 (not the lcm - simpler, just larger).
 *   2. Numerator = den2 * num1 + den1 * num2, the two numerators cross multiplied.
 *   3. g = gcd(numerator, denominator), computed on absolute values by Euclid's algorithm.
 *   4. Divide both by g; if the denominator came out negative, flip the sign of both so the
 *      minus sign ends up on the numerator.
 *
 * KEY INSIGHT
 *   Fraction arithmetic is two separate steps that beginners fuse: combine, then normalise.
 *   Combining is pure cross multiplication; normalising is one gcd plus a sign convention.
 *   Euclid's gcd - gcd(a, b) = gcd(b, a % b) until b is 0 - is the primitive, and reducing
 *   at every step is what keeps repeated additions from overflowing. Note that Java's % keeps
 *   the sign of the left operand, so a raw Euclid on negative inputs can RETURN a negative
 *   gcd; that is why the absolute values are taken up front.
 *
 *   Fixed: gcd() could return a negative divisor for negative numerators, which pushed the
 *   minus sign onto the denominator and printed results such as "1/-2" instead of "-1/2".
 *
 * COMPLEXITY
 *   Time  O(log(min(n, d)))  Euclid's algorithm; the arithmetic itself is O(1)
 *   Space O(log(min(n, d)))  recursion depth of gcd (an iterative gcd would be O(1))
 *
 * INTERVIEW FOLLOW-UPS
 *   - den1 * den2 overflows an int quickly. Use lcm(den1, den2) = den1 / gcd * den2, or longs.
 *   - Return "1" rather than "1/1", and "3" rather than "3/1" - how would you special-case it?
 *   - Build a full Fraction class with add, multiply, compareTo and equals, normalising in the
 *     constructor so every instance is always in lowest terms.
 *   - Convert the reduced fraction to its decimal expansion, marking the repeating part
 *     (LeetCode 166) - that is the C-tier problem in this same folder.
 *
 * RUN
 *   main() runs 5 cases: typical, an exact whole number, a negative numerator, a zero sum,
 *   and a negative input denominator.
 */

class AddTwoFractions {

    /**
     * Euclid's algorithm on absolute values.
     * The abs() matters: Java's % keeps the sign of the left operand, so gcd(-2, 4) would
     * otherwise return -2 and flip the sign of the fraction it is used to reduce.
     */
    public static int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        return b == 0 ? a : gcd(b, a % b);
    }

    /** Add num1/den1 and num2/den2, returning the sum in lowest terms as "n/d". */
    public static String addFractions(int num1, int den1, int num2, int den2) {
        if (den1 == 0 || den2 == 0) {
            throw new IllegalArgumentException("denominator must not be zero");
        }

        int commonDenominator = den1 * den2;                  // not the lcm, just a common one
        int resultNumerator = den2 * num1 + den1 * num2;      // cross multiply, then add

        int divisor = gcd(resultNumerator, commonDenominator);
        resultNumerator /= divisor;
        commonDenominator /= divisor;

        // Convention: the sign lives on the numerator, so -1/2 rather than 1/-2.
        if (commonDenominator < 0) {
            resultNumerator = -resultNumerator;
            commonDenominator = -commonDenominator;
        }

        return resultNumerator + "/" + commonDenominator;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + " -> actual " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1: 1/3 + 2/5   ", addFractions(1, 3, 2, 5), "11/15");
        print("case 2: 1/2 + 1/2   ", addFractions(1, 2, 1, 2), "1/1");
        print("case 3: -1/2 + 0/1  ", addFractions(-1, 2, 0, 1), "-1/2");
        print("case 4: 1/3 + -1/3  ", addFractions(1, 3, -1, 3), "0/1");
        print("case 5: 1/2 + -1/-2 ", addFractions(1, 2, -1, -2), "1/1");
    }
}

/*
 * =====================================================================
 *  First, Middle and Last Digit                      Warm-up | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an inclusive range [lo, hi] inside 100..999, return every three-digit
 *   number whose middle digit equals the sum of its first and last digits.
 *   A range outside 100..999 is rejected, because the digit formulas below
 *   assume exactly three digits.
 *
 * EXAMPLE
 *   101..200  ->  [110, 121, 132, 143, 154, 165, 176, 187, 198]   first digit 1, so b = 1 + c
 *   900..999  ->  [990]                    9 + c <= 9 forces c = 0 and b = 9
 *   50..120   ->  IllegalArgumentException  lo is not a three-digit number
 *
 * APPROACH  (Digit extraction by div/mod)
 *   1. Validate 100 <= lo and hi <= 999, otherwise throw.
 *   2. For each n in the range pull the digits arithmetically:
 *        first  = n / 100         drop the last two digits
 *        middle = (n / 10) % 10   drop the last digit, then read the new last one
 *        last   = n % 10          read the last digit
 *   3. Keep n when first + last == middle.
 *
 * KEY INSIGHT
 *   n / 10^k discards the k lowest digits and n % 10 reads the lowest one, so
 *   "digit k from the right" is always (n / 10^k) % 10. The same primitive drives
 *   reverse-integer, palindrome-number, digit-sum and Armstrong numbers; knowing
 *   it cold means those problems cost no thinking time in an interview.
 *
 * COMPLEXITY
 *   Time  O(hi - lo)  one constant-time digit check per number
 *   Space O(k)        for the k matching numbers returned
 *
 * INTERVIEW FOLLOW-UPS
 *   - Reverse an integer: peel with n % 10, push with rev * 10 + digit, watch for overflow.
 *   - Digits of an arbitrary-length number: loop while n > 0 taking n % 10, then n /= 10.
 *   - Count without scanning: for first digit a, last can be 0..9-a, so 10 - a hits per hundred.
 *
 * RUN
 *   main() runs 3 cases (typical, upper boundary, invalid range) and prints
 *   actual vs expected.
 */
import java.util.ArrayList;
import java.util.List;

class FirstMiddleLast {

    public static List<Integer> findNumbersWithMiddleDigitAsSum(int lo, int hi) {
        if (lo < 100 || hi > 999) {
            throw new IllegalArgumentException(
                    "range must lie within 100..999, got " + lo + ".." + hi);
        }
        List<Integer> result = new ArrayList<>();
        for (int n = lo; n <= hi; n++) {
            int first = n / 100;          // hundreds digit
            int middle = (n / 10) % 10;   // tens digit
            int last = n % 10;            // units digit
            if (first + last == middle) {
                result.add(n);
            }
        }
        return result;
    }

    private static void run(String label, int lo, int hi, String expected) {
        String actual;
        try {
            actual = findNumbersWithMiddleDigitAsSum(lo, hi).toString();
        } catch (IllegalArgumentException e) {
            actual = "IllegalArgumentException";
        }
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 typical      ", 101, 200, "[110, 121, 132, 143, 154, 165, 176, 187, 198]");
        run("case 2 upper bound  ", 900, 999, "[990]");
        run("case 3 invalid range", 50, 120, "IllegalArgumentException");
    }
}

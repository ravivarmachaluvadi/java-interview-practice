/*
 * =====================================================================
 *  Leap Year                                LeetCode ? | ?
 * =====================================================================
 *
 * PROBLEM
 *   Given a year, determine whether it is a leap year.
 *   A leap year is one that satisfies either of:
 *     - divisible by 4, but NOT divisible by 100
 *     - divisible by 400
 *
 * EXAMPLE
 *   2024 -> true
 *   1900 -> false
 *   2000 -> true
 *   2023 -> false
 *
 * APPROACH
 *   Encode the Gregorian rule directly as a boolean expression:
 *     (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
 *   Order matters for readability: the common case (div by 4)
 *   is checked first; the century/400 exception comes after.
 *
 * KEY INSIGHT
 *   "Divisible by 4" alone is wrong for century years.
 *   1900 % 4 == 0 but 1900 is NOT a leap year.
 *   2000 % 100 == 0 but 2000 % 400 == 0, so it IS a leap year.
 *
 * COMPLEXITY
 *   Time  O(1)
 *   Space O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - "Simplify with a single expression?"
 *       return (y % 4 == 0 && y % 100 != 0) || (y % 400 == 0);
 *   - "What about negative years (BC)?"  -> use year <= 0 checks.
 *   - "What about the Julian calendar?"  -> only % 4 == 0.
 *   - "How many leap years in [a, b]?"   -> prefix count:
 *       f(y) = y/4 - y/100 + y/400, answer = f(b) - f(a-1).
 *
 * RUN
 *   main() runs the cases below and prints actual vs expected.
 */
class LeapYear {

    static boolean solve(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("case 1 typical     ", solve(2024), true);
        check("case 2 non-leap    ", solve(2023), false);
        check("case 3 century-no  ", solve(1900), false);
        check("case 4 century-yes ", solve(2000), true);
        check("case 5 divisible4  ", solve(2020), true);
        check("case 6 odd year    ", solve(2021), false);
    }
}
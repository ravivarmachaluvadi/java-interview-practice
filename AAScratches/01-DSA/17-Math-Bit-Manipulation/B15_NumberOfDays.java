/*
 * =====================================================================
 *  Number of Days in a Month                              Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a year and a month (1 = January .. 12 = December), return how many
 *   days that month has. Only February varies: 29 days in a leap year, 28
 *   otherwise. The Gregorian leap rule is divisible by 4, EXCEPT centuries,
 *   UNLESS the century is divisible by 400.
 *
 * EXAMPLE
 *   (2020, 2)  ->  29   2020 is divisible by 4 and is not a century
 *   (2021, 2)  ->  28   not divisible by 4
 *   (1900, 2)  ->  28   century, and 1900 is not divisible by 400
 *   (2000, 2)  ->  29   century, but 2000 IS divisible by 400
 *   (2023, 4)  ->  30   April never depends on the year
 *
 * APPROACH  (lookup table plus the leap-year rule)
 *   1. Keep a fixed 12-entry table of month lengths with February as 28.
 *   2. Validate the month is in 1..12 so a bad call fails loudly.
 *   3. If the month is February and the year is a leap year, return 29.
 *   4. Otherwise return table[month - 1]; the table is 0-indexed, the month is not.
 *
 * KEY INSIGHT
 *   The leap rule is three nested exceptions, and the order matters:
 *   year % 400 == 0 is a leap year; otherwise year % 100 == 0 is NOT; otherwise
 *   year % 4 == 0 is. Written as (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
 *   the two branches are disjoint, which is why it reads as a single boolean.
 *   1900 vs 2000 is the pair that catches a wrong implementation, so test it.
 *
 * COMPLEXITY
 *   Time  O(1)  a table read and a few modulo operations
 *   Space O(1)  the 12-entry table is fixed and shared
 *
 * INTERVIEW FOLLOW-UPS
 *   - Count the days between two dates, and where is the off-by-one?
 *   - Which day of the week is a given date (Zeller's congruence / day counting)?
 *   - What does java.time.YearMonth.of(y, m).lengthOfMonth() do instead, and why
 *     would you still hand-roll this in an interview?
 *   - Why 400 at all - what is the true length of the tropical year?
 *
 * NOTE ON THE ORIGINAL
 *   The leap logic was already correct. Hardened: a month outside 1..12 used to
 *   throw ArrayIndexOutOfBoundsException from deep inside; it now reports the
 *   bad argument directly. The table is now a shared constant instead of being
 *   rebuilt on every call.
 *
 * RUN
 *   main() runs 6 cases (leap, non-leap, the 1900/2000 century pair, a fixed
 *   month, an invalid month) and prints actual vs expected.
 */
class NumberOfDays {

    private static final int[] DAYS_IN_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public int numberOfDays(int year, int month) {
        if (month < 1 || month > 12)
            throw new IllegalArgumentException("month must be 1..12 but was " + month);

        if (month == 2 && isLeapYear(year))
            return 29;                       // February is the only month that moves

        return DAYS_IN_MONTH[month - 1];     // table is 0-indexed, month is 1-indexed
    }

    /** Divisible by 4, except centuries, unless the century divides by 400. */
    static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    public static void main(String[] args) {
        NumberOfDays solution = new NumberOfDays();

        print("case 1 Feb 2020 (leap)      ", solution.numberOfDays(2020, 2), 29);
        print("case 2 Feb 2021 (not leap)  ", solution.numberOfDays(2021, 2), 28);
        print("case 3 Feb 1900 (century)   ", solution.numberOfDays(1900, 2), 28);
        print("case 4 Feb 2000 (400-year)  ", solution.numberOfDays(2000, 2), 29);
        print("case 5 Apr 2023 (fixed)     ", solution.numberOfDays(2023, 4), 30);

        try {
            solution.numberOfDays(2023, 13);
            System.out.println("case 6 month 13             : no exception"
                    + "   expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            System.out.println("case 6 month 13             : IllegalArgumentException"
                    + "   expected IllegalArgumentException");
        }
    }

    private static void print(String label, int actual, int expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

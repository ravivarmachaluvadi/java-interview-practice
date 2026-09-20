/*
 * =====================================================================
 *  Number to Words (Indian system)          LeetCode 273 variant | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Convert an int to English words using the Indian grouping: crore (10^7), lakh (10^5),
 *   thousand, hundred, then the last two digits. Insert "and" before the final 1-99 when the
 *   number has a hundreds-or-higher part ("one thousand and thirty four"). Handle 0, negatives
 *   and the whole int range: Integer.MAX_VALUE is 214 crore, so the crore group itself can be
 *   a three-digit number.
 *
 * EXAMPLE
 *   123456789  -> "twelve crore thirty four lakh fifty six thousand seven hundred and eighty nine"
 *   1034       -> "one thousand and thirty four"
 *   100000000  -> "ten crore"
 *   0          -> "zero"
 *   -45        -> "minus forty five"
 *   2147483647 -> "two hundred and fourteen crore seventy four lakh eighty three thousand
 *                  six hundred and forty seven"
 *
 * APPROACH  (group decomposition with lookup tables)
 *   1. Two tables: LESS_THAN_20[0..19] and TENS[2..9]; belowHundred(n) reads them.
 *   2. Split by integer division: crore = n / 10^7, lakh = (n / 10^5) % 100,
 *      thousand = (n / 1000) % 100, hundred = (n / 100) % 10, rest = n % 100.
 *   3. Emit each non-zero group as "<words> <unit> ". The crore group goes through
 *      belowThousand() because it can reach 214.
 *   4. Emit "and " when n > 99 and rest > 0, then belowHundred(rest); trim the trailing space.
 *
 * KEY INSIGHT
 *   Every group is "a number below 100 (or 1000) plus a unit word", so one small helper does
 *   all the wording and the top level is only arithmetic on group boundaries. The international
 *   system (LeetCode 273) has the same shape with uniform groups of 1000 (thousand, million,
 *   billion) instead of the 1000-100-100 pattern of thousand, lakh, crore.
 *
 * Fixed: a crore group of 100 or more (any n >= 100 crore, e.g. Integer.MAX_VALUE) indexed
 *        TENS[21] and threw ArrayIndexOutOfBoundsException. Negative input was not handled.
 *
 * COMPLEXITY
 *   Time  O(1)  a fixed number of groups for a 32-bit int
 *   Space O(1)  a few short strings
 *
 * INTERVIEW FOLLOW-UPS
 *   - LeetCode 273 (international): three-digit groups with Thousand / Million / Billion.
 *   - long input: which extra units appear (arab, kharab) and where belowThousand() applies.
 *   - Currency: "rupees ... and ... paise" is the same converter called twice.
 *   - The inverse, words to number: running total with a multiplier for each unit word.
 *
 * RUN
 *   main() runs 7 cases and prints actual vs expected.
 */
class NumberToWordsConverter {

    private static final String[] LESS_THAN_20 = {
            "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
            "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
            "seventeen", "eighteen", "nineteen"};

    private static final String[] TENS = {
            "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};

    /** 0..99 -> "" | "seven" | "twenty one"  (no trailing space). */
    private static String belowHundred(int n) {
        if (n < 20) return LESS_THAN_20[n];
        return n % 10 == 0 ? TENS[n / 10] : TENS[n / 10] + " " + LESS_THAN_20[n % 10];
    }

    /** 0..999 -> "" | "two hundred" | "two hundred and fourteen". Needed for the crore group. */
    private static String belowThousand(int n) {
        if (n < 100) return belowHundred(n);
        String hundreds = LESS_THAN_20[n / 100] + " hundred";
        return n % 100 == 0 ? hundreds : hundreds + " and " + belowHundred(n % 100);
    }

    /** "" for an empty group, otherwise "<words> <unit> " with a trailing space. */
    private static String withUnit(String words, String unit) {
        return words.isEmpty() ? "" : words + " " + unit + " ";
    }

    public static String convertToWords(int number) {
        if (number == 0) return "zero";
        long n = Math.abs((long) number);        // long so Integer.MIN_VALUE negates safely

        int crore    = (int) (n / 10_000_000);    // up to 214 for a 32-bit int
        int lakh     = (int) (n / 100_000 % 100);
        int thousand = (int) (n / 1_000 % 100);
        int hundred  = (int) (n / 100 % 10);
        int rest     = (int) (n % 100);

        StringBuilder words = new StringBuilder(number < 0 ? "minus " : "");
        words.append(withUnit(belowThousand(crore), "crore"))
             .append(withUnit(belowHundred(lakh), "lakh"))
             .append(withUnit(belowHundred(thousand), "thousand"))
             .append(withUnit(belowHundred(hundred), "hundred"));
        if (n > 99 && rest > 0) words.append("and ");   // "one thousand and thirty four"
        words.append(belowHundred(rest));
        return words.toString().trim();
    }

    private static void check(int number, String expected) {
        System.out.println(number + " -> \"" + convertToWords(number)
                + "\"   expected \"" + expected + "\"");
    }

    public static void main(String[] args) {
        // typical
        check(123456789,
                "twelve crore thirty four lakh fifty six thousand seven hundred and eighty nine");
        check(1034, "one thousand and thirty four");
        // edge
        check(0, "zero");
        check(20, "twenty");
        check(-45, "minus forty five");
        // tricky: empty middle groups, and a three-digit crore group
        check(100000000, "ten crore");
        check(Integer.MAX_VALUE, "two hundred and fourteen crore seventy four lakh "
                + "eighty three thousand six hundred and forty seven");
    }
}

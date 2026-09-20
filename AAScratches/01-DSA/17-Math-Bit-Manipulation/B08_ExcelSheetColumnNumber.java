/*
 * =====================================================================
 *  Excel Sheet Column Number                           LeetCode 171 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an Excel column title made of uppercase letters ("A", "AB", "ZY"), return the
 *   1-based column number it names. A = 1, B = 2, ... Z = 26, AA = 27, AB = 28, and so on.
 *   Titles in the LeetCode constraints stay within the range of a signed 32-bit int.
 *
 * EXAMPLE
 *   "A"       ->  1 "AB"      ->  28           1 * 26 + 2
 *   "ZY"      ->  701          26 * 26 + 25
 *   "Z"       ->  26           last single-letter column
 *   "AA"      ->  27           the roll-over right after Z
 *   "FXSHRXW" ->  2147483647   the largest title that still fits in an int
 *
 * APPROACH  (base-26 positional parse, Horner style)
 *   1. Start with result = 0.
 *   2. Walk the title left to right. For each letter compute its digit value ch - 'A' + 1,
 *      which maps 'A' to 1 and 'Z' to 26.
 *   3. Shift what is already accumulated one place left in base 26 (result * 26) and add the
 *      new digit: result = result * 26 + value.
 *   4. After the last letter, result is the column number.
 *
 * KEY INSIGHT
 *   This is ordinary positional number parsing in base 26, with one twist: the alphabet is
 *   1-indexed. There is no digit that means zero, so this is "bijective base 26", not plain
 *   base 26. That is why "Z" is 26 rather than 0 and why the successor of "Z" is "AA" rather
 *   than "BA". Recognise Horner's rule - result = result * base + digit - and the same three
 *   lines parse binary, hex, or any other base; only the digit-value mapping changes.
 *
 * COMPLEXITY
 *   Time  O(n)   one pass over the n letters of the title
 *   Space O(1)   a single accumulator; the loop uses charAt(i), so nothing is copied
 *                (a `for (char ch : title.toCharArray())` loop would cost O(n) instead)
 *
 * INTERVIEW FOLLOW-UPS
 *   - The inverse, LeetCode 168: number to title. Because there is no zero digit you must do
 *     n-- before each step: n--; ch = (char)('A' + n % 26); n /= 26; then reverse.
 *   - Where does it overflow, and how would you detect it before it happens?
 *   - Handle lowercase or mixed-case input, or reject invalid characters.
 *   - Why can you not reuse Integer.parseInt(title, 26)? Because 'A' would be digit 10 and
 *     there would be a zero digit, which bijective base 26 does not have.
 *
 * RUN
 *   main() runs 6 cases: typical, both single-letter boundaries, the Z to AA roll-over,
 *   and the int-maximum title.
 */

class ExcelSheetColumnNumber {

    /** Parse an Excel column title as a bijective base-26 number ('A' = 1 ... 'Z' = 26). */
    public static int titleToNumber(String columnTitle) {
        int result = 0;
        for (int i = 0; i < columnTitle.length(); i++) {
            char ch = columnTitle.charAt(i); // charAt keeps this O(1) space: no array copy
            int value = ch - 'A' + 1;        // 1-indexed: there is no digit worth zero
            result = result * 26 + value;    // Horner: shift one base-26 place, then add
        }
        return result;
    }

    private static void print(String title, int expected) {
        System.out.println("\"" + title + "\" -> actual " + titleToNumber(title)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("A", 1);                  // case 1: smallest title
        print("AB", 28);                // case 2: typical two-letter title
        print("ZY", 701);               // case 3: both letters near the top of the alphabet
        print("Z", 26);                 // case 4: last single-letter column
        print("AA", 27);                // case 5: the roll-over right after Z
        print("FXSHRXW", 2147483647);   // case 6: largest title that fits in an int
    }
}

/*
 * =====================================================================
 *  Integer to Roman                                        LeetCode 12 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Convert an integer in [1, 3999] to its Roman numeral. Symbols are I=1 V=5 X=10 L=50
 *   C=100 D=500 M=1000. The six subtractive pairs IV=4 IX=9 XL=40 XC=90 CD=400 CM=900 are
 *   used instead of writing four copies of a symbol.
 *
 * EXAMPLE
 *   3     ->  "III"
 *   58    ->  "LVIII"          50 + 5 + 1 + 1 + 1
 *   1994  ->  "MCMXCIV"        1000 + 900 + 90 + 4
 *   1     ->  "I"              edge: smallest input
 *   3999  ->  "MMMCMXCIX"      edge: largest input
 *
 * APPROACH  (greedy over a descending value table)
 *   1. Keep two parallel arrays, values and symbols, sorted from 1000 down to 1, with the
 *      six subtractive pairs (900, 400, 90, 40, 9, 4) placed at their numeric positions.
 *   2. For each entry in order, while num >= value: append the symbol, subtract the value.
 *   3. Stop when num reaches 0.
 *
 * KEY INSIGHT
 *   Greedy is only correct because the subtractive pairs are in the table: 900 sits between
 *   1000 and 500, so 1994 takes M then CM and never tries to build 900 as D + C + C + C + C.
 *   Bake the exceptions into the data so the loop stays exception-free.
 *
 * COMPLEXITY
 *   Time  O(1)  13 table entries, each used at most 3 times, so at most 15 appends
 *   Space O(1)  the output is at most 15 characters ("MMMDCCCLXXXVIII" = 3888)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Roman to Integer (LeetCode 13): scan right to left, subtract when a smaller value
 *     precedes a larger one.
 *   - Why can a symbol appear at most 3 times in a row? Because four of anything is written
 *     as a subtractive pair; use that to justify the O(1) bound.
 *   - Extend past 3999 (overline notation): only the table changes, not the loop.
 *
 * RUN
 *   main() runs 6 cases (typical, subtractive pairs, both boundaries) and prints actual vs
 *   expected.
 */
class IntegerToRoman {

    // Descending, with the subtractive pairs slotted in so greedy picks them naturally.
    private static final int[] VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final String[] SYMBOLS =
            {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    public static String intToRoman(int num) {
        StringBuilder roman = new StringBuilder();
        for (int i = 0; i < VALUES.length && num > 0; i++) {
            while (num >= VALUES[i]) {
                roman.append(SYMBOLS[i]);
                num -= VALUES[i];
            }
        }
        return roman.toString();
    }

    public static void main(String[] args) {
        int[] inputs = {3, 58, 1994, 9, 1, 3999};
        String[] expected = {"III", "LVIII", "MCMXCIV", "IX", "I", "MMMCMXCIX"};

        for (int i = 0; i < inputs.length; i++) {
            print("case " + (i + 1) + " " + inputs[i], intToRoman(inputs[i]), expected[i]);
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

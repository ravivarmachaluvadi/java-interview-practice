/*
 * =====================================================================
 *  Roman to Integer                               LeetCode 13 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Convert a valid Roman numeral string (chars I V X L C D M) to its integer value.
 *   Normally symbols add up left to right, but a smaller symbol placed before a
 *   larger one is subtracted (IV = 4, IX = 9, XL = 40, XC = 90, CD = 400, CM = 900).
 *   Input is guaranteed valid and in the range 1..3999.
 *
 * EXAMPLE
 *   "MCMXCIV"  ->  1994     M + CM + XC + IV = 1000 + 900 + 90 + 4
 *   "III"      ->  3        all additive
 *   "IV"       ->  4        single subtractive pair
 *   "MMMCMXCIX"->  3999     largest valid input
 *
 * APPROACH  (Right-to-left subtractive scan)
 *   1. Build a map from each Roman symbol to its value.
 *   2. Walk the string from the LAST character to the first, remembering the value
 *      of the symbol just processed (prevValue, initially 0).
 *   3. If the current value is smaller than prevValue, subtract it (it is the left
 *      half of a subtractive pair); otherwise add it.
 *   4. Update prevValue and continue. The running total is the answer.
 *
 * KEY INSIGHT
 *   Scanning right to left turns "look ahead to decide" into "look back", which needs
 *   no bounds check: a symbol is subtracted exactly when it is smaller than the one
 *   to its right. Pattern: when a rule depends on the NEXT element, reverse the scan.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass over the string
 *   Space O(1)  the symbol map has a fixed 7 entries
 *
 * INTERVIEW FOLLOW-UPS
 *   - Integer to Roman: greedy over a descending value table that includes the
 *     six subtractive pairs (see C04_IntegerToRoman).
 *   - Validate the input: reject IIII, VX, IC, or a subtractive pair repeated.
 *   - Left-to-right version: if value[i] < value[i+1] subtract, else add.
 *
 * RUN
 *   main() runs 4 cases (typical, all additive, single pair, max) and prints
 *   actual vs expected.
 */
import java.util.HashMap;
import java.util.Map;

class RomanToInteger {

    private static final Map<Character, Integer> ROMAN_VALUES = new HashMap<>();

    static {
        ROMAN_VALUES.put('I', 1);
        ROMAN_VALUES.put('V', 5);
        ROMAN_VALUES.put('X', 10);
        ROMAN_VALUES.put('L', 50);
        ROMAN_VALUES.put('C', 100);
        ROMAN_VALUES.put('D', 500);
        ROMAN_VALUES.put('M', 1000);
    }

    public static int romanToInt(String s) {
        int total = 0;
        int prevValue = 0; // value of the symbol to the RIGHT of the current one

        for (int i = s.length() - 1; i >= 0; i--) {
            int currentValue = ROMAN_VALUES.get(s.charAt(i));
            if (currentValue < prevValue) {
                total -= currentValue; // smaller symbol before a larger one: subtractive pair
            } else {
                total += currentValue;
            }
            prevValue = currentValue;
        }
        return total;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 MCMXCIV", romanToInt("MCMXCIV"), 1994);
        print("case 2 III", romanToInt("III"), 3);
        print("case 3 IV", romanToInt("IV"), 4);
        print("case 4 MMMCMXCIX", romanToInt("MMMCMXCIX"), 3999);
    }
}

/*
 * =====================================================================
 *  Fraction to Recurring Decimal           LeetCode 166 | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a numerator and a non-zero denominator, return the fraction as a
 *   decimal string. If the fractional part repeats forever, wrap exactly the
 *   repeating block in parentheses. Inputs are 32-bit ints, so the result may
 *   be negative and the numerator may be Integer.MIN_VALUE.
 *
 * EXAMPLE
 *   6 / 11   ->  "0.(54)"        0.545454... repeats from the first digit
 *   1 / 6    ->  "0.1(6)"        one non-repeating digit, then a cycle
 *   1 / 2    ->  "0.5"           terminates, no parentheses
 *   -50 / 8  ->  "-6.25"         sign handled once, up front
 *   MIN_VALUE / 1 -> "-2147483648"   the overflow edge case (see Fixed below)
 *
 * APPROACH  (long division with a remainder -> position map)
 *   1. Zero numerator short-circuits to "0".
 *   2. Emit '-' when exactly one of numerator/denominator is negative, then
 *      work with magnitudes only.
 *   3. Append the integer part num / den. If num % den == 0 we are done.
 *   4. Otherwise append '.', then loop: record the CURRENT remainder together
 *      with the index it will produce in the output, multiply it by 10, append
 *      the next quotient digit, and reduce modulo the denominator.
 *   5. The instant a remainder is seen again, the digits from its recorded
 *      index onward are the repeating block: insert '(' there and append ')'.
 *
 * KEY INSIGHT
 *   Do not look for a repeated DIGIT - look for a repeated REMAINDER. Digits
 *   can repeat by coincidence (1/7 = 0.142857... has no early digit clue), but
 *   a remainder fully determines every digit that follows it, so the first
 *   repeated remainder is exactly where the cycle starts. There are only
 *   den - 1 possible non-zero remainders, so the decimal either terminates or
 *   repeats within den steps - never anything else. This is cycle detection on
 *   state, the same idea as Happy Number, with the state being the remainder.
 *
 * COMPLEXITY
 *   Time  O(den)  at most den distinct remainders before one repeats
 *   Space O(den)  the remainder map plus the output builder
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why must the cycle length be at most den - 1? (pigeonhole on remainders)
 *   - Which denominators terminate? (only factors of 2 and 5 after reduction)
 *   - Replace the HashMap with Floyd's tortoise and hare for O(1) extra space -
 *     what do you lose? (you find that a cycle exists, not where it starts)
 *   - Reverse it: given "0.(54)", recover 6/11.
 *
 * Fixed: the original used int arithmetic. Math.abs(Integer.MIN_VALUE) is
 * still negative, and `remainder *= 10` can overflow an int, so large inputs
 * produced garbage. All division work now happens in long.
 *
 * RUN
 *   main() runs 7 cases (pure cycle, delayed cycle, terminating, negative,
 *   zero, and the MIN_VALUE overflow edge) and prints actual vs expected.
 */

import java.util.HashMap;
import java.util.Map;

class RepeatedNumberInFractionAfterDecimal {

    public static String fractionRepresentation(int numerator, int denominator) {
        if (numerator == 0) {
            return "0";
        }

        StringBuilder result = new StringBuilder();

        // Exactly one side negative => the answer is negative.
        if ((numerator < 0) ^ (denominator < 0)) {
            result.append("-");
        }

        // Widen to long BEFORE taking absolute values: Math.abs(Integer.MIN_VALUE)
        // overflows back to a negative int, long has room for 2147483648.
        long num = Math.abs((long) numerator);
        long den = Math.abs((long) denominator);

        result.append(num / den);

        long remainder = num % den;
        if (remainder == 0) {
            return result.toString();  // the decimal terminates immediately
        }
        result.append(".");

        // remainder -> index in `result` where the digit it produces will land.
        Map<Long, Integer> remainderMap = new HashMap<>();

        while (remainder != 0) {
            // Seen this remainder before => every digit from here repeats.
            Integer cycleStart = remainderMap.get(remainder);
            if (cycleStart != null) {
                result.insert(cycleStart, "(");
                result.append(")");
                break;
            }

            remainderMap.put(remainder, result.length());

            // One step of long division: bring down a zero, emit a digit, keep the rest.
            remainder *= 10;
            result.append(remainder / den);
            remainder %= den;
        }

        return result.toString();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1  6/11 (cycle from digit 1)", fractionRepresentation(6, 11), "0.(54)");
        print("case 2  1/6  (delayed cycle)", fractionRepresentation(1, 6), "0.1(6)");
        print("case 3  1/2  (terminates)", fractionRepresentation(1, 2), "0.5");
        print("case 4  1/3  (single repeating digit)", fractionRepresentation(1, 3), "0.(3)");
        print("case 5  -50/8 (negative, terminates)", fractionRepresentation(-50, 8), "-6.25");
        print("case 6  0/5  (edge, zero)", fractionRepresentation(0, 5), "0");
        print("case 7  MIN_VALUE/1 (overflow edge)",
                fractionRepresentation(Integer.MIN_VALUE, 1), "-2147483648");
    }
}

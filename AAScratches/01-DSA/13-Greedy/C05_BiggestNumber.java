/*
 * =====================================================================
 *  Largest Number (Biggest Number)      LeetCode 179 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of non-negative integers, arrange them so that concatenating their
 *   decimal spellings gives the largest possible number. Return it as a String, because
 *   the result easily overflows long. If every input is 0 the answer is "0", not "000".
 *
 * EXAMPLE
 *   [1, 34, 3, 98, 9, 76, 45, 4]  ->  "998764543431"
 *   [3, 30, 34, 5, 9]             ->  "9534330"   note 9 > 5 > 34 > 3 > 30
 *   [0, 0]                        ->  "0"         all zeros collapse to one "0"
 *   [10]                          ->  "10"        single element
 *
 * APPROACH  (comparator on a+b vs b+a)
 *   1. Map every int to its String form; the arrangement only ever concatenates text.
 *   2. Sort the strings with a comparator that, for a pair (a, b), compares the two
 *      possible joins: put a first if a+b > b+a, otherwise put b first.
 *   3. Concatenate in that order.
 *   4. Guard the all-zero case: if the first string after sorting is "0", every string
 *      is "0", so return "0".
 *
 * KEY INSIGHT
 *   This is the exchange argument written as code. If in some arrangement two ADJACENT
 *   items a, b satisfy a+b < b+a, swapping them strictly increases the result and
 *   changes nothing else. So the optimal order has no improving adjacent swap, which is
 *   exactly what sorting by this comparator produces. The comparator is a valid total
 *   order (it is transitive, because a+b >= b+a behaves like comparing the infinite
 *   repetitions of a and b), so Arrays.sort will not throw on it. Comparing "a+b" with
 *   "b+a" also sidesteps the trap of comparing lengths or first digits: 3 vs 30 needs
 *   "330" vs "303", and no digit-by-digit rule gets that right on its own.
 *
 * COMPLEXITY
 *   Time  O(n log n * k)  n log n comparisons, each comparing strings of length ~2k
 *   Space O(n * k)        the string array plus the builder holding the answer
 *
 * INTERVIEW FOLLOW-UPS
 *   - Prove the comparator is transitive; what breaks if it is not?
 *   - Build the SMALLEST number instead (flip the comparator, then strip leading zeros).
 *   - Why can the answer not be returned as a long?
 *   - Negative numbers in the input: why does the whole argument collapse?
 *
 * RUN
 *   main() runs 4 cases: typical, the 3/30 ordering trap, all zeros, single element.
 */

import java.util.Arrays;
import java.util.Comparator;

class BiggestNumber {

    /** Orders two spellings by which join is bigger: a first when a+b beats b+a. */
    private static final Comparator<String> BY_BEST_JOIN =
            (a, b) -> (b + a).compareTo(a + b);   // descending: a+b larger => a comes first

    public static String largestNumber(Integer[] numbers) {
        if (numbers == null || numbers.length == 0) return "0";

        String[] spellings = Arrays.stream(numbers)
                .map(String::valueOf)
                .toArray(String[]::new);

        Arrays.sort(spellings, BY_BEST_JOIN);

        // After sorting, the biggest piece sits first. If that is "0", nothing else
        // can be non-zero, so the whole answer would be "000...0".
        if (spellings[0].equals("0")) return "0";

        StringBuilder result = new StringBuilder();
        for (String piece : spellings) {
            result.append(piece);
        }
        return result.toString();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (typical)     ",
                largestNumber(new Integer[]{1, 34, 3, 98, 9, 76, 45, 4}), "998764543431");
        print("case 2 (3/30 trap)   ",
                largestNumber(new Integer[]{3, 30, 34, 5, 9}), "9534330");
        print("case 3 (all zeros)   ",
                largestNumber(new Integer[]{0, 0}), "0");
        print("case 4 (single item) ",
                largestNumber(new Integer[]{10}), "10");
    }
}

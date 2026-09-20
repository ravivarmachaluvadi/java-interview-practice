/*
 * =====================================================================
 *  Can These Digits Form A Multiple Of 3               no LeetCode id | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of decimal digits, decide whether some arrangement of ALL of them spells
 *   a number that is divisible by 3. Every digit must be used exactly once; the order is
 *   ours to choose. Return true or false.
 *
 * EXAMPLE
 *   digits = [1, 4, 1, 6]  ->  true    sum 12, and 12 % 3 == 0 (1416, 6411, ... all work)
 *   digits = [1, 4, 5]     ->  false   sum 10, so no ordering of 1, 4, 5 is a multiple of 3
 *   digits = [0]           ->  true    the number 0 is divisible by 3
 *   digits = [9, 9, 9, 1]  ->  false   sum 28, one short of 30
 *
 * APPROACH  (digit-sum divisibility rule)
 *   1. Add up every digit in the array in a single pass.
 *   2. Return sum % 3 == 0.
 *   3. That is the whole algorithm - no permutations are generated or tested.
 *
 * KEY INSIGHT
 *   10 leaves remainder 1 when divided by 3, so every power of 10 does too:
 *   100 = 99 + 1, 1000 = 999 + 1, and so on. A number d_k*10^k + ... + d_0 therefore has the
 *   same remainder mod 3 as d_k + ... + d_0. Because the digit sum does not depend on the
 *   order of the digits, EVERY arrangement has the same remainder - so one sum answers the
 *   question for all n! orderings. The same argument gives the rule for 9 (10 % 9 == 1) and,
 *   with alternating signs, the rule for 11 (10 % 11 == -1).
 *
 * COMPLEXITY
 *   Time  O(n)   one pass to add the digits
 *   Space O(1)   a single running sum
 *
 * INTERVIEW FOLLOW-UPS
 *   - Largest multiple of 3 you can build from the digits (LeetCode 1363): sort descending,
 *     then drop one digit with remainder r, or two with remainder 3 - r.
 *   - Why does the same trick work for 9 but not for 7? Because 10 % 9 == 1 while 10 % 7 == 3.
 *   - Divisibility by 11: alternate + and - on the digits, because 10 % 11 == -1.
 *   - What if the digits may form a number with leading zeros - is that still valid input?
 *
 * RUN
 *   main() runs 5 cases: two typical, the single-digit edges, and a near-miss sum.
 */

class DivisibleByThree {

    /**
     * True when some ordering of all the digits is divisible by 3.
     * Only the sum matters, because reordering digits never changes their sum.
     */
    public static boolean canFormDivisibleBy3(int[] digits) {
        int sum = 0;
        for (int digit : digits) sum += digit;
        return sum % 3 == 0;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + " -> actual " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1: [1, 4, 1, 6] sum 12", canFormDivisibleBy3(new int[]{1, 4, 1, 6}), true);
        print("case 2: [1, 4, 5]    sum 10", canFormDivisibleBy3(new int[]{1, 4, 5}), false);
        print("case 3: [0]          sum 0 ", canFormDivisibleBy3(new int[]{0}), true);
        print("case 4: [7]          sum 7 ", canFormDivisibleBy3(new int[]{7}), false);
        print("case 5: [9, 9, 9, 1] sum 28", canFormDivisibleBy3(new int[]{9, 9, 9, 1}), false);
    }
}

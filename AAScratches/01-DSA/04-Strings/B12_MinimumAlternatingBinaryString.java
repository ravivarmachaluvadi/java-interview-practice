/*
 * =====================================================================
 *  Minimum Changes To Make Alternating Binary String   LeetCode 1758 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a string of '0' and '1', in one operation you may flip any single
 *   character. Return the minimum number of flips so that no two adjacent
 *   characters are equal (the string alternates).
 *
 * EXAMPLE
 *   "0100"        ->  1    flip the last char: "0101"
 *   "10"          ->  0    already alternating
 *   "1111"        ->  2    "1010" or "0101" both need two flips
 *   "0001010111"  ->  2
 *   "0"           ->  0    single char is trivially alternating
 *   ""            ->  0    nothing to flip
 *
 * APPROACH  (two hypotheses counted in one pass)
 *   1. There are only two possible alternating targets of length n:
 *      "0101..." (starts with 0) and "1010..." (starts with 1).
 *   2. Walk the string once. At even index i the first target wants '0' and
 *      the second wants '1'; at odd index the reverse.
 *   3. cost0 counts mismatches against target "0101...", cost1 against
 *      "1010...". Return the smaller one.
 *
 * KEY INSIGHT
 *   When the answer must be one of a tiny, fixed set of candidates, score all
 *   candidates in the same pass rather than looping once per candidate.
 *   Bonus: cost1 == n - cost0, so you could count one and derive the other.
 *
 * COMPLEXITY
 *   Time  O(n)  one scan
 *   Space O(1)  two counters
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is cost1 = n - cost0? Every position mismatches exactly one target.
 *   - Alternating with an arbitrary period k (e.g. "012012")? Score k targets.
 *   - Circular string of even length where flips are limited to a window (LC 1888
 *     "Minimum Number of Flips to Make the Binary String Alternating") needs a
 *     sliding window over s + s.
 *
 * RUN
 *   main() runs 6 cases (typical, edge, tricky) and prints actual vs expected.
 */
class MinimumAlternatingBinaryString {

    public static int minOperations(String s) {
        int cost0 = 0; // flips needed to reach "0101..."
        int cost1 = 0; // flips needed to reach "1010..."

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            char wantForPattern0 = (i % 2 == 0) ? '0' : '1';
            char wantForPattern1 = (i % 2 == 0) ? '1' : '0';

            if (c != wantForPattern0) cost0++;
            if (c != wantForPattern1) cost1++;
        }
        return Math.min(cost0, cost1);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 \"0100\"", minOperations("0100"), 1);
        print("case 2 \"10\"", minOperations("10"), 0);
        print("case 3 \"1111\"", minOperations("1111"), 2);
        print("case 4 \"0001010111\"", minOperations("0001010111"), 2);
        print("case 5 \"0\" (single)", minOperations("0"), 0);
        print("case 6 \"\" (empty)", minOperations(""), 0);
    }
}

/*
 * =====================================================================
 *  Maximum Swap                                     LeetCode 670 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a non-negative integer num, you may swap two of its digits at most once.
 *   Return the largest value you can obtain. Doing nothing is allowed, so the answer
 *   is never smaller than num. num fits in an int (0 <= num <= 10^8).
 *
 * EXAMPLE
 *   num = 2736  ->  7236    swap the 2 and the 7
 *   num = 9973  ->  9973    already descending, no swap helps
 *   num = 1993  ->  9913    swap the leading 1 with the LAST 9, not the first 9
 *   num = 0     ->  0       single digit, nothing to swap
 *
 * APPROACH  (last-occurrence table on digits)
 *   1. Turn the number into a char[] of digits.
 *   2. Precompute last[d] = the rightmost index where digit d appears (0..9).
 *      Writing into last[] in increasing i order leaves the LAST index there.
 *   3. Scan i from left to right. At each i try digits d = 9 down to digits[i]+1.
 *      The first d with last[d] > i is the best digit we can pull into position i.
 *   4. Swap digits[i] with digits[last[d]], parse and return immediately.
 *      Only one swap is allowed, and the leftmost improvable slot is worth the most.
 *   5. If the scan ends with no swap made, the digits were already non-increasing.
 *
 * KEY INSIGHT
 *   Two greedy choices stack. Position first: fixing the leftmost digit that can be
 *   raised beats any gain further right, because place value dominates. Then value:
 *   take the biggest available digit, and among equal digits take the RIGHTMOST copy,
 *   since moving the small digit further right costs less. That is why last[] and not
 *   first[]. Pattern to recognise: "one swap / one edit to maximise" means scan left
 *   to right and commit at the first position that can improve.
 *
 * COMPLEXITY
 *   Time  O(n * 10) = O(n)  n digits, the inner loop is bounded by 10 digit values
 *   Space O(n)              the digit array plus a fixed 10-slot table
 *
 * INTERVIEW FOLLOW-UPS
 *   - What if you may swap at most k times? (greedy fails; needs recursion/DP)
 *   - Minimise the number instead, with no leading zero allowed.
 *   - Why does taking the leftmost occurrence of the max digit give a wrong answer?
 *   - Handle a number too large for int, given as a String.
 *
 * RUN
 *   main() runs 4 cases: typical, already-maximal, duplicate-digit trap, single digit.
 */
class MaximumSwap {

    /**
     * Scan left to right; at the first index that can be raised, pull in the largest
     * digit available to its right and stop. One swap is all we get.
     */
    public int maximumSwap(int num) {
        char[] digits = Integer.toString(num).toCharArray();

        // last[d] = rightmost index holding digit d; -1 means "digit d never appears"
        int[] last = new int[10];
        java.util.Arrays.fill(last, -1);
        for (int i = 0; i < digits.length; i++) {
            last[digits[i] - '0'] = i;
        }

        for (int i = 0; i < digits.length; i++) {
            int here = digits[i] - '0';
            // only a strictly bigger digit improves this position
            for (int d = 9; d > here; d--) {
                if (last[d] > i) {           // that bigger digit sits to the RIGHT of i
                    char smaller = digits[i];
                    digits[i] = digits[last[d]];
                    digits[last[d]] = smaller;
                    return Integer.parseInt(new String(digits));
                }
            }
        }
        return num;                          // digits were already non-increasing
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        MaximumSwap solution = new MaximumSwap();

        print("case 1 (typical)        num=2736", solution.maximumSwap(2736), 7236);
        print("case 2 (no swap helps)  num=9973", solution.maximumSwap(9973), 9973);
        print("case 3 (rightmost 9)    num=1993", solution.maximumSwap(1993), 9913);
        print("case 4 (single digit)   num=0   ", solution.maximumSwap(0), 0);
    }
}

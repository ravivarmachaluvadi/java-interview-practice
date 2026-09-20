/*
 * =====================================================================
 *  Non-Constructible Change                       AlgoExpert | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of positive coin values, return the smallest amount of change that
 *   CANNOT be made from any subset of those coins. Coins may repeat and each coin is
 *   used at most once. With no coins at all the answer is 1.
 *
 * EXAMPLE
 *   coins = [1, 2, 5]        ->  4    1, 2, 3, 5, 6, 7, 8 are makeable; 4 is not
 *   coins = [1, 1, 1, 1, 1]  ->  6    every amount 1..5 is makeable, 6 is one coin short
 *   coins = [1, 1, 3, 4]     ->  10   sum is 9 and nothing in 1..9 is missing
 *   coins = [1, 2, 2, 5]     ->  11   sum is 10 and nothing in 1..10 is missing
 *   coins = [5]              ->  1    the smallest coin already skips past 1
 *   coins = []               ->  1    nothing can be built at all
 *
 * APPROACH  (sorted prefix reachability invariant)
 *   1. Sort the coins ascending.
 *   2. Keep currentChange = the sum of the coins seen so far. Invariant: every amount
 *      from 1 to currentChange is constructible from those coins.
 *   3. For the next coin c: if c > currentChange + 1 there is a gap - nothing can reach
 *      currentChange + 1, because c and every later coin are already too large. Return it.
 *   4. Otherwise c <= currentChange + 1, so adding c extends the reachable run without
 *      leaving a hole. Set currentChange += c and continue.
 *   5. If the loop ends with no gap, the answer is currentChange + 1 (one past the sum).
 *
 * KEY INSIGHT
 *   Track a contiguous reachable range [1 .. currentChange] rather than the set of
 *   achievable sums. A sorted coin c either extends that range (c <= currentChange + 1)
 *   or breaks it forever. Pattern to recognise: "smallest unreachable value" questions
 *   become a single running frontier once the input is sorted - the same frontier idea
 *   that Jump Game applies to array indices.
 *
 * COMPLEXITY
 *   Time  O(n log n)  the sort dominates; the scan itself is one O(n) pass
 *   Space O(1)        besides the sort's own working space, only two counters
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why does sorting matter? Show a case where an unsorted scan answers wrongly.
 *   - Return the smallest amount needing more than k coins instead of any subset.
 *   - If each coin may be used unlimited times, what changes? (Answer: only the min coin.)
 *   - Given a target, add the fewest coins so every amount up to it is constructible.
 *
 * RUN
 *   main() runs 6 cases: the classic gap, all-ones, two no-gap arrays, a single large
 *   coin, and the empty array. Each prints actual vs expected.
 *
 * Fixed: the original expected-output comments for [1, 1, 3, 4] and [1, 2, 2, 5] were
 * swapped and wrong (11 and 10); the true answers are 10 and 11. The code was correct.
 */

import java.util.Arrays;

class NonConstructibleChange {

    public static int nonConstructibleChange(int[] coins) {
        Arrays.sort(coins);

        // Invariant: every amount in 1..currentChange can be built from the coins seen so far.
        int currentChange = 0;

        for (int coin : coins) {
            // A coin bigger than currentChange + 1 leaves a hole no later (larger) coin can fill.
            if (coin > currentChange + 1) {
                return currentChange + 1;
            }
            currentChange += coin;
        }

        // No hole anywhere, so the first unreachable amount is one past the total.
        return currentChange + 1;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 gap at 4", nonConstructibleChange(new int[]{1, 2, 5}), 4);
        print("case 2 all ones", nonConstructibleChange(new int[]{1, 1, 1, 1, 1}), 6);
        print("case 3 no gap", nonConstructibleChange(new int[]{1, 1, 3, 4}), 10);
        print("case 4 no gap", nonConstructibleChange(new int[]{1, 2, 2, 5}), 11);
        print("case 5 no coin 1", nonConstructibleChange(new int[]{5}), 1);
        print("case 6 empty", nonConstructibleChange(new int[]{}), 1);
    }
}

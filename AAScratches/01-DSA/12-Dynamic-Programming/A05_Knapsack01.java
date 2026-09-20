/*
 * =====================================================================
 *  0/1 Knapsack                            Classic | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   You are given n items, item i having weight wt[i] and value val[i], and a
 *   bag of capacity W. Each item may be taken at most once (that is the "0/1").
 *   Return the maximum total value whose total weight is at most W.
 *   Weights and values are non-negative; items cannot be split.
 *
 * EXAMPLE
 *   wt = [1, 2, 4, 5], val = [5, 4, 8, 6], W = 5  ->  13   take items 0 and 2
 *                                                         (weight 1 + 4 = 5,
 *                                                          value 5 + 8 = 13)
 *   wt = [3, 4, 5], val = [30, 50, 60], W = 8     ->  90   take 3 and 5;
 *                                                         greedy by value/weight takes 4 first and
 *                                                         gets 80
 *   W = 0                                         ->  0    edge case in main()
 *
 * APPROACH  (pick / not-pick on the state (index, capacity))
 *   1. State is (ind, W): the best value obtainable from items 0..ind with W
 *      capacity left. The answer is the state (n - 1, W).
 *   2. Base case: ind < 0 (no items left) or W == 0 (no room left) -> value 0.
 *   3. NOT-TAKEN: skip item ind, recurse on (ind - 1, W).
 *   4. TAKEN: only when wt[ind] <= W, earn val[ind] and recurse on
 *      (ind - 1, W - wt[ind]). The index always moves on, which is what forbids
 *      reusing an item.
 *   5. Return max(notTaken, taken).
 *
 * KEY INSIGHT
 *   Greedy by value-per-weight is wrong for 0/1 knapsack because a locally
 *   dense item can block a better pair - the second example above is exactly
 *   that trap, and it is the reason the problem needs DP at all. The fix is to
 *   try both branches and let the state (index, capacity) absorb the history:
 *   two different sets of earlier choices that leave the same capacity are
 *   interchangeable from here on, so the future is a function of (ind, W) and
 *   nothing else. Own this template and subset sum, count-subsequences, target
 *   sum and partition problems are all re-labellings of it - only the combine
 *   step (max vs sum vs OR) changes.
 *
 * COMPLEXITY
 *   Time  O(2^n) as written - both branches are explored at every index.
 *   Space O(n)   recursion stack depth.
 *   Memoised on (ind, W): O(n * W) time and space. That is pseudo-polynomial,
 *   since W is a value, not an input length - knapsack is still NP-hard.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Memoise (ind, W), then bottom-up dp[n][W+1], then one rolling row of
 *     size W+1 iterated from W down to 0 so an item is not reused.
 *   - Unbounded knapsack (item reusable): stay at the same index on a take.
 *   - Report WHICH items were chosen: keep parent choices, or walk the table
 *     backwards comparing dp[i][w] with dp[i-1][w].
 *   - Fractional knapsack: greedy by value/weight IS optimal there - be ready
 *     to say why the two problems differ.
 *
 * RUN
 *   main() runs 4 cases (typical, the greedy trap, an item heavier than the
 *   bag, and capacity 0) and prints actual vs expected.
 */

class knapsack01 {

    /** Best value obtainable from items 0..ind with capacity W left. */
    private int func(int[] wt, int[] val, int ind, int W) {
        if (ind < 0 || W == 0)
            return 0;               // no items left, or no room left

        int notTaken = func(wt, val, ind - 1, W);

        // Taking is only legal when the item still fits in the remaining capacity.
        int taken = 0;
        if (wt[ind] <= W)
            taken = val[ind] + func(wt, val, ind - 1, W - wt[ind]);

        return Math.max(notTaken, taken);
    }

    public int knapsack01(int[] wt, int[] val, int n, int W) {
        return func(wt, val, n - 1, W);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        knapsack01 sol = new knapsack01();

        int[] wt = {1, 2, 4, 5};
        int[] val = {5, 4, 8, 6};
        print("case 1 W = 5          ", sol.knapsack01(wt, val, wt.length, 5), 13);

        // Greedy by value/weight picks item 1 (ratio 12.5) first and ends at 80.
        int[] wt2 = {3, 4, 5};
        int[] val2 = {30, 50, 60};
        print("case 2 greedy trap    ", sol.knapsack01(wt2, val2, wt2.length, 8), 90);

        // Edge: the only item does not fit, so nothing can be taken.
        print("case 3 item too heavy ",
                sol.knapsack01(new int[]{10}, new int[]{100}, 1, 5), 0);

        // Edge: a bag with no capacity is worth nothing.
        print("case 4 W = 0          ", sol.knapsack01(wt, val, wt.length, 0), 0);
    }
}

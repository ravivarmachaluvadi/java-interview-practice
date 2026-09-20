/*
 * =====================================================================
 *  Rod Cutting Problem                                   Classic DP | Medium
 * =====================================================================
 *
 * PROBLEM
 *   A rod of length N can be cut into integer-length pieces. price[i] is what a piece of
 *   length i+1 sells for, so price has exactly N entries. Return the maximum total revenue
 *   obtainable. A piece length may be cut out any number of times (supply is unlimited).
 *
 * EXAMPLE
 *   price = [1, 5, 8, 9, 10, 17, 17, 20], n = 8  ->  22   because 2 + 6 sells for 5 + 17
 *   price = [2, 4, 6, 8], n = 4                  ->  8    every split ties at 8
 *   price = [5], n = 1                           ->  5    edge case: one unit of rod
 *
 * APPROACH  (unbounded knapsack: take / not-take, with reuse)
 *   1. State is (ind, len): piece lengths 1..ind+1 are available, len of rod is still uncut.
 *   2. NOT TAKE  -> move to ind - 1 with the same len; this piece length is never used again.
 *   3. TAKE      -> only if (ind + 1) <= len. Add price[ind] and recurse on the SAME ind with
 *      len - (ind + 1). Staying on ind is the whole difference from 0/1 knapsack.
 *   4. Base case ind == 0: only unit pieces remain, so revenue is price[0] * len.
 *   5. Cache every (ind, len) in dp, so each state is computed once.
 *   bruteForce() is the same search with no cache, kept to show what memoisation buys.
 *
 * KEY INSIGHT
 *   0/1 knapsack and unbounded knapsack share one skeleton; the only edit is the index in the
 *   TAKE branch. Move to ind - 1 and each item is used at most once; stay on ind and it can be
 *   reused. Recognise "pieces/coins may repeat" and reach for exactly this template.
 *
 * COMPLEXITY
 *   Time  O(N^2)   N piece lengths x N rod lengths, each state solved once
 *   Space O(N^2)   the dp table, plus O(N) recursion stack
 *   bruteForce is exponential: it re-derives the same (ind, len) on every path.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Print the actual cut list, not just the revenue (store the winning choice per state).
 *   - Convert to bottom-up tabulation, then to a single 1-D array of size n + 1.
 *   - Coin Change / Coin Change II are this recurrence with min and sum in place of max.
 *   - Add a fixed cost per cut, or cap how many times each length may be used.
 *
 * RUN
 *   main() runs 4 cases (classic CLRS, all-ties, unit-price-heavy, single unit) and prints
 *   both approaches against the expected revenue.
 */

import java.util.Arrays;

class RodCuttingProblem {

    // ---------------------------------------------------------------------------------------
    // Approach 1: brute force recursion, no caching. Shown only as the "before" picture.
    // ---------------------------------------------------------------------------------------
    private int maxProfit = -1;

    public int bruteForce(int[] price, int n) {
        maxProfit = -1;
        bruteForceProfit(price, 0, n, 1); // start by considering piece length 1
        return maxProfit;
    }

    /**
     * @param profit revenue collected so far on this path
     * @param n      rod length still left to cut
     * @param index  current piece length being considered (1-based, so price[index - 1])
     */
    private void bruteForceProfit(int[] price, int profit, int n, int index) {
        if (n == 0) {                       // rod fully used: this path is a complete cut
            maxProfit = Math.max(maxProfit, profit);
            return;
        }
        if (index > price.length || n < 0)  // ran out of piece lengths, or over-cut
            return;

        // choice 1: skip this piece length for good, move to the next one
        bruteForceProfit(price, profit, n, index + 1);

        // choice 2: cut a piece of this length (if it fits) and stay on the same index -> reusable
        if (index <= n)
            bruteForceProfit(price, profit + price[index - 1], n - index, index);
    }

    // ---------------------------------------------------------------------------------------
    // Approach 2: same take / not-take recursion with memoization (unbounded knapsack pattern)
    // ---------------------------------------------------------------------------------------
    public int memoization(int[] price, int n) {
        // Guard: memo() indexes price[0], so an empty rod has no state to solve.
        if (n == 0 || price.length == 0) return 0;

        // dp[ind][len] = best revenue using piece lengths 1..ind+1 on a rod of length len;
        // -1 marks an uncalculated state.
        int[][] dp = new int[price.length][n + 1];
        for (int[] row : dp)
            Arrays.fill(row, -1);

        return memo(price.length - 1, n, price, dp);
    }

    private int memo(int ind, int n, int[] price, int[][] dp) {
        // base case: only length-1 pieces available -> cut the whole rod into n unit pieces
        if (ind == 0) return price[0] * n;

        if (dp[ind][n] != -1) return dp[ind][n];

        int notTaken = memo(ind - 1, n, price, dp);

        int pieceLength = ind + 1;         // piece length represented by this index
        int taken = Integer.MIN_VALUE;     // impossible unless the piece fits
        if (pieceLength <= n)
            // stay on the same ind: the same piece length may be cut again (unbounded)
            taken = price[ind] + memo(ind, n - pieceLength, price, dp);

        dp[ind][n] = Math.max(notTaken, taken);
        return dp[ind][n];
    }

    // ---------------------------------------------------------------------------------------
    private static void print(String label, int actual, int expected) {
        System.out.println(label + " -> " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] prices = {
                {2, 4, 6, 8},                  // every split ties
                {1, 5, 8, 9, 10, 17, 17, 20},  // classic CLRS example: 2 + 6
                {3, 5, 8, 9, 10, 17, 17, 20},  // unit pieces dominate
                {5}                            // edge case: rod of length 1
        };
        int[] expected = {8, 22, 24, 5};

        RodCuttingProblem sol = new RodCuttingProblem();
        for (int c = 0; c < prices.length; c++) {
            int[] price = prices[c];
            int n = price.length;
            System.out.println("case " + (c + 1) + ": price = " + Arrays.toString(price) + ", n = " + n);
            print("  bruteForce ", sol.bruteForce(price, n), expected[c]);
            print("  memoization", sol.memoization(price, n), expected[c]);
        }
    }
}

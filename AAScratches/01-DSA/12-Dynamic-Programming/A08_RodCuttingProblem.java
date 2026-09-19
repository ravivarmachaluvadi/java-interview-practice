import java.util.Arrays;

/**
 * Problem: Given a rod of length N and price[] where price[i] is the selling price of a piece of
 * length i+1, find the maximum revenue obtainable by cutting the rod into integer-length pieces.
 * (Each piece length may be used any number of times - this is unbounded knapsack in disguise.)
 *
 * Approaches:
 * 1) bruteForce   - exhaustive recursion: at each length either skip it or cut it (and stay on the
 *                   same length so it can be reused); track the best profit in a field. O(2^N) time.
 * 2) memoization  - take / not-take recursion on (index, remainingLength) cached in a 2-D dp table.
 *                   O(N * N) states, each O(1) -> O(N^2) time, O(N^2) space + O(N) stack.
 */
class RodCuttingProblem {

    // ---------------------------------------------------------------------------------------------
    // Approach 1: brute force recursion (no caching)
    // ---------------------------------------------------------------------------------------------
    int maxProfit = -1;

    public int bruteForce(int[] price, int n) {
        maxProfit = -1;
        bruteForceProfit(price, 0, n, 1); // start with piece length 1
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

        // choice 1: skip this piece length, move to the next one
        bruteForceProfit(price, profit, n, index + 1);

        // choice 2: cut a piece of this length (if it fits) and stay on the same index -> reusable
        if (index <= n)
            bruteForceProfit(price, profit + price[index - 1], n - index, index);
    }

    // ---------------------------------------------------------------------------------------------
    // Approach 2: take / not-take recursion with memoization (unbounded knapsack pattern)
    // ---------------------------------------------------------------------------------------------
    public int memoization(int[] price, int n) {
        // dp[ind][len] = best revenue using piece lengths 1..ind+1 for a rod of length len;
        // -1 marks an uncalculated state
        int[][] dp = new int[n][n + 1];
        for (int[] row : dp)
            Arrays.fill(row, -1);

        return memo(n - 1, n, price, dp);
    }

    private int memo(int ind, int n, int[] price, int[][] dp) {
        // base case: only length-1 pieces available -> cut the whole rod into n unit pieces
        if (ind == 0) return price[0] * n;

        if (dp[ind][n] != -1) return dp[ind][n];

        int notTaken = memo(ind - 1, n, price, dp);

        int rodLength = ind + 1;           // piece length represented by this index
        int taken = Integer.MIN_VALUE;     // impossible unless the piece fits
        if (rodLength <= n)
            // stay on the same ind: the same piece length may be cut again (unbounded)
            taken = price[ind] + memo(ind, n - rodLength, price, dp);

        dp[ind][n] = Math.max(notTaken, taken);
        return dp[ind][n];
    }

    // ---------------------------------------------------------------------------------------------
    public static void main(String[] args) {
        int[][] prices = {
                {2, 4, 6, 8},                  // expected 8  (four length-1 pieces, or any combo)
                {1, 5, 8, 9, 10, 17, 17, 20},  // classic CLRS example, expected 22 (2 + 6)
                {3, 5, 8, 9, 10, 17, 17, 20}   // expected 24 (eight length-1 pieces)
        };

        RodCuttingProblem sol = new RodCuttingProblem();
        for (int[] price : prices) {
            int n = price.length;
            System.out.println("price = " + Arrays.toString(price) + ", n = " + n);
            System.out.println("  bruteForce  : " + sol.bruteForce(price, n));
            System.out.println("  memoization : " + sol.memoization(price, n));
        }
    }
}

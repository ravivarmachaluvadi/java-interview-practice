/*
 * =====================================================================
 *  Coin Change (fewest coins)         LeetCode 322 | Medium | MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given coin denominations and an amount, return the smallest number of coins that
 *   adds up to exactly that amount. Every denomination has unlimited supply, so a coin
 *   may be used many times. Return -1 when the amount cannot be formed at all.
 *
 * EXAMPLE
 *   coins = [1,2,5],  amount = 11  ->  3    because 5 + 5 + 1
 *   coins = [1,2,3],  amount = 7   ->  3    because 3 + 3 + 1 (or 3 + 2 + 2)
 *   coins = [2],      amount = 3   ->  -1   every total made from 2s is even
 *   coins = [1,2,5],  amount = 0   ->  0    the empty selection already works
 *
 * EXAMPLE OF WHY GREEDY FAILS
 *   coins = [1,3,4],  amount = 6   ->  2    (3 + 3); greedy takes 4 + 1 + 1 = 3 coins
 *
 * APPROACH  (unbounded knapsack over (index, remaining amount), minimising)
 *   1. State: f(ind, T) = fewest coins from coins[0..ind] that make exactly T.
 *   2. Transition - the only difference from 0/1 knapsack is where "take" recurses to:
 *        notTaken = f(ind - 1, T)                 // give up on this denomination
 *        taken    = 1 + f(ind, T - coins[ind])    // use it and STAY at ind (unlimited)
 *        f(ind, T) = min(notTaken, taken)
 *   3. Base case at ind == 0: only coins[0] is left, so T is reachable exactly when it is
 *      a multiple of coins[0], costing T / coins[0] coins; otherwise it is unreachable.
 *   4. Unreachable is modelled as the sentinel INF = 1e9 rather than -1, so that min()
 *      keeps working without special cases. Adding 1 to it cannot overflow an int.
 *   5. The caller converts any answer still >= INF back into -1.
 *
 * KEY INSIGHT
 *   Staying at the same index after taking an item is what makes a knapsack unbounded -
 *   that single character (ind instead of ind - 1) separates this from 0/1 knapsack and
 *   from rod cutting it separates nothing at all, they are the same recursion. The second
 *   habit worth stealing is the infinity sentinel: never mix "impossible" (-1) into a
 *   min(), give it a value that loses every comparison instead.
 *
 * COMPLEXITY
 *   minimumCoinsBruteForce : Time exponential in amount, Space O(amount) stack
 *   minimumCoins (memoised): Time O(n * amount), Space O(n * amount) + O(amount) stack
 *
 * INTERVIEW FOLLOW-UPS
 *   - Coin Change II (LeetCode 518): count the ways instead of minimising - same
 *     recursion with sum instead of min (see C10 in this folder).
 *   - Return the actual coins used, not just how many (store the winning choice).
 *   - Why is greedy wrong here but right for real currency systems?
 *   - Space-optimise the table to two rows, then to one.
 *
 * RUN
 *   main() runs 4 cases (typical, greedy trap, unreachable, zero amount) through both the
 *   brute-force and the memoised version and prints actual vs expected.
 */

import java.util.Arrays;

class CoinChangeMinimum {

    /** Loses every min() comparison, so it marks "this amount cannot be formed". */
    private static final int INF = (int) 1e9;

    /** Plain unbounded-knapsack recursion, no memo: correct but exponential. */
    private int fewestBrute(int[] coins, int ind, int amount) {
        if (ind == 0) {
            // Only coins[0] left: reachable iff amount is an exact multiple of it.
            return (amount % coins[0] == 0) ? amount / coins[0] : INF;
        }

        int notTaken = fewestBrute(coins, ind - 1, amount);

        int taken = INF;
        if (coins[ind] <= amount) {
            // Stay on ind: this denomination may be used again.
            taken = 1 + fewestBrute(coins, ind, amount - coins[ind]);
        }
        return Math.min(notTaken, taken);
    }

    /** Same recursion, memoised on (index, remaining amount); -1 means "not computed". */
    private int fewestMemo(int[] coins, int ind, int amount, int[][] dp) {
        if (ind == 0) {
            return (amount % coins[0] == 0) ? amount / coins[0] : INF;
        }
        if (dp[ind][amount] != -1) return dp[ind][amount];

        int notTaken = fewestMemo(coins, ind - 1, amount, dp);

        int taken = INF;
        if (coins[ind] <= amount) {
            taken = 1 + fewestMemo(coins, ind, amount - coins[ind], dp);
        }
        return dp[ind][amount] = Math.min(notTaken, taken);
    }

    /** Exponential reference implementation - keep the inputs tiny. */
    public int minimumCoinsBruteForce(int[] coins, int amount) {
        int answer = fewestBrute(coins, coins.length - 1, amount);
        return (answer >= INF) ? -1 : answer;   // sentinel survived -> unreachable
    }

    /** The version to write in an interview: O(n * amount) time and space. */
    public int minimumCoins(int[] coins, int amount) {
        int[][] dp = new int[coins.length][amount + 1];
        for (int[] row : dp) {
            Arrays.fill(row, -1);
        }
        int answer = fewestMemo(coins, coins.length - 1, amount, dp);
        return (answer >= INF) ? -1 : answer;
    }

    private static void runCase(CoinChangeMinimum sol, String label,
                                int[] coins, int amount, int expected) {
        System.out.println(label + " coins=" + Arrays.toString(coins) + " amount=" + amount);
        System.out.println("  brute force: " + sol.minimumCoinsBruteForce(coins, amount)
                + "   expected " + expected);
        System.out.println("  memoised   : " + sol.minimumCoins(coins, amount)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        CoinChangeMinimum sol = new CoinChangeMinimum();

        // Case 1 - typical: 5 + 5 + 1
        runCase(sol, "case 1", new int[]{1, 2, 5}, 11, 3);

        // Case 2 - tricky: greedy would answer 3 (4 + 1 + 1), the DP finds 3 + 3
        runCase(sol, "case 2", new int[]{1, 3, 4}, 6, 2);

        // Case 3 - edge: nothing odd can be built from 2s, so the sentinel must reach -1
        runCase(sol, "case 3", new int[]{2}, 3, -1);

        // Case 4 - edge: amount 0 needs no coins at all
        runCase(sol, "case 4", new int[]{1, 2, 5}, 0, 0);
    }
}

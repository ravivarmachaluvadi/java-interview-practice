/*
 * =====================================================================
 *  Coin Change II (count the ways)               LeetCode 518 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given coin denominations with unlimited supply and an amount, count how many
 *   different combinations add up to exactly that amount. Order does NOT matter:
 *   2+1+1 and 1+2+1 are the SAME way. Return 0 when the amount cannot be formed;
 *   the amount alone counts as one way when it is 0 (pick nothing).
 *   This is NOT "fewest coins" (LeetCode 322) - that is C09 in this folder.
 *
 * EXAMPLE
 *   coins = [1,2,5], amount = 5  ->  4   (5 | 2+2+1 | 2+1+1+1 | 1+1+1+1+1)
 *   coins = [1,2,3], amount = 4  ->  4   (3+1 | 2+2 | 2+1+1 | 1+1+1+1)
 *   coins = [2],     amount = 3  ->  0   odd totals are unreachable with 2s
 *   coins = [1,2,5], amount = 0  ->  1   the empty combination
 *
 * APPROACH  (unbounded knapsack over (index, remaining target), summing)
 *   1. State: f(ind, T) = number of ways to make T using coins[0..ind].
 *   2. Transition:
 *        notTaken = f(ind - 1, T)                // stop using this denomination
 *        taken    = f(ind, T - coins[ind])       // use it and STAY at ind (unlimited)
 *        f(ind, T) = notTaken + taken            // sum, because we are COUNTING
 *   3. Base case at ind == 0: exactly one way if T is a multiple of coins[0], else none.
 *   4. The same recurrence is shown four ways, each derived from the one above:
 *        countWaysRecursive  - plain recursion, exponential
 *        countWaysMemo       - + long[n][T+1] memo, O(n*T) time and space
 *        countWaysTabulation - bottom-up 2D table, no recursion stack
 *        countWays1D         - bottom-up single row, O(T) space (write this one)
 *
 * KEY INSIGHT
 *   Compare this with C09 line by line: identical recursion, min() swapped for +. The
 *   part unique to counting is why order does not matter - the coin loop is OUTER, so a
 *   combination is built by finishing all decisions about coin 0, then coin 1, and so on,
 *   which fixes one canonical order per multiset. Swap the loops (amount outer, coins
 *   inner) and you count PERMUTATIONS instead: that is Combination Sum IV, LeetCode 377.
 *   The forward inner pass (T from coin upwards) is what lets a coin be reused, because
 *   dp[T - coin] has already been updated for this same coin.
 *
 * COMPLEXITY
 *   Time  O(n * amount)  for every version except the plain recursion (exponential)
 *   Space O(n * amount)  for memo and 2D tabulation, O(amount) for the 1D version
 *
 * INTERVIEW FOLLOW-UPS
 *   - Combination Sum IV (LeetCode 377): count permutations - which loop moves?
 *   - Fewest coins instead of ways (LeetCode 322) - what single operator changes?
 *   - Limit each coin to k copies: how does the recurrence grow?
 *   - Why does the answer overflow an int on adversarial inputs, and what does LeetCode
 *     guarantee about it? (here every method returns long so the four agree exactly)
 *
 * RUN
 *   main() runs 4 cases (typical, multi-coin, unreachable edge, zero amount) through all
 *   four implementations and prints actual vs expected.
 *
 *   Fixed: the recursive version used to reduce mod 1e9+7 while the other three did not,
 *   so on large inputs the four printed answers disagreed. All four now return long.
 */

import java.util.Arrays;

class CoinChangeII {

    // ---------- Approach 1: plain recursion ----------

    /** f(ind, T) = number of ways to make T using coins[0..ind]. */
    private static long recur(int[] coins, int ind, int T) {
        // Only coins[0] is left: one way if T is an exact multiple of it, otherwise none.
        if (ind == 0) {
            return (T % coins[0] == 0) ? 1 : 0;
        }

        long notTaken = recur(coins, ind - 1, T);          // skip this coin entirely

        long taken = 0;
        if (coins[ind] <= T) {
            taken = recur(coins, ind, T - coins[ind]);     // take it, STAY at ind
        }
        return notTaken + taken;
    }

    public static long countWaysRecursive(int[] coins, int amount) {
        return recur(coins, coins.length - 1, amount);
    }

    // ---------- Approach 2: recursion + memoisation ----------

    private static long memo(int[] coins, int ind, int T, long[][] dp) {
        if (ind == 0) {
            return (T % coins[0] == 0) ? 1 : 0;
        }
        if (dp[ind][T] != -1) return dp[ind][T];           // subproblem already solved

        long notTaken = memo(coins, ind - 1, T, dp);

        long taken = 0;
        if (coins[ind] <= T) {
            taken = memo(coins, ind, T - coins[ind], dp);
        }
        return dp[ind][T] = notTaken + taken;
    }

    public static long countWaysMemo(int[] coins, int amount) {
        long[][] dp = new long[coins.length][amount + 1];
        for (long[] row : dp) {
            Arrays.fill(row, -1);                          // -1 = not computed yet
        }
        return memo(coins, coins.length - 1, amount, dp);
    }

    // ---------- Approach 3: bottom-up 2D tabulation ----------

    public static long countWaysTabulation(int[] coins, int amount) {
        int n = coins.length;
        long[][] dp = new long[n][amount + 1];

        // Row 0 mirrors the recursive base case.
        for (int T = 0; T <= amount; T++) {
            if (T % coins[0] == 0) dp[0][T] = 1;
        }

        for (int ind = 1; ind < n; ind++) {
            for (int T = 0; T <= amount; T++) {
                long notTaken = dp[ind - 1][T];
                // dp[ind][...] on the right: the same coin may be taken again.
                long taken = (coins[ind] <= T) ? dp[ind][T - coins[ind]] : 0;
                dp[ind][T] = notTaken + taken;
            }
        }
        return dp[n - 1][amount];
    }

    // ---------- Approach 4: bottom-up 1D (space optimised) ----------

    public static long countWays1D(int[] coins, int amount) {
        long[] dp = new long[amount + 1];
        dp[0] = 1;                                         // one way to make 0: take nothing

        for (int coin : coins) {                           // coins OUTER -> combinations
            for (int T = coin; T <= amount; T++) {         // forward pass -> unlimited reuse
                dp[T] += dp[T - coin];
            }
        }
        // Swapping the loops (amount outer, coins inner) would count PERMUTATIONS instead
        // (that is Combination Sum IV, LeetCode 377).
        return dp[amount];
    }

    private static void runCase(String label, int[] coins, int amount, long expected) {
        System.out.println(label + " coins=" + Arrays.toString(coins) + " amount=" + amount);
        System.out.println("  recursive  : " + countWaysRecursive(coins, amount)
                + "   expected " + expected);
        System.out.println("  memo       : " + countWaysMemo(coins, amount)
                + "   expected " + expected);
        System.out.println("  tabulation : " + countWaysTabulation(coins, amount)
                + "   expected " + expected);
        System.out.println("  1D dp      : " + countWays1D(coins, amount)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1 - typical: 5 | 2+2+1 | 2+1+1+1 | 1+1+1+1+1
        runCase("case 1", new int[]{1, 2, 5}, 5, 4);

        // Case 2 - typical: 3+1 | 2+2 | 2+1+1 | 1+1+1+1 (note 1+3 is NOT a fifth way)
        runCase("case 2", new int[]{1, 2, 3}, 4, 4);

        // Case 3 - edge: an odd amount from a single even coin is impossible
        runCase("case 3", new int[]{2}, 3, 0);

        // Case 4 - edge: amount 0 has exactly one way, the empty combination
        runCase("case 4", new int[]{1, 2, 5}, 0, 1);
    }
}

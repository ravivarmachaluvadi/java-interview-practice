import java.util.Arrays;

/**
 * Problem: Coin Change II (LeetCode 518) - count the number of ways to make up
 * {@code amount} using unlimited copies of each coin. Order does not matter:
 * 2+1+1 and 1+2+1 are the SAME way (combinations, not permutations).
 * <p>
 * Example: amount = 5, coins = [1,2,5] -> 4 ways: 5 | 2+2+1 | 2+1+1+1 | 1+1+1+1+1
 * <p>
 * NOTE: this is NOT "fewest coins" (LeetCode 322) - that is C09_CoinChange.
 * The old C11 file carried the LeetCode 322 description but its code counted ways.
 * <p>
 * Approaches (all use the same take / not-take recurrence over index and target):
 * <ul>
 *   <li>countWaysRecursive   - plain recursion, exponential time, answer mod 1e9+7</li>
 *   <li>countWaysMemo        - same recursion + long[n][T+1] memo, O(n*T) time and space</li>
 *   <li>countWaysTabulation  - bottom-up 2D dp, O(n*T) time and space</li>
 *   <li>countWays1D          - bottom-up 1D dp (the interview-expected version), O(T) space</li>
 * </ul>
 * Why "order does not matter" works: we iterate coins in the OUTER loop, so a way
 * can only pick coin i after finishing all decisions on coins 0..i-1.
 */
class CoinChangeII {

    private static final int MOD = (int) 1e9 + 7;

    // ---------- Approach 1: plain recursion (from C10) ----------

    // f(ind, T) = number of ways to make T using coins[0..ind]
    private static int recur(int[] coins, int ind, int T) {
        // Base case: only coins[0] is left, T is makeable iff it is a multiple of coins[0]
        if (ind == 0)
            return (T % coins[0] == 0) ? 1 : 0;

        int notTaken = recur(coins, ind - 1, T);          // skip this coin entirely

        int taken = 0;
        if (coins[ind] <= T)
            taken = recur(coins, ind, T - coins[ind]);    // take it, STAY at ind (unlimited supply)

        return (notTaken + taken) % MOD;
    }

    public static int countWaysRecursive(int[] coins, int amount) {
        return recur(coins, coins.length - 1, amount);
    }

    // ---------- Approach 2: recursion + memoization (from C11) ----------

    private static long memo(int[] coins, int ind, int T, long[][] dp) {
        if (ind == 0)
            return (T % coins[0] == 0) ? 1 : 0;

        // Subproblem already solved -> reuse
        if (dp[ind][T] != -1)
            return dp[ind][T];

        long notTaken = memo(coins, ind - 1, T, dp);

        long taken = 0;
        if (coins[ind] <= T)
            taken = memo(coins, ind, T - coins[ind], dp);

        return dp[ind][T] = notTaken + taken;
    }

    public static long countWaysMemo(int[] coins, int amount) {
        long[][] dp = new long[coins.length][amount + 1];
        for (long[] row : dp)
            Arrays.fill(row, -1);                          // -1 = not computed yet
        return memo(coins, coins.length - 1, amount, dp);
    }

    // ---------- Approach 3: bottom-up 2D tabulation ----------

    public static long countWaysTabulation(int[] coins, int amount) {
        int n = coins.length;
        long[][] dp = new long[n][amount + 1];

        // Row 0 mirrors the recursive base case
        for (int T = 0; T <= amount; T++)
            if (T % coins[0] == 0) dp[0][T] = 1;

        for (int ind = 1; ind < n; ind++) {
            for (int T = 0; T <= amount; T++) {
                long notTaken = dp[ind - 1][T];
                long taken = (coins[ind] <= T) ? dp[ind][T - coins[ind]] : 0;
                dp[ind][T] = notTaken + taken;
            }
        }
        return dp[n - 1][amount];
    }

    // ---------- Approach 4: bottom-up 1D (space optimised) ----------

    public static long countWays1D(int[] coins, int amount) {
        long[] dp = new long[amount + 1];
        dp[0] = 1;                                          // one way to make 0: take nothing

        for (int coin : coins)                              // coins OUTER -> combinations
            for (int T = coin; T <= amount; T++)            // forward pass -> unlimited reuse
                dp[T] += dp[T - coin];

        // Swapping the loops (amount outer, coins inner) would count PERMUTATIONS instead
        // (that is "Combination Sum IV", LeetCode 377).
        return dp[amount];
    }

    public static void main(String[] args) {
        int[][] coinSets = {{1, 2, 3}, {1, 2, 5}, {2}};
        int[] amounts = {4, 5, 3};

        for (int i = 0; i < coinSets.length; i++) {
            int[] coins = coinSets[i];
            int amount = amounts[i];
            System.out.println("coins=" + Arrays.toString(coins) + " amount=" + amount);
            System.out.println("  recursive  : " + countWaysRecursive(coins, amount));
            System.out.println("  memo       : " + countWaysMemo(coins, amount));
            System.out.println("  tabulation : " + countWaysTabulation(coins, amount));
            System.out.println("  1D dp      : " + countWays1D(coins, amount));
        }
        // Expected: 4, 4, 0
    }
}

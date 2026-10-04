/*
 * =====================================================================
 *  P085 Unbounded Knapsack (Coin Change Family)   Canonical LC 322 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 322, Coin Change)
 *   Return the fewest coins (unlimited supply of each denomination) that make `amount`,
 *   or -1 if it cannot be made.
 *
 * EXAMPLE
 *   coins [1, 2, 5], amount 11  ->  3      5 + 5 + 1
 *   coins [2],       amount 3   ->  -1
 *   coins [1],       amount 0   ->  0
 *
 * RECOGNIZE WHEN
 *   - Items can be used ANY number of times: coins, rod pieces, perfect squares, words.
 *   - "fewest", "number of ways", "max value" to reach an exact total.
 *   - Watch the wording: "combinations" (order does not matter) vs "sequences / orderings"
 *     (order matters). The loop order is the whole difference.
 *   Not this if: each item at most once -> P084_ZeroOneKnapsack; you must list the
 *   combinations -> P078_CombinationSum.
 *
 * TEMPLATE
 *   dp[0] = base (0 coins / 1 way)
 *   COMBINATIONS (LC 518):   for coin in coins:            for a from coin UP to amount:
 *                                dp[a] += dp[a - coin]
 *   PERMUTATIONS (LC 377):   for a from 1 up to amount:    for coin in coins:
 *                                dp[a] += dp[a - coin]
 *   MIN COINS (LC 322):      either order; dp[a] = min(dp[a], dp[a - coin] + 1)
 *
 * APPROACH
 *   1. dp[a] = fewest coins for amount a; dp[0] = 0, the rest "infinity".
 *   2. For every amount and every coin, try ending with that coin: dp[a - coin] + 1.
 *
 * KEY INSIGHT
 *   Capacity iterated UPWARD lets dp[a - coin] already include this coin, which is what
 *   "unlimited copies" means. For counting, putting coins in the OUTER loop fixes the order
 *   in which coins are considered, so {1,2} and {2,1} are counted once (combinations);
 *   putting amounts outside counts every ordering (permutations).
 *
 * COMPLEXITY
 *   Time O(amount * number of coins), space O(amount).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 518  Coin Change II           count COMBINATIONS: coins outer, amounts inner
 *   [coded] LC 377  Combination Sum IV       count SEQUENCES: amounts outer, coins inner
 *   [coded] LC 279  Perfect Squares          coins = 1, 4, 9, ...; min count
 *   [coded] Rod Cutting (GfG)                piece of length L has price p[L]; max value
 *           LC 983  Min Cost for Tickets     dp over days with 1 / 7 / 30-day passes
 *           LC 1449 Largest Number at Cost   max digits first, then the largest digits
 *           LC 2585 Number of Ways to Earn   bounded counts: limit each type (multi-knapsack)
 *
 * PITFALLS
 *   - Using Integer.MAX_VALUE as infinity overflows on + 1; use amount + 1.
 *   - LC 518 vs LC 377: swapping the loops changes the answer ([1,2,3] to make 4 has 4
 *     combinations but 7 sequences).
 *   - dp[0] = 1 for counting: there is exactly one way to make 0 (use nothing).
 *
 * DEEP DIVE
 *   C08_CoinChangeMinimum, C09_CoinChangeII, A06_RodCuttingProblem (12-Dynamic-Programming)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;

class UnboundedKnapsack {

    // Canonical LC 322.
    static int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);               // "infinity" that cannot overflow
        dp[0] = 0;
        for (int a = 1; a <= amount; a++) {
            for (int c : coins) {
                if (c <= a) {
                    dp[a] = Math.min(dp[a], dp[a - c] + 1);
                }
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }

    // LC 518: number of combinations (order does not matter).
    static int change(int amount, int[] coins) {
        int[] ways = new int[amount + 1];
        ways[0] = 1;
        for (int c : coins) {                      // coins OUTSIDE
            for (int a = c; a <= amount; a++) {
                ways[a] += ways[a - c];
            }
        }
        return ways[amount];
    }

    // LC 377: number of ordered sequences.
    static int combinationSum4(int[] nums, int target) {
        int[] ways = new int[target + 1];
        ways[0] = 1;
        for (int a = 1; a <= target; a++) {        // amounts OUTSIDE
            for (int x : nums) {
                if (x <= a) {
                    ways[a] += ways[a - x];
                }
            }
        }
        return ways[target];
    }

    // LC 279.
    static int numSquares(int n) {
        int[] dp = new int[n + 1];
        Arrays.fill(dp, n + 1);
        dp[0] = 0;
        for (int a = 1; a <= n; a++) {
            for (int s = 1; s * s <= a; s++) {
                dp[a] = Math.min(dp[a], dp[a - s * s] + 1);
            }
        }
        return dp[n];
    }

    // Rod cutting: price[i] is the price of a piece of length i + 1.
    static int cutRod(int[] price, int n) {
        int[] best = new int[n + 1];
        for (int len = 1; len <= n; len++) {
            for (int piece = 1; piece <= len; piece++) {
                best[len] = Math.max(best[len], price[piece - 1] + best[len - piece]);
            }
        }
        return best[n];
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 322 [1,2,5] amount 11", coinChange(new int[]{1, 2, 5}, 11), 3);
        check("LC 322 [2] amount 3", coinChange(new int[]{2}, 3), -1);
        check("LC 322 amount 0", coinChange(new int[]{1}, 0), 0);
        check("LC 322 greedy fails", coinChange(new int[]{1, 3, 4}, 6), 2);

        check("LC 518 amount 5", change(5, new int[]{1, 2, 5}), 4);
        check("LC 518 amount 3 [2]", change(3, new int[]{2}), 0);
        check("LC 518 amount 10 [10]", change(10, new int[]{10}), 1);
        check("LC 518 amount 4 [1,2,3] combinations", change(4, new int[]{1, 2, 3}), 4);

        check("LC 377 [1,2,3] target 4 sequences", combinationSum4(new int[]{1, 2, 3}, 4), 7);
        check("LC 377 [9] target 3", combinationSum4(new int[]{9}, 3), 0);

        check("LC 279 n=12", numSquares(12), 3);
        check("LC 279 n=13", numSquares(13), 2);

        check("rod cutting n=8", cutRod(new int[]{1, 5, 8, 9, 10, 17, 17, 20}, 8), 22);
        check("rod cutting n=8 other prices", cutRod(new int[]{3, 5, 8, 9, 10, 17, 17, 20}, 8), 24);
    }
}

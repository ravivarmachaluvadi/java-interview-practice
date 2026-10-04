/*
 * =====================================================================
 *  P092 State-Machine DP (Stock Trading)   Canonical LC 309 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 309, Best Time to Buy and Sell Stock with Cooldown)
 *   Trade as many times as you like, one share at a time, but after selling you must wait
 *   one day before buying again. Return the maximum profit.
 *
 * EXAMPLE
 *   [1, 2, 3, 0, 2]  ->  3      buy, sell, cooldown, buy, sell
 *   [1]              ->  0
 *
 * RECOGNIZE WHEN
 *   - Each day you are in one of a few STATES (holding, not holding, cooling down, k
 *     transactions used) and the rules say which state can follow which.
 *   - Stock problems with fees, cooldowns or a cap on transactions.
 *   Not this if: one buy and one sell -> P004_KadaneBestEndingHere (LC 121); unlimited
 *   trades and no rule -> sum the rises (P097_RunningBalance).
 *
 * TEMPLATE
 *   one variable per state, updated together each day (use the OLD values on the right):
 *     hold' = max(hold, notHolding - price)          // keep, or buy today
 *     sold' = hold + price                            // sell today
 *     rest' = max(rest, sold)                         // do nothing / finish cooldown
 *   k transactions: buy[t], sell[t] for t = 1..k
 *
 * APPROACH
 *   1. hold = best profit while owning a share; sold = sold today (must cool down);
 *      rest = free to buy.
 *   2. Update all three from yesterday's values.
 *   3. Answer: max(sold, rest) at the end.
 *
 * KEY INSIGHT
 *   Draw the states as boxes and the allowed moves as arrows; each arrow is one term in a
 *   max(). That picture turns every stock variant (fee, cooldown, k trades) into a few
 *   lines, and because each day uses only the previous day, space is O(1).
 *
 * COMPLEXITY
 *   Time O(n) (O(n * k) for k transactions), space O(1) (O(k)).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 122  Unlimited Transactions   two states: hold, free
 *   [coded] LC 714  With Transaction Fee     pay the fee when selling
 *   [coded] LC 188  At Most k Transactions   buy[t] = max(buy[t], sell[t-1] - p),
 *                                            sell[t] = max(sell[t], buy[t] + p)
 *           LC 123  At Most 2 Transactions   LC 188 with k = 2
 *           LC 121  One Transaction          min price so far -> P004_KadaneBestEndingHere
 *           LC 2291 Max Profit Future Prices a 0/1 knapsack, not a state machine
 *
 * PITFALLS
 *   - Updating hold then using the NEW hold for sold mixes two days; keep the old values.
 *   - "hold" must start at -infinity (or -prices[0]), not 0.
 *   - LC 188 with k >= n / 2 is just LC 122; shortcut it to avoid a huge table.
 *
 * DEEP DIVE
 *   C01_BestTimeToBuyAndSellStockII (13-Greedy), B03_BestTimeToBuyAndSellStock (01-Arrays)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class StockStateMachine {

    // Canonical LC 309.
    static int maxProfitCooldown(int[] prices) {
        int hold = Integer.MIN_VALUE / 2;          // owning a share
        int sold = 0;                              // sold today, cooling down tomorrow
        int rest = 0;                              // free to buy
        for (int p : prices) {
            int prevHold = hold;
            int prevSold = sold;
            hold = Math.max(hold, rest - p);
            sold = prevHold + p;
            rest = Math.max(rest, prevSold);
        }
        return Math.max(sold, rest);
    }

    // LC 122.
    static int maxProfitUnlimited(int[] prices) {
        int hold = Integer.MIN_VALUE / 2;
        int free = 0;
        for (int p : prices) {
            int prevHold = hold;
            hold = Math.max(hold, free - p);
            free = Math.max(free, prevHold + p);
        }
        return free;
    }

    // LC 714.
    static int maxProfitFee(int[] prices, int fee) {
        int hold = Integer.MIN_VALUE / 2;
        int free = 0;
        for (int p : prices) {
            int prevHold = hold;
            hold = Math.max(hold, free - p);
            free = Math.max(free, prevHold + p - fee);
        }
        return free;
    }

    // LC 188: at most k buy-sell pairs.
    static int maxProfitK(int k, int[] prices) {
        int[] buy = new int[k + 1];
        int[] sell = new int[k + 1];
        java.util.Arrays.fill(buy, Integer.MIN_VALUE / 2);
        for (int p : prices) {
            for (int t = 1; t <= k; t++) {
                buy[t] = Math.max(buy[t], sell[t - 1] - p);    // t-th buy after t-1 sells
                sell[t] = Math.max(sell[t], buy[t] + p);
            }
        }
        return sell[k];
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 309 [1,2,3,0,2]", maxProfitCooldown(new int[]{1, 2, 3, 0, 2}), 3);
        check("LC 309 [1]", maxProfitCooldown(new int[]{1}), 0);
        check("LC 309 falling", maxProfitCooldown(new int[]{5, 4, 3}), 0);

        check("LC 122 [7,1,5,3,6,4]", maxProfitUnlimited(new int[]{7, 1, 5, 3, 6, 4}), 7);
        check("LC 122 [1,2,3,4,5]", maxProfitUnlimited(new int[]{1, 2, 3, 4, 5}), 4);

        check("LC 714 fee=2", maxProfitFee(new int[]{1, 3, 2, 8, 4, 9}, 2), 8);
        check("LC 714 fee=3", maxProfitFee(new int[]{1, 3, 7, 5, 10, 3}, 3), 6);

        check("LC 188 k=2 [2,4,1]", maxProfitK(2, new int[]{2, 4, 1}), 2);
        check("LC 188 k=2 [3,2,6,5,0,3]", maxProfitK(2, new int[]{3, 2, 6, 5, 0, 3}), 7);
        check("LC 123 as k=2", maxProfitK(2, new int[]{3, 3, 5, 0, 0, 3, 1, 4}), 6);
    }
}

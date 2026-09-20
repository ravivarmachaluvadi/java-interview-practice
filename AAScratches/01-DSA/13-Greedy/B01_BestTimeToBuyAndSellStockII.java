/*
 * =====================================================================
 *  Best Time to Buy and Sell Stock II          LeetCode 122 | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   prices[i] is the price of one share on day i. You may buy and sell as many times as
 *   you like, but you can hold at most one share at a time (you must sell before you buy
 *   again). Return the maximum total profit. Buying and selling on the same day is allowed
 *   and earns nothing, so doing nothing always yields 0 - profit is never negative.
 *
 * EXAMPLE
 *   [7, 1, 5, 3, 6, 4]  ->  7    buy 1 sell 5 (+4), buy 3 sell 6 (+3)
 *   [1, 2, 3, 4, 5]     ->  4    one long hold, or four daily trades - same total
 *   [7, 6, 4, 3, 1]     ->  0    never rises, so never buy (edge case in main)
 *   [5]                 ->  0    fewer than two days, no trade is possible
 *
 * APPROACH  (sum every positive day-over-day delta)
 *   1. If there are fewer than two prices, return 0.
 *   2. Walk i from 1 to n-1.
 *   3. Whenever prices[i] > prices[i-1], add that difference to the profit.
 *   4. Return the running total. No explicit buy/sell days are tracked.
 *
 * KEY INSIGHT
 *   Any hold from day b to day s earns prices[s] - prices[b], which telescopes into the
 *   sum of the daily deltas inside that window. So total profit is always some subset of
 *   the daily deltas - and since you are free to start and stop a hold on any day, you can
 *   pick exactly the positive ones and skip every negative one. That upper bound is
 *   therefore achievable, which is why the local greedy equals the global optimum.
 *   Pattern: when a value telescopes into independent steps and you may opt out of any
 *   step, keep the positive steps.
 *
 * COMPLEXITY
 *   Time  O(n)   one pass, one comparison per day
 *   Space O(1)   a single running total
 *
 * INTERVIEW FOLLOW-UPS
 *   - At most ONE transaction (LC 121): track the running minimum price instead.
 *   - At most k transactions (LC 188) or exactly two (LC 123): greedy dies, use DP over
 *     (day, transactions used, holding or not).
 *   - With a transaction fee (LC 714) or a cooldown day (LC 309): also DP - the fee makes
 *     chaining tiny gains lossy, so the delta trick no longer holds.
 *   - Return the actual buy/sell day pairs, not just the profit.
 *
 * RUN
 *   main() runs 4 cases (typical, monotonic up, monotonic down, single day) and prints
 *   actual vs expected.
 */

import java.util.Arrays;

class BestTimeToBuyAndSellStockII {

    public static int maxProfit(int[] prices) {
        if (prices == null || prices.length < 2) {
            return 0;                       // no pair of days, so no trade
        }
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];   // capture every upward move
            }
        }
        return profit;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] zigzag = {7, 1, 5, 3, 6, 4};
        int[] rising = {1, 2, 3, 4, 5};
        int[] falling = {7, 6, 4, 3, 1};
        int[] single = {5};

        print("case 1 " + Arrays.toString(zigzag), maxProfit(zigzag), 7);
        print("case 2 " + Arrays.toString(rising), maxProfit(rising), 4);
        print("case 3 " + Arrays.toString(falling), maxProfit(falling), 0);
        print("case 4 " + Arrays.toString(single), maxProfit(single), 0);
    }
}

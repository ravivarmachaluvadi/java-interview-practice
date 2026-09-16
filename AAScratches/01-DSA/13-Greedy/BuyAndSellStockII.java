/**
 * You are given an integer array prices where prices[i] is
 * <p>
 * the price of a given stock on the ith day.
 * <p>
 * On each day, you may decide to buy and/or sell the stock. You can
 * <p>
 * only hold at most one share of the stock at any time. However,
 * <p>
 * you can buy it then immediately sell it on the same day.
 * <p>
 * Find and return the maximum profit you can achieve.
 */
// https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/description/
class BuyAndSellStockII {
    public int maxProfit(int[] prices) {
        int maxProfit = 0;
        // Iterate through the price list
        for (int i = 1; i < prices.length; i++) {
            // If today's price is higher than yesterday's
            // price, we can make a profit
            if (prices[i] > prices[i - 1]) {
                maxProfit += prices[i] - prices[i - 1]; // Add the profit to maxProfit
            }
        }
        return maxProfit; // Return the maximum profit
    }
}

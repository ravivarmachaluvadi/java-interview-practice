// https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/description/
// 122. Best Time to Buy and Sell Stock II
class BestTimeToBuyAndSellStockII {

    public static int maxProfit(int[] prices) {
        if (prices == null || prices.length < 2) {
            return 0;
        }
        int profit = 0;
        // Accumulate profit for every upward move
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];
            }
        }
        return profit;
    }

    public static void main(String[] args) {
        // Example:
        int[] prices = {7, 1, 5, 3, 6, 4};
        // Explanation: 
        // Buy on day 2 (price = 1) and sell on day 3 (price = 5), profit = 4.
        // Then buy on day 4 (price = 3) and sell on day 5 (price = 6), profit = 3.
        // Total profit = 4 + 3 = 7.
        int result = maxProfit(prices);
        System.out.println("Max profit: " + result);  // Should print “Max profit: 7”
    }
}

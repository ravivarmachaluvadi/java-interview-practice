// https://leetcode.com/problems/best-time-to-buy-and-sell-stock
// 121. Best Time to Buy and Sell Stock

/**
 * Input: prices = [7,1,5,3,6,4]
 * <p>
 * Output: 5
 */
class BestTimeToBuyAndSellStock {
    public static int maxProfit(int[] prices) {
        int maxProfit = 0;
        int minSoFar = Integer.MAX_VALUE;
        for (int price : prices) {
            if (price < minSoFar) {
                minSoFar = price;
            } else {
                maxProfit = Math.max(maxProfit, price - minSoFar);
            }
        }
        return maxProfit;
    }

    static void main(String[] args) {
        System.out.println(maxProfit(new int[]{7, 1, 5, 3, 6, 4}));
    }
}

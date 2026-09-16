import java.util.Deque;
import java.util.LinkedList;

/**
 * Given stock prices:
 * <p>
 * [2, 1, 5, 90, 3, 10, 82, 67]
 * <p>
 * and a window size:
 * <p>
 * k = 3
 * <p>
 * For every day you sell, you are allowed to buy the stock only within the previous 3 days.
 * <p>
 * We want to find the maximum profit:
 * <p>
 * profit = selling price - minimum buying price in previous 3 days
 */
class MaxProfitWithWindowSize3 {

    public static void main(String[] args) {
        int[] stocks = {2, 1, 5, 90, 3, 10, 82, 67};

        System.out.println(maxProfitWithWindow(stocks)); // 89
    }

    private static int maxProfitWithWindow(int[] stocks) {
        int k = 3;

        if (stocks == null || stocks.length <= k) {
            return -1;
        }

        Deque<Integer> deque = new LinkedList<>();
        int maxProfit = Integer.MIN_VALUE;

        // First possible buy window: indices [0, k - 1]
        for (int i = 0; i < k; i++) {
            while (!deque.isEmpty() && stocks[deque.peekLast()] >= stocks[i]) {
                deque.pollLast();
            }
            deque.offerLast(i);
        }

        // For each sell index i, buy must be in [i-k, i-1]
        for (int i = k; i < stocks.length; i++) {

            // Minimum stock price in previous k days
            int minPrice = stocks[deque.peekFirst()];

            maxProfit = Math.max(maxProfit,stocks[i] - minPrice);

            // Remove index that won't belong to next window
            while (!deque.isEmpty() && deque.peekFirst() <= i - k)
                deque.pollFirst();

            // Maintain increasing prices
            while (!deque.isEmpty() && stocks[deque.peekLast()] >= stocks[i])
                deque.pollLast();

            deque.offerLast(i);
        }

        return maxProfit;
    }
}
/*
 * =====================================================================
 *  Max Profit, Buy Within Previous K Days           Custom (Sliding Window Minimum) | Hard
 * =====================================================================
 *
 * PROBLEM
 *   prices[i] is the stock price on day i. You may sell on any day i, but the buy must
 *   have happened on one of the previous k days, i.e. a day in [i - k, i - 1]. Return the
 *   best single-trade profit (0 if no trade ever profits). This is "Best Time to Buy and
 *   Sell Stock" with the buy window capped at k days, which turns the running minimum
 *   into a sliding-window minimum.
 *
 * EXAMPLE
 *   prices = [2, 1, 5, 90, 3, 10, 82, 67], k = 3  ->  89   (buy 1 on day 1, sell 90 on day 3)
 *   prices = [10, 1, 2, 3, 50], k = 2             ->  48   (day 4 may only buy from days 2,3)
 *   prices = [1, 100, 1, 1], k = 3                ->  99   (sell on day 1, buy on day 0)
 *   prices = [5, 4, 3, 2, 1], k = 3               ->  0    (never profitable)
 *
 * APPROACH  (monotonic deque holding candidate buy days)
 *   1. Keep a deque of indices whose prices are strictly increasing front to back; the
 *      front is always the cheapest buy day still inside the window.
 *   2. For each sell day i: pop indices < i - k from the front (too old to buy on).
 *   3. If the deque is non-empty, profit = prices[i] - prices[front]; keep the best.
 *   4. Push i as a future buy day: first pop every back index whose price >= prices[i],
 *      because a cheaper-or-equal, newer day dominates it.
 *
 * KEY INSIGHT
 *   A plain window cannot answer "minimum of the last k values" in O(1) after the minimum
 *   leaves the window; you would rescan. The monotonic deque fixes this: an older day with
 *   a higher price can never be the best buy again once a newer, cheaper day exists, so
 *   it is deleted forever. Every index is pushed and popped at most once. This is the
 *   local form of LeetCode 239 Sliding Window Maximum.
 *
 * COMPLEXITY
 *   Time  O(n)  each index enters and leaves the deque once
 *   Space O(k)  the deque never holds more than k indices
 *
 * INTERVIEW FOLLOW-UPS
 *   - k = n (no cap): the deque front is just the running minimum; a single int suffices.
 *   - Sliding Window Maximum (LC 239): same deque, flip the comparison.
 *   - Report the buy and sell days, not just the profit.
 *   - Why not a TreeMap or heap of the last k prices? Works, but O(n log k) and more code.
 *
 * Fixed: the original only evaluated sells from day k onward, so [1, 100, 1, 1] returned 0
 * instead of 99; sells on days 1..k-1 are valid (their window is simply shorter). Also k is
 * now a parameter and "no profitable trade" returns 0 instead of a negative number.
 *
 * RUN
 *   main() runs 4 cases (typical, window matters, early sell, decreasing) and prints
 *   actual vs expected.
 */
import java.util.ArrayDeque;
import java.util.Deque;

class MaxProfitWithWindowSize3 {

    public static int maxProfitWithWindow(int[] prices, int k) {
        if (prices == null || prices.length < 2 || k < 1)
            return 0;

        Deque<Integer> buyDays = new ArrayDeque<>(); // indices, prices strictly increasing
        int maxProfit = 0;

        for (int sell = 0; sell < prices.length; sell++) {
            // Drop buy days that fell out of the window [sell - k, sell - 1].
            while (!buyDays.isEmpty() && buyDays.peekFirst() < sell - k)
                buyDays.pollFirst();

            if (!buyDays.isEmpty())
                maxProfit = Math.max(maxProfit, prices[sell] - prices[buyDays.peekFirst()]);

            // Today becomes a buy candidate; it dominates any older day that is not cheaper.
            while (!buyDays.isEmpty() && prices[buyDays.peekLast()] >= prices[sell])
                buyDays.pollLast();
            buyDays.offerLast(sell);
        }
        return maxProfit;
    }

    private static void print(String label, int actual, int expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] prices = {2, 1, 5, 90, 3, 10, 82, 67};
        print("case 1 typical       ", maxProfitWithWindow(prices, 3), 89);
        print("case 2 window matters", maxProfitWithWindow(new int[]{10, 1, 2, 3, 50}, 2), 48);
        print("case 3 early sell    ", maxProfitWithWindow(new int[]{1, 100, 1, 1}, 3), 99);
        print("case 4 decreasing    ", maxProfitWithWindow(new int[]{5, 4, 3, 2, 1}, 3), 0);
    }
}

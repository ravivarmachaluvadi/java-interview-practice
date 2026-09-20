/*
 * =====================================================================
 *  Best Time to Buy and Sell Stock              LeetCode 121 | Easy    MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   prices[i] is the price of a stock on day i. Choose one day to buy and a LATER
 *   day to sell to maximise profit. Return that maximum profit, or 0 if no
 *   profitable trade exists (you may decline to trade). 1 <= n <= 1e5.
 *
 * EXAMPLE
 *   prices = [7, 1, 5, 3, 6, 4]  ->  5    buy day 1 at 1, sell day 4 at 6
 *   prices = [7, 6, 4, 3, 1]     ->  0    prices only fall, so do not trade
 *   prices = [5]                 ->  0    cannot sell on the same day
 *   prices = [2, 4, 1]           ->  2    the later min (1) never gets a sell day
 *
 * APPROACH  (running min plus best-so-far)
 *   1. Keep minSoFar = cheapest price seen on any day BEFORE today, and
 *      maxProfit = best profit found so far (starts at 0 = "no trade").
 *   2. For each price left to right:
 *        if price < minSoFar  -> this is a new cheapest buy day; remember it
 *        else                 -> selling today after buying at minSoFar yields
 *                                price - minSoFar; keep it if it beats maxProfit
 *   3. Return maxProfit.
 *   A new min never needs a profit check: selling at the min gives profit 0,
 *   which maxProfit already covers.
 *
 * KEY INSIGHT
 *   At each day the only past information that matters is the cheapest price so
 *   far. Scanning once while carrying that one value plus the best answer so far
 *   turns the O(n^2) "try every pair" search into O(n). This "running extreme +
 *   best-so-far" idiom is the direct ancestor of Kadane's maximum subarray:
 *   prices[j] - prices[i] is the sum of daily differences over (i, j].
 *
 * COMPLEXITY
 *   Time  O(n)  one pass
 *   Space O(1)  two integers
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the buy/sell DAYS, not just the profit: track the index of minSoFar
 *     and record both indices whenever maxProfit improves.
 *   - Unlimited transactions (LeetCode 122): sum every positive daily difference.
 *   - At most k transactions (LeetCode 123 / 188): DP over transactions x days.
 *   - Why can we ignore a min that appears late (the [2, 4, 1] case)? Any sell
 *     after it is compared against it, and if none comes the earlier best stands.
 *
 * RUN
 *   main() runs 4 cases (typical, strictly falling, single day, late min) and
 *   prints actual vs expected.
 */

class BestTimeToBuyAndSellStock {

    public static int maxProfit(int[] prices) {
        int maxProfit = 0;                     // 0 = "no trade" is always allowed
        int minSoFar = Integer.MAX_VALUE;      // cheapest buy price seen before today
        for (int price : prices) {
            if (price < minSoFar) {
                minSoFar = price;              // new best buy day; nothing to sell yet
            } else {
                // sell today after buying at the cheapest earlier day
                maxProfit = Math.max(maxProfit, price - minSoFar);
            }
        }
        return maxProfit;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical        ", maxProfit(new int[]{7, 1, 5, 3, 6, 4}), 5);
        print("case 2 falling prices ", maxProfit(new int[]{7, 6, 4, 3, 1}), 0);
        print("case 3 single day     ", maxProfit(new int[]{5}), 0);
        print("case 4 late minimum   ", maxProfit(new int[]{2, 4, 1}), 2);
    }
}

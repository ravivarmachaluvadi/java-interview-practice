/*
 * =====================================================================
 *  P097 Greedy Running Balance   Canonical LC 134 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 134, Gas Station)
 *   Stations on a circle: you gain gas[i] at station i and spend cost[i] to reach the next.
 *   Starting with an empty tank, return the unique start index that completes the loop, or
 *   -1.
 *
 * EXAMPLE
 *   gas [1,2,3,4,5], cost [3,4,5,1,2]  ->  3
 *   gas [2,3,4],     cost [3,4,3]      ->  -1
 *
 * RECOGNIZE WHEN
 *   - A quantity goes up and down along a sequence (fuel, money, balance, score) and must
 *     never drop below zero, or you want the best point to start / stop.
 *   - "collect every rise", "can you always give change", "best hour to close".
 *   Not this if: the choices interact over a window or with a limit on count -> DP
 *   (P092_StockStateMachine for k trades).
 *
 * TEMPLATE
 *   total = 0, tank = 0, start = 0
 *   for i in 0..n-1:
 *       total += delta[i]; tank += delta[i]
 *       if tank < 0: start = i + 1; tank = 0     // no start in [start..i] can work
 *   answer = total >= 0 ? start : -1
 *
 * APPROACH
 *   1. If total gas < total cost, no start works.
 *   2. Otherwise run once with a tank; whenever it goes negative at i, every start from the
 *      current candidate up to i fails, so the next candidate is i + 1.
 *
 * KEY INSIGHT
 *   If starting at s you run dry at i, then starting anywhere between s and i also runs dry
 *   at i (you would arrive at each of those points with at least as much gas as from s).
 *   That lets you skip the whole stretch, making the search O(n) instead of O(n^2).
 *
 * COMPLEXITY
 *   Time O(n), space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 122  Stock II (greedy)        add every positive day-to-day rise
 *   [coded] LC 860  Lemonade Change          keep counts of 5s and 10s; pay 15 change with
 *                                            10 + 5 before 5 + 5 + 5
 *   [coded] LC 2483 Min Penalty for a Shop   running balance of N minus Y; close where it
 *                                            peaks
 *           LC 1413 Min Start Value          1 - min(prefix sum), at least 1
 *           LC 2214 Min Health to Beat Game  total damage - min(armor, max hit) + 1
 *
 * PITFALLS
 *   - Check the total first; the single pass alone may report a start when none exists.
 *   - LC 860: prefer giving a 10 in change; keeping 5s is what keeps future change possible.
 *   - LC 2483: ties go to the EARLIEST hour.
 *
 * DEEP DIVE
 *   C10_GasStation, B05_LemonadeChange, C01_BestTimeToBuyAndSellStockII,
 *   C03_MinimumHealthToBeatGame (13-Greedy)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class RunningBalance {

    // Canonical LC 134.
    static int canCompleteCircuit(int[] gas, int[] cost) {
        int total = 0;
        int tank = 0;
        int start = 0;
        for (int i = 0; i < gas.length; i++) {
            int delta = gas[i] - cost[i];
            total += delta;
            tank += delta;
            if (tank < 0) {
                start = i + 1;                     // no start in the old stretch survives
                tank = 0;
            }
        }
        return total >= 0 ? start : -1;
    }

    // LC 122: unlimited trades = sum of every rise.
    static int maxProfit(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            profit += Math.max(0, prices[i] - prices[i - 1]);
        }
        return profit;
    }

    // LC 860: customers pay 5, 10 or 20 for a 5 lemonade, in order.
    static boolean lemonadeChange(int[] bills) {
        int fives = 0;
        int tens = 0;
        for (int b : bills) {
            if (b == 5) {
                fives++;
            } else if (b == 10) {
                fives--;
                tens++;
            } else if (tens > 0) {
                tens--;                            // 10 + 5 keeps more 5s for later
                fives--;
            } else {
                fives -= 3;
            }
            if (fives < 0) {
                return false;
            }
        }
        return true;
    }

    // LC 2483: closing at hour j costs (N before j) + (Y at or after j); earliest best hour.
    static int bestClosingTime(String customers) {
        int balance = 0;                           // (Y seen) - (N seen) before hour j
        int best = 0;
        int bestHour = 0;
        for (int j = 0; j < customers.length(); j++) {
            balance += customers.charAt(j) == 'Y' ? 1 : -1;
            if (balance > best) {
                best = balance;
                bestHour = j + 1;
            }
        }
        return bestHour;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 134 start 3",
                canCompleteCircuit(new int[]{1, 2, 3, 4, 5}, new int[]{3, 4, 5, 1, 2}), 3);
        check("LC 134 impossible", canCompleteCircuit(new int[]{2, 3, 4}, new int[]{3, 4, 3}), -1);
        check("LC 134 single station", canCompleteCircuit(new int[]{5}, new int[]{4}), 0);

        check("LC 122 [7,1,5,3,6,4]", maxProfit(new int[]{7, 1, 5, 3, 6, 4}), 7);
        check("LC 122 falling", maxProfit(new int[]{7, 6, 4, 3, 1}), 0);

        check("LC 860 [5,5,5,10,20]", lemonadeChange(new int[]{5, 5, 5, 10, 20}), true);
        check("LC 860 [5,5,10,10,20]", lemonadeChange(new int[]{5, 5, 10, 10, 20}), false);
        check("LC 860 first bill 10", lemonadeChange(new int[]{10}), false);

        check("LC 2483 YYNY", bestClosingTime("YYNY"), 2);
        check("LC 2483 NNNNN", bestClosingTime("NNNNN"), 0);
        check("LC 2483 YYYY", bestClosingTime("YYYY"), 4);
    }
}

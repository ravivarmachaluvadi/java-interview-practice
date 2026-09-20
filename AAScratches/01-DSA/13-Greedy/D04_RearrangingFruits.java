/*
 * =====================================================================
 *  Rearranging Fruits                                  LeetCode 2561 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Two baskets of n fruits each; basket1[i] and basket2[i] are costs. One operation
 *   swaps basket1[i] with basket2[j] for any i, j at a cost of min(basket1[i], basket2[j]).
 *   Return the minimum total cost to make the two baskets equal as MULTISETS (same
 *   values with the same counts, order irrelevant), or -1 if that is impossible.
 *
 * EXAMPLE
 *   b1 = [4,2,2,2], b2 = [1,4,1,2]  ->  1    one swap of a 2 with a 1 costs min(2,1) = 1
 *   b1 = [2,3,4,1], b2 = [3,2,5,1]  -> -1    the values 4 and 5 each appear once in total
 *   b1 = [1,2,3],   b2 = [3,2,1]    ->  0    already equal as multisets
 *   b1 = [25,25,1], b2 = [30,30,1]  ->  2    route BOTH swaps through the cheap 1:
 *                                            (25 <-> 1) costs 1, then (1 <-> 30) costs 1
 *
 * APPROACH  (multiset difference, then a 2 * globalMin proxy swap)
 *   1. Build a signed tally: +1 for each value in basket1, -1 for each in basket2.
 *      Record minVal = the cheapest fruit value seen in either basket.
 *   2. Feasibility: if any value has an ODD tally it cannot be split evenly between two
 *      baskets, so return -1.
 *   3. Surplus list: a value with tally d != 0 is over-represented on one side by |d|,
 *      and exactly |d| / 2 copies of it must move across. Push that many copies.
 *   4. The list has even size m, half of it sitting in basket1 and half in basket2. Each
 *      swap fixes one item from each half, so we perform m / 2 swaps and pay for only one
 *      item per swap. Sort ascending and pay for the cheaper half: elements 0 .. m/2 - 1.
 *   5. Cost of moving an item x is min(x, 2 * minVal): swap x directly, OR use the
 *      globally cheapest fruit as a broker in two swaps.
 *
 * KEY INSIGHT
 *   Two stacked ideas. First, positions never matter - only the multiset difference does,
 *   so the whole problem collapses to "which surplus items must cross". Second, the
 *   cheapest fruit in either basket can broker any exchange: move x out using minVal,
 *   then move minVal back, for 2 * minVal total. That caps the price of every single
 *   item, which is why an expensive fruit never costs its own value. Pattern to
 *   recognise: whenever a direct operation has a price, check whether a cheap universal
 *   intermediary makes a two-step route cheaper.
 *
 * COMPLEXITY
 *   Time  O(n log n)  one pass to tally, then sorting the surplus list (size <= n)
 *   Space O(n)        the tally map plus the surplus list
 *
 * INTERVIEW FOLLOW-UPS
 *   - Prove the 2 * minVal bound: why is a three-step route never cheaper?
 *   - Why pay for only the cheaper half of the sorted surplus list, not all of it?
 *   - What changes if a swap costs max(a, b) instead of min(a, b)?
 *   - Same shape with k baskets instead of 2 - does the greedy still hold?
 *
 * RUN
 *   main() runs 5 cases: the two LeetCode examples, an already-equal case, the
 *   broker case where 2 * minVal wins, and a single-element infeasible case.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SolutionRearrangingFruits {

    public static long minCost(int[] basket1, int[] basket2) {
        // 1. Signed tally: positive = surplus in basket1, negative = surplus in basket2.
        Map<Integer, Integer> tally = new HashMap<>();
        for (int v : basket1) tally.merge(v, 1, Integer::sum);
        for (int v : basket2) tally.merge(v, -1, Integer::sum);

        int minVal = Integer.MAX_VALUE;
        List<Integer> surplus = new ArrayList<>();

        for (Map.Entry<Integer, Integer> e : tally.entrySet()) {
            int value = e.getKey();
            int diff = e.getValue();

            // 2. An odd total count of this value cannot be halved between two baskets.
            if ((diff % 2) != 0) return -1L;

            // 3. Half of the imbalance has to cross to the other basket.
            int crossings = Math.abs(diff) / 2;
            for (int i = 0; i < crossings; i++) surplus.add(value);

            // Every key of the tally appeared in at least one basket, so this really is
            // the global minimum - including values that are already balanced (diff == 0).
            minVal = Math.min(minVal, value);
        }

        // 4. Each swap moves one item each way, so only the cheaper half is ever paid for.
        Collections.sort(surplus);
        long cost = 0L;
        int swapsNeeded = surplus.size() / 2;
        for (int i = 0; i < swapsNeeded; i++) {
            int x = surplus.get(i);
            // 5. Either swap x directly, or route it through the cheapest fruit twice.
            cost += Math.min(x, 2L * minVal);
        }
        return cost;
    }

    private static void print(String label, Object actual, Object expected) {
        boolean ok = String.valueOf(actual).equals(String.valueOf(expected));
        System.out.println(label + ": " + actual + "   expected " + expected
                + (ok ? "" : "   <-- MISMATCH"));
    }

    public static void main(String[] args) {
        // Typical: one cheap swap fixes the imbalance.
        print("case 1 [4,2,2,2] / [1,4,1,2]",
                minCost(new int[]{4, 2, 2, 2}, new int[]{1, 4, 1, 2}), 1L);

        // Infeasible: 4 and 5 each occur once in total, so neither can be split.
        print("case 2 [2,3,4,1] / [3,2,5,1]",
                minCost(new int[]{2, 3, 4, 1}, new int[]{3, 2, 5, 1}), -1L);

        // Edge: already equal as multisets, nothing to pay.
        print("case 3 [1,2,3] / [3,2,1]",
                minCost(new int[]{1, 2, 3}, new int[]{3, 2, 1}), 0L);

        // Tricky: the surplus items cost 25, but the broker route costs 2 * 1 = 2.
        print("case 4 [25,25,1] / [30,30,1]",
                minCost(new int[]{25, 25, 1}, new int[]{30, 30, 1}), 2L);

        // Edge: single element per basket with different values -> odd tally -> -1.
        print("case 5 [1] / [2]",
                minCost(new int[]{1}, new int[]{2}), -1L);
    }
}

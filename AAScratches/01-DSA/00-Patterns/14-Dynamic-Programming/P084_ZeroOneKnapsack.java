/*
 * =====================================================================
 *  P084 0/1 Knapsack (Subset Sum Family)   Canonical LC 416 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 416, Partition Equal Subset Sum)
 *   Return whether nums (positive integers) can be split into two subsets with equal sums.
 *
 * EXAMPLE
 *   [1, 5, 11, 5]  ->  true      [1, 5, 5] and [11]
 *   [1, 2, 3, 5]   ->  false     total 11 is odd
 *
 * RECOGNIZE WHEN
 *   - Each item is used AT MOST ONCE, and a capacity / target sum limits the choice:
 *     "subset with sum k", "split into two equal / closest halves", "assign + or - signs",
 *     "max value within weight W".
 *   - n * target is small enough for a table (target up to ~10^4).
 *   Not this if: items can be reused -> P085_UnboundedKnapsack; you must LIST the subsets
 *   -> P078_CombinationSum; the order of picks matters (LC 377) ->
 *   P085_UnboundedKnapsack.
 *
 * TEMPLATE
 *   dp[c] = best answer with capacity / sum c using the items seen so far
 *   for item in items:
 *       for c from CAP down to item.weight:            // DOWNWARD: each item used once
 *           dp[c] = combine(dp[c], dp[c - item.weight] (+ item.value))
 *   reachable: boolean OR;  count: add;  max value: max
 *
 * APPROACH
 *   1. Equal halves means a subset sums to total / 2 (impossible if total is odd).
 *   2. can[s] = some subset of the items so far sums to s; can[0] = true.
 *   3. For each number, update can[] from high sums to low.
 *
 * KEY INSIGHT
 *   Iterating the capacity DOWNWARD makes dp[c - w] still hold the value from BEFORE this
 *   item, so the item is counted at most once. Iterating upward would let it be reused,
 *   which is exactly the unbounded knapsack. Many problems become this one after a rewrite:
 *   target sum (+/- signs) is "subset sum to (total + target) / 2".
 *
 * COMPLEXITY
 *   Time O(n * CAP), space O(CAP).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] Subset Sum (GfG)                 boolean dp to a given target
 *   [coded] LC 494  Target Sum               P - N = target, P + N = total
 *                                            -> count subsets with sum (total + target) / 2
 *   [coded] LC 1049 Last Stone Weight II     smash = split into two groups; minimise the
 *                                            difference: best reachable sum <= total / 2
 *   [coded] 0/1 Knapsack (value)             dp[c] = max(dp[c], dp[c - w] + v)
 *           LC 474  Ones and Zeroes          two capacities (zeros, ones): 2D dp, both downward
 *           LC 879  Profitable Schemes       count with a (members, profit) table
 *           LC 2915 Longest Subsequence Sum  max count instead of max value
 *
 * PITFALLS
 *   - Iterating capacity upward silently turns 0/1 into unbounded.
 *   - LC 494: (total + target) must be even and non-negative; zeros double the count.
 *   - dp[0] = true / 1 is the empty subset; do not forget it.
 *
 * DEEP DIVE
 *   A04_SubsetSumEqualsToTarget, A05_Knapsack01, C07_TargetSumCountWays,
 *   A03_CountSubsequenceWithTargetSum (12-Dynamic-Programming)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class ZeroOneKnapsack {

    // Canonical LC 416.
    static boolean canPartition(int[] nums) {
        int total = 0;
        for (int x : nums) {
            total += x;
        }
        return total % 2 == 0 && subsetSum(nums, total / 2);
    }

    // Subset sum: can some subset reach exactly target?
    static boolean subsetSum(int[] nums, int target) {
        boolean[] can = new boolean[target + 1];
        can[0] = true;
        for (int x : nums) {
            for (int s = target; s >= x; s--) {
                can[s] = can[s] || can[s - x];
            }
        }
        return can[target];
    }

    // LC 494: count sign assignments that reach target.
    static int findTargetSumWays(int[] nums, int target) {
        int total = 0;
        for (int x : nums) {
            total += x;
        }
        if (Math.abs(target) > total || (total + target) % 2 != 0) {
            return 0;
        }
        int want = (total + target) / 2;          // sum of the numbers that get a '+'
        int[] ways = new int[want + 1];
        ways[0] = 1;
        for (int x : nums) {
            for (int s = want; s >= x; s--) {
                ways[s] += ways[s - x];
            }
        }
        return ways[want];
    }

    // LC 1049: smallest possible weight of the last stone.
    static int lastStoneWeightII(int[] stones) {
        int total = 0;
        for (int x : stones) {
            total += x;
        }
        boolean[] can = new boolean[total / 2 + 1];
        can[0] = true;
        for (int x : stones) {
            for (int s = total / 2; s >= x; s--) {
                can[s] = can[s] || can[s - x];
            }
        }
        for (int s = total / 2; s >= 0; s--) {
            if (can[s]) {
                return total - 2 * s;              // two groups: s and total - s
            }
        }
        return total;
    }

    // Classic 0/1 knapsack: best value with total weight <= capacity.
    static int knapsack(int[] weight, int[] value, int capacity) {
        int[] best = new int[capacity + 1];
        for (int i = 0; i < weight.length; i++) {
            for (int c = capacity; c >= weight[i]; c--) {
                best[c] = Math.max(best[c], best[c - weight[i]] + value[i]);
            }
        }
        return best[capacity];
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 416 [1,5,11,5]", canPartition(new int[]{1, 5, 11, 5}), true);
        check("LC 416 [1,2,3,5] odd total", canPartition(new int[]{1, 2, 3, 5}), false);
        check("LC 416 [2,2,3,5] even but impossible", canPartition(new int[]{2, 2, 3, 5}), false);

        check("subset sum target 9", subsetSum(new int[]{3, 34, 4, 12, 5, 2}, 9), true);
        check("subset sum target 30", subsetSum(new int[]{3, 34, 4, 12, 5, 2}, 30), false);

        check("LC 494 five ones t=3", findTargetSumWays(new int[]{1, 1, 1, 1, 1}, 3), 5);
        check("LC 494 [1] t=1", findTargetSumWays(new int[]{1}, 1), 1);
        check("LC 494 zeros double the count",
                findTargetSumWays(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1}, 1), 256);
        check("LC 494 unreachable", findTargetSumWays(new int[]{1, 2}, 4), 0);

        check("LC 1049 [2,7,4,1,8,1]", lastStoneWeightII(new int[]{2, 7, 4, 1, 8, 1}), 1);
        check("LC 1049 [31,26,33,21,40]", lastStoneWeightII(new int[]{31, 26, 33, 21, 40}), 5);

        check("knapsack W=7", knapsack(new int[]{1, 3, 4, 5}, new int[]{1, 4, 5, 7}, 7), 9);
        check("knapsack nothing fits", knapsack(new int[]{5}, new int[]{10}, 4), 0);
    }
}

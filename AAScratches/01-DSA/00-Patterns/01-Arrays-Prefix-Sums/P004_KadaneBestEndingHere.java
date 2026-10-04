/*
 * =====================================================================
 *  P004 Kadane: Best Subarray Ending Here   Canonical LC 53 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 53, Maximum Subarray)
 *   Given an int array nums (at least one element), return the largest sum of any
 *   non-empty contiguous subarray.
 *
 * EXAMPLE
 *   [-2, 1, -3, 4, -1, 2, 1, -5, 4]  ->  6     [4, -1, 2, 1]
 *   [5, 4, -1, 7, 8]                 ->  23    the whole array
 *   [-3, -1, -2]                     ->  -1    the trap: all negative, answer is not 0
 *
 * RECOGNIZE WHEN
 *   - "maximum (or minimum) sum / product of a contiguous subarray".
 *   - The best answer ending at i depends only on the best answer ending at i - 1.
 *   - "best profit with one buy then one sell" (a running minimum is the same idea).
 *   Not this if: the subarray needs a fixed length -> P015_FixedWindowAggregate; or the
 *   elements need not be contiguous -> P082_LinearTakeSkip.
 *
 * TEMPLATE
 *   cur = best = a[0]
 *   for i in 1..n-1:
 *       cur = max(a[i], cur + a[i])     // extend the run, or start fresh at i
 *       best = max(best, cur)
 *
 * APPROACH
 *   1. cur = best sum of a subarray that ENDS exactly at i.
 *   2. Either a[i] joins the run ending at i - 1, or it starts a new run; keep the larger.
 *   3. The answer is the best cur seen anywhere.
 *
 * KEY INSIGHT
 *   A prefix with a negative sum can only hurt whatever follows it, so drop it the moment
 *   it goes negative. That one decision per index is a 1-state DP in O(1) space.
 *
 * COMPLEXITY
 *   Time O(n), space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 152  Maximum Product Subarray  carry BOTH max and min ending here; a
 *                                             negative number swaps them
 *   [coded] LC 918  Max Circular Subarray     max(kadaneMax, total - kadaneMin), except
 *                                             when every number is negative
 *   [coded] LC 121  Best Time Buy/Sell Stock  profit = price - min price so far
 *           LC 1749 Max Absolute Sum          max(kadaneMax, -kadaneMin)
 *           LC 1186 Max Sum With One Deletion two states: no deletion yet / one used
 *           LC 1014 Best Sightseeing Pair     carry best (a[i] + i) so far
 *           LC 2606 Max Cost Substring        map each char to its value, then Kadane
 *
 * PITFALLS
 *   - Starting best at 0 returns 0 for an all-negative array; start at a[0].
 *   - Product: compute the new max and min from the OLD pair (save one in a temp).
 *   - Circular: if kadaneMax < 0, total - kadaneMin would be the empty subarray.
 *
 * DEEP DIVE
 *   C01_KadaneSAlgorithm (01-Arrays), C02_MaxProductSubArray (12-Dynamic-Programming)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class KadaneBestEndingHere {

    // Canonical LC 53.
    static int maxSubArray(int[] nums) {
        int cur = nums[0];
        int best = nums[0];
        for (int i = 1; i < nums.length; i++) {
            cur = Math.max(nums[i], cur + nums[i]);
            best = Math.max(best, cur);
        }
        return best;
    }

    // LC 152: a negative number turns the smallest product into the largest, so carry both.
    static int maxProduct(int[] nums) {
        int hi = nums[0];
        int lo = nums[0];
        int best = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int x = nums[i];
            int newHi = Math.max(x, Math.max(hi * x, lo * x));
            int newLo = Math.min(x, Math.min(hi * x, lo * x));
            hi = newHi;
            lo = newLo;
            best = Math.max(best, hi);
        }
        return best;
    }

    // LC 918: a wrapping subarray is "everything except a middle run", so its best sum is
    // total minus the most negative middle run.
    static int maxSubarraySumCircular(int[] nums) {
        int total = 0;
        int curMax = 0;
        int bestMax = Integer.MIN_VALUE;
        int curMin = 0;
        int bestMin = Integer.MAX_VALUE;
        for (int x : nums) {
            total += x;
            curMax = Math.max(x, curMax + x);
            bestMax = Math.max(bestMax, curMax);
            curMin = Math.min(x, curMin + x);
            bestMin = Math.min(bestMin, curMin);
        }
        return bestMax < 0 ? bestMax : Math.max(bestMax, total - bestMin);
    }

    // LC 121: the best sale on day i uses the cheapest day before it.
    static int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int best = 0;
        for (int p : prices) {
            minPrice = Math.min(minPrice, p);
            best = Math.max(best, p - minPrice);
        }
        return best;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 53 [-2,1,-3,4,-1,2,1,-5,4]",
                maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}), 6);
        check("LC 53 [1]", maxSubArray(new int[]{1}), 1);
        check("LC 53 [5,4,-1,7,8]", maxSubArray(new int[]{5, 4, -1, 7, 8}), 23);
        check("LC 53 [-3,-1,-2] all negative", maxSubArray(new int[]{-3, -1, -2}), -1);

        check("LC 152 [2,3,-2,4]", maxProduct(new int[]{2, 3, -2, 4}), 6);
        check("LC 152 [-2,0,-1]", maxProduct(new int[]{-2, 0, -1}), 0);
        check("LC 152 [-2,3,-4] two negatives", maxProduct(new int[]{-2, 3, -4}), 24);

        check("LC 918 [1,-2,3,-2]", maxSubarraySumCircular(new int[]{1, -2, 3, -2}), 3);
        check("LC 918 [5,-3,5] wraps", maxSubarraySumCircular(new int[]{5, -3, 5}), 10);
        check("LC 918 [-3,-2,-3] all negative", maxSubarraySumCircular(new int[]{-3, -2, -3}), -2);

        check("LC 121 [7,1,5,3,6,4]", maxProfit(new int[]{7, 1, 5, 3, 6, 4}), 5);
        check("LC 121 [7,6,4,3,1] falling", maxProfit(new int[]{7, 6, 4, 3, 1}), 0);
    }
}

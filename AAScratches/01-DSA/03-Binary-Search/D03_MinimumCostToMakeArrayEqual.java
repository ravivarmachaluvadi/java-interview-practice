/*
 * =====================================================================
 *  Minimum Cost to Make Array Equal                 LeetCode 2448 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given nums[] and cost[] of equal length n (all positive), one operation changes
 *   one nums[i] by +1 or -1 and costs cost[i]. Return the minimum total cost to make
 *   every element equal. Values reach 1e6 and n reaches 1e5, so the total needs a long.
 *
 * EXAMPLE
 *   nums = [1, 3, 5, 2], cost = [2, 3, 1, 14]  ->  8    make everything 2: 2*1 + 3*1 + 1*3
 *   nums = [2, 2, 2, 2, 2], cost = [4, 2, 8, 1, 3]  ->  0    already equal
 *   nums = [7], cost = [5]                     ->  0    single element, nothing to move
 *   nums = [1, 1000000], cost = [1000000, 1000000]  ->  999999000000   overflows an int
 *
 * APPROACH  (binary search on a unimodal / convex function)
 *   1. The answer target always lies in [min(nums), max(nums)]; moving outside that
 *      range only adds cost for every element.
 *   2. totalCost(t) = sum |nums[i] - t| * cost[i] is a sum of V-shaped functions, so it
 *      is convex: it decreases, hits a minimum, then increases (no other dips).
 *   3. Binary search the target range. At mid compare f(mid) with f(mid + 1):
 *      if f(mid) > f(mid+1) the curve is still going down, so the minimum is to the
 *      right (low = mid + 1); otherwise it is at mid or to the left (high = mid - 1).
 *   4. Track the smallest f seen; that is the answer once the window closes.
 *
 *   Second method, weightedMedianCost(): sort pairs by value, walk until cumulative
 *   cost reaches half the total. That value is the weighted median and the optimum.
 *
 * KEY INSIGHT
 *   Not every binary search needs a yes/no predicate. When the function is convex,
 *   the slope sign f(mid+1) - f(mid) is monotone (negative, then non-negative), so
 *   "is the slope non-negative yet?" IS the monotone predicate and the same template
 *   applies. Pattern: minimise a convex cost over an integer range => compare
 *   neighbours, descend. The weighted median is the closed-form of the same fact.
 *
 * COMPLEXITY
 *   Time  O(n log(maxV - minV))  each probe costs O(n), about 20 probes
 *   Space O(1)                    only the running answer and the window
 *   (weightedMedianCost: O(n log n) time for the sort, O(n) space for the pairs)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is the minimum at the weighted median? Cost slope at t is (weight left of t)
 *     minus (weight right of t); it crosses zero exactly when half the weight is passed.
 *   - Unweighted version (LC 462, cost all 1): plain median, O(n log n) or O(n) select.
 *   - Why not ternary search? Works too, but f is integer-domain and flat spots on the
 *     curve can trick strict-comparison ternary search; the slope test is safer.
 *   - What if elements could only be increased? Then the target is max(nums), no search.
 *
 * RUN
 *   main() runs 4 cases through both methods and prints actual vs expected.
 */

import java.util.Arrays;

class MinimumCostToMakeArrayEqual {

    /** Author's approach: binary search on the target value, descending the convex curve. */
    public static long minCost(int[] nums, int[] cost) {
        int low = Integer.MAX_VALUE, high = 0;
        for (int num : nums) {
            high = Math.max(high, num);
            low = Math.min(low, num);
        }
        long answer = Long.MAX_VALUE;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            long costAtMid = totalCost(mid, nums, cost);
            long costAtNext = totalCost(mid + 1, nums, cost);
            answer = Math.min(answer, Math.min(costAtMid, costAtNext));
            if (costAtMid > costAtNext) {
                low = mid + 1;      // still going downhill: minimum is to the right
            } else {
                high = mid - 1;     // flat or uphill: minimum is at mid or to the left
            }
        }
        return answer;
    }

    /** Cost of making every element equal to target. */
    private static long totalCost(int target, int[] nums, int[] cost) {
        long total = 0;
        for (int i = 0; i < nums.length; i++) {
            // cast to long: 1e6 * 1e6 overflows int
            total += (long) Math.abs(nums[i] - target) * cost[i];
        }
        return total;
    }

    /** Alternative: the optimum is the weighted median of nums, with cost[] as the weights. */
    public static long weightedMedianCost(int[] nums, int[] cost) {
        int n = nums.length;
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Integer.compare(nums[a], nums[b]));

        long totalWeight = 0;
        for (int c : cost) totalWeight += c;

        // First value at which the cumulative weight reaches half the total.
        long seen = 0;
        int median = nums[order[n - 1]];
        for (int idx : order) {
            seen += cost[idx];
            if (seen * 2 >= totalWeight) {
                median = nums[idx];
                break;
            }
        }
        return totalCost(median, nums, cost);
    }

    private static void run(String label, int[] nums, int[] cost, long expected) {
        System.out.println(label + ": binarySearch=" + minCost(nums, cost)
                + " weightedMedian=" + weightedMedianCost(nums, cost)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 typical", new int[]{1, 3, 5, 2}, new int[]{2, 3, 1, 14}, 8L);
        run("case 2 all equal", new int[]{2, 2, 2, 2, 2}, new int[]{4, 2, 8, 1, 3}, 0L);
        run("case 3 single", new int[]{7}, new int[]{5}, 0L);
        run("case 4 long overflow", new int[]{1, 1000000}, new int[]{1000000, 1000000},
                999999000000L);
    }
}

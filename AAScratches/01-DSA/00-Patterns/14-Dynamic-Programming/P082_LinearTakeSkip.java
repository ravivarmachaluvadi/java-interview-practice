/*
 * =====================================================================
 *  P082 1D DP: Take or Skip   Canonical LC 198 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 198, House Robber)
 *   nums[i] is the money in house i. You cannot rob two ADJACENT houses. Return the most
 *   money you can rob.
 *
 * EXAMPLE
 *   [1, 2, 3, 1]     ->  4      houses 0 and 2
 *   [2, 7, 9, 3, 1]  ->  12     houses 0, 2 and 4
 *   [2, 1, 1, 2]     ->  4      houses 0 and 3: skipping two in a row can pay
 *
 * RECOGNIZE WHEN
 *   - A line of items; at each one you decide (take / skip, step 1 / step 2), and the
 *     choice limits the NEXT choice or two.
 *   - "number of ways to reach step n", "min cost to reach the top", "cannot pick
 *     neighbours".
 *   - Brute force is a binary recursion that recomputes the same suffixes.
 *   Not this if: the items are a contiguous subarray -> P004_KadaneBestEndingHere; there is
 *   a capacity / weight limit -> P084_ZeroOneKnapsack.
 *
 * TEMPLATE
 *   dp[i] = best answer using items 0..i
 *   dp[i] = best( dp[i-1]            (skip item i),
 *                 dp[i-2] + value[i] (take item i) )
 *   only two previous values are used -> keep prev2, prev1 (O(1) space)
 *   counting version: ways[i] = ways[i-1] + ways[i-2]
 *
 * APPROACH
 *   1. best(i) = max(best(i-1), best(i-2) + nums[i]).
 *   2. Roll two variables: prev2 = best(i-2), prev1 = best(i-1).
 *
 * KEY INSIGHT
 *   Write the recurrence from "what happens to the LAST item": either it is skipped (the
 *   answer is the best for the shorter prefix) or it is taken (so its neighbour is not).
 *   When dp[i] needs only dp[i-1] and dp[i-2], the table collapses to two variables.
 *
 * COMPLEXITY
 *   Time O(n), space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 70   Climbing Stairs          ways[i] = ways[i-1] + ways[i-2] (Fibonacci)
 *   [coded] LC 746  Min Cost Climbing Stairs dp[i] = cost[i] + min(dp[i-1], dp[i-2])
 *   [coded] LC 213  House Robber II          houses in a CIRCLE: max(rob 0..n-2, rob 1..n-1)
 *   [coded] LC 740  Delete and Earn          bucket points by value, then house robber over
 *                                            values (taking v forbids v - 1 and v + 1)
 *           LC 2140 Questions With Brainpower take i jumps to i + brainpower + 1 (fill right
 *                                            to left)
 *           LC 1137 N-th Tribonacci          three rolling variables
 *           LC 256  Paint House              state per colour: dp[i][c] = cost + min of others
 *           LC 337  House Robber III         the same idea on a tree -> P062_TreeDp
 *
 * PITFALLS
 *   - Base cases: n == 0 and n == 1 before the loop.
 *   - LC 213 with one house: both ranges are empty; return nums[0].
 *   - Overflow: LC 70 with large n grows like Fibonacci (long or modulo).
 *
 * DEEP DIVE
 *   B03_HouseRobber, A02_ClimbingStairs, A01_Fibonacci (12-Dynamic-Programming)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class LinearTakeSkip {

    // Canonical LC 198.
    static int rob(int[] nums) {
        return robRange(nums, 0, nums.length - 1);
    }

    // Best for houses lo..hi (inclusive).
    private static int robRange(int[] nums, int lo, int hi) {
        int prev2 = 0;                             // best up to i - 2
        int prev1 = 0;                             // best up to i - 1
        for (int i = lo; i <= hi; i++) {
            int cur = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    // LC 70.
    static long climbStairs(int n) {
        long a = 1;                                // ways to reach step 0
        long b = 1;                                // ways to reach step 1
        for (int i = 2; i <= n; i++) {
            long c = a + b;
            a = b;
            b = c;
        }
        return b;
    }

    // LC 746: start at step 0 or 1; pay cost[i] to leave step i; reach the top (index n).
    static int minCostClimbingStairs(int[] cost) {
        int a = 0;                                 // min cost to stand on step i - 2
        int b = 0;                                 // min cost to stand on step i - 1
        for (int i = 2; i <= cost.length; i++) {
            int c = Math.min(b + cost[i - 1], a + cost[i - 2]);
            a = b;
            b = c;
        }
        return b;
    }

    // LC 213: first and last house are neighbours.
    static int robCircle(int[] nums) {
        if (nums.length == 1) {
            return nums[0];
        }
        return Math.max(robRange(nums, 0, nums.length - 2), robRange(nums, 1, nums.length - 1));
    }

    // LC 740: taking value v earns v * count(v) and deletes all v - 1 and v + 1.
    static int deleteAndEarn(int[] nums) {
        int max = 0;
        for (int x : nums) {
            max = Math.max(max, x);
        }
        int[] points = new int[max + 1];
        for (int x : nums) {
            points[x] += x;
        }
        return rob(points);                        // adjacent VALUES cannot both be taken
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 198 [1,2,3,1]", rob(new int[]{1, 2, 3, 1}), 4);
        check("LC 198 [2,7,9,3,1]", rob(new int[]{2, 7, 9, 3, 1}), 12);
        check("LC 198 [2,1,1,2] skip two", rob(new int[]{2, 1, 1, 2}), 4);
        check("LC 198 single house", rob(new int[]{5}), 5);

        check("LC 70 n=2", climbStairs(2), 2L);
        check("LC 70 n=3", climbStairs(3), 3L);
        check("LC 70 n=45", climbStairs(45), 1836311903L);

        check("LC 746 [10,15,20]", minCostClimbingStairs(new int[]{10, 15, 20}), 15);
        check("LC 746 ten steps",
                minCostClimbingStairs(new int[]{1, 100, 1, 1, 1, 100, 1, 1, 100, 1}), 6);

        check("LC 213 [2,3,2]", robCircle(new int[]{2, 3, 2}), 3);
        check("LC 213 [1,2,3,1]", robCircle(new int[]{1, 2, 3, 1}), 4);
        check("LC 213 [1,2,3]", robCircle(new int[]{1, 2, 3}), 3);
        check("LC 213 single house", robCircle(new int[]{5}), 5);

        check("LC 740 [3,4,2]", deleteAndEarn(new int[]{3, 4, 2}), 6);
        check("LC 740 [2,2,3,3,3,4]", deleteAndEarn(new int[]{2, 2, 3, 3, 3, 4}), 9);
    }
}

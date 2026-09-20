/*
 * =====================================================================
 *  House Robber                                  LeetCode 198 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Each array slot holds the money in one house standing in a row. Robbing two
 *   adjacent houses triggers the alarm, so no two chosen indices may be neighbours.
 *   Return the largest total that can be taken. Values are non-negative.
 *
 * EXAMPLE
 *   nums = [1, 2, 3, 1]     -> 4    rob house 0 and house 2 (1 + 3)
 *   nums = [2, 7, 9, 3, 1]  -> 12   rob houses 0, 2, 4 (2 + 9 + 1)
 *   nums = [5]              -> 5    single house, nothing to skip
 *   nums = []               -> 0    nothing to rob
 *
 * APPROACH  (non-adjacent 1D DP, then the O(1) rolling form)
 *   1. dp[i] = the best total achievable considering only houses 0..i.
 *   2. At house i there are exactly two choices:
 *        skip it  -> dp[i - 1]
 *        take it  -> nums[i] + dp[i - 2]   (i - 1 is now off limits)
 *      so dp[i] = max(dp[i - 1], nums[i] + dp[i - 2]).
 *   3. Seed dp[0] = nums[0] and dp[1] = max(nums[0], nums[1]).
 *   4. The transition only ever looks two steps back, so the whole array collapses
 *      into two ints (robSpaceOptimized). Both methods are run from main().
 *
 * KEY INSIGHT
 *   "Take or skip, and taking forbids the previous index" is the whole pattern.
 *   Any problem phrased as "pick a maximum-weight set with no two neighbours"
 *   is this recurrence: max(dp[i-1], value[i] + dp[i-2]).
 *
 * COMPLEXITY
 *   Time  O(n)   one pass, constant work per house
 *   Space O(n)   for the table version; O(1) for the rolling version
 *
 * INTERVIEW FOLLOW-UPS
 *   - House Robber II: the houses form a circle, so run this twice, on [0..n-2]
 *     and [1..n-1], and take the better of the two.
 *   - House Robber III: the houses form a binary tree; return a (rob, skip) pair
 *     from each node instead of scanning an array.
 *   - Print which houses were robbed, not just the total (walk the dp table back).
 *   - What changes if values can be negative? Never take a negative house.
 *
 * RUN
 *   main() runs 4 cases (typical, larger typical, single house, empty) through
 *   both implementations and prints actual vs expected.
 *
 * Fixed: the file declared rob(int[]) twice, so it did not compile. The second
 *        one is now robSpaceOptimized. Its guard was also wrong:
 *        "nums == null && nums.length == 0" dereferences null before the check;
 *        it needs || , not &&.
 */

import java.util.Arrays;

class HouseRobber {

    /** Table version: dp[i] = best total using only houses 0..i. */
    public static int rob(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];

        int[] dp = new int[nums.length];
        dp[0] = nums[0];                            // only one house available
        dp[1] = Math.max(nums[0], nums[1]);         // adjacent, so take the richer one

        for (int i = 2; i < nums.length; i++) {
            int skipThisHouse = dp[i - 1];
            int robThisHouse = nums[i] + dp[i - 2]; // i - 1 is blocked by the alarm
            dp[i] = Math.max(skipThisHouse, robThisHouse);
        }
        return dp[nums.length - 1];
    }

    /**
     * Same recurrence with two rolling variables instead of the table:
     * twoBack plays dp[i - 2], oneBack plays dp[i - 1].
     */
    public static int robSpaceOptimized(int[] nums) {
        if (nums == null || nums.length == 0) return 0;   // was "&&", which NPE'd on null
        int n = nums.length;
        if (n == 1) return nums[0];

        int twoBack = nums[0];
        int oneBack = Math.max(nums[0], nums[1]);
        for (int i = 2; i < n; i++) {
            int current = Math.max(oneBack, twoBack + nums[i]);
            twoBack = oneBack;
            oneBack = current;
        }
        return oneBack;   // holds dp[n - 1] once the loop ends
    }

    private static void print(String label, int[] nums, int expected) {
        System.out.println(label + " " + Arrays.toString(nums)
                + "  table=" + rob(nums)
                + "  rolling=" + robSpaceOptimized(nums)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1:", new int[]{1, 2, 3, 1}, 4);
        print("case 2:", new int[]{2, 7, 9, 3, 1}, 12);
        print("case 3 (single):", new int[]{5}, 5);
        print("case 4 (empty):", new int[]{}, 0);
    }
}

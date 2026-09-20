/*
 * =====================================================================
 *  Target Sum (count +/- assignments)             LeetCode 494 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Put a '+' or a '-' in front of every number in nums (all values are >= 0) and
 *   concatenate them into an expression. Count how many of the 2^n sign assignments
 *   evaluate to target. Different positions holding equal values count separately,
 *   and target may be negative.
 *
 * EXAMPLE
 *   nums = [1,1,1,1,1], target = 3   ->  5   (flip any one of the five 1s to minus)
 *   nums = [1],         target = 1   ->  1   (only +1)
 *   nums = [1,2],       target = 2   ->  0   (parity makes it impossible)
 *   nums = [0,0,1],     target = 1   ->  4   (+1 is forced, each 0 is free: 2 * 2)
 *
 * APPROACH  (re-label the signs as a subset-sum count, then pick/not-pick)
 *   1. Split nums into P (the numbers given '+') and N (the numbers given '-').
 *      sum(P) - sum(N) = target and sum(P) + sum(N) = totalSum.
 *      Subtracting gives sum(N) = (totalSum - target) / 2, a fixed non-negative number.
 *   2. So the question becomes: how many SUBSETS of nums add up to that value? Every
 *      such subset is exactly one valid sign assignment (that subset gets the minuses).
 *   3. Reject impossible inputs first: if totalSum - target is negative, or is odd,
 *      no integer split exists, so the answer is 0.
 *   4. Count subsets with the standard pick / not-pick recursion over (index, remaining):
 *        f(i, rem) = f(i-1, rem)                      // skip nums[i]
 *                  + f(i-1, rem - nums[i])            // take nums[i], if it fits
 *   5. Base case at index 0 is where zeros bite: if nums[0] == 0 and rem == 0 then BOTH
 *      taking and skipping the zero are distinct assignments, so it returns 2, not 1.
 *
 * KEY INSIGHT
 *   Sign problems are subset problems in disguise. Once you write sum(P) - sum(N) = target
 *   next to sum(P) + sum(N) = totalSum, the whole question collapses into "count subsets
 *   with sum (totalSum - target) / 2" - the tier-A knapsack template, unchanged. The two
 *   details that decide the interview are the parity/negativity rejection in step 3 and
 *   the zero handling in the base case.
 *
 * COMPLEXITY
 *   countWaysBruteForce : Time O(2^n),     Space O(n) recursion depth
 *   countWays (memoised): Time O(n * S),   Space O(n * S) table + O(n) stack,
 *                         where S = (totalSum - target) / 2
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why does a zero in nums double the count, and where exactly does the code say so?
 *   - Space-optimise the table to a single row of length S + 1.
 *   - Partition Equal Subset Sum (LeetCode 416) is the same table with target = 0.
 *   - What changes if nums may contain negative numbers? (the subset-sum reduction breaks)
 *
 * RUN
 *   main() runs 4 cases (typical, single element, parity-impossible, zeros) through both
 *   the brute-force and the memoised version and prints actual vs expected.
 */

import java.util.Arrays;

class TargetSumCountWays {

    /** Plain pick / not-pick recursion: count subsets of arr[0..ind] adding up to target. */
    private int countSubsetsBrute(int ind, int target, int[] arr) {
        if (ind == 0) {
            // A zero at index 0 can be taken or skipped and both give sum 0 -> two ways.
            if (target == 0 && arr[0] == 0) return 2;
            if (target == 0 || target == arr[0]) return 1;
            return 0;
        }

        int notTaken = countSubsetsBrute(ind - 1, target, arr);
        int taken = 0;
        if (arr[ind] <= target) {
            taken = countSubsetsBrute(ind - 1, target - arr[ind], arr);
        }
        return notTaken + taken;
    }

    /** Same recursion with a (index, target) memo table; -1 means "not computed yet". */
    private int countSubsetsMemo(int ind, int target, int[] arr, int[][] dp) {
        if (ind == 0) {
            if (target == 0 && arr[0] == 0) return 2;
            if (target == 0 || target == arr[0]) return 1;
            return 0;
        }
        if (dp[ind][target] != -1) return dp[ind][target];

        int notTaken = countSubsetsMemo(ind - 1, target, arr, dp);
        int taken = 0;
        if (arr[ind] <= target) {
            taken = countSubsetsMemo(ind - 1, target - arr[ind], arr, dp);
        }
        return dp[ind][target] = notTaken + taken;
    }

    /**
     * Turns the sign problem into a subset-sum target, or reports that it is impossible.
     * Returns -1 when no assignment can work, otherwise the subset sum to hunt for.
     */
    private int subsetSumTarget(int[] nums, int target) {
        int totalSum = 0;
        for (int value : nums) {
            totalSum += value;
        }

        int remainder = totalSum - target;   // this is 2 * sum(negatives)
        if (remainder < 0) return -1;        // target is bigger than everything we have
        if (remainder % 2 == 1) return -1;   // cannot split an odd number into two equal halves
        return remainder / 2;
    }

    /** Exponential reference implementation - correct, but only usable on tiny inputs. */
    public int countWaysBruteForce(int[] nums, int target) {
        int subsetTarget = subsetSumTarget(nums, target);
        if (subsetTarget < 0) return 0;
        return countSubsetsBrute(nums.length - 1, subsetTarget, nums);
    }

    /** The version to write in an interview: same recursion, memoised to O(n * S). */
    public int countWays(int[] nums, int target) {
        int subsetTarget = subsetSumTarget(nums, target);
        if (subsetTarget < 0) return 0;

        int[][] dp = new int[nums.length][subsetTarget + 1];
        for (int[] row : dp) {
            Arrays.fill(row, -1);
        }
        return countSubsetsMemo(nums.length - 1, subsetTarget, nums, dp);
    }

    private static void runCase(TargetSumCountWays sol, String label,
                                int[] nums, int target, int expected) {
        System.out.println(label + " nums=" + Arrays.toString(nums) + " target=" + target);
        System.out.println("  brute force: " + sol.countWaysBruteForce(nums, target)
                + "   expected " + expected);
        System.out.println("  memoised   : " + sol.countWays(nums, target)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        TargetSumCountWays sol = new TargetSumCountWays();

        // Case 1 - typical: flip exactly one of the five 1s to minus
        runCase(sol, "case 1", new int[]{1, 1, 1, 1, 1}, 3, 5);

        // Case 2 - edge: a single number, only "+1" reaches the target
        runCase(sol, "case 2", new int[]{1}, 1, 1);

        // Case 3 - edge: totalSum - target = 1 is odd, so no split exists at all
        runCase(sol, "case 3", new int[]{1, 2}, 2, 0);

        // Case 4 - tricky: the leading zeros each double the count (2 * 2 * one way for 1)
        runCase(sol, "case 4", new int[]{0, 0, 1}, 1, 4);
    }
}

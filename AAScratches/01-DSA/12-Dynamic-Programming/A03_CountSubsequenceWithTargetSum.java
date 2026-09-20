/*
 * =====================================================================
 *  Count Subsequences With Target Sum            Classic | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of non-negative ints and a target, count how many
 *   subsequences (any subset of positions, order kept, not necessarily
 *   contiguous) add up exactly to the target. The empty subsequence counts
 *   when target is 0. Two subsets at different positions count separately
 *   even if they hold the same values.
 *
 * EXAMPLE
 *   nums = [2, 3, 5, 6, 8, 10], target = 10  ->  3    {2,8}, {10}, {2,3,5}
 *   nums = [2, 3, 5, 6, 8, 10], target = 0   ->  1    the empty subsequence
 *   nums = [0, 0, 1],           target = 1   ->  4    {1} and each way to add
 *                                                    zero, one or both zeros
 *
 * APPROACH  (pick / not-pick recursion carrying the remaining target)
 *   1. State is (ind, remaining): how many subsequences of nums[ind..n-1] add
 *      up to remaining. The answer is the state (0, target).
 *   2. If remaining < 0 the branch is dead, return 0. This prune is only sound
 *      because the values are non-negative - nothing later can pull the sum
 *      back up.
 *   3. If ind == n there is nothing left to choose, so the branch is a success
 *      exactly when remaining == 0: return 1, else 0.
 *   4. Otherwise add the two disjoint branches: PICK nums[ind] and recurse on
 *      (ind + 1, remaining - nums[ind]), plus NOT-PICK and recurse on
 *      (ind + 1, remaining).
 *
 *   Fixed: the original returned 1 the moment remaining hit 0, before reaching
 *   the end of the array. With a 0 in the array that skips the choices left
 *   over, e.g. [1, 0] with target 1 returned 1 instead of 2. Testing remaining
 *   only at ind == n keeps the same algorithm and counts zeros correctly.
 *
 * KEY INSIGHT
 *   Every subsequence problem is the same two-way branch - take this element or
 *   do not - and the only design choice is what you carry in the state. Here it
 *   is the remaining target, which turns an exponential enumeration into a
 *   function of (index, remaining) with O(n * target) distinct values. That is
 *   exactly why it can be memoised: bounded state, not the number of paths.
 *   Recognise it whenever the branches must be ADDED rather than max'd.
 *
 * COMPLEXITY
 *   Time  O(2^n) as written - every pick/not-pick path is walked.
 *   Space O(n)   recursion stack depth, one frame per index.
 *   With a memo on (ind, remaining) it becomes O(n * target) time and space.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Memoise (ind, remaining), then flatten to a bottom-up dp[n+1][target+1],
 *     then to a single rolling row - the standard three-step upgrade.
 *   - Return true/false instead of a count -> A06 Subset Sum.
 *   - Maximise value under a weight cap -> A07 0/1 Knapsack, same state.
 *   - Negative numbers allowed? The remaining < 0 prune breaks; you must shift
 *     the sum range or memoise with a HashMap keyed on (ind, remaining).
 *
 * RUN
 *   main() runs 3 cases (typical, target 0, an array with zeros) and prints
 *   actual vs expected.
 */

class CountSubsequenceWithTargetSum {

    /** Counts subsequences of nums[ind..] that sum to remaining. */
    private int func(int ind, int remaining, int[] nums) {
        // Values are non-negative, so an overshoot can never be repaired.
        if (remaining < 0) return 0;

        // Nothing left to choose: this path succeeded only if the target is met.
        if (ind == nums.length) return remaining == 0 ? 1 : 0;

        int pick = func(ind + 1, remaining - nums[ind], nums);
        int notPick = func(ind + 1, remaining, nums);
        return pick + notPick;      // disjoint choices, so the counts add
    }

    public int countSubsequenceWithTargetSum(int[] nums, int target) {
        return func(0, target, nums);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        CountSubsequenceWithTargetSum counter = new CountSubsequenceWithTargetSum();

        int[] nums = {2, 3, 5, 6, 8, 10};
        print("case 1 target 10       ", counter.countSubsequenceWithTargetSum(nums, 10), 3);

        // Edge: only the empty subsequence sums to 0.
        print("case 2 target 0        ", counter.countSubsequenceWithTargetSum(nums, 0), 1);

        // Tricky: each zero doubles the count, which the old early exit lost.
        int[] withZeros = {0, 0, 1};
        print("case 3 zeros, target 1 ", counter.countSubsequenceWithTargetSum(withZeros, 1), 4);
    }
}

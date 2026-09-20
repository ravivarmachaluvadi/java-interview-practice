/*
 * =====================================================================
 *  Subset Sum Equals Target                      Classic | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of non-negative ints and a target, decide whether ANY
 *   subset of the array adds up to exactly the target. Return true or false -
 *   the subset itself is not required. The empty subset makes target 0 always
 *   reachable, including for an empty array.
 *
 * EXAMPLE
 *   arr = [1, 2, 3, 4], target = 4   ->  true    {4}, also {1,3}
 *   arr = [2, 4, 6],    target = 5   ->  false   every subset sum is even
 *   arr = [],           target = 4   ->  false   nothing to pick (edge case)
 *   arr = [],           target = 0   ->  true    the empty subset
 *
 * APPROACH  (pick / not-pick recursion, boolean subset sum)
 *   1. State is (ind, target): can some subset of arr[0..ind] reach target?
 *      The recursion walks the array BACKWARDS from the last index.
 *   2. If target == 0 the answer is already yes - stop and return true.
 *   3. If ind < 0 there is nothing left to pick and target is still positive,
 *      so this branch fails: return false.
 *   4. NOT-TAKEN: recurse on (ind - 1, target).
 *      TAKEN: only if arr[ind] <= target, recurse on (ind - 1, target-arr[ind]).
 *   5. Return notTaken OR taken. Java's || short-circuits, so a success on the
 *      first branch never explores the second.
 *
 *   Fixed: the base case used to be "if (ind == 0) return arr[0] == target",
 *   which an empty array skips past into ind = -1 and then recurses forever
 *   (StackOverflowError). Testing ind < 0 covers the empty array and makes the
 *   single-element case fall out of the general rule.
 *
 * KEY INSIGHT
 *   This is A03_CountSubsequenceWithTargetSum with the transition swapped:
 *   counting used +, deciding uses OR. The state, the branching and the base
 *   cases are identical. That is the lesson to carry - once you own the
 *   (index, remaining) skeleton, a new problem usually only changes what you
 *   combine the two branches WITH: sum for counts, OR for feasibility, max for
 *   value, min for cost. Recognise the skeleton and you have already written
 *   most of the answer.
 *
 * COMPLEXITY
 *   Time  O(2^n) as written - each index branches twice.
 *   Space O(n)   recursion stack, one frame per index.
 *   Memoised on (ind, target) it becomes O(n * target) time and space, which is
 *   pseudo-polynomial: it scales with the VALUE of target, not its bit length.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Memoise (ind, target), then bottom-up dp[n][target+1], then one rolling
 *     boolean row of size target+1.
 *   - Partition Equal Subset Sum (LC 416): run this with target = totalSum / 2,
 *     answering false immediately when totalSum is odd.
 *   - Return the subset itself: keep parent pointers, or re-walk the dp table.
 *   - Negative values break the arr[ind] <= target guard; shift the sum range
 *     or memoise in a HashMap instead.
 *
 * RUN
 *   main() runs 4 cases (typical hit, unreachable target, empty array with a
 *   positive target, empty array with target 0) and prints actual vs expected.
 */

class SubsetSumEqualsToTarget {

    /** True if some subset of arr[0..ind] sums to target. */
    private boolean func(int ind, int target, int[] arr) {
        if (target == 0) return true;   // empty remainder is a valid subset
        if (ind < 0) return false;      // out of elements with target still unmet

        boolean notTaken = func(ind - 1, target, arr);

        // Values are non-negative, so an element bigger than target can never help.
        boolean taken = false;
        if (arr[ind] <= target)
            taken = func(ind - 1, target - arr[ind], arr);

        return notTaken || taken;
    }

    public boolean isSubsetSum(int[] arr, int target) {
        return func(arr.length - 1, target, arr);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        SubsetSumEqualsToTarget sol = new SubsetSumEqualsToTarget();

        print("case 1 [1,2,3,4] t=4  ", sol.isSubsetSum(new int[]{1, 2, 3, 4}, 4), true);
        print("case 2 [2,4,6]   t=5  ", sol.isSubsetSum(new int[]{2, 4, 6}, 5), false);
        print("case 3 []        t=4  ", sol.isSubsetSum(new int[]{}, 4), false);
        print("case 4 []        t=0  ", sol.isSubsetSum(new int[]{}, 0), true);
    }
}

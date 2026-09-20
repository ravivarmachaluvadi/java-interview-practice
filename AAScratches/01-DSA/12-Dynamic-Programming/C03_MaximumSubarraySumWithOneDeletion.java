/*
 * =====================================================================
 *  Maximum Subarray Sum With One Deletion         LeetCode 1186 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array, pick a contiguous subarray and optionally delete one
 *   element from it; return the largest sum achievable. What is left after the
 *   deletion must still be non-empty, so an all-negative array cannot answer 0 -
 *   it has to keep its least bad single element.
 *
 * EXAMPLE
 *   arr = [1, -2, 0, 3]      -> 4    take [1, -2, 0, 3], delete -2  => 1 + 0 + 3
 *   arr = [1, -2, -2, 3]     -> 3    delete nothing, take [3]
 *   arr = [-1, -1, -1, -1]   -> -1   every subarray is negative; keep one element
 *   arr = [5]                -> 5    single element, nothing may be deleted
 *
 * APPROACH  (two-state Kadane)
 *   1. Scan left to right carrying two running values for each index i:
 *        noDelete[i]   - best sum of a subarray ending at i with no deletion
 *        withDelete[i] - best sum of a subarray ending at i with exactly one deleted
 *   2. Transitions:
 *        noDelete[i]   = max(noDelete[i-1] + arr[i], arr[i])
 *                        (extend the previous run, or start a new one at i)
 *        withDelete[i] = max(noDelete[i-1],              // delete arr[i] itself
 *                            withDelete[i-1] + arr[i])   // deletion already spent
 *   3. Seed noDelete[0] = arr[0]; withDelete[0] is impossible (deleting the only
 *      element leaves nothing), so it gets a large negative sentinel.
 *   4. The answer is the maximum seen over both rows, not just the last cell.
 *
 * KEY INSIGHT
 *   "At most one X allowed" becomes a second parallel Kadane lane: one lane for
 *   the budget unspent, one for the budget spent, with the only crossing being
 *   the step where you spend it. The same trick handles "at most one swap",
 *   "at most k transactions", "one free move".
 *
 * COMPLEXITY
 *   Time  O(n)   single pass, constant work per index
 *   Space O(n)   the two arrays; two ints would make it O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Generalise to deleting at most k elements: k + 1 lanes, dp[i][j].
 *   - Return the indices of the subarray and the deleted element.
 *   - Maximum Subarray (LeetCode 53): the one-lane version this extends.
 *   - Rewrite it with two ints instead of two arrays.
 *
 * RUN
 *   main() runs 4 cases (typical, no deletion needed, all negative, single element)
 *   and prints actual vs expected.
 *
 * Fixed: the original did not compile - the closing parenthesis of the withDelete
 *        Math.max call sat inside a trailing // comment and was swallowed.
 *        Also, withDelete[0] was Integer.MIN_VALUE, and "withDelete[i-1] + arr[i]"
 *        on a negative arr[i] overflows to a large positive. A MIN_VALUE / 4
 *        sentinel stays unreachable without wrapping around.
 */

import java.util.Arrays;

class MaximumSubarraySumWithOneDeletion {

    /** Unreachable but safe to add to: no overflow when a negative is added. */
    private static final int IMPOSSIBLE = Integer.MIN_VALUE / 4;

    public static int maximumSum(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        int[] noDelete = new int[arr.length];
        int[] withDelete = new int[arr.length];

        noDelete[0] = arr[0];
        withDelete[0] = IMPOSSIBLE;   // deleting the only element leaves an empty subarray

        int maxSum = arr[0];

        for (int i = 1; i < arr.length; i++) {
            // extend the running subarray, or restart it at arr[i]
            noDelete[i] = Math.max(noDelete[i - 1] + arr[i], arr[i]);

            withDelete[i] = Math.max(
                    noDelete[i - 1],                 // spend the deletion on arr[i]
                    withDelete[i - 1] + arr[i]);     // deletion already used earlier

            maxSum = Math.max(maxSum, Math.max(noDelete[i], withDelete[i]));
        }

        return maxSum;
    }

    private static void print(String label, int[] arr, int expected) {
        System.out.println(label + " " + Arrays.toString(arr)
                + " -> " + maximumSum(arr) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1:", new int[]{1, -2, 0, 3}, 4);
        print("case 2 (no deletion helps):", new int[]{1, -2, -2, 3}, 3);
        print("case 3 (all negative):", new int[]{-1, -1, -1, -1}, -1);
        print("case 4 (single):", new int[]{5}, 5);
    }
}

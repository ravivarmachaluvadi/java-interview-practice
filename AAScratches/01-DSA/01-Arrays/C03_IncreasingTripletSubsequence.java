/*
 * =====================================================================
 *  Increasing Triplet Subsequence                   LeetCode 334 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array nums, return true if there are indices i < j < k with
 *   nums[i] < nums[j] < nums[k] (strictly increasing, not necessarily adjacent).
 *   The target is O(n) time and O(1) extra space; the input must not be modified.
 *
 * EXAMPLE
 *   nums = [2, 1, 5, 0, 4, 6]   ->  true    0 < 4 < 6
 *   nums = [5, 4, 3, 2, 1]      ->  false   strictly decreasing
 *   nums = [5, 6, 1, 7]         ->  true    5 < 6 < 7, even though 'first' goes stale
 *   nums = [1, 1, 2]            ->  false   duplicates are not strictly increasing
 *   nums = []                   ->  false   fewer than three elements
 *
 * APPROACH  (two sentinels, O(1) space)
 *   1. first  = smallest value seen so far (starts at Integer.MAX_VALUE).
 *   2. second = smallest value that has some smaller value BEFORE it (starts MAX).
 *   3. For each num:
 *        num <= first   -> lower first
 *        num <= second  -> lower second (num > first, so a smaller element precedes it)
 *        otherwise      -> num > second > something earlier: return true
 *   4. If the loop ends, return false.
 *
 * KEY INSIGHT
 *   Correctness rests on 'second' alone. It is assigned only when num > first, and
 *   that first came from an earlier index, so a finite second always has a smaller
 *   predecessor. Lowering first later (a "stale" first that sits AFTER second) cannot
 *   break that guarantee; it only makes future pairs easier to form. Both comparisons
 *   are non-strict (<=) so an equal value refreshes a sentinel instead of advancing:
 *   with strict <, [1, 1, 2] would set second = 1 and wrongly report a triplet.
 *   Pattern: keep the best endpoint of a chain of length 1 and of length 2; this is
 *   LIS "tails" (patience sorting) with k = 3 collapsed into two scalars.
 *
 * COMPLEXITY
 *   Time  O(n)  single pass
 *   Space O(1)  two ints; only existence is reported, indices are not recoverable
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the actual indices: remember the index of first that produced second,
 *     plus the index of second, and report them with the final k.
 *   - Generalise to an increasing subsequence of length k: LIS tails, O(n log k).
 *   - Why is [5, 6, 1, 7] still true when first = 1 sits after second = 6?
 *
 * RUN
 *   main() runs 5 cases (typical, decreasing, stale first, duplicates, empty)
 *   and prints actual vs expected.
 */
import java.util.Arrays;

class IncreasingTripletSubsequence {

    public static boolean increasingTriplet(int[] nums) {
        int first = Integer.MAX_VALUE;  // smallest value seen so far
        int second = Integer.MAX_VALUE; // smallest value with something smaller before it

        for (int num : nums) {
            if (num <= first) {
                first = num;
            } else if (num <= second) {
                second = num;           // num > first, so a smaller element precedes it
            } else {
                return true;            // num > second > (something earlier)
            }
        }
        return false;
    }

    public static void main(String[] args) {
        print(new int[]{2, 1, 5, 0, 4, 6}, true);
        print(new int[]{5, 4, 3, 2, 1}, false);
        print(new int[]{5, 6, 1, 7}, true);       // first goes stale, second still valid
        print(new int[]{1, 1, 2}, false);         // duplicates must not count
        print(new int[]{}, false);                // empty
    }

    private static void print(int[] nums, boolean expected) {
        System.out.printf("%-22s -> %-5b   expected %b%n",
                Arrays.toString(nums), increasingTriplet(nums), expected);
    }
}

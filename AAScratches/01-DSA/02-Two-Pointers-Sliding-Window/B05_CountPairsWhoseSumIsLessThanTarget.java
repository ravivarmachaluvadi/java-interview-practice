/*
 * =====================================================================
 *  Count Pairs Whose Sum is Less than Target         LeetCode 2824 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a list of integers nums and an integer target, count the index pairs (i, j) with
 *   i < j such that nums[i] + nums[j] < target. Values may be negative. Order of the input
 *   does not matter for the answer, so sorting is allowed.
 *
 * EXAMPLE
 *   nums = [-1, 1, 2, 3, 1],           target = 2   ->  3    pairs (0,1) (0,2) (0,4)
 *   nums = [-6, 2, 5, -2, -7, -1, 3],  target = -2  ->  10
 *   nums = [10, 1, 6, 2, 3, 8],        target = 9   ->  5
 *   nums = [7],                        target = 10  ->  0    (no pair possible)
 *   nums = [2, 2, 2],                  target = 5   ->  3    (all equal, every pair counts)
 *
 * APPROACH  (sort + converging pair, block count)
 *   1. Sort nums ascending.
 *   2. Put low at the first index and high at the last.
 *   3. If nums[low] + nums[high] < target, then nums[low] paired with EVERY index in
 *      (low, high] is also below target (those values are <= nums[high]). Add (high - low)
 *      to the count in one step and move low right.
 *   4. Otherwise the sum is too big; only shrinking the larger side can help, so move high left.
 *   5. Stop when low meets high.
 *
 * KEY INSIGHT
 *   One comparison settles a whole block of pairs. Because the array is sorted, when the
 *   smallest candidate plus the current largest is already under target, everything in
 *   between is too, so count += (high - low) instead of checking each pair. Recognise this
 *   "count a block, then advance" shape: it turns an O(n^2) pair scan into O(n) after sorting.
 *
 * COMPLEXITY
 *   Time  O(n log n)  dominated by the sort; the two-pointer sweep is O(n)
 *   Space O(1)        extra, beyond the in-place sort (it mutates the input list)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Count pairs with sum <= target, == target, or in a range [lo, hi] (two sweeps, subtract)
 *   - Count pairs with sum >= target (mirror: when sum >= target, count += high - low, high--)
 *   - Input must not be mutated: sort a copy, or use a frequency map when values are bounded
 *   - Same block-count trick with 3Sum Smaller (LeetCode 259): fix one, converge on the rest
 *
 * RUN
 *   main() runs 5 cases (two LeetCode examples, a typical unsorted case, a single element,
 *   all-equal values) and prints actual vs expected.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class CountPairsWhoseSumIsLessThanTarget {

    public static int countPairs(List<Integer> nums, int target) {
        Collections.sort(nums); // sorted order is what makes the block count valid
        int count = 0;
        int low = 0, high = nums.size() - 1;

        while (low < high) {
            if (nums.get(low) + nums.get(high) < target) {
                // nums[low] + nums[x] < target for every x in (low, high], so count them all
                count += (high - low);
                low++;
            } else {
                high--; // sum too big; the only way down is a smaller high
            }
        }
        return count;
    }

    private static void run(String label, Integer[] nums, int target, int expected) {
        List<Integer> list = new ArrayList<>(Arrays.asList(nums)); // fresh, mutable copy
        System.out.println(label + ": " + countPairs(list, target) + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 leetcode ex1 ", new Integer[]{-1, 1, 2, 3, 1}, 2, 3);
        run("case 2 leetcode ex2 ", new Integer[]{-6, 2, 5, -2, -7, -1, 3}, -2, 10);
        run("case 3 unsorted     ", new Integer[]{10, 1, 6, 2, 3, 8}, 9, 5);
        run("case 4 single       ", new Integer[]{7}, 10, 0);
        run("case 5 all equal    ", new Integer[]{2, 2, 2}, 5, 3);
    }
}

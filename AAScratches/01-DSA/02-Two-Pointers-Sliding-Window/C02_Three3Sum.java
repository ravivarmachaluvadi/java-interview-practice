/*
 * =====================================================================
 *  3Sum                                      LeetCode 15 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array nums, return every distinct triplet [a, b, c] with
 *   a + b + c == 0, taken from three different indices. The output must not
 *   contain duplicate triplets, and order inside a triplet does not matter.
 *
 * EXAMPLE
 *   nums = [-1, 0, 1, 2, -1, -4]  ->  [[-1, -1, 2], [-1, 0, 1]]
 *   nums = [0, 0, 0, 0]           ->  [[0, 0, 0]]      duplicates reported once
 *   nums = [1, 2, 3]              ->  []               all positive, no answer
 *   nums = []                     ->  []
 *
 * APPROACH  (sort, fix one index, converge on the other two)
 *   1. Sort nums so equal values sit together and the pair search can be directed.
 *   2. For each i, skip it if nums[i] == nums[i-1]: that first value already
 *      produced every triplet starting with it.
 *   3. With i fixed, run converging pointers j = i+1, k = n-1 on the rest:
 *        sum < 0  -> j++   (need a bigger number)
 *        sum > 0  -> k--   (need a smaller number)
 *        sum == 0 -> record, move both, then skip equal neighbours on each side.
 *   4. Collect all triplets found.
 *
 * KEY INSIGHT
 *   Sorting turns "find a pair with a given sum" into a two-pointer walk, and it
 *   also makes duplicates adjacent so they can be skipped in O(1) per step. The
 *   shape "outer loop fixes one element, inner two pointers solve the rest" is
 *   what 3Sum Closest, 4Sum and general kSum are built from.
 *
 * COMPLEXITY
 *   Time  O(n^2)  n choices of i, each with an O(n) sweep (the sort is O(n log n))
 *   Space O(1)    extra beyond the output list (sort may use O(log n) stack)
 *
 * INTERVIEW FOLLOW-UPS
 *   - 3Sum Closest (LC 16): same loop, track the sum nearest to target.
 *   - 4Sum / kSum: add one more fixed index per level; recursion down to 2Sum.
 *   - Early exit: once nums[i] > 0 no triplet can sum to zero (sorted array).
 *   - Why not a HashSet for the inner pair? Works but dedup becomes awkward.
 *
 * RUN
 *   main() runs 4 cases (typical, all zeros, no answer, empty) and prints
 *   actual vs expected.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Three3Sum {

    public static List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        int n = nums.length;

        for (int i = 0; i < n; i++) {
            // Same first value as last round: every triplet with it was already found.
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            int j = i + 1;
            int k = n - 1;
            while (j < k) {
                int sum = nums[i] + nums[j] + nums[k];
                if (sum < 0) {
                    j++;
                } else if (sum > 0) {
                    k--;
                } else {
                    result.add(Arrays.asList(nums[i], nums[j], nums[k]));
                    j++;
                    k--;
                    // Skip runs of equal values so the same triplet is not added again.
                    while (j < k && nums[j] == nums[j - 1]) j++;
                    while (j < k && nums[k] == nums[k + 1]) k--;
                }
            }
        }
        return result;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical   ", threeSum(new int[]{-1, 0, 1, 2, -1, -4}),
                "[[-1, -1, 2], [-1, 0, 1]]");
        print("case 2 all zeros ", threeSum(new int[]{0, 0, 0, 0}), "[[0, 0, 0]]");
        print("case 3 no answer ", threeSum(new int[]{1, 2, 3}), "[]");
        print("case 4 empty     ", threeSum(new int[]{}), "[]");
    }
}

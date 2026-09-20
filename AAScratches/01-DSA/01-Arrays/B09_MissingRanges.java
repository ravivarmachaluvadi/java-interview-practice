/*
 * =====================================================================
 *  Missing Ranges                                    LeetCode 163 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a sorted array of unique integers nums and an inclusive range [lower, upper],
 *   where every value of nums lies inside that range, return the list of maximal ranges
 *   [start, end] that cover exactly the numbers in [lower, upper] that are NOT in nums.
 *   Output ranges are sorted and non-overlapping.
 *
 * EXAMPLE
 *   nums = [0, 1, 3, 50, 75], lower = 0, upper = 99  ->  [[2, 2], [4, 49], [51, 74], [76, 99]]
 *   nums = [],  lower = 1, upper = 1                 ->  [[1, 1]]          (everything missing)
 *   nums = [1, 2, 3], lower = 1, upper = 3           ->  []                (nothing missing)
 *   nums = [-1], lower = -1, upper = -1              ->  []                (single, fills range)
 *
 * APPROACH  (gap enumeration with explicit boundaries)
 *   1. Empty nums: the whole [lower, upper] is one missing range; return it.
 *   2. Before the first element: if lower < nums[0], add [lower, nums[0] - 1].
 *   3. Between neighbours: for each i, if nums[i+1] - nums[i] > 1, add
 *      [nums[i] + 1, nums[i+1] - 1]. A difference of exactly 1 means no gap.
 *   4. After the last element: if upper > nums[n-1], add [nums[n-1] + 1, upper].
 *
 * KEY INSIGHT
 *   There is no clever idea here; the whole problem is edge-case discipline. Treat the
 *   three regions (before first, between pairs, after last) as separate checks and the
 *   empty array as its own case. A common trick that folds all four into one loop is to
 *   pretend the array is [lower - 1, ...nums, upper + 1], but that overflows at int limits.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass over nums
 *   Space O(1)  extra, beyond the output list
 *
 * INTERVIEW FOLLOW-UPS
 *   - Older LeetCode wording wants strings: "2" for a single value, "4->49" for a span.
 *   - What if nums may contain duplicates? The difference check (<= 1) already skips them.
 *   - What if lower/upper can be Integer.MIN_VALUE/MAX_VALUE? Use long for the arithmetic,
 *     or the sentinel trick above breaks.
 *
 * RUN
 *   main() runs 4 cases (typical, empty, no gaps, single) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MissingRanges {

    public static List<List<Integer>> findMissingRanges(int[] nums, int lower, int upper) {
        List<List<Integer>> missing = new ArrayList<>();
        int n = nums.length;
        if (n == 0) {
            missing.add(List.of(lower, upper));
            return missing;
        }

        // gap before the first element
        if (lower < nums[0]) {
            missing.add(List.of(lower, nums[0] - 1));
        }

        // gaps between consecutive elements; a difference of 1 means adjacent, no gap
        for (int i = 0; i < n - 1; i++) {
            if (nums[i + 1] - nums[i] <= 1) {
                continue;
            }
            missing.add(List.of(nums[i] + 1, nums[i + 1] - 1));
        }

        // gap after the last element
        if (upper > nums[n - 1]) {
            missing.add(List.of(nums[n - 1] + 1, upper));
        }
        return missing;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical ", findMissingRanges(new int[]{0, 1, 3, 50, 75}, 0, 99),
                "[[2, 2], [4, 49], [51, 74], [76, 99]]");
        print("case 2 empty   ", findMissingRanges(new int[]{}, 1, 1), "[[1, 1]]");
        print("case 3 no gaps ", findMissingRanges(new int[]{1, 2, 3}, 1, 3), "[]");
        print("case 4 single  ", findMissingRanges(new int[]{-1}, -1, -1), "[]");
    }
}

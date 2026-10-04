/*
 * =====================================================================
 *  P011 Two Pointers: Opposite Ends on Sorted   Canonical LC 167 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 167, Two Sum II - Input Array Is Sorted)
 *   numbers is sorted ascending. Return the 1-based indices [i, j] (i < j) of the two
 *   numbers that add up to target. Exactly one answer exists. Use O(1) extra space.
 *
 * EXAMPLE
 *   [2, 7, 11, 15], target 9   ->  [1, 2]
 *   [2, 3, 4],      target 6   ->  [1, 3]
 *   [-1, 0],        target -1  ->  [1, 2]
 *
 * RECOGNIZE WHEN
 *   - The input is sorted (or you are allowed to sort it) and you look for a PAIR or
 *     TRIPLE meeting a sum / difference / product condition.
 *   - "Container", "area between two lines": the answer depends on both ends at once.
 *   - k-Sum: fix k - 2 values with loops, finish with one two-pointer sweep.
 *   Not this if: the array is unsorted and indices must be returned -> P020_ComplementLookup
 *   (sorting would scramble the indices); you need a contiguous range -> sliding window.
 *
 * TEMPLATE
 *   lo = 0, hi = n - 1
 *   while lo < hi:
 *       s = a[lo] + a[hi]
 *       if s == target: record; lo++; hi--  (skip duplicates if results must be unique)
 *       elif s < target: lo++              // need bigger: only lo can grow the sum
 *       else:            hi--              // need smaller: only hi can shrink it
 *
 * APPROACH
 *   1. Start with the smallest and the largest value.
 *   2. Too small -> move lo right; too large -> move hi left; equal -> done.
 *
 * KEY INSIGHT
 *   When a[lo] + a[hi] is too small, a[lo] cannot pair with ANY remaining value (a[hi] is
 *   the biggest left), so discarding it loses nothing. Each step safely eliminates one
 *   candidate, which turns O(n^2) pair checking into O(n).
 *
 * COMPLEXITY
 *   Pair: O(n) time, O(1) space. 3Sum: O(n^2) after an O(n log n) sort.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 15   3Sum                     sort, fix i, two-pointer the rest; skip equal
 *                                            values at i, lo and hi for unique triples
 *   [coded] LC 16   3Sum Closest             same sweep, track min |sum - target|
 *   [coded] LC 11   Container With Most Water area = min(h) * width; move the SHORTER side
 *           LC 18   4Sum                     two outer loops + the same sweep; use long sums
 *           LC 259  3Sum Smaller             if sum < target, all hi' in (lo, hi] work:
 *                                            count += hi - lo, then lo++
 *           LC 2824 Count Pairs Less Target  the counting trick of LC 259 for pairs
 *           LC 977  Squares of Sorted Array  biggest square is at one of the ends; fill the
 *                                            output from the back
 *           LC 881  Boats to Save People     -> P096_SortAndPair
 *
 * PITFALLS
 *   - 3Sum duplicates: skip a[i] == a[i - 1] for the fixed value, and after a hit move lo
 *     and hi past equal neighbours, or the same triple is reported twice.
 *   - Return the ORIGINAL indices in Two Sum (unsorted): sorting loses them.
 *   - 4Sum overflow: four ints near 10^9 overflow an int sum.
 *
 * DEEP DIVE
 *   C02_Three3Sum, C01_ContainerWithMostWater, B01_SquaresOfASortedArray,
 *   B05_CountPairsWhoseSumIsLessThanTarget (02-Two-Pointers-Sliding-Window)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class OppositeEndsSorted {

    // Canonical LC 167.
    static int[] twoSum(int[] numbers, int target) {
        int lo = 0;
        int hi = numbers.length - 1;
        while (lo < hi) {
            int s = numbers[lo] + numbers[hi];
            if (s == target) {
                return new int[]{lo + 1, hi + 1};
            } else if (s < target) {
                lo++;
            } else {
                hi--;
            }
        }
        return new int[0];
    }

    // LC 15: fix the smallest value of the triple, two-pointer the rest.
    static List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> out = new ArrayList<>();
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;                              // same fixed value -> same triples
            }
            if (nums[i] > 0) {
                break;                                 // three positives cannot sum to 0
            }
            int lo = i + 1;
            int hi = nums.length - 1;
            while (lo < hi) {
                int s = nums[i] + nums[lo] + nums[hi];
                if (s == 0) {
                    out.add(Arrays.asList(nums[i], nums[lo], nums[hi]));
                    while (lo < hi && nums[lo] == nums[lo + 1]) {
                        lo++;
                    }
                    while (lo < hi && nums[hi] == nums[hi - 1]) {
                        hi--;
                    }
                    lo++;
                    hi--;
                } else if (s < 0) {
                    lo++;
                } else {
                    hi--;
                }
            }
        }
        return out;
    }

    // LC 16: same sweep; remember the sum closest to target.
    static int threeSumClosest(int[] nums, int target) {
        Arrays.sort(nums);
        int best = nums[0] + nums[1] + nums[2];
        for (int i = 0; i < nums.length - 2; i++) {
            int lo = i + 1;
            int hi = nums.length - 1;
            while (lo < hi) {
                int s = nums[i] + nums[lo] + nums[hi];
                if (Math.abs(s - target) < Math.abs(best - target)) {
                    best = s;
                }
                if (s == target) {
                    return s;
                } else if (s < target) {
                    lo++;
                } else {
                    hi--;
                }
            }
        }
        return best;
    }

    // LC 11: the shorter line caps the area; moving the taller one can never help.
    static int maxArea(int[] height) {
        int lo = 0;
        int hi = height.length - 1;
        int best = 0;
        while (lo < hi) {
            best = Math.max(best, Math.min(height[lo], height[hi]) * (hi - lo));
            if (height[lo] < height[hi]) {
                lo++;
            } else {
                hi--;
            }
        }
        return best;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 167 [2,7,11,15] t=9",
                Arrays.toString(twoSum(new int[]{2, 7, 11, 15}, 9)), "[1, 2]");
        check("LC 167 [2,3,4] t=6", Arrays.toString(twoSum(new int[]{2, 3, 4}, 6)), "[1, 3]");
        check("LC 167 [-1,0] t=-1", Arrays.toString(twoSum(new int[]{-1, 0}, -1)), "[1, 2]");

        check("LC 15 [-1,0,1,2,-1,-4]",
                threeSum(new int[]{-1, 0, 1, 2, -1, -4}), "[[-1, -1, 2], [-1, 0, 1]]");
        check("LC 15 [0,1,1]", threeSum(new int[]{0, 1, 1}), "[]");
        check("LC 15 [0,0,0,0] duplicates", threeSum(new int[]{0, 0, 0, 0}), "[[0, 0, 0]]");
        check("LC 15 [-2,0,0,2,2]", threeSum(new int[]{-2, 0, 0, 2, 2}), "[[-2, 0, 2]]");

        check("LC 16 [-1,2,1,-4] t=1", threeSumClosest(new int[]{-1, 2, 1, -4}, 1), 2);
        check("LC 16 [0,0,0] t=1", threeSumClosest(new int[]{0, 0, 0}, 1), 0);

        check("LC 11 [1,8,6,2,5,4,8,3,7]", maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}), 49);
        check("LC 11 [1,1]", maxArea(new int[]{1, 1}), 1);
    }
}

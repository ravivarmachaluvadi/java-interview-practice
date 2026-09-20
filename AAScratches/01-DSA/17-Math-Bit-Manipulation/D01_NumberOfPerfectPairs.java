/*
 * =====================================================================
 *  Number of Perfect Pairs                        LeetCode 3649 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array nums, count index pairs (i, j) with i < j that are
 *   "perfect":  min(|a-b|, |a+b|) <= min(|a|, |b|)  AND
 *               max(|a-b|, |a+b|) >= max(|a|, |b|),  where a = nums[i], b = nums[j].
 *   Values may be negative or zero and the count can exceed int range, so the
 *   answer is a long.
 *
 * EXAMPLE
 *   nums = [-3, 1, 2, -2, 4]  ->  8    absolute values sort to [1, 2, 2, 3, 4]
 *   nums = [0, 0, 0]          ->  3    edge case: every pair of zeros qualifies
 *   nums = [7]                ->  0    edge case: a pair needs two elements
 *
 * APPROACH  (algebraic reduction, then sort + two pointers)
 *   1. Reduce the condition. Write a = |nums[i]|, b = |nums[j]| and assume
 *      a <= b. Whatever the original signs, {|a-b|, |a+b|} is {b-a, b+a}, so
 *      the min side says b - a <= a, i.e. b <= 2a, and the max side says
 *      b + a >= b, which is always true. Signs therefore do not matter at all.
 *   2. So: replace every value by its absolute value and sort ascending.
 *   3. Sweep j from left to right keeping a left pointer `lo`. Advance `lo`
 *      while nums[j] > 2 * nums[lo], because that partner is too small.
 *   4. Every index in [lo, j) now pairs with j, so add (j - lo) to the answer.
 *      `lo` never moves backwards, so the sweep is linear after the sort.
 *   5. Fixed: the original version sorted and took absolute values in the
 *      caller's array, silently destroying the input. We copy first.
 *
 * KEY INSIGHT
 *   The whole problem is one line of algebra: a perfect pair is just
 *   max(|a|, |b|) <= 2 * min(|a|, |b|). Once the condition is monotone in the
 *   sorted order ("as j grows, the smallest legal partner only moves right"),
 *   the two-pointer window is forced. Pattern to recognise: simplify the
 *   predicate before you design the algorithm, not after.
 *
 * COMPLEXITY
 *   Time  O(n log n)  dominated by the sort; the two-pointer sweep is O(n)
 *   Space O(n)        the defensive copy of the input (sort itself is in place)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why can the answer overflow int? n = 1e5 all equal gives ~5e9 pairs.
 *   - Replace the two pointers with a binary search per j: still O(n log n),
 *     but why is the sweep preferable?
 *   - Values bounded by 1e5: counting-sort them and answer with a prefix sum.
 *   - Math.abs(Integer.MIN_VALUE) is still negative -- what breaks, and how
 *     would you handle it if the constraints allowed that value?
 *
 * RUN
 *   main() runs 5 cases (typical, all zeros, mixed signs, single element,
 *   large values). Each line prints the O(n log n) answer, the O(n^2)
 *   brute-force answer from the raw definition, and the expected value.
 */

import java.util.Arrays;

class NumberOfPerfectPairs {

    /** Sort the absolute values, then count pairs with max <= 2 * min. */
    public static long perfectPairs(int[] nums) {
        int n = nums.length;

        // Copy before mutating: the caller's array must survive this call.
        int[] magnitudes = new int[n];
        for (int i = 0; i < n; i++) {
            magnitudes[i] = Math.abs(nums[i]); // signs are provably irrelevant
        }
        Arrays.sort(magnitudes);

        int lo = 0;       // smallest index that can still partner the current j
        long pairs = 0;

        for (int j = 0; j < n; j++) {
            // magnitudes[lo] is too small when magnitudes[j] > 2 * magnitudes[lo].
            while (magnitudes[j] - magnitudes[lo] > magnitudes[lo]) {
                lo++;
            }
            pairs += j - lo; // every index in [lo, j) forms a perfect pair with j
        }
        return pairs;
    }

    /** O(n^2) check straight from the problem statement, used to verify the reduction. */
    public static long perfectPairsBruteForce(int[] nums) {
        long pairs = 0;
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                long a = nums[i];
                long b = nums[j];
                long diff = Math.abs(a - b);
                long sum = Math.abs(a + b);
                if (Math.min(diff, sum) <= Math.min(Math.abs(a), Math.abs(b))
                        && Math.max(diff, sum) >= Math.max(Math.abs(a), Math.abs(b))) {
                    pairs++;
                }
            }
        }
        return pairs;
    }

    private static void printCase(String label, int[] nums, long expected) {
        int[] original = nums.clone();
        long fast = perfectPairs(nums);
        long slow = perfectPairsBruteForce(nums);
        boolean inputIntact = Arrays.equals(original, nums);
        System.out.println(label + Arrays.toString(original)
                + "  ->  two-pointer " + fast
                + ", brute force " + slow
                + ", input unchanged " + inputIntact
                + "   expected " + expected + ", " + expected + ", true");
    }

    public static void main(String[] args) {
        printCase("case 1 (typical):   ", new int[]{-3, 1, 2, -2, 4}, 8);
        printCase("case 2 (all zeros): ", new int[]{0, 0, 0}, 3);
        printCase("case 3 (mixed):     ", new int[]{5, -1, 3, -2}, 3);
        printCase("case 4 (single):    ", new int[]{7}, 0);
        printCase("case 5 (large):     ", new int[]{1000000000, -1000000000}, 1);
    }
}

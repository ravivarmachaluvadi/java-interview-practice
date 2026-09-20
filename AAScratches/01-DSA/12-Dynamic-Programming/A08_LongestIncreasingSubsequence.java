/*
 * =====================================================================
 *  Longest Increasing Subsequence                        LeetCode 300 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array nums, return the LENGTH of the longest strictly increasing
 *   subsequence. A subsequence keeps the original order but may drop elements; it does not
 *   have to be contiguous. Equal values do not chain, because the order must be strict.
 *
 * EXAMPLE
 *   [10, 9, 2, 5, 3, 7, 101, 18]  ->  4   e.g. 2, 3, 7, 101
 *   [7, 7, 7, 7, 7, 7, 7]         ->  1   equal values cannot extend each other
 *   [0, 1, 0, 3, 2, 3]            ->  4   0, 1, 2, 3
 *   []                            ->  0   edge case: empty array
 *   [5]                           ->  1   edge case: single element
 *
 * APPROACH  (take / skip with a "previous value" constraint, then the O(n log n) trick)
 *   1. backtracking: at every index either append nums[i] - allowed only when it is strictly
 *      greater than the last value already taken - or skip it. The longest list wins. O(2^n).
 *   2. dpTabulation: dp[i] = length of the LIS that ENDS at index i. Every element alone is a
 *      subsequence of length 1, so dp starts as all 1s. For each i, scan every j < i and if
 *      nums[i] > nums[j] then nums[i] can extend that chain: dp[i] = max(dp[i], dp[j] + 1).
 *      The answer is the maximum over the whole dp array, not dp[n - 1].
 *   3. binarySearch (patience sorting): tails[k] = the smallest possible tail value of any
 *      increasing subsequence of length k + 1. tails stays sorted, so binary-search the first
 *      tails[k] >= num. No such k means num extends the longest chain, so append and grow the
 *      answer; otherwise overwrite tails[k] - same length, smaller tail, more room to grow.
 *
 * KEY INSIGHT
 *   The DP state must be "LIS ENDING at i", not "LIS in the first i elements". Anchoring the
 *   chain at a concrete last element is what makes the transition checkable with one
 *   comparison. For O(n log n), remember that tails is NOT an actual subsequence - only its
 *   LENGTH is meaningful; it is a ladder of the best possible tail per chain length.
 *
 * COMPLEXITY
 *   Time  O(n^2) for dpTabulation, O(n log n) for binarySearch, O(2^n) for backtracking
 *   Space O(n)   for dp or tails; backtracking also needs O(n) recursion stack
 *
 * INTERVIEW FOLLOW-UPS
 *   - Print the subsequence itself: keep a parent array in the O(n^2) version, or record the
 *     insertion index per element in the O(n log n) one and walk back.
 *   - Count how many LIS there are (LeetCode 673): carry a count array beside dp.
 *   - Non-decreasing variant: change the strict comparison, and binary-search upper bound.
 *   - Russian Doll Envelopes / Longest String Chain / Max Envelopes: sort first, then LIS.
 *
 * RUN
 *   main() runs 5 cases (typical, all equal, duplicates in the middle, empty, single) and
 *   prints all three approaches against the expected length.
 */

import java.util.ArrayList;
import java.util.Arrays;

class LongestIncreasingSubsequence {

    // ---------- Approach 1: include/exclude backtracking (brute force, exponential) ----------
    // Every element is either appended (only if it keeps the list strictly increasing) or
    // skipped. At the end of the array the current list is one candidate; record its size.
    static int maxLen = 0;

    static int backtracking(int[] nums) {
        // reset the global so the method is re-runnable
        maxLen = 0;
        if (nums == null) return 0;
        backtrack(nums, 0, new ArrayList<>());
        return maxLen;
    }

    private static void backtrack(int[] nums, int i, ArrayList<Integer> list) {
        if (i == nums.length) {
            if (list.size() > maxLen) {
                maxLen = list.size();
            }
            return;
        }

        // include nums[i] only if it keeps the subsequence strictly increasing
        if (list.isEmpty() || list.get(list.size() - 1) < nums[i]) {
            list.add(nums[i]);
            backtrack(nums, i + 1, list);
            list.remove(list.size() - 1);             // undo the choice (backtrack)
        }
        // exclude nums[i]
        backtrack(nums, i + 1, list);
    }

    // ---------- Approach 2: O(n^2) tabulation ----------
    // dp[i] = length of the longest increasing subsequence that ENDS at index i.
    // Every element alone is an LIS of length 1, so dp starts filled with 1.
    static int dpTabulation(int[] nums) {
        if (nums == null || nums.length == 0) return 0;

        int n = nums.length;
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        int maxLength = 1;                            // a single element is always a valid answer

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                // nums[i] can sit after nums[j] only if it is strictly larger
                if (nums[i] > nums[j]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                    maxLength = Math.max(maxLength, dp[i]);
                }
            }
        }
        return maxLength;
    }

    // ---------- Approach 3: O(n log n) binary search (patience sorting) ----------
    // tails[k] holds the smallest possible tail value of an increasing subsequence of length k+1.
    // tails is always sorted, so for each num binary-search the first tails[k] >= num:
    //   - if none exists, num extends the longest subsequence -> append (size grows by 1)
    //   - otherwise num replaces tails[k]: same length, smaller tail, more room to grow later.
    // NOTE: tails is NOT the actual LIS, only its length is meaningful.
    static int binarySearch(int[] nums) {
        if (nums == null || nums.length == 0) return 0;

        int[] tails = new int[nums.length];
        int size = 0;
        for (int num : nums) {
            int lo = 0, hi = size;                    // search in tails[0..size)
            while (lo < hi) {
                int mid = lo + (hi - lo) / 2;
                if (tails[mid] < num) lo = mid + 1;
                else hi = mid;
            }
            tails[lo] = num;                          // lo == size means append
            if (lo == size) size++;
        }
        return size;
    }

    // -------------------------------------------------------------------------------------
    private static void print(String label, int actual, int expected) {
        System.out.println(label + " -> " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] tests = {
                {10, 9, 2, 5, 3, 7, 101, 18},         // typical
                {7, 7, 7, 7, 7, 7, 7},                // all equal: strict order means no chaining
                {0, 1, 0, 3, 2, 3},                   // duplicates in the middle: 0, 1, 2, 3
                {},                                   // edge case: empty
                {5}                                   // edge case: single element
        };
        int[] expected = {4, 1, 4, 0, 1};

        for (int c = 0; c < tests.length; c++) {
            int[] nums = tests[c];
            System.out.println("case " + (c + 1) + ": nums = " + Arrays.toString(nums));
            print("  backtracking O(2^n)    ", backtracking(nums), expected[c]);
            print("  dpTabulation O(n^2)    ", dpTabulation(nums), expected[c]);
            print("  binarySearch O(n log n)", binarySearch(nums), expected[c]);
        }
    }
}

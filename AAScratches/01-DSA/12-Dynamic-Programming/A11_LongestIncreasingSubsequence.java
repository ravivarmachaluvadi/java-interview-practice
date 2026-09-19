import java.util.ArrayList;
import java.util.Arrays;

// https://leetcode.com/problems/longest-increasing-subsequence/
// 300. Longest Increasing Subsequence   (Array, Binary Search, Dynamic Programming)
//
// Problem:    Given nums, return the length of the longest strictly increasing subsequence.
// Approaches: 1. backtracking  - include/exclude every element, track the best length.  O(2^n) time, O(n) stack.
//             2. dpTabulation  - dp[i] = longest LIS ending at i = max(dp[j] + 1) for j < i, nums[j] < nums[i].  O(n^2) time, O(n) space.
//             3. binarySearch  - "patience sorting": tails[k] = smallest tail of any increasing subsequence of length k+1.  O(n log n) time, O(n) space.
class LongestIncreasingSubsequence {

    // ---------- Approach 1: include/exclude backtracking (brute force, exponential) ----------
    // Every element is either appended (only if it keeps the list strictly increasing) or skipped.
    // At the end of the array the current list is one candidate subsequence; record its size.
    static int maxLen = 0;

    static int backtracking(int[] nums) {
        maxLen = 0;                                   // reset the global so the method is re-runnable
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
        int maxLength = 1;
        Arrays.fill(dp, 1);

        for (int i = 1; i < n; i++) {
            // Iterate through all indices j less than i
            for (int j = 0; j < i; j++) {
                // If nums[i] > nums[j], nums[i] can extend the LIS ending at j
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
    // tails is always sorted, so for each num we binary-search the first tails[k] >= num:
    //   - if none exists, num extends the longest subsequence -> append (size grows by 1)
    //   - otherwise, num replaces tails[k]: same length, but a smaller tail leaves more room to grow later.
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

    public static void main(String[] args) {
        int[][] tests = {
                {10, 9, 2, 5, 3, 7, 101, 18},         // expected 4  (2,3,7,101 or 2,3,7,18)
                {7, 7, 7, 7, 7, 7, 7},                // expected 1  (strictly increasing, so equal values don't chain)
                {0, 1, 0, 3, 2, 3},                   // expected 4  (0,1,2,3)
                {}                                    // expected 0
        };

        for (int[] nums : tests) {
            System.out.println("Input array: " + Arrays.toString(nums));
            System.out.println("  backtracking  (O(2^n))     : " + backtracking(nums));
            System.out.println("  dpTabulation  (O(n^2))     : " + dpTabulation(nums));
            System.out.println("  binarySearch  (O(n log n)) : " + binarySearch(nums));
        }
    }
}

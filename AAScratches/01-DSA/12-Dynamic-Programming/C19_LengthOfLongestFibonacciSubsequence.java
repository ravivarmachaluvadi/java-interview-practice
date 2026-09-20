/*
 * =====================================================================
 *  Length of Longest Fibonacci Subsequence         LeetCode 873 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   arr is strictly increasing and positive. A subsequence is Fibonacci-like
 *   if it has at least 3 elements and every element from the third on equals
 *   the sum of the two before it. Return the length of the longest such
 *   subsequence, or 0 if there is none.
 *
 * EXAMPLE
 *   [1,2,3,4,5,6,7,8]    ->  5   1,2,3,5,8
 *   [1,3,7,11,12,14,18]  ->  3   e.g. 1,11,12  (also 3,11,14 and 7,11,18)
 *   [1,2,3]              ->  3   the whole array
 *   [1,4,7,13]           ->  0   no pair's sum is in the array (edge case)
 *
 * APPROACH  (three framings, all run from main)
 *   1. Greedy simulation: put every value in a HashSet. Fix the first two
 *      elements (i, j), then keep asking "is a + b present?" and slide the
 *      window forward. Length is whatever the walk reached.
 *   2. Binary search: identical walk, but arr is already sorted so look the
 *      next term up with Arrays.binarySearch instead of hashing.
 *   3. Dynamic programming: dp[j][k] = length of the Fibonacci chain that ends
 *      with the pair (arr[j], arr[k]). For each k, two pointers find every
 *      pair i < j with arr[i] + arr[j] == arr[k], then dp[j][k] = 1 + dp[i][j].
 *      The stored value counts terms past the first two, so add 2 at the end.
 *
 * KEY INSIGHT
 *   Two consecutive terms determine the entire rest of the sequence, so the
 *   state is a PAIR of indices, not one index. That is the whole step up from
 *   ordinary 1D DP: when the recurrence needs the last two choices, key the
 *   table on both of them. Because arr is sorted and positive, the two-pointer
 *   sweep finds each pair summing to arr[k] in O(n) instead of O(n log n).
 *
 * COMPLEXITY
 *   Greedy         Time O(n^2 * log M)  each (i, j) walk is O(log M) terms
 *   Binary search  Time O(n^2 * log M * log n), Space O(1) beyond the input
 *   DP             Time O(n^2)  two-pointer sweep per k, Space O(n^2) table
 *   (M is the largest value; Fibonacci growth caps a chain at ~log M terms.)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the subsequence itself, not just its length.
 *   - What if arr is NOT sorted? Sorting changes the answer's meaning - discuss.
 *   - Why can the answer never be 1 or 2? The definition requires 3+ elements.
 *   - Overflow: a + b can exceed int when values approach 1e9; use long.
 *
 * RUN
 *   main() runs 4 cases (typical, sparse, minimum length, none) and prints all
 *   three methods against the expected value on one line.
 */

import java.util.*;

class LengthOfLongestFibonacciSubsequence {

    /** Approach 1: fix the first two terms, then walk forward using a HashSet. */
    public static int lenLongestFibSubseqGreedy(int[] arr) {
        int n = arr.length;
        Set<Integer> values = new HashSet<>();
        for (int num : arr) {
            values.add(num);
        }

        int longest = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                int a = arr[i];
                int b = arr[j];
                int fibLen = 2;
                // long keeps a + b from wrapping when values approach 1e9
                long next = nextTerm(a, b);
                while (next <= Integer.MAX_VALUE && values.contains((int) next)) {
                    a = b;
                    b = (int) next;
                    next = nextTerm(a, b);
                    fibLen++;
                }
                if (fibLen > 2) { // a Fibonacci-like subsequence needs 3+ terms
                    longest = Math.max(longest, fibLen);
                }
            }
        }
        return longest;
    }

    private static long nextTerm(int a, int b) {
        return (long) a + b;
    }

    /** Approach 2: same walk, but arr is sorted so binary search the next term. */
    public static int lenLongestFibSubseqBinarySearch(int[] arr) {
        int n = arr.length;
        int longest = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                int chainBeyondPair = fibChainLength(arr, arr[i], arr[j]);
                if (chainBeyondPair > 0) {
                    longest = Math.max(longest, 2 + chainBeyondPair);
                }
            }
        }
        return longest;
    }

    /** Terms found after the pair (a, b); 0 means the pair starts nothing. */
    private static int fibChainLength(int[] arr, int a, int b) {
        long next = nextTerm(a, b);
        if (next > arr[arr.length - 1]) {
            return 0; // past the largest value, so it cannot be present
        }
        int idx = Arrays.binarySearch(arr, (int) next);
        if (idx < 0) {
            return 0;
        }
        return 1 + fibChainLength(arr, b, (int) next);
    }

    /**
     * Approach 3: dp[j][k] = terms found after the pair (arr[j], arr[k]).
     * For each k, two pointers scan for pairs (i before j) with
     * arr[i] + arr[j] == arr[k].
     */
    public static int lenLongestFibSubseqDP(int[] arr) {
        int n = arr.length;
        int[][] dp = new int[n][n];
        int longest = 0;

        for (int k = 2; k < n; k++) {
            int i = 0;
            int j = k - 1;
            while (i < j) {
                int sum = arr[i] + arr[j];
                if (sum < arr[k]) {
                    i++;            // need a bigger sum
                } else if (sum > arr[k]) {
                    j--;            // need a smaller sum
                } else {
                    // arr[i], arr[j], arr[k] is Fibonacci-like: extend the
                    // chain that already ended with the pair (arr[i], arr[j]).
                    dp[j][k] = 1 + dp[i][j];
                    longest = Math.max(longest, dp[j][k]);
                    i++;
                    j--;
                }
            }
        }
        // dp counts terms beyond the first two, so add them back.
        return longest == 0 ? 0 : 2 + longest;
    }

    private static void print(String label, int[] arr, int expected) {
        System.out.println(label + " " + Arrays.toString(arr)
                + " -> greedy " + lenLongestFibSubseqGreedy(arr)
                + ", binarySearch " + lenLongestFibSubseqBinarySearch(arr)
                + ", dp " + lenLongestFibSubseqDP(arr)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (typical):", new int[]{1, 2, 3, 4, 5, 6, 7, 8}, 5);
        print("case 2 (sparse): ", new int[]{1, 3, 7, 11, 12, 14, 18}, 3);
        print("case 3 (minimum):", new int[]{1, 2, 3}, 3);
        print("case 4 (none):   ", new int[]{1, 4, 7, 13}, 0);
    }
}

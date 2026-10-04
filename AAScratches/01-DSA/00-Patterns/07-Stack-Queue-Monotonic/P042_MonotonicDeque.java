/*
 * =====================================================================
 *  P042 Monotonic Deque: Window Max / Min   Canonical LC 239 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 239, Sliding Window Maximum)
 *   Return the maximum of every window of size k as it slides from left to right.
 *
 * EXAMPLE
 *   [1, 3, -1, -3, 5, 3, 6, 7], k = 3  ->  [3, 3, 5, 5, 6, 7]
 *   [1],                        k = 1  ->  [1]
 *
 * RECOGNIZE WHEN
 *   - A sliding window (fixed OR variable) needs its max or min at every step; a running
 *     sum would work for sums, but max/min cannot be "subtracted" when an element leaves.
 *   - DP where dp[i] = best of dp[j] over a sliding range of j (+ a[i]).
 *   - "shortest subarray with sum >= K" when values can be NEGATIVE.
 *   Not this if: the window aggregate is a sum or a count -> P015_FixedWindowAggregate; you
 *   need the median of the window -> P045_TwoHeaps.
 *
 * TEMPLATE
 *   dq = deque of indices, values decreasing from front to back (for max)
 *   for i in 0..n-1:
 *       while dq and a[dq.back] <= a[i]: dq.popBack()      // they can never be the max
 *       dq.pushBack(i)
 *       if dq.front <= i - k: dq.popFront()                 // fell out of the window
 *       if i >= k - 1: output a[dq.front]
 *
 * APPROACH
 *   1. Keep indices whose values are decreasing; the front is the window max.
 *   2. A new value removes every smaller value from the back: they are older AND smaller,
 *      so they can never be a maximum again.
 *   3. Drop the front when its index leaves the window.
 *
 * KEY INSIGHT
 *   An element that has a newer, bigger element behind it is useless for every future
 *   window. Removing such elements leaves a decreasing deque whose front is the answer,
 *   and each index enters and leaves once: O(n) total.
 *
 * COMPLEXITY
 *   Time O(n), space O(k).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 1438 Longest Abs Diff <= Limit variable window + TWO deques (max and min);
 *                                            shrink while max - min > limit
 *   [coded] LC 862  Shortest Sum >= K        deque of prefix-sum indices, increasing; pop
 *                                            the front while it gives a valid sum
 *   [coded] LC 1696 Jump Game VI             dp[i] = a[i] + max(dp[i-k..i-1]); deque of dp
 *           LC 1499 Max Value of Equation    max of (y - x) over a window of x
 *           LC 1425 Constrained Subseq Sum   LC 1696 where you may also start fresh
 *           LC 2398 Max Robots Within Budget variable window + max deque + running sum
 *
 * PITFALLS
 *   - Store INDICES, so you can tell when the front has left the window.
 *   - Pop the back with <= (for max) to keep the deque short; either works for
 *     correctness, but be consistent.
 *   - LC 862: sliding window fails with negatives; the deque of prefix sums is the fix.
 *
 * DEEP DIVE
 *   D04_SlidingWindowMaximum (07-Stack-Queue-Monotonic)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

class MonotonicDeque {

    // Canonical LC 239.
    static int[] maxSlidingWindow(int[] a, int k) {
        int[] out = new int[a.length - k + 1];
        Deque<Integer> dq = new ArrayDeque<>();
        for (int i = 0; i < a.length; i++) {
            while (!dq.isEmpty() && a[dq.peekLast()] <= a[i]) {
                dq.pollLast();
            }
            dq.offerLast(i);
            if (dq.peekFirst() <= i - k) {
                dq.pollFirst();
            }
            if (i >= k - 1) {
                out[i - k + 1] = a[dq.peekFirst()];
            }
        }
        return out;
    }

    // LC 1438.
    static int longestSubarray(int[] a, int limit) {
        Deque<Integer> maxQ = new ArrayDeque<>();
        Deque<Integer> minQ = new ArrayDeque<>();
        int left = 0;
        int best = 0;
        for (int right = 0; right < a.length; right++) {
            while (!maxQ.isEmpty() && a[maxQ.peekLast()] <= a[right]) {
                maxQ.pollLast();
            }
            maxQ.offerLast(right);
            while (!minQ.isEmpty() && a[minQ.peekLast()] >= a[right]) {
                minQ.pollLast();
            }
            minQ.offerLast(right);
            while (a[maxQ.peekFirst()] - a[minQ.peekFirst()] > limit) {
                left++;
                if (maxQ.peekFirst() < left) {
                    maxQ.pollFirst();
                }
                if (minQ.peekFirst() < left) {
                    minQ.pollFirst();
                }
            }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    // LC 862: values may be negative.
    static int shortestSubarray(int[] a, int k) {
        int n = a.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + a[i];
        }
        Deque<Integer> dq = new ArrayDeque<>();          // prefix indices, prefix increasing
        int best = Integer.MAX_VALUE;
        for (int j = 0; j <= n; j++) {
            while (!dq.isEmpty() && prefix[j] - prefix[dq.peekFirst()] >= k) {
                best = Math.min(best, j - dq.pollFirst());   // shortest for that start
            }
            while (!dq.isEmpty() && prefix[dq.peekLast()] >= prefix[j]) {
                dq.pollLast();                     // a later, smaller prefix is a better start
            }
            dq.offerLast(j);
        }
        return best == Integer.MAX_VALUE ? -1 : best;
    }

    // LC 1696: from index i you may jump 1..k forward; maximise the sum of visited values.
    static int maxResult(int[] a, int k) {
        int n = a.length;
        int[] dp = new int[n];
        dp[0] = a[0];
        Deque<Integer> dq = new ArrayDeque<>();
        dq.offerLast(0);
        for (int i = 1; i < n; i++) {
            if (dq.peekFirst() < i - k) {
                dq.pollFirst();
            }
            dp[i] = a[i] + dp[dq.peekFirst()];
            while (!dq.isEmpty() && dp[dq.peekLast()] <= dp[i]) {
                dq.pollLast();
            }
            dq.offerLast(i);
        }
        return dp[n - 1];
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 239 k=3",
                Arrays.toString(maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3)),
                "[3, 3, 5, 5, 6, 7]");
        check("LC 239 k=1", Arrays.toString(maxSlidingWindow(new int[]{1}, 1)), "[1]");
        check("LC 239 decreasing",
                Arrays.toString(maxSlidingWindow(new int[]{9, 8, 7, 6}, 2)), "[9, 8, 7]");

        check("LC 1438 [8,2,4,7] limit=4", longestSubarray(new int[]{8, 2, 4, 7}, 4), 2);
        check("LC 1438 [10,1,2,4,7,2] limit=5",
                longestSubarray(new int[]{10, 1, 2, 4, 7, 2}, 5), 4);
        check("LC 1438 limit=0", longestSubarray(new int[]{4, 2, 2, 2, 4, 4, 2, 2}, 0), 3);

        check("LC 862 [1] k=1", shortestSubarray(new int[]{1}, 1), 1);
        check("LC 862 [1,2] k=4", shortestSubarray(new int[]{1, 2}, 4), -1);
        check("LC 862 [2,-1,2] k=3 negatives", shortestSubarray(new int[]{2, -1, 2}, 3), 3);
        check("LC 862 [84,-37,32,40,95] k=167",
                shortestSubarray(new int[]{84, -37, 32, 40, 95}, 167), 3);

        check("LC 1696 k=2", maxResult(new int[]{1, -1, -2, 4, -7, 3}, 2), 7);
        check("LC 1696 k=3", maxResult(new int[]{10, -5, -2, 4, 0, 3}, 3), 17);
        check("LC 1696 k=2 must cross negatives",
                maxResult(new int[]{1, -5, -20, 4, -1, 3, -6, -3}, 2), 0);
    }
}

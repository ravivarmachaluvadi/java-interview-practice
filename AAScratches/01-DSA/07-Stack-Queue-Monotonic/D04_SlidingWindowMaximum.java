/*
 * =====================================================================
 *  Sliding Window Maximum                     LeetCode 239 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array and a window size k, return the maximum of every
 *   contiguous window of size k as the window slides one step at a time from
 *   left to right. Output length is n - k + 1. Values may be negative.
 *
 * EXAMPLE
 *   [1, 3, -1, -3, 5, 3, 6, 7], k = 3  ->  [3, 3, 5, 5, 6, 7]
 *   [9, 8, 7, 6], k = 2                ->  [9, 8, 7]   front keeps expiring
 *   [4, 4, 4], k = 2                   ->  [4, 4]      equal values
 *   [1], k = 1                         ->  [1]
 *   [2, 5, 1], k = 3                   ->  [5]         window is the whole array
 *
 * APPROACH  (monotonic decreasing deque of indices)
 *   1. Keep a deque of INDICES whose values are decreasing from front to back.
 *      The front is always the index of the current window's maximum.
 *   2. For each i: evict the front if its index has left the window
 *      (index < i - k + 1). At most one can expire per step.
 *   3. Pop from the back every index whose value is smaller than nums[i];
 *      they can never be a maximum again while nums[i] is in the window.
 *   4. Push i at the back. Once i >= k - 1, record nums[front] as the answer.
 *
 * KEY INSIGHT
 *   A smaller element to the LEFT of a bigger one is dead: it leaves the window
 *   first and is never the max while the bigger one lives. So only a decreasing
 *   run of candidates matters. The deque is a monotonic stack (pop from back)
 *   plus one extra operation the stack lacks: expiry from the front by index.
 *   Storing indices instead of values is what makes expiry possible.
 *
 * COMPLEXITY
 *   Time  O(n)  each index enters and leaves the deque at most once
 *   Space O(k)  the deque never holds more than one window's worth of indices
 *
 * INTERVIEW FOLLOW-UPS
 *   - Sliding window minimum: same code with the comparison flipped.
 *   - Heap solution O(n log k) with lazy deletion; why the deque beats it.
 *   - Two-stack queue with a max (the A-tier QueueUsingStacks with running max).
 *   - Shortest subarray with sum at least K (LC 862): same deque on prefix sums.
 *
 * RUN
 *   main() runs 5 cases (typical, decreasing, all equal, single, k == n)
 *   and prints actual vs expected.
 */

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

class ImportantSlidingWindowMaximum {

    public static int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || k <= 0 || k > nums.length) return new int[0];
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> candidates = new ArrayDeque<>(); // indices; values decreasing front to back

        for (int i = 0; i < n; i++) {
            // front has slid out of the window [i-k+1, i]
            if (!candidates.isEmpty() && candidates.peekFirst() < i - k + 1) {
                candidates.pollFirst();
            }
            // anything smaller than nums[i] to its left can never be a max again
            while (!candidates.isEmpty() && nums[candidates.peekLast()] < nums[i]) {
                candidates.pollLast();
            }
            candidates.offerLast(i);

            if (i >= k - 1) {
                result[i - k + 1] = nums[candidates.peekFirst()];
            }
        }
        return result;
    }

    static void print(String label, int[] nums, int k, int[] expected) {
        System.out.println(label + " " + Arrays.toString(nums) + " k=" + k + ": "
                + Arrays.toString(maxSlidingWindow(nums, k))
                + "   expected " + Arrays.toString(expected));
    }

    public static void main(String[] args) {
        print("case 1 typical   ", new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3,
                new int[]{3, 3, 5, 5, 6, 7});
        print("case 2 decreasing", new int[]{9, 8, 7, 6}, 2, new int[]{9, 8, 7});
        print("case 3 all equal ", new int[]{4, 4, 4}, 2, new int[]{4, 4});
        print("case 4 single    ", new int[]{1}, 1, new int[]{1});
        print("case 5 k == n    ", new int[]{2, 5, 1}, 3, new int[]{5});
    }
}

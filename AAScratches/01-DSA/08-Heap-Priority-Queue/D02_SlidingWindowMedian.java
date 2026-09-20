/*
 * =====================================================================
 *  Sliding Window Median                                LeetCode 480 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given an int array nums and a window size k, return the median of every
 *   contiguous window of size k, left to right, as doubles. The median of an
 *   even-sized window is the mean of its two middle values. nums[i] can be any
 *   int, including Integer.MAX_VALUE, so the two-value mean must not overflow.
 *
 * EXAMPLE
 *   nums = [1, 3, -1, -3, 5, 3, 6, 7], k = 3  ->  [1, -1, -1, 3, 5, 6]
 *     windows: [1 3 -1] [3 -1 -3] [-1 -3 5] [-3 5 3] [5 3 6] [3 6 7]
 *   nums = [1, 2, 3, 4], k = 4                 ->  [2.5]     one window, even size
 *   nums = [4, 2, 5], k = 1                    ->  [4, 2, 5] every element is its own median
 *   nums = [2147483647, 2147483647], k = 2     ->  [2147483647]  overflow trap
 *
 * APPROACH  (two heaps with window removal)
 *   1. maxHeap ("low") holds the smaller half, minHeap ("high") the larger half.
 *      Invariant: low.size() == high.size() or low.size() == high.size() + 1,
 *      so the median is low.peek() (odd k) or the mean of both roots (even k).
 *   2. For each index i: add nums[i] to low if it is <= low's root, else to high,
 *      then rebalance so the invariant holds.
 *   3. Once i >= k - 1 the window is full: record the median, then remove the
 *      element leaving the window (nums[i - k + 1]) from whichever heap holds
 *      it, and rebalance again because an arbitrary removal can break step 1.
 *   4. Removal uses PriorityQueue.remove(Object), which is O(k): it scans the heap.
 *
 * KEY INSIGHT
 *   The median-of-stream two-heap trick still works with eviction, as long as
 *   every add AND every remove is followed by a rebalance. The invariant, not
 *   the heap contents, is what makes peek() the median. When k is large, swap
 *   the O(k) remove for lazy deletion (a "to delete" count map, purged when the
 *   stale element reaches a root) or two TreeMap multisets for O(log k).
 *
 * COMPLEXITY
 *   Time  O(n * k)    each slide does O(log k) heap ops plus one O(k) remove(Object)
 *   Space O(k)        the two heaps together hold exactly the window
 *
 * INTERVIEW FOLLOW-UPS
 *   - Make removal O(log k): lazy deletion with a HashMap<Integer, Integer> of pending
 *     deletes, or replace the heaps with two TreeMap<Integer, Integer> multisets.
 *   - Why not just sort each window? O(n * k log k) and no reuse between windows.
 *   - Streaming version with no window (LeetCode 295): same heaps, no remove step.
 *   - Sliding window MAX instead of median (LeetCode 239): monotonic deque, O(n).
 *
 * Fixed: (maxHeap.peek() + minHeap.peek()) / 2.0 added the two ints before
 *        widening to double, so a window like [2147483647, 2147483647] overflowed
 *        to a negative median. Now the first root is cast to double before adding.
 *
 * RUN
 *   main() runs 4 cases (typical, single window, k = 1, int overflow) and prints
 *   actual vs expected.
 */

import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;

class SlidingWindowMedian {

    public static double[] medianSlidingWindow(int[] nums, int k) {
        double[] result = new double[nums.length - k + 1];

        // low: max-heap with the smaller half; high: min-heap with the larger half
        PriorityQueue<Integer> low = new PriorityQueue<>(Collections.reverseOrder());
        PriorityQueue<Integer> high = new PriorityQueue<>();

        for (int i = 0; i < nums.length; i++) {
            add(nums[i], low, high);
            rebalance(low, high);

            if (i >= k - 1) {                     // window [i-k+1 .. i] is full
                result[i - k + 1] = median(low, high);
                remove(nums[i - k + 1], low, high); // evict the element leaving the window
                rebalance(low, high);               // removal can break the size invariant
            }
        }
        return result;
    }

    // Route a new value to the half it belongs to.
    private static void add(int num, PriorityQueue<Integer> low, PriorityQueue<Integer> high) {
        if (low.isEmpty() || num <= low.peek()) {
            low.offer(num);
        } else {
            high.offer(num);
        }
    }

    // Restore: low.size() == high.size() or low.size() == high.size() + 1.
    private static void rebalance(PriorityQueue<Integer> low, PriorityQueue<Integer> high) {
        if (low.size() > high.size() + 1) {
            high.offer(low.poll());
        } else if (high.size() > low.size()) {
            low.offer(high.poll());
        }
    }

    // With the invariant in place the median is at the heap roots.
    private static double median(PriorityQueue<Integer> low, PriorityQueue<Integer> high) {
        if (low.size() > high.size()) {
            return low.peek();
        }
        // cast BEFORE adding: two ints near 2^31 overflow if summed as int
        return ((double) low.peek() + high.peek()) / 2.0;
    }

    // Remove the outgoing element from whichever half holds it. O(k) scan.
    private static void remove(int num, PriorityQueue<Integer> low, PriorityQueue<Integer> high) {
        if (num <= low.peek()) {
            low.remove(num);
        } else {
            high.remove(num);
        }
    }

    private static void print(String label, double[] actual, String expected) {
        System.out.println(label + ": " + Arrays.toString(actual) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical k=3",
                medianSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3),
                "[1.0, -1.0, -1.0, 3.0, 5.0, 6.0]");

        print("case 2 k == n, even",
                medianSlidingWindow(new int[]{1, 2, 3, 4}, 4),
                "[2.5]");

        print("case 3 k == 1",
                medianSlidingWindow(new int[]{4, 2, 5}, 1),
                "[4.0, 2.0, 5.0]");

        print("case 4 int overflow",
                medianSlidingWindow(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE}, 2),
                "[2.147483647E9]");
    }
}

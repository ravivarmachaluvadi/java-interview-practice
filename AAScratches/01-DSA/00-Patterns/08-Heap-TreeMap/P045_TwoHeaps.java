/*
 * =====================================================================
 *  P045 Two Heaps: Running Median and Split Choices   Canonical LC 295 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 295, Find Median from Data Stream)
 *   Support addNum(x) and findMedian() over a stream of integers. The median of an even
 *   count is the mean of the two middle values.
 *
 * EXAMPLE
 *   addNum(1), addNum(2), findMedian() -> 1.5, addNum(3), findMedian() -> 2.0
 *
 * RECOGNIZE WHEN
 *   - Median (or any "middle" order statistic) of a stream or of a sliding window.
 *   - The data splits into a "lower half" and an "upper half" and you need the border.
 *   - Two pools where you move items from one to the other as a budget grows (IPO:
 *     "locked" projects become "affordable" ones).
 *   Not this if: you need the k largest only -> P043_TopK (one heap).
 *
 * TEMPLATE
 *   low = max-heap (smaller half), high = min-heap (larger half)
 *   add(x): push x into low; move low.top to high; if high.size > low.size: move back
 *   invariant: low.size == high.size or low.size == high.size + 1, and low.top <= high.top
 *   median = low.size > high.size ? low.top : (low.top + high.top) / 2.0
 *
 * APPROACH
 *   1. Every new number goes through low and then high, so the order invariant holds.
 *   2. Rebalance sizes so low has the extra element when the count is odd.
 *   3. The median is read from the two tops in O(1).
 *
 * KEY INSIGHT
 *   The median only depends on the BORDER between the halves. Two heaps facing each other
 *   expose exactly that border at their tops, with O(log n) insertion and O(1) reads,
 *   instead of re-sorting.
 *
 * COMPLEXITY
 *   addNum O(log n), findMedian O(1), space O(n). Sliding window: O(n log k).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 480  Sliding Window Median    two ordered sets of INDICES (so a specific
 *                                            element can be removed when it leaves)
 *   [coded] LC 502  IPO                      min-heap by capital (locked), max-heap by
 *                                            profit (affordable); unlock, take the best
 *           LC 1825 Finding MK Average       three ordered multisets: low, middle, high
 *           LC 857  Min Cost to Hire Workers sort by wage/quality, max-heap of qualities
 *           LC 2102 Location Ranking Tracker the same two-heap border, moved one per query
 *
 * PITFALLS
 *   - (a + b) / 2 overflows for big ints; use (a + (double) b) / 2.
 *   - PriorityQueue.remove(Object) is O(k); for windows use TreeSet / TreeMap or lazy
 *     deletion.
 *   - TreeSet of values loses duplicates; store indices with a (value, index) comparator.
 *
 * DEEP DIVE
 *   D01_MedianOfStream, D02_SlidingWindowMedian (08-Heap-Priority-Queue)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.TreeSet;

class TwoHeaps {

    // Canonical LC 295.
    static class MedianFinder {
        private final PriorityQueue<Integer> low = new PriorityQueue<>(Collections.reverseOrder());
        private final PriorityQueue<Integer> high = new PriorityQueue<>();

        void addNum(int x) {
            low.offer(x);
            high.offer(low.poll());
            if (high.size() > low.size()) {
                low.offer(high.poll());
            }
        }

        double findMedian() {
            return low.size() > high.size() ? low.peek() : (low.peek() + (double) high.peek()) / 2;
        }
    }

    // LC 480: ordered sets of indices, ordered by (value, index) so duplicates survive.
    static double[] medianSlidingWindow(int[] nums, int k) {
        Comparator<Integer> byValue = (i, j) -> nums[i] != nums[j]
                ? Integer.compare(nums[i], nums[j]) : Integer.compare(i, j);
        TreeSet<Integer> low = new TreeSet<>(byValue);
        TreeSet<Integer> high = new TreeSet<>(byValue);
        double[] out = new double[nums.length - k + 1];
        for (int i = 0; i < nums.length; i++) {
            if (i >= k && !low.remove(i - k)) {
                high.remove(i - k);                // the leaving index is in one of them
            }
            low.add(i);
            high.add(low.pollLast());
            while (high.size() > low.size()) {
                low.add(high.pollFirst());
            }
            if (i >= k - 1) {
                out[i - k + 1] = k % 2 == 1 ? nums[low.last()]
                        : (nums[low.last()] + (double) nums[high.first()]) / 2;
            }
        }
        return out;
    }

    // LC 502: start with capital w, finish at most k projects, maximise final capital.
    static int findMaximizedCapital(int k, int w, int[] profits, int[] capital) {
        PriorityQueue<Integer> locked =
                new PriorityQueue<>((i, j) -> Integer.compare(capital[i], capital[j]));
        PriorityQueue<Integer> affordable =
                new PriorityQueue<>((i, j) -> Integer.compare(profits[j], profits[i]));
        for (int i = 0; i < profits.length; i++) {
            locked.offer(i);
        }
        for (int done = 0; done < k; done++) {
            while (!locked.isEmpty() && capital[locked.peek()] <= w) {
                affordable.offer(locked.poll());
            }
            if (affordable.isEmpty()) {
                break;
            }
            w += profits[affordable.poll()];
        }
        return w;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        MedianFinder mf = new MedianFinder();
        mf.addNum(1);
        mf.addNum(2);
        check("LC 295 after 1,2", mf.findMedian(), 1.5);
        mf.addNum(3);
        check("LC 295 after 1,2,3", mf.findMedian(), 2.0);
        MedianFinder big = new MedianFinder();
        big.addNum(Integer.MAX_VALUE);
        big.addNum(Integer.MAX_VALUE);
        check("LC 295 overflow trap", big.findMedian(), 2.147483647E9);

        check("LC 480 k=3",
                Arrays.toString(medianSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3)),
                "[1.0, -1.0, -1.0, 3.0, 5.0, 6.0]");
        check("LC 480 k=3 duplicates",
                Arrays.toString(medianSlidingWindow(new int[]{1, 2, 3, 4, 2, 3, 1, 4, 2}, 3)),
                "[2.0, 3.0, 3.0, 3.0, 2.0, 3.0, 2.0]");
        check("LC 480 k=2 even", Arrays.toString(medianSlidingWindow(new int[]{1, 4, 2, 3}, 2)),
                "[2.5, 3.0, 2.5]");

        check("LC 502 k=2 w=0",
                findMaximizedCapital(2, 0, new int[]{1, 2, 3}, new int[]{0, 1, 1}), 4);
        check("LC 502 k=3 w=0",
                findMaximizedCapital(3, 0, new int[]{1, 2, 3}, new int[]{0, 1, 2}), 6);
        check("LC 502 nothing affordable",
                findMaximizedCapital(1, 0, new int[]{5}, new int[]{1}), 0);
    }
}

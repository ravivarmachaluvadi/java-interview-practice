/*
 * =====================================================================
 *  P043 Top-K: Heap, Quickselect, Buckets   Canonical LC 215 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 215, Kth Largest Element in an Array)
 *   Return the k-th largest element of nums (k-th in sorted order, duplicates count).
 *   Can you do better than sorting?
 *
 * EXAMPLE
 *   [3, 2, 1, 5, 6, 4],          k = 2  ->  5
 *   [3, 2, 3, 1, 2, 4, 5, 5, 6], k = 4  ->  4
 *
 * RECOGNIZE WHEN
 *   - "k largest / smallest / most frequent / closest", "k-th largest".
 *   - k is much smaller than n, or the data arrives as a stream.
 *   Not this if: you need the median or both halves -> P045_TwoHeaps; you merge k SORTED
 *   lists -> P044_KWayMerge; the window slides -> P042_MonotonicDeque.
 *
 * TEMPLATE
 *   heap of size k ordered so that the WORST kept item is on top
 *       (k largest -> MIN-heap; k smallest / closest -> MAX-heap)
 *   for x in items:
 *       heap.offer(x)
 *       if heap.size() > k: heap.poll()        // evict the worst
 *   heap holds the answer; heap.peek() is the k-th
 *   alternatives: quickselect O(n) average; bucket sort when keys are small ints (counts)
 *
 * APPROACH
 *   1. Keep a min-heap of the k largest values seen so far.
 *   2. Every new value goes in; if the heap grows past k, the smallest leaves.
 *   3. The top of the heap is the k-th largest.
 *
 * KEY INSIGHT
 *   You never need the whole order, only a boundary. A size-k heap whose TOP is the
 *   current weakest member is that boundary: anything worse than the top is rejected in
 *   O(log k). Quickselect finds the boundary in place in O(n) average; buckets do it in
 *   O(n) when the key is a small count.
 *
 * COMPLEXITY
 *   Heap O(n log k) time, O(k) space. Quickselect O(n) average, O(n^2) worst, O(1) space.
 *   Buckets O(n) time and space.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 215 quickselect               partition until the pivot lands at n - k
 *   [coded] LC 347  Top K Frequent Elements  count, then bucket[count] = values; read the
 *                                            buckets from high to low
 *   [coded] LC 973  K Closest Points         max-heap by distance, size k
 *   [coded] LC 703  Kth Largest in a Stream  the heap lives in the object; add() is O(log k)
 *           LC 692  Top K Frequent Words     comparator: count desc, then word asc
 *           LC 658  K Closest Elements       sorted input: binary search the window start
 *           LC 451  Sort Characters by Freq  the bucket idea over characters
 *           LC 1985 Kth Largest String Num   comparator by length, then lexicographic
 *
 * PITFALLS
 *   - Java's PriorityQueue is a MIN-heap; use (a, b) -> b - a, or Collections.reverseOrder(),
 *     for a max-heap.
 *   - Comparators with a - b overflow for big ints; use Integer.compare.
 *   - Quickselect needs a random pivot to avoid O(n^2) on sorted input.
 *
 * DEEP DIVE
 *   A01_KthLargestElementInAStream, C01_topKFrequent, C02_KClosestPointsToOrigin
 *   (08-Heap-Priority-Queue), C01_QuickSelect (18-Sorting-Searching-Algorithms)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

class TopK {

    // Canonical LC 215 with a size-k min-heap.
    static int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        for (int x : nums) {
            heap.offer(x);
            if (heap.size() > k) {
                heap.poll();
            }
        }
        return heap.peek();
    }

    // LC 215 with quickselect: the answer is the value at sorted index n - k.
    static int findKthLargestQuickselect(int[] nums, int k) {
        int[] a = nums.clone();
        int target = a.length - k;
        int lo = 0;
        int hi = a.length - 1;
        Random rnd = new Random(1);
        while (true) {
            int p = partition(a, lo, hi, lo + rnd.nextInt(hi - lo + 1));
            if (p == target) {
                return a[p];
            } else if (p < target) {
                lo = p + 1;
            } else {
                hi = p - 1;
            }
        }
    }

    // Lomuto partition: returns the pivot's final index.
    private static int partition(int[] a, int lo, int hi, int pivotIndex) {
        swap(a, pivotIndex, hi);
        int store = lo;
        for (int i = lo; i < hi; i++) {
            if (a[i] < a[hi]) {
                swap(a, store++, i);
            }
        }
        swap(a, store, hi);
        return store;
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    // LC 347 with bucket sort: bucket[c] holds the values that occur c times.
    static List<Integer> topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> count = new HashMap<>();
        for (int x : nums) {
            count.merge(x, 1, Integer::sum);
        }
        List<List<Integer>> bucket = new ArrayList<>();
        for (int i = 0; i <= nums.length; i++) {
            bucket.add(new ArrayList<>());
        }
        count.forEach((value, c) -> bucket.get(c).add(value));
        List<Integer> out = new ArrayList<>();
        for (int c = nums.length; c >= 1 && out.size() < k; c--) {
            out.addAll(bucket.get(c));
        }
        List<Integer> top = new ArrayList<>(out.subList(0, k));
        Collections.sort(top);                     // any order is accepted; sorted to print
        return top;
    }

    // LC 973: max-heap by squared distance keeps the k closest.
    static int[][] kClosest(int[][] points, int k) {
        PriorityQueue<int[]> heap =
                new PriorityQueue<>((p, q) -> Integer.compare(dist(q), dist(p)));
        for (int[] p : points) {
            heap.offer(p);
            if (heap.size() > k) {
                heap.poll();
            }
        }
        int[][] out = heap.toArray(new int[0][]);
        Arrays.sort(out, (p, q) -> Integer.compare(dist(p), dist(q)));
        return out;
    }

    private static int dist(int[] p) {
        return p[0] * p[0] + p[1] * p[1];
    }

    // LC 703.
    static class KthLargest {
        private final PriorityQueue<Integer> heap = new PriorityQueue<>();
        private final int k;

        KthLargest(int k, int[] nums) {
            this.k = k;
            for (int x : nums) {
                add(x);
            }
        }

        int add(int val) {
            heap.offer(val);
            if (heap.size() > k) {
                heap.poll();
            }
            return heap.peek();
        }
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 215 heap k=2", findKthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2), 5);
        check("LC 215 heap k=4 duplicates",
                findKthLargest(new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4), 4);
        check("LC 215 quickselect k=2",
                findKthLargestQuickselect(new int[]{3, 2, 1, 5, 6, 4}, 2), 5);
        check("LC 215 quickselect k=4",
                findKthLargestQuickselect(new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4), 4);
        check("LC 215 quickselect all equal", findKthLargestQuickselect(new int[]{7, 7, 7}, 2), 7);

        check("LC 347 k=2", topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2), "[1, 2]");
        check("LC 347 k=1", topKFrequent(new int[]{1}, 1), "[1]");

        check("LC 973 k=1",
                Arrays.deepToString(kClosest(new int[][]{{1, 3}, {-2, 2}}, 1)), "[[-2, 2]]");
        check("LC 973 k=2", Arrays.deepToString(kClosest(new int[][]{{3, 3}, {5, -1}, {-2, 4}}, 2)),
                "[[3, 3], [-2, 4]]");

        KthLargest stream = new KthLargest(3, new int[]{4, 5, 8, 2});
        int[] adds = {3, 5, 10, 9, 4};
        int[] got = new int[adds.length];
        for (int i = 0; i < adds.length; i++) {
            got[i] = stream.add(adds[i]);
        }
        check("LC 703 k=3 stream", Arrays.toString(got), "[4, 5, 5, 8, 8]");
    }
}

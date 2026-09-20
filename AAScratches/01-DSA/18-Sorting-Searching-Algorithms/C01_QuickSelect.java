/*
 * =====================================================================
 *  Quickselect - kth smallest / kth largest   LeetCode 215 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an unsorted array and k, return the kth smallest value (and, with the
 *   same engine, the kth largest). k is 1-based and must satisfy 1 <= k <= n.
 *   Duplicates count as separate ranks: in [3, 3, 3, 1] the 2nd smallest is 3.
 *   Expected better than sorting the whole array.
 *
 * EXAMPLE
 *   [7, 1, 4, 2, 8, 5, 0, 9, 3], k = 2  ->  1   (sorted: 0, 1, 2, 3, ...)
 *   [7, 1, 4, 2, 8, 5, 0, 9, 3], k = 3 largest  ->  7   (9, 8, 7)
 *   [42], k = 1  ->  42                  (single element, one partition, done)
 *
 * APPROACH  (one-sided partition recursion)
 *   1. Convert the 1-based k to a target INDEX: the kth smallest is whatever ends
 *      up at index k - 1 of the sorted array.
 *   2. Partition arr[low..high] around arr[high] (Lomuto). The pivot lands at its
 *      final sorted index p, everything left of p is smaller, everything right larger.
 *   3. If p == target, arr[p] is the answer - stop.
 *   4. If p < target, the answer is to the right: recurse on [p + 1, high].
 *   5. If p > target, recurse on [low, p - 1]. Only ONE side is ever searched.
 *
 * KEY INSIGHT
 *   Quick sort recurses into both halves; quickselect throws one half away. That
 *   turns n + n/2 + n/4 + ... into 2n, so the expected cost is O(n) rather than
 *   O(n log n) - you never pay to order the elements you were not asked about.
 *   Whenever a question says "kth largest", "top K" or "median", partition is the
 *   answer that beats sorting, and a heap is the answer when the data is streaming.
 *
 * COMPLEXITY
 *   Time  O(n) expected   each level halves the search range: n + n/2 + n/4 ... = 2n
 *         O(n^2) worst    sorted input with a last-element pivot peels one per level;
 *                         a random pivot or median-of-medians removes this
 *   Space O(1) extra      partitioning is in place; recursion is O(log n) expected
 *                         stack and can be rewritten as a while loop
 *
 * INTERVIEW FOLLOW-UPS
 *   - Quickselect vs a size-k heap for "kth largest": O(n) expected vs O(n log k)
 *     guaranteed - which do you pick for a stream, and why?
 *   - How does median-of-medians make the worst case O(n), and why is it rarely used?
 *   - Many duplicates degrade Lomuto; how does three-way partitioning fix it?
 *   - Top K elements (not just the kth): partition once, then return arr[0..k-1].
 *
 * NOTE
 *   quickSelect rearranges the array it is given, so the public wrappers work on a
 *   copy - otherwise case 2 would be answering a question about case 1's leftovers.
 *
 * RUN
 *   main() runs 4 cases (kth smallest, kth largest, single element, duplicates)
 *   and prints actual vs expected.
 */
import java.util.Arrays;

class ImportantQuickSelect {

    public static void main(String[] args) {
        int[] arr = {7, 1, 4, 2, 8, 5, 0, 9, 3};

        print("case 1 2nd smallest  ", kthSmallest(arr, 2), 1);
        print("case 2 3rd largest   ", kthLargest(arr, 3), 7);
        print("case 3 single element", kthSmallest(new int[]{42}, 1), 42);
        print("case 4 duplicates    ", kthSmallest(new int[]{3, 3, 3, 1}, 2), 3);

        // The wrappers copy, so the caller's array is untouched.
        print("case 5 input intact  ", Arrays.toString(arr), "[7, 1, 4, 2, 8, 5, 0, 9, 3]");
    }

    /** kth smallest value, k is 1-based. Does not modify the caller's array. */
    public static int kthSmallest(int[] arr, int k) {
        checkRank(arr, k);
        int[] copy = Arrays.copyOf(arr, arr.length);
        return quickSelect(0, copy.length - 1, k - 1, copy);   // rank k -> sorted index k-1
    }

    /** kth largest value, k is 1-based. The kth largest is the (n - k + 1)th smallest. */
    public static int kthLargest(int[] arr, int k) {
        checkRank(arr, k);
        return kthSmallest(arr, arr.length - k + 1);
    }

    private static void checkRank(int[] arr, int k) {
        if (k < 1 || k > arr.length) {
            throw new IllegalArgumentException("k must be in 1.." + arr.length + " but was " + k);
        }
    }

    /**
     * Returns the value that belongs at sorted index k, searching only arr[low..high].
     * Invariant: low <= k <= high, so exactly one side is ever recursed into.
     */
    private static int quickSelect(int low, int high, int k, int[] arr) {
        int partitionIndex = partition(low, high, arr);

        if (partitionIndex == k) {
            return arr[k];                                     // pivot landed on the target index
        }
        if (partitionIndex < k) {
            return quickSelect(partitionIndex + 1, high, k, arr);
        }
        return quickSelect(low, partitionIndex - 1, k, arr);
    }

    /** Lomuto partition around arr[high]; returns the pivot's final sorted index. */
    private static int partition(int low, int high, int[] arr) {
        int pivotValue = arr[high];
        int partitionIndex = low;                              // end of the "< pivot" prefix

        for (int j = low; j < high; j++) {
            if (arr[j] < pivotValue) {
                swap(arr, j, partitionIndex);
                partitionIndex++;                              // prefix grew by one
            }
        }
        swap(arr, partitionIndex, high);                       // pivot takes its final seat
        return partitionIndex;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

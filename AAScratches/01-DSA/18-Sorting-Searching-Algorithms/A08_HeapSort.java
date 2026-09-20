/*
 * =====================================================================
 *  Heap Sort (in-place, max-heap)         LeetCode 912 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Sort an integer array ascending, in place, in guaranteed O(n log n) time and
 *   O(1) extra space. Duplicates and negatives are allowed; the array may be
 *   empty or a single element.
 *
 * EXAMPLE
 *   [12, 11, 13, 5, 6, 7]  ->  [5, 6, 7, 11, 12, 13]
 *   [4]                    ->  [4]              (single element, nothing to do)
 *   [2, 2, 2]              ->  [2, 2, 2]        (all equal, still O(n log n))
 *
 * APPROACH  (array-as-tree + sift-down)
 *   1. Read the array as a complete binary tree: node i has children 2i+1 and
 *      2i+2, and parent (i-1)/2. No pointers are needed.
 *   2. Build a max-heap: sift-down every internal node from n/2 - 1 back to 0.
 *      Working backwards means both children are already heaps when a node is fixed.
 *   3. arr[0] is now the maximum. Swap it with the last slot of the live region,
 *      which parks it at its final sorted position.
 *   4. Shrink the live region by one and sift-down the new root to restore the heap.
 *   5. Repeat until one element is left; the array is sorted ascending.
 *
 * KEY INSIGHT
 *   A max-heap sorts ascending because each extracted maximum is parked at the END
 *   of the array, so the sorted suffix grows leftwards and never collides with the
 *   heap. Building bottom-up is O(n), not O(n log n), because most nodes are near
 *   the leaves and sift down almost no distance. That 2i+1 / 2i+2 indexing is the
 *   same layout used by PriorityQueue, top-K, streaming median and segment trees.
 *
 * COMPLEXITY
 *   Time  O(n log n)  build heap O(n), then n extractions each with O(log n) sift-down
 *   Space O(1)        swaps are in place; sift-down recursion is O(log n) stack and
 *                     can be written as a loop for true O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is build-heap O(n) while inserting n elements one by one is O(n log n)?
 *   - Heap sort has a better worst case than quick sort - why is quick sort still
 *     faster in practice? (cache locality, constant factors)
 *   - Heap sort is not stable; which sort do you reach for when stability matters?
 *   - Kth largest without a full sort: stop after k extractions, O(n + k log n).
 *
 * RUN
 *   main() runs 4 cases (typical, single element, all equal, empty) and prints
 *   actual vs expected.
 */
import java.util.Arrays;

class HeapSort {

    public void heapSort(int[] arr) {
        int n = arr.length;

        // Build a max heap: fix every internal node bottom-up so children are
        // already valid heaps when their parent is sifted down.
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // Repeatedly move the root (current maximum) to the end of the live region.
        for (int i = n - 1; i > 0; i--) {
            swap(arr, 0, i);        // arr[i] is now final
            heapify(arr, i, 0);     // heap is one element smaller
        }
    }

    /**
     * Sift-down: pushes arr[i] down until the subtree rooted at i is a max heap.
     * Only indices [0, n) are treated as part of the heap; the rest is sorted output.
     */
    void heapify(int[] arr, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && arr[left] > arr[largest]) {
            largest = left;
        }
        if (right < n && arr[right] > arr[largest]) {
            largest = right;
        }

        if (largest != i) {         // the root was out of place, so the child subtree may be too
            swap(arr, i, largest);
            heapify(arr, n, largest);
        }
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {
        HeapSort sorter = new HeapSort();

        int[] typical = {12, 11, 13, 5, 6, 7};
        sorter.heapSort(typical);
        print("case 1 typical  ", typical, "[5, 6, 7, 11, 12, 13]");

        int[] single = {4};
        sorter.heapSort(single);
        print("case 2 single   ", single, "[4]");

        int[] allEqual = {2, 2, 2};
        sorter.heapSort(allEqual);
        print("case 3 all equal", allEqual, "[2, 2, 2]");

        int[] empty = {};
        sorter.heapSort(empty);
        print("case 4 empty    ", empty, "[]");
    }

    private static void print(String label, int[] actual, String expected) {
        System.out.println(label + ": " + Arrays.toString(actual) + "   expected " + expected);
    }
}

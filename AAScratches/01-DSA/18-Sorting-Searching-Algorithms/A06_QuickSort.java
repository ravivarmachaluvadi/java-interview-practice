/*
 * =====================================================================
 *  Quick Sort (Lomuto partition)          LeetCode 912 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Sort an integer array in ascending order, in place, without using a
 *   library sort. Duplicates are allowed and negative values are allowed.
 *   The array may be empty or have a single element.
 *
 * EXAMPLE
 *   [12, 4, 8, 1, 3, 15, 7, 9]  ->  [1, 3, 4, 7, 8, 9, 12, 15]
 *   [5, 5, 5, 5]                ->  [5, 5, 5, 5]   (all equal: worst case for Lomuto)
 *   []                          ->  []             (low = 0, high = -1, recursion never starts)
 *
 * APPROACH  (divide and conquer around a pivot)
 *   1. If low >= high the range has 0 or 1 element, so it is already sorted: return.
 *   2. partition(low, high) picks array[high] as the pivot and sweeps j from low
 *      to high - 1, keeping the invariant "everything in [low..index] is <= pivot".
 *   3. Each time array[j] <= pivot, grow that prefix by one (index++) and swap
 *      array[j] into it.
 *   4. Finally swap the pivot into index + 1, so the pivot now sits at its final
 *      sorted position: smaller-or-equal on its left, larger on its right.
 *   5. Recurse on [low, p - 1] and [p + 1, high]. The pivot itself is never
 *      revisited, which is what guarantees progress.
 *
 * KEY INSIGHT
 *   Partition is the reusable primitive, not the sort. One partition pass places
 *   exactly one element - the pivot - at its final index for free. Quick sort
 *   recurses on both sides; quickselect recurses on one; Dutch-national-flag /
 *   sort-colors is the three-way version of the same sweep. Recognise "one pass
 *   that puts one element where it belongs forever" and the family follows.
 *
 * COMPLEXITY
 *   Time  O(n log n) average  each level partitions n elements, depth is log n
 *         O(n^2) worst        sorted input or all-equal input with a last-element
 *                             pivot splits off one element at a time
 *   Space O(log n) average    recursion stack only; the swaps are in place
 *         O(n) worst          same degenerate splits give a stack of depth n
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is the last element a bad pivot, and how do median-of-three or a random
 *     pivot restore expected O(n log n) on already-sorted input?
 *   - Hoare partition vs Lomuto: fewer swaps, but the pivot is not placed finally.
 *   - All-equal input degrades Lomuto to O(n^2); how does three-way partitioning fix it?
 *   - Why is merge sort preferred when stability or guaranteed O(n log n) is required?
 *
 * RUN
 *   main() runs 4 cases (typical, empty, all-equal, negatives) and prints actual
 *   vs expected.
 */
import java.util.Arrays;

class ImportantQuickSort {

    public static void main(String[] args) {
        ImportantQuickSort sorter = new ImportantQuickSort();

        int[] typical = {12, 4, 8, 1, 3, 15, 7, 9};
        sorter.quickSort(typical, 0, typical.length - 1);
        print("case 1 typical  ", typical, "[1, 3, 4, 7, 8, 9, 12, 15]");

        int[] empty = {};
        sorter.quickSort(empty, 0, empty.length - 1);
        print("case 2 empty    ", empty, "[]");

        // All equal: every element goes into the "<= pivot" prefix, so the split is
        // always (n-1, 0). Correct, but this is the O(n^2) shape.
        int[] allEqual = {5, 5, 5, 5};
        sorter.quickSort(allEqual, 0, allEqual.length - 1);
        print("case 3 all equal", allEqual, "[5, 5, 5, 5]");

        int[] negatives = {3, -1, 0, -7, 2, -1};
        sorter.quickSort(negatives, 0, negatives.length - 1);
        print("case 4 negatives", negatives, "[-7, -1, -1, 0, 2, 3]");
    }

    private static void print(String label, int[] actual, String expected) {
        System.out.println(label + ": " + Arrays.toString(actual) + "   expected " + expected);
    }

    /** Sorts array[low..high] in place. */
    public void quickSort(int[] array, int low, int high) {
        if (low < high) {                                  // 0 or 1 element is already sorted
            int partitionIndex = partition(array, low, high);
            quickSort(array, low, partitionIndex - 1);     // pivot itself is now final
            quickSort(array, partitionIndex + 1, high);
        }
    }

    /**
     * Lomuto partition: places array[high] at its final position and returns that index.
     * Invariant: after each step, array[low..index] holds every value seen so far
     * that is <= pivot, and array[index+1..j] holds the values that are > pivot.
     */
    private int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int index = low - 1;               // end of the "<= pivot" prefix (empty at first)

        for (int j = low; j < high; j++) {
            if (array[j] <= pivot) {
                index++;
                swap(array, index, j);     // grow the prefix by one
            }
        }
        swap(array, index + 1, high);      // drop the pivot just after the prefix
        return index + 1;
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}

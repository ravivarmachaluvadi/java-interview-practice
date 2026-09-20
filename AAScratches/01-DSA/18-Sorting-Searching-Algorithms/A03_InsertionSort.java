/*
 * =====================================================================
 *  Insertion Sort                                       Classic | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Sort an int array ascending, in place, by growing a sorted prefix one
 *   element at a time. Input may be empty, sorted, reversed, or full of
 *   duplicates; output is the same array object, sorted.
 *
 * EXAMPLE
 *   [4, 1, 5, 2, 7, 8, 3, 9, 6]  ->  [1, 2, 3, 4, 5, 6, 7, 8, 9]
 *   [1, 2, 3, 4, 5]              ->  [1, 2, 3, 4, 5]   (best case, O(n))
 *   [7, 7, -2, 0]                ->  [-2, 0, 7, 7]
 *
 * APPROACH  (shift the new element into the sorted prefix)
 *   1. Treat arr[0 .. i] as already sorted; start with i = 0.
 *   2. Take the next element, at index i + 1, and walk it leftwards.
 *   3. Stop the moment it is no longer smaller than its left neighbour - the
 *      prefix is sorted, so everything further left is already smaller too.
 *      That break is what gives the O(n) best case on sorted input.
 *   4. Repeat until the prefix covers the whole array.
 *
 *   insertionSortByShifting() is the same algorithm written the canonical way:
 *   hold the value in a temp, slide larger elements one slot right, drop the
 *   value into the hole. One write per shift instead of three per swap.
 *
 * KEY INSIGHT
 *   The sorted prefix is the invariant, and the early break is the payoff: work
 *   is proportional to the number of inversions, not to n^2. That is why
 *   insertion sort is the base case inside real hybrid sorts (Timsort, dual-pivot
 *   quicksort) for small or nearly-sorted runs. Shell sort is literally this
 *   loop with a gap instead of a step of 1.
 *
 * COMPLEXITY
 *   Time  O(n^2)  worst and average (reversed input shifts every element);
 *                 O(n) best case when the array is already sorted.
 *   Space O(1)    in place, one temp variable.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Is it stable? Yes - it stops at the first element that is not strictly
 *     greater, so equal elements keep their order.
 *   - Binary insertion sort: binary-search the insert point. Comparisons drop to
 *     O(n log n) but shifts stay O(n^2), so the wall-clock win is small.
 *   - Why do library sorts fall back to it under ~32 elements? Low constant
 *     factor, no recursion, cache friendly, and nearly-sorted runs are free.
 *   - Adapt it to a linked list: no shifting, but no random access either.
 *
 * RUN
 *   main() runs 3 cases (typical, already sorted, duplicates + negative) on the
 *   swap version, then re-runs the typical and empty cases on the shift version,
 *   printing actual vs expected.
 */
import java.util.Arrays;

class InsertionSort {

    /** Author's version: bubble the new element left with swaps, break when settled. */
    private static void insertionSort(int[] arr) {
        int n = arr.length;
        // i is the last index of the sorted prefix; the element at i + 1 is the new one.
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j > 0; j--) {
                if (arr[j] < arr[j - 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j - 1];
                    arr[j - 1] = temp;
                } else {
                    break; // prefix is sorted, so nothing further left can be bigger
                }
            }
        }
    }

    /** Canonical version: one write per shift instead of a three-write swap. */
    private static void insertionSortByShifting(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j]; // slide the larger element one slot right
                j--;
            }
            arr[j + 1] = key; // drop the key into the hole left behind
        }
    }

    private static void print(String label, int[] actual, int[] expected) {
        System.out.println(label + ": " + Arrays.toString(actual)
                + "   expected " + Arrays.toString(expected));
    }

    public static void main(String[] args) {
        int[] typical = {4, 1, 5, 2, 7, 8, 3, 9, 6};
        insertionSort(typical);
        print("case 1 typical       ", typical, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

        int[] alreadySorted = {1, 2, 3, 4, 5};
        insertionSort(alreadySorted);
        print("case 2 sorted (best) ", alreadySorted, new int[]{1, 2, 3, 4, 5});

        int[] dupsAndNegative = {7, 7, -2, 0};
        insertionSort(dupsAndNegative);
        print("case 3 dups + neg    ", dupsAndNegative, new int[]{-2, 0, 7, 7});

        int[] typical2 = {4, 1, 5, 2, 7, 8, 3, 9, 6};
        insertionSortByShifting(typical2);
        print("case 4 shift version ", typical2, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

        int[] empty = {};
        insertionSortByShifting(empty);
        print("case 5 empty         ", empty, new int[]{});
    }
}

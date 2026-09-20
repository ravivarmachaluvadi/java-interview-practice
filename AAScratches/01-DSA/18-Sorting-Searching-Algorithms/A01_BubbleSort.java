/*
 * =====================================================================
 *  Bubble Sort                                          Classic | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Sort an int array into ascending order in place, using only adjacent swaps.
 *   Input is an arbitrary int array (may be empty, may contain duplicates and
 *   negatives). Output: the same array object, sorted.
 *
 * EXAMPLE
 *   [4, 1, 5, 2, 7, 8, 3, 9, 6]  ->  [1, 2, 3, 4, 5, 6, 7, 8, 9]
 *   []                           ->  []            (loops never run)
 *   [5, 5, -1, 0, -1]            ->  [-1, -1, 0, 5, 5]
 *
 * APPROACH  (adjacent swap, bubble the maximum to the right)
 *   1. Walk j from left to right over the unsorted prefix, comparing arr[j-1]
 *      with arr[j] and swapping when they are out of order.
 *   2. One full inner pass drags the largest remaining value all the way to the
 *      right end of that prefix - it "bubbles up".
 *   3. After pass i the last i slots are final, so the next pass stops i earlier.
 *   4. bubbleSortWithEarlyExit() adds the standard optimisation: if a pass makes
 *      zero swaps the array is already sorted, so stop (best case O(n)).
 *
 * KEY INSIGHT
 *   The loop invariant is the whole algorithm: after pass i, the last i elements
 *   hold the i largest values, already in final position. Every simple sort is
 *   just a different way of growing a sorted region - bubble grows it from the
 *   right using only neighbour swaps, which is why it is stable.
 *
 * COMPLEXITY
 *   Time  O(n^2)  n passes, each scanning up to n elements; O(n) best case
 *                 with the early-exit flag on already-sorted input.
 *   Space O(1)    in place, only a temp variable for the swap.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Is bubble sort stable? Yes - equal neighbours are never swapped.
 *   - How do you get O(n) on sorted input? The swapped flag; show it.
 *   - Why is it worse than insertion sort in practice? Same O(n^2) but many
 *     more writes: insertion shifts once per element, bubble swaps repeatedly.
 *   - When would you ever use it? Almost never; only tiny or nearly-sorted data.
 *
 * RUN
 *   main() runs 3 cases (typical, empty edge case, duplicates + negatives) on
 *   both variants and prints actual vs expected.
 */
import java.util.Arrays;

class BubbleSort {

    /** Author's version: fixed number of passes, no early exit. */
    private static void bubbleSort(int[] arr) {
        int last = arr.length - 1;
        for (int i = 0; i <= last; i++) {
            // Each pass pushes the largest value of the unsorted prefix to index last - i,
            // so the next pass can stop one slot earlier.
            for (int j = 1; j <= last - i; j++) {
                if (arr[j] < arr[j - 1]) {
                    swap(arr, j, j - 1);
                }
            }
        }
    }

    /** Same algorithm plus the classic optimisation: a swap-free pass means sorted. */
    private static void bubbleSortWithEarlyExit(int[] arr) {
        int last = arr.length - 1;
        for (int i = 0; i <= last; i++) {
            boolean swapped = false;
            for (int j = 1; j <= last - i; j++) {
                if (arr[j] < arr[j - 1]) {
                    swap(arr, j, j - 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                return; // nothing moved -> no inversions left anywhere
            }
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private static void print(String label, int[] actual, int[] expected) {
        System.out.println(label + ": " + Arrays.toString(actual)
                + "   expected " + Arrays.toString(expected));
    }

    public static void main(String[] args) {
        int[] typical = {4, 1, 5, 2, 7, 8, 3, 9, 6};
        bubbleSort(typical);
        print("case 1 typical      ", typical, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

        int[] empty = {};
        bubbleSort(empty);
        print("case 2 empty        ", empty, new int[]{});

        int[] dupsAndNegatives = {5, 5, -1, 0, -1};
        bubbleSort(dupsAndNegatives);
        print("case 3 dups + neg   ", dupsAndNegatives, new int[]{-1, -1, 0, 5, 5});

        // Same three inputs through the early-exit variant.
        int[] typical2 = {4, 1, 5, 2, 7, 8, 3, 9, 6};
        bubbleSortWithEarlyExit(typical2);
        print("case 4 early-exit   ", typical2, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

        int[] alreadySorted = {1, 2, 3, 4};
        bubbleSortWithEarlyExit(alreadySorted);
        print("case 5 sorted input ", alreadySorted, new int[]{1, 2, 3, 4});
    }
}

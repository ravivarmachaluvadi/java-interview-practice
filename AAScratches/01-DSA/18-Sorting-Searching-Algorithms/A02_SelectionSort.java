/*
 * =====================================================================
 *  Selection Sort                                       Classic | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Sort an int array ascending, in place, by repeatedly picking the extreme
 *   element of the unsorted region and putting it on the boundary. This file
 *   picks the MAXIMUM and parks it at the right end (the mirror of the usual
 *   pick-the-minimum-and-put-it-left presentation).
 *
 * EXAMPLE
 *   [4, 1, 5, 2, 7, 8, 3, 9, 6]  ->  [1, 2, 3, 4, 5, 6, 7, 8, 9]
 *   [42]                         ->  [42]          (single element, no work)
 *   [2, 2, -3, 2]                ->  [-3, 2, 2, 2]
 *
 * APPROACH  (select the extreme, place it at the boundary)
 *   1. Let the unsorted region be arr[0 .. lastIndex], starting at the whole array.
 *   2. Scan it once to find the index of the largest value (indexOfMax).
 *   3. Swap that value with arr[lastIndex]; it is now in its final position.
 *   4. Shrink lastIndex by one and repeat until the region has one element left.
 *
 * KEY INSIGHT
 *   Fixed(ish) number of writes: exactly one swap per pass, no matter how
 *   scrambled the input. That is the one thing selection sort is good at, and
 *   the reason it shows up when writes are expensive. The "scan for the extreme"
 *   helper is exactly what a heap replaces later - heapify turns that O(n) scan
 *   into an O(log n) sift, which is how heap sort gets to O(n log n).
 *
 * COMPLEXITY
 *   Time  O(n^2)  always - the scan for the max runs even on sorted input,
 *                 so there is no best case to exploit.
 *   Space O(1)    in place; only index and temp variables.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Is it stable? No - the long-distance swap can jump an equal element over
 *     its twin. A shift-based (not swap-based) variant is stable.
 *   - Why does it never beat insertion sort on nearly-sorted data? No early exit.
 *   - Where is it actually preferred? When writes cost far more than reads
 *     (flash wear, EEPROM): O(n) swaps versus insertion sort's O(n^2) shifts.
 *   - How do you turn this into heap sort? Replace the linear max scan with a
 *     max-heap so each extraction is O(log n).
 *
 * FIXED
 *   The original getMaxVal() read maxVal once and never reassigned it inside the
 *   loop, so after the first element larger than arr[0] it stopped tracking the
 *   real maximum. On {4,1,5,2,7,8,3,9,6} it printed [1,2,3,4,5,7,8,9,6].
 *   The helper now compares against arr[indexOfMax], so no stale copy can drift.
 *
 * RUN
 *   main() runs 3 cases (typical, single element, duplicates + negative) and
 *   prints actual vs expected.
 */
import java.util.Arrays;

class SelectionSort {

    private static void selectionSort(int[] arr) {
        // After k passes the last k slots hold the k largest values, already final.
        for (int lastIndex = arr.length - 1; lastIndex > 0; lastIndex--) {
            int indexOfMax = indexOfMax(arr, lastIndex);
            swap(arr, indexOfMax, lastIndex);
        }
    }

    /** Index of the largest value in arr[0 .. lastIndex]. */
    private static int indexOfMax(int[] arr, int lastIndex) {
        int indexOfMax = 0;
        for (int j = 1; j <= lastIndex; j++) {
            // Compare against the current best VALUE, read through its index -
            // this is what the original version forgot to keep in sync.
            if (arr[j] > arr[indexOfMax]) {
                indexOfMax = j;
            }
        }
        return indexOfMax;
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
        selectionSort(typical);
        print("case 1 typical    ", typical, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

        int[] single = {42};
        selectionSort(single);
        print("case 2 single     ", single, new int[]{42});

        int[] dupsAndNegative = {2, 2, -3, 2};
        selectionSort(dupsAndNegative);
        print("case 3 dups + neg ", dupsAndNegative, new int[]{-3, 2, 2, 2});
    }
}

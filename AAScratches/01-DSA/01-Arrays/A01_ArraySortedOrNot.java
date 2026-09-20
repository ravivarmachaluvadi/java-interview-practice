/*
 * =====================================================================
 *  Array Sorted Or Not                                    GfG | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array, return true if it is sorted in non-decreasing
 *   order (each element <= the next), false otherwise. Duplicates next to
 *   each other are allowed. Empty and single-element arrays count as sorted.
 *
 * EXAMPLE
 *   [1, 2, 3, 4, 5, 6]  ->  true
 *   [1, 3, 2]           ->  false   because 3 > 2
 *   [2, 2, 2]           ->  true    equal neighbours are fine
 *   []                  ->  true    nothing to violate
 *   [5]                 ->  true
 *   [6, 5, 4]           ->  false   descending fails at the first pair
 *
 * APPROACH  (adjacent-pair single scan)
 *   1. Walk i from 0 to n - 2 so that arr[i + 1] is always in bounds.
 *   2. If arr[i] > arr[i + 1], the order is broken: return false at once.
 *   3. If the loop finishes without a violation, return true.
 *
 * KEY INSIGHT
 *   "Sorted" is a property of every adjacent pair, so one pass over the
 *   pairs settles it and you can bail out on the first bad pair. This
 *   compare-arr[i]-with-arr[i+1]-and-exit-early loop is the base shape that
 *   monotonic, mountain, bitonic and streak problems all build on.
 *
 * COMPLEXITY
 *   Time  O(n)  each pair is looked at at most once
 *   Space O(1)  only the loop index
 *
 * INTERVIEW FOLLOW-UPS
 *   - Strictly increasing? Change > to >= in the check.
 *   - Sorted in either direction? Track two flags in one pass (see A02).
 *   - Sorted then rotated? Count the number of "drops"; at most one, with wrap.
 *   - Recursive version: isSorted(arr, i) = arr[i] <= arr[i+1] && isSorted(arr, i+1).
 *
 * RUN
 *   main() runs 6 cases (sorted, broken, all equal, empty, single, descending)
 *   and prints actual vs expected.
 */
import java.util.Arrays;

class ArraySortedOrNot {

    static boolean isSorted(int[] arr) {
        // Stop at n - 2 so arr[i + 1] never runs off the end.
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false; // first out-of-order pair settles it
            }
        }
        return true;
    }

    private static void check(int[] arr, boolean expected) {
        System.out.println(Arrays.toString(arr) + " -> " + isSorted(arr)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        check(new int[]{1, 2, 3, 4, 5, 6}, true);   // typical sorted
        check(new int[]{1, 3, 2}, false);           // broken in the middle
        check(new int[]{2, 2, 2}, true);            // all equal, non-decreasing
        check(new int[]{}, true);                   // empty: vacuously sorted
        check(new int[]{5}, true);                  // single element
        check(new int[]{6, 5, 4}, false);           // descending
    }
}

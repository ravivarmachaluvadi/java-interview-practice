/*
 * =====================================================================
 *  Merge Sort                       LeetCode 912 | Medium    MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Sort an int array ascending in O(n log n) worst case. This top-down version
 *   returns a NEW sorted array and leaves the input untouched, which is the
 *   easiest form to write correctly under interview pressure.
 *   Input may be empty, a single element, or full of duplicates.
 *
 * EXAMPLE
 *   [5, 2, 9, 1, 5, 6]  ->  [1, 2, 5, 5, 6, 9]
 *   []                  ->  []            (left > right base case)
 *   [-4, -4, 0, -9]     ->  [-9, -4, -4, 0]
 *
 * APPROACH  (divide and conquer, merge two sorted halves)
 *   1. mergeSort(left, right) owns the slice nums[left .. right].
 *   2. left > right means the slice is empty (only reachable when the whole
 *      array is empty) -> return an empty array. left == right means one
 *      element -> it is already sorted, return it as a one-element array.
 *   3. Split at mid, sort each half recursively, and merge the two results.
 *   4. merge() walks two sorted arrays with two cursors, always copying the
 *      smaller head, then drains whichever array still has elements left.
 *
 * KEY INSIGHT
 *   Merging two sorted arrays is linear, and halving bottoms out in log n
 *   levels, so the total is n work per level times log n levels. The `<=` in
 *   merge (arr1[i] <= arr2[j]) is what makes the sort STABLE: on a tie the left
 *   half wins, so equal elements keep their original order. Flip it to `<` and
 *   you silently break stability - a classic interview trap.
 *   Carrying the empty-range base case (left > right) rather than assuming
 *   left <= right is what makes this version safe on an empty input.
 *
 * COMPLEXITY
 *   Time  O(n log n)  guaranteed - log n levels, O(n) merging per level,
 *                     with no bad-pivot worst case like quicksort has.
 *   Space O(n)        fresh arrays at each merge, plus O(log n) recursion stack.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Make it in place / O(n) auxiliary: allocate ONE scratch buffer up front
 *     and merge into it, instead of a new array per call.
 *   - Bottom-up merge sort: iterative, widths 1, 2, 4, ... - no recursion stack.
 *   - Why does Java use merge sort for objects and quicksort for primitives?
 *     Stability matters for objects; primitives have no identity to preserve.
 *   - Count inversions (or "reverse pairs", LeetCode 493) by counting during the
 *     merge step - the standard divide-and-conquer follow-up.
 *   - Sort a linked list (LeetCode 148): merge sort is the O(1)-space answer.
 *
 * RUN
 *   main() runs 3 cases (typical with duplicates, empty array, all negatives
 *   with duplicates) and prints actual vs expected.
 */
import java.util.Arrays;

class MergeSort {

    public int[] sortArray(int[] nums) {
        return mergeSort(0, nums.length - 1, nums);
    }

    /** Returns a new sorted array holding nums[left .. right]. */
    private int[] mergeSort(int left, int right, int[] nums) {
        if (left > right) {
            // Empty range - only reachable when nums.length == 0.
            return new int[0];
        }
        if (left == right) {
            // Base case: a single element is already sorted.
            return new int[]{nums[left]};
        }

        // left + (right - left) / 2 instead of (left + right) / 2: same value,
        // but cannot overflow int for very large indices.
        int mid = left + (right - left) / 2;
        int[] leftPart = mergeSort(left, mid, nums);
        int[] rightPart = mergeSort(mid + 1, right, nums);

        return merge(leftPart, rightPart);
    }

    /** Merges two already-sorted arrays into one. Stable: ties take from arr1. */
    private int[] merge(int[] arr1, int[] arr2) {
        int m = arr1.length;
        int n = arr2.length;
        int[] result = new int[m + n];
        int i = 0, j = 0, k = 0;

        while (i < m && j < n) {
            // <= (not <) keeps equal elements in their original relative order.
            if (arr1[i] <= arr2[j]) {
                result[k++] = arr1[i++];
            } else {
                result[k++] = arr2[j++];
            }
        }

        // At most one of these drain loops does any work.
        while (i < m) {
            result[k++] = arr1[i++];
        }
        while (j < n) {
            result[k++] = arr2[j++];
        }

        return result;
    }

    private static void print(String label, int[] actual, int[] expected) {
        System.out.println(label + ": " + Arrays.toString(actual)
                + "   expected " + Arrays.toString(expected));
    }

    public static void main(String[] args) {
        MergeSort solution = new MergeSort();

        int[] typical = {5, 2, 9, 1, 5, 6};
        print("case 1 typical    ", solution.sortArray(typical), new int[]{1, 2, 5, 5, 6, 9});
        // The input is never mutated - this version builds new arrays.
        print("case 1 input kept ", typical, new int[]{5, 2, 9, 1, 5, 6});

        int[] empty = {};
        print("case 2 empty      ", solution.sortArray(empty), new int[]{});

        int[] negatives = {-4, -4, 0, -9};
        print("case 3 negatives  ", solution.sortArray(negatives), new int[]{-9, -4, -4, 0});
    }
}

/*
 * =====================================================================
 *  Remove Duplicates from Sorted Array               LeetCode 26 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an int array sorted in non-decreasing order, remove the duplicates in place so
 *   that each value appears exactly once, keeping the original relative order. Return k,
 *   the number of unique values; the first k slots must hold them.
 *
 * EXAMPLE
 *   arr = [1, 1, 2, 2, 2, 3, 3]  ->  k = 3, arr[0..3) = [1, 2, 3]
 *   arr = [5]                    ->  k = 1, arr[0..1) = [5]           (single element)
 *   arr = [7, 7, 7, 7]           ->  k = 1, arr[0..1) = [7]           (all equal)
 *   arr = []                     ->  k = 0                            (empty; see Fixed)
 *
 * APPROACH  (write pointer on a sorted array)
 *   1. lastUnique = 0 points at the last element we have decided to keep. arr[0] is
 *      always kept, so the scan starts at index 1.
 *   2. For each right index from 1 to n-1, compare arr[right] with arr[lastUnique].
 *   3. Because the array is sorted, a value equal to arr[lastUnique] is a duplicate;
 *      a different value is guaranteed new. Advance lastUnique and copy it there.
 *   4. Return lastUnique + 1 (index to count).
 *
 * KEY INSIGHT
 *   The write pointer compares against the LAST KEPT element instead of a fixed
 *   constant (as in Remove Element). Sorting is what makes "differs from the last kept"
 *   equivalent to "never seen before". Without sorting you would need a HashSet.
 *
 * Fixed: an empty array returned lastUnique + 1 = 1 instead of 0. LeetCode guarantees
 *   n >= 1 so it never showed up there, but the guard makes the method safe to call.
 *
 * COMPLEXITY
 *   Time  O(n)  single pass, one comparison per element
 *   Space O(1)  two indices only
 *
 * INTERVIEW FOLLOW-UPS
 *   - Allow each value at most twice (LC 80): compare arr[right] with arr[write - 2]
 *     instead of the last kept; generalises to "at most k copies".
 *   - Unsorted input: the same shape needs a HashSet for "seen before" and O(n) space.
 *   - Count how many duplicates were removed: n - k.
 *
 * RUN
 *   main() runs 4 cases (typical, single element, all equal, empty) and prints
 *   actual vs expected.
 */

import java.util.Arrays;

class RemoveDuplicates {

    // Two-pointer solution: lastUnique writes, right scans.
    static int removeDuplicates(int[] arr) {
        if (arr.length == 0) {
            return 0; // guard: otherwise lastUnique + 1 would report 1 unique value
        }
        int lastUnique = 0; // index of the last element we decided to keep
        for (int right = 1; right < arr.length; right++) {
            if (arr[right] != arr[lastUnique]) { // sorted, so "differs" means "new value"
                lastUnique++;
                arr[lastUnique] = arr[right];
            }
        }
        return lastUnique + 1;
    }

    // Formats "k=<count> kept=[...]" so a single line shows both outputs of the method.
    private static String run(int[] arr) {
        int k = removeDuplicates(arr);
        return "k=" + k + " kept=" + Arrays.toString(Arrays.copyOf(arr, k));
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical  ", run(new int[]{1, 1, 2, 2, 2, 3, 3}), "k=3 kept=[1, 2, 3]");
        print("case 2 single   ", run(new int[]{5}), "k=1 kept=[5]");
        print("case 3 all equal", run(new int[]{7, 7, 7, 7}), "k=1 kept=[7]");
        print("case 4 empty    ", run(new int[]{}), "k=0 kept=[]");
    }
}

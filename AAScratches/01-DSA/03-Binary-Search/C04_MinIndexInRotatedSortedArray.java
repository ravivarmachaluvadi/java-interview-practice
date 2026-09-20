/*
 * =====================================================================
 *  Min Index in Rotated Sorted Array       LeetCode 153 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   A sorted array of distinct integers was rotated some number of times, e.g.
 *   [0,1,2,3,4,5,7,8,9] became [4,5,7,8,9,0,1,2,3]. Return the INDEX of the
 *   minimum element (LC 153 asks for the value; the index is the rotation pivot).
 *   Must run in O(log n).
 *
 * EXAMPLE
 *   arr = [4, 5, 7, 8, 9, 0, 1, 2, 3]  ->  5
 *   arr = [5, 1, 2, 3, 4]              ->  1    (old code returned -1 here)
 *   arr = [1, 2, 3]                    ->  0    not rotated at all
 *   arr = [2, 1]                       ->  1    (old code threw on arr[mid - 1])
 *   arr = [7]                          ->  0    single element
 *
 * APPROACH  (find the rotation pivot)
 *   1. Keep [low, high] as the window that contains the minimum.
 *   2. Shortcut: if arr[low] < arr[high], the window is already sorted and the
 *      minimum is arr[low].
 *   3. Otherwise compare mid with high. If arr[mid] < arr[high], the right half is
 *      sorted and the pivot is at mid or to its left: high = mid (mid may be it).
 *      Else the drop is strictly to the right: low = mid + 1.
 *   4. Loop ends with low == high, which is the pivot.
 *
 * KEY INSIGHT
 *   In a rotated array exactly one half of any window is sorted. Comparing mid to
 *   high tells you which half, and the minimum always lives in the unsorted half
 *   (or is mid itself). The next three files in this folder all start by finding
 *   this pivot.
 *
 * Fixed: mid was computed once before the loop and never updated, and the
 *   arr[mid - 1] neighbour check had no bounds guard. Returned -1 for
 *   [5,1,2,3,4], threw for [2,1], and returned -1 for a single element.
 *
 * COMPLEXITY
 *   Time  O(log n)  window halves each step
 *   Space O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why compare with high and not low? With low, the "not rotated" case is
 *     ambiguous; high always sits at the end of the sorted tail.
 *   - Duplicates allowed (LC 154): when arr[mid] == arr[high] you can only do
 *     high--, so the worst case degrades to O(n).
 *   - Once you have the pivot, search a target in the rotated array (LC 33).
 *
 * RUN
 *   main() runs 5 cases (typical, pivot near start, not rotated, two elements,
 *   single element) and prints actual vs expected.
 */

class MinIndexInRotatedSortedArray {

    private static int getMinIndex(int[] arr) {
        int low = 0, high = arr.length - 1;
        while (low < high) {
            if (arr[low] < arr[high]) {
                return low;          // window already sorted, its first element is the min
            }
            int mid = low + (high - low) / 2;   // recomputed every iteration
            if (arr[mid] < arr[high]) {
                high = mid;          // right half sorted, pivot is at mid or left of it
            } else {
                low = mid + 1;       // drop happens strictly right of mid
            }
        }
        return low;                  // low == high: the pivot (also handles length 1)
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 [4,5,7,8,9,0,1,2,3]", getMinIndex(new int[]{4, 5, 7, 8, 9, 0, 1, 2, 3}), 5);
        print("case 2 [5,1,2,3,4] pivot near start", getMinIndex(new int[]{5, 1, 2, 3, 4}), 1);
        print("case 3 [1,2,3] not rotated", getMinIndex(new int[]{1, 2, 3}), 0);
        print("case 4 [2,1] two elements", getMinIndex(new int[]{2, 1}), 1);
        print("case 5 [7] single", getMinIndex(new int[]{7}), 0);
    }
}

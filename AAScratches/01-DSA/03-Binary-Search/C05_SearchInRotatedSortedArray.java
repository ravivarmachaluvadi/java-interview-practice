/*
 * =====================================================================
 *  Search in Rotated Sorted Array              LeetCode 33 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   An ascending array of DISTINCT integers was rotated at an unknown pivot.
 *   Return the index of target, or -1 if absent. Must run in O(log n).
 *
 * EXAMPLE
 *   [4, 5, 6, 7, 0, 1, 2], target 0  ->  4
 *   [4, 5, 6, 7, 0, 1, 2], target 3  ->  -1
 *   [1], target 0                    ->  -1    single element, not found
 *   [1, 2, 3], target 3              ->  2     no rotation at all
 *   [3, 1], target 1                 ->  1     two elements, pivot at index 1
 *
 * APPROACH  (identify the sorted half)
 *   1. Normal binary search skeleton: low, high, mid. If arr[mid] == target, done.
 *   2. At least one half of [low..high] is sorted. It is the left half when
 *      arr[low] <= arr[mid], otherwise the right half.
 *   3. Look ONLY at the sorted half: if target lies within its min..max range,
 *      shrink into that half; otherwise the target must be in the other half.
 *   4. Repeat until low > high, then return -1.
 *
 * KEY INSIGHT
 *   You cannot decide a direction by comparing target with arr[mid] alone,
 *   because the array is not globally sorted. But one half always IS sorted,
 *   and on a sorted half a range check is a certain answer. So the pattern is:
 *   find the sorted half, test membership there, and go the other way if it
 *   fails. Distinct values guarantee arr[low] <= arr[mid] identifies the half.
 *
 * COMPLEXITY
 *   Time  O(log n)  the range halves every iteration
 *   Space O(1)      three indices
 *
 * INTERVIEW FOLLOW-UPS
 *   - duplicates allowed (LC 81): arr[low] == arr[mid] == arr[high] is ambiguous,
 *     shrink both ends by one; worst case becomes O(n)
 *   - find the minimum / rotation count instead (LC 153)
 *   - alternative: find the pivot first, then binary search in the right piece
 *
 * RUN
 *   main() runs 6 cases (typical found, not found, single element, no rotation,
 *   two elements, target in left half) and prints actual vs expected.
 */
class SearchInRotatedSortedArray {

    public static int search(int[] arr, int target) {
        int low = 0;
        int high = arr.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (arr[mid] == target) {
                return mid;
            }

            // Exactly one of the two halves is guaranteed sorted. Decide which,
            // then use a plain range check on that half.
            if (arr[low] <= arr[mid]) {
                // left half [low..mid] is sorted
                if (arr[low] <= target && target < arr[mid]) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            } else {
                // right half [mid..high] is sorted
                if (arr[mid] < target && target <= arr[high]) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
        }
        return -1;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("case 1 typical found  ", search(new int[]{4, 5, 6, 7, 0, 1, 2}, 0), 4);
        check("case 2 not found      ", search(new int[]{4, 5, 6, 7, 0, 1, 2}, 3), -1);
        check("case 3 single element ", search(new int[]{1}, 0), -1);
        check("case 4 no rotation    ", search(new int[]{1, 2, 3}, 3), 2);
        check("case 5 two elements   ", search(new int[]{3, 1}, 1), 1);
        check("case 6 left half      ", search(new int[]{4, 5, 6, 7, 0, 1, 2}, 5), 1);
    }
}

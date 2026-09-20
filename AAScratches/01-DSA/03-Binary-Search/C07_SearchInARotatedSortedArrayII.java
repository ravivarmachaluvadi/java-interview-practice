/*
 * =====================================================================
 *  Search in Rotated Sorted Array II                  LeetCode 81 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Same as LC 33 (rotated ascending array, find target) but the array MAY
 *   CONTAIN DUPLICATES. Return true if target exists, false otherwise.
 *   Only existence is asked, not the index, because with duplicates the
 *   index would not be unique.
 *
 * EXAMPLE
 *   [2, 5, 6, 0, 0, 1, 2], target 0  ->  true
 *   [2, 5, 6, 0, 0, 1, 2], target 3  ->  false
 *   [1, 1, 1, 1], target 2           ->  false   all equal, degrades to O(n)
 *   [1, 0, 1, 1, 1], target 0        ->  true    low == mid == high trap
 *   [1], target 1                    ->  true    single element
 *
 * APPROACH  (duplicates break the invariant; shrink when ambiguous)
 *   1. Binary search skeleton exactly as in LC 33.
 *   2. If arr[low] == arr[mid] == arr[high], you cannot tell which half is
 *      sorted (example: [1,0,1,1,1]). Shrink both ends by one and retry.
 *   3. Otherwise proceed as in LC 33: arr[low] <= arr[mid] means the left half
 *      is sorted, range-check the target against it, move accordingly.
 *
 * KEY INSIGHT
 *   LC 33 relies on "arr[low] <= arr[mid] implies left half sorted". Duplicates
 *   make the equality case ambiguous: with arr[low] == arr[mid] == arr[high]
 *   the pivot could be in either half. The fix is not cleverness, it is giving
 *   up one element on each side. That is why the worst case is O(n): an array
 *   of all-equal values plus one different element forces a linear shrink.
 *
 * COMPLEXITY
 *   Time  O(log n) average, O(n) worst case (many duplicates)
 *   Space O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - why only shrink by one, not jump? because the pivot could be anywhere
 *     inside the equal run; any bigger jump might skip it
 *   - find the minimum with duplicates (LC 154): same shrink trick on high
 *   - prove the O(n) lower bound: [1,1,1,...,1,0,1,...,1] cannot be done faster
 *
 * RUN
 *   main() runs 5 cases (typical found, not found, all equal, ambiguous
 *   low==mid==high, single element) and prints actual vs expected.
 */
class SearchInARotatedSortedArrayII {

    public static boolean searchInARotatedSortedArrayII(int[] arr, int target) {
        int low = 0, high = arr.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] == target) return true;

            // Ambiguous case: cannot tell which half is sorted. Drop one element
            // from each end; the target (if present) is still inside.
            if (arr[low] == arr[mid] && arr[mid] == arr[high]) {
                low++;
                high--;
                continue;
            }

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
        return false;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("case 1 typical found     ",
                searchInARotatedSortedArrayII(new int[]{2, 5, 6, 0, 0, 1, 2}, 0), true);
        check("case 2 not found         ",
                searchInARotatedSortedArrayII(new int[]{2, 5, 6, 0, 0, 1, 2}, 3), false);
        check("case 3 all equal         ",
                searchInARotatedSortedArrayII(new int[]{1, 1, 1, 1}, 2), false);
        check("case 4 low==mid==high    ",
                searchInARotatedSortedArrayII(new int[]{1, 0, 1, 1, 1}, 0), true);
        check("case 5 single element    ",
                searchInARotatedSortedArrayII(new int[]{1}, 1), true);
    }
}

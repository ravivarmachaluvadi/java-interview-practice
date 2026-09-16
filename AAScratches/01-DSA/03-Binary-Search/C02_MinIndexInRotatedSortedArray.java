/**
 * Finds the index of the minimum element in a rotated sorted array.
 *
 * The algorithm uses binary search: at each step it checks whether the
 * current subarray is already sorted; if so, the low index holds the
 * minimum. Otherwise it narrows the search to the unsorted half by
 * comparing mid with high and adjusting low/high accordingly.
 *
 * Time Complexity: O(log n)
 * Space Complexity: O(1)
 */
class MinIndexInRotatedSortedArray {
    public static void main(String[] args) {
        int[] arr = {4, 5, 7, 8, 9, 0, 1, 2, 3};
        System.out.println(getMinIndex(arr));
    }

    private static int getMinIndex(int[] arr) {
        int low = 0, high = arr.length - 1;
        int mid = (low + high) / 2;
        while (low < high) {
            if (arr[low] < arr[high]) return low;
            if (arr[mid] < arr[mid + 1] && arr[mid] < arr[mid - 1]) {
                return mid;
            }
            // identifying sorted part using mid pointer
            if (arr[mid] < arr[high]) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }

}

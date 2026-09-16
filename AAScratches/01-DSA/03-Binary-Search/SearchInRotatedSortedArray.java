/**
 * Input: nums = [4,5,6,7,0,1,2]
 * <p>
 * target = 0
 * <p>
 * Output: 4
 */
// https://leetcode.com/problems/search-in-rotated-sorted-array/description/
//Search In Rotated Sorted Array not the min element index
class SearchInRotatedSortedArray {
    public static int search(int[] arr, int target) {
        int low = 0;
        int high = arr.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (arr[mid] == target) {
                return mid;
            }
            // we are looking for sorted part only
            // is left part sorted or right part sorted ?
            if (arr[low] <= arr[mid]) {
                if (arr[low] <= target && target <= arr[mid]) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            } else {
                if (arr[mid] <= target && target <= arr[high]) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        System.out.println(search(new int[]{4, 5, 6, 7, 0, 1, 2}, 0)); // 4
    }
}
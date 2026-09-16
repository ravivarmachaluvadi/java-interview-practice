/**
 * Problem: Determine whether a target value exists in a rotated sorted array that may contain duplicates.
 *
 * Approach: Perform a modified binary search. At each step, if the low, mid, and high values are equal,
 * increment low and decrement high to skip duplicates. Otherwise, identify which half is properly sorted
 * and decide whether to search left or right based on the target's range.
 *
 * Time Complexity: O(n) in worst case (when many duplicates cause linear scan), average O(log n).
 * Space Complexity: O(1). 
 */
import java.util.ArrayList;

class SearchInARotatedSortedArrayII {
    public boolean searchInARotatedSortedArrayII(ArrayList<Integer> arr, int target) {
        int n = arr.size();
        int low = 0, high = n - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (arr.get(mid) == target) return true;
            if (arr.get(low).equals(arr.get(mid)) && arr.get(mid).equals(arr.get(high))) {
                low = low + 1;
                high = high - 1;
                continue;
            }
            if (arr.get(low) <= arr.get(mid)) {
                if (arr.get(low) <= target && target <= arr.get(mid)) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            } else {
                if (arr.get(mid) <= target && target <= arr.get(high)) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SearchInARotatedSortedArrayII obj = new SearchInARotatedSortedArrayII();
        ArrayList<Integer> arr = new ArrayList<>();
        arr.add(2);
        arr.add(5);
        arr.add(6);
        arr.add(0);
        arr.add(0);
        arr.add(1);
        arr.add(2);
        int target = 0;
        boolean result = obj.searchInARotatedSortedArrayII(arr, target);
        System.out.println("Target " + target + " found: " + result);
    }
}
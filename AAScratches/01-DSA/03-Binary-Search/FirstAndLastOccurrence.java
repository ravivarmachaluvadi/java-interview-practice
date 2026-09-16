/**
 * Problem: Given a sorted array of integers, find the starting and ending indices
 * of a specified target value. If the target is not present, return [-1, -1].
 *
 * Approach: Use two binary searches—one to locate the first occurrence and one for
 * the last. Each search runs in O(log n) time by narrowing the search window based
 * on comparisons with the middle element.
 *
 * Complexity:
 *   Time:  O(log n) + O(log n) = O(log n)
 *   Space: O(1) auxiliary space (in-place algorithm)
 */
class FirstAndLastOccurrence {

    private int firstOccurrence(int[] nums, int target) {
        int low = 0, high = nums.length - 1;
        int first = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] == target) {
                first = mid;
                high = mid - 1;
            } else if (nums[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return first;
    }

    private int lastOccurrence(int[] nums, int target) {
        int low = 0, high = nums.length - 1;
        int last = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] == target) {
                last = mid;
                low = mid + 1;
            } else if (nums[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return last;
    }

    public int[] searchRange(int[] nums, int target) {
        int first = firstOccurrence(nums, target);
        if (first == -1) return new int[]{-1, -1};
        int last = lastOccurrence(nums, target);
        return new int[]{first, last};
    }

    public static void main(String[] args) {
        FirstAndLastOccurrence solution = new FirstAndLastOccurrence();
        int[] nums = {5, 7, 7, 8, 8, 10};
        int target = 8;

        int[] result = solution.searchRange(nums, target);
        System.out.println("First and Last Occurrence of " + target + ": [" + result[0] + ", " + result[1] + "]");
    }
}

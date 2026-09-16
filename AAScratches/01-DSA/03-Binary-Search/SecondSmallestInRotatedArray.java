/**
 * Finds the second smallest element in a rotated sorted array of distinct integers.
 *
 * The algorithm first locates the index of the minimum element using binary search
 * (O(log n) time).  Because the array is a rotation of an ascending sequence,
 * the element immediately after the minimum (wrapping around to the start if needed)
 * is the second smallest.  This value is returned in O(1) additional work.
 *
 * Time Complexity:   O(log n) – binary search for the minimum.
 * Space Complexity:  O(1) – only a few integer variables are used.
 */
class SecondSmallestInRotatedArray {

    public static int findSecondSmallest(int[] nums) {
        if (nums == null || nums.length < 2)
            throw new IllegalArgumentException("Array should have at least two elements");

        int n = nums.length;
        int smallestIndex = findMinIndex(nums);

        // The 2nd smallest is the next element in the rotated array
        // Handle the case when the smallest element is at the last index
        int secondSmallestIndex = (smallestIndex + 1) % n;
        return nums[secondSmallestIndex];
    }

    private static int findMinIndex(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }

    public static void main(String[] args) {
        int[] nums = {5, 6, 1, 2, 3, 4};
        System.out.println("Second smallest element: " + findSecondSmallest(nums));
    }
}

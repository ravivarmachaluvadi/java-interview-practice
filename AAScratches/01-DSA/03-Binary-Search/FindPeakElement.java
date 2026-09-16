/**
 * Input: nums = [1,2,1,3,5,6,4]
 * Output: 5
 * Explanation: Your function can return either index number 1 where the
 * peak element is 2, or index number 5 where the peak element is 6.
 */
class FindPeakElement {

    public static int findPeakElement(int[] nums) {
        int n = nums.length;

        // Edge case: single element
        if (n == 1) {
            return 0;
        }

        // Check boundaries explicitly
        if (nums[0] > nums[1]) {
            return 0;
        }
        if (nums[n - 1] > nums[n - 2]) {
            return n - 1;
        }

        int low = 1;
        int high = n - 2;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            // Now mid-1 and mid+1 are always valid
            if (nums[mid - 1] < nums[mid] && nums[mid] > nums[mid + 1]) {
                return mid; // peak found
            }

            // Move toward the side with the larger neighbor
            if (nums[mid] < nums[mid + 1]) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        // Theoretically unreachable because a peak is guaranteed,
        // but keep a safe fallback.
        return -1;
    }

    public static void main(String[] args) {
        System.out.println(findPeakElement(new int[]{1, 2, 1, 3, 5, 6, 4}));
    }
}
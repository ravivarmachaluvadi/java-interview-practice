/**
 * Given an array arr of positive integers sorted in a
 * <p>
 * strictly increasing order, and an integer k.
 * <p>
 * Return the kth positive integer that is missing from this array.
 * <p>
 * Example 1:
 * <p>
 * Input: arr = [2,3,4,7,11], k = 5
 * <p>
 * Output: 9
 * <p>
 * Explanation: The missing positive integers are [1,5,6,8,9,10,12,13,...].
 * <p>
 * The 5th missing positive integer is 9.
 */
//https://leetcode.com/problems/kth-missing-positive-number/description/
class KthMissingPositive {

    public static void main(String[] args) {
        KthMissingPositive solution = new KthMissingPositive();
        int[] arr = {2, 3, 4, 7, 11};
        int k = 5;
        System.out.println("findKthPositive "+solution.findKthPositive(arr, k));  // Output: 9
        System.out.println("findKthPositive "+solution.findKthPositiveBS(arr, k));  // Output: 9
    }

    /**
     * 1 <= arr.length <= 1000
     * <p>
     * 1 <= arr[i] <= 1000
     * <p>
     * 1 <= k <= 1000
     */
    public int findKthPositive(int[] arr, int k) {
        int missingCount = 0;
        int expectedNum = 1;
        int index = 0;

        while (missingCount < k) {
            if (index < arr.length && arr[index] == expectedNum) {
                // Move to the next element in the array
                index++;
            } else {
                // Count this as a missing number
                missingCount++;
            }
            // If k missing numbers are found, return the current number
            if (missingCount == k) {
                return expectedNum;
            }
            expectedNum++;  // Check the next expected number
        }
        return -1; // Should not reach here
    }

    public int findKthPositiveBS(int[] arr, int k) {
        int left = 0;
        int right = arr.length - 1;
        while (left <= right) {
            int pivotMid = left + (right - left) / 2;
            int missingBeforeK = arr[pivotMid] - (pivotMid + 1);

            if (missingBeforeK < k) {
                left = pivotMid + 1;
            } else {
                right = pivotMid - 1;
            }
        }
//        high+k+1
        return left + k;
    }
}

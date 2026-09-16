/**
 * <p>Solves the <b>Maximum Subarray Sum</b> problem (LeetCode #53) using
 * {@code Kadane's Algorithm} in O(n) time and O(1) space.</p>
 *
 * <p>Given an integer array {@code nums}, find the contiguous subarray
 * (containing at least one number) with the largest sum, and return that sum.</p>
 *
 * <p><b>Approach:</b></p>
 * <ul>
 *   <li>Maintain a running {@code sum} of the current subarray.</li>
 *   <li>Update {@code maxSum} <i>before</i> any reset, so a lone negative
 *       element is still captured when all elements are negative.</li>
 *   <li>Reset {@code sum} to {@code 0} whenever it goes negative, since a
 *       negative prefix can never help a future subarray.</li>
 * </ul>
 *
 * <pre>
 * Input:  [-2, 1, -3, 4, -1, 2, 1, -5, 4]
 * Output: 6   (subarray [4, -1, 2, 1])
 * </pre>
 */
class KadaneSAlgorithm {

    /**
     * <p>Returns the maximum possible sum of a contiguous subarray of {@code nums}.</p>
     *
     * @param nums non-empty array of integers (may contain negatives)
     * @return the maximum subarray sum
     */
    public int maxSubArray(int[] nums) {
        long maxSum = Long.MIN_VALUE;
        long sum = 0;
        for (int num : nums) {
            sum += num;
            if (sum > maxSum) maxSum = sum;
            if (sum < 0) sum = 0;
        }
        return (int) maxSum;
    }

    public static void main(String[] args) {
        KadaneSAlgorithm ksa = new KadaneSAlgorithm();

        int[] mixed = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println("Maximum subarray sum: " + ksa.maxSubArray(mixed)); // 6

        int[] allNegative = {-3, -1, -4, -1, -5};
        System.out.println("Maximum subarray sum: " + ksa.maxSubArray(allNegative)); // -1

        int[] single = {7};
        System.out.println("Maximum subarray sum: " + ksa.maxSubArray(single)); // 7
    }
}

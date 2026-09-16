/**
 * Problem:
 *   Given an integer array, find the maximum possible sum of a non‑empty contiguous subarray
 *   after deleting at most one element from that subarray.
 *
 * Approach:
 *   Use dynamic programming with two states per index:
 *     - noDelete[i]: max subarray sum ending at i without any deletion.
 *     - withDelete[i]: max subarray sum ending at i having deleted exactly one element.
 *   Transition:
 *     noDelete[i]  = max(noDelete[i-1] + arr[i], arr[i])
 *     withDelete[i]= max(noDelete[i-1],          // delete current element
 *                       withDelete[i-1] + arr[i]) // extend previous deleted subarray
 *   Track the global maximum across both states.
 *
 * Complexity:
 *   Time:  O(n) – single pass over the array.
 *   Space: O(1) – only constant extra variables are needed (arrays can be replaced by two ints).
 */
class MaximumSubarraySumWithOneDeletion {
    public static int maximumSum(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        int[] noDelete = new int[arr.length];
        int[] withDelete = new int[arr.length];

        noDelete[0] = arr[0];
        withDelete[0] = Integer.MIN_VALUE;

        int maxSum = arr[0];

        for (int i = 1; i < arr.length; i++) {
            // keep maintain preFixSum and update
            noDelete[i] = Math.max(noDelete[i - 1] + arr[i], arr[i]);
            withDelete[i] = Math.max(noDelete[i - 1] // considring deleting current one
                    , withDelete[i - 1] + arr[i]// some earlier element deleted including curr one);
            maxSum = Math.max(maxSum, Math.max(noDelete[i], withDelete[i]));
        }

        return maxSum;
    }

    public static void main(String[] args) {
        // Example input:
        int[] arr = {1, -2, 0, 3};
        // Expected output: 4
        // The maximum sum is obtained by deleting the element -2 and taking the subarray [1, 0, 3].

        int result = maximumSum(arr);
        System.out.println("Maximum subarray sum with one deletion: " + result);
    }
}

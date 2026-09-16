/**
 * Problem:
 *   Find the maximum product of any contiguous subarray within a given integer array.
 *
 * Approach:
 *   Iterate through the array while maintaining two running products: one from the
 *   left (pre) and one from the right (suff). Reset each to 1 whenever a zero is
 *   encountered, then update the answer with the maximum of pre and suff at each step.
 *
 * Complexity:
 *   Time O(n) – single pass over the array.
 *   Space O(1) – constant auxiliary space.
 */
class MaxProductSubArray {
    public static int maxProductSubArray(int[] arr) {
        int n = arr.length; //size of array.

        int pre = 1, suff = 1;
        int ans = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            if (pre == 0) pre = 1;
            if (suff == 0) suff = 1;
            pre *= arr[i];
            suff *= arr[n - 1 - i];
            ans = Math.max(ans, Math.max(pre, suff));
        }
        return ans;
    }

    public static void main(String[] args) {
        int[] arr = {1, 2, -3, 0, -4, -5};
        int answer = maxProductSubArray(arr);
        System.out.println("The maximum product subarray is: " + answer);
    }
}


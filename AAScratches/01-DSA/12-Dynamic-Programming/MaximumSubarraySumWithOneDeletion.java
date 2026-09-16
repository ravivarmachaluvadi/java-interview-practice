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

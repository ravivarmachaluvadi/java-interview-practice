class MinSubArrayExceedsSumSlidingWindow {
    // sliding window or two pointer problem
    public static int subArrayExceedsSum(int[] arr, int target) {
        int minLength = Integer.MAX_VALUE;
        int currentSum = 0;
        int start = 0; // or start as left
        // or end as right
        for (int end = 0; end < arr.length; end++) {
            currentSum += arr[end];

            // Shrink the window as long as currentSum >= target
            while (currentSum >= target) {
                minLength = Math.min(minLength, end - start + 1);
                currentSum -= arr[start];
                start++;
            }
        }
        return (minLength == Integer.MAX_VALUE) ? -1 : minLength;
    }

    public static void main(String[] args) {
        boolean result = true;
        int[] arr = {1, 2, 3, 4};

        result = result && subArrayExceedsSum(arr, 6) == 2;
        result = result && subArrayExceedsSum(arr, 12) == -1;

        if (result) {
            System.out.println("All tests pass\n");
        } else {
            System.out.println("There are test failures\n");
        }
    }
}

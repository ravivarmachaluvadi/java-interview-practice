/**
 * Problem: Given a positive integer array and a target sum, find the length of the smallest contiguous subarray
 * whose elements sum to at least the target. Return -1 if no such subarray exists.
 *
 * Approach: Use a sliding window with two pointers (start, end). Expand the window by moving 'end' forward,
 * adding values to currentSum. When currentSum reaches or exceeds the target, shrink from the left
 * while maintaining the condition, updating the minimal length found.
 *
 * Time Complexity: O(n), each element is added and removed at most once.
 * Space Complexity: O(1) auxiliary space (only a few integer variables).
 */
class ImportantMinSubArrayExceedsSumSlidingWindow {
    public static int subArrayExceedsSum(int[] arr, int target) {
        int minLength = Integer.MAX_VALUE;
        int currentSum = 0;
        int start = 0;
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

    /**
     * Execution entry point.
     */
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

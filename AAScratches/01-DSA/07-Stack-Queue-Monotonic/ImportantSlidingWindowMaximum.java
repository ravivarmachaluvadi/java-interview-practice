import java.util.*;

/**
 * The deque stores indices of elements, but it is maintained in such a way that:
 * <p>
 * The front (head) of deque always holds the index of the maximum element in the current window.
 * <p>
 * The deque itself is in decreasing order of values (nums[index]).
 */
class ImportantSlidingWindowMaximum {
    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || k <= 0) return new int[0];
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new LinkedList<>();

        for (int i = 0; i < n; i++) {
            // Remove elements from the front if they are outside the current window range
            // moving left pointer
            if (!deque.isEmpty() && deque.peekFirst() < i - k + 1)
                deque.pollFirst();

            // Remove elements from the back that are smaller than the current element
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i])
                deque.pollLast();

            deque.offer(i);

            // If we have processed at least 'k' elements, the front of the deque is the max
            if (i >= k - 1)
                result[i - k + 1] = nums[deque.peekFirst()];
        }
        return result;
    }

    public static void main(String[] args) {
        ImportantSlidingWindowMaximum solution = new ImportantSlidingWindowMaximum();
        int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;
        int[] result = solution.maxSlidingWindow(nums, k);
        System.out.println("The maximum values in each sliding window are: " + Arrays.toString(result));
        // Output: [3, 3, 5, 5, 6, 7]
    }
}

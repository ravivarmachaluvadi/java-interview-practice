// LeetCode Problem Link: https://leetcode.com/problems/sliding-window-median/

import java.util.*;
/**
 * Example 1:
 * <p>
 * Input: nums = [1,3,-1,-3,5,3,6,7], k = 3
 * Output: [1.00000,-1.00000,-1.00000,3.00000,5.00000,6.00000]
 * Explanation:
 * Window position                Median
 * ---------------                -----
 * [1  3  -1] -3  5  3  6  7        1
 * 1 [3  -1  -3] 5  3  6  7       -1
 * 1  3 [-1  -3  5] 3  6  7       -1
 * 1  3  -1 [-3  5  3] 6  7        3
 * 1  3  -1  -3 [5  3  6] 7        5
 * 1  3  -1  -3  5 [3  6  7]       6
 */
class SlidingWindowMedian {

    public static double[] medianSlidingWindow(int[] nums, int k) {
        // The result array to store the medians
        double[] result = new double[nums.length - k + 1];

        // Two heaps: maxHeap stores the smaller half, minHeap stores the larger half
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder()); // max-heap
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(); // min-heap

        for (int i = 0; i < nums.length; i++) {
            // Add the current element to the heaps
            add(nums[i], maxHeap, minHeap);

            // Balance the heaps if necessary
            balanceHeaps(maxHeap, minHeap);

            // If the window size is reached, compute the median
            if (i >= k - 1) {
                result[i - k + 1] = getMedian(maxHeap, minHeap);
                // Remove the element that is sliding out of the window
                remove(nums[i - k + 1], maxHeap, minHeap);
                balanceHeaps(maxHeap, minHeap); // Balance after removal
            }
        }
        return result;
        // 1.0 -1.0 -1.0 3.0 5.0 6.0
    }

    // Adds a number to one of the heaps
    private static void add(int num, PriorityQueue<Integer> maxHeap,
                            PriorityQueue<Integer> minHeap) {
        if (maxHeap.isEmpty() || num <= maxHeap.peek()) {
            maxHeap.offer(num);
        } else {
            minHeap.offer(num);
        }
    }

    // Balances the heaps so that maxHeap can only contain at most one more element than minHeap
    private static void balanceHeaps(PriorityQueue<Integer> maxHeap,
                                     PriorityQueue<Integer> minHeap) {
        // one extra element allowed in maxHeap if more than one then balance
        if (maxHeap.size() > minHeap.size() + 1) {
            minHeap.offer(maxHeap.poll());
        } else if (minHeap.size() > maxHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }

    // Returns the current median
    private static double getMedian(PriorityQueue<Integer> maxHeap,
                                    PriorityQueue<Integer> minHeap) {
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        } else {
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        }
    }

    // Removes a number from one of the heaps
    private static void remove(int num, PriorityQueue<Integer> maxHeap,
                               PriorityQueue<Integer> minHeap) {
        if (num <= maxHeap.peek()) {
            maxHeap.remove(num);
        } else {
            minHeap.remove(num);
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        // Example input
        int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;

        // Output the sliding window medians
        double[] result = medianSlidingWindow(nums, k);

        // Print the result
        System.out.println("Sliding Window Medians:");
        for (double median : result) {
            System.out.print(median + " ");
        }
    }
}

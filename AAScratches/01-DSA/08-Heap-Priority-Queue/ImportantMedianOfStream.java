/**
 * Problem:  Compute the median of a stream of integers as numbers are added one by one.
 *
 * Approach: Maintain two heaps – a max‑heap for the lower half and a min‑heap for the upper half.
 * When inserting, place the number in the appropriate heap and rebalance so that the size difference
 * never exceeds one. The median is either the top of the larger heap or the average of both tops.
 *
 * Time Complexity: O(log n) per insertion; O(1) to retrieve the median.
 * Space Complexity: O(n) for storing all elements in the two heaps.
 */
import java.util.Collections;
import java.util.PriorityQueue;

class ImportantMedianOfStream {
    private PriorityQueue<Integer> left = new PriorityQueue<>(Collections.reverseOrder());
    private PriorityQueue<Integer> right = new PriorityQueue<>();

    public void addNum(int num) {
        if (left.isEmpty() || left.peek() > num) left.offer(num);
        else right.offer(num);

        if (left.size() - right.size() > 1) right.offer(left.poll());
        else if (left.size() < right.size()) left.offer(right.poll());
    }

    public double findMedian() {
        if (left.size() == right.size())
            return (left.peek() + right.peek()) / 2.0;
        else
            return (double) left.peek();
    }

    public static void main(String[] args) {
        ImportantMedianOfStream ims = new ImportantMedianOfStream();
        int[] nums = {5, 15, 1, 3};
        System.out.print("Input: ");
        for (int n : nums) System.out.print(n + " ");
        System.out.println();
        for (int n : nums) ims.addNum(n);
        double median = ims.findMedian();
        System.out.println("Output (median): " + median);
    }
}

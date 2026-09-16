import java.util.PriorityQueue;

// https://leetcode.com/problems/kth-largest-element-in-a-stream/
class KthLargestElementInAStream {
    private PriorityQueue<Integer> minHeap;
    private int k;

    public void KthLargest(int k, int[] nums) {
        this.k = k;
        minHeap = new PriorityQueue<>(k);  // min-heap to store k largest elements
        for (int num : nums) {
            add(num);  // add existing elements to the stream
        }
    }

    public int add(int val) {
        if (minHeap.size() < k) {
            // if heap size is less than k, just add the element
            minHeap.offer(val);
        } else if (val > minHeap.peek()) {
            // remove the smallest element
            minHeap.poll();
            // add the new larger element
            minHeap.offer(val);
        }
        return minHeap.peek();  // the root is the kth largest element
    }
}

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

    public static void main(String[] args) {
        int k = 3;
        int[] initialNums = {4, 5, 8, 2};
        KthLargestElementInAStream kth = new KthLargestElementInAStream();
        kth.KthLargest(k, initialNums);
        System.out.println("Initial stream: " + java.util.Arrays.toString(initialNums));
        System.out.println("k = " + k + ", current kth largest after initialization: " + kth.add(0)); // dummy add to get current kth
        int[] newVals = {3, 5, 10, 9};
        for (int val : newVals) {
            int kthLargest = kth.add(val);
            System.out.println("After adding " + val + ", kth largest is: " + kthLargest);
        }
    }
}

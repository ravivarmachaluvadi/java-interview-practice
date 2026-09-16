/**
 * Problem: Given an integer array nums and an integer k, return the k most frequent elements.
 *
 * Approach:
 * 1. Count frequencies of each number using a HashMap.
 * 2. Maintain a min-heap (PriorityQueue) of size at most k containing map entries
 *    sorted by frequency; evict the smallest when size exceeds k.
 * 3. Extract keys from the heap into an array and return it.
 *
 * Time Complexity: O(n log k), where n is nums.length, because each insertion into
 * the heap costs O(log k) and we perform at most n insertions.
 *
 * Space Complexity: O(m + k), where m is the number of distinct elements in nums;
 * we store frequencies in a map (O(m)) and up to k entries in the heap.
 */
import java.util.*;

class TopKFrequent {

    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }

        PriorityQueue<Map.Entry<Integer, Integer>> minHeap =
                new PriorityQueue<>((a, b) -> a.getValue() - b.getValue());

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        int[] arr = new int[k];
        int i = 0;
        while (!minHeap.isEmpty()) {
            arr[i++] = minHeap.poll().getKey();
        }
        return arr;
    }

    public static void main(String[] args) {
        TopKFrequent sol = new TopKFrequent();
        int[] nums = {1, 1, 1, 2, 2, 3};
        int k = 2;
        int[] topK = sol.topKFrequent(nums, k);
        System.out.println(Arrays.toString(topK)); // Output: [1, 2]
    }
}

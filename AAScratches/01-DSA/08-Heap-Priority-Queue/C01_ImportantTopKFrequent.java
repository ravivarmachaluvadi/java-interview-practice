/**
 * Problem: Given an integer array nums and an integer k, return the k most frequent elements.
 *
 * Approach: Count frequencies with a HashMap, then maintain a min-heap (PriorityQueue) of size at most k
 * to keep the top k entries by frequency. Finally extract keys from the heap into the result array.
 *
 * Time Complexity: O(n log k), where n is nums.length (building the map is O(n), each heap operation is O(log k)).
 * Space Complexity: O(n) for the frequency map plus O(k) for the heap and result array.
 */
import java.util.*;

class ImportantTopKFrequent {

    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int num : nums)
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);

        PriorityQueue<Map.Entry<Integer, Integer>> minHeap = new PriorityQueue<>(
                (a, b) -> a.getValue() - b.getValue());
        // Sort by frequency (map values)

        //PriorityQueue<Map.Entry<Integer, Integer>> minHeap = new PriorityQueue<>(
        //                Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            minHeap.add(entry);
            if (minHeap.size() > k)
                minHeap.poll();
        }

        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--)
            result[i] = minHeap.poll().getKey();

        return result;
    }

    public static void main(String[] args) {
        ImportantTopKFrequent sol = new ImportantTopKFrequent();
        int[] nums = {1, 1, 1, 2, 2, 3};
        int k = 2;
        int[] topK = sol.topKFrequent(nums, k);
        System.out.println(Arrays.toString(topK)); // Output: [1, 2]
    }
}

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

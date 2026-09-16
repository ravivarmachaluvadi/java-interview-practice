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

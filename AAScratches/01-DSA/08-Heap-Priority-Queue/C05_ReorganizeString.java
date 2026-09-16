import java.util.*;

class ReorganizeString {

    /**
     * Example 1:
     * <p>
     * Input: s = "aab"
     * Output: "aba"
     * Example 2:
     * <p>
     * Input: s = "aaab"
     * Output: ""
     */
    // use Max heap with dual poll of Map.Entry<Character, Integer>
    public static String reorganizeString(String s) {
        if (s == null || s.length() <= 1) {
            return s;
        }
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : s.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<Map.Entry<Character, Integer>> maxHeap = new PriorityQueue<>(
                (a, b) -> b.getValue() - a.getValue()
        );

        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            maxHeap.offer(entry);
        }

        // edge case of more than half of characters of same type
        if (maxHeap.peek().getValue() > (s.length() + 1) / 2) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        while (maxHeap.size() > 1) {
            Map.Entry<Character, Integer> first = maxHeap.poll();
            Map.Entry<Character, Integer> second = maxHeap.poll();

            result.append(first.getKey());
            result.append(second.getKey());

            if (first.getValue() > 1) {
                maxHeap.offer(new AbstractMap.SimpleEntry<>(first.getKey(), first.getValue() - 1));
            }
            if (second.getValue() > 1) {
                maxHeap.offer(new AbstractMap.SimpleEntry<>(second.getKey(), second.getValue() - 1));
            }
        }
        // edge case of single last char entry
        if (!maxHeap.isEmpty()) {
            result.append(maxHeap.poll().getKey());
        }
        return result.toString();
    }

    public static void main(String[] args) {
        String s = "aab";

        String result = reorganizeString(s);
        System.out.println("Reorganized string: " + result);
    }
}

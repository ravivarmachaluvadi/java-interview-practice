/*
 * =====================================================================
 *  Last Stone Weight                                  LeetCode 1046 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   stones[i] is the weight of the ith stone. Each turn take the two heaviest stones
 *   x <= y and smash them: if x == y both vanish, otherwise y becomes y - x and x vanishes.
 *   Return the weight of the last stone left, or 0 if none remain.
 *   1 <= stones.length <= 30, 1 <= stones[i] <= 1000.
 *
 * EXAMPLE
 *   [2, 7, 4, 1, 8, 1] -> 1    8&7 -> 1; 4&2 -> 2; 2&1 -> 1; 1&1 -> 0; left [1]
 *   [1]                -> 1    nothing to smash
 *   [2, 2]             -> 0    equal stones destroy each other
 *   [10, 4, 4]         -> 2    10&4 -> 6; 6&4 -> 2
 *
 * APPROACH  (max-heap simulation loop)
 *   1. Put every stone into a max-heap (PriorityQueue with Collections.reverseOrder()).
 *   2. While at least two stones remain: poll the two largest.
 *      If they differ, push back the difference; if equal, push nothing.
 *   3. When the loop ends the heap holds 0 or 1 stones: return that stone or 0.
 *
 * KEY INSIGHT
 *   The rule "always act on the two largest" is exactly what a max-heap serves in O(log n).
 *   Recognise the shape "poll two, push one back" -- it recurs in Huffman coding and
 *   Reorganize String. Java has no max-heap class; reverseOrder() on a min-heap is it.
 *
 * COMPLEXITY
 *   Time  O(n log n)   each smash removes at least one stone, each heap op is O(log n)
 *   Space O(n)         the heap
 *
 * INTERVIEW FOLLOW-UPS
 *   - Weights bounded by 1000: counting-sort / bucket approach gives O(n + maxWeight).
 *   - Last Stone Weight II (LC 1049): pick ANY two stones -> becomes subset-sum DP, not greedy.
 *   - Why is the answer at most one stone? Every turn removes at least one stone.
 *
 * RUN
 *   main() runs 4 cases (typical, single stone, equal pair, empty) and prints
 *   actual vs expected.
 */
import java.util.Collections;
import java.util.PriorityQueue;

// https://leetcode.com/problems/last-stone-weight/
class LastStoneWeight {
    public static int lastStoneWeight(int[] stones) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        for (int stone : stones) {
            maxHeap.add(stone);
        }
        while (maxHeap.size() > 1) {
            int heaviest = maxHeap.poll();
            int secondHeaviest = maxHeap.poll();
            if (heaviest != secondHeaviest) {
                maxHeap.add(heaviest - secondHeaviest);   // the survivor goes back in
            }
        }
        return maxHeap.isEmpty() ? 0 : maxHeap.poll();
    }

    public static void main(String[] args) {
        System.out.println("case 1: " + lastStoneWeight(new int[]{2, 7, 4, 1, 8, 1})
                + "   expected 1");
        System.out.println("case 2: " + lastStoneWeight(new int[]{1}) + "   expected 1");
        System.out.println("case 3: " + lastStoneWeight(new int[]{2, 2}) + "   expected 0");
        System.out.println("case 4: " + lastStoneWeight(new int[]{}) + "   expected 0");
    }
}

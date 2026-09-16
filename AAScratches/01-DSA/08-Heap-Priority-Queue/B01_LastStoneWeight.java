import java.util.*;

/**
 * You are given an array of integers stones where stones[i] is the weight of the ith stone.
 * <p>
 * We are playing a game with the stones. On each turn, we choose the heaviest two stones
 * <p>
 * and smash them together. Suppose the heaviest two stones have weights x and y with x <= y.
 * <p>
 * The result of this smash is:
 * <p>
 * If x == y, both stones are destroyed, and
 * <p>
 * If x != y, the stone of weight x is destroyed, and the stone of weight y has new weight y - x.
 * <p>
 * At the end of the game, there is at most one stone left.
 * <p>
 * Return the weight of the last remaining stone. If there are no stones left, return 0.
 */
// https://leetcode.com/problems/last-stone-weight/
//1046. Last Stone Weight
class LastStoneWeight {
    public static int lastStoneWeight(int[] stones) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        for (int stone : stones) maxHeap.add(stone);
        while (maxHeap.size() > 1) {
            int stone1 = maxHeap.poll();
            int stone2 = maxHeap.poll();
            if (stone1 != stone2) maxHeap.add(stone1 - stone2);
        }
        return maxHeap.isEmpty() ? 0 : maxHeap.poll();
    }

    public static void main(String[] args) {
        System.out.println(lastStoneWeight(new int[]{2, 7, 4, 1, 8, 1}));// 1
    }
}

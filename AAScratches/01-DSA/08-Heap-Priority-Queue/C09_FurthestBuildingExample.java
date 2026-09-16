import java.util.PriorityQueue;

// https://leetcode.com/problems/furthest-building-you-can-reach/description/
// 1642. Furthest Building You Can Reach
import java.util.PriorityQueue;
class Solution {
    public int furthestBuilding(int[] heights, int bricks, int ladders) {
        PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> b - a); // Max heap
        int n = heights.length;
        int i;
        for (i = 0; i < n - 1; i++) {
            int diff = heights[i + 1] - heights[i];
            if (diff > 0) {
                if (bricks >= diff) {
                    // Use bricks first
                    bricks -= diff;
                    pq.offer(diff);
                } else if (ladders > 0) {
                    // Use ladder when bricks not enough
                    if (!pq.isEmpty() && pq.peek() > diff) {
                        // Replace the largest brick usage with ladder
                        int max = pq.poll();
                        bricks += max - diff;
                        pq.offer(diff);
                    }
                    ladders--;
                } else {
                    // No resources left
                    break;
                }
            }
        }
        return i;
    }
}
class FurthestBuildingExample {

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Example 1
        int[] heights1 = {4, 2, 7, 6, 9, 14, 12};
        int bricks1 = 5;
        int ladders1 = 1;
        int result1 = sol.furthestBuilding(heights1, bricks1, ladders1);
        System.out.println("Example 1 -> Furthest building index: " + result1);
        // Expected Output: 4

        // Example 2
        int[] heights2 = {4, 12, 2, 7, 3, 18, 20, 3, 19};
        int bricks2 = 10;
        int ladders2 = 2;
        int result2 = sol.furthestBuilding(heights2, bricks2, ladders2);
        System.out.println("Example 2 -> Furthest building index: " + result2);
        // Expected Output: 7

        // Example 3
        int[] heights3 = {14, 3, 19, 3};
        int bricks3 = 17;
        int ladders3 = 0;
        int result3 = sol.furthestBuilding(heights3, bricks3, ladders3);
        System.out.println("Example 3 -> Furthest building index: " + result3);
        // Expected Output: 3
    }
}

import java.util.PriorityQueue;

// 1642. Furthest Building You Can Reach
// https://leetcode.com/problems/furthest-building-you-can-reach/description/
class FurthestBuildingYouCanReach {

    public static int furthestBuilding(int[] heights, int bricks, int ladders) {
        // Min-heap to keep track of climbs where we've “used” a ladder (tentatively)
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        int n = heights.length;

        for (int i = 0; i < n - 1; i++) {
            int diff = heights[i + 1] - heights[i];
            if (diff > 0) {
                // record this climb
                minHeap.offer(diff);
                // if we have used more ladders than allowed,
                // convert the smallest ladder-use to bricks
                if (minHeap.size() > ladders) {
                    int smallestClimb = minHeap.poll();
                    bricks -= smallestClimb;
                }
                // if bricks run out, we can't move further
                if (bricks < 0) {
                    return i;
                }
            }
            // if diff <= 0: no resources needed, just move on
        }

        // if we traversed all, return last index
        return n - 1;
    }

    public static void main(String[] args) {
        // Example 1
        int[] heights1 = {4, 2, 7, 6, 9, 14, 12};
        int bricks1 = 5;
        int ladders1 = 1;
        System.out.println("Example 1 Output: " + furthestBuilding(heights1, bricks1, ladders1));
        // Expected: 4

        // Example 2
        int[] heights2 = {4, 12, 2, 7, 3, 18, 20, 3, 19};
        int bricks2 = 10;
        int ladders2 = 2;
        System.out.println("Example 2 Output: " + furthestBuilding(heights2, bricks2, ladders2));
        // Expected: 7

        // Example 3
        int[] heights3 = {14, 3, 19, 3};
        int bricks3 = 17;
        int ladders3 = 0;
        System.out.println("Example 3 Output: " + furthestBuilding(heights3, bricks3, ladders3));
        // Expected: 3
    }
}

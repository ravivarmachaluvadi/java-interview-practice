import java.util.Collections;
import java.util.PriorityQueue;

/**
 * Problem: LeetCode 1642. Furthest Building You Can Reach
 * https://leetcode.com/problems/furthest-building-you-can-reach/description/
 * Given building heights, `bricks` and `ladders`, climbing up by `diff` costs either `diff` bricks
 * or one ladder. Return the furthest index reachable (0-based). Moving down costs nothing.
 *
 * Approaches:
 *   1. laddersFirstMinHeap  - min-heap of climbs tentatively paid with ladders; once more than
 *                             `ladders` climbs are held, the SMALLEST one is downgraded to bricks.
 *   2. bricksFirstMaxHeap   - max-heap of climbs paid with bricks; when bricks run short, the LARGEST
 *                             brick-paid climb is upgraded to a ladder and its bricks refunded.
 * Both are O(n log n) time, O(ladders) / O(n) heap space, and give the same answer:
 * ladders should always end up on the biggest climbs, bricks on the smallest.
 */
class FurthestBuildingYouCanReach {

    /**
     * Approach 1: ladders first, min-heap.
     * Every climb is first assigned a ladder. When we hold more climbs than ladders,
     * the smallest held climb is the cheapest one to pay with bricks instead.
     */
    public static int laddersFirstMinHeap(int[] heights, int bricks, int ladders) {
        // Min-heap of climbs where we've "used" a ladder (tentatively)
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        int n = heights.length;

        for (int i = 0; i < n - 1; i++) {
            int diff = heights[i + 1] - heights[i];
            if (diff > 0) {
                // record this climb as a ladder use
                minHeap.offer(diff);
                // if we have used more ladders than allowed,
                // convert the smallest ladder-use to bricks
                if (minHeap.size() > ladders) {
                    bricks -= minHeap.poll();
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

    /**
     * Approach 2: bricks first, max-heap.
     * Pay every climb with bricks while they last. When bricks fall short, spend a ladder,
     * but put it on the LARGEST brick-paid climb so far (refunding those bricks) if that climb is
     * bigger than the current one - otherwise use the ladder on the current climb directly.
     */
    public static int bricksFirstMaxHeap(int[] heights, int bricks, int ladders) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        int n = heights.length;
        int i;
        for (i = 0; i < n - 1; i++) {
            int diff = heights[i + 1] - heights[i];
            if (diff <= 0) continue;              // downhill or flat: free

            if (bricks >= diff) {
                // Use bricks first
                bricks -= diff;
                maxHeap.offer(diff);
            } else if (ladders > 0) {
                // Use a ladder when bricks are not enough
                if (!maxHeap.isEmpty() && maxHeap.peek() > diff) {
                    // Replace the largest brick usage with the ladder; pay this smaller climb with bricks
                    int max = maxHeap.poll();
                    bricks += max - diff;
                    maxHeap.offer(diff);
                }
                // else: ladder goes on the current climb
                ladders--;
            } else {
                // No resources left
                break;
            }
        }
        return i;
    }

    private static void run(String label, int[] heights, int bricks, int ladders, int expected) {
        System.out.println(label + " (bricks=" + bricks + ", ladders=" + ladders + ") expected=" + expected);
        System.out.println("  laddersFirstMinHeap -> " + laddersFirstMinHeap(heights, bricks, ladders));
        System.out.println("  bricksFirstMaxHeap  -> " + bricksFirstMaxHeap(heights, bricks, ladders));
    }

    public static void main(String[] args) {
        run("Example 1", new int[]{4, 2, 7, 6, 9, 14, 12}, 5, 1, 4);
        run("Example 2", new int[]{4, 12, 2, 7, 3, 18, 20, 3, 19}, 10, 2, 7);
        run("Example 3", new int[]{14, 3, 19, 3}, 17, 0, 3);
        run("Example 4 (ladders only)", new int[]{1, 5, 1, 2, 3, 4, 10000}, 0, 5, 6);
    }
}

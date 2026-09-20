/*
 * =====================================================================
 *  Furthest Building You Can Reach                   LeetCode 1642 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   heights[i] is the height of building i. Moving to a lower or equal building
 *   is free. Moving up by diff costs either diff bricks or exactly one ladder
 *   (a ladder covers any height). Given bricks and ladders, return the furthest
 *   0-based index you can reach.
 *
 * EXAMPLE
 *   [4,2,7,6,9,14,12], bricks 5, ladders 1     ->  4   climbs 5,3,5: ladder + 5 bricks cover
 *                                                      two of them, the third 5 is unpaid
 *   [4,12,2,7,3,18,20,3,19], bricks 10, ladders 2 ->  7
 *   [14,3,19,3], bricks 17, ladders 0           ->  3   bricks alone cover the climb of 16
 *   [1,5,1,2,3,4,10000], bricks 0, ladders 5    ->  6   five ladders for five climbs
 *   [5], bricks 0, ladders 0                    ->  0   single building, nothing to climb
 *
 * APPROACH  (regret heap of size = ladders)  -- laddersFirstMinHeap
 *   1. For each upward step diff, tentatively spend a ladder: push diff into a min-heap.
 *   2. If the heap now holds more climbs than there are ladders, the smallest held
 *      climb is the cheapest one to downgrade: pop it and pay it with bricks.
 *   3. If bricks go negative, this step is unaffordable: return the current index.
 *   4. If the loop finishes, every building was reached: return n - 1.
 *
 * ALTERNATIVE  (bricks first, max-heap)  -- bricksFirstMaxHeap
 *   Pay every climb with bricks and record it in a max-heap. When bricks run
 *   short, spend a ladder on the LARGEST brick-paid climb so far (refunding its
 *   bricks) if that climb is bigger than the current one; otherwise use the
 *   ladder on the current climb directly. Same answer, more branches.
 *
 * KEY INSIGHT
 *   You cannot know up front which climbs deserve a ladder, so make a tentative
 *   choice and take it back later ("regret" greedy). A min-heap capped at
 *   `ladders` entries always holds exactly the L largest climbs seen so far, and
 *   everything that falls out is what bricks must cover. Ladders on the biggest
 *   climbs, bricks on the smallest, is provably optimal.
 *
 * COMPLEXITY
 *   Time  O(n log L)  L = ladders; one heap operation per climb (approach 1)
 *   Space O(L)        approach 1 never holds more than L + 1 entries; approach 2 is O(n)
 *
 * INTERVIEW FOLLOW-UPS
 *   - ladders = 0: the heap immediately pops every push, degrading to plain brick accounting.
 *   - Binary search on the answer ("can I reach index m?") is the fallback if you do not see
 *     the greedy: sort the first m climbs, ladder the largest L, brick the rest. O(n log^2 n).
 *   - Minimum Number of Refueling Stops (LC 871) uses the same retroactive-greedy idea.
 *
 * RUN
 *   main() runs 5 cases through both methods and prints actual vs expected.
 */

import java.util.Collections;
import java.util.PriorityQueue;

class FurthestBuildingYouCanReach {

    /**
     * Approach 1: ladders first, min-heap.
     * Every climb is first assigned a ladder. When we hold more climbs than ladders,
     * the smallest held climb is the cheapest one to pay with bricks instead.
     */
    public static int laddersFirstMinHeap(int[] heights, int bricks, int ladders) {
        PriorityQueue<Integer> ladderedClimbs = new PriorityQueue<>(); // min-heap
        int n = heights.length;

        for (int i = 0; i < n - 1; i++) {
            int diff = heights[i + 1] - heights[i];
            if (diff <= 0) {
                continue; // downhill or flat: free
            }
            ladderedClimbs.offer(diff);
            // one ladder too many: downgrade the smallest laddered climb to bricks
            if (ladderedClimbs.size() > ladders) {
                bricks -= ladderedClimbs.poll();
            }
            if (bricks < 0) {
                return i; // cannot afford this step, stop on building i
            }
        }
        return n - 1;
    }

    /**
     * Approach 2: bricks first, max-heap.
     * Pay every climb with bricks while they last. When bricks fall short, spend a ladder,
     * but put it on the LARGEST brick-paid climb so far (refunding those bricks) if that
     * climb is bigger than the current one; otherwise use the ladder on the current climb.
     */
    public static int bricksFirstMaxHeap(int[] heights, int bricks, int ladders) {
        PriorityQueue<Integer> brickedClimbs = new PriorityQueue<>(Collections.reverseOrder());
        int n = heights.length;
        int i;
        for (i = 0; i < n - 1; i++) {
            int diff = heights[i + 1] - heights[i];
            if (diff <= 0) {
                continue; // downhill or flat: free
            }
            if (bricks >= diff) {
                bricks -= diff;
                brickedClimbs.offer(diff);
            } else if (ladders > 0) {
                if (!brickedClimbs.isEmpty() && brickedClimbs.peek() > diff) {
                    // move the ladder onto the biggest bricked climb; bricks then cover this one
                    int biggest = brickedClimbs.poll();
                    bricks += biggest - diff;
                    brickedClimbs.offer(diff);
                }
                // else: the ladder goes on the current climb
                ladders--;
            } else {
                break; // no resources left; i is the furthest building
            }
        }
        return i;
    }

    private static void print(String label, int[] heights, int bricks, int ladders, int expected) {
        System.out.println(label + " (bricks=" + bricks + ", ladders=" + ladders + ")");
        int viaLadders = laddersFirstMinHeap(heights, bricks, ladders);
        int viaBricks = bricksFirstMaxHeap(heights, bricks, ladders);
        System.out.println("  laddersFirstMinHeap: " + viaLadders + "   expected " + expected);
        System.out.println("  bricksFirstMaxHeap : " + viaBricks + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical", new int[]{4, 2, 7, 6, 9, 14, 12}, 5, 1, 4);
        print("case 2 typical", new int[]{4, 12, 2, 7, 3, 18, 20, 3, 19}, 10, 2, 7);
        print("case 3 no ladders", new int[]{14, 3, 19, 3}, 17, 0, 3);
        print("case 4 ladders only", new int[]{1, 5, 1, 2, 3, 4, 10000}, 0, 5, 6);
        print("case 5 single building", new int[]{5}, 0, 0, 0);
    }
}

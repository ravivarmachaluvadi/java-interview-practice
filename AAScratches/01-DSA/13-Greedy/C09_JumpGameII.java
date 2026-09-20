/*
 * =====================================================================
 *  Jump Game II                                        LeetCode 45 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   nums[i] is the maximum number of steps you may jump forward from index i.
 *   Starting at index 0, return the MINIMUM number of jumps needed to reach the
 *   last index. The inputs are generated so the last index is always reachable.
 *
 * EXAMPLE
 *   [2, 3, 1, 1, 4]  ->  2    jump 0->1 (1 step), then 1->4 (3 steps)
 *   [2, 3, 0, 1, 4]  ->  2    jump 0->1, then 1->4; going 0->2 would cost more
 *   [2, 1]           ->  1    one jump clears the whole array
 *   [0]              ->  0    already at the last index, no jump needed
 *
 * APPROACH  (layered frontier / BFS on an array)
 *   Think of the indices as BFS levels: level 0 is {0}, level 1 is everything
 *   reachable in one jump, level 2 everything reachable in two, and so on.
 *   1. Walk i from 0 to n-2 (never stand ON the last index; arriving ends it).
 *   2. farthest = max(farthest, i + nums[i]) is the best index any cell seen so
 *      far can reach, i.e. the right edge of the NEXT level.
 *   3. currentEnd is the right edge of the level we are walking through.
 *      When i reaches currentEnd we have exhausted this level, so we must spend
 *      one jump: jumps++ and currentEnd = farthest (move to the next level).
 *   4. Stop early once currentEnd covers the last index.
 *
 * KEY INSIGHT
 *   You never decide WHICH index to jump to - only WHEN the current level runs
 *   out. Every index inside a level is reachable with the same jump count, so
 *   the only thing that matters is how far the whole level can reach. The jump
 *   counter increments exactly once per level, which is the BFS distance.
 *   Pattern to recognise: "minimum steps over an interval that keeps extending"
 *   -> layered frontier. Video Stitching and Minimum Taps are the same shape.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass, every index touched once
 *   Space O(1)  three integers, no queue needed even though this is BFS
 *
 * INTERVIEW FOLLOW-UPS
 *   - Can you even reach the end? (Jump Game I: drop the counter, keep farthest)
 *   - If reachability is NOT guaranteed, how do you detect it? (see canJumpToEnd)
 *   - Why loop to n-2 and not n-1? (landing on the last index must not bill a jump)
 *   - Same trick for Minimum Taps to Water a Garden and Video Stitching.
 *
 * RUN
 *   main() runs 6 cases (typical, single element, two elements, a greedy trap,
 *   plus two reachability checks) and prints actual vs expected.
 */
class JumpGameII {

    /**
     * Minimum jumps from index 0 to index n-1. Assumes the end is reachable.
     */
    public int jump(int[] nums) {
        int n = nums.length;
        if (n <= 1) return 0;

        int jumps = 0;
        int currentEnd = 0;   // right edge of the level we are currently walking
        int farthest = 0;     // right edge of the level we will jump into next

        // n - 1 is deliberately excluded: arriving there is the goal, not a launch point.
        for (int i = 0; i < n - 1; i++) {
            farthest = Math.max(farthest, i + nums[i]);

            if (i == currentEnd) {          // this level is used up, pay for one jump
                jumps++;
                currentEnd = farthest;
                if (currentEnd >= n - 1) break;   // next level already covers the end
            }
        }
        return jumps;
    }

    /**
     * Guard for the follow-up: the LeetCode problem promises the end is reachable,
     * but a real input may not be. This is Jump Game I - same frontier, no counter.
     */
    public boolean canJumpToEnd(int[] nums) {
        int farthest = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > farthest) return false;   // stuck before ever reaching i
            farthest = Math.max(farthest, i + nums[i]);
        }
        return true;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        JumpGameII sol = new JumpGameII();

        // typical
        print("case 1 [2,3,1,1,4]", sol.jump(new int[]{2, 3, 1, 1, 4}), 2);

        // edge: already at the last index
        print("case 2 [0]", sol.jump(new int[]{0}), 0);

        // edge: one jump clears everything
        print("case 3 [2,1]", sol.jump(new int[]{2, 1}), 1);

        // tricky: the biggest first hop (to index 2) is a dead end, so the
        // frontier must be measured over the whole level, not one best cell
        print("case 4 [2,3,0,1,4]", sol.jump(new int[]{2, 3, 0, 1, 4}), 2);

        // follow-up: reachability is a separate question from jump count
        int[] trap = {3, 2, 1, 0, 4};
        print("case 5 canJumpToEnd [3,2,1,0,4]", sol.canJumpToEnd(trap), false);
        int[] ok = {2, 3, 1, 1, 4};
        print("case 6 canJumpToEnd [2,3,1,1,4]", sol.canJumpToEnd(ok), true);
    }
}

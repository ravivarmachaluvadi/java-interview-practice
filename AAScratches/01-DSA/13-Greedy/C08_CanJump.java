/*
 * =====================================================================
 *  Jump Game                              LeetCode 55 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   You start at index 0 of an array nums. From index i you may jump forward any number
 *   of steps from 1 to nums[i]. Return true if you can reach the last index.
 *   Values are non-negative, so a 0 is a wall you must jump OVER, not a dead end you can
 *   step past. The array is never empty (index 0 alone is already the last index).
 *
 * EXAMPLE
 *   nums = [4, 3, 7, 1, 2]  ->  true   index 0 can jump 4 and land on the last index
 *   nums = [2, 3, 1, 1, 4]  ->  true   0 -> 1 -> 4
 *   nums = [3, 2, 1, 0, 4]  ->  false  every route stalls on the 0 at index 3
 *   nums = [0]              ->  true   already at the last index, no jump needed
 *
 * APPROACH  (farthest-reach frontier)
 *   1. Keep one number, maxReach: the largest index reachable using indices seen so far.
 *      Start it at 0, since index 0 is where we stand.
 *   2. Walk i from 0 to n-1.
 *   3. If i > maxReach, index i is unreachable. Everything after it is unreachable too,
 *      because reach only ever comes from an index we could actually stand on. Return false.
 *   4. Otherwise i is reachable, so update maxReach = max(maxReach, i + nums[i]).
 *   5. Surviving the whole loop means index n-1 was reachable. Return true.
 *
 * KEY INSIGHT
 *   You never have to decide how far to jump. The set of reachable indices is always a
 *   prefix 0..maxReach with no holes, because if you can land on index j you can also
 *   land on every index before it. So one scalar replaces any search: the only question
 *   is whether the frontier ever fails to keep up with the scan. Recognise this frontier
 *   invariant whenever the reachable set is an interval and the answer is "can I get to
 *   the end" - the same variable, extended with a layer counter, answers Jump Game II,
 *   and extended with interval ends it solves the tap/garden covering problems.
 *
 * COMPLEXITY
 *   Time  O(n)   one pass, constant work per index
 *   Space O(1)   a single int beyond the input
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the MINIMUM number of jumps instead (Jump Game II, LeetCode 45).
 *   - Jumping backwards allowed, or exact jump lengths only (becomes BFS on a graph).
 *   - Reconstruct one valid path, not just the yes/no answer.
 *   - Why is the DP solution O(n^2) and what exactly does the greedy prune?
 *
 * RUN
 *   main() runs 4 cases: typical reachable, multi-hop reachable, blocked by a zero,
 *   and the single-element edge case.
 */

import java.util.Arrays;

class CanJump {

    public static boolean canJump(int[] nums) {
        int maxReach = 0;                          // farthest index reachable so far
        for (int i = 0; i < nums.length; i++) {
            // the frontier fell behind the scan: index i can never be stood on
            if (i > maxReach) return false;
            maxReach = Math.max(maxReach, i + nums[i]);
        }
        return true;
    }

    private static void print(String label, int[] nums, boolean actual, boolean expected) {
        System.out.println(label + " " + Arrays.toString(nums)
                + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] typical = {4, 3, 7, 1, 2};
        int[] multiHop = {2, 3, 1, 1, 4};
        int[] blocked = {3, 2, 1, 0, 4};
        int[] single = {0};

        print("case 1 (one big jump) ", typical, canJump(typical), true);
        print("case 2 (multi hop)    ", multiHop, canJump(multiHop), true);
        print("case 3 (stuck on zero)", blocked, canJump(blocked), false);
        print("case 4 (single index) ", single, canJump(single), true);
    }
}

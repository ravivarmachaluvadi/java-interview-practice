/*
 * =====================================================================
 *  Find the Duplicate Number                  LeetCode 287 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   nums has n + 1 integers, every value is in [1, n], and exactly one value is
 *   repeated (it may appear more than twice). Return that value.
 *   Follow-up constraints that define the problem: do not modify nums, use O(1)
 *   extra space, run in better than O(n^2).
 *
 * EXAMPLE
 *   nums = [1, 3, 4, 2, 2]                 ->  2
 *   nums = [3, 1, 3, 4, 2]                 ->  3   duplicate sits at index 0
 *   nums = [2, 2, 2, 2, 2]                 ->  2   duplicate fills the array
 *   nums = [1, 1]                          ->  1   smallest input, n = 1
 *   nums = [2, 5, 9, 6, 9, 3, 8, 9, 7, 1]  ->  9   duplicate appears three times
 *
 * APPROACH  (Floyd cycle detection on value pointers)
 *   1. Treat nums as a linked list: node i points to node nums[i]. Start at index 0.
 *   2. Phase 1: slow = nums[slow], fast = nums[nums[fast]] until they meet. They
 *      must meet because the walk never leaves [1, n] and so must loop.
 *   3. Phase 2: reset slow to nums[0]; move slow and fast one step each until
 *      they meet again. That meeting node is the cycle entry = the duplicate.
 *   A second method, findDuplicateSeenArray, is the obvious boolean[] "seen" scan:
 *   O(n) extra space, so it fails the follow-up but is the baseline to mention.
 *
 * KEY INSIGHT
 *   Values in [1, n] are valid indices into an array of length n + 1, so the array
 *   IS a functional graph (each node has one out-edge). Index 0 is never a target,
 *   so starting there you enter a rho-shaped path. Two different indices holding
 *   the duplicate value both point at the same node, and that node is exactly the
 *   cycle entry. "Array values are indices" -> think linked list / cycle.
 *
 * COMPLEXITY
 *   Time  O(n)  each phase walks at most a few multiples of n steps
 *   Space O(1)  two integer pointers, input untouched
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why can the walk from index 0 never revisit index 0? (no value equals 0)
 *   - Prove phase 2 lands on the cycle entry (distance-to-entry equals head-to-entry
 *     modulo the cycle length).
 *   - If mutation were allowed: sign-marking or cyclic sort, still O(1) extra space.
 *   - Binary search on the value range [1, n] with a counting pass: O(n log n), O(1).
 *
 * RUN
 *   main() runs 5 cases (typical, index-0 duplicate, all equal, n = 1, triple
 *   repeat) through both methods and prints actual vs expected.
 */

import java.util.Arrays;

class FindDuplicate {

    /**
     * Baseline: boolean "seen" array. O(n) time, O(n) space. Does not mutate nums.
     * Indices are safe because nums.length == n + 1 while values cap at n.
     */
    public int findDuplicateSeenArray(int[] nums) {
        boolean[] seen = new boolean[nums.length];
        for (int val : nums) {
            if (seen[val]) return val;
            seen[val] = true;
        }
        return -1;   // unreachable when the constraints hold
    }

    /**
     * Intended solution: Floyd's tortoise and hare. O(n) time, O(1) space, no mutation.
     */
    public int findDuplicate(int[] nums) {
        int slow = nums[0];
        int fast = nums[0];

        // Phase 1: find any meeting point inside the cycle.
        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        } while (slow != fast);

        // Phase 2: restart slow from the head; the next meeting is the cycle entry.
        slow = nums[0];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }
        return slow;
    }

    private static void print(String label, int[] nums, int expected) {
        FindDuplicate solver = new FindDuplicate();
        System.out.println(label + ": " + Arrays.toString(nums)
                + " -> floyd " + solver.findDuplicate(nums)
                + ", seenArray " + solver.findDuplicateSeenArray(nums)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical      ", new int[]{1, 3, 4, 2, 2}, 2);
        print("case 2 dup at idx 0 ", new int[]{3, 1, 3, 4, 2}, 3);
        print("case 3 all equal    ", new int[]{2, 2, 2, 2, 2}, 2);
        print("case 4 n = 1        ", new int[]{1, 1}, 1);
        print("case 5 triple repeat", new int[]{2, 5, 9, 6, 9, 3, 8, 9, 7, 1}, 9);
    }
}

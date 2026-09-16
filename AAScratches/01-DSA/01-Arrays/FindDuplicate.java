import java.util.Arrays;

/**
 * LeetCode 287 — Find the Duplicate Number.
 *
 * <p>Given an array {@code nums} of {@code n + 1} integers where each value lies in
 * the range {@code [1, n]}, exactly one value is repeated (possibly many times).
 * Return that value.
 *
 * <p>Two approaches are provided:
 *
 * <ul>
 *   <li>{@link #findDuplicateSeenArray(int[])} — a boolean "seen" array. O(n) time,
 *       O(n) space. Simple and obvious, but fails the problem's follow-up.</li>
 *   <li>{@link #findDuplicate(int[])} — Floyd's cycle detection. O(n) time,
 *       O(1) space, input left untouched. This is the intended solution.</li>
 * </ul>
 *
 * <p>The cycle insight: treat the array as a linked list where node {@code i} points
 * to node {@code nums[i]}. Because values are confined to {@code [1, n]}, index
 * {@code 0} is never a pointer target, so traversal can never escape and a cycle is
 * guaranteed. The duplicate value is exactly the node where two chains merge —
 * the cycle's entry point.
 *
 * <pre>
 * Input:  nums = [1, 3, 4, 2, 2]   Output: 2
 * Input:  nums = [3, 1, 3, 4, 2]   Output: 3
 * Input:  nums = [2, 2, 2, 2, 2]   Output: 2
 * </pre>
 *
 * <p>Constraints:
 *
 * <ul>
 *   <li>{@code n == nums.length - 1}</li>
 *   <li>{@code 1 <= n <= 10^5}</li>
 *   <li>{@code 1 <= nums[i] <= n}</li>
 *   <li>Exactly one value appears two or more times</li>
 * </ul>
 *
 * @see <a href="https://leetcode.com/problems/find-the-duplicate-number/">LeetCode 287</a>
 */
class FindDuplicate {

    /**
     * Finds the repeated value using a boolean lookup array.
     *
     * <p>Safe on indices because {@code nums.length == n + 1} while values cap at
     * {@code n}, so the largest index touched is exactly the last valid one.
     * Slot {@code 0} is never used.
     *
     * <p>O(n) time, O(n) space. Does not mutate the input.
     *
     * @param nums the input array
     * @return the duplicated value, or {@code -1} if the constraints are violated
     */
    public int findDuplicateSeenArray(int[] nums) {
        boolean[] seen = new boolean[nums.length];
        for (int val : nums) {
            if (seen[val]) return val;
            seen[val] = true;
        }
        return -1;
    }

    /**
     * Finds the repeated value using Floyd's tortoise-and-hare cycle detection.
     *
     * <p>Phase one advances a slow pointer one step and a fast pointer two steps
     * until they meet somewhere inside the cycle. Phase two resets the slow pointer
     * to the start; advancing both one step at a time then makes them meet precisely
     * at the cycle's entry, which is the duplicated value.
     *
     * <p>O(n) time, O(1) space. Does not mutate the input.
     *
     * @param nums the input array
     * @return the duplicated value
     */
    public int findDuplicate(int[] nums) {
        int slow = nums[0];
        int fast = nums[0];

        // Phase 1: find any meeting point inside the cycle.
        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        } while (slow != fast);

        // Phase 2: walk from the start to locate the cycle entry.
        slow = nums[0];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }
        return slow;
    }

    public static void main(String[] args) {
        FindDuplicate solver = new FindDuplicate();

        int[][] cases = {
                {1, 3, 4, 2, 2},        // duplicate appears twice
                {3, 1, 3, 4, 2},        // duplicate at index 0
                {2, 2, 2, 2, 2},        // duplicate fills the array
                {1, 1},                 // smallest possible input, n = 1
                {2, 5, 9, 6, 9, 3, 8, 9, 7, 1}  // duplicate appears three times
        };

        for (int[] nums : cases) {
            System.out.printf("%-32s -> seenArray: %d, floyd: %d%n",
                    Arrays.toString(nums),
                    solver.findDuplicateSeenArray(nums),
                    solver.findDuplicate(nums));
        }
    }
}
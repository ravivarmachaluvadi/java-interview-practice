/*
 * =====================================================================
 *  Cycle Length in an Array (Functional Graph)      Floyd building block | Medium
 * =====================================================================
 *
 * PROBLEM
 *   nums[i] is itself a valid index into nums, so "i -> nums[i]" is a next-pointer and
 *   the array is a linked structure (a functional graph). Starting at 'start' and
 *   following the pointers you must eventually loop. Return the length of that loop
 *   using O(1) extra space. A self-loop (nums[i] == i) is a cycle of length 1.
 *
 * EXAMPLE
 *   nums = [1, 0],          start 0  ->  2   0 -> 1 -> 0
 *   nums = [1, 2, 3, 1],    start 0  ->  3   tail 0, cycle 1 -> 2 -> 3 -> 1
 *   nums = [0, 1, 1, 1],    start 0  ->  1   self-loop at 0
 *   nums = [1, 2, 3, 4, 3], start 0  ->  2   tail 0 -> 1 -> 2, cycle 3 -> 4 -> 3
 *   nums = [0],             start 0  ->  1   single element, self-loop
 *
 * APPROACH  (Floyd tortoise and hare, two phases)
 *   1. Validate: non-empty array, start in range, every element in range. A cycle is
 *      then guaranteed (finite set of nodes, every node has an out-edge).
 *   2. Phase 1: slow moves one step (slow = nums[slow]) and fast moves two
 *      (fast = nums[nums[fast]]) until they land on the same index. That index is
 *      somewhere on the cycle.
 *   3. Phase 2: from the meeting index, walk one pointer around until it returns,
 *      counting steps. That count is the cycle length.
 *
 * KEY INSIGHT
 *   The Phase 1 meeting point lies on the cycle, but the number of steps taken to get
 *   there is NOT the cycle length: it is a multiple of it that also depends on the tail
 *   ([1, 2, 3, 4, 3] meets after 4 steps although the cycle has length 2). So a second,
 *   separate lap around the cycle is required. Pattern: any "array of indices" or
 *   "array of values in 1..n" is a linked list in disguise, so Floyd applies with no
 *   extra memory. C17 (Find the Duplicate) is exactly this machinery plus phase 3.
 *
 * COMPLEXITY
 *   Time  O(mu + lambda)  tail length plus cycle length; the validation scan adds O(n)
 *   Space O(1)            three index variables
 *
 * INTERVIEW FOLLOW-UPS
 *   - Find the cycle START (mu): reset one pointer to 'start', advance both one step at a
 *     time until they meet. That is LeetCode 142 and the engine of LeetCode 287 (C17).
 *   - Why does fast catch slow at all? Inside the cycle the gap shrinks by exactly 1 per step.
 *   - Values in 1..n instead of 0..n-1: use nums[i] - 1 as the pointer, or start at index 0.
 *   - Nodes may have no next (nums[i] == -1): stop when fast reaches -1, meaning no cycle.
 *
 * RUN
 *   main() runs 5 cases (pure cycle, tail + cycle, self-loop, tail longer than cycle,
 *   single element) and prints actual vs expected.
 */

public class C16_CycleLengthInArray {

    /**
     * @param nums  array where every element is a valid index into nums
     * @param start index to begin the walk from
     * @return the length of the cycle reached from start
     * @throws IllegalArgumentException if the array is null/empty, start is out of range,
     *                                  or any element is not a valid index
     */
    public static int findCycleLength(int[] nums, int start) {
        validate(nums, start);

        // Phase 1: find a node guaranteed to be on the cycle.
        int slow = nums[start];
        int fast = nums[nums[start]];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[nums[fast]];
        }

        // Phase 2: walk the cycle once from the meeting node to measure it.
        int length = 1;
        for (int cur = nums[slow]; cur != slow; cur = nums[cur]) {
            length++;
        }
        return length;
    }

    /** Every element must be an in-range index; that is what guarantees a cycle exists. */
    private static void validate(int[] nums, int start) {
        if (nums == null || nums.length == 0) {
            throw new IllegalArgumentException("nums must be non-empty");
        }
        if (start < 0 || start >= nums.length) {
            throw new IllegalArgumentException("start out of range: " + start);
        }
        for (int v : nums) {
            if (v < 0 || v >= nums.length) {
                throw new IllegalArgumentException("not a valid index: " + v);
            }
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Typical: the whole array is one cycle.
        print("case 1 pure cycle     ", findCycleLength(new int[]{1, 2, 0}, 0), 3);

        // Typical: a tail of length 1 leading into a 3-cycle.
        print("case 2 tail + cycle   ", findCycleLength(new int[]{1, 2, 3, 1}, 0), 3);

        // Edge: self-loop at the start node.
        print("case 3 self-loop      ", findCycleLength(new int[]{0, 1, 1, 1}, 0), 1);

        // Tricky: tail longer than the cycle; phase 1 meets after 4 steps, cycle is 2.
        print("case 4 long tail      ", findCycleLength(new int[]{1, 2, 3, 4, 3}, 0), 2);

        // Edge: single element must point to itself.
        print("case 5 single element ", findCycleLength(new int[]{0}, 0), 1);
    }
}

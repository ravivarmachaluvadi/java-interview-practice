/**
 * <h2>Find Cycle Length in a Functional Graph</h2>
 *
 * <p>Given an array where each element {@code nums[i]} is a valid index into
 * the same array, the array defines a functional graph: from any index you
 * follow {@code i -> nums[i]} to reach the next index. Starting from
 * {@code start} and following this mapping repeatedly, the walk must
 * eventually revisit an index, forming a cycle.</p>
 *
 * <p>This method returns the length of that cycle, found via Floyd's
 * tortoise-and-hare algorithm in two phases:</p>
 * <ul>
 *   <li><b>Phase 1</b> — advance {@code slow} by one step and {@code fast}
 *       by two until they meet. The meeting node is guaranteed to lie on
 *       the cycle.</li>
 *   <li><b>Phase 2</b> — hold the meeting node fixed and walk a second
 *       pointer around from it, counting steps until it returns. That count
 *       is the exact cycle length.</li>
 * </ul>
 *
 * <p>Phase 2 is required: the step count at the Phase 1 meeting point is
 * {@code λ * ceil(μ / λ)} (a multiple of the cycle length {@code λ}, where
 * {@code μ} is the tail length), which equals {@code λ} only when
 * {@code μ <= λ}.</p>
 *
 * <p>A self-loop ({@code nums[i] == i}) is a valid cycle of length 1.
 * Because every element is an in-range index, a cycle always exists, so
 * there is no "no cycle found" case.</p>
 *
 * <pre>
 * Examples:
 *   {1, 0}          start 0 -> 2   (0 -> 1 -> 0)
 *   {1, 2, 0}       start 0 -> 3   (0 -> 1 -> 2 -> 0)
 *   {1, 2, 3, 1}    start 0 -> 3   (tail 0, cycle 1 -> 2 -> 3 -> 1)
 *   {0, 1, 1, 1}    start 0 -> 1   (self-loop at 0)
 *   {1, 2, 3, 4, 3} start 0 -> 2   (tail 0..2, cycle 3 -> 4 -> 3)
 * </pre>
 *
 * <p>Time: {@code O(μ + λ)}.<br>
 * Space: {@code O(1)}.</p>
 *
 * @param nums  array where every element is a valid index into {@code nums}
 * @param start index to begin the walk from
 * @return the length of the cycle reached from {@code start}
 * @throws IllegalArgumentException if the array is null/empty, {@code start}
 *                                  is out of range, or any element is not a valid index
 */
private static int findCycleLength(int[] nums, int start) {
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

    // Phase 1: find a node guaranteed to be on the cycle
    int slow = nums[start];
    int fast = nums[nums[start]];
    while (slow != fast) {
        slow = nums[slow];
        fast = nums[nums[fast]];
    }

    // Phase 2: walk the cycle once to measure it
    int length = 1;
    for (int cur = nums[slow]; cur != slow; cur = nums[cur]) {
        length++;
    }
    return length;
}

void main() {
    IO.println(findCycleLength(new int[]{1, 0}, 0));          // 2
    IO.println(findCycleLength(new int[]{1, 2, 0}, 0));       // 3
    IO.println(findCycleLength(new int[]{1, 2, 3, 1}, 0));    // 3
    IO.println(findCycleLength(new int[]{0, 1, 1, 1}, 0));    // 1  (was -1)
    IO.println(findCycleLength(new int[]{1, 2, 3, 4, 3}, 0)); // 2  (was 4)
}
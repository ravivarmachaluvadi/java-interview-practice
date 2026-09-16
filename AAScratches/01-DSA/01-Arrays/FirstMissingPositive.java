import java.util.Arrays;

/**
 * LeetCode 41 — First Missing Positive.
 *
 * <p>Given an unsorted integer array {@code nums}, returns the smallest positive
 * integer that is not present. Must run in O(n) time using O(1) auxiliary space.
 *
 * <p><b>Key bound.</b> With {@code n} slots the array can hold at most the values
 * {@code 1..n}, so by the pigeonhole principle the answer always lies in
 * {@code [1, n + 1]}. Anything negative, zero, or greater than {@code n} is
 * irrelevant and can be skipped outright.
 *
 * <p><b>Approach — cyclic sort.</b> Place each in-range value {@code v} at index
 * {@code v - 1}, so a fully populated array reads {@code [1, 2, 3, ...]}. A single
 * scan then reports the first index whose value doesn't match, and if none mismatch
 * the answer is {@code n + 1}.
 *
 * <p>The swap is skipped when {@code nums[correctIdx] == nums[i]}, which covers both
 * "already in place" and "a duplicate already occupies the target". Without that
 * guard, duplicates would swap two equal values forever. Termination is guaranteed
 * because every swap parks a value at its final index, capping the total at
 * {@code n} swaps even though {@code i} does not advance on swap iterations.
 *
 * <pre>
 * Input:  nums = [3, 4, -1, 1]
 * Output: 2                      // 1 is present, 2 is not
 * </pre>
 *
 * <p>O(n) time, O(1) extra space — but the input array is <b>rearranged in place</b>,
 * which is precisely what buys the constant space.
 *
 * @see <a href="https://leetcode.com/problems/first-missing-positive/">LeetCode 41</a>
 */
class FirstMissingPositive {

    /**
     * Returns the smallest positive integer absent from {@code nums}.
     *
     * @param nums the input array, reordered in place
     * @return the smallest missing positive integer, in {@code [1, nums.length + 1]}
     */
    public int firstMissingPositive(int[] nums) {
        int n = nums.length;

        // Phase 1: send every value in [1, n] to index as value - 1.
        int i = 0;
        while (i < n) {
            if (nums[i] < 1 || nums[i] > n) {
                i++;
                continue;
            }
            int correctIdx = nums[i] - 1;
            if (nums[correctIdx] != nums[i]) {
                int temp = nums[correctIdx];
                nums[correctIdx] = nums[i];
                nums[i] = temp;
            } else {
                i++;   // already placed, or a duplicate holds the slot
            }
        }

        // Phase 2: the first index that breaks the 1,2,3,... pattern.
        for (int j = 0; j < n; j++) {
            if (nums[j] != j + 1) {
                return j + 1;
            }
        }
        return n + 1;   // 1..n all present
    }

    public static void main(String[] args) {
        FirstMissingPositive solution = new FirstMissingPositive();

        int[][] cases = {
                {3, 4, -1, 1},        // mix of in-range and negative
                {1, 2, 3, 4, 5},      // fully packed, answer is n + 1
                {7, 8, 9, 11, 12},    // everything out of range
                {1, 1},               // duplicate would loop without the guard
                {2, 2},               // duplicate, and 1 never appears
                {}                    // empty input
        };

        for (int[] nums : cases) {
            String before = Arrays.toString(nums);   // capture before mutation
            System.out.printf("%-20s -> %d%n", before, solution.firstMissingPositive(nums));
        }
    }
}

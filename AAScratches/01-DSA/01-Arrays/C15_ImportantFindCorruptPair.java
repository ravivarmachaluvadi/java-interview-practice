import java.util.Arrays;

/**
 * Finds the corrupt pair in an array that should contain the values {@code 1..n}
 * exactly once, but where one value has been duplicated, overwriting another.
 *
 * <p><b>Approach — cyclic sort.</b> Each value {@code v} belongs at index
 * {@code v - 1}, so a clean array reads {@code [1, 2, 3, ...]}. Walk the array and
 * swap values toward their home index until every slot is settled.
 *
 * <p>The swap is skipped when {@code nums[i] == nums[correctIdx]}, covering both
 * "already in place" and "a duplicate already holds the slot". Without that guard,
 * two equal values would swap against each other forever. Termination is guaranteed
 * because each swap parks a value at its final index, capping the run at {@code n}
 * swaps even though {@code i} does not advance on swap iterations.
 *
 * <p>Afterwards exactly one index is wrong: one copy of the duplicate sits at its
 * correct position, while the other is stranded at the index that belonged to the
 * missing value. That single mismatch yields both answers — {@code nums[j]} is the
 * duplicate and {@code j + 1} is the missing number.
 *
 * <pre>
 * Input:  nums = [3, 1, 2, 5, 2]
 * After placement: [1, 2, 3, 2, 5]
 * Output: {4, 2}                     // 4 is missing, 2 is duplicated
 * </pre>
 *
 * <p>O(n) time, O(1) extra space. The input array is <b>reordered in place</b>,
 * which is what buys the constant space.
 *
 * <p>Constraints:
 *
 * <ul>
 *   <li>{@code 1 <= nums[i] <= n}, where {@code n == nums.length}</li>
 *   <li>Exactly one value appears twice; exactly one value is absent</li>
 * </ul>
 */
class FindCorruptPair {

    /**
     * Returns the missing and duplicated values.
     *
     * @param nums the input array, reordered in place
     * @return a two-element array {@code {missing, duplicated}}
     */
    public static int[] findCorruptPair(int[] nums) {
        int n = nums.length;

        // Phase 1: send each value v to index v - 1.
        int i = 0;
        while (i < n) {
            int correctIdx = nums[i] - 1;
            if (nums[i] != nums[correctIdx]) {
                swap(nums, i, correctIdx);
            } else {
                i++;   // placed, or a duplicate already occupies the target
            }
        }

        // Phase 2: the one wrong slot names both numbers.
        for (int j = 0; j < n; j++) {
            if (nums[j] != j + 1) {
                return new int[]{j + 1, nums[j]};
            }
        }
        return new int[]{-1, -1};   // unreachable under the stated constraints
    }

    private static void swap(int[] arr, int first, int second) {
        int temp = arr[first];
        arr[first] = arr[second];
        arr[second] = temp;
    }

    public static void main(String[] args) {
        int[][] cases = {
                {3, 1, 2, 5, 2},
                {3, 1, 2, 3, 6, 4},
                {4, 1, 2, 1, 6, 3},
                {4, 3, 4, 5, 1},
                {5, 3, 5, 6, 2, 1},
                {1, 1},              // smallest case
                {2, 2}               // duplicate at the far end of the range
        };

        String separator = "-".repeat(60);
        for (int i = 0; i < cases.length; i++) {
            String before = Arrays.toString(cases[i]);   // capture before mutation
            int[] pair = findCorruptPair(cases[i]);      // call once, not twice

            System.out.printf("%d.\tGiven array: %s%n", i + 1, before);
            System.out.printf("\tMissing: %d, Duplicated: %d%n", pair[0], pair[1]);
            System.out.println(separator);
        }
    }
}
import java.util.Arrays;

/**
 * Problem: an array should hold {@code 1..n} exactly once, but one value is duplicated
 * and therefore one value is missing. Return the {@code {missing, duplicated}} pair.
 *
 * <p>Approaches:
 * <ul>
 *   <li>{@link #cyclicSort} — swap each value {@code v} to index {@code v-1}; the single
 *       mismatched slot names both numbers. O(n) time, O(1) space, <b>mutates input</b>.</li>
 *   <li>{@link #xorPartition} — XOR all values with {@code 1..n} to get {@code missing ^ dup},
 *       split by the lowest set bit into two groups, XOR each group, then count to tell
 *       which is the duplicate. O(n) time, O(1) space, input untouched.</li>
 * </ul>
 *
 * <pre>
 * Input:  [3, 1, 2, 5, 2]
 * Output: {4, 2}          // 4 is missing, 2 is duplicated
 * </pre>
 *
 * <p>Constraints: {@code 1 <= nums[i] <= n} where {@code n == nums.length}; exactly one
 * value appears twice and exactly one value is absent.
 */
class FindCorruptPair {

    // ---------------------------------------------------------------------------------------
    // Approach 1: cyclic sort
    // ---------------------------------------------------------------------------------------

    /**
     * Each value {@code v} belongs at index {@code v - 1}, so a clean array reads
     * {@code [1, 2, 3, ...]}. Walk the array and swap values toward their home index until
     * every slot is settled.
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
     * Input:  [3, 1, 2, 5, 2]
     * After placement: [1, 2, 3, 2, 5]
     * Output: {4, 2}
     * </pre>
     *
     * @param nums the input array, reordered in place (that is what buys the O(1) space)
     * @return {@code {missing, duplicated}}
     */
    public static int[] cyclicSort(int[] nums) {
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

    // ---------------------------------------------------------------------------------------
    // Approach 2: XOR partitioning
    // ---------------------------------------------------------------------------------------

    /**
     * XOR properties this relies on:
     * <pre>
     * Self-inverse   a ^ a = 0                   duplicates cancel — the engine of the trick
     * Identity       a ^ 0 = a                   safe accumulator seed
     * Commutative    a ^ b = b ^ a               traversal order is irrelevant
     * Associative    (a ^ b) ^ c = a ^ (b ^ c)   fold an array in one pass
     * Involution     a ^ b ^ b = a               XOR is its own undo
     * Complement     a ^ ~0 = ~a                 bitwise NOT via XOR
     * Bit semantics  bit is 1 iff inputs differ  the partitioning insight below
     * </pre>
     *
     * <p>Step 1: XOR every array value with every value {@code 1..n}. Values present once
     * cancel with their counterpart, leaving {@code xr = missing ^ dup}.
     *
     * <p>Step 2: {@code xr & -xr} isolates the lowest set bit of {@code xr}. Missing and
     * dup differ at that bit (that is what "set" means), so it splits every number into
     * a "bit=1" group and a "bit=0" group with missing in one and dup in the other.
     * Note it is minus {@code -xr}, not tilde {@code ~xr}.
     *
     * <p>Step 3: XOR each group separately over array values and {@code 1..n}. Each group
     * collapses to a single number: one is missing, the other is dup, but we do not yet
     * know which is which.
     *
     * <p>Step 4: count how often one candidate appears in the array. Twice means it is the
     * duplicate.
     *
     * @param a the input array, not modified
     * @return {@code {missing, duplicated}}
     */
    public static int[] xorPartition(int[] a) {
        int n = a.length;

        // Step 1: xr = missing ^ dup
        int xr = 0;
        for (int i = 0; i < n; i++) {
            xr ^= a[i];
            xr ^= (i + 1);
        }

        // Step 2: lowest set bit differentiates missing from dup
        int bit = xr & -xr;

        // Step 3: group and fold
        int zero = 0;   // numbers with that bit = 0
        int one = 0;    // numbers with that bit = 1
        for (int i = 0; i < n; i++) {
            if ((a[i] & bit) != 0) one ^= a[i];
            else zero ^= a[i];
        }
        for (int i = 1; i <= n; i++) {
            if ((i & bit) != 0) one ^= i;
            else zero ^= i;
        }

        // Step 4: decide which candidate is the duplicate
        int cnt = 0;
        for (int i = 0; i < n; i++) if (a[i] == zero) cnt++;
        if (cnt == 2) return new int[]{one, zero};   // zero is dup, one is missing
        return new int[]{zero, one};                 // one is dup, zero is missing
    }

    // ---------------------------------------------------------------------------------------

    public static void main(String[] args) {
        int[][] cases = {
                {3, 1, 2, 5, 2},
                {3, 1, 2, 3, 6, 4},
                {4, 1, 2, 1, 6, 3},
                {4, 3, 4, 5, 1},
                {5, 3, 5, 6, 2, 1},
                {3, 1, 2, 5, 4, 6, 7, 5},
                {1, 1},              // smallest case
                {2, 2}               // duplicate at the far end of the range
        };

        String separator = "-".repeat(60);
        for (int i = 0; i < cases.length; i++) {
            String before = Arrays.toString(cases[i]);

            // cyclicSort mutates, so give it a copy; xorPartition reads the original.
            int[] bySort = cyclicSort(Arrays.copyOf(cases[i], cases[i].length));
            int[] byXor = xorPartition(cases[i]);

            System.out.printf("%d.\tGiven array: %s%n", i + 1, before);
            System.out.printf("\tcyclicSort   -> Missing: %d, Duplicated: %d%n", bySort[0], bySort[1]);
            System.out.printf("\txorPartition -> Missing: %d, Duplicated: %d%n", byXor[0], byXor[1]);
            System.out.println(separator);
        }
    }
}

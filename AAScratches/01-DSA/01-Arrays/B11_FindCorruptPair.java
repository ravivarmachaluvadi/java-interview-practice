/*
 * =====================================================================
 *  Find the Corrupt Pair (Set Mismatch)       LeetCode 645 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   An array of length n should contain 1..n exactly once, but one value got written
 *   twice and so one value is missing. Return {missing, duplicated}. Target: O(n) time,
 *   O(1) extra space. (LeetCode 645 asks for the same pair in the order [dup, missing].)
 *
 * EXAMPLE
 *   nums = [3, 1, 2, 5, 2]           ->  [4, 2]   4 is missing, 2 appears twice
 *   nums = [1, 1]                    ->  [2, 1]   smallest possible input
 *   nums = [2, 2]                    ->  [1, 2]   duplicate at the top of the range
 *   nums = [3, 1, 2, 5, 4, 6, 7, 5]  ->  [8, 5]   the missing value is n itself
 *
 * APPROACH 1  (cyclic sort, single mismatch)  -- cyclicSort()
 *   1. Value v belongs at index v - 1. Walk i: if nums[i] != nums[nums[i] - 1], swap
 *      nums[i] home and re-check i; otherwise advance i.
 *   2. After placement exactly one slot is wrong: one copy of the duplicate is home, the
 *      other copy is stranded in the slot the missing value should own.
 *   3. Scan for the first j with nums[j] != j + 1: missing = j + 1, duplicate = nums[j].
 *
 * APPROACH 2  (XOR partition, input untouched)  -- xorPartition()
 *   1. XOR every nums[i] and every 1..n together. Values present once cancel, leaving
 *      xr = missing ^ dup.
 *   2. bit = xr & -xr isolates the lowest set bit; missing and dup differ at that bit.
 *   3. XOR the same two streams again, but into two buckets split by that bit. Each
 *      bucket collapses to one candidate: one is missing, the other is dup.
 *   4. Count one candidate in nums: two hits means it is the duplicate.
 *
 * KEY INSIGHT
 *   After cyclic sort, a SINGLE wrong index tells you both answers: what is sitting
 *   there (the extra copy) and what should be (the absent value). That double payoff is
 *   why cyclic sort is worth memorising for any "values in 1..n" question. The XOR route
 *   answers "and if you may not modify the input?"; it is LeetCode 260 (Single Number
 *   III) in disguise, with 1..n acting as the second half of the stream.
 *
 * COMPLEXITY
 *   Time  O(n)  cyclic sort: at most n swaps + one scan; XOR: four linear passes
 *   Space O(1)  cyclic sort reorders the input in place; XOR needs only a few ints
 *
 * INTERVIEW FOLLOW-UPS
 *   - Input is read-only? Use xorPartition, or sum / sum-of-squares algebra (watch overflow).
 *   - Only the duplicate is needed, read-only, O(1) space: Floyd's cycle detection
 *     (C15_FindDuplicate).
 *   - Several duplicates and several missing: cyclic sort still works; collect every bad slot.
 *   - Why does the equality guard (not an index guard) stop the swap loop from spinning?
 *
 * RUN
 *   main() runs 4 cases through both methods and prints actual vs expected.
 */

import java.util.Arrays;

class FindCorruptPair {

    // ---------------------------------------------------------------------------------------
    // Approach 1: cyclic sort
    // ---------------------------------------------------------------------------------------

    /**
     * Each value v belongs at index v - 1, so a clean array reads [1, 2, 3, ...].
     * The swap is skipped when nums[i] == nums[correctIdx], which covers both "already in
     * place" and "a duplicate already holds the slot"; without that guard two equal values
     * would swap against each other forever. Each swap parks one value at its final index,
     * so the loop makes at most n swaps even though i does not advance on a swap.
     *
     * <pre>
     * Input:  [3, 1, 2, 5, 2]
     * After placement: [1, 2, 3, 2, 5]
     * Output: {4, 2}
     * </pre>
     *
     * @param nums the input array, reordered in place (that is what buys the O(1) space)
     * @return {missing, duplicated}
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
     * Self-inverse   a ^ a = 0                   duplicates cancel: the engine of the trick
     * Identity       a ^ 0 = a                   safe accumulator seed
     * Commutative    a ^ b = b ^ a               traversal order is irrelevant
     * Associative    (a ^ b) ^ c = a ^ (b ^ c)   fold an array in one pass
     * Bit semantics  bit is 1 iff inputs differ  the partitioning insight below
     * </pre>
     *
     * <p>Step 1: XOR every array value with every value 1..n. Values present once cancel
     * with their counterpart, leaving xr = missing ^ dup.
     *
     * <p>Step 2: xr & -xr isolates the lowest set bit of xr (note: minus, not tilde).
     * Missing and dup differ at that bit, so it splits every number into a "bit=1" group
     * and a "bit=0" group with missing in one and dup in the other.
     *
     * <p>Step 3: XOR each group separately over array values and 1..n. Each group collapses
     * to a single number: one is missing, the other is dup, but not yet known which.
     *
     * <p>Step 4: count how often one candidate appears in the array. Twice means duplicate.
     *
     * @param a the input array, not modified
     * @return {missing, duplicated}
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

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    /** Runs both approaches on one input; cyclicSort mutates, so it gets a copy. */
    private static void runCase(String label, int[] nums, String expected) {
        int[] bySort = cyclicSort(Arrays.copyOf(nums, nums.length));
        int[] byXor = xorPartition(nums);
        print(label + " cyclicSort  ", Arrays.toString(bySort), expected);
        print(label + " xorPartition", Arrays.toString(byXor), expected);
    }

    public static void main(String[] args) {
        // Typical: 4 missing, 2 duplicated.
        runCase("case 1 typical     ", new int[]{3, 1, 2, 5, 2}, "[4, 2]");

        // Edge: smallest possible input.
        runCase("case 2 smallest    ", new int[]{1, 1}, "[2, 1]");

        // Edge: duplicate at the far end of the range.
        runCase("case 3 top of range", new int[]{2, 2}, "[1, 2]");

        // Tricky: the missing value is n itself, so the wrong slot is the last one.
        runCase("case 4 missing n   ", new int[]{3, 1, 2, 5, 4, 6, 7, 5}, "[8, 5]");
    }
}

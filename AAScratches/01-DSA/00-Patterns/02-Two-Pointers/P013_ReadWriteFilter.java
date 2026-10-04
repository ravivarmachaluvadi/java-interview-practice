/*
 * =====================================================================
 *  P013 Two Pointers: Read / Write In-Place Filter   Canonical LC 26 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 26, Remove Duplicates from Sorted Array)
 *   nums is sorted. Remove duplicates in place so each value appears once, keeping the
 *   order. Return k, the number of unique values; nums[0..k-1] must hold them.
 *
 * EXAMPLE
 *   [1, 1, 2]                         ->  k = 2, [1, 2]
 *   [0, 0, 1, 1, 1, 2, 2, 3, 3, 4]    ->  k = 5, [0, 1, 2, 3, 4]
 *
 * RECOGNIZE WHEN
 *   - "in place", "O(1) extra space", "return the new length", "relative order kept".
 *   - Remove / keep / compact some elements: duplicates, a value, zeros to the end.
 *   Not this if: order does not matter and you want fewer writes -> swap with the end
 *   (P007_ThreeWayPartition); you must pair elements from both ends -> P011_OppositeEndsSorted.
 *
 * TEMPLATE
 *   w = 0                                   // next slot to write; a[0..w-1] is the answer
 *   for r in 0..n-1:                        // r reads every element once
 *       if keep(a[r]): a[w++] = a[r]
 *   return w
 *   // "at most k copies" on sorted input: keep(x) = w < k or x != a[w - k]
 *
 * APPROACH
 *   1. The read pointer r visits every element; the write pointer w trails behind.
 *   2. A value is kept when it differs from the last kept value a[w - 1].
 *
 * KEY INSIGHT
 *   The write pointer never passes the read pointer, so writing at w never destroys a
 *   value that has not been read yet. Compare against what was WRITTEN (a[w - k]), not
 *   against the previous input, and the "at most k copies" rule becomes one line.
 *
 * COMPLEXITY
 *   Time O(n), space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 80   Remove Duplicates II     keep(x) = w < 2 or x != a[w - 2]
 *   [coded] LC 283  Move Zeroes              keep non-zeros, then fill a[w..] with 0
 *   [coded] LC 27   Remove Element           keep(x) = x != val
 *           LC 443  String Compression       read a run of equal chars, write char + count
 *           LC 1089 Duplicate Zeros          output is LONGER: count first, write from the back
 *           LC 2149 Rearrange by Sign        two write pointers (even / odd slots), new array
 *           LC 1047 Remove Adjacent Dups     the write area acts as a stack
 *                                            -> P036_MatchingStack
 *
 * PITFALLS
 *   - Comparing with a[r - 1] instead of a[w - 1] breaks once values have shifted.
 *   - When the output can be longer than the input, write from the back (LC 88, 1089).
 *
 * DEEP DIVE
 *   A03_RemoveDuplicates, A02_MoveZeroes, A01_RemoveElement
 *   (02-Two-Pointers-Sliding-Window)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;

class ReadWriteFilter {

    // Canonical LC 26: at most one copy of each value.
    static int removeDuplicates(int[] nums) {
        return keepAtMost(nums, 1);
    }

    // LC 80 and the general rule: keep x when fewer than k copies have been written.
    static int keepAtMost(int[] nums, int k) {
        int w = 0;
        for (int x : nums) {
            if (w < k || x != nums[w - k]) {
                nums[w++] = x;
            }
        }
        return w;
    }

    // LC 283: compact the non-zeros, then zero-fill the tail.
    static int[] moveZeroes(int[] nums) {
        int w = 0;
        for (int x : nums) {
            if (x != 0) {
                nums[w++] = x;
            }
        }
        while (w < nums.length) {
            nums[w++] = 0;
        }
        return nums;
    }

    // LC 27.
    static int removeElement(int[] nums, int val) {
        int w = 0;
        for (int x : nums) {
            if (x != val) {
                nums[w++] = x;
            }
        }
        return w;
    }

    static String show(int[] a, int k) {
        return "k=" + k + " " + Arrays.toString(Arrays.copyOf(a, k));
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] a = {1, 1, 2};
        check("LC 26 [1,1,2]", show(a, removeDuplicates(a)), "k=2 [1, 2]");
        int[] b = {0, 0, 1, 1, 1, 2, 2, 3, 3, 4};
        check("LC 26 ten values", show(b, removeDuplicates(b)), "k=5 [0, 1, 2, 3, 4]");
        int[] c = {7};
        check("LC 26 single", show(c, removeDuplicates(c)), "k=1 [7]");

        int[] d = {1, 1, 1, 2, 2, 3};
        check("LC 80 [1,1,1,2,2,3]", show(d, keepAtMost(d, 2)), "k=5 [1, 1, 2, 2, 3]");
        int[] e = {0, 0, 1, 1, 1, 1, 2, 3, 3};
        check("LC 80 [0,0,1,1,1,1,2,3,3]", show(e, keepAtMost(e, 2)), "k=7 [0, 0, 1, 1, 2, 3, 3]");

        check("LC 283 [0,1,0,3,12]",
                Arrays.toString(moveZeroes(new int[]{0, 1, 0, 3, 12})), "[1, 3, 12, 0, 0]");
        check("LC 283 [0]", Arrays.toString(moveZeroes(new int[]{0})), "[0]");

        int[] f = {3, 2, 2, 3};
        check("LC 27 [3,2,2,3] val=3", show(f, removeElement(f, 3)), "k=2 [2, 2]");
        int[] g = {0, 1, 2, 2, 3, 0, 4, 2};
        check("LC 27 val=2", show(g, removeElement(g, 2)), "k=5 [0, 1, 3, 0, 4]");
    }
}

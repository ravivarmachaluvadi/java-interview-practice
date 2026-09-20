/*
 * =====================================================================
 *  Merge Two Sorted Arrays In Place (O(1) space)         LeetCode 88 variant | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Two sorted int arrays arr1 (length m) and arr2 (length n). Rearrange the values so that
 *   arr1 holds the m smallest in sorted order and arr2 holds the n largest in sorted order,
 *   using no extra array. (LeetCode 88 gives arr1 spare room at the end; this version does
 *   not, which is what makes it hard.)
 *
 * EXAMPLE
 *   arr1 = [1, 3, 5, 7, 9, 11], arr2 = [0, 2, 4, 6, 8, 10]
 *     ->  arr1 = [0, 1, 2, 3, 4, 5], arr2 = [6, 7, 8, 9, 10, 11]
 *   arr1 = [10, 12], arr2 = [5, 18, 20]   ->  arr1 = [5, 10], arr2 = [12, 18, 20]
 *   arr1 = [],       arr2 = [1, 2]        ->  unchanged (nothing to merge)
 *   arr1 = [2, 2, 3], arr2 = [1, 2]       ->  arr1 = [1, 2, 2], arr2 = [2, 3]
 *
 * APPROACH  (Shell-sort style gap method)
 *   1. Treat arr1 followed by arr2 as one virtual array of length m + n. Index i < m lives
 *      in arr1[i], index i >= m lives in arr2[i - m].
 *   2. gap = ceil((m + n) / 2). Walk left = 0, right = left + gap over the virtual array and
 *      swap the pair whenever virtual[left] > virtual[right]. Three physical cases: both
 *      in arr1, both in arr2, or straddling the boundary.
 *   3. Shrink gap = ceil(gap / 2) and repeat. When the pass with gap == 1 finishes, stop.
 *
 * KEY INSIGHT
 *   The naive O(1)-space merge (insertion into the right place) is O(m * n). The gap trick
 *   is Shell sort's idea applied to two already-sorted halves: comparing elements gap apart
 *   moves each value most of the way to its home in a few big jumps, and the final gap == 1
 *   pass only has to fix neighbours. Each pass is linear and there are log(m + n) passes.
 *   Pattern to recognise: "in place" + "two sorted pieces" = gap method.
 *
 * COMPLEXITY
 *   Time  O((m + n) log(m + n))  log passes, each a linear scan
 *   Space O(1)                   only indices and one temp for swapping
 *
 * INTERVIEW FOLLOW-UPS
 *   - LeetCode 88 (arr1 has n trailing zeros): fill from the back in O(m + n), no gap needed.
 *   - Why not plain insertion (shift arr1 element into arr2 on each mismatch)? It is O(m * n).
 *   - Prove the gap sequence ends at 1 (ceil(gap / 2) reaches 1 and the loop then breaks).
 *   - Merge k sorted arrays: heap of heads, O(N log k), no longer in place.
 *
 * RUN
 *   main() runs 4 cases (typical, unequal lengths, empty arr1, duplicates) and prints
 *   actual vs expected for both arrays.
 */
import java.util.Arrays;

class MergeTwoSortedArrays {

    /** Rearranges arr1 and arr2 in place so that arr1 ++ arr2 is fully sorted. */
    public static void mergeInPlace(int[] arr1, int[] arr2) {
        int m = arr1.length;
        int n = arr2.length;
        int length = m + n;

        int gap = ceilHalf(length);
        while (gap > 0) {
            int left = 0;
            int right = gap;
            while (right < length) {
                if (left < m && right >= m) {
                    // pair straddles the boundary: arr1[left] vs arr2[right - m]
                    swapIfGreater(arr1, left, arr2, right - m);
                } else if (left >= m) {
                    // both indices are inside arr2
                    swapIfGreater(arr2, left - m, arr2, right - m);
                } else {
                    // both indices are inside arr1
                    swapIfGreater(arr1, left, arr1, right);
                }
                left++;
                right++;
            }
            if (gap == 1)
                break; // the gap == 1 pass is the final neighbour fix-up
            gap = ceilHalf(gap);
        }
    }

    /** ceil(x / 2); using floor would let gap become 0 before the gap == 1 pass runs. */
    private static int ceilHalf(int x) {
        return (x / 2) + (x % 2);
    }

    private static void swapIfGreater(int[] a, int i, int[] b, int j) {
        if (a[i] > b[j]) {
            int tmp = a[i];
            a[i] = b[j];
            b[j] = tmp;
        }
    }

    private static void run(String label, int[] arr1, int[] arr2, int[] exp1, int[] exp2) {
        mergeInPlace(arr1, arr2);
        String actual = "arr1=" + Arrays.toString(arr1) + " arr2=" + Arrays.toString(arr2);
        String expected = "arr1=" + Arrays.toString(exp1) + " arr2=" + Arrays.toString(exp2);
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 typical   ", new int[]{1, 3, 5, 7, 9, 11}, new int[]{0, 2, 4, 6, 8, 10},
                new int[]{0, 1, 2, 3, 4, 5}, new int[]{6, 7, 8, 9, 10, 11});
        run("case 2 unequal   ", new int[]{10, 12}, new int[]{5, 18, 20},
                new int[]{5, 10}, new int[]{12, 18, 20});
        run("case 3 empty arr1", new int[]{}, new int[]{1, 2},
                new int[]{}, new int[]{1, 2});
        run("case 4 duplicates", new int[]{2, 2, 3}, new int[]{1, 2},
                new int[]{1, 2, 2}, new int[]{2, 3});
    }
}

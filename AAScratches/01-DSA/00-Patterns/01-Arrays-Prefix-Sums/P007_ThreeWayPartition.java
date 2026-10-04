/*
 * =====================================================================
 *  P007 Three-Way Partition (Dutch Flag)   Canonical LC 75 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 75, Sort Colors)
 *   nums holds only 0, 1 and 2. Sort it in place in one pass, without a library sort and
 *   without counting first.
 *
 * EXAMPLE
 *   [2, 0, 2, 1, 1, 0]  ->  [0, 0, 1, 1, 2, 2]
 *   [2, 0, 1]           ->  [0, 1, 2]
 *
 * RECOGNIZE WHEN
 *   - Split an array into 2 or 3 groups in place: "evens first", "below / equal / above
 *     pivot", "zeros to the end", "negatives before positives".
 *   - Quicksort / quickselect partitioning, especially with many duplicate keys.
 *   Not this if: the original order inside each group must be kept (stable) -> write into
 *   a new array, or use P013_ReadWriteFilter when only one group must keep its order.
 *
 * TEMPLATE
 *   low = 0, mid = 0, high = n - 1      // [0,low) < p, [low,mid) == p, (high,n-1] > p
 *   while mid <= high:
 *       if a[mid] < p:  swap(low++, mid++)
 *       elif a[mid] > p: swap(mid, high--)       // mid stays: the swapped-in value is new
 *       else:            mid++
 *
 * APPROACH
 *   1. Keep three regions growing from the edges and an unknown middle [mid, high].
 *   2. Look at a[mid]: a 0 goes to the low region, a 2 to the high region, a 1 stays.
 *   3. Stop when the unknown region is empty.
 *
 * KEY INSIGHT
 *   Every step shrinks the unknown region by one, so it is a single pass. After swapping
 *   with high, mid must NOT move: the value that came from high has not been looked at.
 *   The value that comes from low is always a 1, which is why mid may move there.
 *
 * COMPLEXITY
 *   Time O(n), space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 905  Sort Array By Parity      two groups: one write pointer for evens
 *   [coded] LC 2161 Partition Around Pivot    STABLE version: three passes into a new array
 *   [coded] 3-way quicksort                   Dutch partition around a[lo]; recurse on the
 *                                             < and > parts only, so duplicates cost nothing
 *           LC 324  Wiggle Sort II            median (quickselect) + 3-way on mapped indices
 *           LC 2149 Rearrange by Sign         stable -> two write cursors, new array
 *           LC 283  Move Zeroes               keep order -> P013_ReadWriteFilter
 *
 * PITFALLS
 *   - Loop condition is mid <= high (the element at high is still unknown).
 *   - Incrementing mid after the swap with high skips an unexamined value.
 *   - Lomuto / Hoare partitions are not stable; say so if the interviewer asks.
 *
 * DEEP DIVE
 *   C08_Sort012 (01-Arrays), A05_SortArrayByParity (02-Two-Pointers-Sliding-Window)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;

class ThreeWayPartition {

    static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    // Canonical LC 75, pivot value 1.
    static int[] sortColors(int[] nums) {
        int low = 0;
        int mid = 0;
        int high = nums.length - 1;
        while (mid <= high) {
            if (nums[mid] == 0) {
                swap(nums, low++, mid++);
            } else if (nums[mid] == 2) {
                swap(nums, mid, high--);
            } else {
                mid++;
            }
        }
        return nums;
    }

    // LC 905: two groups. w is where the next even number goes.
    static int[] sortArrayByParity(int[] nums) {
        int w = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] % 2 == 0) {
                swap(nums, w++, i);
            }
        }
        return nums;
    }

    // LC 2161: the in-place partition is not stable; when order matters, copy in 3 passes.
    static int[] pivotArray(int[] nums, int pivot) {
        int[] out = new int[nums.length];
        int w = 0;
        for (int x : nums) {
            if (x < pivot) {
                out[w++] = x;
            }
        }
        for (int x : nums) {
            if (x == pivot) {
                out[w++] = x;
            }
        }
        for (int x : nums) {
            if (x > pivot) {
                out[w++] = x;
            }
        }
        return out;
    }

    // 3-way quicksort: partition [lo..hi] around a[lo] into < | == | >, recurse on the ends.
    static int[] quickSort3Way(int[] a) {
        sort3(a, 0, a.length - 1);
        return a;
    }

    private static void sort3(int[] a, int lo, int hi) {
        if (lo >= hi) {
            return;
        }
        int p = a[lo];
        int lt = lo;
        int i = lo;
        int gt = hi;
        while (i <= gt) {
            if (a[i] < p) {
                swap(a, lt++, i++);
            } else if (a[i] > p) {
                swap(a, i, gt--);
            } else {
                i++;
            }
        }
        sort3(a, lo, lt - 1);
        sort3(a, gt + 1, hi);
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 75 [2,0,2,1,1,0]",
                Arrays.toString(sortColors(new int[]{2, 0, 2, 1, 1, 0})), "[0, 0, 1, 1, 2, 2]");
        check("LC 75 [2,0,1]", Arrays.toString(sortColors(new int[]{2, 0, 1})), "[0, 1, 2]");
        check("LC 75 [2,2] no zeros", Arrays.toString(sortColors(new int[]{2, 2})), "[2, 2]");

        check("LC 905 [3,1,2,4]",
                Arrays.toString(sortArrayByParity(new int[]{3, 1, 2, 4})), "[2, 4, 3, 1]");
        check("LC 905 [0]", Arrays.toString(sortArrayByParity(new int[]{0})), "[0]");

        check("LC 2161 [9,12,5,10,14,3,10] p=10",
                Arrays.toString(pivotArray(new int[]{9, 12, 5, 10, 14, 3, 10}, 10)),
                "[9, 5, 3, 10, 10, 12, 14]");
        check("LC 2161 [-3,4,3,2] p=2",
                Arrays.toString(pivotArray(new int[]{-3, 4, 3, 2}, 2)), "[-3, 2, 4, 3]");

        check("3-way quicksort many duplicates",
                Arrays.toString(quickSort3Way(new int[]{3, 1, 3, 2, 3, 1, 3})),
                "[1, 1, 2, 3, 3, 3, 3]");
        check("3-way quicksort reversed",
                Arrays.toString(quickSort3Way(new int[]{5, 4, 3, 2, 1})), "[1, 2, 3, 4, 5]");
    }
}

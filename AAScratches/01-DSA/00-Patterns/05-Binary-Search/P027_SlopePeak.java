/*
 * =====================================================================
 *  P027 Binary Search on a Slope: Peaks   Canonical LC 162 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 162, Find Peak Element)
 *   nums[i] != nums[i + 1] for all i, and nums[-1] = nums[n] = -infinity. Return the index
 *   of ANY element strictly greater than its neighbours, in O(log n).
 *
 * EXAMPLE
 *   [1, 2, 3, 1]           ->  2
 *   [1, 2, 1, 3, 5, 6, 4]  ->  5     (1 is also accepted)
 *
 * RECOGNIZE WHEN
 *   - "peak", "mountain", "bitonic", "local maximum / minimum" with an O(log n) target.
 *   - The array is not sorted, but comparing a[mid] with a[mid + 1] tells you which way
 *     is "uphill".
 *   Not this if: the array is a sorted array that was rotated -> P024_RotatedSortedArray.
 *
 * TEMPLATE
 *   lo = 0, hi = n - 1
 *   while lo < hi:
 *       mid = lo + (hi - lo) / 2
 *       if a[mid] < a[mid + 1]: lo = mid + 1   // rising: a peak exists to the right
 *       else:                   hi = mid       // falling: mid or something left is a peak
 *   return lo
 *
 * APPROACH
 *   1. Compare mid with its right neighbour.
 *   2. Walk toward the higher side; because the ends are -infinity, the higher side must
 *      contain a peak.
 *
 * KEY INSIGHT
 *   Binary search does not need a sorted array, only a test that safely discards half.
 *   "a[mid] < a[mid + 1]" guarantees a peak on the right (climb until it stops rising or
 *   hits the end), so the left half can be thrown away.
 *
 * COMPLEXITY
 *   Time O(log n), space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 852  Peak Index in Mountain   exactly one peak; the same loop finds it
 *   [coded] LC 1095 Find in Mountain Array   find the peak, then binary search the rising
 *                                            half ascending and the falling half descending
 *           LC 1901 Find a Peak Element II   binary search on columns: take the max of the
 *                                            middle column, move toward the bigger neighbour
 *           Bitonic array max / search       the same as LC 852 / LC 1095
 *
 * PITFALLS
 *   - mid + 1 must exist: the loop is lo < hi, so mid < hi and mid + 1 <= hi.
 *   - LC 1095: search the LEFT half first, since the smallest index is wanted.
 *   - Descending half: flip the comparison, not the bounds.
 *
 * DEEP DIVE
 *   C03_FindPeakElement (03-Binary-Search)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class SlopePeak {

    // Canonical LC 162 (also LC 852: a mountain has exactly one peak).
    static int findPeakElement(int[] a) {
        int lo = 0;
        int hi = a.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] < a[mid + 1]) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }

    // LC 852.
    static int peakIndexInMountainArray(int[] arr) {
        return findPeakElement(arr);
    }

    // LC 1095: smallest index of target in a mountain array.
    static int findInMountainArray(int target, int[] mountain) {
        int peak = findPeakElement(mountain);
        int left = search(mountain, target, 0, peak, true);
        if (left != -1) {
            return left;
        }
        return search(mountain, target, peak + 1, mountain.length - 1, false);
    }

    private static int search(int[] a, int target, int lo, int hi, boolean ascending) {
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] == target) {
                return mid;
            }
            boolean goRight = ascending ? a[mid] < target : a[mid] > target;
            if (goRight) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return -1;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 162 [1,2,3,1]", findPeakElement(new int[]{1, 2, 3, 1}), 2);
        check("LC 162 [1,2,1,3,5,6,4] (1 also valid)",
                findPeakElement(new int[]{1, 2, 1, 3, 5, 6, 4}), 5);
        check("LC 162 strictly falling", findPeakElement(new int[]{5, 4, 3}), 0);
        check("LC 162 single element", findPeakElement(new int[]{7}), 0);

        check("LC 852 [0,1,0]", peakIndexInMountainArray(new int[]{0, 1, 0}), 1);
        check("LC 852 [0,2,1,0]", peakIndexInMountainArray(new int[]{0, 2, 1, 0}), 1);
        check("LC 852 [0,10,5,2]", peakIndexInMountainArray(new int[]{0, 10, 5, 2}), 1);

        check("LC 1095 target 3 on both sides",
                findInMountainArray(3, new int[]{1, 2, 3, 4, 5, 3, 1}), 2);
        check("LC 1095 target 3 absent", findInMountainArray(3, new int[]{0, 1, 2, 4, 2, 1}), -1);
        check("LC 1095 target only on the right",
                findInMountainArray(1, new int[]{3, 5, 3, 2, 1}), 4);
    }
}

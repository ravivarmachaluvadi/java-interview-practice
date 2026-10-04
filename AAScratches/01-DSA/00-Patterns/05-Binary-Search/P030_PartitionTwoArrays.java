/*
 * =====================================================================
 *  P030 Binary Search a Partition of Two Arrays   Canonical LC 4 | Hard
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 4, Median of Two Sorted Arrays)
 *   Given sorted arrays a (size m) and b (size n), return the median of all m + n values
 *   in O(log(min(m, n))).
 *
 * EXAMPLE
 *   a = [1, 3],  b = [2]       ->  2.0
 *   a = [1, 2],  b = [3, 4]    ->  2.5
 *   a = [],      b = [1]       ->  1.0      one side empty
 *
 * RECOGNIZE WHEN
 *   - Two sorted arrays and a question about their combined order (median, kth smallest)
 *     with a logarithmic target, so merging (O(m + n)) is too slow.
 *   Not this if: O(m + n) is fine -> P014_WalkTwoSequences (merge walk); many arrays ->
 *   P044_KWayMerge.
 *
 * TEMPLATE
 *   binary search i = how many elements the LEFT half takes from the smaller array a
 *   j = half - i                                  // the rest comes from b
 *   aLeft = a[i-1], aRight = a[i], bLeft = b[j-1], bRight = b[j]  (+-infinity at the edges)
 *   if aLeft <= bRight and bLeft <= aRight: the cut is right
 *   elif aLeft > bRight: i too big -> hi = i - 1
 *   else:                i too small -> lo = i + 1
 *
 * APPROACH
 *   1. The median splits the merged order into a left half of (m + n + 1) / 2 values.
 *   2. Choose how many of those come from a (i); the rest come from b (j). A cut is valid
 *      when everything on the left is <= everything on the right.
 *   3. Binary search i over the smaller array; the median comes from the four border values.
 *
 * KEY INSIGHT
 *   Searching for a VALUE is hard here; searching for a CUT is easy, because a wrong cut
 *   tells you which way to move (aLeft too big means take fewer from a). Searching the
 *   smaller array keeps j inside b's bounds.
 *
 * COMPLEXITY
 *   Time O(log(min(m, n))), space O(1). Kth element: O(log(min(m, n))) as well.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] Kth Element of Two Sorted Arrays the left half has exactly k values; i ranges
 *                                            over [max(0, k - n), min(k, m)]
 *           Median of a row-sorted matrix    binary search the value + count per row
 *           Merge-walk fallback              O(m + n) answer worth naming first in the
 *                                            interview, before the partition idea
 *
 * PITFALLS
 *   - Always search the SHORTER array, or j can go negative or past n.
 *   - Use Integer.MIN_VALUE / MAX_VALUE for empty sides of the cut.
 *   - Odd total: the median is max(aLeft, bLeft) because the left half gets the extra one.
 *
 * DEEP DIVE
 *   D02_MedianOfTwoSortedArrays, D01_KthElementOf2SortedArrays (03-Binary-Search)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class PartitionTwoArrays {

    // Canonical LC 4.
    static double findMedianSortedArrays(int[] a, int[] b) {
        if (a.length > b.length) {
            return findMedianSortedArrays(b, a);
        }
        int m = a.length;
        int n = b.length;
        int half = (m + n + 1) / 2;
        int lo = 0;
        int hi = m;
        while (lo <= hi) {
            int i = lo + (hi - lo) / 2;
            int j = half - i;
            int aLeft = i == 0 ? Integer.MIN_VALUE : a[i - 1];
            int aRight = i == m ? Integer.MAX_VALUE : a[i];
            int bLeft = j == 0 ? Integer.MIN_VALUE : b[j - 1];
            int bRight = j == n ? Integer.MAX_VALUE : b[j];
            if (aLeft <= bRight && bLeft <= aRight) {
                int leftMax = Math.max(aLeft, bLeft);
                if ((m + n) % 2 == 1) {
                    return leftMax;
                }
                return (leftMax + (double) Math.min(aRight, bRight)) / 2;
            } else if (aLeft > bRight) {
                hi = i - 1;
            } else {
                lo = i + 1;
            }
        }
        throw new IllegalArgumentException("inputs are not sorted");
    }

    // Kth smallest (1-based) of the union of two sorted arrays.
    static int kthElement(int[] a, int[] b, int k) {
        if (a.length > b.length) {
            return kthElement(b, a, k);
        }
        int m = a.length;
        int n = b.length;
        int lo = Math.max(0, k - n);
        int hi = Math.min(k, m);
        while (lo <= hi) {
            int i = lo + (hi - lo) / 2;
            int j = k - i;
            int aLeft = i == 0 ? Integer.MIN_VALUE : a[i - 1];
            int aRight = i == m ? Integer.MAX_VALUE : a[i];
            int bLeft = j == 0 ? Integer.MIN_VALUE : b[j - 1];
            int bRight = j == n ? Integer.MAX_VALUE : b[j];
            if (aLeft <= bRight && bLeft <= aRight) {
                return Math.max(aLeft, bLeft);
            } else if (aLeft > bRight) {
                hi = i - 1;
            } else {
                lo = i + 1;
            }
        }
        throw new IllegalArgumentException("k out of range");
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 4 [1,3] [2]", findMedianSortedArrays(new int[]{1, 3}, new int[]{2}), 2.0);
        check("LC 4 [1,2] [3,4]", findMedianSortedArrays(new int[]{1, 2}, new int[]{3, 4}), 2.5);
        check("LC 4 [] [1]", findMedianSortedArrays(new int[]{}, new int[]{1}), 1.0);
        check("LC 4 [0,0] [0,0]", findMedianSortedArrays(new int[]{0, 0}, new int[]{0, 0}), 0.0);
        check("LC 4 interleaved",
                findMedianSortedArrays(new int[]{1, 4, 7}, new int[]{2, 3, 5, 6}), 4.0);

        int[] x = {2, 3, 6, 7, 9};
        int[] y = {1, 4, 8, 10};
        check("kth k=5", kthElement(x, y, 5), 6);
        check("kth k=1 smallest", kthElement(x, y, 1), 1);
        check("kth k=9 largest", kthElement(x, y, 9), 10);
    }
}

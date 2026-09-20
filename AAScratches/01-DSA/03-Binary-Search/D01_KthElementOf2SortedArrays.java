/*
 * =====================================================================
 *  K-th Element of Two Sorted Arrays                GFG classic | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given two sorted arrays a (size m) and b (size n) and a 1-based k (1 <= k <= m + n),
 *   return the k-th smallest element of their merged order without actually merging.
 *   Target time is O(log(min(m, n))).
 *
 * EXAMPLE
 *   a = [2, 3, 6, 7, 9], b = [1, 4, 8, 10], k = 5  ->  6    merged: 1 2 3 4 [6] 7 8 9 10
 *   a = [], b = [1, 2, 3], k = 2                   ->  2    one array empty
 *   a = [1, 2, 3], b = [4, 5, 6], k = 6            ->  6    k = m + n, the very last element
 *   a = [1, 2, 3], b = [4, 5, 6], k = 1            ->  1    k = 1, the very first element
 *
 * APPROACH  (partition two sorted arrays)
 *   1. Make a the shorter array (swap if needed) so the binary search runs on fewer indices.
 *   2. The k smallest elements form a "left half". Take mid1 of them from a and mid2 = k - mid1
 *      from b. mid1 must be in [max(0, k - n), min(k, m)] so that mid2 stays within b.
 *   3. Look at the four boundary values: l1 = a[mid1-1], r1 = a[mid1], l2 = b[mid2-1],
 *      r2 = b[mid2] (use MIN/MAX sentinels when an index falls off the end).
 *   4. The split is correct when every left value is <= every right value, i.e.
 *      l1 <= r2 && l2 <= r1. Then the k-th element is max(l1, l2), the largest on the left.
 *   5. If l1 > r2, a contributed too much: high = mid1 - 1. Otherwise (l2 > r1) a contributed
 *      too little: low = mid1 + 1.
 *
 * KEY INSIGHT
 *   Binary search on how many elements the smaller array contributes to the first k. The
 *   check is purely local (four boundary values) yet decides global correctness, because
 *   both arrays are already sorted internally. Median of Two Sorted Arrays (LC 4) is exactly
 *   this with k = (m + n + 1) / 2 plus an even/odd fix-up; learn the invariant here first.
 *
 * COMPLEXITY
 *   Time  O(log(min(m, n)))  binary search over how many elements a contributes
 *   Space O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why clamp low to max(0, k - n)? If a gives fewer than k - n, b must give more than n.
 *   - Why can the loop never fall through to the dummy return? A valid partition always
 *     exists for 1 <= k <= m + n, so the search must find it.
 *   - The O(k) two-pointer merge is the baseline to mention before the log solution.
 *   - Follow-up: return the median (LC 4) or the k-th largest instead of the k-th smallest.
 *
 * RUN
 *   main() runs 4 cases (typical, one empty array, k = last, k = first) and prints
 *   actual vs expected.
 */

class KthElementOf2SortedArrays {

    /** k is 1-based: k = 1 returns the smallest element of the merged order. */
    public int kthElement(int[] a, int[] b, int k) {
        int m = a.length;
        int n = b.length;
        if (m > n) {
            return kthElement(b, a, k);   // binary search on the shorter array
        }

        // mid1 elements come from a, mid2 = k - mid1 from b; both must be in range.
        int low = Math.max(0, k - n);
        int high = Math.min(k, m);

        while (low <= high) {
            int mid1 = low + (high - low) / 2;
            int mid2 = k - mid1;

            // Boundary values around the cut; sentinels when a cut is at an array edge.
            int l1 = (mid1 > 0) ? a[mid1 - 1] : Integer.MIN_VALUE;
            int l2 = (mid2 > 0) ? b[mid2 - 1] : Integer.MIN_VALUE;
            int r1 = (mid1 < m) ? a[mid1] : Integer.MAX_VALUE;
            int r2 = (mid2 < n) ? b[mid2] : Integer.MAX_VALUE;

            if (l1 <= r2 && l2 <= r1) {
                return Math.max(l1, l2);  // all left <= all right; k-th is the largest on the left
            } else if (l1 > r2) {
                high = mid1 - 1;          // a gave too many, take fewer from a
            } else {
                low = mid1 + 1;           // a gave too few, take more from a
            }
        }
        return -1; // unreachable for 1 <= k <= m + n
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        KthElementOf2SortedArrays s = new KthElementOf2SortedArrays();
        int[] a = {2, 3, 6, 7, 9};
        int[] b = {1, 4, 8, 10};
        print("case 1 typical",     s.kthElement(a, b, 5), 6);
        print("case 2 one empty",   s.kthElement(new int[]{}, new int[]{1, 2, 3}, 2), 2);
        print("case 3 k = m + n",   s.kthElement(new int[]{1, 2, 3}, new int[]{4, 5, 6}, 6), 6);
        print("case 4 k = 1",       s.kthElement(new int[]{1, 2, 3}, new int[]{4, 5, 6}, 1), 1);
    }
}

/*
 * =====================================================================
 *  P029 Binary Search on an Index-Value Relation   Canonical LC 540 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 540, Single Element in a Sorted Array)
 *   In a sorted array every value appears exactly twice except one, which appears once.
 *   Return that value in O(log n) time and O(1) space.
 *
 * EXAMPLE
 *   [1, 1, 2, 3, 3, 4, 4, 8, 8]  ->  2
 *   [3, 3, 7, 7, 10, 11, 11]     ->  10
 *   [1]                          ->  1
 *
 * RECOGNIZE WHEN
 *   - The VALUE at index i tells you something about everything before i: "how many are
 *     missing up to here" (a[i] - i - 1), "are pairs still aligned" (pair starts at an even
 *     index), "how many papers have at least this many citations" (n - i).
 *   - O(log n) is asked on a sorted array but the target is not a value you can compare to.
 *   Not this if: you compare values with a target directly -> P023_BoundarySearch.
 *
 * TEMPLATE
 *   define f(i) from i and a[i] so that f is monotonic (false ... false true ... true)
 *   lo = 0, hi = n
 *   while lo < hi:
 *       mid = lo + (hi - lo) / 2
 *       if f(mid): hi = mid else lo = mid + 1
 *   convert the boundary index lo back into the answer
 *
 * APPROACH
 *   1. Before the single element, every pair starts at an EVEN index: a[2j] == a[2j + 1].
 *      After it, pairs start at odd indices, so a[2j] != a[2j + 1].
 *   2. Binary search over even indices for the first place where the pairing breaks.
 *
 * KEY INSIGHT
 *   You cannot binary search for the answer by value, but you can binary search for the
 *   first index where an index-based property flips. The answer then comes from that
 *   index (the single element sits exactly where the pairing first breaks).
 *
 * COMPLEXITY
 *   Time O(log n), space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 1539 Kth Missing Positive     missing(i) = a[i] - (i + 1); first i with
 *                                            missing(i) >= k; answer = i + k
 *   [coded] LC 275  H-Index II               first i with a[i] >= n - i; answer = n - i
 *           LC 1060 Missing Element Sorted   missing(i) = a[i] - a[0] - i, the same idea
 *           LC 1064 Fixed Point              distinct sorted ints: a[i] - i is non-decreasing;
 *                                            first i with a[i] >= i, check equality
 *           LC 441  Arranging Coins          last k with k(k + 1) / 2 <= n
 *
 * PITFALLS
 *   - LC 540: force mid to be even (mid -= mid % 2) before comparing with mid + 1.
 *   - LC 1539: if no index qualifies, lo == n and the answer is n + k.
 *   - Define f so that it is truly monotonic; test it on a tiny example by hand first.
 *
 * DEEP DIVE
 *   C08_SingleNonDuplicateElement, B01_KthMissingPositive (03-Binary-Search)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class IndexValuePredicate {

    // Canonical LC 540.
    static int singleNonDuplicate(int[] a) {
        int lo = 0;
        int hi = a.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            mid -= mid % 2;                      // look at the start of a would-be pair
            if (a[mid] == a[mid + 1]) {
                lo = mid + 2;                    // pairs still aligned: single is to the right
            } else {
                hi = mid;                        // alignment broke at or before mid
            }
        }
        return a[lo];
    }

    // LC 1539.
    static int findKthPositive(int[] a, int k) {
        int lo = 0;
        int hi = a.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] - (mid + 1) >= k) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo + k;
    }

    // LC 275: citations sorted ascending.
    static int hIndex(int[] citations) {
        int n = citations.length;
        int lo = 0;
        int hi = n;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (citations[mid] >= n - mid) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return n - lo;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 540 [1,1,2,3,3,4,4,8,8]",
                singleNonDuplicate(new int[]{1, 1, 2, 3, 3, 4, 4, 8, 8}), 2);
        check("LC 540 [3,3,7,7,10,11,11]",
                singleNonDuplicate(new int[]{3, 3, 7, 7, 10, 11, 11}), 10);
        check("LC 540 [1]", singleNonDuplicate(new int[]{1}), 1);
        check("LC 540 single at the end", singleNonDuplicate(new int[]{1, 1, 2, 2, 3}), 3);

        check("LC 1539 [2,3,4,7,11] k=5", findKthPositive(new int[]{2, 3, 4, 7, 11}, 5), 9);
        check("LC 1539 [1,2,3,4] k=2", findKthPositive(new int[]{1, 2, 3, 4}, 2), 6);
        check("LC 1539 [5] k=1 before the array", findKthPositive(new int[]{5}, 1), 1);

        check("LC 275 [0,1,3,5,6]", hIndex(new int[]{0, 1, 3, 5, 6}), 3);
        check("LC 275 [1,2,100]", hIndex(new int[]{1, 2, 100}), 2);
        check("LC 275 [0]", hIndex(new int[]{0}), 0);
    }
}

/*
 * =====================================================================
 *  P024 Binary Search: Rotated Sorted Array   Canonical LC 33 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 33, Search in Rotated Sorted Array)
 *   A sorted array of DISTINCT values was rotated at an unknown pivot. Return the index
 *   of target, or -1. O(log n).
 *
 * EXAMPLE
 *   [4, 5, 6, 7, 0, 1, 2], target 0  ->  4
 *   [4, 5, 6, 7, 0, 1, 2], target 3  ->  -1
 *   [3, 1],                target 1  ->  1     two elements: both halves are tiny
 *
 * RECOGNIZE WHEN
 *   - "sorted then rotated", "shifted", "circularly sorted", "find the rotation count".
 *   - O(log n) is required but a plain binary search fails because the order wraps.
 *   Not this if: no rotation -> P023_BoundarySearch; it rises then falls (a mountain, not a
 *   rotation) -> P027_SlopePeak.
 *
 * TEMPLATE
 *   lo = 0, hi = n - 1
 *   while lo <= hi:
 *       mid = lo + (hi - lo) / 2
 *       if a[mid] == target: return mid
 *       if a[lo] <= a[mid]:                       // left half [lo, mid] is sorted
 *           if a[lo] <= target < a[mid]: hi = mid - 1 else lo = mid + 1
 *       else:                                     // right half [mid, hi] is sorted
 *           if a[mid] < target <= a[hi]: lo = mid + 1 else hi = mid - 1
 *   return -1
 *
 * APPROACH
 *   1. At any mid, at least one of the two halves is in sorted order.
 *   2. Find which one, ask "is target inside that sorted range?", and keep that half if
 *      yes, the other half if no.
 *
 * KEY INSIGHT
 *   A rotated array is two sorted runs; cutting it anywhere leaves at least one sorted
 *   half, and a sorted half lets you test membership with two comparisons. For the minimum,
 *   compare a[mid] with a[hi]: if a[mid] > a[hi], the drop (and the minimum) is to the right.
 *
 * COMPLEXITY
 *   Time O(log n); with duplicates (LC 81 / 154) O(n) in the worst case. Space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 153  Find Minimum             a[mid] > a[hi] -> lo = mid + 1, else hi = mid
 *   [coded] LC 81   Search with Duplicates   when a[lo] == a[mid] == a[hi] you cannot tell
 *                                            which half is sorted: shrink lo++ and hi--
 *           LC 154  Minimum with Duplicates  LC 153, and on a[mid] == a[hi] do hi--
 *           Rotation count                   index of the minimum (LC 153)
 *           Search after finding the pivot   find min index k, then plain binary search on
 *                                            [0, k - 1] or [k, n - 1]
 *
 * PITFALLS
 *   - Use a[lo] <= a[mid] (with "="): when lo == mid the left half is one element and is
 *     sorted; "<" sends [3, 1] the wrong way.
 *   - Compare with a[hi], not a[lo], when hunting the minimum: an unrotated array has
 *     a[lo] < a[mid] but the minimum is on the left.
 *   - Duplicates break the O(log n) promise; say so in the interview.
 *
 * DEEP DIVE
 *   C06_SearchInRotatedSortedArray, C07_SearchInARotatedSortedArrayII,
 *   C04_MinIndexInRotatedSortedArray (03-Binary-Search)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class RotatedSortedArray {

    // Canonical LC 33.
    static int search(int[] a, int target) {
        int lo = 0;
        int hi = a.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] == target) {
                return mid;
            }
            if (a[lo] <= a[mid]) {
                if (a[lo] <= target && target < a[mid]) {
                    hi = mid - 1;
                } else {
                    lo = mid + 1;
                }
            } else {
                if (a[mid] < target && target <= a[hi]) {
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
        }
        return -1;
    }

    // LC 153.
    static int findMin(int[] a) {
        int lo = 0;
        int hi = a.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] > a[hi]) {
                lo = mid + 1;                    // the drop is to the right of mid
            } else {
                hi = mid;                        // mid could be the minimum
            }
        }
        return a[lo];
    }

    // LC 81: duplicates allowed.
    static boolean searchWithDuplicates(int[] a, int target) {
        int lo = 0;
        int hi = a.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] == target) {
                return true;
            }
            if (a[lo] == a[mid] && a[mid] == a[hi]) {
                lo++;                            // cannot tell which half is sorted
                hi--;
            } else if (a[lo] <= a[mid]) {
                if (a[lo] <= target && target < a[mid]) {
                    hi = mid - 1;
                } else {
                    lo = mid + 1;
                }
            } else {
                if (a[mid] < target && target <= a[hi]) {
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
        }
        return false;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] a = {4, 5, 6, 7, 0, 1, 2};
        check("LC 33 target 0", search(a, 0), 4);
        check("LC 33 target 3 absent", search(a, 3), -1);
        check("LC 33 [1] target 0", search(new int[]{1}, 0), -1);
        check("LC 33 [3,1] target 1", search(new int[]{3, 1}, 1), 1);
        check("LC 33 not rotated", search(new int[]{1, 2, 3, 4}, 4), 3);

        check("LC 153 [3,4,5,1,2]", findMin(new int[]{3, 4, 5, 1, 2}), 1);
        check("LC 153 [4,5,6,7,0,1,2]", findMin(a), 0);
        check("LC 153 [11,13,15,17] not rotated", findMin(new int[]{11, 13, 15, 17}), 11);

        check("LC 81 target 0", searchWithDuplicates(new int[]{2, 5, 6, 0, 0, 1, 2}, 0), true);
        check("LC 81 target 3", searchWithDuplicates(new int[]{2, 5, 6, 0, 0, 1, 2}, 3), false);
        check("LC 81 [1,0,1,1,1] ambiguous ends",
                searchWithDuplicates(new int[]{1, 0, 1, 1, 1}, 0), true);
    }
}

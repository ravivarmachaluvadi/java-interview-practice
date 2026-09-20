/*
 * =====================================================================
 *  Median of Two Sorted Arrays                     LeetCode 4 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given two sorted arrays of sizes m and n (m + n >= 1), return the median of the combined
 *   sorted order as a double. The required time is O(log(m + n)); the standard answer is
 *   O(log(min(m, n))).
 *
 * EXAMPLE
 *   arr1 = [1, 2], arr2 = [3, 4]        ->  2.5   merged 1 2 | 3 4, average of 2 and 3
 *   arr1 = [1, 3], arr2 = [2]           ->  2.0   merged 1 [2] 3, odd total
 *   arr1 = [], arr2 = [1]               ->  1.0   one array empty, single element
 *   arr1 = [1, 2, 3, 4, 5], arr2 = [6]  ->  3.5   all of arr2 sits on the right
 *
 * APPROACH  (median by partitioning, binary search on the shorter array)
 *   1. Make arr1 the shorter array so the search space is [0, m].
 *   2. The left half of the merged order holds halfLength = (m + n + 1) / 2 elements
 *      (the +1 puts the extra element on the left when the total is odd).
 *   3. Take mid1 elements from arr1 and mid2 = halfLength - mid1 from arr2.
 *   4. Read the four boundary values l1, r1, l2, r2 (MIN/MAX sentinels at the array edges).
 *   5. If l1 <= r2 && l2 <= r1 the partition is correct:
 *        odd total  -> median = max(l1, l2)
 *        even total -> median = (max(l1, l2) + min(r1, r2)) / 2.0
 *   6. If l1 > r2, arr1 contributed too much: high = mid1 - 1. Else low = mid1 + 1.
 *
 * KEY INSIGHT
 *   The median is the boundary of a partition that splits the combined data into two equal
 *   halves with every left value <= every right value. Because both arrays are sorted, that
 *   condition reduces to comparing four boundary numbers, so you can binary search how many
 *   elements the shorter array puts on the left. This is K-th Element of Two Sorted Arrays
 *   with k = halfLength, plus one line to average the two middle values for even totals.
 *
 * COMPLEXITY
 *   Time  O(log(min(m, n)))  binary search over mid1 in [0, m], m the shorter length
 *   Space O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why (m + n + 1) / 2 and not (m + n) / 2? So the odd case can read the median straight
 *     off the left side as max(l1, l2) without a second branch.
 *   - Why always search the shorter array? mid2 = halfLength - mid1 could go negative or
 *     beyond n if the longer array were searched; the shorter array keeps mid2 in range.
 *   - Baseline to mention first: merge with two pointers up to the middle, O(m + n).
 *   - Generalise: k-th smallest of two sorted arrays; k-th smallest of k sorted lists (heap).
 *
 * RUN
 *   main() runs 4 cases (even total, odd total, one array empty, all of arr2 on the right)
 *   and prints actual vs expected.
 */

class ImportantMedianOfTwoSortedArrays {

    public static double findMedianSortedArrays(int[] arr1, int[] arr2) {
        if (arr2.length < arr1.length) {
            return findMedianSortedArrays(arr2, arr1);   // binary search on the shorter array
        }

        int m = arr1.length;
        int n = arr2.length;
        int totalLength = m + n;
        int halfLength = (m + n + 1) / 2;  // odd totals put the extra element on the left

        int low = 0;
        int high = m;

        while (low <= high) {
            int mid1 = low + (high - low) / 2;  // elements taken from arr1 for the left half
            int mid2 = halfLength - mid1;       // the rest of the left half comes from arr2

            // Boundary values around the two cuts; sentinels when a cut is at an array edge.
            int l1 = (mid1 > 0) ? arr1[mid1 - 1] : Integer.MIN_VALUE;
            int l2 = (mid2 > 0) ? arr2[mid2 - 1] : Integer.MIN_VALUE;
            int r1 = (mid1 < m) ? arr1[mid1] : Integer.MAX_VALUE;
            int r2 = (mid2 < n) ? arr2[mid2] : Integer.MAX_VALUE;

            if (l1 <= r2 && l2 <= r1) {
                // Correct partition: everything left <= everything right.
                if (totalLength % 2 == 1) {
                    return Math.max(l1, l2);
                }
                return (Math.max(l1, l2) + Math.min(r1, r2)) / 2.0;
            }

            if (l1 > r2) {
                high = mid1 - 1;   // arr1 gave too many to the left
            } else {
                low = mid1 + 1;    // arr1 gave too few to the left
            }
        }
        return 0.0; // unreachable for sorted input with m + n >= 1
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] oneTwo = {1, 2};
        print("case 1 even total",     findMedianSortedArrays(oneTwo, new int[]{3, 4}), 2.5);
        print("case 2 odd total",      findMedianSortedArrays(new int[]{1, 3}, new int[]{2}), 2.0);
        print("case 3 one empty",      findMedianSortedArrays(new int[]{}, new int[]{1}), 1.0);
        int[] oneToFive = {1, 2, 3, 4, 5};
        print("case 4 arr2 all right", findMedianSortedArrays(oneToFive, new int[]{6}), 3.5);
    }
}

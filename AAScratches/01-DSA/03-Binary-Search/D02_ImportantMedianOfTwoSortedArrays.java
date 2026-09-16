/**
 * Problem: Find the median of two sorted integer arrays in O(log(min(m,n))) time.
 *
 * Approach: Perform a binary search on the smaller array to partition both arrays
 * such that all elements on the left side are less than or equal to those on the right.
 * The correct partition satisfies l1 <= r2 and l2 <= r1. Once found, compute the median
 * from the boundary values of the partitions.
 *
 * Time Complexity: O(log min(m,n)) where m and n are the lengths of the two arrays.
 * Space Complexity: O(1) auxiliary space.
 */
class ImportantMedianOfTwoSortedArrays {
    public static void main(String[] args) {
        int[] arr1 = {1, 2};
        int[] arr2 = {3, 4};
        System.out.println(findMedianSortedArrays(arr1, arr2));
    }

    private static double findMedianSortedArrays(int[] arr1, int[] arr2) {
        if (arr2.length < arr1.length) {
            return findMedianSortedArrays(arr2, arr1);
        }

        int m = arr1.length;
        int n = arr2.length;
        int totalLength = m + n;
        int halfLength = (m + n + 1) / 2; // Integer division ensures the middle is calculated correctly

        int low = 0, high = m;

        while (low <= high) { // Change the condition to low <= high
            int mid1 = (low + high) / 2;

            //***********************************//
            //                                   //
            int mid2 = halfLength - mid1;   //
            //                                   //
            //***********************************//

            // Use appropriate values for left and right partitions
            int l1 = (mid1 > 0) ? arr1[mid1 - 1] : Integer.MIN_VALUE;
            int l2 = (mid2 > 0) ? arr2[mid2 - 1] : Integer.MIN_VALUE;
            int r1 = (mid1 < m) ? arr1[mid1] : Integer.MAX_VALUE;
            int r2 = (mid2 < n) ? arr2[mid2] : Integer.MAX_VALUE;

            // Check if partition is correct
            if (l1 <= r2 && l2 <= r1) {
                // If the total length is odd, return the max of the left partitions
                if (totalLength % 2 == 1) {
                    return Math.max(l1, l2);
                }
                // If even, return the average of the two middle values
                return (Math.max(l1, l2) + Math.min(r1, r2)) / 2.0;
            }

            // Adjust binary search range
            if (l1 > r2) {
                high = mid1 - 1;
            } else {
                low = mid1 + 1;
            }
        }

        return 0.0;
    }
}

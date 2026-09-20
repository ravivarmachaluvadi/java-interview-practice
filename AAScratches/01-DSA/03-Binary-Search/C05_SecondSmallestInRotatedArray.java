/*
 * =====================================================================
 *  Second Smallest in Rotated Sorted Array         LeetCode 153 variant | Medium
 * =====================================================================
 *
 * PROBLEM
 *   An ascending array of DISTINCT integers was rotated at an unknown pivot,
 *   e.g. [1,2,3,4,5,6] became [5,6,1,2,3,4]. Return the second smallest value.
 *   The array has at least two elements. Must run in O(log n).
 *
 * EXAMPLE
 *   [5, 6, 1, 2, 3, 4]  ->  2    min is at index 2, next index holds 2
 *   [2, 3, 4, 1]        ->  2    min is at the LAST index, wrap to index 0
 *   [1, 2, 3]           ->  2    not rotated at all, min at index 0
 *   [2, 1]              ->  2    smallest possible input
 *
 * APPROACH  (pivot plus one, wrapping)
 *   1. Binary search for the index of the minimum (the rotation pivot):
 *      compare nums[mid] with nums[right]; if nums[mid] > nums[right] the
 *      pivot is to the right of mid, otherwise it is at mid or to the left.
 *   2. The second smallest is the element right after the minimum in the
 *      rotated order, so its index is (minIndex + 1) % n.
 *
 * KEY INSIGHT
 *   A rotated sorted array is still sorted if you read it cyclically starting
 *   at the pivot. Once you know the pivot, "k-th smallest" is just
 *   nums[(pivot + k - 1) % n]. The only trap is the wrap-around when the
 *   minimum sits at the last index.
 *
 * COMPLEXITY
 *   Time  O(log n)  one binary search for the pivot, then O(1)
 *   Space O(1)      a few index variables
 *
 * INTERVIEW FOLLOW-UPS
 *   - k-th smallest in a rotated array: nums[(pivot + k - 1) % n]
 *   - duplicates allowed: pivot search degrades to O(n) worst case (LC 154)
 *   - find the rotation count: it equals the pivot index
 *
 * RUN
 *   main() runs 5 cases (typical, wrap-around, no rotation, two elements,
 *   invalid input) and prints actual vs expected.
 */
class SecondSmallestInRotatedArray {

    public static int findSecondSmallest(int[] nums) {
        if (nums == null || nums.length < 2) {
            throw new IllegalArgumentException("Array should have at least two elements");
        }
        int n = nums.length;
        int minIndex = findMinIndex(nums);

        // The second smallest is the element right after the minimum in rotated
        // order; the modulo handles the minimum sitting at the last index.
        return nums[(minIndex + 1) % n];
    }

    // Standard "find the pivot" binary search (LC 153).
    private static int findMinIndex(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right]) {
                left = mid + 1;   // pivot is strictly to the right of mid
            } else {
                right = mid;      // pivot is at mid or to the left
            }
        }
        return left;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("case 1 typical      ", findSecondSmallest(new int[]{5, 6, 1, 2, 3, 4}), 2);
        check("case 2 min at last  ", findSecondSmallest(new int[]{2, 3, 4, 1}), 2);
        check("case 3 no rotation  ", findSecondSmallest(new int[]{1, 2, 3}), 2);
        check("case 4 two elements ", findSecondSmallest(new int[]{2, 1}), 2);
        try {
            findSecondSmallest(new int[]{7});
            check("case 5 single elem  ", "no exception", "IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            check("case 5 single elem  ", "IllegalArgumentException", "IllegalArgumentException");
        }
    }
}

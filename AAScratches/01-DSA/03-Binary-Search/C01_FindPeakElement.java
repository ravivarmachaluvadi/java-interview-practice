/*
 * =====================================================================
 *  Find Peak Element                       LeetCode 162 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   A peak is an element strictly greater than both neighbours. Imagine nums[-1] and
 *   nums[n] are minus infinity, so the ends can be peaks. Adjacent elements are never
 *   equal. Return the index of ANY peak in O(log n).
 *
 * EXAMPLE
 *   nums = [1, 2, 1, 3, 5, 6, 4]  ->  5     index 1 (value 2) is also a valid peak
 *   nums = [1, 2, 3]              ->  2     strictly increasing: last element
 *   nums = [3, 2, 1]              ->  0     strictly decreasing: first element
 *   nums = [7]                    ->  0     single element is a peak by definition
 *
 * APPROACH  (binary search on unsorted input)
 *   1. Handle n == 1 and the two ends explicitly, so the loop only ever looks at
 *      indices 1..n-2 where both neighbours exist.
 *   2. At mid, if nums[mid] beats both neighbours, return mid.
 *   3. Otherwise walk toward the larger neighbour: if nums[mid] < nums[mid+1] the
 *      right side is rising away from mid, so a peak must exist there (the array
 *      ends in -infinity). Symmetrically for the left.
 *
 * KEY INSIGHT
 *   Binary search does not need a sorted array, only a decision rule that is
 *   guaranteed to keep a valid answer inside the half you keep. "Go uphill" is that
 *   rule: an ascending slope must eventually come down (or hit the -inf boundary),
 *   so a peak is trapped on the uphill side.
 *
 * COMPLEXITY
 *   Time  O(log n)  halve the range every step
 *   Space O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is a peak guaranteed to exist? The maximum element is always a peak.
 *   - 2D version (LC 1901): binary search on columns, take the max of each column.
 *   - What if adjacent duplicates are allowed? The uphill rule breaks; worst case O(n).
 *   - Plateau/all-equal input: no strict peak; the problem forbids it.
 *
 * RUN
 *   main() runs 5 cases (typical, increasing, decreasing, single, two elements)
 *   and prints actual vs expected.
 */

class FindPeakElement {

    public static int findPeakElement(int[] nums) {
        int n = nums.length;

        if (n == 1) {
            return 0;
        }
        // The ends only have one neighbour, so check them explicitly.
        if (nums[0] > nums[1]) {
            return 0;
        }
        if (nums[n - 1] > nums[n - 2]) {
            return n - 1;
        }

        int low = 1;
        int high = n - 2;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            // mid-1 and mid+1 are always valid here
            if (nums[mid - 1] < nums[mid] && nums[mid] > nums[mid + 1]) {
                return mid;
            }
            // Walk uphill: a peak is guaranteed on the side of the larger neighbour.
            if (nums[mid] < nums[mid + 1]) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;   // unreachable: a peak always exists
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 [1,2,1,3,5,6,4] (1 or 5 valid; this search finds)",
                findPeakElement(new int[]{1, 2, 1, 3, 5, 6, 4}), 5);
        print("case 2 [1,2,3] increasing", findPeakElement(new int[]{1, 2, 3}), 2);
        print("case 3 [3,2,1] decreasing", findPeakElement(new int[]{3, 2, 1}), 0);
        print("case 4 [7] single", findPeakElement(new int[]{7}), 0);
        print("case 5 [1,2] two elements", findPeakElement(new int[]{1, 2}), 1);
    }
}

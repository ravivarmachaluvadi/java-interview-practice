/*
 * =====================================================================
 *  Find First and Last Position of Element in Sorted Array    LeetCode 34 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a sorted int array (may contain duplicates) and a target, return the index of the
 *   first and last occurrence of target as [first, last]. If target is absent return [-1, -1].
 *   Must run in O(log n), so a linear scan is not acceptable.
 *
 * EXAMPLE
 *   nums = [5, 7, 7, 8, 8, 10], target = 8  ->  [3, 4]
 *   nums = [5, 7, 7, 8, 8, 10], target = 6  ->  [-1, -1]   absent
 *   nums = [],                  target = 0  ->  [-1, -1]   empty input
 *   nums = [2, 2, 2, 2],        target = 2  ->  [0, 3]     every element matches
 *
 * APPROACH  (two bounded binary searches)
 *   1. firstOccurrence: standard binary search, but when nums[mid] == target do NOT stop.
 *      Record mid as a candidate and keep searching LEFT (high = mid - 1).
 *   2. lastOccurrence: same, but on a match record mid and keep searching RIGHT (low = mid + 1).
 *   3. If the first search returns -1 the target is absent; skip the second search.
 *
 * KEY INSIGHT
 *   This is the lower/upper bound template (A01_LowerAndUpperBounds) called twice. A plain binary
 *   search stops at ANY match; to reach the edge of a run of duplicates you treat a match as
 *   "possible answer, keep going" instead of "done". The only difference between the two helpers is
 *   which side you continue toward after a match.
 *
 * COMPLEXITY
 *   Time  O(log n)  two independent halvings of the window
 *   Space O(1)      a handful of ints
 *
 * INTERVIEW FOLLOW-UPS
 *   - Count occurrences of target: last - first + 1 (or 0 when absent).
 *   - Solve with a single lowerBound helper: first = lowerBound(target),
 *     last = lowerBound(target + 1) - 1; check nums[first] == target.
 *   - Why not find any match then expand linearly: a run of n duplicates makes that O(n).
 *
 * RUN
 *   main() runs 4 cases (typical, absent, empty, all duplicates) and prints actual vs expected.
 */
import java.util.Arrays;

class FirstAndLastOccurrence {

    private int firstOccurrence(int[] nums, int target) {
        int low = 0, high = nums.length - 1;
        int first = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] == target) {
                first = mid;     // a match, but an earlier one may exist
                high = mid - 1;  // keep looking to the left
            } else if (nums[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return first;
    }

    private int lastOccurrence(int[] nums, int target) {
        int low = 0, high = nums.length - 1;
        int last = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] == target) {
                last = mid;      // a match, but a later one may exist
                low = mid + 1;   // keep looking to the right
            } else if (nums[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return last;
    }

    public int[] searchRange(int[] nums, int target) {
        int first = firstOccurrence(nums, target);
        if (first == -1) return new int[]{-1, -1}; // absent: no need for the second search
        int last = lastOccurrence(nums, target);
        return new int[]{first, last};
    }

    private static void print(String label, int[] nums, int target, String expected) {
        int[] actual = new FirstAndLastOccurrence().searchRange(nums, target);
        System.out.println(label + ": " + Arrays.toString(actual) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical       ", new int[]{5, 7, 7, 8, 8, 10}, 8, "[3, 4]");
        print("case 2 absent        ", new int[]{5, 7, 7, 8, 8, 10}, 6, "[-1, -1]");
        print("case 3 empty         ", new int[]{}, 0, "[-1, -1]");
        print("case 4 all duplicates", new int[]{2, 2, 2, 2}, 2, "[0, 3]");
    }
}

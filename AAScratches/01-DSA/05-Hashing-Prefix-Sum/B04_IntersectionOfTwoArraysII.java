/*
 * =====================================================================
 *  Intersection of Two Arrays II                         LeetCode 350 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given two integer arrays, return their intersection where each value appears as many
 *   times as it appears in BOTH arrays (min of the two counts). Output order does not matter.
 *   Arrays are unsorted and may contain duplicates; either may be empty.
 *
 * EXAMPLE
 *   nums1 = [1, 2, 2, 1], nums2 = [2, 2]           ->  [2, 2]     2 appears twice on each side
 *   nums1 = [4, 9, 5],    nums2 = [9, 4, 9, 8, 4]  ->  [9, 4]     9 and 4 once each in nums1
 *   nums1 = [],           nums2 = [1, 1]           ->  []         nothing to match
 *   nums1 = [3, 3, 3],    nums2 = [3]              ->  [3]        min(3, 1) = 1 copy
 *
 * APPROACH  (frequency map with multiplicity)
 *   1. Count every value of nums1 into a HashMap value -> remaining count.
 *   2. Scan nums2. If the value still has a positive count, emit it and decrement.
 *      The decrement is what caps each value at min(count1, count2).
 *   3. Copy the collected list into an int[] for the return type.
 *
 * KEY INSIGHT
 *   A Set answers "seen?"; a count map answers "how many are still unclaimed?". Decrement
 *   on every match and the min() falls out without ever computing both counts explicitly.
 *   Pattern: whenever duplicates must be preserved, upgrade Set<T> to Map<T, Integer>.
 *
 * COMPLEXITY
 *   Time  O(n + m)  one pass over each array, O(1) map operations
 *   Space O(n)      the map holds at most nums1's distinct values (count the smaller array)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Both arrays sorted: two pointers, emit on every equal pair, O(1) extra space.
 *   - nums1 tiny, nums2 on disk: count nums1 in memory and stream nums2 through it.
 *   - Both huge on disk: external sort both, then the two-pointer merge.
 *   - Distinct-only result (LeetCode 349): the same code with a Set instead of a Map.
 *
 * RUN
 *   main() runs 4 cases (typical, order-of-appearance, empty, capped count) and prints
 *   actual vs expected. Output order follows nums2, which is what the expected values show.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class IntersectionOfTwoArraysII {

    public static int[] intersect(int[] nums1, int[] nums2) {
        Map<Integer, Integer> remaining = new HashMap<>();
        for (int num : nums1) {
            remaining.merge(num, 1, Integer::sum);
        }

        List<Integer> matched = new ArrayList<>();
        for (int num : nums2) {
            int left = remaining.getOrDefault(num, 0);
            if (left > 0) {
                matched.add(num);
                remaining.put(num, left - 1);   // claim one copy so counts are capped at the min
            }
        }
        return toIntArray(matched);
    }

    private static int[] toIntArray(List<Integer> values) {
        int[] result = new int[values.size()];
        for (int i = 0; i < result.length; i++) result[i] = values.get(i);
        return result;
    }

    private static void print(String label, int[] nums1, int[] nums2, String expected) {
        System.out.println(label + ": " + Arrays.toString(intersect(nums1, nums2))
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (typical)   ", new int[]{1, 2, 2, 1}, new int[]{2, 2}, "[2, 2]");
        print("case 2 (nums2 order)", new int[]{4, 9, 5}, new int[]{9, 4, 9, 8, 4}, "[9, 4]");
        print("case 3 (empty)     ", new int[]{}, new int[]{1, 1}, "[]");
        print("case 4 (min count) ", new int[]{3, 3, 3}, new int[]{3}, "[3]");
    }
}

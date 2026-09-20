/*
 * =====================================================================
 *  Find the Difference of Two Arrays                   LeetCode 2215 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given two integer arrays nums1 and nums2, return a list of two lists:
 *   answer[0] holds the distinct values in nums1 that are NOT in nums2, and
 *   answer[1] holds the distinct values in nums2 that are NOT in nums1.
 *   Order within each list does not matter; duplicates must be removed.
 *
 * EXAMPLE
 *   nums1 = [1, 2, 3],    nums2 = [2, 4, 6]     ->  [[1, 3], [4, 6]]
 *   nums1 = [1, 2, 3, 3], nums2 = [1, 1, 2, 2]  ->  [[3], []]    3 listed once
 *   nums1 = [5, 5],       nums2 = [5]           ->  [[], []]     nothing unique
 *
 * APPROACH  (Two sets, one-sided difference)
 *   1. Load nums1 into set1 and nums2 into set2. The Set drops duplicates for free.
 *   2. For each value in set1, keep it if set2 does not contain it -> answer[0].
 *   3. For each value in set2, keep it if set1 does not contain it -> answer[1].
 *
 * KEY INSIGHT
 *   "In A but not in B" is a set-membership question, so build a set of B and ask
 *   contains() once per element of A. Building a set of A as well handles the
 *   de-duplication requirement without any extra bookkeeping. Two directions, two
 *   loops, same shape.
 *
 * COMPLEXITY
 *   Time  O(n + m)  build both sets, then one pass over each
 *   Space O(n + m)  the two sets
 *
 * INTERVIEW FOLLOW-UPS
 *   - Symmetric difference in one sorted list? Merge both results and sort (sibling file).
 *   - Intersection instead of difference? Flip the condition to contains() (LC 349).
 *   - Sorted inputs and no extra memory? Two pointers walking both arrays.
 *   - Values bounded to a small range? A boolean[] per array replaces each set.
 *
 * RUN
 *   main() runs 3 cases (typical, duplicates on both sides, all shared) and prints
 *   actual vs expected. HashSet iterates small non-negative ints in ascending order,
 *   which is why the expected order below is stable; the problem accepts any order.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class FindTheDifferenceOfTwoArrays {

    public static List<List<Integer>> findDifference(int[] nums1, int[] nums2) {
        Set<Integer> set1 = toSet(nums1);
        Set<Integer> set2 = toSet(nums2);

        List<Integer> onlyIn1 = new ArrayList<>();
        for (int num : set1) {
            if (!set2.contains(num)) onlyIn1.add(num);
        }

        List<Integer> onlyIn2 = new ArrayList<>();
        for (int num : set2) {
            if (!set1.contains(num)) onlyIn2.add(num);
        }

        List<List<Integer>> result = new ArrayList<>();
        result.add(onlyIn1);
        result.add(onlyIn2);
        return result;
    }

    // Loading into a Set is what removes duplicates from the answer.
    private static Set<Integer> toSet(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int num : nums) set.add(num);
        return set;
    }

    private static void check(String label, int[] nums1, int[] nums2, String expected) {
        System.out.println(label + " nums1=" + Arrays.toString(nums1)
                + ", nums2=" + Arrays.toString(nums2)
                + " -> " + findDifference(nums1, nums2)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("case 1 (typical):    ", new int[]{1, 2, 3}, new int[]{2, 4, 6}, "[[1, 3], [4, 6]]");
        check("case 2 (duplicates): ", new int[]{1, 2, 3, 3}, new int[]{1, 1, 2, 2}, "[[3], []]");
        check("case 3 (all shared): ", new int[]{5, 5}, new int[]{5}, "[[], []]");
    }
}

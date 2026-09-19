/**
 * Problem: Given two integer arrays, return a sorted list of every element that appears in
 *          exactly one of them (the symmetric difference = union minus intersection).
 *
 * Approaches:
 *   1. setDifferenceWithHashSets   - two HashSets, scan each for elements absent from the other,
 *                                    sort the result.            Time O(n + m + k log k), Space O(n + m)
 *   2. setDifferenceWithTwoPointers - sort both arrays, walk them with two pointers, skip equal
 *                                    elements, append leftovers. Time O(n log n + m log m), Space O(n + m)
 *
 * Related but different problem: LeetCode 2215 "Find the Difference of Two Arrays" returns the two
 * one-sided differences as separate lists ([[nums1-only], [nums2-only]]). See
 * B01_FindTheDifferenceOfTwoArrays in this folder - same HashSet technique, different output shape.
 */
import java.util.*;

class SetDifferenceOfTwoArrays {

    /**
     * Approach 1: HashSets.
     * - Insert each array's values into separate HashSets to deduplicate.
     * - For every element in set1 not present in set2, add it to the result.
     * - Repeat for elements in set2 not present in set1.
     * - Sort the resulting list before returning (HashSet iteration order is arbitrary).
     *
     * Time:  O(n + m) for building sets and scanning, plus O(k log k) to sort the result (k = result size).
     * Space: O(n + m) for the two HashSets, plus O(k) for the output list.
     */
    public List<Integer> setDifferenceWithHashSets(int[] nums1, int[] nums2) {
        Set<Integer> set1 = new HashSet<>();
        Set<Integer> set2 = new HashSet<>();
        for (int num : nums1) set1.add(num);
        for (int num : nums2) set2.add(num);

        List<Integer> result = new ArrayList<>();
        for (int num : set1) {
            if (!set2.contains(num)) result.add(num);
        }
        for (int num : set2) {
            if (!set1.contains(num)) result.add(num);
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Approach 2: Sort + two pointers.
     * - Sort both input arrays (note: sorts the caller's arrays in place).
     * - Traverse them simultaneously: when the heads are equal, skip both (element is in the
     *   intersection); otherwise add the smaller head and advance that pointer only.
     * - Append any remaining elements from either array after traversal.
     * - Output comes out already sorted, so no final sort is needed.
     *
     * Caveat: unlike the HashSet version this does NOT deduplicate values that repeat within a
     * single array (e.g. nums1 = {5,1,3,3}, nums2 = {3,9,5} yields [1, 3, 9], not [1, 9]).
     * Fine for distinct-element inputs; otherwise dedupe first or use the HashSet version.
     *
     * Time:  O(n log n + m log m) due to sorting (n = nums1.length, m = nums2.length).
     * Space: O(n + m) for the result list (plus sorting overhead).
     */
    public List<Integer> setDifferenceWithTwoPointers(int[] nums1, int[] nums2) {
        Arrays.sort(nums1);
        Arrays.sort(nums2);
        int i = 0, j = 0;
        List<Integer> result = new ArrayList<>();
        while (i < nums1.length && j < nums2.length) {
            if (nums1[i] == nums2[j]) {
                i++;
                j++;
            } else if (nums1[i] < nums2[j]) {
                result.add(nums1[i++]);
            } else {
                result.add(nums2[j++]);
            }
        }
        // Add leftovers
        while (i < nums1.length) result.add(nums1[i++]);
        while (j < nums2.length) result.add(nums2[j++]);
        return result;
    }

    public static void main(String[] args) {
        SetDifferenceOfTwoArrays solution = new SetDifferenceOfTwoArrays();
        int[] nums1 = {1, 2, 3};
        int[] nums2 = {2, 4, 6};
        // Pass copies to the two-pointer version so its in-place sort cannot affect the other run.
        System.out.println("HashSets    : " + solution.setDifferenceWithHashSets(nums1, nums2));   // [1, 3, 4, 6]
        System.out.println("Two pointers: " + solution.setDifferenceWithTwoPointers(nums1.clone(), nums2.clone())); // [1, 3, 4, 6]

        // Second case shows where the two approaches diverge: 3 repeats inside a.
        int[] a = {5, 1, 3, 3};
        int[] b = {3, 9, 5};
        System.out.println("HashSets    : " + solution.setDifferenceWithHashSets(a, b));   // [1, 9]
        System.out.println("Two pointers: " + solution.setDifferenceWithTwoPointers(a.clone(), b.clone())); // [1, 3, 9]  <- extra 3: in-array duplicate is not deduped (see caveat)
    }
}

/**
 * Problem:
 *   Given two integer arrays, return a sorted list of all elements that appear in
 *   exactly one of the arrays (the symmetric difference).
 *
 * Approach:
 *   • Insert each array's values into separate HashSets to deduplicate.
 *   • For every element in set1 not present in set2, add it to the result.
 *   • Repeat for elements in set2 not present in set1.
 *   • Sort the resulting list before returning.
 *
 * Complexity:
 *   Time:  O(n + m) for building sets and scanning,
 *         plus O(k log k) for sorting the final list (k = size of result).
 *   Space: O(n + m) for the two HashSets, plus O(k) for the output list.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class SetDifferenceOfTwoArrays {

    public List<Integer> setDifference(int[] nums1, int[] nums2) {
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

    public static void main(String[] args) {
        SetDifferenceOfTwoArrays solution = new SetDifferenceOfTwoArrays();
        int[] nums1 = {1, 2, 3};
        int[] nums2 = {2, 4, 6};
        List<Integer> result = solution.setDifference(nums1, nums2);
        System.out.println(result); // Output: [1, 3, 4, 6]
    }
}

/**
 * Computes the symmetric difference of two integer arrays.
 *
 * Given two unsorted arrays, this method returns a list containing all
 * elements that appear in exactly one of the arrays (i.e., the union minus the intersection).
 *
 * Approach:
 * 1. Sort both input arrays.
 * 2. Use two pointers to traverse them simultaneously, adding the smaller element
 *    when they differ and advancing appropriately; skip equal elements.
 * 3. Append any remaining elements from either array after traversal.
 *
 * Time Complexity: O(n log n + m log m) due to sorting (n = nums1.length, m = nums2.length).
 * Space Complexity: O(n + m) for the result list and the sorted arrays' overhead.
 */
import java.util.*;

class SetDifferenceTwoPointers {
    public List<Integer> setDifference(int[] nums1, int[] nums2) {
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
        SetDifferenceTwoPointers solution = new SetDifferenceTwoPointers();
        int[] nums1 = {1, 2, 3};
        int[] nums2 = {2, 4, 6};
        List<Integer> result = solution.setDifference(nums1, nums2);
        System.out.println(result); // Output: [1, 3, 4, 6]
    }
}

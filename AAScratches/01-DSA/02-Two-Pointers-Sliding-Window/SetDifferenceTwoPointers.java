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

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

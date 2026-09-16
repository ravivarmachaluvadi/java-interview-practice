import java.util.*;
/**
 * Input: nums1 = [1,2,3], nums2 = [2,4,6]
 * <p>
 * Output: [[1,3],[4,6]]
 */
class FindTheDifferenceOfTwoArrays {

    public static List<List<Integer>> findDifference(int[] nums1, int[] nums2) {
        Set<Integer> set1 = new HashSet<>();
        Set<Integer> set2 = new HashSet<>();
        List<List<Integer>> result = new ArrayList<>();

        for (int num : nums1) set1.add(num);
        for (int num : nums2) set2.add(num);

        List<Integer> diff1 = new ArrayList<>();
        List<Integer> diff2 = new ArrayList<>();

        for (int num : set1) {
            if (!set2.contains(num)) diff1.add(num);
        }

        for (int num : set2) {
            if (!set1.contains(num)) diff2.add(num);
        }

        result.add(diff1);
        result.add(diff2);

        return result;
    }

    public static void main(String[] args) {
        System.out.println(findDifference(new int[]{1, 2, 3}, new int[]{2, 4, 6}));
    }
}
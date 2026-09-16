import java.util.HashSet;
import java.util.Set;

// https://leetcode.com/problems/contains-duplicate/description/
// 217. Contains Duplicate
class ContainsDuplicate {
    public boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int num : nums) {
            if (seen.contains(num)) {
                return true;
            }
            seen.add(num);
        }
        return false;
    }

    // Example test
    public static void main(String[] args) {
        ContainsDuplicate sol = new ContainsDuplicate();

        int[] nums1 = {1, 2, 3, 1};
        System.out.println(sol.containsDuplicate(nums1));  // should print true

        int[] nums2 = {1, 2, 3, 4};
        System.out.println(sol.containsDuplicate(nums2));  // should print false

        int[] nums3 = {1, 1, 1, 3, 3, 4, 3, 2, 4, 2};
        System.out.println(sol.containsDuplicate(nums3));  // should print true
    }
}

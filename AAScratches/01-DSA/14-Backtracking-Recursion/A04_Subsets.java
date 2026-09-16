import java.util.ArrayList;
import java.util.List;

// Array, Backtracking, Bit Manipulation
// https://leetcode.com/problems/subsets

/**
 * Example 1:
 * <p>
 * Input: nums = [1,2,3]
 * <p>
 * Output: [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
 */
class Subsets {
    public static void subsets(int[] arr, int idx, List<List<Integer>> list, List<Integer> ls) {
        if (idx >= arr.length) {
            list.add(new ArrayList<>(ls));
            return;
        }
        // don't pick the element
        subsets(arr, idx + 1, list, ls);
        // pick the element
        ls.add(arr[idx]);
        subsets(arr, idx + 1, list, ls);
        ls.remove(ls.size() - 1);
    }

    public static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> list = new ArrayList<>();
        List<Integer> ls = new ArrayList<>();
        subsets(nums, 0, list, ls);
        return list;
    }

    public static void main(String[] args) {
        List<List<Integer>> subsets = subsets(new int[]{1, 2, 3});
        System.out.println(subsets);
    }
}
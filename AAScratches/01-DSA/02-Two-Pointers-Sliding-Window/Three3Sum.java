import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Array, Two Pointers, Sorting
// https://leetcode.com/problems/3sum
// nums[i] + nums[j] + nums[k] == 0 such that i < j < k
class Three3Sum {
    public static List<List<Integer>> triplet(int n, int[] arr) {
        List<List<Integer>> ans = new ArrayList<>();
        Arrays.sort(arr);

        // [1,1,1,1,2,3] last 1 will be taken
        //    {-4,-1,-1,0,1,2}
        for (int i = 0; i < n; i++) {
            //remove duplicates:
            if (i != 0 && arr[i] == arr[i - 1]) continue;
            int j = i + 1;
            int k = n - 1;
            // remember j<k
            while (j < k) {
                int sum = arr[i] + arr[j] + arr[k];
                if (sum < 0) {
                    j++;
                } else if (sum > 0) {
                    k--;
                } else {
                    List<Integer> temp = Arrays.asList(arr[i], arr[j], arr[k]);
                    ans.add(temp);
                    // remember
                    j++;
                    k--;
                    // skip the duplicates:
                    // these loops inside of else block
                    while (j < k && arr[j] == arr[j - 1]) j++;
                    while (j < k && arr[k] == arr[k + 1]) k--;
                }
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        int[] arr = {-1, 0, 1, 2, -1, -4};
        int n = arr.length;
        List<List<Integer>> ans = triplet(n, arr);
        for (List<Integer> it : ans) {
            System.out.print("[");
            for (Integer i : it) {
                System.out.print(i + " ");
            }
            System.out.print("] ");
        }
        System.out.println();
    }
}

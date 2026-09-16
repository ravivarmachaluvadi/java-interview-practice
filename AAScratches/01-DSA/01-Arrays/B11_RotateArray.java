/**
 * Input: nums = [1,2,3,4,5,6,7], k = 3
 * <p>
 * Output: [5,6,7,1,2,3,4]
 */
// Array, Math, Two Pointers
// https://leetcode.com/problems/rotate-array
public static void rotate(int[] nums, int k) {

    int n = nums.length;
    k = k % n;
    // order of below statements matter
    reverse(nums, 0, n - 1);
    reverse(nums, 0, k - 1);
    reverse(nums, k, n - 1);
}

public static void reverse(int[] nums, int s, int e) {
    while (s < e) {
        int temp = nums[s];
        nums[s] = nums[e];
        nums[e] = temp;
        s++;
        e--;
    }

}

void main() {
    int[] nums = {1, 2, 3, 4, 5, 6, 7};
    rotate(nums, 3);
    IO.println(Arrays.toString(nums));
}
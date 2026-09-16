// 1   2   3  4  5
// 1   2   6  24 120
// 120 120 60 20 5
// inclusive of standing element itself
public static int[] productExceptSelf(int[] nums) {
    int n = nums.length;
    int[] ans = new int[n];
    int runningProduct = 1;
    // First pass: Fill ans with the product of elements to the left of each index
    for (int i = 0; i < n; i++) {
        ans[i] = runningProduct;
        runningProduct *= nums[i];
    }
    runningProduct = 1;
    // Second pass: Multiply by the product of elements to the right of each index
    for (int i = n - 1; i >= 0; i--) {
        // remember below not only assigning but multiplying
        ans[i] *= runningProduct;
        runningProduct *= nums[i];
    }
    return ans;
}

void main() {
    int[] nums = {1, 2, 3, 4};
    int[] result = productExceptSelf(nums);
    IO.println(Arrays.toString(result));
    // [24, 12, 8, 6]
}

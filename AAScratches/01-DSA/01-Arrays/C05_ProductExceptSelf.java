/*
 * =====================================================================
 *  Product of Array Except Self             LeetCode 238 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given nums (length >= 2), return answer where answer[i] is the product of every
 *   element except nums[i]. Must run in O(n) and must not use division. Follow-up:
 *   O(1) extra space, not counting the output array.
 *
 * EXAMPLE
 *   nums = [1, 2, 3, 4]        ->  [24, 12, 8, 6]
 *   nums = [-1, 1, 0, -3, 3]   ->  [0, 0, 9, 0, 0]   one zero: only that slot survives
 *   nums = [0, 0]              ->  [0, 0]            two zeros: everything is zero
 *   nums = [2, 3]              ->  [3, 2]            smallest allowed input
 *
 * APPROACH  (prefix and suffix products)
 *   1. Pass 1, left to right: ans[i] = product of nums[0..i-1]. Write the running
 *      product into ans[i] BEFORE multiplying nums[i] into it, so i is excluded.
 *   2. Pass 2, right to left: keep a scalar suffix product of nums[i+1..n-1] and
 *      MULTIPLY it into ans[i] (not assign), then fold nums[i] into the scalar.
 *   3. Now ans[i] = prefix(i) * suffix(i), with nums[i] absent from both halves.
 *
 * KEY INSIGHT
 *   "Everything except i" = "everything left of i" x "everything right of i". Both
 *   are running products. The output array holds the prefix half while the suffix
 *   half lives in one scalar, which is what makes the extra space O(1).
 *   Pattern: two-pass prefix/suffix decomposition, the same skeleton as trapping
 *   rain water, candy, and best seat.
 *
 * COMPLEXITY
 *   Time  O(n)  two passes Space O(1)  extra beyond the output array
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not total / nums[i]? Zeros break it and the problem forbids division.
 *     Be ready to describe the count-the-zeros division variant anyway.
 *   - Overflow: the product of 1e5 ints does not fit an int; long or modulo 1e9+7.
 *   - Start from the three-array version (prefix[], suffix[]) and collapse to O(1).
 *
 * RUN
 *   main() runs 4 cases (typical, one zero, two zeros, two elements) and prints
 *   actual vs expected.
 */
import java.util.Arrays;

class ProductExceptSelf {

    public static int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] ans = new int[n];

        // Pass 1: ans[i] = product of everything to the LEFT of i
        int runningProduct = 1;
        for (int i = 0; i < n; i++) {
            ans[i] = runningProduct;       // written before nums[i] joins the product
            runningProduct *= nums[i];
        }

        // Pass 2: multiply in the product of everything to the RIGHT of i
        runningProduct = 1;
        for (int i = n - 1; i >= 0; i--) {
            ans[i] *= runningProduct;      // multiply, not assign: keep the left half
            runningProduct *= nums[i];
        }
        return ans;
    }

    public static void main(String[] args) {
        print("case 1 typical   ", productExceptSelf(new int[]{1, 2, 3, 4}), "[24, 12, 8, 6]");
        print("case 2 one zero  ", productExceptSelf(new int[]{-1, 1, 0, -3, 3}),
                "[0, 0, 9, 0, 0]");
        print("case 3 two zeros ", productExceptSelf(new int[]{0, 0}), "[0, 0]");
        print("case 4 two elems ", productExceptSelf(new int[]{2, 3}), "[3, 2]");
    }

    private static void print(String label, int[] actual, String expected) {
        System.out.println(label + ": " + Arrays.toString(actual) + "   expected " + expected);
    }
}

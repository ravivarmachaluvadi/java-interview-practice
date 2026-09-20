/*
 * =====================================================================
 *  Find Pivot Index                                    LeetCode 724 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array nums, return the LEFTMOST index i where the sum of all
 *   elements strictly left of i equals the sum of all elements strictly right of i.
 *   An empty side sums to 0. Return -1 if no such index exists.
 *   Values may be negative; 1 <= n <= 1e4, |nums[i]| <= 1000.
 *
 * EXAMPLE
 *   nums = [1, 7, 3, 6, 5, 6]  ->  3     left 1+7+3 = 11, right 5+6 = 11
 *   nums = [1, 2, 3]           ->  -1    no split balances
 *   nums = [2, 1, -1]          ->  0     left side is empty (0), right 1 + -1 = 0
 *   nums = [-1, -1, -1, -1, -1, 0] -> 2  negatives: left -2, right -2
 *   nums = []                  ->  -1    nothing to pivot on
 *
 * APPROACH  (total sum minus running prefix)
 *   1. One pass to compute totalSum of the whole array.
 *   2. Walk left to right carrying leftSum = sum of nums[0 .. i-1].
 *   3. At index i the right sum is totalSum - leftSum - nums[i]; if it equals
 *      leftSum, i is the pivot: return it immediately (leftmost wins).
 *   4. Otherwise add nums[i] to leftSum and continue. Return -1 after the loop.
 *
 * KEY INSIGHT
 *   You never need a suffix-sum array. Since left + nums[i] + right == total,
 *   the right sum is derivable from the total and the running left sum, so a
 *   single scalar replaces the second array. Pattern to recognise: whenever a
 *   condition needs "sum of everything except a window", compute the total once
 *   and subtract.
 *
 * COMPLEXITY
 *   Time  O(n)  two linear passes
 *   Space O(1)  three integers
 *
 * INTERVIEW FOLLOW-UPS
 *   - Can it be done in one pass? Not without knowing the total first; two passes
 *     is optimal for streaming input you cannot buffer.
 *   - Same idea generalises to "equilibrium index" and to prefix-sum problems
 *     like subarray-sum-equals-k, where the subtraction moves into a hash map.
 *   - Why check before adding nums[i]? leftSum must exclude the pivot itself.
 *
 * RUN
 *   main() runs 5 cases (typical, no pivot, pivot at index 0, negatives, empty)
 *   and prints actual vs expected.
 */

class PivotIndex {

    public static int pivotIndex(int[] nums) {
        int totalSum = 0;
        for (int num : nums) {
            totalSum += num;
        }

        int leftSum = 0;                       // sum of nums[0 .. i-1]
        for (int i = 0; i < nums.length; i++) {
            int rightSum = totalSum - leftSum - nums[i];   // everything after i
            if (leftSum == rightSum) {
                return i;                      // first match is the leftmost pivot
            }
            leftSum += nums[i];                // nums[i] joins the left side
        }
        return -1;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical      ", pivotIndex(new int[]{1, 7, 3, 6, 5, 6}), 3);
        print("case 2 no pivot     ", pivotIndex(new int[]{1, 2, 3}), -1);
        print("case 3 pivot at 0   ", pivotIndex(new int[]{2, 1, -1}), 0);
        print("case 4 negatives    ", pivotIndex(new int[]{-1, -1, -1, -1, -1, 0}), 2);
        print("case 5 empty        ", pivotIndex(new int[]{}), -1);
    }
}

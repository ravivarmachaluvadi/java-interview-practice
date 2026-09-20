/*
 * =====================================================================
 *  Single Number                              LeetCode 136 | Easy  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   A non-empty array where every element appears exactly twice except
 *   one, which appears once. Return that element.
 *   Required: linear time and constant extra space, so no HashSet and
 *   no sorting.
 *
 * EXAMPLE
 *   nums = [4, 1, 2, 1, 2]   ->  4    the 1s and 2s pair off
 *   nums = [7]               ->  7    single element, the loop runs once
 *   nums = [-3, 5, -3]       ->  5    XOR works on the sign bit too
 *   nums = [0, 1, 0]         ->  1    a zero pair cancels like any other
 *
 * APPROACH  (XOR self-cancellation)
 *   1. Start an accumulator at 0.
 *   2. XOR every element into it, in any order.
 *   3. What is left is the unpaired element.
 *
 *   Truth table the whole trick rests on:
 *     A B  A^B        so  x ^ x = 0   (a pair vanishes)
 *     0 0   0             x ^ 0 = x   (0 is the identity)
 *     0 1   1
 *     1 0   1         and XOR is commutative and associative, so the
 *     1 1   0         pairs cancel no matter how they are interleaved
 *
 * KEY INSIGHT
 *   XOR is a self-inverse operation: applying a value twice undoes it.
 *   That makes it the constant-space way to cancel duplicates without
 *   remembering which values you have already seen. Recognise it any
 *   time the problem says "everything appears an even number of times
 *   except one".
 *
 * COMPLEXITY
 *   Time  O(n)  one pass, one XOR per element
 *   Space O(1)  a single int accumulator
 *
 * INTERVIEW FOLLOW-UPS
 *   - Two elements appear once (LeetCode 260): XOR everything, take the
 *     lowest set bit of the result, and split the array on that bit
 *   - Every element appears three times except one (LeetCode 137):
 *     XOR no longer cancels - count each bit position mod 3
 *   - Missing Number (LeetCode 268): XOR the indices with the values
 *
 * RUN
 *   main() runs 4 cases (typical, single element, negatives, zeros)
 *   and prints actual vs expected.
 */

import java.util.Arrays;

class SingleNumber {

    public int singleNumber(int[] nums) {
        int xor = 0;
        for (int val : nums) {
            xor ^= val;   // duplicates cancel; the lone value survives
        }
        return xor;
    }

    public static void main(String[] args) {
        SingleNumber solver = new SingleNumber();

        print(solver, new int[]{4, 1, 2, 1, 2}, 4);
        print(solver, new int[]{7}, 7);
        print(solver, new int[]{-3, 5, -3}, 5);
        print(solver, new int[]{0, 1, 0}, 1);
    }

    private static void print(SingleNumber solver, int[] nums, int expected) {
        System.out.println(Arrays.toString(nums) + " -> " + solver.singleNumber(nums)
                + "   expected " + expected);
    }
}

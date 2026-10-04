/*
 * =====================================================================
 *  P099 XOR Tricks   Canonical LC 136 | Easy
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 136, Single Number)
 *   Every element appears twice except one. Find it in O(n) time and O(1) extra space.
 *
 * EXAMPLE
 *   [2, 2, 1]        ->  1
 *   [4, 1, 2, 1, 2]  ->  4
 *   [1]              ->  1
 *
 * RECOGNIZE WHEN
 *   - "every value appears twice (or k times) except one / two", "find the missing / extra
 *     value", with O(1) space.
 *   - Pairs should cancel out; order does not matter.
 *   Not this if: extra space is fine and clarity matters -> a HashMap count
 *   (P021_FrequencyCanonicalKey); values lie in 1..n and you may modify the array ->
 *   P005_CyclicSortIndexAsHash.
 *
 * TEMPLATE
 *   x ^ x = 0, x ^ 0 = x, XOR is commutative and associative
 *   single:   r = 0; for x in a: r ^= x                      // pairs cancel
 *   missing:  r = n; for i, x: r ^= i ^ x                    // indices vs values
 *   two singles: x = a ^ b (all XOR); bit = x & -x; split numbers by that bit
 *   k copies: count each of the 32 bits mod k
 *
 * APPROACH
 *   1. XOR all numbers together.
 *   2. Each paired value cancels itself, so only the single one remains.
 *
 * KEY INSIGHT
 *   XOR is "addition without carry, mod 2" per bit, so anything appearing an even number of
 *   times vanishes. When two values remain, any set bit of their XOR is a bit where they
 *   differ, which splits the array into two groups that each hold one of them.
 *
 * COMPLEXITY
 *   Time O(n), space O(1). LC 137: O(32 n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 268  Missing Number           XOR of 0..n with every value
 *   [coded] LC 260  Single Number III        lowest set bit of a ^ b separates a from b
 *   [coded] LC 137  Single Number II         others appear 3 times: per-bit counts mod 3
 *           LC 389  Find the Difference      XOR all characters of s and t
 *           LC 1720 Decode XORed Array       arr[i + 1] = encoded[i] ^ arr[i]
 *           LC 2433 Find Original from Prefix XOR  arr[i] = pref[i] ^ pref[i - 1]
 *           LC 1310 XOR Queries              prefix XOR -> P001_PrefixSumRangeQuery
 *
 * PITFALLS
 *   - x & -x isolates the lowest set bit; it works for negative x too (two's complement).
 *   - LC 137 with negatives: rebuild all 32 bits, including the sign bit.
 *   - Sum-based "missing number" can overflow; XOR cannot.
 *
 * DEEP DIVE
 *   A03_SingleNumber, B01_MissingNumber (17-Math-Bit-Manipulation)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;

class XorTricks {

    // Canonical LC 136.
    static int singleNumber(int[] nums) {
        int r = 0;
        for (int x : nums) {
            r ^= x;
        }
        return r;
    }

    // LC 268: nums holds n distinct values from 0..n.
    static int missingNumber(int[] nums) {
        int r = nums.length;
        for (int i = 0; i < nums.length; i++) {
            r ^= i ^ nums[i];
        }
        return r;
    }

    // LC 260: exactly two values appear once, the rest twice.
    static int[] singleNumberIII(int[] nums) {
        int both = 0;
        for (int x : nums) {
            both ^= x;                             // = a ^ b
        }
        int bit = both & -both;                    // a bit where a and b differ
        int a = 0;
        for (int x : nums) {
            if ((x & bit) != 0) {
                a ^= x;                            // only the group with that bit
            }
        }
        int[] out = {a, both ^ a};
        Arrays.sort(out);                          // any order is accepted
        return out;
    }

    // LC 137: every value appears three times except one.
    static int singleNumberII(int[] nums) {
        int result = 0;
        for (int b = 0; b < 32; b++) {
            int count = 0;
            for (int x : nums) {
                count += (x >> b) & 1;
            }
            if (count % 3 != 0) {
                result |= 1 << b;                  // the single number owns this bit
            }
        }
        return result;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 136 [2,2,1]", singleNumber(new int[]{2, 2, 1}), 1);
        check("LC 136 [4,1,2,1,2]", singleNumber(new int[]{4, 1, 2, 1, 2}), 4);
        check("LC 136 [1]", singleNumber(new int[]{1}), 1);

        check("LC 268 [3,0,1]", missingNumber(new int[]{3, 0, 1}), 2);
        check("LC 268 [0,1] missing n", missingNumber(new int[]{0, 1}), 2);
        check("LC 268 nine values", missingNumber(new int[]{9, 6, 4, 2, 3, 5, 7, 0, 1}), 8);

        check("LC 260 [1,2,1,3,2,5]",
                Arrays.toString(singleNumberIII(new int[]{1, 2, 1, 3, 2, 5})), "[3, 5]");
        check("LC 260 [-1,0]", Arrays.toString(singleNumberIII(new int[]{-1, 0})), "[-1, 0]");

        check("LC 137 [2,2,3,2]", singleNumberII(new int[]{2, 2, 3, 2}), 3);
        check("LC 137 [0,1,0,1,0,1,99]", singleNumberII(new int[]{0, 1, 0, 1, 0, 1, 99}), 99);
        check("LC 137 negative single",
                singleNumberII(new int[]{-2, -2, 1, 1, 4, 1, 4, 4, -4, -2}), -4);
    }
}

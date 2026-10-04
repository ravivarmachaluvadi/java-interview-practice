/*
 * =====================================================================
 *  P009 Reverse Tricks: Rotate, Next Permutation   Canonical LC 189 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 189, Rotate Array)
 *   Rotate nums to the right by k steps, in place, with O(1) extra space. k may be
 *   larger than the length.
 *
 * EXAMPLE
 *   [1, 2, 3, 4, 5, 6, 7], k = 3  ->  [5, 6, 7, 1, 2, 3, 4]
 *   [-1, -100, 3, 99],     k = 2  ->  [3, 99, -1, -100]
 *   [1, 2],                k = 3  ->  [2, 1]          the trap: k > n, use k % n
 *
 * RECOGNIZE WHEN
 *   - Move a block of an array (or string) to the other end in place.
 *   - "Next / previous arrangement in dictionary order", "next bigger number, same digits".
 *   - Reverse the ORDER of words but keep each word readable.
 *   Not this if: the sequence is a linked list -> P034_GapPointers (LC 61); a matrix
 *   rotation -> P010_MatrixInPlace (transpose + reverse rows).
 *
 * TEMPLATE
 *   rotate right by k:   k %= n; reverse(0, n-1); reverse(0, k-1); reverse(k, n-1)
 *   rotate left by k:    reverse(0, k-1); reverse(k, n-1); reverse(0, n-1)
 *   next permutation:    i = last index with a[i] < a[i+1]; if i >= 0, swap a[i] with the
 *                        last a[j] > a[i]; then reverse(i+1, n-1)
 *
 * APPROACH
 *   1. Reverse everything: the last k elements are now in front, but backwards.
 *   2. Reverse the first k and the remaining n - k to restore each block's order.
 *
 * KEY INSIGHT
 *   Reversal is its own inverse and composes nicely: reversing the whole and then each
 *   block swaps the blocks while keeping their insides in order, all with O(1) memory.
 *   The same "fix the suffix with one reverse" idea drives next permutation.
 *
 * COMPLEXITY
 *   Time O(n), space O(1) for all three.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 31   Next Permutation          find the pivot from the right, swap with the
 *                                             next bigger digit, reverse the suffix
 *   [coded] LC 151  Reverse Words in String   reverse the whole, then each word; squeeze
 *                                             the spaces while copying
 *           LC 556  Next Greater Element III  LC 31 on the digits; return -1 if > int max
 *           LC 186  Reverse Words II          char[] in place, no spaces to squeeze
 *           LC 796  Rotate String             goal is a rotation iff (s + s) contains goal
 *           LC 61   Rotate List               -> P034_GapPointers
 *
 * PITFALLS
 *   - Forgetting k %= n (k may be bigger than n, and n may be 1).
 *   - Next permutation: when no pivot exists (descending), reverse the whole array.
 *   - Find the swap partner from the RIGHT: the suffix is descending, so the first value
 *     from the right that is bigger is the smallest bigger value.
 *
 * DEEP DIVE
 *   C09_RotateArray, C10_NextGreaterPermutation (01-Arrays),
 *   C01_ReverseWordsInString (04-Strings)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;

class ReverseTricks {

    static void reverse(int[] a, int i, int j) {
        while (i < j) {
            int t = a[i];
            a[i++] = a[j];
            a[j--] = t;
        }
    }

    // Canonical LC 189.
    static int[] rotate(int[] nums, int k) {
        int n = nums.length;
        k %= n;
        reverse(nums, 0, n - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, n - 1);
        return nums;
    }

    // LC 31.
    static int[] nextPermutation(int[] nums) {
        int i = nums.length - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) {
            i--;                                   // suffix nums[i+1..] is non-increasing
        }
        if (i >= 0) {
            int j = nums.length - 1;
            while (nums[j] <= nums[i]) {
                j--;                               // smallest value in the suffix > nums[i]
            }
            int t = nums[i];
            nums[i] = nums[j];
            nums[j] = t;
        }
        reverse(nums, i + 1, nums.length - 1);     // smallest arrangement of the suffix
        return nums;
    }

    // LC 151: reverse the whole string, then each word, squeezing extra spaces.
    static String reverseWords(String s) {
        char[] c = s.toCharArray();
        reverseChars(c, 0, c.length - 1);
        int w = 0;
        int r = 0;
        while (r < c.length) {
            if (c[r] == ' ') {
                r++;
                continue;
            }
            if (w > 0) {
                c[w++] = ' ';                      // one space between words
            }
            int start = w;
            while (r < c.length && c[r] != ' ') {
                c[w++] = c[r++];
            }
            reverseChars(c, start, w - 1);         // word is backwards; flip it
        }
        return new String(c, 0, w);
    }

    static void reverseChars(char[] c, int i, int j) {
        while (i < j) {
            char t = c[i];
            c[i++] = c[j];
            c[j--] = t;
        }
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 189 k=3", Arrays.toString(rotate(new int[]{1, 2, 3, 4, 5, 6, 7}, 3)),
                "[5, 6, 7, 1, 2, 3, 4]");
        check("LC 189 k=2",
                Arrays.toString(rotate(new int[]{-1, -100, 3, 99}, 2)), "[3, 99, -1, -100]");
        check("LC 189 k=3 > n=2", Arrays.toString(rotate(new int[]{1, 2}, 3)), "[2, 1]");
        check("LC 189 k=0", Arrays.toString(rotate(new int[]{1, 2, 3}, 0)), "[1, 2, 3]");

        check("LC 31 [1,2,3]", Arrays.toString(nextPermutation(new int[]{1, 2, 3})), "[1, 3, 2]");
        check("LC 31 [3,2,1] wraps",
                Arrays.toString(nextPermutation(new int[]{3, 2, 1})), "[1, 2, 3]");
        check("LC 31 [1,1,5]", Arrays.toString(nextPermutation(new int[]{1, 1, 5})), "[1, 5, 1]");
        check("LC 31 [1,3,2]", Arrays.toString(nextPermutation(new int[]{1, 3, 2})), "[2, 1, 3]");

        check("LC 151 [the sky is blue]",
                "[" + reverseWords("the sky is blue") + "]", "[blue is sky the]");
        check("LC 151 [  hello world  ]",
                "[" + reverseWords("  hello world  ") + "]", "[world hello]");
        check("LC 151 [a good   example]",
                "[" + reverseWords("a good   example") + "]", "[example good a]");
    }
}

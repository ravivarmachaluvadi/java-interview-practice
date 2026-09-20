/*
 * =====================================================================
 *  Next Permutation                           LeetCode 31 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a sequence of integers, rearrange it in place into the lexicographically
 *   next greater permutation. If it is already the largest (fully descending), wrap
 *   around to the smallest (fully ascending). Duplicates are allowed.
 *
 * EXAMPLE
 *   [2, 1, 5, 4, 3, 0, 0]  ->  [2, 3, 0, 0, 1, 4, 5]
 *   [1, 2, 3]              ->  [1, 3, 2]
 *   [3, 2, 1]              ->  [1, 2, 3]     no pivot: wrap to ascending
 *   [1, 1, 5]              ->  [1, 5, 1]     duplicates
 *   [1]                    ->  [1]
 *
 * APPROACH  (pivot, swap, reverse suffix)
 *   1. Scan from the right for the first index ind where A[ind] < A[ind+1]. Everything
 *      right of ind is non-increasing, i.e. already the largest arrangement of itself.
 *   2. No such ind: the whole sequence is descending; reverse it and return.
 *   3. Scan from the right for the first value greater than A[ind] and swap the two.
 *      Scanning from the right finds the SMALLEST value larger than A[ind], because the
 *      suffix is sorted descending.
 *   4. Reverse the suffix after ind. It was descending, so reversing makes it ascending,
 *      the smallest arrangement, which gives the very next permutation.
 *
 * KEY INSIGHT
 *   The next permutation changes the sequence as far to the right as possible: keep the
 *   longest descending suffix, bump the element just before it by the smallest possible
 *   amount, then make the suffix as small as possible. "Find pivot, swap with next
 *   larger, reverse the tail" is a three-step recipe to memorise, not derive.
 *
 * COMPLEXITY
 *   Time  O(n)  three linear scans of the suffix at most
 *   Space O(1)  in place (Collections.reverse on a subList view does not copy)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Previous permutation: mirror every comparison (find A[ind] > A[ind+1], swap with
 *     the next smaller, reverse the suffix).
 *   - Why strict "<" in step 1 and strict ">" in step 3? So duplicates are skipped and
 *     the result is strictly greater, not equal.
 *   - Generate all permutations in order by calling this n! times from the sorted start.
 *   - k-th permutation directly (LeetCode 60): factorial number system, no iteration.
 *
 * RUN
 *   main() runs 5 cases (typical, ascending, descending wrap, duplicates, single) through
 *   both the List<Integer> and the int[] versions and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class NextGreaterPermutation {

    /** Author's version on a List; mutates and returns the same list. */
    public static List<Integer> nextGreaterPermutation(List<Integer> A) {
        int n = A.size();

        // Step 1: find the pivot, the rightmost index with A[ind] < A[ind + 1].
        int ind = -1;
        for (int i = n - 2; i >= 0; i--) {
            if (A.get(i) < A.get(i + 1)) {
                ind = i;
                break;
            }
        }

        // Step 2: no pivot means the sequence is fully descending; wrap to ascending.
        if (ind == -1) {
            Collections.reverse(A);
            return A;
        }

        // Step 3: swap the pivot with the smallest value to its right that is larger.
        // The suffix is descending, so the first match from the right is that value.
        for (int i = n - 1; i > ind; i--) {
            if (A.get(i) > A.get(ind)) {
                int tmp = A.get(i);
                A.set(i, A.get(ind));
                A.set(ind, tmp);
                break;
            }
        }

        // Step 4: the suffix is still descending; reverse it to make it the smallest.
        Collections.reverse(A.subList(ind + 1, n));
        return A;
    }

    /** Same algorithm on an int[] (the LeetCode signature). */
    public static void nextPermutation(int[] nums) {
        int n = nums.length;
        int ind = n - 2;
        while (ind >= 0 && nums[ind] >= nums[ind + 1]) {
            ind--;
        }
        if (ind >= 0) {
            int j = n - 1;
            while (nums[j] <= nums[ind]) {
                j--;
            }
            swap(nums, ind, j);
        }
        reverse(nums, ind + 1, n - 1); // ind == -1 reverses the whole array
    }

    private static void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }

    private static void reverse(int[] nums, int s, int e) {
        while (s < e) {
            swap(nums, s++, e--);
        }
    }

    private static void run(String label, int[] input, String expected) {
        List<Integer> list = new ArrayList<>();
        for (int v : input) list.add(v);
        List<Integer> listResult = nextGreaterPermutation(list);

        int[] arr = input.clone();
        nextPermutation(arr);

        System.out.println(label + ": list " + listResult + " | array " + Arrays.toString(arr)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 typical   ", new int[]{2, 1, 5, 4, 3, 0, 0}, "[2, 3, 0, 0, 1, 4, 5]");
        run("case 2 ascending ", new int[]{1, 2, 3}, "[1, 3, 2]");
        run("case 3 descending", new int[]{3, 2, 1}, "[1, 2, 3]");
        run("case 4 duplicates", new int[]{1, 1, 5}, "[1, 5, 1]");
        run("case 5 single    ", new int[]{1}, "[1]");
    }
}

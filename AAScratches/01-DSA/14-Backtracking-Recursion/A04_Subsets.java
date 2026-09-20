/*
 * =====================================================================
 *  Subsets (Power Set)                      LeetCode 78 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of distinct integers, return all possible subsets (the power set).
 *   The answer must not contain duplicate subsets; any order of subsets is accepted.
 *   n is small (<= 10 on LeetCode) because the output itself has 2^n entries.
 *
 * EXAMPLE
 *   nums = [1, 2, 3]  ->  [[], [3], [2], [2, 3], [1], [1, 3], [1, 2], [1, 2, 3]]
 *   nums = []         ->  [[]]        the empty set is itself a subset
 *   nums = [5]        ->  [[], [5]]   2^1 = 2 subsets
 *
 * APPROACH  (pick / not-pick recursion)
 *   1. Walk the array with an index. At every index there are exactly two choices:
 *      leave the element out, or take it.
 *   2. Base case: index == nums.length means every element has been decided, so record
 *      a COPY of the running list and return.
 *   3. Not-pick branch: recurse on index + 1 without touching the list.
 *   4. Pick branch: add nums[index], recurse on index + 1, then remove it again so the
 *      caller's list is exactly as it was handed over.
 *   A second method, subsetsByStartIndex, shows the same answer in the start-index loop
 *   form - that is the shape SubsetsII, CombinationSum and friends all build on.
 *
 * KEY INSIGHT
 *   A subset is one yes/no decision per element, so the recursion tree is a binary tree
 *   of depth n with 2^n leaves - the count falls straight out of the shape. The undo
 *   (ls.remove after the pick call) is what makes one shared list safe for the whole
 *   tree, and copying on record is what stops every stored result aliasing that one list.
 *   Once you see subsets as "decide element i, recurse on the rest", combinations,
 *   permutations and target-sum problems are all the same loop with one extra constraint.
 *
 * COMPLEXITY
 *   Time  O(n * 2^n)  2^n subsets, each copied in up to O(n).
 *   Space O(n) working (recursion depth + running list), plus O(n * 2^n) for the output.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Input has duplicates, e.g. [1, 2, 2] - how do you avoid duplicate subsets?
 *     (sort, then skip nums[i] == nums[i-1] at the same depth: LeetCode 90, SubsetsII)
 *   - Do it with bitmasks: for mask in 0..2^n-1, take bit i of mask. No recursion.
 *   - Return only subsets of size k (LeetCode 77 Combinations) - where is the prune?
 *   - Why is the result copied into a new ArrayList on every base case hit?
 *
 * RUN
 *   main() runs 3 cases (typical, empty, single) through both methods and prints
 *   actual vs expected.
 */
import java.util.ArrayList;
import java.util.List;

class Subsets {

    /** Pick / not-pick form: the answer order is not-pick first at every level. */
    public static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> list = new ArrayList<>();
        subsets(nums, 0, list, new ArrayList<>());
        return list;
    }

    private static void subsets(int[] arr, int idx, List<List<Integer>> list, List<Integer> ls) {
        if (idx >= arr.length) {
            list.add(new ArrayList<>(ls)); // copy: ls keeps changing after this line
            return;
        }
        // don't pick the element
        subsets(arr, idx + 1, list, ls);
        // pick the element, then undo the pick so the caller's list is untouched
        ls.add(arr[idx]);
        subsets(arr, idx + 1, list, ls);
        ls.remove(ls.size() - 1);
    }

    /**
     * Same power set, start-index loop form. Every prefix is a valid subset, so the
     * result is recorded on entry rather than only at the leaves. This is the template
     * SubsetsII / CombinationSum extend, which is why it is worth knowing both.
     */
    public static List<List<Integer>> subsetsByStartIndex(int[] nums) {
        List<List<Integer>> list = new ArrayList<>();
        buildFrom(nums, 0, list, new ArrayList<>());
        return list;
    }

    private static void buildFrom(int[] arr, int start,
                                  List<List<Integer>> list, List<Integer> ls) {
        list.add(new ArrayList<>(ls)); // the prefix built so far is already a subset
        for (int i = start; i < arr.length; i++) {
            ls.add(arr[i]);
            buildFrom(arr, i + 1, list, ls); // i + 1: each element is used at most once
            ls.remove(ls.size() - 1);
        }
    }

    public static void main(String[] args) {
        int[] typical = {1, 2, 3};
        print("case 1 pick/not-pick [1,2,3]", subsets(typical),
                "[[], [3], [2], [2, 3], [1], [1, 3], [1, 2], [1, 2, 3]]");
        print("case 1 start-index   [1,2,3]", subsetsByStartIndex(typical),
                "[[], [1], [1, 2], [1, 2, 3], [1, 3], [2], [2, 3], [3]]");

        int[] empty = {};
        print("case 2 pick/not-pick []     ", subsets(empty), "[[]]");
        print("case 2 start-index   []     ", subsetsByStartIndex(empty), "[[]]");

        int[] single = {5};
        print("case 3 pick/not-pick [5]    ", subsets(single), "[[], [5]]");
        print("case 3 start-index   [5]    ", subsetsByStartIndex(single), "[[], [5]]");

        print("case 4 count for n=3        ", subsets(typical).size(), 8);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

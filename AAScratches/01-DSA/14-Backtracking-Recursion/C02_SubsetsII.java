/*
 * =====================================================================
 *  Subsets II                        LeetCode 90 | Medium | MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array that may contain duplicates, return all possible
 *   subsets (the power set) with no duplicate subset in the answer. Two subsets
 *   are the same if they hold the same multiset of values, so [1,2] and [2,1]
 *   count as one. The answer may be returned in any order.
 *
 * EXAMPLE
 *   nums = [1,2,2]  ->  [[], [1], [1,2], [1,2,2], [2], [2,2]]   6, not 8
 *   nums = [1,2,3]  ->  8 subsets, nothing skipped (no duplicates present)
 *   nums = [2,2,2]  ->  [[], [2], [2,2], [2,2,2]]               4, not 8
 *   nums = []       ->  [[]]                                    edge case
 *
 * APPROACH  (sort, then skip duplicates at the same depth)
 *   1. Sort nums so equal values sit next to each other.
 *   2. backtrack(start) records the current partial list as a subset the moment
 *      it is entered -- every node of the tree is an answer, not just leaves.
 *   3. Loop i from start to the end: each i is "the next element I take".
 *   4. Skip the branch when i > start and nums[i] == nums[i-1]: an identical
 *      value was already tried at this same position, so this whole subtree
 *      would repeat subsets already produced.
 *   5. Add nums[i], recurse with start = i + 1 (no reuse), then remove it.
 *
 * KEY INSIGHT
 *   The dedupe rule is i > start, NOT i > 0. i == start is the first choice at
 *   this level, which is allowed even if the value equals its neighbour -- that
 *   is how [2,2] is still produced. Any later i with the same value is a repeat
 *   of a sibling branch, and sibling branches are exactly what create duplicate
 *   subsets. Sorting is what makes duplicates adjacent so one comparison finds
 *   them. This same i > start line reappears in Combination Sum II and in every
 *   other "...II with duplicates" backtracking problem.
 *
 * COMPLEXITY
 *   Time  O(n * 2^n)  up to 2^n subsets, each copied in O(n); sort is O(n log n)
 *   Space O(n)        recursion depth and the working list
 *                     (the output list itself is O(n * 2^n) on top of that)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Subsets (LC 78) without duplicates -- drop the sort and the skip line.
 *   - Why i > start and not i > 0? (Answer it out loud; it is the whole trick.)
 *   - Combination Sum II (LC 40): same skip rule plus a target and pruning.
 *   - Permutations II (LC 47): duplicates again, but with a used[] array
 *     because order matters there, so the skip condition changes shape.
 *   - Could you do it with bitmasks instead, and dedupe with a HashSet? Yes,
 *     but it is O(n * 2^n) memory in the set and loses the pruning.
 *
 * RUN
 *   main() runs 4 cases (typical with one duplicate, no duplicates, all equal,
 *   empty array) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SubsetsII {

    public static List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums); // duplicates must be adjacent for the skip rule to work
        backtrack(result, new ArrayList<>(), nums, 0);
        return result;
    }

    private static void backtrack(List<List<Integer>> result, List<Integer> current,
                                  int[] nums, int start) {
        // every node of the recursion tree is itself a valid subset
        result.add(new ArrayList<>(current));

        for (int i = start; i < nums.length; i++) {
            // i == start is the first pick at this level and is always allowed;
            // a later i with the same value repeats a sibling branch already taken
            if (i > start && nums[i] == nums[i - 1]) {
                continue;
            }
            current.add(nums[i]);                         // choose
            backtrack(result, current, nums, i + 1);      // explore, no reuse of i
            current.remove(current.size() - 1);           // undo
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + " " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1: one duplicated value -- 6 subsets instead of the naive 8
        print("case 1 [1,2,2]:", subsetsWithDup(new int[]{1, 2, 2}),
                "[[], [1], [1, 2], [1, 2, 2], [2], [2, 2]]");

        // case 2: no duplicates at all -- the plain power set, 2^3 = 8
        print("case 2 [1,2,3]:", subsetsWithDup(new int[]{1, 2, 3}),
                "[[], [1], [1, 2], [1, 2, 3], [1, 3], [2], [2, 3], [3]]");

        // case 3: tricky -- every element equal, so only 4 distinct subsets
        print("case 3 [2,2,2]:", subsetsWithDup(new int[]{2, 2, 2}),
                "[[], [2], [2, 2], [2, 2, 2]]");

        // case 4: edge -- empty input still has one subset, the empty set
        print("case 4 []:     ", subsetsWithDup(new int[]{}), "[[]]");
    }
}
